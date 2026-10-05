/*
 * Copyright 2003-2026 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0.
 * Browser adaptation: use the real in-memory ProjectRepository without desktop file watchers.
 */
package jetbrains.mps.project;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.InvalidDataException;
import java.io.File;
import java.io.IOException;
import jetbrains.mps.ide.MPSCoreComponents;
import jetbrains.mps.vfs.IFile;
import jetbrains.mps.vfs.IFileSystem;
import org.jdom.JDOMException;

public abstract class MPSProject extends ProjectBase implements FileBasedProject, Disposable {
  private final com.intellij.openapi.project.Project project;
  private boolean opened;
  private IFileSystem fileSystem;
  private final jetbrains.mps.smodel.ProjectModelAccess2 modelAccess;

  public MPSProject(com.intellij.openapi.project.Project project) {
    super(project.getName(), MPSCoreComponents.getInstance().getPlatform(), false);
    this.project = project;
    modelAccess = new jetbrains.mps.smodel.ProjectModelAccess2(this, jetbrains.mps.smodel.BrowserModelAccess.getInstalled());
    ProjectRepository repository = new ProjectRepository(this,
        myPlatform.findComponent(jetbrains.mps.smodel.MPSModuleRepository.class),
        myPlatform.findComponent(jetbrains.mps.extapi.module.SRepositoryRegistry.class), modelAccess);
    repository.init();
    initRepository(repository);
  }

  @Override public void projectOpened() {
    if (isDisposed()) throw new IllegalStateException("Project is disposed");
    if (opened) return;
    // Modules are explicitly registered by the browser host, without scanning host paths.
    myProjectManager.projectOpened(this);
    opened = true;
  }

  @Override public void projectClosed() {
    if (!opened) return;
    myProjectManager.projectClosed(this);
    opened = false;
  }

  @Override public void dispose() {
    if (isDisposed()) return;
    modelAccess.dispose();
    jetbrains.mps.ide.undo.WorkbenchUndoHandler undo = project.getServiceIfCreated(jetbrains.mps.ide.undo.WorkbenchUndoHandler.class);
    if (undo != null) undo.dispose();
    jetbrains.mps.nodefs.NodeVirtualFileSystem nodeFiles = com.intellij.openapi.application.ApplicationManager.getApplication()
        .getServiceIfCreated(jetbrains.mps.nodefs.NodeVirtualFileSystem.class);
    if (nodeFiles != null) nodeFiles.releaseRepository(getRepository());
    projectClosed();
    super.dispose();
  }

  public com.intellij.openapi.project.Project getProject() { return project; }
  @Override public String getName() { return super.getName(); }

  @Override public File getProjectFile() {
    String path = project.getPresentableUrl();
    if (path == null) throw new IllegalStateException("This in-memory project has no project file");
    return new File(path);
  }

  @Override public void save() { project.save(); }

  public static MPSProject open(String path) throws InvalidDataException, IOException, JDOMException {
    throw new UnsupportedOperationException("Create a browser project and register its modules explicitly");
  }

  @Override public <T> T getComponent(Class<T> type) {
    if (isDisposed()) return null;
    T component = super.getComponent(type);
    return component != null ? component : project.getComponent(type);
  }

  /** Browser host extension: attach a supported virtual filesystem explicitly. */
  public void setFileSystem(IFileSystem provider) { fileSystem = provider; }
  public final IFileSystem getFileSystem() {
    if (fileSystem == null) throw new UnsupportedOperationException("No browser project filesystem registered");
    return fileSystem;
  }

  @Override public void reconcileProjectFiles(Iterable<IFile> files) {
    if (files != null && files.iterator().hasNext()) {
      throw new UnsupportedOperationException("Desktop VFS/VCS reconciliation is outside the browser project");
    }
  }
}
