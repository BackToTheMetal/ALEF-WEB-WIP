/*
 * Copyright 2003-2024 JetBrains s.r.o.
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
package jetbrains.mps.smodel.language;

import jetbrains.mps.classloading.ClassLoaderManager;
import jetbrains.mps.classloading.DeployListener;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.components.ComponentHost;
import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.module.ReloadableModule;
import jetbrains.mps.project.AbstractModule;
import jetbrains.mps.project.SModuleOperations;
import jetbrains.mps.project.Solution;
import jetbrains.mps.project.structure.modules.Dependency;
import jetbrains.mps.project.structure.modules.LanguageDescriptor;
import jetbrains.mps.smodel.Generator;
import jetbrains.mps.smodel.Language;
import jetbrains.mps.smodel.adapter.ids.MetaIdByDeclaration;
import jetbrains.mps.smodel.adapter.ids.MetaIdHelper;
import jetbrains.mps.smodel.adapter.ids.SLanguageId;
import jetbrains.mps.smodel.runtime.ILanguageAspect;
import jetbrains.mps.smodel.runtime.ModuleDeploymentListener;
import jetbrains.mps.smodel.runtime.ModuleRuntime;
import jetbrains.mps.smodel.runtime.ModuleRuntime.Extension;
import jetbrains.mps.smodel.runtime.ModuleRuntime.Extension.MatchRequest;
import jetbrains.mps.smodel.runtime.ModuleRuntime.Flags;
import jetbrains.mps.smodel.runtime.ModuleRuntime.ModuleRuntimeContext;
import jetbrains.mps.smodel.runtime.impl.GeneratorRuntimeActivator;
import jetbrains.mps.smodel.runtime.impl.LanguageRuntimeActivator;
import jetbrains.mps.util.NameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SLanguage;
import org.jetbrains.mps.openapi.module.SDependency;
import org.jetbrains.mps.openapi.module.SDependencyScope;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.module.SRepository;
import org.jetbrains.mps.openapi.util.ProgressMonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Preferred way to obtain instance of {@code LanguageRegistry} is to query {@link jetbrains.mps.components.ComponentHost}, e.g.
 * through {@code jetbrains.mps.core.platform.Platform} or {@link jetbrains.mps.project.Project#getComponent(Class)} which is aware of the MPS platform.
 *
 * evgeny, 3/11/11
 */
public final class LanguageRegistry implements CoreComponent, DeployListener {
  private static final Logger LOG = Logger.getLogger(LanguageRegistry.class);

  private static LanguageRegistry INSTANCE;

  /**
   * @deprecated obtain instance through {@link jetbrains.mps.components.ComponentHost#findComponent(Class) componentHost#findComponent(LanguageRegistry.class)}
   *             or use context-specific alternative {@link #getInstance(SRepository)}.
   */
@Deprecated(since = "3.3", forRemoval = true)
  public static LanguageRegistry getInstance() {
    return INSTANCE;
  }

  /**
   * IMPORTANT: use {@link jetbrains.mps.components.ComponentHost#findComponent(Class) componentHost#findComponent(LanguageRegistry.class)} whenever \
   * possible instead of this method. USE OF THIS METHOD IS DISCOURAGED.
   * <p/>
   * At the moment, there's only 1 global LanguageRegistry. However, we move slowly towards independent
   * projects/non-global module repositories and thus would need repository-specific registries,
   * and use of the method is the proper way to  obtain registry and to think about proper
   * context in the client code right away.
   *
   * @return collection of languages available in the given context
   */
  public static LanguageRegistry getInstance(@SuppressWarnings("unused") @NotNull SRepository repository) {
    return INSTANCE;
  }

  private final Map<SLanguageId, LanguageRuntime> myLanguagesById = new HashMap<>();
  private final Map<LanguageRuntime, ModuleRuntime> myCompiledLanguages = new HashMap<>();
  private final Set<LanguageRuntime> myCompiledUnloading = new HashSet<>();

  /** Deploy an already linked browser language with no external runtime/generation dependencies. */
  public void registerCompiledLanguage(@NotNull LanguageRuntime language) {
    registerCompiledLanguage(language, ClassLoader.getSystemClassLoader(), Collections.emptyList(), Collections.emptyList());
  }

