package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Priority-ordered delayed elements using only Java state. Expiry is read from each
 * element's getDelay method; this queue neither advances a clock nor schedules a callback.
 */
public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {
  private final PriorityQueue<E> data = new PriorityQueue<>();

  public DelayQueue() {}

  public DelayQueue(Collection<? extends E> collection) {
    addAll(collection);
  }

  @Override
  public boolean offer(E element) {
    return data.offer(Objects.requireNonNull(element));
  }

  /** An unbounded insertion executes immediately and does not consume interruption. */
  @Override
  public void put(E element) {
    offer(element);
  }

  /** Capacity never requires a wait; the timeout has no scheduling effect. */
  @Override
  public boolean offer(E element, long timeout, TimeUnit unit) {
    Objects.requireNonNull(unit);
    return offer(element);
  }

  /** Returns and removes the first element only when its delay is nonpositive. */
  @Override
  public E poll() {
    E first = data.peek();
    return first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0 ? null : data.poll();
  }

  /** An empty queue or an unexpired head cannot suspend the browser main thread. */
  @Override
  public E take() throws InterruptedException {
    checkInterrupted();
    E element = poll();
    if (element == null) {
      throw new IllegalStateException("DelayQueue.take cannot block the browser main thread");
    }
    return element;
  }

  /** A failed timed wait returns null immediately, without removing unexpired data. */
  @Override
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(unit);
    checkInterrupted();
    return poll();
  }

  /** Peeking exposes the first element even when it has not expired. */
  @Override
  public E peek() {
    return data.peek();
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public int remainingCapacity() {
    return Integer.MAX_VALUE;
  }

  @Override
  public int drainTo(Collection<? super E> collection) {
    return drainTo(collection, Integer.MAX_VALUE);
  }

  /** Transfers only expired elements, preserving each item if destination.add throws. */
  @Override
  public int drainTo(Collection<? super E> collection, int maxElements) {
    Objects.requireNonNull(collection);
    if (collection == this) {
      throw new IllegalArgumentException("Cannot drain a queue to itself");
    }
    int transferred = 0;
    while (transferred < maxElements) {
      E first = data.peek();
      if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
        break;
      }
      collection.add(first);
      data.poll();
      transferred++;
    }
    return transferred;
  }

  @Override
  public boolean remove(Object object) {
    return data.remove(object);
  }

  @Override
  public boolean contains(Object object) {
    return data.contains(object);
  }

  @Override
  public void clear() {
    data.clear();
  }

  /** Snapshot traversal is unordered; iterator removal targets the returned object identity. */
  @Override
  public Iterator<E> iterator() {
    Iterator<E> snapshot = new ArrayList<>(data).iterator();
    return new Iterator<E>() {
      private E returned;

      @Override
      public boolean hasNext() {
        return snapshot.hasNext();
      }

      @Override
      public E next() {
        returned = snapshot.next();
        return returned;
      }

      @Override
      public void remove() {
        if (returned == null) {
          throw new IllegalStateException();
        }
        for (Iterator<E> live = data.iterator(); live.hasNext(); ) {
          if (live.next() == returned) {
            live.remove();
            break;
          }
        }
        returned = null;
      }
    };
  }

  private static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }
}
