/*
 * Copyright 2003-2025 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0.
 * Browser adaptation: inject the selected platform instead of starting the desktop IDE.
 */
package jetbrains.mps.ide;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import java.util.Objects;
import jetbrains.mps.classloading.ClassLoaderManager;
import jetbrains.mps.core.platform.Platform;
import jetbrains.mps.library.LibraryInitializer;
import jetbrains.mps.persistence.PersistenceRegistry;
import jetbrains.mps.smodel.MPSModuleRepository;
import org.jetbrains.mps.openapi.persistence.PersistenceFacade;

public class MPSCoreComponents implements Disposable {
  private final Platform platform;
  private boolean disposed;

  public MPSCoreComponents() {
    this(ApplicationManager.getApplication().getService(Platform.class));
  }

  public MPSCoreComponents(Platform platform) {
    this.platform = Objects.requireNonNull(platform, "Register the browser Platform before MPSCoreComponents");
  }

  public Platform getPlatform() {
    if (disposed) throw new IllegalStateException("MPS core components are disposed");
    return platform;
  }

  public PersistenceFacade getPersistenceFacade() { return getPlatform().findComponent(PersistenceRegistry.class); }
  public LibraryInitializer getLibraryInitializer() { return getPlatform().findComponent(LibraryInitializer.class); }
  public ClassLoaderManager getClassLoaderManager() { return getPlatform().findComponent(ClassLoaderManager.class); }
  @Deprecated public MPSModuleRepository getModuleRepository() { return getPlatform().findComponent(MPSModuleRepository.class); }

  public static MPSCoreComponents getInstance() {
    return Objects.requireNonNull(ApplicationManager.getApplication().getComponent(MPSCoreComponents.class),
        "Register MPSCoreComponents before starting an editor");
  }

  @Override public void dispose() {
    if (disposed) return;
    disposed = true;
    platform.dispose();
  }
}
