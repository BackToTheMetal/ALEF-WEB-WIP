package org.trovej2cl;

import gnu.trove.*;
import java.util.*;

/** Shared executable contracts, compiled separately against each implementation. */
public final class ContractSuite {
  private static int seed;
  private static int checks;
  private static int digest;
  private static StringBuilder details;

  public static String run() { return run(20260908); }

  public static String run(int initialSeed) {
    seed = initialSeed; checks = 0; digest = 17; details = new StringBuilder();
    objects(); boundaries(); lists(); callbacks(); identity();
    FamilyScenarios.run();
    MpsScenarios.run();
    return "PASS checks=" + checks + " digest=" + digest + "\n" + details;
  }

  static int next() {
    seed ^= seed << 13; seed ^= seed >>> 17; seed ^= seed << 5;
    return seed;
  }

  static void check(boolean value, String label) {
    checks++;
    if (!value) throw new AssertionError(label + " (check " + checks + ")");
  }

  static void record(String value) {
    digest = (digest * 31 + value.hashCode()) | 0;
    details.append(value).append('\n');
  }

  private static final TObjectHashingStrategy<String> IGNORE_CASE = new TObjectHashingStrategy<String>() {
    public int computeHashCode(String s) { return 1; } // exercise collisions deliberately
    public boolean equals(String a, String b) { return a.equalsIgnoreCase(b); }
  };

  static void objects() {
    THashMap<String, Integer> map = new THashMap<>(1, 0.6f, IGNORE_CASE);
    check(map.put("Ab", 1) == null, "object insert");
    check(map.put("aB", 2) == 1 && map.size() == 1, "strategy replacement");
    map.put("nil", null);
    check(map.containsKey("NIL") && map.get("NIL") == null, "null value present");
    check(map.get("absent") == null && !map.containsKey("absent"), "object missing");
    Set<String> keys = map.keySet(); Collection<Integer> values = map.values();
    keys.remove("AB"); check(map.size() == 1, "backed key view");
    map.put("entry", 7); check(values.contains(7), "backed values");
    for (Map.Entry<String, Integer> e : map.entrySet()) {
      if (e.getKey().equals("entry")) check(e.setValue(8) == 7, "entry replacement");
    }
    check(map.get("ENTRY") == 8, "strategy entry");
    THashMap<String, Integer> copy = map.clone();
    copy.put("entry", 10); check(map.get("entry") == 8, "object clone independent");
    check(copy.get("ENTRY") == 10, "clone strategy");
    map.clear();
    for (int i = 0; i < 1000; i++) map.put("k" + i, i);
    for (int i = 0; i < 1000; i += 2) map.remove("k" + i);
    map.compact(); map.ensureCapacity(500);
    for (int i = 0; i < 1000; i++) check(map.containsKey("K" + i) == ((i & 1) != 0), "collision and compact");
    Iterator<String> it = keys.iterator();
    while (it.hasNext()) { it.next(); it.remove(); }
    check(map.isEmpty() && values.isEmpty(), "remove through old view");
    THashSet<String> set = new THashSet<>(IGNORE_CASE);
    set.add("A"); check(!set.add("a"), "set strategy");
    String[] small = set.toArray(new String[0]); check(small.length == 1, "typed set array");
    String[] large = {"x", "tail", "keep"};
    check(set.toArray(large) == large && large[1] == null && large[2].equals("keep"), "array tail");
    map.put("x", 1);
    String[] typed = map.keySet().toArray(new String[0]);
    check(typed.length == 1 && typed[0].equals("x"), "typed map view");
    record("wrongArrayType=" + exception(() -> set.toArray(new Integer[0])));
    set.add("B"); set.add("C");
    check(set.retainAll(Arrays.asList("A", "B")) && set.size() == 2, "object set retainAll");
    map.put("y", 2); map.put("z", 3);
    check(map.keySet().retainAll(Arrays.asList("x", "y")) && map.size() == 2, "map key view retainAll");
    check(map.values().retainAll(Arrays.asList(2)) && map.size() == 1 && map.containsKey("y"), "map value view retainAll");
    record("object=" + map.size());
  }

