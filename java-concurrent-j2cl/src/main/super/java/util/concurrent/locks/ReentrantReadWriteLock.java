package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** Read/write hold counters; upgrading a read lock requires a migration. */
public class ReentrantReadWriteLock implements ReadWriteLock, Serializable {
  private final ReentrantLock writer;
  private final ReadLock readLock;
  private final WriteLock writeLock;
  private int reads;

  public ReentrantReadWriteLock() {
    this(false);
  }

  public ReentrantReadWriteLock(boolean fair) {
    writer = new ReentrantLock(fair);
    readLock = new ReadLock(this);
    writeLock = new WriteLock(this);
  }

  @Override
  public ReadLock readLock() {
    return readLock;
  }

  @Override
  public WriteLock writeLock() {
    return writeLock;
  }

  public final boolean isFair() {
    return writer.isFair();
  }

  protected Thread getOwner() {
    return writer.getOwner();
  }

  public int getReadLockCount() {
    return reads;
  }

  public int getReadHoldCount() {
    return reads;
  }

  public boolean isWriteLocked() {
    return writer.isLocked();
  }

  public boolean isWriteLockedByCurrentThread() {
    return writer.isHeldByCurrentThread();
  }

  public int getWriteHoldCount() {
    return writer.getHoldCount();
  }

  protected Collection<Thread> getQueuedWriterThreads() {
    return Collections.emptyList();
  }

  protected Collection<Thread> getQueuedReaderThreads() {
    return Collections.emptyList();
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
    return writer.hasWaiters(condition);
  }

  public int getWaitQueueLength(Condition condition) {
    return writer.getWaitQueueLength(condition);
  }

  protected Collection<Thread> getWaitingThreads(Condition condition) {
    return writer.getWaitingThreads(condition);
  }

  @Override
  public String toString() {
    return super.toString() + "[Write locks = " + getWriteHoldCount()
        + ", Read locks = " + reads + "]";
  }

  public static class ReadLock implements Lock, Serializable {
    private final ReentrantReadWriteLock owner;

    protected ReadLock(ReentrantReadWriteLock owner) {
      this.owner = Objects.requireNonNull(owner);
    }

    @Override
    public void lock() {
      if (owner.reads == 65535) {
        throw new Error("Maximum read lock count exceeded");
      }
      owner.reads++;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
      ReentrantLock.checkInterrupted();
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
      if (owner.reads == 0) {
        throw new IllegalMonitorStateException("Current thread does not hold a read lock");
      }
      owner.reads--;
    }

    @Override
    public Condition newCondition() {
      throw new UnsupportedOperationException("Read locks do not support conditions");
    }

    @Override
    public String toString() {
      return super.toString() + "[Read locks = " + owner.reads + "]";
    }
  }

  public static class WriteLock implements Lock, Serializable {
    private final ReentrantReadWriteLock owner;

    protected WriteLock(ReentrantReadWriteLock owner) {
      this.owner = Objects.requireNonNull(owner);
    }

    @Override
    public void lock() {
      if (!tryLock()) {
        throw new IllegalStateException("A read lock cannot be upgraded on the browser main thread");
      }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
      ReentrantLock.checkInterrupted();
      lock();
    }

    @Override
    public boolean tryLock() {
      if (owner.reads != 0 && !owner.writer.isHeldByCurrentThread()) {
        return false;
      }
      if (owner.writer.getHoldCount() == 65535) {
        throw new Error("Maximum write lock count exceeded");
      }
      return owner.writer.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
      Objects.requireNonNull(unit);
      ReentrantLock.checkInterrupted();
      return tryLock();
    }

    @Override
    public void unlock() {
      owner.writer.unlock();
    }

    @Override
    public Condition newCondition() {
      return owner.writer.newCondition();
    }

    public boolean isHeldByCurrentThread() {
      return owner.writer.isHeldByCurrentThread();
    }

    public int getHoldCount() {
      return owner.writer.getHoldCount();
    }

    @Override
    public String toString() {
      return owner.writer.toString();
    }
  }
}
