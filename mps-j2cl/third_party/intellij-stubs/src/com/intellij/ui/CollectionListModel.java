package com.intellij.ui;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
public class CollectionListModel<T> extends javax.swing.AbstractListModel<T> {
  private final List<T> items = new ArrayList<>();
  public CollectionListModel() {}
  public CollectionListModel(Collection<? extends T> values) { items.addAll(values); }
  @SafeVarargs public CollectionListModel(T... values) { Collections.addAll(items, values); }
  @Override public int getSize() { return items.size(); }
  @Override public T getElementAt(int index) { return items.get(index); }
  public List<T> getItems() { return Collections.unmodifiableList(items); }
  public void add(T value) { int index = items.size(); items.add(value); fireIntervalAdded(this, index, index); }
  public void add(Collection<? extends T> values) { addAll(values); }
  public void addAll(Collection<? extends T> values) { int start = items.size(); items.addAll(values); if (items.size() > start) fireIntervalAdded(this, start, items.size() - 1); }
  public void remove(int index) { items.remove(index); fireIntervalRemoved(this, index, index); }
  public void remove(T value) { int index = items.indexOf(value); if (index >= 0) remove(index); }
  public void removeAll() { int size = items.size(); items.clear(); if (size > 0) fireIntervalRemoved(this, 0, size - 1); }
  public void replaceAll(Collection<? extends T> values) { removeAll(); addAll(values); }
  public void setElementAt(T value, int index) { items.set(index, value); fireContentsChanged(this, index, index); }
}
