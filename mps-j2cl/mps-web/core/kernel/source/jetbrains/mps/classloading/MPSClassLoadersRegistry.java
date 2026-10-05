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

import jetbrains.mps.classloading.DeployListener.ResourceTrackerCallback;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.module.ReloadableModule;
import jetbrains.mps.project.facets.JavaModuleFacet;
import jetbrains.mps.project.facets.JavaModuleFacet.LoadClasses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SModuleReference;
import org.jetbrains.mps.openapi.util.Consumer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class stores a map SModuleReference->MPSModuleClassLoader.
 * <p>
 * Note: the actual dispose of ModuleClassLoaders happen asynchronously with
 * the help of {@link DisposeSession}
 * </p>
 */
final class MPSClassLoadersRegistry {
  private static final Logger LOG = Logger.getLogger(MPSClassLoadersRegistry.class);

  private final Map<SModuleReference, ModuleClassLoaderSupport> myMPSClassLoaders = new HashMap<>();
  private final Map<SModuleReference, MPSModuleClassLoader> myIDEAClassLoaders = new HashMap<>();
  private final ReadWriteLock myAccessClassLoaders = new ReentrantReadWriteLock(); // guards access to myMPSClassLoaders & myIDEAClassLoaders
  private final List<DisposeSession> myDisposeSessions = new LinkedList<>();

  public MPSClassLoadersRegistry() {
  }

  @Nullable
  /*package*/ MPSModuleClassLoader getClassLoader(@NotNull SModuleReference mref) {
    try {
      // 10 seconds is just an attempt to avoid deadlock and not to affect regular debug.
      if (!myAccessClassLoaders.readLock().tryLock(10, TimeUnit.SECONDS)) {
        LOG.warning("Attempt to get CL for %s failed due to timeout".formatted(mref.getModuleName()));
        return null;
      }
    } catch (InterruptedException ex) {
      LOG.warning("Attempt to get CL for %s failed due to exception".formatted(mref.getModuleName()), ex);
      return null;
    }
    try {
      return _getCL(mref);
    } finally {
      myAccessClassLoaders.readLock().unlock();
    }
  }

  // expects myAccessClassLoaders.readLock()!!!
  private MPSModuleClassLoader _getCL(SModuleReference mref) {
    final ModuleClassLoaderSupport clSupport = myMPSClassLoaders.get(mref);
    if (clSupport != null) {
      return clSupport.getModuleClassLoader(); // shall not return null
    }
    return myIDEAClassLoaders.get(mref);
  }

  // internal method for DisposeSession only (just an effective way to guard CL access)
  private Set<MPSModuleClassLoader> _getClassLoaders(Stream<SModuleReference> modules) {
    // XXX decided not to use tryLock(timeout) here as we use this inside model write and CLM manipulation only
    myAccessClassLoaders.readLock().lock();
    try {
      return modules.map(this::_getCL).filter(Objects::nonNull).collect(Collectors.toSet());
    } finally {
      myAccessClassLoaders.readLock().unlock();
    }
  }

  /*package*/ boolean hasCL(SModuleReference mRef) {
    return getClassLoader(mRef) != null;
  }

  /**
   * Note, this method doesn't destroy/dispose any CL, it merely records a fact there are no CL for a module and forgets their instances
   *
   * @param session enumerates modules and their ClassLoaders we are no longer interested to track
   */
  /*package*/ void forgetClassLoaders(@NotNull DisposeSession session) {
    myAccessClassLoaders.writeLock().lock();
    try {
      // FWIW, session, when created, filters modules with CL present; additional checks here are likely excessive, but as a tribute
      // to legacy code and an extra sanity check - why not
      for (MPSModuleClassLoader mcl : session.classloaders()) {
        MPSModuleClassLoader present = _getCL(mcl.getModule());
        if (present == null) {
          LOG.error("Module %s is not loaded -- cannot destroy %s".formatted(mcl.getModule().getModuleName(), mcl));
        } else {
          // present != null indicates either myMPSClassLoaders or myIDEAClassLoaders got it
          if (mcl != present) {
            LOG.error("Module %s actual CL %s doesn't match CL to dispose %s".formatted(mcl.getModule().getModuleName(), present, mcl));
          }
          myMPSClassLoaders.remove(mcl.getModule());
          myIDEAClassLoaders.remove(mcl.getModule());
        }
      }
    } finally {
      myAccessClassLoaders.writeLock().unlock();
    }
  }

