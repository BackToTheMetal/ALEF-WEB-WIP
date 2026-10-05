package mpsj2cl.typechecking;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/** Deterministic public-contract tests; garbage collection has a separate browser probe. */
public final class FoundationJreTest {
  private static ReferenceQueue<Object> gcQueue;
  private static WeakReference<Object> gcWeak;
  private static PhantomReference<Object> gcPhantom;
  private static WeakHashMap<Object, String> gcMap;
  private static int gcNotifications;
  private static WeakReference<Object> iteratorWeak;
  private static WeakHashMap<Object, String> iteratorMap;
  private static Iterator<Map.Entry<Object, String>> gcIterator;
  public static String run() {
    require(Instant.MIN.toString().equals("-1000000000-01-01T00:00:00Z"), "Instant.MIN");
    require(Instant.MAX.toString().equals("+1000000000-12-31T23:59:59.999999999Z"), "Instant.MAX");
    require(Instant.ofEpochMilli(-1).equals(Instant.ofEpochSecond(-1, 999000000)), "negative millisecond normalization");
    require(Instant.ofEpochSecond(1, -1).getNano() == 999999999, "negative adjustment normalization");
    require(Instant.ofEpochSecond(1, -1).getEpochSecond() == 0, "negative adjustment seconds");
    require(Instant.EPOCH.minusNanos(1).toString().equals("1969-12-31T23:59:59.999999999Z"), "negative ISO");
    require(Instant.ofEpochMilli(951782400123L).toString().equals("2000-02-29T00:00:00.123Z"), "leap day");
    require(Instant.ofEpochSecond(0, 123456000).toString().equals("1970-01-01T00:00:00.123456Z"), "microseconds");
    long[] millis = {Long.MIN_VALUE, -1001, -1000, -999, -1, 0, 1, 999, 1000, 1001, Long.MAX_VALUE};
    for (long value : millis) require(Instant.ofEpochMilli(value).toEpochMilli() == value, "millisecond round trip");
    try { Instant.MAX.toEpochMilli(); throw new AssertionError("overflow accepted"); } catch (ArithmeticException expected) {}
    try { Instant.MAX.plusNanos(1); throw new AssertionError("range accepted"); } catch (java.time.DateTimeException expected) {}
    require(Instant.MIN.isBefore(Instant.EPOCH) && Instant.MAX.isAfter(Instant.EPOCH), "sentinel order");
    require(Instant.EPOCH.plusNanos(1).isAfter(Instant.EPOCH), "nanosecond comparison");
    require(Duration.between(Instant.EPOCH, Instant.EPOCH.minusNanos(1)).toNanos() == -1, "negative duration nanos");
    require(Duration.ofNanos(-1).toMillis() == 0, "duration truncation");
    require(Duration.ofMinutes(3).toMillis() == 180000, "timeout duration");
    ReferenceQueue<Object> queue = new ReferenceQueue<>();
    Object referent = new Object();
    WeakReference<Object> weak = new WeakReference<>(referent, queue);
    require(weak.get() == referent && weak.refersTo(referent), "weak identity");
    require(queue.poll() == null, "live reference not queued");
    weak.clear();
    require(weak.get() == null && weak.refersTo(null) && queue.poll() == null, "clear does not enqueue");
    require(weak.enqueue() && weak.isEnqueued() && !weak.enqueue(), "one enqueue");
    require(queue.poll() == weak && !weak.isEnqueued() && !weak.enqueue() && queue.poll() == null, "one dequeue");
    PhantomReference<Object> phantom = new PhantomReference<>(referent, queue);
    require(phantom.get() == null && phantom.refersTo(referent), "phantom inaccessible");
    require(phantom.enqueue() && phantom.refersTo(null) && queue.poll() == phantom, "phantom queue");
    SoftReference<Object> soft = new SoftReference<>(referent, queue);
    require(soft.get() == referent, "soft cache retained");
    soft.clear();
    require(soft.get() == null && queue.poll() == null, "soft explicit eviction");
    require(!new WeakReference<>(referent).enqueue(), "unregistered queue");
    WeakHashMap<Object, String> map = new WeakHashMap<>();
    Object key = new Object();
    map.put(key, "first"); map.put(null, "null");
    require(map.size() == 2 && "first".equals(map.put(key, "second")), "map replacement");
    require(map.containsKey(null) && "null".equals(map.get(null)), "null key");
    map.put(new EqualKey(1), "equal");
    require("equal".equals(map.get(new EqualKey(1))), "Java equality rather than identity");
    require(map.entrySet().contains(new AbstractMap.SimpleEntry<>(key, "second")), "entry equality");
    Iterator<Map.Entry<Object, String>> entries = map.entrySet().iterator();
    Map.Entry<Object, String> entry = entries.next();
    entry.setValue("changed");
    require("changed".equals(map.get(entry.getKey())), "entry updates map");
    entries.remove();
    require(map.size() == 2, "iterator removes map entry");
    Iterator<?> invalidated = map.keySet().iterator();
    map.put(new Object(), "new");
    try { invalidated.next(); throw new AssertionError("concurrent mutation accepted"); } catch (ConcurrentModificationException expected) {}
    map.clear(); require(map.isEmpty(), "map clear");
    return "PASS";
  }
  public static void prepareGc() {
    gcQueue = new ReferenceQueue<>();
    Object target = new Object();
    gcWeak = new WeakReference<>(target, gcQueue);
    gcPhantom = new PhantomReference<>(target, gcQueue);
    gcMap = new WeakHashMap<>();
    gcMap.put(new Object(), "weak");
    gcMap.put(null, "null");
    gcNotifications = 0;
  }
  public static String pollGc() {
    while (gcQueue.poll() != null) gcNotifications++;
    if (gcWeak.get() != null || gcNotifications != 2 || gcMap.size() != 1) return "PENDING";
    require(gcPhantom.refersTo(null) && "null".equals(gcMap.get(null)), "collected references preserve null map entry");
    return "COLLECTED";
  }
  public static void prepareIteratorGc() {
    Object target = new Object();
    iteratorWeak = new WeakReference<>(target);
    iteratorMap = new WeakHashMap<>();
    iteratorMap.put(target, "iterator");
    gcIterator = iteratorMap.entrySet().iterator();
    require(gcIterator.hasNext(), "iterator selects a live key");
  }
  public static String advanceIteratorGc() {
    require(iteratorWeak.get() != null, "hasNext retains the selected key across jobs");
    Map.Entry<Object, String> entry = gcIterator.next();
    require(entry.getKey() == iteratorWeak.get(), "next returns the selected live key");
    return "RETAINED";
  }
  public static String removeIteratorGc() {
    require(iteratorWeak.get() != null, "next retains the current key until remove or advance");
    gcIterator.remove();
    require(iteratorMap.isEmpty(), "iterator removal updates the map");
    return "REMOVED";
  }
  public static String pollIteratorGc() {
    return iteratorWeak.get() == null ? "COLLECTED" : "PENDING";
  }
  private static final class EqualKey {
    private final int value;
    EqualKey(int value) { this.value = value; }
    @Override public int hashCode() { return value; }
    @Override public boolean equals(Object other) { return other instanceof EqualKey && value == ((EqualKey) other).value; }
  }
  private static void require(boolean condition, String detail) { if (!condition) throw new AssertionError(detail); }
}
