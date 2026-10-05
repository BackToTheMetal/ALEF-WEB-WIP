/*
 * Copyright 2003-2025 JetBrains s.r.o.
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
package jetbrains.mps.classloading;

import jetbrains.mps.classloading.MPSClassLoadersRegistry.DisposeSession;
import jetbrains.mps.classloading.ModulesWatcher.UpdateOutcome;
import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.module.ReloadableModule;
import jetbrains.mps.module.ReloadableModule.DeploymentStatus;
import jetbrains.mps.progress.EmptyProgressMonitor;
import jetbrains.mps.project.SModuleOperations;
import jetbrains.mps.project.facets.JavaModuleFacet;
import jetbrains.mps.project.structure.modules.ModuleReference;
import jetbrains.mps.smodel.tempmodel.TempModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.mps.annotations.Internal;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;
import org.jetbrains.mps.openapi.module.event.SModuleChangedEvent;
import org.jetbrains.mps.openapi.module.event.SRepositoryEvent;
import org.jetbrains.mps.openapi.util.ProgressMonitor;
import org.jetbrains.mps.openapi.util.SubProgressKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.ClassLoader.getSystemClassLoader;

/**
 * A ClassLoaderManager is a singleton and provides an internal API for loading classes
 * within MPS.
 * NOTE: External API is placed in {@link jetbrains.mps.module.ReloadableModule} interface.
 * Using the methods of this class is not recommended. This is no longer true
 *
 * This class intimately deals with {@link JavaModuleFacet} as it's (at the moment) the only mechanism
 * to describe classloading (perhaps, we can add another facet to address classloading of e.g. Kotlin modules)
 * therefore, it "watches" (keep track of) modules with Java facet and coming with classes (check {@link JavaModuleFacet.Compile})
 *
 * General information (FIXME OUTDATED):
 * A MPS java module is loadable iff it is possible to associate some ClassLoader with it.
 * Currently there are two types of <it>loadable</it> modules:
 * 1. <it>Reloadable</it> modules are modules which ClassLoader maybe redeployed on-the-fly
 * @see jetbrains.mps.module.ReloadableModule
 * @see #myMPSLoadableCondition
 * Presently the associated ClassLoader for these modules is {@link ModuleClassLoader}.
 * ClassLoaderManager stores a map of this ClassLoader instances, reloads them if needed, delegates class requests to them.
 * 2. <it>Non-reloadable</it> modules are not reloadable modules.
 * Currently such modules are bundled with Idea plugin, the associated ClassLoader for these modules is the result
 * of the method {@code com.intellij.openapi.extensions.PluginDescriptor#getPluginClassLoader()} call.
 * CLManager delegates Class/ClassLoader requests to Idea plugin [for these modules].
 *
 * Common part
 * CLManager listens to newly added <it>loadable</it> modules (into the repository) and to modules' removal.
 * When module is added, CLManager marks it as ({@code LAZY_LOADED}) and broadcasts the event to
 * {@link jetbrains.mps.classloading.DeployListener} clients.
 * When module's classes (or ClassLoader) are requested, the actual module load happens.
 * When module is removed from the repository, CLManager unloaded module's data from its storage.
 * @see ClassLoadingProgress for more information on module's loading progress and module's lifecycle
 *
 * Every module add/remove/reload triggers events dispatching to MPSClassesListeners
 * @see jetbrains.mps.classloading.DeployListener
 *
 * Also CLManager tracks the <em>validity</em> of the repository modules.
 * The invariant condition is that a module can not be (class) loaded if any of its dependencies is absent in the repository.
 * That means that for an <em>invalid</em> module CLManager will return <code>null</code> for all Class/ClassLoader requests (#getClass, #getClassLoader)
 * @see jetbrains.mps.classloading.ModulesWatcher
 * @see jetbrains.mps.classloading.ModulesWatcher.ClassLoadingStatus
 * @see jetbrains.mps.classloading.ModulesWatcher#getStatus(SModuleReference)
 *
 * Reloadable part
 * Any <it>reloadable</it> module M has a class loading lifecycle like this:
 * # module M is added to the repository [no ModuleClassLoader created]
 * # someone reloads M or asks for the classloader of M -> initializing ModuleClassLoader creation for M (lazy load) [ModuleClassLoader is constructed]
 * # more M reloads and class requests
 * # module M is removed from the repository -> ModuleClassLoader gets disposed. [ModuleClassLoader removed]
 *
 * Reload may be triggered by a client explicitly with {@link #reloadModules(Iterable)}.
 * [**] Notice that it is a very uncommon case when you might need an explicit reload.
 * Currently a module's reload happens automatically on module's changes (some specific changes, details below).
 * @see BatchEventsProcessor for details
 *
 * CLManager exploits a lazy mechanism of module's reloading. It stacks all module events,
 * and occasionally <em>refresh</em> happens: CLM flushes them and processes all the accumulated events.
 * When CLManager refreshes, its state becomes actual. [in the sense of information about modules', their class loading status and their ClassLoader's]
 * The state is guaranteed to be actual at these points:
 * 1. Right after the end of write action [CLManager has a {@link org.jetbrains.mps.openapi.repository.WriteActionListener}]
 * 2. In the middle of write action if a Class or ClassLoader was requested [{@link #getClassLoader}].
 *     Only write action holder is able to provoke <em>refresh</em> [!]
 * 3. Explicit reload: a call of reload methods [{@link #reloadModule}, {@link #reloadModules}].
 * @see #getClassLoader(org.jetbrains.mps.openapi.module.SModule) documentation for more details on pt. 2
 * @see #refresh()
 *
 * Repository lock policy
 * Every reload requires a repository write lock. Actual ModuleClassLoader construction happens inside the
 * end of write action or at forced refresh() (inside a write, too). Although we keep reference to SModule inside
 * classloader/classloader support, it's purely for diagnostic purposes and could be replaced wiht an SModuleReference.
 *
 *
 * FIXME here we give no guarantees when we give out ClassLoader (#getClassLoader) that it will be valid the next moment.
 *  We need to introduce some kind of write/read actions for that matter
 *
 *  THE PLAN for the future
 *  updates (#reload, #unload, #load):
 *
 *  The dependency graph is updated at the special moments under myLoadingModulesLock lock.
 *  This operation is assumed not to happen too often.
 *
 *  Each module has a loaded/unloaded status and that one is updated only upon request (explicit invocation of #reload) and requests a write* action.
 *   (write* is the lock guarding the module classes, write is the lock guarding the modules/models repository (where we do not need any classes)).
 *  We have generations of classloaders, each classloader remembers his generation token, also it has disposed/not-disposed state.
 *
 *  Each module has an API-available state (deployed/not-deployed), it is possible to deploy/undeploy the module directly, but it can fail due to reasons which are passed
 *   to the caller.
 *
 *  queries:
 *
 *  The graph with deps is queried when needed by CLM and by external clients. It must be done under the same lock as above.
 *
 *  Each module has an API-available state (valid/not-valid). The lock is the same as with the graph.
 *
 *  Since there is no easy way to track all the instances of the given class,
 *   #getClassLoader now gets the second parameter of ReloadTracker which is essentially the listener which is invoked on #reloads (like {@link DeployListener}
 *   and is obliged to recreate all the instances of dynamic classes he has hard references too (for example, stores them in the map).
 *   When the client decides that he does not need the old ModuleClassLoader to exist, he invokes callback#release (if the client's workflow is asynchronous)
 *
 *  In order to guarantee for a piece of code to be executed on the same generation of ModuleClassLoaders (which is not always the case, I believe),
 *   the client has to take read* lock.
 *
 *  Each ModuleClassLoader will depend only on ModuleClassLoaders from the same generation (currently that is not true).
 *
 *  From each generation token it will be possible to extract information about the specific #reload operation that happenned.
 *
 *  We catch ClassCastExceptions somewhere to dump every detail we have on two different ModuleClassLoaders which clashed
 *  Also we catch any problems from extensions and switch them off like IJ does.
 *
 *
 * @author apyshkin
 */