  /**
   * Once this method finishes, registry doesn't keep record of SModule or ReloadableModule instances.
   *
   * @param toLoad modules NOT tracked by this registry as LOADED (i.e. matching #getUnloadedCondition()) but otherwise ready (dependency-wise) to get loaded
   * @param deps answers with dependencies of a module
   * @return subset of {@code toLoad} modules that got new ClassLoader
   */
  /*package*/ Set<ReloadableModule> createClassLoaders(final Collection<? extends ReloadableModule> toLoad, Function<SModuleReference, Collection<SModuleReference>> deps) {
    final HashSet<ReloadableModule> result = new HashSet<>();
    myAccessClassLoaders.writeLock().lock();
    try {
      for (ReloadableModule module : toLoad) {
        SModuleReference moduleReference = module.getModuleReference();
        if (_getCL(moduleReference) != null) {
          // fine, there's CL for this module, do nothing. If it has to be re-loaded, I'd expect it to show up in #forgetClassLoaders() first.
          // This is what getUnloadedCondition() used to ensure in CLM.preLoadModules()
          continue;
        }
        // TODO would be great to send out events only for modules with non-empty CL, i.e. to avoid
        //       warnings like "Missing language runtime class" on loaded + "No language with id" on unloaded
        //       for modules not yet compiled. Perhaps, ModuleClassLoaderSupport could check CP entry existence?
        //
        // give precedence to CCLF.
        final CustomClassLoadingFacet customClassLoadingFacet = module.getFacet(CustomClassLoadingFacet.class);
        if (customClassLoadingFacet != null ) {
          // SModuleOperations.classesAvailableToMPS() aka ModulesWatcher.myWatchableCondition respects modules with CCLF
          ClassLoader dd;
          if (customClassLoadingFacet.isValid() && (dd = customClassLoadingFacet.getClassLoader()) != null) {
            myIDEAClassLoaders.put(moduleReference, createDelegateClassLoader(moduleReference, dd));
            result.add(module);
          } else {
            LOG.warning(String.format("Module %s got invalid custom ClassLoader, ignored and not loaded", moduleReference.getModuleName()));
          }
          continue; // ignore JMF when CCLF is there, even if it didn't succeed
        }
        final JavaModuleFacet jmf = module.getFacet(JavaModuleFacet.class);
        if (jmf.getLoadClasses() == LoadClasses.ManagedByMPS) {
          ModuleClassLoaderSupport clSupport = prepareModuleClassLoader(module.getModule(), deps.apply(moduleReference));
          myMPSClassLoaders.put(moduleReference, clSupport);
        } else if (jmf.getLoadClasses() == LoadClasses.ManagedByContributor) {
          ClassLoader classLoader = new RootClassloaderLookup(module.getModule()).get();
          // XXX these CLs used to be non-reloadable, but I don't see a reason to treat them differently compared
          //     to a CL of a regular MPS module. We dispose and reload them as needed
          myIDEAClassLoaders.put(moduleReference, createDelegateClassLoader(moduleReference, classLoader));
        } else {
          // shall not happen, jmf.getLoadClasses().classesAvailable() is precondition of myWatchableCondition
          LOG.error(String.format("Module %s got unexpected class loading setting: %s", module.getModuleName(), jmf.getLoadClasses()));
          continue;
        }
        result.add(module);
      }
    } finally {
      myAccessClassLoaders.writeLock().unlock();
    }
    return result;
  }

  private ModuleClassLoaderSupport prepareModuleClassLoader(@NotNull SModule module, Collection<SModuleReference> deps) {
    LOG.debug("Creating ModuleClassLoader for " + module);
    return ModuleClassLoaderSupport.create(module, this, deps);
  }

  @NotNull
  private MPSModuleClassLoader createDelegateClassLoader(SModuleReference module, ClassLoader classLoader) {
    LOG.debug("Creating DelegateClassLoader for " + module);
    // Would be nice to avoid unnecessary nesting of classloaders, but we have to keep common MPSModuleClassLoader subclass
    return new IDEADelegatingModuleClassLoader(module, classLoader);
  }

  public void dispose() {
    for (DisposeSession session : myDisposeSessions) {
      session.destroy();
    }
    myDisposeSessions.clear();
  }

  @NotNull
  public DisposeSession createDisposeSession(@NotNull Set<ReloadableModule> modulesToUnload) {
    // XXX not really nice is that read here and write in #forgetClassLoaders() are separated by notification broadcast, but
    //     I don't expect any code to alter registry in between (we're inside model write here, all we can get are parallel reads with CL access lock)
    Set<MPSModuleClassLoader> classLoaders = _getClassLoaders(modulesToUnload.stream().map(SModule::getModuleReference));

    // next is alternative to getLoadedCondition() filtering that used to happen before #createSession() call
    final Set<SModuleReference> withClassloaders = classLoaders.stream().map(MPSModuleClassLoader::getModule).collect(Collectors.toSet());
    Set<ReloadableModule> toUnload =
        modulesToUnload.stream().filter(m -> withClassloaders.contains(m.getModuleReference())).collect(Collectors.toSet());

    DisposeSession ds = new DisposeSession(toUnload, classLoaders, myDisposeSessions::remove,  myDisposeSessions.size() + 1);
    myDisposeSessions.add(ds);
    return ds;
  }

