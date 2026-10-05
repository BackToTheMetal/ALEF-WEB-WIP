package java.lang;

/** Signature prerequisite for ObjectInputStream in the J2CL browser profile. */
public class ClassNotFoundException extends ReflectiveOperationException {
  public ClassNotFoundException() {}

  public ClassNotFoundException(String message) {
    super(message);
  }

  public ClassNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClassNotFoundException(Throwable cause) {
    super(cause);
  }
}
