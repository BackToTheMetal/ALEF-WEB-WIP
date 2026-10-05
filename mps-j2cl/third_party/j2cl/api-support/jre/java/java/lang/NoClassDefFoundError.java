package java.lang;

/** A referenced class definition could not be found or initialized. */
public class NoClassDefFoundError extends LinkageError {
  public NoClassDefFoundError() {}
  public NoClassDefFoundError(String message) { super(message); }
}
