package com.intellij.util.containers;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class SortedList<T> extends AbstractList<T> {
  private final List<T> values = new ArrayList<>();
  private final Comparator<? super T> comparator;
  public SortedList(Comparator<? super T> comparator) { this.comparator = comparator; }
  @Override public T get(int index) { return values.get(index); }
  @Override public int size() { return values.size(); }
  @Override public void add(int index, T value) { add(value); }
  @Override public boolean add(T value) { int index = java.util.Collections.binarySearch(values, value, comparator); values.add(index < 0 ? -index - 1 : index, value); return true; }
  @Override public T remove(int index) { return values.remove(index); }
  @Override public void clear() { values.clear(); }
}
