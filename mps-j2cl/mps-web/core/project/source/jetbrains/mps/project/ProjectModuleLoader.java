/*
 * Copyright 2003-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.project;

import jetbrains.mps.library.ModulesMiner;
import jetbrains.mps.library.ModulesMiner.ModuleHandle;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.project.structure.modules.ModuleDescriptor;
import jetbrains.mps.project.structure.project.ModulePath;
import jetbrains.mps.project.structure.project.ProjectDescriptor;
import jetbrains.mps.smodel.ModuleInstanceFactory;
import jetbrains.mps.smodel.ModuleRepositoryFacade;
import jetbrains.mps.util.MacroHelper;
import jetbrains.mps.util.Pair;
import jetbrains.mps.util.StringUtil;
import jetbrains.mps.vfs.IFile;
import jetbrains.mps.vfs.util.PathFormatChecker.PathFormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Package-local helper class to manage project module attachment/detachment, as well as
 * association with the project on project (re-)loading events. Observable with {@link ProjectModuleLoadingListener}.
 * <br>
 * Input: {@link ProjectDescriptor} (see {@link #reloadProjectModules}).
 * <br>
 * The inner class {@link Update} implements a two-stage update procedure: its construction in {@link #reloadProjectModules}
 * is IO-bound, while {@link Update#run} requires write action.
 * <p>
 * The state is managed by the inner class {@link Store}, which is a table indexed by {@link SModuleReference}.
 * <br>
 * Active modules are accessible via {@link #activeModules()}.
 * Method {@link #forEachModuleEntry} can be used to iterate over all loaded module entries.
 * Errors detected by {@link #loadDiscoveredModules} are logged and made available via {@link #getErrors()},
 * and also reported via {@link ProjectModuleLoadingListener}.
 * <p>
 * <blockquote>
 * FIXME what about modules without path, i.e. those without descriptor file. Perhaps PML shall care about
 *       ModulePath-backed modules, while ProjectBase could deal with non-MP modules? Alternatively, keep
 *       fake/singleton/uniquely generated MP for a module without one. Might need to change contains() logic then
 *
 * </blockquote>
 * 
 * Created by apyshkin on 11/5/15.
 */
