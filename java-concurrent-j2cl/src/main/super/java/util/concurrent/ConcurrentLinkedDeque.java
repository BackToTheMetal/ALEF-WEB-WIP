package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;

/** A main-thread deque with removable, weakly consistent iterators. */
public class ConcurrentLinkedDeque<E> extends AbstractCollection<E>
    implements Deque<E>, Serializable {
  private Node<E> first;
  private Node<E> last;
  private int size;

  private static final class Node<E> {
    final E element;
    Node<E> previous;
    Node<E> next;
    boolean removed;

    Node(E element) {
      this.element = Objects.requireNonNull(element);
    }
  }

  public ConcurrentLinkedDeque() {}

  public ConcurrentLinkedDeque(Collection<? extends E> collection) {
    addAll(collection);
  }

  @Override
  public void addFirst(E element) {
    Node<E> node = new Node<>(element);
    node.next = first;
    if (first == null) {
      last = node;
    } else {
      first.previous = node;
    }
    first = node;
    size++;
  }

  @Override
  public void addLast(E element) {
    Node<E> node = new Node<>(element);
    node.previous = last;
    if (last == null) {
      first = node;
    } else {
      last.next = node;
    }
    last = node;
    size++;
  }

  @Override
  public boolean offerFirst(E element) {
    addFirst(element);
    return true;
  }

  @Override
  public boolean offerLast(E element) {
    addLast(element);
    return true;
  }

  @Override
  public E peekFirst() {
    return first == null ? null : first.element;
  }

  @Override
  public E peekLast() {
    return last == null ? null : last.element;
  }

  @Override
  public E getFirst() {
    return requireElement(peekFirst());
  }

  @Override
  public E getLast() {
    return requireElement(peekLast());
  }

  @Override
  public E pollFirst() {
    return first == null ? null : unlink(first);
  }

  @Override
  public E pollLast() {
    return last == null ? null : unlink(last);
  }

  @Override
  public E removeFirst() {
    return requireElement(pollFirst());
  }

  @Override
  public E removeLast() {
    return requireElement(pollLast());
  }

  @Override
  public boolean offer(E element) {
    return offerLast(element);
  }

  @Override
  public boolean add(E element) {
    return offerLast(element);
  }

  @Override
  public E poll() {
    return pollFirst();
  }

  @Override
  public E peek() {
    return peekFirst();
  }

  @Override
  public E remove() {
    return removeFirst();
  }

  @Override
  public E pop() {
    return removeFirst();
  }

  @Override
  public E element() {
    return getFirst();
  }

  @Override
  public void push(E element) {
    addFirst(element);
  }

  @Override
  public boolean removeFirstOccurrence(Object object) {
    if (object == null) {
      return false;
    }
    for (Node<E> node = first; node != null; node = node.next) {
      if (object.equals(node.element)) {
        unlink(node);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean removeLastOccurrence(Object object) {
    if (object == null) {
      return false;
    }
    for (Node<E> node = last; node != null; node = node.previous) {
      if (object.equals(node.element)) {
        unlink(node);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean remove(Object object) {
    return removeFirstOccurrence(object);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    if (collection == this) {
      throw new IllegalArgumentException("Cannot add a deque to itself");
    }
    // Validate before linking, as the JDK concurrent deque does.
    java.util.ArrayList<E> snapshot = new java.util.ArrayList<>(collection);
    for (E element : snapshot) {
      Objects.requireNonNull(element);
    }
    for (E element : snapshot) {
      addLast(element);
    }
    return !snapshot.isEmpty();
  }

  @Override
  public void clear() {
    while (first != null) {
      unlink(first);
    }
  }

  private E unlink(Node<E> node) {
    if (!node.removed) {
      if (node.previous == null) {
        first = node.next;
      } else {
        node.previous.next = node.next;
      }
      if (node.next == null) {
        last = node.previous;
      } else {
        node.next.previous = node.previous;
      }
      node.removed = true;
      size--;
    }
    // Retain links so an existing iterator can continue after removal.
    return node.element;
  }

  private E requireElement(E element) {
    if (element == null) {
      throw new NoSuchElementException();
    }
    return element;
  }

  @Override
  public Iterator<E> iterator() {
    return new DequeIterator(false);
  }

  @Override
  public Iterator<E> descendingIterator() {
    return new DequeIterator(true);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliteratorUnknownSize(iterator(),
        Spliterator.CONCURRENT | Spliterator.NONNULL | Spliterator.ORDERED);
  }

  private final class DequeIterator implements Iterator<E> {
    private final boolean descending;
    private Node<E> next;
    private Node<E> returned;

    DequeIterator(boolean descending) {
      this.descending = descending;
      next = descending ? last : first;
    }

    private void skipRemoved() {
      while (next != null && next.removed) {
        next = descending ? next.previous : next.next;
      }
    }

    @Override
    public boolean hasNext() {
      skipRemoved();
      return next != null;
    }

    @Override
    public E next() {
      skipRemoved();
      if (next == null) {
        throw new NoSuchElementException();
      }
      returned = next;
      next = descending ? next.previous : next.next;
      return returned.element;
    }

    @Override
    public void remove() {
      if (returned == null) {
        throw new IllegalStateException();
      }
      unlink(returned);
      returned = null;
    }
  }
}
