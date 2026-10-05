package java.util.concurrent;

import java.util.Collection;
import java.util.Objects;

/**
 * Ordinary FIFO operations run synchronously. Transfer operations cannot rendezvous
 * with a blocked consumer on the one browser main thread and do not enqueue an item.
 */
public class LinkedTransferQueue<E> extends MainThreadBlockingQueue<E> implements TransferQueue<E> {
  public LinkedTransferQueue() {
    super(Integer.MAX_VALUE);
  }

  public LinkedTransferQueue(Collection<? extends E> collection) {
    this();
    addAll(collection);
  }

  /** Unbounded insertion never waits and preserves the current interrupt status. */
  @Override
  public void put(E element) {
    add(element);
  }

  @Override
  public boolean offer(E element, long timeout, TimeUnit unit) {
    Objects.requireNonNull(unit);
    return offer(element);
  }

  /** There are no waiting consumers; failure leaves the queue unchanged. */
  @Override
  public boolean tryTransfer(E element) {
    Objects.requireNonNull(element);
    return false;
  }

  /** Reports the unsupported rendezvous instead of falsely completing a transfer. */
  @Override
  public void transfer(E element) throws InterruptedException {
    Objects.requireNonNull(element);
    checkInterrupted();
    throw new IllegalStateException("LinkedTransferQueue.transfer cannot rendezvous on the browser main thread");
  }

  /** A timed transfer fails immediately and leaves the queue unchanged. */
  @Override
  public boolean tryTransfer(E element, long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(element);
    Objects.requireNonNull(unit);
    checkInterrupted();
    return false;
  }

  @Override
  public boolean hasWaitingConsumer() {
    return false;
  }

  @Override
  public int getWaitingConsumerCount() {
    return 0;
  }

  @Override
  public int remainingCapacity() {
    return Integer.MAX_VALUE;
  }

  private static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }
}
