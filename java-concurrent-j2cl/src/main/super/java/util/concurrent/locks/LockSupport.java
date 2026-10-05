package java.util.concurrent.locks;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/** Permit bookkeeping without suspending the browser event loop. */
public class LockSupport {
  private static final Map<Thread, Boolean> permits = new IdentityHashMap<>();
  private static Object currentBlocker;

  private LockSupport() {}

  public static void setCurrentBlocker(Object blocker) {
    currentBlocker = blocker;
  }

  public static void unpark(Thread thread) {
    if (thread != null) {
      permits.put(thread, Boolean.TRUE);
    }
  }

  public static void park(Object blocker) {
    if (!consumePermit() && !Thread.currentThread().isInterrupted()) {
      throw new IllegalStateException("LockSupport.park cannot block the browser main thread");
    }
  }

  public static void park() {
    park(null);
  }

  public static void parkNanos(Object blocker, long nanos) {
    if (nanos > 0) {
      consumePermit();
    }
  }

  public static void parkNanos(long nanos) {
    parkNanos(null, nanos);
  }

  public static void parkUntil(Object blocker, long deadline) {
    consumePermit();
  }

  public static void parkUntil(long deadline) {
    parkUntil(null, deadline);
  }

  public static Object getBlocker(Thread thread) {
    Objects.requireNonNull(thread);
    return thread == Thread.currentThread() ? currentBlocker : null;
  }

  private static boolean consumePermit() {
    return permits.remove(Thread.currentThread()) != null;
  }
}
