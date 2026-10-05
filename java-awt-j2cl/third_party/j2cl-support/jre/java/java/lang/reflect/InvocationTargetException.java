package java.lang.reflect;

/** Wraps a failure thrown by an invoked callback; does not implement method reflection. */
public class InvocationTargetException extends ReflectiveOperationException {
  private final Throwable target;

  protected InvocationTargetException() {
    this(null, null);
  }

  public InvocationTargetException(Throwable target) {
    this(target, null);
  }

  public InvocationTargetException(Throwable target, String message) {
    super(message, null);
    this.target = target;
  }

  public Throwable getTargetException() {
    return target;
  }

  @Override
  public Throwable getCause() {
    return target;
  }
}
