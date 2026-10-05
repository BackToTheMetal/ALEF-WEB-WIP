package java.lang;

/** Minimal hierarchy prerequisite for class-loading-related checked exceptions. */
public class ReflectiveOperationException extends Exception {
  public ReflectiveOperationException() {}

  public ReflectiveOperationException(String message) {
    super(message);
  }

  public ReflectiveOperationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectiveOperationException(Throwable cause) {
    super(cause);
  }
}
