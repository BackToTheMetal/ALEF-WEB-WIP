package java.lang;

import java.util.Objects;

/** Main-thread source compatibility. start() invokes run() inline exactly once. */
public class Thread implements Runnable {
  public static final int MIN_PRIORITY = 1;
  public static final int NORM_PRIORITY = 5;
  public static final int MAX_PRIORITY = 10;
  private static long nextId;
  private static final ThreadGroup MAIN_GROUP = new ThreadGroup("main");
  private static final Thread MAIN = new Thread("browser-main");
  private static UncaughtExceptionHandler defaultHandler;
  private final long id = ++nextId;
  private Runnable target;
  private String name;
  private ThreadGroup group;
  private boolean started;
  private boolean alive;
  private boolean interrupted;
  private boolean daemon;
  private int priority = NORM_PRIORITY;
  private UncaughtExceptionHandler handler;
  public Thread() { this((Runnable) null); }
  public Thread(Runnable target) { this(null, target, "main-task-" + (nextId + 1)); }
  public Thread(String name) { this(null, null, name); }
  public Thread(Runnable target, String name) { this(null, target, name); }
  public Thread(ThreadGroup group, Runnable target) { this(group, target, "main-task-" + (nextId + 1)); }
  public Thread(ThreadGroup group, String name) { this(group, null, name); }
  public Thread(ThreadGroup group, Runnable target, String name) {
    this.group = group == null ? MAIN_GROUP : group; this.target = target; this.name = Objects.requireNonNull(name);
  }
  public Thread(ThreadGroup group, Runnable target, String name, long stackSize) { this(group, target, name); }
  @Override public void run() { if (target != null) target.run(); }
  @SuppressWarnings("DoNotCall") // This profile intentionally executes Thread.run on the main thread.
  public void start() {
    if (started || this == MAIN) throw new IllegalThreadStateException("Thread already started");
    started = true; alive = true;
    try { run(); }
    catch (RuntimeException | Error failure) {
      UncaughtExceptionHandler selected = getUncaughtExceptionHandler();
      if (selected == null) throw failure;
      selected.uncaughtException(this, failure);
    } finally { alive = false; target = null; }
  }
  public static Thread currentThread() { return MAIN; }
  public static boolean interrupted() { boolean old = MAIN.interrupted; MAIN.interrupted = false; return old; }
  public void interrupt() { interrupted = true; }
  public boolean isInterrupted() { return interrupted; }
  public boolean isAlive() { return this == MAIN || alive; }
  public long getId() { return id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = Objects.requireNonNull(name); }
  public int getPriority() { return priority; }
  public void setPriority(int value) {
    if (value < MIN_PRIORITY || value > MAX_PRIORITY) throw new IllegalArgumentException(); priority = value;
  }
  public void setDaemon(boolean daemon) {
    if (isAlive()) throw new IllegalThreadStateException(); this.daemon = daemon;
  }
  public boolean isDaemon() { return daemon; }
  public ThreadGroup getThreadGroup() { return group; }
  public void join() throws InterruptedException {
    if (Thread.interrupted()) throw new InterruptedException();
    if (isAlive()) throw new IllegalStateException("Cannot join running work on the browser main thread");
  }
  public void join(long millis) throws InterruptedException {
    if (millis < 0) throw new IllegalArgumentException(); join();
  }
  public void join(long millis, int nanos) throws InterruptedException {
    if (nanos < 0 || nanos > 999999) throw new IllegalArgumentException(); join(millis);
  }
  public static void yield() {}
  public static void sleep(long millis) throws InterruptedException { sleep(millis, 0); }
  public static void sleep(long millis, int nanos) throws InterruptedException {
    if (millis < 0 || nanos < 0 || nanos > 999999) throw new IllegalArgumentException();
    if (Thread.interrupted()) throw new InterruptedException();
    if (millis > 0 || nanos > 0) throw new IllegalStateException("Use ScheduledExecutorService instead of blocking Thread.sleep");
  }
  public enum State { NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED }
  public State getState() { return isAlive() ? State.RUNNABLE : started ? State.TERMINATED : State.NEW; }
  public interface UncaughtExceptionHandler { void uncaughtException(Thread thread, Throwable failure); }
  public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) { this.handler = handler; }
  public UncaughtExceptionHandler getUncaughtExceptionHandler() { return handler != null ? handler : defaultHandler; }
  public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler handler) { defaultHandler = handler; }
  public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() { return defaultHandler; }
  @Override public String toString() { return "Thread[" + name + "," + priority + "]"; }
}
