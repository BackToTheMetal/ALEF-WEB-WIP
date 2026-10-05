package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/** Blocking queue signatures with synchronous operations in this profile. */
public interface BlockingQueue<E> extends Queue<E> {
  void put(E element) throws InterruptedException;
  boolean offer(E element, long timeout, TimeUnit unit) throws InterruptedException;
  E take() throws InterruptedException;
  E poll(long timeout, TimeUnit unit) throws InterruptedException;
  int remainingCapacity();
  int drainTo(Collection<? super E> collection);
  int drainTo(Collection<? super E> collection, int maxElements);
}
