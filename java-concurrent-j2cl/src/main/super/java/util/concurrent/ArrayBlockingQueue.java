package java.util.concurrent;

import java.util.Collection;
import java.util.Objects;

/** Fixed-capacity FIFO contract; fairness has no scheduling effect on one thread. */
public class ArrayBlockingQueue<E> extends MainThreadBlockingQueue<E> {
  public ArrayBlockingQueue(int capacity) {
    super(capacity);
  }

  public ArrayBlockingQueue(int capacity, boolean fair) {
    this(capacity);
  }

  public ArrayBlockingQueue(int capacity, boolean fair, Collection<? extends E> collection) {
    this(capacity, fair);
    Objects.requireNonNull(collection);
    if (collection.size() > capacity) {
      throw new IllegalArgumentException("Collection exceeds queue capacity");
    }
    addAll(collection);
  }
}