  /**
   * Browser deployment entrypoint. The manifest supplies the same dependency data that
   * normal deployment reads from LanguageDescriptor. Generated aspect creation stays real.
   * Call from the browser model/command thread and register extended languages first.
   */
  public void registerCompiledLanguage(@NotNull LanguageRuntime language, @NotNull ClassLoader loader,
                                      @NotNull Collection<SModuleReference> runtimeModules,
                                      @NotNull Collection<SModuleReference> generationTargets) {
    Objects.requireNonNull(language);
    Objects.requireNonNull(loader);
    final SModuleReference reference = language.getIdentity().getSourceModuleReference();
    final ModuleRuntime module = new ModuleRuntime(reference, loader,
        (mr, ctx) -> new LanguageRuntimeActivator(language), Flags.WithExtensions);
    final Set<SLanguageId> extensionTargets = new HashSet<>();
    myRuntimeInstanceAccess.writeLock().lock();
    try {
      if (myLanguagesById.containsKey(language.getId()) || getLanguage(language.getNamespace()) != null
          || myModuleRuntime.containsKey(reference)) {
        throw new IllegalStateException("Language is already deployed: " + language.getNamespace());
      }
      language.setLanguageRuntimeModules(runtimeModules);
      language.setGeneratesIntoTargets(generationTargets);
      myLanguagesById.put(language.getId(), language);
      try {
        reinitialize();
        language.contributeExtensions(myExtensionRegistry.forContributor(this, language, extensionTargets));
        notifyExtensionsChanged(extensionTargets);
        myModuleRuntime.put(reference, module);
        module.activate(myPlatformAccess::get);
        myCompiledLanguages.put(language, module);
      } catch (RuntimeException | Error failure) {
        myExtensionRegistry.clearContributionsOf(language, extensionTargets);
        myModuleRuntime.remove(reference);
        myLanguagesById.remove(language.getId());
        language.dispose();
        language.deinitialize();
        reinitialize();
        notifyExtensionsChanged(extensionTargets);
        throw failure;
      }
    } finally {
      myRuntimeInstanceAccess.writeLock().unlock();
    }
    notifyLoad(Collections.singleton(language));
    myDeploymentNotification.update(Collections.singleton(reference), Collections.emptyList());
    // JavaScript has one event thread; preserve ordering without starting a JVM worker.
    myDeploymentNotification.dispatch(true, () -> {});
  }

  /**
   * Unload a directly registered language. Dependants must be unloaded first.
   * A deployment listener may delay final disposal with acquireRemovedTrackingLock;
   * the runtime remains available until the last such tracking token is released.
   */
  public void unregisterCompiledLanguage(@NotNull LanguageRuntime language) {
    myRuntimeInstanceAccess.writeLock().lock();
    try {
      final ModuleRuntime module = myCompiledLanguages.get(language);
      if (module == null) throw new IllegalArgumentException("Language was not registered through browser deployment");
      if (myCompiledUnloading.contains(language)) return;
      for (LanguageRuntime other : myLanguagesById.values()) {
        if (other != language && other.getExtendedLanguages().contains(language)) {
          throw new IllegalStateException("Unload dependant language first: " + other.getNamespace());
        }
      }
      myCompiledUnloading.add(language);
      myDeploymentNotification.update(Collections.emptyList(), Collections.singleton(module.getSourceModule()));
      myDeploymentNotification.dispatch(true, () -> {
        myRuntimeInstanceAccess.writeLock().lock();
        try {
          notifyUnload(Collections.singleton(language));
          Set<SLanguageId> extensionTargets = new HashSet<>();
          myExtensionRegistry.clearContributionsOf(language, extensionTargets);
          language.dispose();
          myLanguagesById.remove(language.getId());
          myModuleRuntime.remove(module.getSourceModule());
          myCompiledLanguages.remove(language);
          myCompiledUnloading.remove(language);
          language.deinitialize();
          notifyExtensionsChanged(extensionTargets);
          reinitialize();
          module.deactivate(myPlatformAccess::get);
        } finally {
          myRuntimeInstanceAccess.writeLock().unlock();
        }
      });
    } finally {
      myRuntimeInstanceAccess.writeLock().unlock();
    }
  }

  private final Set<SModuleReference> myLanguagesNoRuntime = new HashSet<>();

  private final Map<SModuleReference, ModuleRuntime> myModuleRuntime = new HashMap<>();

  /*
   * Don't want to expose this lock right now, although perhaps would need to do it later, to facilitate scenarios with
   * LanguageRegistry that are not satisfied with withAvailableLanguages (e.g. span longer lifecycle).
   * At the moment, LR is updated inside repository's write action, and grabs myRuntimeInstanceAccess's write lock
   * as well, which may lead to deadlock  (myRuntimeInstanceAccess.read + MA.read vs MA.write+myRuntimeInstanceAccess.write from another thread)
   * and eventually we may split registry update out from model write, to run later, after write. Just need to sort out {@link LanguageRegistryListener}
   * contract that tells events are dispatched in [write] (didn't find anyone to rely on this, though). With a split, we can mitigate deadlock risk.
   */
  private final ReadWriteLock myRuntimeInstanceAccess = new ReentrantReadWriteLock();

  private final List<LanguageRegistryListener> myLanguageListeners = new CopyOnWriteArrayList<>();

  private final ClassLoaderManager myClassLoaderManager;

  private final LanguageExtensionRegistry myExtensionRegistry;

  private final Supplier<ComponentHost> myPlatformAccess;

  private final DeploymentNotificationImpl myDeploymentNotification;

  private final AtomicBoolean myReportedMissingRuntime = new AtomicBoolean(false);

  public LanguageRegistry(ClassLoaderManager loaderManager, Supplier<ComponentHost> platformAccess) {
    myClassLoaderManager = loaderManager;
    myExtensionRegistry = new LanguageExtensionRegistry();
    myPlatformAccess = platformAccess;
    myDeploymentNotification = new DeploymentNotificationImpl(this);
  }

