package com.intellij.openapi.command.undo;
public abstract class BasicUndoableAction implements UndoableAction {
  private final DocumentReference[] documents;
  public BasicUndoableAction(DocumentReference... documents) { this.documents = java.util.Arrays.copyOf(documents, documents.length); }
  @Override public DocumentReference[] getAffectedDocuments() { return java.util.Arrays.copyOf(documents, documents.length); }
  @Override public boolean isGlobal() { return false; }
}
