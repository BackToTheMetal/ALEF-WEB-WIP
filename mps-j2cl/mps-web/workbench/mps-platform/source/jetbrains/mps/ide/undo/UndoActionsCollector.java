/* Copyright 2003-2024 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.ide.undo;

import com.intellij.openapi.command.undo.UndoManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jetbrains.mps.ide.project.ProjectHelper;
import jetbrains.mps.project.MPSProject;
import jetbrains.mps.project.Project;
import jetbrains.mps.smodel.ModelRenameUndoableAction;
import jetbrains.mps.smodel.SNodeUndoableAction;
import jetbrains.mps.smodel.UndoItem;
import jetbrains.mps.smodel.undo.UndoContext;

/** Browser model undo registration, without desktop virtual-file/document cache operations. */
public class UndoActionsCollector {
  private final UndoContext myUndoContext;
  private final List<UndoItem> myActions = new ArrayList<>();
  private boolean myDisposed;

  UndoActionsCollector(UndoContext undoContext) { myUndoContext = Objects.requireNonNull(undoContext); }

  void addAction(ModelRenameUndoableAction action) { add(action); }
  void addAction(SNodeUndoableAction action) { add(action); }

  private void add(UndoItem action) {
    if (myDisposed) throw new IllegalStateException("Undo collector is disposed");
    myActions.add(Objects.requireNonNull(action));
  }

  void flushAndDispose() {
    if (myDisposed) throw new IllegalStateException("Undo collector is disposed");
    myDisposed = true;
    try {
      if (myActions.isEmpty()) return;
      Project project = ProjectHelper.getProject(myUndoContext.getRepository());
      if (!(project instanceof MPSProject)) {
        throw new IllegalStateException("No browser MPSProject owns the command repository: " + project);
      }
      UndoManager manager = UndoManager.getInstance(((MPSProject) project).getProject());
      // The browser has a project-wide model history. Undo/redo executes these same
      // model operations; only IDEA's text Document and virtual-file caches are absent.
      manager.undoableActionPerformed(new SNodeIdeaUndoableAction(myActions, myUndoContext.getRepository(), true, Collections.emptyList()));
    } finally {
      myActions.clear();
    }
  }
}
