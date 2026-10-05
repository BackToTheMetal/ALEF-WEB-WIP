package org.trovej2cl;

import gnu.trove.*;

/** Correct behavior for documented upstream defects and J2CL portability edges. */
public final class RegressionSuite {
  private static void check(boolean value, String message) {
    if (!value) throw new AssertionError(message);
  }
  public static String run() {
    ReviewRegressions.run();
    TIntArrayList extrema = new TIntArrayList(new int[] {7, -4, 3});
    check(extrema.max() == 7 && extrema.min() == -4, "extrema must inspect live elements");
    check(new TByteArrayList(new byte[] {7, -4, 3}).min() == -4, "byte extrema");
    check(new TLongArrayList(new long[] {7, -4, 3}).max() == 7, "long extrema");
    check(new TFloatArrayList(new float[] {7, -4, 3}).min() == -4, "float extrema");
    check(new TDoubleArrayList(new double[] {7, -4, 3}).max() == 7, "double extrema");
    TObjectIntHashMap<String> original = new TObjectIntHashMap<>();
    original.put("x", 1);
    TObjectIntHashMap<String> cloned = (TObjectIntHashMap<String>)original.clone();
    cloned.put("x", 2);
    check(original.get("x") == 1, "object/primitive clones must copy value storage");
    TIntIntHashMap map = new TIntIntHashMap();
    for (int i = 0; i < 1000; i++) map.put(i, i);
    map.retainEntries((k, v) -> k < 3);
    check(map.size() == 3 && map.get(2) == 2, "retain must defer compaction");
    try {
      map.retainEntries((k, v) -> { throw new IllegalArgumentException("callback"); });
      throw new AssertionError("callback exception swallowed");
    } catch (IllegalArgumentException expected) {}
    map.stopCompactingOnRemove(); map.startCompactingOnRemove(true);
    TIntHashSet set = new TIntHashSet();
    for (int i = 0; i < 1000; i++) set.add(i);
    set.retainAll(new int[] {1, 3, 5});
    check(set.size() == 3 && set.contains(5), "retainAll must defer compaction");
    for (int i = 0; i < 2000; i++) { map.put(i, i); map.remove(i); }
    check(map.isEmpty(), "delete/reinsert termination");
    TFloatFloatHashMap floats = new TFloatFloatHashMap();
    float big = 16777216f;
    floats.put(1, big); floats.increment(1);
    check(floats.get(1) == big, "float32 addition rounding");
    floats.transformValues(v -> v + 1);
    check(floats.get(1) == big, "float32 callback rounding");
    floats.put(big + 1, 2);
    check(floats.containsKey(big), "float32 key boundary");
    TFloatArrayList list = new TFloatArrayList(new float[] {big + 1});
    check(list.get(0) == big, "float32 bulk copy");
    list.transformValues(v -> v + 1);
    check(list.get(0) == big, "float32 list callback");
    TIntArrayList ints = new TIntArrayList(new int[] {1, 2});
    try { ints.get(-1); throw new AssertionError("negative index accepted"); }
    catch (ArrayIndexOutOfBoundsException expected) {}
    return "PASS regression contracts";
  }
}
