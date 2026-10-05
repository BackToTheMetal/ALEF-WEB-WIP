package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** A reentrant ownership counter for the one browser main thread. */
public class ReentrantLock implements Lock, Serializable {
  private final boolean fair;
  private int holds;

  public ReentrantLock() {
    this(false);
  }

  public ReentrantLock(boolean fair) {
    this.fair = fair;
  }

  @Override
  public void lock() {
    if (holds == Integer.MAX_VALUE) {
      throw new Error("Maximum lock count exceeded");
    }
    holds++;
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    checkInterrupted();
    lock();
  }

  @Override
  public boolean tryLock() {
    lock();
    return true;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(unit);
    lockInterruptibly();
    return true;
  }

  @Override
  public void unlock() {
    checkHeld();
    holds--;
  }

  @Override
  public Condition newCondition() {
    return new MainThreadCondition(this);
  }

  public int getHoldCount() {
    return holds;
  }

  public boolean isHeldByCurrentThread() {
    return holds != 0;
  }

  public boolean isLocked() {
    return holds != 0;
  }

  public final boolean isFair() {
    return fair;
  }

  protected Thread getOwner() {
    return holds == 0 ? null : Thread.currentThread();
  }

  public final boolean hasQueuedThreads() {
    return false;
  }

  public final boolean hasQueuedThread(Thread thread) {
    Objects.requireNonNull(thread);
    return false;
  }

  public final int getQueueLength() {
    return 0;
  }

  protected Collection<Thread> getQueuedThreads() {
    return Collections.emptyList();
  }

  public boolean hasWaiters(Condition condition) {
    checkCondition(condition);
    return false;
  }

  public int getWaitQueueLength(Condition condition) {
    checkCondition(condition);
    return 0;
  }

  protected Collection<Thread> getWaitingThreads(Condition condition) {
    checkCondition(condition);
    return Collections.emptyList();
  }

  private void checkCondition(Condition condition) {
    Objects.requireNonNull(condition);
    if (!(condition instanceof MainThreadCondition)
        || ((MainThreadCondition) condition).owner != this) {
      throw new IllegalArgumentException("Condition belongs to a different lock");
    }
    checkHeld();
  }

  private void checkHeld() {
    if (holds == 0) {
      throw new IllegalMonitorStateException("Current thread does not hold this lock");
    }
  }

  static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  @Override
  public String toString() {
    return super.toString() + (holds == 0 ? "[Unlocked]" : "[Locked by main thread]");
  }

  private static final class MainThreadCondition implements Condition {
    private final ReentrantLock owner;

    MainThreadCondition(ReentrantLock owner) {
      this.owner = owner;
    }

    @Override
    public void await() throws InterruptedException {
      checkInterrupted();
      awaitUninterruptibly();
    }

    @Override
    public void awaitUninterruptibly() {
      owner.checkHeld();
      throw new IllegalStateException("Condition.await cannot block the browser main thread");
    }

    @Override
    public long awaitNanos(long nanosTimeout) throws InterruptedException {
      checkInterrupted();
      owner.checkHeld();
      return Math.min(0L, nanosTimeout);
    }

    @Override
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
      Objects.requireNonNull(unit);
      checkInterrupted();
      owner.checkHeld();
      return false;
    }

    @Override
    public boolean awaitUntil(Date deadline) throws InterruptedException {
      Objects.requireNonNull(deadline);
      checkInterrupted();
      owner.checkHeld();
      return false;
    }

    @Override
    public void signal() {
      owner.checkHeld();
    }

    @Override
    public void signalAll() {
      owner.checkHeld();
    }
  }
}