  @Override
  public void init() {
    if (INSTANCE != null) {
      throw new IllegalStateException("double initialization");
    }
    INSTANCE = this;
    myClassLoaderManager.addListener(this);
  }

  @Override
  public void dispose() {
    myClassLoaderManager.removeListener(this);
    INSTANCE = null;
  }

  private void notifyUnload(final Collection<LanguageRuntime> languages) {
    if (languages.isEmpty()) {
      return;
    }

    for (LanguageRegistryListener l : myLanguageListeners) {
      try {
        l.beforeLanguagesUnloaded(languages);
      } catch (Exception ex) {
        LOG.error(format("Exception on language unloading; languages: %s; listener: %s", languages, l), ex);
      }
    }
  }

  private void notifyLoad(final Collection<LanguageRuntime> languages) {
    if (languages.isEmpty()) {
      return;
    }

    for (LanguageRegistryListener l : myLanguageListeners) {
      try {
        l.afterLanguagesLoaded(languages);
      } catch (Exception ex) {
        LOG.error(format("Exception on language loading; languages: %s; listener: %s", languages, l), ex);
      }
    }
  }

  @Nullable
  private LanguageRuntime createRuntime(Language l) {
    final String rtClassName = l.getModuleName() + ".Language";
    // Here, we consider few cases:
    // (a) there's no LR class
    // (b) there's legacy LR class (if we did changes to LR this release)
    // (c) LR in accordance with actual MPS version
    // Both (b) and (c) may fail during class-loading, which we treat as invalid language, although
    // for legacy versions and careless class evolution we might face otherwise valid languages which
    // fail to load due to class validation errors.
    // We aim to support binary compatibility between any two subsequent releases, thus failures for (b)
    // shall serve as an indicator we failed to maintain binary compatibility between releases
    try {
      final Class<?> rtClass = myClassLoaderManager.getClassLoader(l).loadOwnClass(rtClassName);
      if (LanguageRuntime.class.isAssignableFrom(rtClass)) {
        if (!ModuleClassLoader.hasFactory(rtClass)) {
          throw new ModuleClassLoader.FactoryNotFoundException("No registered noarg language-runtime factory: " + rtClassName);
        }
        try {
          return ModuleClassLoader.newInstance(rtClass, LanguageRuntime.class);
        } catch (Throwable failure) {
          // Constructor exceptions must not be mistaken for loader misses.
          LOG.error(String.format("Failed to load language %s", l.getModuleName()), failure);
          return null;
        }
      }
      LOG.error(String.format("Incompatible language runtime class for module %s; returning null", l.getModuleName()));
      final Class<?> rtSuper = rtClass.getSuperclass();
      if (rtSuper != null) {
        // generally it is mylang.Language extends smodel.language.LanguageRuntime; this extra information here helps to detect
        // cases when [mps-core].LanguageRuntime class got erroneously loaded through some other CL.
        final String m = "Runtime class %s was resolved through module CL %s; its superclass is %s";
        LOG.info(String.format(m, rtClassName, myClassLoaderManager.getClassLoader(l), rtSuper.getSimpleName()));
      }
      return null;
    } catch (ModuleClassLoader.FactoryNotFoundException | ClassNotFoundException ex) {
      if (!myReportedMissingRuntime.get()) {
        myReportedMissingRuntime.set(true);
        LOG.warning("Failed to load classes for some modules. Set log level to debug for more info.");
      }
      // would like to have error + exception here, but there are tests (e.g. ModulesReloadTest) that legitimately expect non-compiled modules
      // and no distinction between source and deployed modules. Now, we try to load any module added to the global repository, even if it's
      // a source module just added to a project. Once we tell deployed modules from sources (two distinct repositories would likely suffice),
      LOG.debug(String.format("Missing language runtime class for module %s (make failed?); returning null", l.getModuleName()));
      return null;
    }
  }

