package java.lang;

/** A requested method name or signature is absent from an explicit method registry. */
public class NoSuchMethodException extends ReflectiveOperationException {
  public NoSuchMethodException() {
  }

  public NoSuchMethodException(String message) {
    super(message);
  }
}