@SuppressWarnings("unchecked")
public class ClassLoaderManager implements CoreComponent {
  private static final Logger LOG = Logger.getLogger(ClassLoaderManager.class);

  private static ClassLoaderManager INSTANCE;

  static final MPSModuleClassLoader DEFAULT_DELEGATING_TO_SYSTEM_CL = new MPSModuleClassLoader("Delegate to System CL", getSystemClassLoader()) {
    // I want to keep MPSModuleClassLoader abstract for now just to avoid confusion one can create and use
    // MPSModuleClassLoader instances directly


    @Override
    public SModuleReference getModule() {
      // fake MR just to satisfy MPSModuleClassLoader
      return new ModuleReference(getName(), null);
    }

    @Override
    public @NotNull Class<?> loadOwnClass(String name) throws ClassNotFoundException {
      // don't see a reason to try and distinguish own vs full CP (with deps) as it's just a delegate to system CL
      // and there's nothing we could have guessed here.
      return loadClass(name);
    }
  };

  /**
   * guards any loading/unloading operations
   * first write/read repo lock must be taken and then this one
   */
  private final ReentrantLock myLoadingModulesLock = new ReentrantLock();

  /**
   * @deprecated use {@link jetbrains.mps.components.ComponentHost#findComponent(Class) ComponentHost#findComponent(ClassLoaderManager.class)} instead
   */
  @Deprecated
  public static ClassLoaderManager getInstance() {
    return INSTANCE;
  }

