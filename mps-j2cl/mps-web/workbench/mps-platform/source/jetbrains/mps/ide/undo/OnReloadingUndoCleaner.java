/* Copyright 2003-2024 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.ide.undo;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.project.Project;
import jetbrains.mps.ide.project.ProjectHelper;
import jetbrains.mps.project.MPSProject;
import jetbrains.mps.smodel.RepoListenerRegistrar;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.module.SModule;
import org.jetbrains.mps.openapi.module.SRepository;
import org.jetbrains.mps.openapi.module.SRepositoryContentAdapter;

/** Invalidates project model history synchronously before old model objects can be replayed. */
public final class OnReloadingUndoCleaner implements Disposable {
  private final SRepository myRepository;
  private RepoListenerRegistrar myListenerRegistrar;

  public static OnReloadingUndoCleaner getInstance(Project project) {
    return project.getService(OnReloadingUndoCleaner.class);
  }

  public OnReloadingUndoCleaner(Project project) {
    this(ProjectHelper.fromIdeaProjectOrFail(project).getRepository());
  }

  OnReloadingUndoCleaner(SRepository repository) {
    myRepository = repository;
    myListenerRegistrar = new RepoListenerRegistrar(repository, new SRepositoryContentAdapter() {
      @Override protected boolean isIncluded(SModule module) { return !module.isReadOnly(); }
      @Override protected void startListening(SModel model) {
        if (!model.isReadOnly()) model.addModelListener(this);
      }
      @Override protected void stopListening(SModel model) {
        if (!model.isReadOnly()) {
          model.removeModelListener(this);
          clearUndoStack();
        }
      }
      @Override public void modelReplaced(SModel model) { clearUndoStack(); }
    });
    myListenerRegistrar.attach();
  }

  private void clearUndoStack() {
    if (myListenerRegistrar == null) return;
    jetbrains.mps.project.Project project = ProjectHelper.getProject(myRepository);
    if (project instanceof MPSProject) {
      // Conservative browser policy: there are no per-text-document stacks.
      // A model replacement/removal invalidates the whole project's history.
      UndoManager.getInstance(((MPSProject) project).getProject()).clearHistory();
    }
  }

  @Override public void dispose() {
    if (myListenerRegistrar == null) return;
    clearUndoStack();
    RepoListenerRegistrar registrar = myListenerRegistrar;
    myListenerRegistrar = null;
    registrar.detach();
  }
}
