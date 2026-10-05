package java.lang;

/** Signals an unbalanced lock/condition operation in the main-thread profile. */
public class IllegalMonitorStateException extends RuntimeException {
  public IllegalMonitorStateException() {}
  public IllegalMonitorStateException(String message) { super(message); }
}