  private final SRepository myRepository;

  // TODO ideally, shall be satisfied with SModuleReference and no repo access, at the moment still needs SRepo access
  private final MPSClassLoadersRegistry myClassLoadersHolder = new MPSClassLoadersRegistry();

  private final ModulesWatcher myModulesWatcher;

  private final ClassLoadingBroadCaster myBroadCaster; // needs ReloadableModule instance as its DeployListener operates with it

  private final ModuleEventsHandler myRepositoryListener;

  public ClassLoaderManager(@NotNull SRepository repository) {
    myRepository = repository;
    // watchableCondition is to filter modules that are subject to class loading. We want to watch and trace the dependencies between these.
    // It answers if it is possible to associate a ClassLoader (whether IDEA-delegating or true MPS module CL) with the module.
    // XXX perhaps, this condition shall include a check for actual .class present, to avoid attempts like e.g. LanguageRegistry to access classes
    // from bare, non-compiled modules (i.e. not *can*ProvideClasses but also *does*ProvideClasses)
    final Predicate<SModule> watchableCondition = SModuleOperations::classesAvailableToMPS;
    myModulesWatcher = new ModulesWatcher(myRepository, watchableCondition);
    // ModuleEventsHandler batches module events and pumps it back here, for susbequent processing by myModulesWatcher
    myRepositoryListener = new ModuleEventsHandler(repository, this);
    myBroadCaster = new ClassLoadingBroadCaster(repository.getModelAccess());
  }

  @Override
  public void init() {
    if (INSTANCE != null) {
      throw new IllegalStateException("ClassLoaderManager is already initialized");
    }
    INSTANCE = this;
    myRepositoryListener.init();
  }

  @Override
  public void dispose() {
    myClassLoadersHolder.dispose();
    myRepositoryListener.dispose();
    INSTANCE = null;
  }

  @TestOnly
  /*package*/ ModulesWatcher getModulesWatcher() {
    return myModulesWatcher;
  }

  /**
   * TODO refactor all usages of getClass()
   * So if {@link } method returns true and the class file is in place you will get the class
   *
   * @deprecated use module-specific methods which throw different ClassNotFoundExceptions,
   * you need to process it on your own (probably show some user notification)
   *
   * @see ModuleClassNotFoundException
   */
@Deprecated(since = "3.2", forRemoval = true)
  @Nullable
  public Class<?> getClass(@NotNull SModule module, String classFqName) {
    try {
      return getClassLoader(module).loadClass(classFqName);
    } catch (ClassNotFoundException e) {
      LOG.error("Exception during class loading", e);
    }
    return null;
  }

