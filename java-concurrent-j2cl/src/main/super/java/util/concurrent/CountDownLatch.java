package java.util.concurrent;

import java.util.Objects;

/** A countdown whose already-completed waits succeed synchronously. */
public class CountDownLatch {
  private int count;

  public CountDownLatch(int count) {
    if (count < 0) {
      throw new IllegalArgumentException("count < 0");
    }
    this.count = count;
  }

  public void await() throws InterruptedException {
    checkInterrupted();
    if (count != 0) {
      throw new IllegalStateException("CountDownLatch.await cannot block the browser main thread");
    }
  }

  public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(unit);
    checkInterrupted();
    return count == 0;
  }

  public void countDown() {
    if (count != 0) {
      count--;
    }
  }

  public long getCount() {
    return count;
  }

  private static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  @Override
  public String toString() {
    return super.toString() + "[Count = " + count + "]";
  }
}
