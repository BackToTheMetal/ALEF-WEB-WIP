package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

/** An insertion-ordered set with immutable iterator snapshots. */
public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements Serializable {
  private final CopyOnWriteArrayList<E> data;

  public CopyOnWriteArraySet() {
    data = new CopyOnWriteArrayList<>();
  }

  public CopyOnWriteArraySet(Collection<? extends E> collection) {
    this();
    data.addAllAbsent(collection);
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public boolean contains(Object object) {
    return data.contains(object);
  }

  @Override
  public boolean add(E element) {
    return data.addIfAbsent(element);
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    return data.addAllAbsent(collection) != 0;
  }

  @Override
  public boolean remove(Object object) {
    return data.remove(object);
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return data.removeAll(collection);
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return data.retainAll(collection);
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    return data.removeIf(filter);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public Iterator<E> iterator() {
    return data.iterator();
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(data.toArray(), Spliterator.IMMUTABLE | Spliterator.DISTINCT);
  }
}