  /**
   * We expect Generator RT class for any generator module, both interpreted and generated.
   * However, in 2018.1 we tolerate no activator class case for compatibility with code generated with previous MPS releases.
   * In this case, we resort to TemplateModuleInterpreted supplied from LanguageRuntime#getGenerators().
   * <p/>
   * Note, there's an entanglement with TemplateModuleInterpreted/TemplateModuleInterpreted2, they don't work well when last.
   * Either doesn't track model/module changes and may answer with stale info if the instance stays for a long time.
   * Prior to 2018.1, approach was to ask language for generators (LR.getGenerators(), where new instance is created),
   * and LR+TMI assume no changes in generator module while these generators are consumed.
   * However, with activator class of generator module being there unless regenerated/reloaded, we may face issues, when template model is not in sync with
   * generated code.
   */
  private GeneratorRuntime createRuntime(Generator g) {
    SLanguage sourceLanguage = g.sourceLanguage();
    // A bit of history. The need for activator class name arise when we generate a code from a Generator module and load it at runtime.
    // First, there were no activators at all. Then, activators for generator modules with 'generated' templates were introduced.
    // They resorted to g.getSourceLanguage().getModuleName() + ".Generator", likely, not to deal with '#' in generator module name, and the fact
    // that generator module name left to '#' is identical to source language anyway. E.g. GeneratorDescriptorModelProvider assumed that
    // hash-less namespace of generator module was the same as source language. This was wrong, however, as there are generators that don't follow this
    // pattern, and attempt to load activator class like getSourceLanguage().getModuleName() + ".Generator" would fail for them. It's time to get this fixed.
    // Now, with activators generated for any Generator module,
    // we have to deal with a case when there are multiple generators in a language (well, we had to deal with it anyway, just didn't encounter it for
    // 'generated' templates). The idea is to use true name of generator module for activator class (except for '#...' suffix) as there seems to be no
    // solid ground to keep left-hand side of generator module name equal to that of its source language. Provided it was derived from the language,
    // existing compiled code (i.e. 'generated' generators) won't get broken as the value would be the same. There were no activators for generator modules
    // with interpreted templates, therefore it's the right time to introduce the change.
    // This code mimics GeneratorDescriptorModelProvider#getModelReference(), which controls name of a descriptor model (and, therefore, output location of
    // the activator class which is generated from the model).
    String mn = g.getModuleName();
    int hashSign = mn.indexOf('#');
    if (hashSign > 0) {
      mn = mn.substring(0, hashSign);
    }
    // we used to mangle name with ordinal index of generator in its language (getSourceLanguage().getOwnedGenerators().indexOf(this))
    // when there are few generators, but I don't think it's reasonable any more - it's easier to keep distinct generator modules in separate
    // namespaces rather than to guess activator name based on index in uncontrolled collection. Besides, generating two+ modules into same location
    // of their source language fools Make and leads to removal of activator of one of generated modules (same dir but file is not touched).
//    ArrayList<Generator> ownedGenerators = new ArrayList<>();
//    if (ownedGenerators.size() > 0 && ownedGenerators.indexOf(this) >= 0) {
//      className += ownedGenerators.indexOf(this);
//    }
    final String rtClassName = NameUtil.longNameFromNamespaceAndShortName(mn, "Generator");

    try {
      Class<?> rtClass = myClassLoaderManager.getClassLoader(g).loadOwnClass(rtClassName);
      if (GeneratorRuntime.class.isAssignableFrom(rtClass)) {
        final LanguageRuntime sourceLanguageRuntime = getLanguage(sourceLanguage);
        if (sourceLanguageRuntime == null) {
          String m = format("Could not find language runtime for %s to attach generator %s to", sourceLanguage.getQualifiedName(), g.getModuleName());
          // we used to throw exception, yet same considerations as createRuntime(Language), above, shall apply. If Language module misses classes,
          // createRuntime() for it would warn about 'make failed?' and go on gracefully, but its dependent generator fails with exception, which is inapropriate
//          throw new InstantiationException(m);
          LOG.warning(m);
          return null;
        }
        // Provisional constructor for TemplateModule, that takes LanguageRegistry, LanguageRuntime and Generator module. @since 2018.1
        // It's to substitute new TemplateModuleInterpreted from the LanguageRuntime subclass. We need it until all the generators are completely
        // generated. Meanwhile we have a 'partially' interpreted generators, with TemplateModule/GeneratorRuntime being generated always.
        Class<?>[] parameterTypes = {LanguageRegistry.class, LanguageRuntime.class, Generator.class};
        Object[] arguments = {this, sourceLanguageRuntime, g};
        if (!ModuleClassLoader.hasFactory(rtClass, parameterTypes)) {
          // Legacy constructor for TemplateModule. @since 2017.1
          parameterTypes = new Class<?>[]{LanguageRegistry.class, LanguageRuntime.class};
          arguments = new Object[]{this, sourceLanguageRuntime};
          if (!ModuleClassLoader.hasFactory(rtClass, parameterTypes)) {
            LOG.error(String.format("No proper constructor found in the class %s of generator %s", rtClassName, g.getModuleName()));
            return null;
          }
        }
        try {
          return ModuleClassLoader.newInstance(rtClass, GeneratorRuntime.class, parameterTypes, arguments);
        } catch (Throwable failure) {
          LOG.error(String.format("Failed to instantiate runtime %s of generator %s. Bad constructor?", rtClassName, g.getModuleName()), failure);
          return null;
        }
      } else {
        LOG.error(String.format("Generator runtime class %s from module %s is not an instance of GeneratorRuntime", rtClass.getName(), g.getModuleName()));
        return null;
      }
    } catch (ClassNotFoundException e) {
      String msg = format("Failed to load runtime %s of generator %s", rtClassName, g.getModuleName());
      if (g.generateTemplates()) {
        if (LOG.isDebugLevel()) {
          LOG.warning(msg, e);
        } else {
          LOG.warning(msg);
        }
      } else {
        // FIXME this is compatibility code. Language RT generated prior to 2018.1 included #getGenerators() implementation for interpreted templates,
        //       and generator module lacked any activator class. With 2018.1, we generate Generator RT class for every generator module (including interpreted)
        //       Remove this code once 2018.1 is out.
        LOG.debug(msg, e);
      }
    }
    return null;
  }