  static void boundaries() {
    TLongObjectHashMap<String> ids = new TLongObjectHashMap<>();
    long[] keys = {0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, 9007199254740992L, 9007199254740993L};
    for (int i = 0; i < keys.length; i++) ids.put(keys[i], "v" + i);
    for (int i = 0; i < keys.length; i++) check(ids.get(keys[i]).equals("v" + i), "long precision");
    check(ids.size() == keys.length, "long distinct keys");
    TIntIntHashMap ints = new TIntIntHashMap();
    ints.put(1, Integer.MAX_VALUE); ints.increment(1);
    check(ints.get(1) == Integer.MIN_VALUE, "int overflow");
    TByteByteHashMap bytes = new TByteByteHashMap();
    bytes.put((byte)1, Byte.MAX_VALUE); bytes.increment((byte)1);
    check(bytes.get((byte)1) == Byte.MIN_VALUE, "byte overflow");
    TDoubleDoubleHashMap doubles = new TDoubleDoubleHashMap();
    doubles.put(1, Double.NaN); doubles.put(2, Double.POSITIVE_INFINITY); doubles.put(3, -0.0);
    check(Double.isNaN(doubles.get(1)), "NaN value");
    check(doubles.get(2) == Double.POSITIVE_INFINITY, "infinite value");
    check(1 / doubles.get(3) == Double.NEGATIVE_INFINITY, "negative zero value");
    record("doubleZero=" + HashFunctions.hash(-0.0));
    record("floatHash=" + HashFunctions.hash(1.2345f));
    record("longHash=" + HashFunctions.hash(Long.MIN_VALUE));
  }

  interface Action { void run(); }
  static String exception(Action action) {
    try { action.run(); return "none"; }
    catch (ArrayIndexOutOfBoundsException e) { return "array-bounds"; }
    catch (ArrayStoreException e) { return "array-store"; }
    catch (IndexOutOfBoundsException e) { return "bounds"; }
    catch (NullPointerException e) { return "null"; }
    catch (IllegalArgumentException e) { return "argument"; }
    catch (IllegalStateException e) { return "state"; }
    catch (NoSuchElementException e) { return "exhausted"; }
    catch (ConcurrentModificationException e) { return "modified"; }
  }

  static void lists() {
    TIntArrayList list = new TIntArrayList(new int[] {1, 2, 3});
    record("getNegative=" + exception(() -> list.get(-1)));
    record("getPast=" + exception(() -> list.get(3)));
    record("setNegative=" + exception(() -> list.set(-1, 5)));
    record("insertPast=" + exception(() -> list.insert(20, 5)));
    record("nullKey=" + exception(() -> new THashMap<String, String>().put(null, "x")));
    record("exhausted=" + exception(() -> new TIntHashSet().iterator().next()));
  }

  static void callbacks() {
    TIntIntHashMap map = new TIntIntHashMap();
    for (int i = 0; i < 100; i++) map.put(i, i);
    int[] calls = {0};
    check(!map.forEachEntry((k, v) -> ++calls[0] < 3) && calls[0] == 3, "procedure short circuit");
    check(map.retainEntries((k, v) -> (k & 1) == 0), "retain changed");
    check(map.size() == 50, "retain size");
    TIntIntIterator it = map.iterator(); it.advance(); map.put(101, 1);
    record("iteratorMutation=" + exception(it::advance));
    map.clear(); map.stopCompactingOnRemove(); map.startCompactingOnRemove(false);
    check(map.isEmpty(), "compaction pairing");
  }

  static final class Key {
    final int id;
    Key(int id) { this.id = id; }
    public boolean equals(Object o) { return o instanceof Key && ((Key)o).id == id; }
    public int hashCode() { return id; }
  }

  static void identity() {
    Key a = new Key(1), b = new Key(1);
    THashMap<Key, String> map = new THashMap<>(new TObjectIdentityHashingStrategy<Key>());
    map.put(a, "a"); map.put(b, "b");
    check(map.size() == 2 && map.get(a).equals("a") && map.get(b).equals("b"), "identity strategy");
    THashMap<Key, String> equality = new THashMap<>(); equality.put(a, "a"); equality.put(b, "b");
    check(equality.size() == 1, "canonical equality");
  }

}
