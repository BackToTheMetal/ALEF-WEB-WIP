package com.intellij.openapi.util;
public class InvalidDataException extends Exception {
  public InvalidDataException() {}
  public InvalidDataException(String message) { super(message); }
  public InvalidDataException(Throwable cause) { super(cause); }
}
