package com.intellij.openapi.project;

public class IndexNotReadyException extends RuntimeException {
  public static IndexNotReadyException create() { return new IndexNotReadyException(); }
}