/*package*/ final class ProjectModuleLoader {
  private static final Logger LOG = Logger.getLogger(ProjectModuleLoader.class);

  /**
   * Captures the update logic.
   * <br>
   * Dropped descriptor files correspond to modules that need to be unloaded.
   * <br>
   * Discovered module handles represent modules that need to be loaded.
   * <p>
   * <strong>NB!</strong>Must be run in a write action, as it performs module loading and updating operations.
   */
  public class Update implements Runnable {

    private final ProjectBase myProject;
    private final Set<IFile> myDroppedDescriptorFiles;
    private final Collection<ModuleHandle> myDiscoveredModuleHandles;

    private Update(ProjectBase project, Set<IFile> droppedDescriptorFiles, Collection<ModuleHandle> discoveredModuleHandles) {
      myProject = project;
      myDroppedDescriptorFiles = droppedDescriptorFiles;
      myDiscoveredModuleHandles = discoveredModuleHandles;
    }

    @Override
    public void run() {
      LOG.info("Loading modules...");
      clearErrorsBuffer();
      detachDroppedModules(myDroppedDescriptorFiles, myProject);
      int loadedModules = loadDiscoveredModules(myDiscoveredModuleHandles, myProject);
      LOG.info("Modules are loaded: " + loadedModules + " new; " + myDroppedDescriptorFiles.size() + " removed");
    }
  }
  
  private enum ModuleStateChange {
    INTRODUCED,
    DROPPED,
    CHANGED_FOLDER,
  }

  private record Entry (
    SModuleReference moduleReference,
    IFile descriptorFile,
    String virtualFolder
  ) {}

  /*
   * Primary key is ref.
   * Descriptor file is not a part of PK, since there may be >1 modules in a single file,
   * and the file object, i.e. path, may not be unique (consider /var/... and /private/var/... pointing to the same file).
   */
  private static class Store {
    // internal state
    private final Map<SModuleReference, Entry> myModuleReferenceToEntry = new HashMap<>();

    /*
     * This operation may not be implemented very efficiently, since it's impossible to build
     * a reliable index of IFile -> Entry mappings.
     */
    private List<Entry> select(@NotNull Set<IFile> files) {
      return myModuleReferenceToEntry.values().stream().filter(e -> files.contains(e.descriptorFile)).toList();
    }

    private Optional<Entry> select(@NotNull SModuleReference ref) {
      if (myModuleReferenceToEntry.containsKey(ref)) {
        return Optional.of(myModuleReferenceToEntry.get(ref));
      }
      return Optional.empty();
    }

    private Stream<Entry> selectAll() {
      return myModuleReferenceToEntry.values().stream();
    }

    private void update(@NotNull SModuleReference ref, @NotNull IFile file, String virtualFolder) {
      myModuleReferenceToEntry.put(ref, new Entry(ref, file, virtualFolder));
    }

    private void update(@NotNull SModuleReference ref, String virtualFolder) {
      myModuleReferenceToEntry.computeIfPresent(ref, (r, e) -> new Entry(ref, e.descriptorFile, virtualFolder));
    }

    private void drop(SModuleReference ref) {
      if (ref != null) {
        myModuleReferenceToEntry.remove(ref);
      }
    }

  }

  private final Store myStore = new Store();
  
  private final List<ProjectModuleLoadingListener> myListeners = new CopyOnWriteArrayList<>();

  private final List<String> myErrors = new ArrayList<>();

  /*package*/ ProjectModuleLoader() {
  }

  /*package*/ void addListener(@NotNull ProjectModuleLoadingListener listener) {
    myListeners.add(listener);
  }

  /*package*/ void removeListener(@NotNull ProjectModuleLoadingListener listener) {
    if (!myListeners.contains(listener)) {
      LOG.warning("Listener could not be found : " + listener);
    }
    myListeners.remove(listener);
  }

  @Nullable
  @Deprecated
  /*package*/ ModulePath getPath(@NotNull SModuleReference mRef) {
    return myStore.select(mRef).map(e -> new ModulePath(e.descriptorFile, e.virtualFolder)).orElse(null);
  }

  @Nullable
  /*package*/ IFile getDescriptorFile(SModuleReference ref) {
    return myStore.select(ref).map(e -> e.descriptorFile).orElse(null);
  }

  @Nullable
  /*package*/ String getVirtualFolder(SModuleReference ref) {
    return myStore.select(ref).map(e -> e.virtualFolder).orElse(null);
  }

  /*package*/ void setVirtualFolder(SModuleReference moduleReference, String newFolder) {
    myStore.update(moduleReference, newFolder);
  }

  /*package*/ Collection<SModuleReference> activeModules() {
    return myStore.selectAll()
              .map(e -> e.moduleReference)
              .toList();
  }

  /*package*/ void forEachModuleEntry(BiConsumer<IFile, String> descriptorFileToFolder) {
    myStore.selectAll()
          .forEach(e -> descriptorFileToFolder.accept(e.descriptorFile, e.virtualFolder));
  }

  @Deprecated
  /*package*/ Stream<ModulePath> allPaths() {
    return myStore.selectAll()
              .map(e -> new ModulePath(e.descriptorFile, e.virtualFolder));
  }

  @NotNull
  /*package*/ String getErrors() {
    return String.join(System.lineSeparator(), myErrors);
  }

  /**
   * Reloads project modules. Uses {@link ModulesMiner} to read descriptors. Should be invoked
   * in an IO-bound context (read action).
   * <br>
   * Input: {@code projectDescriptor} containing list of module descriptor files.
   * <br>
   * Output: {@link Update} object that performs the update.
   * <br>
   * The returned {@link Update} must be executed in a write action, as it performs
   * module loading and updating operations.
   * @param project
   * @param projectDescriptor
   * @return a {@link Runnable} that performs the actual update
   */
  /*package*/ Update reloadProjectModules(ProjectBase project, @NotNull ProjectDescriptor projectDescriptor) {
    Map<IFile, Pair<ModuleStateChange, String>> diff = buildDiff(projectDescriptor);
    Set<IFile> dropped = new HashSet<>();
    List<Pair<IFile, String>> introduced = new ArrayList<>();
    for (Map.Entry<IFile, Pair<ModuleStateChange, String>> e : diff.entrySet()) {
      IFile file = e.getKey();
      ModuleStateChange value = e.getValue().o1;
      switch (value) {
        case DROPPED -> dropped.add(file);
        // one of the modules might change its virtual folder but not the location
        // in this case we need to remove that module from project and insert it again
        case CHANGED_FOLDER -> {
          dropped.add(file);
          introduced.add(new Pair<>(file, e.getValue().o2));
        }
        case INTRODUCED -> introduced.add(new Pair<>(file, e.getValue().o2));
      }
    }
    if (LOG.isDebugLevel()) {
      LOG.debug("Dropped modules: " + dropped.size() + ", introduced modules: " + introduced.size());
    }
    return new Update(project, dropped, discoverModules(project, introduced));
  }

  private Map<IFile, Pair<ModuleStateChange, String>> buildDiff(ProjectDescriptor projectDescriptor) {
    HashMap<IFile, String> fileToFolder = new HashMap<>();
    forEachModuleEntry(fileToFolder::put);
    Map<IFile, Pair<ModuleStateChange, String>> diff = new HashMap<>();
    projectDescriptor.forEachEntry((file, folder) -> {
      if (fileToFolder.containsKey(file)) {
        String oldFolder = fileToFolder.remove(file);
        if (!Objects.equals(oldFolder, folder)) {
          diff.put(file, new Pair<>(ModuleStateChange.CHANGED_FOLDER, folder));
        }
      } else {
        diff.put(file, new Pair<>(ModuleStateChange.INTRODUCED, folder));
      }
    });
    // fileToFolder contains dropped files at this point
    for (IFile descriptorFile : fileToFolder.keySet()) {
      diff.put(descriptorFile, new Pair<>(ModuleStateChange.DROPPED, null));
    }
    return diff;
  }

  private @NotNull Collection<ModuleHandle> discoverModules(ProjectBase project, List<Pair<IFile, String>> pathsToLoad) {
    final ModulesMiner modulesMiner = new ModulesMiner(project.getPlatform());
    for (Pair<IFile, String> p : pathsToLoad) {
      IFile descriptorFile = p.o1;
      try {
        if (descriptorFile.exists()) {
          // there could be more than 1 module collected from a single file
          // XXX in fact, we know we reference individual descriptor files here, and don't need all facilities of MM, like
          //    collecting 'excluded' locations (necessary for recursive discovery). Even support for deployed module discovery
          //    might be superfluous here (OTOH, might be a nice feature if we want to compose a project with deployed modules)
          //    Perhaps, shall use specific discovery method or introduce a mode/flag to avoid unnecessary processing of MD values
          modulesMiner.collectModules(descriptorFile, p.o2);
        } else {
          error("Can't load module from " + descriptorFile.getPath() + ". File doesn't exist.");
          fireModuleNotFound(descriptorFile);
        }
      } catch (PathFormatException e) {
        // fixme apyshkin
        MacroHelper.MacroMatcher matcher = MacroHelper.MACRO_PATTERN.matcher(e.getProblemPath());
        if (matcher.find()) {
          LOG.warning("Some paths might contain unknown macros, please define them in 'Path variables' and reopen the project");
        } else {
          throw e;
        }
      }
    }
    Collection<ModuleHandle> collectedModules = modulesMiner.getCollectedModules();
    if (LOG.isDebugLevel()) {
      LOG.debug("Collected modules: " + collectedModules.size());
    }
    return collectedModules;
  }

  // needs model write
  private void detachDroppedModules(Set<IFile> droppedDescriptors, ProjectBase project) {
    final SRepository projectRepo = project.getRepository();
    myStore.select(droppedDescriptors).forEach( e -> {
      // XXX I wonder is project.getScope().resolve isn't a better alternative (provided I fix its linear
      //     search implementation. I do care about modules belonging to the project only, scope seems to be fair
      //     alternative to a repository which may provide access to foreign modules.
      final SModule oldModule = e.moduleReference.resolve(projectRepo);
      if (oldModule == null) {
        LOG.error("Module " + e.moduleReference.getModuleName() + " (" + e.descriptorFile + ") not found in the project repository");
      }
      else {
        // fire event with module still attached to a project repo
        detachModule(oldModule, e.descriptorFile);
        // checkProjectIsOwner=false: assume all modules we track here are with ModulePath and do belong to the project
        project.dissociateFromProjectRepo(oldModule, false);
        if (LOG.isDebugLevel()) {
          LOG.debug("Detached module: " + oldModule.getModuleName());
        }
      }
    });
  }

  /**
   * @return the number of successfully loaded modules
   */
  private int loadDiscoveredModules(Collection<ModuleHandle> discovered, ProjectBase project) {
    // at the moment, MRF is not capable to register a generator sooner that its language. To make sure no generator comes first,
    // there's sorter in MM.
    ModuleInstanceFactory moduleFactory = new ModuleRepositoryFacade(project);
    // XXX This code resembles ProjectModulesFiller (ProjectStrategyBase). Do we need to keep them separate?
    int loadedModules = 0;
    for (ModuleHandle handle: discovered) {
      IFile descriptorFile = handle.getFile();
      ModuleDescriptor descriptorObject = handle.getDescriptor();
      if (descriptorObject != null) {
        SModule module = moduleFactory.instantiate(descriptorObject, descriptorFile);
        // it's quite tempting, indeed, to move project update (i.e. addModuleEntry) into listener ProjectModuleLoadingListener.moduleLoaded
        // just need to sort out ModuleLoader and Project relationship.

        project.associateWithProjectRepo(module);
        attachModule(module, descriptorFile, StringUtil.emptyIfNull(String.valueOf(handle.getUserData())));
        if (LOG.isDebugLevel()) {
          LOG.debug("Attached module: " + module.getModuleName());
        }
        ++loadedModules;
      } else {
        error("Can't load module from " + descriptorFile.getPath() + ". Unknown file type.");
        fireModuleTypeIsUnknown(descriptorFile);
      }
    }
    return loadedModules;
  }

  @Nullable
  @Deprecated
  /*package*/ ModulePath unloaded(@NotNull SModuleReference moduleReference) {
    Pair<IFile, String> p = dropIfAttached(moduleReference);
    return p != null ? new ModulePath(p.o1, p.o2) : null;
  }

  // FIXME seems reasonable to
  @Deprecated
  /*package*/ void forget(@NotNull SModule module, @NotNull ModulePath modulePath) {
    detachModule(module, modulePath.getFile());
  }

  // XXX the method dispatches moduleLoaded event with SModule instance, but as long as the only thing from the module needed there is SModuleReference,
  // we don't care if the module is registered in a repo or not. FIXME update listener to take SModuleReference instead
  @Deprecated
  /*package*/ void record(@NotNull SModule module, @NotNull ModulePath modulePath) {
    attachModule(module, modulePath.getFile(), modulePath.getVirtualFolder());
  }

  /*
   * Reverse-engineered contract:
   *  - if there is an entry with ATTACHED state for this module ref, return the corresponding file-virtualFolder pair
   *    but first mark it as INVALID by dropping it from the map
   *  - otherwise return null
   */
  @Nullable
  /*package*/ Pair<IFile, String> dropIfAttached(SModuleReference ref) {
    if (ref == null) {
      return null;
    }
    Entry e = myStore.myModuleReferenceToEntry.remove(ref);
    return e != null ? new Pair<>(e.descriptorFile, e.virtualFolder) : null;
  }

  /*package*/ void attachModule(@NotNull SModule module, @NotNull IFile descriptorFile, String virtualFolder) {
    myStore.update(module.getModuleReference(), descriptorFile, virtualFolder);
    fireModuleLoaded(module, descriptorFile);
  }

  /*package*/ void detachModule(@NotNull SModule module, @NotNull IFile descriptorFile) {
    myStore.drop(module.getModuleReference());
    fireModuleRemoved(module, descriptorFile);
  }

  private void clearErrorsBuffer() {
    myErrors.clear();
  }

  private void error(@NotNull String text) {
    myErrors.add(text);
    LOG.error(text);
  }

  private void fireModuleNotFound(IFile file) {
    for (ProjectModuleLoadingListener listener : myListeners) {
      listener.moduleNotFound(file);
    }
  }

  private void fireModuleTypeIsUnknown(IFile file) {
    for (ProjectModuleLoadingListener listener : myListeners) {
      listener.moduleTypeIsUnknown(file);
    }
  }

  private void fireModuleRemoved(SModule module, IFile file) {
    for (ProjectModuleLoadingListener listener : myListeners) {
      listener.moduleRemoved(module, file);
    }
  }

  private void fireModuleLoaded(SModule module, @NotNull IFile file) {
    for (ProjectModuleLoadingListener listener : myListeners) {
      listener.moduleLoaded(module, file);
    }
  }
}
