package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;

/** FIFO data semantics without native concurrency. */
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Serializable {
  private final ConcurrentLinkedDeque<E> data = new ConcurrentLinkedDeque<>();

  public ConcurrentLinkedQueue() {}

  public ConcurrentLinkedQueue(Collection<? extends E> collection) {
    data.addAll(collection);
  }

  @Override
  public boolean offer(E element) {
    return data.offerLast(element);
  }

  @Override
  public E poll() {
    return data.pollFirst();
  }

  @Override
  public E peek() {
    return data.peekFirst();
  }

  @Override
  public int size() {
    return data.size();
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
    return data.removeFirstOccurrence(object);
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    if (collection == this) {
      throw new IllegalArgumentException("Cannot add a queue to itself");
    }
    return data.addAll(collection);
  }

  @Override
  public void clear() {
    data.clear();
  }
}