  /**
   * @return the class loader associated with the module. Note that sometimes it may return "outdated" ClassLoader.
   * To be exact it returns the classloader for module, which was actual for the moment of last refresh event.
   * @see #refresh()
   *
   * Refresh happens at these points:
   * 1. At the end of write action,
   * 2. During #getClassLoader calls inside the write action,
   * 3. Also it may be triggered explicitly by #reloadModules(), #reloadModule(), etc. call.
   *
   * This method can return the class loader of the IDEA plugin which manages the module's classes.
   * Use it if you want to get a class from the module with IdeaPluginFacet.
   *
   * INTERNAL USE ONLY: use module-specific methods which throw ClassNotFoundException,
   * you need to process it by yourself (probably show some user notification)
   *
   * @see jetbrains.mps.module.ReloadableModule
   */
  @NotNull
  public MPSModuleClassLoader getClassLoader(final SModule module) {
//    if (!myValidCondition.test(module)) {
//      // I'm not sure there's any reason to keep this condition, as it seems to be just some sort of optimization
//      // especially as long as there's subsequent `refresh()` call, yet there's a long story for this condition, and
//      // reluctant to remove it right away
//      return DEFAULT_DELEGATING_TO_SYSTEM_CL;
//    }

    // first, check if status of this module is valid in the dependencies graph
    //    however, not sure there's need to, myClassLoadersHolder.getClassLoader(), below, gives the same result
    //    here is just a tribute to myValidCondition
    if (!myModulesWatcher.getStatus(module.getModuleReference()).isValid()) {
      return DEFAULT_DELEGATING_TO_SYSTEM_CL;
    }
    // myValidCondition is true here

//    createClassloaders(Collections.singleton(reloadableModule), new EmptyProgressMonitor());
    MPSModuleClassLoader classLoader = myClassLoadersHolder.getClassLoader(module.getModuleReference());
    return classLoader != null ? classLoader : DEFAULT_DELEGATING_TO_SYSTEM_CL;
  }

  /**
   * the caller is guaranteed that no reload happen during the transaction (reload which is coming from the other thread)
   * due to possible deadlock at least the read access is demanded
   */
  @Internal
  public void runTransaction(@NotNull Runnable transaction) {
    myRepository.getModelAccess().checkReadAccess();
    _runTransaction(transaction);
  }

  private void _runTransaction(@NotNull Runnable transaction) {
    // FIXME use tryLock(timeout), and, perhaps, few attempts for uses from CLM (where 'write' is held)
    myLoadingModulesLock.lock();
    try {
      transaction.run();
    } finally {
      myLoadingModulesLock.unlock();
    }
  }

  /**
   * no events are triggered, classloaders do not change during this section
   */
  @Deprecated(since = "201", forRemoval = true)
  @Internal
  public void runNonReloadableSection(@NotNull Runnable runnable) {
    myRepositoryListener.pause();
    try {
      runnable.run();
    } finally {
      myRepositoryListener.proceed();
    }
  }

  /**
   * Flushes all delayed notifications to keep up the graph in {@link ModulesWatcher} with the module repository state
   * @see ModuleEventsHandler
   * @return if refresh actually happened
   */
  private boolean refresh() {
    checkReadAccess();
    return myRepositoryListener.refresh();
  }

  /**
   * Of all changed module graph verticies take those that got all dependencies satisfied and tries to create CLs for them (unless there are already).
   * Then, for each module with a new CL, notifications are sent by means of {@link DeployListener}.
   * Note, at the moment, we track modules that can supply classes ({@code ModulesWatcher.watchableCondition}), therefore "all dependencies ready"
   * is the only condition we check here.
   *
   * @param modules vertices in dependency graph that has been added/replaced by a new one.
   */
  private void preLoadModules(Collection<CModule> modules, ProgressMonitor monitor) {
    // pre: modules - transitive closure
    checkWriteAccess();
    monitor.start("Loading", 6);

    // XXX Here we imply classloading process for re-loaded module (unloaded and the loaded again) has to be complete at this point
    //     as clHolder.createClassLoaders() doesn't re-create CL if there's one already, but what if/when I combine unload/preLoad
    //     into single transaction, would this assumption cause any throuble then?
    Set<ReloadableModule> modulesPreLoad = onlyRM(modules.stream().filter(cm -> cm.getStatus().isValid()));
    if (modulesPreLoad.isEmpty()) {
      return;
    }
    monitor.advance(1);

    // markLazyLoaded (inside createClassLoaders()) expects modules that meet myWatchableCondition (part of myValidCondition now)
    // while we still hold model read for SModule, create CLs for them. Would be great to use CModule, but need JMF inside createClassLoader() ATM
    // XXX perhaps, could keep CL information in CModule, to release model read? OTOH, shall broadcast events within model lock for now (DeployListener
    //     promises model write)
    // FIXME I wonder why we get all dependencies, not just direct and let regular CL delegation to deal with the rest?
    // TODO I wonder if I shall pass subTask into createClassLoaders()
    // TODO pass CModule (filtered by isValid(), and only then filter ReloadableModule instances, so that the moment we can split SModule and
    //      ReloadableModule, only onLoad() would be affected
    modulesPreLoad = myClassLoadersHolder.createClassLoaders(modulesPreLoad, mr -> myModulesWatcher.getDependencies(mr));
    monitor.advance(1);
    myBroadCaster.onLoad(modulesPreLoad, monitor.subTask(4, SubProgressKind.AS_COMMENT));

    monitor.done();
  }

