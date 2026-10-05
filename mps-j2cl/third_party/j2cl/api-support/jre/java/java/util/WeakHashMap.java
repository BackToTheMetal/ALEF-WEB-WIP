package java.util;

import java.lang.ref.WeakReference;

/**
 * Equality-based weak-key map for the browser. Object keys use native WeakRef;
 * collected entries are purged on access. Values are strong, matching Java's
 * value-to-key retention caveat. Primitive-backed String/Boolean/Double keys
 * cannot be native weak targets and are retained until removal or clear.
 * This initial implementation uses a linear entry index.
 */
public class WeakHashMap<K, V> extends AbstractMap<K, V> {
  private final List<WeakEntry<K, V>> entries;
  private int version;

  public WeakHashMap() { this(16, 0.75f); }
  public WeakHashMap(int initialCapacity) { this(initialCapacity, 0.75f); }
  public WeakHashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0) throw new IllegalArgumentException("Negative capacity");
    if (!(loadFactor > 0)) throw new IllegalArgumentException("Non-positive or NaN load factor");
    entries = new ArrayList<>(initialCapacity);
  }
  public WeakHashMap(Map<? extends K, ? extends V> other) { this(other.size()); putAll(other); }

  private int find(Object key) {
    int hash = Objects.hashCode(key);
    for (int i = 0; i < entries.size(); i++) {
      WeakEntry<K, V> entry = entries.get(i);
      K current = entry.getKey();
      if (entry.expired(current)) entries.remove(i--);
      else if (entry.hash == hash && Objects.equals(key, current)) return i;
    }
    return -1;
  }
  private void purge() {
    for (int i = entries.size() - 1; i >= 0; i--) {
      WeakEntry<K, V> entry = entries.get(i);
      if (entry.expired(entry.getKey())) entries.remove(i);
    }
  }
  private boolean removeEntry(WeakEntry<K, V> target) {
    for (int i = 0; i < entries.size(); i++) {
      if (entries.get(i) == target) { entries.remove(i); return true; }
    }
    return false;
  }
  @Override public int size() { purge(); return entries.size(); }
  @Override public boolean containsKey(Object key) { return find(key) >= 0; }
  @Override public V get(Object key) { int i = find(key); return i < 0 ? null : entries.get(i).getValue(); }
  @Override public V put(K key, V value) {
    int index = find(key);
    if (index >= 0) return entries.get(index).setValue(value);
    entries.add(new WeakEntry<>(key, value));
    version++;
    return null;
  }
  @Override public V remove(Object key) {
    int index = find(key);
    if (index < 0) return null;
    V value = entries.remove(index).getValue();
    version++;
    return value;
  }
  @Override public void clear() { if (!entries.isEmpty()) { entries.clear(); version++; } }

  @Override public Set<Map.Entry<K, V>> entrySet() {
    return new AbstractSet<Map.Entry<K, V>>() {
      @Override public int size() { return WeakHashMap.this.size(); }
      @Override public void clear() { WeakHashMap.this.clear(); }
      @Override public boolean contains(Object object) {
        if (!(object instanceof Map.Entry)) return false;
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
        int index = find(entry.getKey());
        return index >= 0 && Objects.equals(entries.get(index).getValue(), entry.getValue());
      }
      @Override public boolean remove(Object object) {
        if (!contains(object)) return false;
        WeakHashMap.this.remove(((Map.Entry<?, ?>) object).getKey());
        return true;
      }
      @Override public Iterator<Map.Entry<K, V>> iterator() {
        purge();
        List<WeakEntry<K, V>> snapshot = new ArrayList<>(entries);
        return new Iterator<Map.Entry<K, V>>() {
          private int expectedVersion = version;
          private int position;
          private WeakEntry<K, V> next;
          private K nextKey;
          private WeakEntry<K, V> last;
          private K currentKey;
          private void checkVersion() { if (version != expectedVersion) throw new ConcurrentModificationException(); }
          @Override public boolean hasNext() {
            checkVersion();
            if (next != null) return true;
            while (position < snapshot.size()) {
              WeakEntry<K, V> candidate = snapshot.get(position++);
              K key = candidate.getKey();
              if (candidate.expired(key)) removeEntry(candidate);
              else { next = candidate; nextKey = key; return true; }
            }
            return false;
          }
          @Override public Map.Entry<K, V> next() {
            if (!hasNext()) throw new NoSuchElementException();
            last = next;
            // Match Java's iterator: retain the selected key across browser jobs
            // until the iterator advances or removes it. remove() observes this
            // field, so optimized builds must preserve the strong reference.
            currentKey = nextKey;
            next = null;
            nextKey = null;
            return last;
          }
          @Override public void remove() {
            checkVersion();
            if (last == null) throw new IllegalStateException();
            WeakHashMap.this.remove(currentKey);
            expectedVersion = version;
            last = null;
            currentKey = null;
          }
        };
      }
    };
  }

  private static final class WeakEntry<K, V> implements Map.Entry<K, V> {
    private final WeakReference<K> key;
    private final boolean nullKey;
    private final int hash;
    private V value;
    WeakEntry(K key, V value) {
      this.key = new WeakReference<>(key);
      nullKey = key == null;
      hash = Objects.hashCode(key);
      this.value = value;
    }
    boolean expired(K current) { return !nullKey && current == null; }
    @Override public K getKey() { return key.get(); }
    @Override public V getValue() { return value; }
    @Override public V setValue(V replacement) { V previous = value; value = replacement; return previous; }
    @Override public boolean equals(Object object) {
      if (!(object instanceof Map.Entry)) return false;
      Map.Entry<?, ?> other = (Map.Entry<?, ?>) object;
      return Objects.equals(getKey(), other.getKey()) && Objects.equals(value, other.getValue());
    }
    @Override public int hashCode() { return Objects.hashCode(getKey()) ^ Objects.hashCode(value); }
    @Override public String toString() { return getKey() + "=" + value; }
  }
}
