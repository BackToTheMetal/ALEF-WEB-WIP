// CHECKSTYLE_OFF: Copyrighted to Guava Authors.
/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// CHECKSTYLE_ON

package java.util.concurrent;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Main-thread emulation of {@link java.util.concurrent.ConcurrentHashMap}.
 *
 * <p>Note that the javascript is single-threaded, it is essentially a {@link java.util.HashMap},
 * implementing the new methods introduced by {@link ConcurrentMap}. Iterators capture entries at
 * construction so callbacks may mutate the live map without invalidating traversal. Entry updates
 * and iterator removals still write through to the live map.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

  private final Map<K, V> backingMap;
  private final Set<Entry<K, V>> entries = new EntrySet();

  public ConcurrentHashMap() {
    this.backingMap = new HashMap<K, V>();
  }

  public ConcurrentHashMap(int initialCapacity) {
    this.backingMap = new HashMap<K, V>(initialCapacity);
  }

  public ConcurrentHashMap(int initialCapacity, float loadFactor) {
    this.backingMap = new HashMap<K, V>(initialCapacity, loadFactor);
  }

  public ConcurrentHashMap(Map<? extends K, ? extends V> t) {
    this();
    putAll(t);
  }

  public V putIfAbsent(K key, V value) {
    checkNotNull(key);
    checkNotNull(value);
    return backingMap.putIfAbsent(key, value);
  }

  public boolean remove(Object key, Object oldValue) {
    V mapValue = get(key);
    if (mapValue != null && mapValue.equals(oldValue)) {
      remove(key);
      return true;
    } else {
      return false;
    }
  }

  public boolean replace(K key, V oldValue, V newValue) {
    checkNotNull(oldValue);
    checkNotNull(newValue);
    V mapValue = get(key);
    if (oldValue.equals(mapValue)) {
      put(key, newValue);
      return true;
    } else {
      return false;
    }
  }

  public V replace(K key, V value) {
    checkNotNull(value);
    if (containsKey(key)) {
      return put(key, value);
    } else {
      return null;
    }
  }

  @Override
  public boolean containsKey(Object key) {
    checkNotNull(key);
    return backingMap.containsKey(key);
  }

  @Override
  public V get(Object key) {
    checkNotNull(key);
    return backingMap.get(key);
  }

  @Override
  public V put(K key, V value) {
    checkNotNull(key);
    checkNotNull(value);
    return backingMap.put(key, value);
  }

  @Override
  public boolean containsValue(Object value) {
    checkNotNull(value);
    return backingMap.containsValue(value);
  }

  @Override
  public V remove(Object key) {
    checkNotNull(key);
    return backingMap.remove(key);
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return entries;
  }

  public boolean contains(Object value) {
    return containsValue(value);
  }

  public Enumeration<V> elements() {
    return Collections.enumeration(values());
  }

  public Enumeration<K> keys() {
    return Collections.enumeration(keySet());
  }

  public static <T> Set<T> newKeySet() {
    return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
  }

  @Override
  public int size() {
    return backingMap.size();
  }

  @Override
  public boolean isEmpty() {
    return backingMap.isEmpty();
  }

  @Override
  public void clear() {
    backingMap.clear();
  }

  @Override
  public Collection<V> values() {
    return new AbstractCollection<V>() {
      @Override public int size() { return ConcurrentHashMap.this.size(); }
      @Override public void clear() { ConcurrentHashMap.this.clear(); }
      @Override public boolean contains(Object value) { return containsValue(value); }
      @Override public Iterator<V> iterator() {
        Iterator<Entry<K, V>> iterator = entries.iterator();
        return new Iterator<V>() {
          @Override public boolean hasNext() { return iterator.hasNext(); }
          @Override public V next() { return iterator.next().getValue(); }
          @Override public void remove() { iterator.remove(); }
        };
      }
      @Override public boolean removeIf(Predicate<? super V> filter) {
        Objects.requireNonNull(filter);
        return removeMatching(entry -> filter.test(entry.getValue()));
      }
    };
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Objects.requireNonNull(function);
    for (Entry<K, V> entry : entries) {
      K key = entry.getKey();
      V previous = entry.getValue();
      do {
        V replacement = Objects.requireNonNull(function.apply(key, previous));
        if (replace(key, previous, replacement)) break;
        // A reentrant callback may remove or replace this mapping.
        previous = get(key);
      } while (previous != null);
    }
  }

  private boolean removeMatching(Predicate<? super Entry<K, V>> filter) {
    boolean changed = false;
    for (Entry<K, V> entry : entries) {
      V previous = entry.getValue();
      if (filter.test(entry) && remove(entry.getKey(), previous)) changed = true;
    }
    return changed;
  }

  private final class WriteThroughEntry extends SimpleEntry<K, V> {
    WriteThroughEntry(K key, V value) { super(key, value); }

    @Override public V setValue(V value) {
      Objects.requireNonNull(value);
      put(getKey(), value);
      return super.setValue(value);
    }
  }

  private final class EntrySet extends AbstractSet<Entry<K, V>> {
    @Override public int size() { return ConcurrentHashMap.this.size(); }
    @Override public void clear() { ConcurrentHashMap.this.clear(); }

    @Override public Iterator<Entry<K, V>> iterator() {
      List<Entry<K, V>> snapshot = new ArrayList<>();
      for (Entry<K, V> entry : backingMap.entrySet()) {
        snapshot.add(new WriteThroughEntry(entry.getKey(), entry.getValue()));
      }
      Iterator<Entry<K, V>> iterator = snapshot.iterator();
      return new Iterator<Entry<K, V>>() {
        private Entry<K, V> current;
        @Override public boolean hasNext() { return iterator.hasNext(); }
        @Override public Entry<K, V> next() {
          current = iterator.next();
          return current;
        }
        @Override public void remove() {
          if (current == null) throw new IllegalStateException();
          ConcurrentHashMap.this.remove(current.getKey());
          current = null;
        }
      };
    }

    @Override public Spliterator<Entry<K, V>> spliterator() {
      return Spliterators.spliteratorUnknownSize(iterator(),
          Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.NONNULL);
    }

    @Override public boolean contains(Object object) {
      if (!(object instanceof Entry)) return false;
      Entry<?, ?> entry = (Entry<?, ?>) object;
      return entry.getKey() != null && entry.getValue() != null
          && entry.getValue().equals(get(entry.getKey()));
    }

    @Override public boolean remove(Object object) {
      if (!(object instanceof Entry)) return false;
      Entry<?, ?> entry = (Entry<?, ?>) object;
      return entry.getKey() != null && entry.getValue() != null
          && ConcurrentHashMap.this.remove(entry.getKey(), entry.getValue());
    }

    @Override public boolean add(Entry<K, V> entry) {
      return !Objects.equals(put(entry.getKey(), entry.getValue()), entry.getValue());
    }

    @Override public boolean removeIf(Predicate<? super Entry<K, V>> filter) {
      Objects.requireNonNull(filter);
      return removeMatching(filter);
    }
  }
}
