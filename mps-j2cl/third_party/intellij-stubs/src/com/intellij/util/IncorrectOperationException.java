package com.intellij.util;

public class IncorrectOperationException extends RuntimeException {
  public IncorrectOperationException() {}
  public IncorrectOperationException(String message) { super(message); }
  public IncorrectOperationException(String message, Throwable cause) { super(message, cause); }
  public IncorrectOperationException(Throwable cause) { super(cause); }
}
