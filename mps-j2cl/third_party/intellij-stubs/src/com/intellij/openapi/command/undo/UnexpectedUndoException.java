package com.intellij.openapi.command.undo;
public class UnexpectedUndoException extends Exception {
  public UnexpectedUndoException(String message) { super(message); }
  public UnexpectedUndoException(Throwable cause) { super(cause); }
}