  /**
   * Stops tracking all the {@code modules}, which are MPS-loadable
   * Disposes all class loaders for these modules
   * Method is not lazy
   *
   * FIXME not sure supplied modules match myMPSLoadableCondition, perhaps externally-managed modules are
   *       among these, as well. If yes, this means we dispatch different set of modules in onLoaded and in onUnloaded
   * @param modules vertices in dependency graph that has been removed/replaced by a new one
   */
  private void unloadModules(Collection<CModule> modules, @NotNull ProgressMonitor monitor) {
    // pre: modules - transitive closure
    checkWriteAccess();
    monitor.start("Unloading", 6);

    final DisposeSession session = myClassLoadersHolder.createDisposeSession(onlyRM(modules.stream()));
    monitor.advance(1);
    try {
      if (session.modules().isEmpty()) {
        return;
      }
      LOG.debug("Unloading %d modules".formatted(session.modules().size()));
      if (ourCheckMemLeaks && session.actualNumberOfSessions() > MAX_SESSIONS_ALIVE) {
        // note that if we do 100 reloads during a single write action we might get a 100 sessions
        LOG.error(
            ("Possible leaking class loaders : currently there are %d sessions alive. "
            + "Please avoid running too many reloads in the single write action").formatted(session.actualNumberOfSessions()));
      }

      myBroadCaster.onUnload(session, monitor.subTask(4, SubProgressKind.AS_COMMENT));
      myClassLoadersHolder.forgetClassLoaders(session);
      monitor.advance(1);
    } finally {
      session.readyToDispose();
      monitor.done();
    }
  }

  // With PluginLoaderRegistry greatly improved since the moment the check was introduced, and along with the fact it doesn't listen to CLM any more
  //      (proceeds aggregated events fron LanguageRegistry, instead), there seems no reason to keep high number here.
  //      Besides, I'd like to cancel the paactive of CL reload during write, see #getClassLoader()
  private static final int MAX_SESSIONS_ALIVE = 20;
  private boolean ourCheckMemLeaks = true;
  /*
   * MigrationsTest used to do that, but no more.
   */
  @TestOnly
  public void setCheckMemLeaks(boolean check) {
    ourCheckMemLeaks = check;
  }


  private static Set<ReloadableModule> onlyRM(final Stream<CModule> modules) {
    return modules.map(CModule::getModule).filter(ReloadableModule.class::isInstance).map(ReloadableModule.class::cast).collect(Collectors.toSet());
  }

  public void addListener(@NotNull DeployListener listener) {
    myBroadCaster.addListener(listener);
  }

  public void removeListener(@NotNull DeployListener listener) {
    myBroadCaster.removeListener(listener);
  }

  /**
   * Use this method to invalidate modules (namely, recreate their class loaders)
   * There are also useful {@link #reloadModules(Iterable)} and {@link #reloadModule(SModule)}.
   */
  public void reloadModules(Iterable<? extends SModule> modules, @NotNull ProgressMonitor monitor) {
    final long beginTime = System.nanoTime();
    checkWriteAccess();
    refresh();
    // this is a hack. just a missstep towards better code (once reload process with a new watcher loguc is streamlined, we won't need casts)
    // perhaps, we can just ask watcher to give us tracked modules among these
    Collection<ReloadableModule> modulesToReload = new LinkedHashSet();
    for (SModule module : modules) {
      if (!(module instanceof TempModule) && module.getRepository() == null) {
        // FIXME I don't quite undertand how come TempModule from console could get repository == null (e4ebe803, MPS-20789 10yo! hotfix!!)
        throw new IllegalStateException(String.format("Cannot reload the module %s which does not belong to a repository", module));
      }
      if (module instanceof ReloadableModule) {
        modulesToReload.add((ReloadableModule) module);
      }
    }
    // report modules as changed to trigger update of their CL
    processModuleChanges(modulesToReload.stream().map(SModuleChangedEvent::new).collect(Collectors.toList()), monitor);
    LOG.info(String.format("Reloaded %d module(s) in %.3f s", modulesToReload.size(), (System.nanoTime() - beginTime) / 1e9));
  }

