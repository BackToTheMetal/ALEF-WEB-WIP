package java.util.concurrent;

import java.util.Collection;

/** Linked FIFO storage with optional capacity and synchronous wait boundaries. */
public class LinkedBlockingQueue<E> extends MainThreadBlockingQueue<E> {
  public LinkedBlockingQueue() {
    super(Integer.MAX_VALUE);
  }

  public LinkedBlockingQueue(int capacity) {
    super(capacity);
  }

  public LinkedBlockingQueue(Collection<? extends E> collection) {
    this();
    addAll(collection);
  }
}
