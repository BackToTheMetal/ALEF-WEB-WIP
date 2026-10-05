package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;

/** Shared bounded FIFO implementation; storage capacity is enforced, never waited for. */
abstract class MainThreadBlockingQueue<E> extends AbstractQueue<E>
    implements BlockingQueue<E>, Serializable {
  private final int capacity;
  private final ConcurrentLinkedQueue<E> data = new ConcurrentLinkedQueue<>();

  MainThreadBlockingQueue(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be positive");
    }
    this.capacity = capacity;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public int remainingCapacity() {
    return capacity - size();
  }

  @Override
  public boolean offer(E element) {
    Objects.requireNonNull(element);
    return size() < capacity && data.offer(element);
  }

  @Override
  public void put(E element) throws InterruptedException {
    Objects.requireNonNull(element);
    checkInterrupted();
    if (!offer(element)) {
      throw new IllegalStateException("BlockingQueue.put cannot block the browser main thread");
    }
  }

  @Override
  public boolean offer(E element, long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(element);
    Objects.requireNonNull(unit);
    checkInterrupted();
    return offer(element);
  }

  @Override
  public E poll() {
    return data.poll();
  }

  @Override
  public E take() throws InterruptedException {
    checkInterrupted();
    E element = poll();
    if (element == null) {
      throw new IllegalStateException("BlockingQueue.take cannot block the browser main thread");
    }
    return element;
  }

  @Override
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(unit);
    checkInterrupted();
    return poll();
  }

  @Override
  public E peek() {
    return data.peek();
  }

  @Override
  public Iterator<E> iterator() {
    return data.iterator();
  }

  @Override
  public Spliterator<E> spliterator() {
    return data.spliterator();
  }

  @Override
  public boolean remove(Object object) {
    return data.remove(object);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public int drainTo(Collection<? super E> collection) {
    return drainTo(collection, Integer.MAX_VALUE);
  }

  @Override
  public int drainTo(Collection<? super E> collection, int maxElements) {
    Objects.requireNonNull(collection);
    if (collection == this) {
      throw new IllegalArgumentException("Cannot drain a queue to itself");
    }
    int limit = Math.min(size(), Math.max(0, maxElements));
    int transferred = 0;
    while (transferred < limit) {
      E element = data.peek();
      if (element == null) {
        break;
      }
      // Keep the source element when the destination rejects by throwing.
      collection.add(element);
      data.poll();
      transferred++;
    }
    return transferred;
  }

  private static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }
}
