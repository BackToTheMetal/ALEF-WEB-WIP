package java.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/** Plain permit accounting; an unsatisfied blocking acquire fails immediately. */
public class Semaphore implements Serializable {
  private int permits;
  private final boolean fair;

  public Semaphore(int permits) {
    this(permits, false);
  }

  public Semaphore(int permits, boolean fair) {
    this.permits = permits;
    this.fair = fair;
  }

  public void acquire() throws InterruptedException {
    acquire(1);
  }

  public void acquire(int permits) throws InterruptedException {
    checkNonnegative(permits);
    checkInterrupted();
    acquireUninterruptibly(permits);
  }

  public void acquireUninterruptibly() {
    acquireUninterruptibly(1);
  }

  public void acquireUninterruptibly(int permits) {
    if (!tryAcquire(permits)) {
      throw new IllegalStateException("Semaphore.acquire cannot block the browser main thread");
    }
  }

  public boolean tryAcquire() {
    return tryAcquire(1);
  }

  public boolean tryAcquire(int permits) {
    checkNonnegative(permits);
    if (this.permits < permits) {
      return false;
    }
    this.permits -= permits;
    return true;
  }

  public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
    return tryAcquire(1, timeout, unit);
  }

  public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(unit);
    checkNonnegative(permits);
    checkInterrupted();
    return tryAcquire(permits);
  }

  public void release() {
    release(1);
  }

  public void release(int permits) {
    checkNonnegative(permits);
    long updated = (long) this.permits + permits;
    if (updated > Integer.MAX_VALUE) {
      throw new Error("Maximum permit count exceeded");
    }
    this.permits = (int) updated;
  }

  public int availablePermits() {
    return permits;
  }

  public int drainPermits() {
    int previous = permits;
    permits = 0;
    return previous;
  }

  protected void reducePermits(int reduction) {
    checkNonnegative(reduction);
    long updated = (long) permits - reduction;
    if (updated < Integer.MIN_VALUE) {
      throw new Error("Permit count underflow");
    }
    permits = (int) updated;
  }

  public boolean isFair() {
    return fair;
  }

  public final boolean hasQueuedThreads() {
    return false;
  }

  public final int getQueueLength() {
    return 0;
  }

  protected Collection<Thread> getQueuedThreads() {
    return Collections.emptyList();
  }

  private static void checkNonnegative(int value) {
    if (value < 0) {
      throw new IllegalArgumentException("permits < 0");
    }
  }

  private static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  @Override
  public String toString() {
    return super.toString() + "[Permits = " + permits + "]";
  }
}
