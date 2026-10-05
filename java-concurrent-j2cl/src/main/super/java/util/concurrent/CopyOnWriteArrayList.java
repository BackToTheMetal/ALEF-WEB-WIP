package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/** Snapshot iteration is preserved even though mutations all occur on one thread. */
public class CopyOnWriteArrayList<E> extends AbstractList<E>
    implements RandomAccess, Cloneable, Serializable {
  // A published list is never mutated. Iterators and views can retain its identity.
  private ArrayList<E> data;

  public CopyOnWriteArrayList() {
    data = new ArrayList<>();
  }

  public CopyOnWriteArrayList(Collection<? extends E> collection) {
    data = new ArrayList<>(Objects.requireNonNull(collection));
  }

  public CopyOnWriteArrayList(E[] array) {
    data = new ArrayList<>(Arrays.asList(Objects.requireNonNull(array)));
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public E get(int index) {
    return data.get(index);
  }

  @Override
  public E set(int index, E element) {
    E previous = data.get(index);
    if (previous != element) {
      ArrayList<E> updated = new ArrayList<>(data);
      updated.set(index, element);
      data = updated;
    }
    return previous;
  }

  @Override
  public boolean add(E element) {
    add(data.size(), element);
    return true;
  }

  @Override
  public void add(int index, E element) {
    ArrayList<E> updated = new ArrayList<>(data);
    updated.add(index, element);
    data = updated;
  }

  @Override
  public E remove(int index) {
    ArrayList<E> updated = new ArrayList<>(data);
    E previous = updated.remove(index);
    data = updated;
    return previous;
  }

  @Override
  public boolean remove(Object object) {
    int index = data.indexOf(object);
    if (index < 0) {
      return false;
    }
    remove(index);
    return true;
  }

  @Override
  protected void removeRange(int fromIndex, int toIndex) {
    checkRange(fromIndex, toIndex, data.size());
    ArrayList<E> updated = new ArrayList<>(data);
    updated.subList(fromIndex, toIndex).clear();
    data = updated;
  }

  public boolean addIfAbsent(E element) {
    if (data.contains(element)) {
      return false;
    }
    return add(element);
  }

  public int addAllAbsent(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    ArrayList<E> updated = new ArrayList<>(data);
    int added = 0;
    for (E element : collection) {
      if (!updated.contains(element)) {
        updated.add(element);
        added++;
      }
    }
    if (added != 0) {
      data = updated;
    }
    return added;
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    return addAll(size(), collection);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    ArrayList<E> updated = new ArrayList<>(data);
    boolean changed = updated.addAll(index, collection);
    if (changed) {
      data = updated;
    }
    return changed;
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    Objects.requireNonNull(collection);
    return removeIf(collection::contains);
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    Objects.requireNonNull(collection);
    return removeIf(element -> !collection.contains(element));
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    return removeMatching(filter, 0, size());
  }

  private boolean removeMatching(Predicate<? super E> filter, int fromIndex, int toIndex) {
    Objects.requireNonNull(filter);
    ArrayList<E> expected = data;
    ArrayList<E> updated = new ArrayList<>(expected.size());
    for (int index = 0; index < expected.size(); index++) {
      E element = expected.get(index);
      if (index < fromIndex || index >= toIndex || !filter.test(element)) {
        updated.add(element);
      }
    }
    if (data != expected) {
      throw new ConcurrentModificationException();
    }
    if (updated.size() == expected.size()) {
      return false;
    }
    data = updated;
    return true;
  }

  @Override
  public void replaceAll(UnaryOperator<E> operator) {
    Objects.requireNonNull(operator);
    ArrayList<E> updated = new ArrayList<>(data);
    updated.replaceAll(operator);
    data = updated;
  }

  @Override
  public void sort(Comparator<? super E> comparator) {
    ArrayList<E> updated = new ArrayList<>(data);
    updated.sort(comparator);
    data = updated;
  }

  @Override
  public void clear() {
    data = new ArrayList<>();
  }

  @Override
  public int indexOf(Object object) {
    return data.indexOf(object);
  }

  public int indexOf(E element, int index) {
    ArrayList<E> snapshot = data;
    if (index < 0) {
      throw new IndexOutOfBoundsException();
    }
    for (int current = index; current < snapshot.size(); current++) {
      if (Objects.equals(element, snapshot.get(current))) {
        return current;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object object) {
    return data.lastIndexOf(object);
  }

  public int lastIndexOf(E element, int index) {
    ArrayList<E> snapshot = data;
    if (index >= snapshot.size()) {
      throw new IndexOutOfBoundsException();
    }
    for (int current = index; current >= 0; current--) {
      if (Objects.equals(element, snapshot.get(current))) {
        return current;
      }
    }
    return -1;
  }

  public Object clone() {
    return new CopyOnWriteArrayList<>(data);
  }

  @Override
  public Object[] toArray() {
    return data.toArray();
  }

  @Override
  public <T> T[] toArray(T[] array) {
    return data.toArray(array);
  }

  @Override
  public Iterator<E> iterator() {
    return listIterator();
  }

  @Override
  public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return Collections.unmodifiableList(data).listIterator(index);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(data, Spliterator.IMMUTABLE | Spliterator.ORDERED);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    checkRange(fromIndex, toIndex, size());
    return new SnapshotSubList(fromIndex, toIndex - fromIndex);
  }

  private static void checkRange(int fromIndex, int toIndex, int size) {
    if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
      throw new IndexOutOfBoundsException();
    }
  }

  private final class SnapshotSubList extends AbstractList<E> implements RandomAccess {
    private final int offset;
    private int length;
    private ArrayList<E> expected;

    SnapshotSubList(int offset, int length) {
      this.offset = offset;
      this.length = length;
      expected = data;
    }

    private void checkUnmodified() {
      if (expected != data) {
        throw new ConcurrentModificationException();
      }
    }

    private void checkIndex(int index, boolean insertion) {
      checkUnmodified();
      if (index < 0 || (insertion ? index > length : index >= length)) {
        throw new IndexOutOfBoundsException();
      }
    }

    @Override
    public int size() {
      checkUnmodified();
      return length;
    }

    @Override
    public E get(int index) {
      checkIndex(index, false);
      return data.get(offset + index);
    }

    @Override
    public E set(int index, E element) {
      checkIndex(index, false);
      E previous = CopyOnWriteArrayList.this.set(offset + index, element);
      expected = data;
      return previous;
    }

    @Override
    public void add(int index, E element) {
      checkIndex(index, true);
      CopyOnWriteArrayList.this.add(offset + index, element);
      expected = data;
      length++;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
      return addAll(size(), collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
      checkIndex(index, true);
      int before = data.size();
      boolean changed = CopyOnWriteArrayList.this.addAll(offset + index, collection);
      length += data.size() - before;
      expected = data;
      return changed;
    }

    @Override
    public E remove(int index) {
      checkIndex(index, false);
      E previous = CopyOnWriteArrayList.this.remove(offset + index);
      expected = data;
      length--;
      return previous;
    }

    @Override
    public boolean remove(Object object) {
      int index = indexOf(object);
      if (index < 0) {
        return false;
      }
      remove(index);
      return true;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
      checkUnmodified();
      checkRange(fromIndex, toIndex, length);
      CopyOnWriteArrayList.this.removeRange(offset + fromIndex, offset + toIndex);
      length -= toIndex - fromIndex;
      expected = data;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
      Objects.requireNonNull(collection);
      return removeIf(collection::contains);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
      Objects.requireNonNull(collection);
      return removeIf(element -> !collection.contains(element));
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
      checkUnmodified();
      int before = data.size();
      boolean changed = removeMatching(filter, offset, offset + length);
      length += data.size() - before;
      expected = data;
      return changed;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
      Objects.requireNonNull(operator);
      checkUnmodified();
      ArrayList<E> updated = new ArrayList<>(data);
      updated.subList(offset, offset + length).replaceAll(operator);
      data = updated;
      expected = data;
    }

    @Override
    public void sort(Comparator<? super E> comparator) {
      checkUnmodified();
      ArrayList<E> updated = new ArrayList<>(data);
      updated.subList(offset, offset + length).sort(comparator);
      data = updated;
      expected = data;
    }

    @Override
    public Iterator<E> iterator() {
      return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
      return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
      checkIndex(index, true);
      return Collections.unmodifiableList(data.subList(offset, offset + length)).listIterator(index);
    }

    @Override
    public Spliterator<E> spliterator() {
      checkUnmodified();
      return Spliterators.spliterator(data.subList(offset, offset + length),
          Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
      checkUnmodified();
      checkRange(fromIndex, toIndex, length);
      return new SnapshotSubList(offset + fromIndex, toIndex - fromIndex);
    }
  }
}
