package com.intellij.openapi.progress;

public class ProcessCanceledException extends RuntimeException {
  public ProcessCanceledException() {}
  public ProcessCanceledException(Throwable cause) { super(cause); }
}
