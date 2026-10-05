/*
 * Copyright 2003-2011 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package jetbrains.mps.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jetbrains.annotations.NotNull;

/**
 * Equality-based weak set for browsers. Object-backed Java values use native WeakRef;
 * expired entries are removed on access. JavaScript primitive-backed String, Boolean,
 * and Double values are retained strongly because they cannot be WeakRef targets.
 * Null is retained as in WeakHashMap. Object values require native WeakRef support.
 */
public class WeakSet<T> extends AbstractSet<T> {
  private final List<Entry<T>> entries;
  private int version;

  public WeakSet(int size) {
    entries = new ArrayList<>(size);
  }

  public WeakSet() {
    this(16);
  }

  public WeakSet(@NotNull Collection<? extends T> collection) {
    this(collection.size());
    addAll(collection);
  }

  @Override
  public boolean add(T value) {
    if (find(value) >= 0) return false;
    entries.add(new Entry<>(value));
    version++;
    return true;
  }

  @Override
  public boolean contains(Object value) {
    return find(value) >= 0;
  }

  @Override
  public boolean remove(Object value) {
    int index = find(value);
    if (index < 0) return false;
    entries.remove(index);
    version++;
    return true;
  }

  @Override
  public void clear() {
    if (!entries.isEmpty()) {
      entries.clear();
      version++;
    }
  }

  private int find(Object value) {
    int hash = Objects.hashCode(value);
    for (int i = 0; i < entries.size(); i++) {
      Entry<T> entry = entries.get(i);
      T current = entry.get();
      if (entry.expired(current)) {
        entries.remove(i--);
      } else if (entry.hash == hash && Objects.equals(value, current)) {
        return i;
      }
    }
    return -1;
  }

  private void purge() {
    for (int i = 0; i < entries.size(); i++) {
      Entry<T> entry = entries.get(i);
      if (entry.expired(entry.get())) entries.remove(i--);
    }
  }

  @Override
  public int size() {
    purge();
    return entries.size();
  }

  @NotNull
  @Override
  public Iterator<T> iterator() {
    purge();
    // Snapshot references, not referents: an iterator must not pin the whole set.
    List<Entry<T>> snapshot = new ArrayList<>(entries);
    return new Iterator<T>() {
      private int expectedVersion = version;
      private int index;
      private Entry<T> nextEntry;
      private Entry<T> lastEntry;
      private T nextValue;
      private boolean ready;
      private boolean removable;

      private void checkVersion() {
        if (version != expectedVersion) throw new ConcurrentModificationException();
      }

      @Override
      public boolean hasNext() {
        checkVersion();
        if (ready) return true;
        while (index < snapshot.size()) {
          Entry<T> entry = snapshot.get(index++);
          T value = entry.get();
          if (entry.expired(value)) {
            entries.remove(entry);
          } else {
            nextEntry = entry;
            nextValue = value;
            ready = true;
            return true;
          }
        }
        return false;
      }

      @Override
      public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        T value = nextValue;
        lastEntry = nextEntry;
        nextEntry = null;
        nextValue = null;
        ready = false;
        removable = true;
        return value;
      }

      @Override
      public void remove() {
        checkVersion();
        if (!removable) throw new IllegalStateException();
        if (entries.remove(lastEntry)) version++;
        expectedVersion = version;
        lastEntry = null;
        removable = false;
      }
    };
  }

  private static final class Entry<T> {
    final int hash;
    final NativeWeakRef<T> reference;
    final T retained;

    Entry(T value) {
      hash = Objects.hashCode(value);
      if (value == null || value instanceof String || value instanceof Boolean || value instanceof Double) {
        reference = null;
        retained = value;
      } else {
        if (getWeakRefConstructor() == null) {
          throw new UnsupportedOperationException("WeakSet requires native WeakRef support");
        }
        reference = new NativeWeakRef<>(value);
        retained = null;
      }
    }

    T get() {
      return reference == null ? retained : reference.deref();
    }

    boolean expired(T value) {
      return reference != null && value == null;
    }
  }

  @JsProperty(namespace = JsPackage.GLOBAL, name = "globalThis.WeakRef")
  private static native Object getWeakRefConstructor();

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakRef")
  private static final class NativeWeakRef<T> {
    NativeWeakRef(T value) {}
    native T deref();
  }
}
