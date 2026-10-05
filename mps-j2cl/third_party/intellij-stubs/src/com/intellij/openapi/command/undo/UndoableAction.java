package com.intellij.openapi.command.undo;
public interface UndoableAction {
  void undo() throws UnexpectedUndoException;
  void redo() throws UnexpectedUndoException;
  DocumentReference[] getAffectedDocuments();
  boolean isGlobal();
}
