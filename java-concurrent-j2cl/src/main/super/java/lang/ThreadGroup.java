package java.lang;

/** Naming metadata only; there are no browser worker groups. */
public class ThreadGroup implements Thread.UncaughtExceptionHandler {
  private final String name;
  private final ThreadGroup parent;
  public ThreadGroup(String name) { this(null, name); }
  public ThreadGroup(ThreadGroup parent, String name) { this.parent = parent; this.name = name; }
  public String getName() { return name; }
  public ThreadGroup getParent() { return parent; }
  public int activeCount() { return 0; }
  public void uncaughtException(Thread thread, Throwable failure) {
    if (parent != null) parent.uncaughtException(thread, failure);
    else if (Thread.getDefaultUncaughtExceptionHandler() != null) Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, failure);
    else throw new RuntimeException(failure);
  }
}