  @Nullable
  private ModuleRuntime createRuntime(Solution solution) {
    return new ModuleRuntime(solution.getModuleReference(), myClassLoaderManager.getClassLoader(solution), deduceRuntimeFlags(solution));
  }

  private static Flags[] deduceRuntimeFlags(AbstractModule am) {
    if (SModuleOperations.canSupplyExtensionsForMPS(am)) {
      return new Flags[] {Flags.WithExtensions};
    }
    return new Flags[] {Flags.NoExtensions};
  }

  public String toString() {
    return "LanguageRegistry";
  }

  public void addRegistryListener(LanguageRegistryListener listener) {
    myLanguageListeners.add(listener);
  }

  public void removeRegistryListener(LanguageRegistryListener listener) {
    myLanguageListeners.remove(listener);
  }

  public void addRegistryListener(@NotNull ModuleDeploymentListener listener) {
    myDeploymentNotification.addListener(listener);
  }

  public void removeRegistryListener(@NotNull ModuleDeploymentListener listener) {
    myDeploymentNotification.removeListener(listener);
  }

  /**
   * Synchronous access to actual languages in the registry.
   * It's guaranteed no change to the set of languages happen while this method is working.
   * BEWARE, {@code operation} shall not perform model read/write as it might lead to dead-lock
   * (a thread starts model write and waits for write on myRuntimeInstanceAccess, while another thread had grabbed
   * myRuntimeInstanceAccess read lock and consumer operation trues to grab model lock).
   * @param operation invoked for each actual {@link LanguageRuntime}, minimalistic and simple.
   */
  public void withAvailableLanguages(@NotNull Consumer<LanguageRuntime> operation) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      myLanguagesById.values().forEach(operation);
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  /**
   * Pretty much what {@link #withAvailableLanguages(Consumer)} does, except for a designated subset.
   * @since 2021.2
   */
  public void withAvailableLanguages(@NotNull Stream<SLanguage> languages, @NotNull Consumer<LanguageRuntime> operation) {
    withAvailableLanguages(operation, languages.map(MetaIdHelper::getLanguage));
  }

  //
  // could be public, if necessary
  /*package*/ void withAvailableLanguages(@NotNull Consumer<LanguageRuntime> operation, @NotNull Stream<SLanguageId> languages) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      languages.map(myLanguagesById::get).filter(Objects::nonNull).forEach(operation);
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  /**
   * Access specific present (non-null) aspect instance of supplied languages.
   * @since 2023.2
   */
  public <T extends ILanguageAspect> void withAvailableAspects(@NotNull Stream<SLanguage> languages, @NotNull Class<T> aspectClass, @NotNull Consumer<T> aspectOperation) {
    withAvailableLanguages(languages, lr -> {
      final T aspectInstance = lr.getAspect(aspectClass);
      if (aspectInstance != null) {
        aspectOperation.accept(aspectInstance);
      }
    });
  }

  /**
   * Access specific present (non-null) aspect instance of all available languages.
   * @since 2023.3
   */
  public <T extends ILanguageAspect> void withAvailableAspects(@NotNull Class<T> aspectClass, @NotNull Consumer<T> aspectOperation) {
    withAvailableLanguages(lr -> {
      final T aspectInstance = lr.getAspect(aspectClass);
      if (aspectInstance != null) {
        aspectOperation.accept(aspectInstance);
      }
    });
  }


  /**
   * @return snapshot of languages known to the registry at the given moment.
   *         May not reflect actual state (a language might get unloaded), but as long as it's about identity objects, it's not that important to
   *         keep the collection exact.
   */
  public Collection<SLanguage> getAllLanguages() {
    ArrayList<SLanguage> rv = new ArrayList<>(100);
    withAvailableLanguages(lr -> rv.add(lr.getIdentity()));
    return rv;
  }

  @Nullable
  public LanguageRuntime getLanguage(SLanguage language) {
    return getLanguage(MetaIdHelper.getLanguage(language));
  }