  static final class DisposeSession {
    private static final int MAX_MINUTES_FOR_STALE_CLASSLOADERS = 5;
    private final Set<ReloadableModule> myModulesToUnload;
    private final Set<MPSModuleClassLoader> myModuleClassloaders2Dispose;
    private final ConcurrentMap<Object, Boolean> myBlockingRequestors = new ConcurrentHashMap<>();
    private final Long myCreationTime;
    private final Consumer<DisposeSession> myOnDisposed;
    private final int myActualNumberOfSessions;
    private volatile boolean myDisposeHappened = false;
    private volatile Long myPlanningDisposalTime;
    private volatile Long myActualDisposalTime;

    private final ResourceTrackerCallback myTrackerCallback = new ResourceTrackerCallback() {
      @NotNull
      @Override
      public Set<ReloadableModule> acquire(@NotNull Object requestor) {
        doAquire(requestor);
        return Collections.unmodifiableSet(myModulesToUnload);
      }

      @NotNull
      @Override
      public Set<ModuleClassLoader> acquire2(@NotNull Object requestor) {
        doAquire(requestor);
        return myModuleClassloaders2Dispose.stream().filter(ModuleClassLoader.class::isInstance).map(ModuleClassLoader.class::cast).collect(Collectors.toSet());
      }

      @Override
      public void release(@NotNull Object requestor) {
        doRelease(requestor);
      }
    };

    public DisposeSession(@NotNull Set<ReloadableModule> modulesToUnload,
                          @NotNull Set<MPSModuleClassLoader> theirClassloaders,
                          @NotNull Consumer<DisposeSession> onDisposed,
                          int actualNumberOfSessions) {
      myOnDisposed = onDisposed;
      myActualNumberOfSessions = actualNumberOfSessions;
      myCreationTime = System.currentTimeMillis();
      myModulesToUnload = modulesToUnload;
      myModuleClassloaders2Dispose = theirClassloaders;
    }

    /*package*/ void doAquire(@NotNull Object requestor) {
      if (null != myBlockingRequestors.putIfAbsent(requestor, Boolean.TRUE)) {
        throw new IllegalStateException(String.format("Requestor '%s' has invoked #acquire more than once", requestor));
      }
      if (myActualDisposalTime != null || myDisposeHappened) {
        throw new IllegalStateException(String.format("Requestor '%s' tries to #acquire already disposed session", requestor));
      }
    }

    /*package*/ void doRelease(@NotNull Object requestor) {
      // we can get here from multiple threads at the same time
      Boolean value = myBlockingRequestors.remove(requestor);
      if (value == null) {
        LOG.warning("Please report next message and the steps that lead to it to MPS-36887");
        LOG.error("DisposeSession release comes from an unknown (already removed?) requestor " + requestor);
      }
      checkAndDisposeIfReady();
    }

    private synchronized void checkAndDisposeIfReady() {
      if (isReadyToDispose() && myBlockingRequestors.isEmpty() && !myDisposeHappened) {
        doDispose();
      } // else we wait for the last #release
    }


    public synchronized void readyToDispose() {
      assert myCreationTime != null;
      assert myPlanningDisposalTime == null;
      myPlanningDisposalTime = System.currentTimeMillis();
      checkAndDisposeIfReady();
    }

    // shall answer true iff readyToDispose() was signaled
    private boolean isReadyToDispose() {
      return myPlanningDisposalTime != null;
    }

    public synchronized void destroy() {
      Long now = System.currentTimeMillis();
      if (!myDisposeHappened && ((now - myPlanningDisposalTime) / 60000L) > MAX_MINUTES_FOR_STALE_CLASSLOADERS) {
        Set<Object> blockers = myBlockingRequestors.keySet();
        LOG.error(String.format("The following requestors have not invoked #release and probably are leaking ModuleClassLoaders: %s", blockers));
      }
      if (!myDisposeHappened) {
        doDispose();
      }
    }

    public synchronized boolean isDisposed() {
      return myDisposeHappened;
    }

    private synchronized void doDispose() {
      assert myCreationTime != null;
      assert myPlanningDisposalTime != null;
      assert !myDisposeHappened : "Dispose has already been done.";
      myActualDisposalTime = System.currentTimeMillis();
      LOG.debug("Disposing " + myModuleClassloaders2Dispose.size() + " class loaders");
      for (MPSModuleClassLoader classLoader : myModuleClassloaders2Dispose) {
        classLoader.dispose();
      }
      // help GC by removing references to CL
      myModuleClassloaders2Dispose.clear();
      myModulesToUnload.clear();
      myDisposeHappened = true;
      myOnDisposed.consume(this);
    }

    // internal use only
    Set<ReloadableModule> modules() {
      return Collections.unmodifiableSet(myModulesToUnload);
    }

    // internal use only
    Set<MPSModuleClassLoader> classloaders() {
      return Collections.unmodifiableSet(myModuleClassloaders2Dispose);
    }

    // CLM use only; gives total number of actual (non-disposed) sessions the moment this one was created
    int actualNumberOfSessions() {
      return myActualNumberOfSessions;
    }

    @NotNull
    ResourceTrackerCallback getTrackerCallback() {
      return myTrackerCallback;
    }
  }
}
