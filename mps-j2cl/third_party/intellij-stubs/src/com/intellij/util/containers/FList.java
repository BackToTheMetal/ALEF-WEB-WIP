package com.intellij.util.containers;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/** Immutable persistent list used to return completion highlight ranges. */
public final class FList<E> extends AbstractList<E> {
  private static final FList<?> EMPTY = new FList<>(null, null, 0);
  private final E head;
  private final FList<E> tail;
  private final int size;
  private FList(E head, FList<E> tail, int size) { this.head = head; this.tail = tail; this.size = size; }
  @SuppressWarnings("unchecked") public static <E> FList<E> emptyList() { return (FList<E>) EMPTY; }
  public static <E> FList<E> singleton(E value) { return FList.<E>emptyList().prepend(value); }
  public static <E> FList<E> createFromReversed(Iterable<? extends E> values) {
    FList<E> result = emptyList();
    for (E value : values) result = result.prepend(value);
    return result;
  }
  public E getHead() { return head; }
  public FList<E> getTail() { return tail; }
  public FList<E> prepend(E value) { return new FList<>(value, this, size + 1); }
  public FList<E> without(E value) {
    List<E> kept = new ArrayList<>();
    boolean removed = false;
    for (E item : this) {
      if (!removed && Objects.equals(item, value)) removed = true; else kept.add(item);
    }
    if (!removed) return this;
    FList<E> result = emptyList();
    for (int i = kept.size() - 1; i >= 0; i--) result = result.prepend(kept.get(i));
    return result;
  }
  @Override public int size() { return size; }
  @Override public E get(int index) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index: " + index);
    FList<E> entry = this;
    for (int i = 0; i < index; i++) entry = entry.tail;
    return entry.head;
  }
  @Override public Iterator<E> iterator() {
    return new Iterator<E>() {
      private FList<E> next = FList.this;
      public boolean hasNext() { return next.size > 0; }
      public E next() {
        if (!hasNext()) throw new java.util.NoSuchElementException();
        E value = next.head; next = next.tail; return value;
      }
    };
  }
}