  @Nullable
  public LanguageRuntime getLanguage(SLanguageId id) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      return myLanguagesById.get(id);
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  @Nullable
  public LanguageRuntime getLanguage(String namespace) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      for (LanguageRuntime l : myLanguagesById.values()) {
        if (Objects.equals(l.getNamespace(), namespace)) {
          return l;
        }
      }
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
    return null;
  }

  @Nullable
  public LanguageRuntime getLanguage(Language language) {
    return getLanguage(MetaIdByDeclaration.getLanguageId(language));
  }

  /**
   *
   * @param generatorIdentity we use {@link SModuleReference} to identify generator, not to introduce a dedicated {@code SGenerator} similar to {@link SLanguage}
   */
  @Nullable
  public GeneratorRuntime getGenerator(@NotNull SModuleReference generatorIdentity) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      final ModuleRuntime mr = myModuleRuntime.get(generatorIdentity);
      GeneratorRuntime[] rv = new GeneratorRuntime[] {null};
      if (mr != null) {
        mr.forActivatorIfInstance(GeneratorRuntimeActivator.class, a -> rv[0] = a.getGeneratorRuntime());
      }
      return rv[0];
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  // ClassLoaderManager/DeployListener part


  @Override
  public void onUnloaded(@NotNull final ResourceTrackerCallback callback, @NotNull ProgressMonitor monitor) {
    // we need to identify each request individually
    final Object unloadToken = Long.toString(System.currentTimeMillis(), 16);
    final Set<ReloadableModule> unloadedModules = callback.acquire(unloadToken);
    if (LOG.isTraceLevel()) {
      LOG.trace("CLM unload token acquired: " + unloadToken);
    }
    try {
      myRuntimeInstanceAccess.writeLock().lock();
      monitor.start("Solution Runtime", 5);
      final UnloadRecord modulesToUnload = new UnloadRecord();
      for (Solution s : collectSolutionModules(unloadedModules)) {
        // get, not remove as we notify all first, and only then remove them.
        final ModuleRuntime moduleRuntime = myModuleRuntime.get(s.getModuleReference());
        if (moduleRuntime == null) {
          continue;
        }
        modulesToUnload.add(moduleRuntime, true);
      }
      monitor.advance(1);

      monitor.step("Generator Runtime");
      for (Generator generator : collectGeneratorModules(unloadedModules)) {
        final ModuleRuntime moduleRuntime = myModuleRuntime.get(generator.getModuleReference());
        if (moduleRuntime == null) {
          continue;
        }
        modulesToUnload.add(moduleRuntime, false);
        GeneratorRuntime[] generatorRuntime = new GeneratorRuntime[] {null};
        moduleRuntime.forActivatorIfInstance(GeneratorRuntimeActivator.class, a -> generatorRuntime[0] = a.getGeneratorRuntime());
        if (generatorRuntime[0] == null) {
          LOG.error(String.format("ModuleRuntime for %s present but doesn't hold activator instance", generator.getModuleReference()));
          continue;
        }
        // let runtime know we don't need its services anymore, but do it the moment it's still in complete state.
        generatorRuntime[0].deactivate(); // TODO unite with MR.Activator interface
        LanguageRuntime srcLangRuntime = generatorRuntime[0].getSourceLanguage();
        srcLangRuntime.unregister(generatorRuntime[0]);
      }
      monitor.advance(1);

      monitor.step("Language Runtime");
      Set<LanguageRuntime> languagesToUnload = new HashSet<>();
      for (Language language : collectLanguageModules(unloadedModules)) {
        SLanguageId sl = MetaIdByDeclaration.getLanguageId(language);
        if (!myLanguagesById.containsKey(sl)) {
          if (!myLanguagesNoRuntime.remove(language.getModuleReference())) {
            LOG.warning(String.format("No language %s to unload", language.getModuleReference()));
          } // we've seen this langauge, but there's no runtime class (e.g. broken or missing), no reason to warn then
        } else {
          languagesToUnload.add(myLanguagesById.get(sl));
        }
        final ModuleRuntime moduleRuntime = myModuleRuntime.get(language.getModuleReference());
        if (moduleRuntime != null) {
          modulesToUnload.add(moduleRuntime, true);
        }
      }
      monitor.advance(1);

      final ModuleRuntimeContext rtc = myPlatformAccess::get;
      myDeploymentNotification.update(Collections.emptyList(), modulesToUnload.notifyDeployment());
      myDeploymentNotification.dispatch(true, () -> {
        if (LOG.isTraceLevel()) {
          LOG.trace("Deployment notification is over, actual runtime deactivation to start. Token: " + unloadToken);
        }

        // let CLs go
        callback.release(unloadToken);
        if (LOG.isTraceLevel()) {
          LOG.trace("CLM unload  token released: " + unloadToken);
        }
      });

      // forget about runtimes right away, but dispose them only once all processing in listeners don
      myModuleRuntime.values().removeAll(modulesToUnload.myModules);

      monitor.step("Language Registry Listeners");
      notifyUnload(languagesToUnload);
      monitor.advance(1);

      Set<SLanguageId> extensionTargets = new HashSet<>();
      for (LanguageRuntime languageRuntime : languagesToUnload) {
        // XXX why do I use map at LER and don't store data right inside LR instance?
        myExtensionRegistry.clearContributionsOf(languageRuntime, extensionTargets);
        languageRuntime.dispose();
        myLanguagesById.remove(languageRuntime.getId());
      }
      notifyExtensionsChanged(extensionTargets);
      reinitialize();

      // I'd love to deactivate MR only when all listeners of myDeploymentNotification done processing,
      // but delaying deactivation without delaying activation of a reloaded module leaves MPS in inconsistent state
      // (namely j.m.make.facets.ModuleActivator and FacetRegistry, which keeps single facet instance per IName)
      // hence as a quick'n'dirty workaround, I deactivate a module right away, even though its runtime is expected to be
      // "functional" until ModuleDeploymentListener(s) notify they are done processing notifications.
      for (ModuleRuntime rt : modulesToUnload.myModules) {
        rt.deactivate(rtc);
      }
    } finally {
      myRuntimeInstanceAccess.writeLock().unlock();
    }
    monitor.done();
  }

  @Override
  public void onLoaded(@NotNull Set<ReloadableModule> loadedModules, @NotNull ProgressMonitor monitor) {
    monitor.start("Language Runtime", 5);
    // XXX likely, would need to use PluginSorter here to sort loaded modules to get their activators running in proper order
    // (i.e. to respect dependencies)
    LoadRecord loadedRuntimes = new LoadRecord();
    ArrayList<LanguageRuntime> transitional = new ArrayList<>();
    try {
      myRuntimeInstanceAccess.writeLock().lock();
      for (Language language : collectLanguageModules(loadedModules)) {
        try {
          LanguageRuntime langRuntime = createRuntime(language);
          if (langRuntime == null) {
            myLanguagesNoRuntime.add(language.getModuleReference());
            continue;
          }
          SLanguageId sl = langRuntime.getId();
          if (myLanguagesById.containsKey(sl)) {
            String msg = String.format("There is already a language '%s'", myLanguagesById.get(sl));
            LOG.error(msg, new IllegalArgumentException(msg));
            continue;
          }
          myLanguagesById.put(sl, langRuntime);
          final LanguageRuntimeActivator activator = new LanguageRuntimeActivator(langRuntime);
          ModuleRuntime.ActivatorFactory af = (mr,ctx) -> activator;
          loadedRuntimes.add(new ModuleRuntime(language.getModuleReference(),myClassLoaderManager.getClassLoader(language), af, Flags.WithExtensions), true);
          // perhaps, has to be part of loadedRuntimes cycle, below, but this is the place I've got Language instance,
          // don't want to bother recording Pairs
          fixupLanguageRuntime(language, langRuntime);
          transitional.add(langRuntime);
        } catch (LinkageError le) {
          processLinkageErrorForLanguage(language, le);
        }
      }
      reinitialize();
      Set<SLanguageId> extensionTargets = new HashSet<>();
      // perhaps, could be part of LangRuntime.initialize(LangReg), if I expose (package-local) LangExtReg from here
      for (LanguageRuntime lr : transitional) {
        final LanguageExtensions languageExtensions = myExtensionRegistry.forContributor(this, lr, extensionTargets);
        lr.contributeExtensions(languageExtensions);
      }
      // note, extensionTargets is filled the moment LR.contributeExtensions() works, not inside LER.forContributor().
      //
      // if I improve aspects/extensions for any module, would need to move this down, when all the modules get the chance to initialise
      // at the moment, it's only languages, that's why I do it right after I handled all the languages
      notifyExtensionsChanged(extensionTargets);
      monitor.advance(1);

      monitor.step("Generator Runtime");
      for (Generator generator : collectGeneratorModules(loadedModules)) {
        GeneratorRuntime generatorRuntime = createRuntime(generator);
        if (generatorRuntime == null) {
          // either interpreted or no generator at all, let generated LanguageRuntime#getGenerators() decide
          continue;
        }
        LanguageRuntime srcLangRuntime = generatorRuntime.getSourceLanguage();
        srcLangRuntime.register(generatorRuntime);
        GeneratorRuntimeActivator activator = new GeneratorRuntimeActivator(generatorRuntime);
        loadedRuntimes.add(new ModuleRuntime(generator.getModuleReference(), myClassLoaderManager.getClassLoader(generator), (mr, ctx)->activator, Flags.NoExtensions), false);
      }
      monitor.advance(1);

      monitor.step("Solution Runtime");
      for (Solution s : collectSolutionModules(loadedModules)) {
        ModuleRuntime moduleRuntime = createRuntime(s);
        if (moduleRuntime == null) {
          continue;
        }
        loadedRuntimes.add(moduleRuntime, true);
      }
      monitor.advance(1);

      monitor.step("Activators...");
      ModuleRuntimeContext rtc = myPlatformAccess::get;
      for (ModuleRuntime rt : loadedRuntimes.myModules) {
        // XXX perhaps, 2 separate loops, one to register in the map, another to activate()? Just in case
        // activate code looks up MR of another module (although generally shall not).
        ModuleRuntime old = myModuleRuntime.put(rt.getSourceModule(), rt);
        if (old != null) {
          LOG.warning(String.format("There's already runtime instance for module '%s'", old.getSourceModule()));
        }
        rt.activate(rtc);
      }
      monitor.advance(1);
    } finally {
      myRuntimeInstanceAccess.writeLock().unlock();
    }

    monitor.step("Language Registry Listeners");
    // XXX perhaps, shall grab read lock of myRuntimeInstanceAccess? Or it's enough to assume we would never get into onLoaded again while we are not
    //     over with the previous one?
    notifyLoad(transitional);
    monitor.advance(1);
    monitor.done();
    myDeploymentNotification.update(loadedRuntimes.notifyDeployment(), Collections.emptyList());
    myDeploymentNotification.dispatch(false, () -> {});
  }

  private static Collection<Language> collectLanguageModules(Collection<ReloadableModule> modules) {
    return collectModules(Language.class, modules);
  }
  private static Collection<Generator> collectGeneratorModules(Collection<ReloadableModule> modules) {
    return collectModules(Generator.class, modules);
  }
  private static Collection<Solution> collectSolutionModules(Collection<ReloadableModule> modules) {
    return collectModules(Solution.class, modules);
  }
  @SuppressWarnings("unchecked")
  private static <E extends SModule> Collection<E> collectModules(Class<E> cls, Collection<ReloadableModule> modules) {
    // I assume no SModule duplicates comes from CLM dispatch, but in case it's not true, toSet() might be better
    return modules.stream().map(ReloadableModule::getModule).filter(cls::isInstance).map(module -> (E) module).collect(Collectors.toList());
  }

  private void reinitialize() {
    myLanguagesById.values().forEach(LanguageRuntime::deinitialize);
    myLanguagesById.values().forEach(languageRuntime -> languageRuntime.initialize(this));
  }

  private void notifyExtensionsChanged(Set<SLanguageId> extensionTargets) {
    // in case any existing/new language has cached extensions for its aspects, let them know there might be new one
    for (SLanguageId lid : extensionTargets) {
      final LanguageRuntime extTargetRT = myLanguagesById.get(lid);
      if (extTargetRT != null) {
        // assume target language module could be present for CL but LR failed to instantiate, see
        // comments inside LER.forContributor.
        // Another case is when target language has been disposed along with the contributor, we don't care to notify
        // disposed RT about the change, it is going to be disposed anyway
        extTargetRT.languageExtensionsChanged();
      }
    }
  }

  private void fixupLanguageRuntime(Language sourceModule, LanguageRuntime langRuntime) {
    final Collection<SModuleReference> runtimeModulesReferences;
    ArrayList<SModuleReference> generatesInto = new ArrayList<>(4);
    final LanguageDescriptor md = sourceModule.getModuleDescriptor();
    if (md != null) {
      // AM.getDeclaredDependencies() doesn't really stick to 'declared' only, it also
      // goes an extra mile to collect dependencies I don't care about (at least here)
      runtimeModulesReferences = md.getRuntimeModules();
      for (Dependency dependency : md.getDependencies()) {
        if (dependency.getScope() == SDependencyScope.GENERATES_INTO) {
          generatesInto.add(dependency.getModuleRef());
        }
      }
    } else {
      // not that I think it's essential to keep this 'else', there are hardly Language modules
      // w/o MD, but this code shows we can handle this scenario as well.
      runtimeModulesReferences = sourceModule.getRuntimeModulesReferences();
      for (SDependency dd : sourceModule.getDeclaredDependencies()) {
        if (dd.getScope() == SDependencyScope.GENERATES_INTO) {
          generatesInto.add(dd.getTargetModule());
        }
      }
    }
    langRuntime.setLanguageRuntimeModules(runtimeModulesReferences);
    langRuntime.setGeneratesIntoTargets(generatesInto);
  }

  /*package*/ LanguageExtensionRegistry getExtensionRegistry() {
    // provisionally expose the registry. shall keep all the operations over the registry local to this class and guard them with myRuntimeInstanceAccess lock
    return myExtensionRegistry;
  }

  /*package*/ void withAvailableModuleRuntime(Consumer<Function<SModuleReference, ModuleRuntime>> callback) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      callback.accept(myModuleRuntime::get);
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  /**
   * Generified {@link #withAvailableLanguages(Stream, Consumer)} for any module runtime.
   * @param operation invoked for each discovered module runtime
   * @since 2023.3
   */
  public void withModuleRuntime(Stream<SModuleReference> modules, Consumer<ModuleRuntime> operation) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      modules.map(myModuleRuntime::get).filter(Objects::nonNull).forEach(operation);
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  public <T> void withAvailableExtensions(final Class<T> kind, final MatchRequest matchRequest, Consumer<T> operation) {
    try {
      myRuntimeInstanceAccess.readLock().lock();
      myModuleRuntime.values().stream().filter(ModuleRuntime::withExtensions).flatMap(mr -> mr.extensionsFor(kind)).filter(e -> e.matches(matchRequest))
                     .map(Extension::get).filter(Optional::isPresent).map(Optional::get).forEach(operation);
    } finally {
      myRuntimeInstanceAccess.readLock().unlock();
    }
  }

  private static void processLinkageErrorForLanguage(Language language, LinkageError linkageError) {
    LOG.error("Caught a linkage error while creating LanguageRuntime for the `" + language + "` language." +
        "Probably the language sources/classes are outdated, try rebuilding the project.", linkageError);
    LOG.warning("MPS will attempt running in a inconsistent state.");
  }

  private static class Record {
    protected final List<ModuleRuntime> myModules = new ArrayList<>();
    private final Set<SModuleReference> myNotifications = new HashSet<>();

    /*package*/ void add(ModuleRuntime mr, boolean needsNotify) {
      // XXX needsNotify: for the time being, we don't include generators, although eventually shall include these, too
      myModules.add(mr);
      if (needsNotify) {
        myNotifications.add(mr.getSourceModule());
      }
    }

    /*package*/ Collection<SModuleReference> notifyDeployment() {
      return Collections.unmodifiableSet(myNotifications);
    }
  }

  private static class LoadRecord extends Record {

  }

  private static class UnloadRecord extends Record {

  }
}