  /**
   * Resovle modules with associated repository and reload any found.
   * @since 2021.2
   */
  public void reload(Iterable<SModuleReference> modules, @NotNull ProgressMonitor monitor) {
    if (myRepository.getModelAccess().canWrite()) {
      resolveAndReload(modules, monitor);
    } else {
      myRepository.getModelAccess().runWriteAction(() -> resolveAndReload(modules, monitor));
    }
  }

  private void resolveAndReload(Iterable<SModuleReference> modules, @NotNull ProgressMonitor monitor) {
    checkWriteAccess();
    ArrayList<SModule> toReload = new ArrayList<>();
    for (SModuleReference mr : modules) {
      SModule m = mr.resolve(myRepository);
      if (m != null) {
        toReload.add(m);
      }
    }
    if (toReload.isEmpty()) {
      return;
    }
    reloadModules(toReload, monitor);
  }

  /**
   * @see #reloadModules(Iterable, org.jetbrains.mps.openapi.util.ProgressMonitor)
   */
  public void reloadModules(Iterable<? extends SModule> modules) {
    reloadModules(modules, new EmptyProgressMonitor());
  }

  /**
   * For internal use
   * @see jetbrains.mps.module.ReloadableModule
   */
  @Internal
  public void reloadModule(SModule module) {
    reloadModules(Collections.singleton(module), new EmptyProgressMonitor());
  }

  /**
   * Note: usually reloading only the "dirty" modules is enough.
   * Please take a look at {@link #reloadModule} and {@link #reloadModules} methods.
   * @see #reloadModules(Iterable, org.jetbrains.mps.openapi.util.ProgressMonitor)
   */
  @Internal
  public void reloadAll(@NotNull ProgressMonitor monitor) {
    if (myRepository.getModelAccess().canWrite()) {
      reloadModules(myRepository.getModules(), monitor);
    } else {
      myRepository.getModelAccess().runWriteAction(() -> reloadModules(myRepository.getModules(), monitor));
    }
  }

  private void checkWriteAccess() {
    myRepository.getModelAccess().checkWriteAccess();
  }

  private void checkReadAccess() {
    myRepository.getModelAccess().checkReadAccess();
  }

  @NotNull
  public DeploymentStatus getStatus(@NotNull SModule module) {
    SModuleReference moduleReference = module.getModuleReference();
    if (myClassLoadersHolder.hasCL(moduleReference)) {
      return DeploymentStatuses.DEPLOYED;
    }
    if (myModulesWatcher.getStatus(moduleReference).isValid()) {
      return DeploymentStatuses.NOT_DEPLOYED;
    } else {
      return DeploymentStatuses.NOT_IN_REPO;
      //fixme return dependency problem sometimes
    }
  }

  /**
   * requires exclusive/write access to CL repository
   */
  /*package*/ void processModuleChanges(List<SRepositoryEvent> events, @NotNull ProgressMonitor monitor) {
    checkWriteAccess();
    monitor.start("", 4);
    _runTransaction(() -> {
      final UpdateOutcome r = myModulesWatcher.update(events, monitor.subTask(2, SubProgressKind.REPLACING));
      unloadModules(r.unloaded, monitor.subTask(1, SubProgressKind.REPLACING));
      preLoadModules(r.loaded, monitor.subTask(1, SubProgressKind.REPLACING));
    });

    monitor.done();
  }

  enum DeploymentStatuses implements DeploymentStatus {
    //fixme
    NOT_IN_REPO("The module does not belong to any repository (or depends on such module)", false, false),
//    DEPENDENCY_IS_NOT_IN_REPO("The module has a dependency which is not in the repository", false, false),
    NOT_DEPLOYED("The module is not deployed but it can be", true, false),
    DEPLOYED("The module is deployed and is ready to load classes", true, true);

    private final String myMessage;
    private boolean myCanLoadClasses;
    private boolean myIsDeployed;

    DeploymentStatuses(@NotNull String message, boolean canLoadClasses, boolean isDeployed) {
      myMessage = message;
      myCanLoadClasses = canLoadClasses;
      myIsDeployed = isDeployed;
    }

    @NotNull
    public String getMessage() {
      return myMessage;
    }

    @Override
    public boolean canBeDeployed() {
      return myCanLoadClasses;
    }

    @Override
    public boolean isDeployed() {
      return myIsDeployed;
    }
  }
}