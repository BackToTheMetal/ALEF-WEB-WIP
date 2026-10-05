package org.trovej2cl;

import gnu.trove.*;
import java.util.Arrays;
import java.util.Random;

/** Independent expectations for the defects found in the code review. */
final class ReviewRegressions {
  private static void check(boolean result, String label) {
    if (!result) throw new AssertionError(label);
  }
  static void run() {
    byteLists(); intLists(); longLists(); floatLists(); doubleLists(); nullKeyRemoval();
  }
  private static final class ChoiceRandom extends Random {
    final boolean last;
    int calls;
    ChoiceRandom(boolean last) { this.last = last; }
    @Override public int nextInt(int bound) {
      check(bound > 0, "positive random bound");
      calls++;
      return last ? bound - 1 : 0;
    }
  }

  private static final class ByteList extends TByteArrayList {
    ByteList(byte[] values) { super(values); }
    byte[] storage() { return _data; }
  }
  private static void byteLists() {
    byte[] input = {9, 8, 7, 6};
    TByteArrayList list = new TByteArrayList(new byte[]{1, 2, 3, 4});
    list.set(1, input, 1, 2);
    check(Arrays.equals(list.toNativeArray(), new byte[]{1, 8, 7, 4}), "byte bulk offsets");
    check(Arrays.equals(input, new byte[]{9, 8, 7, 6}), "byte source preserved");
    list.set(0, new byte[]{5, 6});
    check(Arrays.equals(list.toNativeArray(), new byte[]{5, 6, 7, 4}), "byte convenience set");
    list.set(list.size(), new byte[0]);
    check(list.size() == 4, "byte empty slice");
    try {
      list.set(0, input, 3, 2);
      throw new AssertionError("byte invalid source slice accepted");
    } catch (ArrayIndexOutOfBoundsException expected) {}
    check(Arrays.equals(list.toNativeArray(), new byte[]{5, 6, 7, 4}), "byte rejected copy preserves contents");
    ByteList alias = new ByteList(new byte[]{1, 2, 3, 4});
    alias.set(1, alias.storage(), 0, 3);
    check(Arrays.equals(alias.toNativeArray(), new byte[]{1, 1, 2, 3}), "byte overlap right");
    alias = new ByteList(new byte[]{1, 2, 3, 4});
    alias.set(0, alias.storage(), 1, 3);
    check(Arrays.equals(alias.toNativeArray(), new byte[]{2, 3, 4, 4}), "byte overlap left");
    ChoiceRandom last = new ChoiceRandom(true);
    list = new TByteArrayList(new byte[]{1, 2});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new byte[]{1, 2}), "byte shuffle permits identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new byte[]{2, 1}), "byte shuffle permits swap");
    list = new TByteArrayList(new byte[]{1, 2, 3, 4});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new byte[]{1, 2, 3, 4}), "byte larger identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new byte[]{2, 3, 4, 1}), "byte larger permutation");
    int calls = last.calls;
    new TByteArrayList().shuffle(last);
    new TByteArrayList(new byte[]{1}).shuffle(last);
    check(last.calls == calls, "byte empty/singleton does not request randomness");
  }

  private static final class IntList extends TIntArrayList {
    IntList(int[] values) { super(values); }
    int[] storage() { return _data; }
  }
  private static void intLists() {
    int[] input = {9, 8, 7, 6};
    TIntArrayList list = new TIntArrayList(new int[]{1, 2, 3, 4});
    list.set(1, input, 1, 2);
    check(Arrays.equals(list.toNativeArray(), new int[]{1, 8, 7, 4}), "int bulk offsets");
    check(Arrays.equals(input, new int[]{9, 8, 7, 6}), "int source preserved");
    list.set(0, new int[]{5, 6});
    check(Arrays.equals(list.toNativeArray(), new int[]{5, 6, 7, 4}), "int convenience set");
    list.set(list.size(), new int[0]);
    check(list.size() == 4, "int empty slice");
    try {
      list.set(0, input, 3, 2);
      throw new AssertionError("int invalid source slice accepted");
    } catch (ArrayIndexOutOfBoundsException expected) {}
    check(Arrays.equals(list.toNativeArray(), new int[]{5, 6, 7, 4}), "int rejected copy preserves contents");
    IntList alias = new IntList(new int[]{1, 2, 3, 4});
    alias.set(1, alias.storage(), 0, 3);
    check(Arrays.equals(alias.toNativeArray(), new int[]{1, 1, 2, 3}), "int overlap right");
    alias = new IntList(new int[]{1, 2, 3, 4});
    alias.set(0, alias.storage(), 1, 3);
    check(Arrays.equals(alias.toNativeArray(), new int[]{2, 3, 4, 4}), "int overlap left");
    ChoiceRandom last = new ChoiceRandom(true);
    list = new TIntArrayList(new int[]{1, 2});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new int[]{1, 2}), "int shuffle permits identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new int[]{2, 1}), "int shuffle permits swap");
    list = new TIntArrayList(new int[]{1, 2, 3, 4});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new int[]{1, 2, 3, 4}), "int larger identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new int[]{2, 3, 4, 1}), "int larger permutation");
    int calls = last.calls;
    new TIntArrayList().shuffle(last);
    new TIntArrayList(new int[]{1}).shuffle(last);
    check(last.calls == calls, "int empty/singleton does not request randomness");
  }

  private static final class LongList extends TLongArrayList {
    LongList(long[] values) { super(values); }
    long[] storage() { return _data; }
  }
  private static void longLists() {
    long[] input = {9, 8, 7, 6};
    TLongArrayList list = new TLongArrayList(new long[]{1, 2, 3, 4});
    list.set(1, input, 1, 2);
    check(Arrays.equals(list.toNativeArray(), new long[]{1, 8, 7, 4}), "long bulk offsets");
    check(Arrays.equals(input, new long[]{9, 8, 7, 6}), "long source preserved");
    list.set(0, new long[]{5, 6});
    check(Arrays.equals(list.toNativeArray(), new long[]{5, 6, 7, 4}), "long convenience set");
    list.set(list.size(), new long[0]);
    check(list.size() == 4, "long empty slice");
    try {
      list.set(0, input, 3, 2);
      throw new AssertionError("long invalid source slice accepted");
    } catch (ArrayIndexOutOfBoundsException expected) {}
    check(Arrays.equals(list.toNativeArray(), new long[]{5, 6, 7, 4}), "long rejected copy preserves contents");
    LongList alias = new LongList(new long[]{1, 2, 3, 4});
    alias.set(1, alias.storage(), 0, 3);
    check(Arrays.equals(alias.toNativeArray(), new long[]{1, 1, 2, 3}), "long overlap right");
    alias = new LongList(new long[]{1, 2, 3, 4});
    alias.set(0, alias.storage(), 1, 3);
    check(Arrays.equals(alias.toNativeArray(), new long[]{2, 3, 4, 4}), "long overlap left");
    ChoiceRandom last = new ChoiceRandom(true);
    list = new TLongArrayList(new long[]{1, 2});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new long[]{1, 2}), "long shuffle permits identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new long[]{2, 1}), "long shuffle permits swap");
    list = new TLongArrayList(new long[]{1, 2, 3, 4});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new long[]{1, 2, 3, 4}), "long larger identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new long[]{2, 3, 4, 1}), "long larger permutation");
    int calls = last.calls;
    new TLongArrayList().shuffle(last);
    new TLongArrayList(new long[]{1}).shuffle(last);
    check(last.calls == calls, "long empty/singleton does not request randomness");
  }

  private static final class FloatList extends TFloatArrayList {
    FloatList(float[] values) { super(values); }
    float[] storage() { return _data; }
  }
  private static void floatLists() {
    float[] input = {9, 8, 7, 6};
    TFloatArrayList list = new TFloatArrayList(new float[]{1, 2, 3, 4});
    list.set(1, input, 1, 2);
    check(Arrays.equals(list.toNativeArray(), new float[]{1, 8, 7, 4}), "float bulk offsets");
    check(Arrays.equals(input, new float[]{9, 8, 7, 6}), "float source preserved");
    list.set(0, new float[]{5, 6});
    check(Arrays.equals(list.toNativeArray(), new float[]{5, 6, 7, 4}), "float convenience set");
    list.set(list.size(), new float[0]);
    check(list.size() == 4, "float empty slice");
    try {
      list.set(0, input, 3, 2);
      throw new AssertionError("float invalid source slice accepted");
    } catch (ArrayIndexOutOfBoundsException expected) {}
    check(Arrays.equals(list.toNativeArray(), new float[]{5, 6, 7, 4}), "float rejected copy preserves contents");
    FloatList alias = new FloatList(new float[]{1, 2, 3, 4});
    alias.set(1, alias.storage(), 0, 3);
    check(Arrays.equals(alias.toNativeArray(), new float[]{1, 1, 2, 3}), "float overlap right");
    alias = new FloatList(new float[]{1, 2, 3, 4});
    alias.set(0, alias.storage(), 1, 3);
    check(Arrays.equals(alias.toNativeArray(), new float[]{2, 3, 4, 4}), "float overlap left");
    ChoiceRandom last = new ChoiceRandom(true);
    list = new TFloatArrayList(new float[]{1, 2});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new float[]{1, 2}), "float shuffle permits identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new float[]{2, 1}), "float shuffle permits swap");
    list = new TFloatArrayList(new float[]{1, 2, 3, 4});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new float[]{1, 2, 3, 4}), "float larger identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new float[]{2, 3, 4, 1}), "float larger permutation");
    int calls = last.calls;
    new TFloatArrayList().shuffle(last);
    new TFloatArrayList(new float[]{1}).shuffle(last);
    check(last.calls == calls, "float empty/singleton does not request randomness");
  }

  private static final class DoubleList extends TDoubleArrayList {
    DoubleList(double[] values) { super(values); }
    double[] storage() { return _data; }
  }
  private static void doubleLists() {
    double[] input = {9, 8, 7, 6};
    TDoubleArrayList list = new TDoubleArrayList(new double[]{1, 2, 3, 4});
    list.set(1, input, 1, 2);
    check(Arrays.equals(list.toNativeArray(), new double[]{1, 8, 7, 4}), "double bulk offsets");
    check(Arrays.equals(input, new double[]{9, 8, 7, 6}), "double source preserved");
    list.set(0, new double[]{5, 6});
    check(Arrays.equals(list.toNativeArray(), new double[]{5, 6, 7, 4}), "double convenience set");
    list.set(list.size(), new double[0]);
    check(list.size() == 4, "double empty slice");
    try {
      list.set(0, input, 3, 2);
      throw new AssertionError("double invalid source slice accepted");
    } catch (ArrayIndexOutOfBoundsException expected) {}
    check(Arrays.equals(list.toNativeArray(), new double[]{5, 6, 7, 4}), "double rejected copy preserves contents");
    DoubleList alias = new DoubleList(new double[]{1, 2, 3, 4});
    alias.set(1, alias.storage(), 0, 3);
    check(Arrays.equals(alias.toNativeArray(), new double[]{1, 1, 2, 3}), "double overlap right");
    alias = new DoubleList(new double[]{1, 2, 3, 4});
    alias.set(0, alias.storage(), 1, 3);
    check(Arrays.equals(alias.toNativeArray(), new double[]{2, 3, 4, 4}), "double overlap left");
    ChoiceRandom last = new ChoiceRandom(true);
    list = new TDoubleArrayList(new double[]{1, 2});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new double[]{1, 2}), "double shuffle permits identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new double[]{2, 1}), "double shuffle permits swap");
    list = new TDoubleArrayList(new double[]{1, 2, 3, 4});
    list.shuffle(last);
    check(Arrays.equals(list.toNativeArray(), new double[]{1, 2, 3, 4}), "double larger identity");
    list.shuffle(new ChoiceRandom(false));
    check(Arrays.equals(list.toNativeArray(), new double[]{2, 3, 4, 1}), "double larger permutation");
    int calls = last.calls;
    new TDoubleArrayList().shuffle(last);
    new TDoubleArrayList(new double[]{1}).shuffle(last);
    check(last.calls == calls, "double empty/singleton does not request randomness");
  }

  private static void nullKeyRemoval() {
    TObjectHashingStrategy<String> strategy = new TObjectHashingStrategy<String>() {
      public int computeHashCode(String key) { return 1; }
      public boolean equals(String a, String b) { return a.equalsIgnoreCase(b); }
    };
    THashMap<String, String> map = new THashMap<>(strategy);
    map.put("NULL", null); map.put("value", "v");
    check(map.keySet().remove("null"), "null-valued key reports removal through strategy");
    check(!map.containsKey("NULL") && map.size() == 1, "null-valued key removed");
    check(!map.keySet().remove("absent"), "absent key reports no change");
    check(map.keySet().remove("VALUE") && map.isEmpty(), "non-null removal");
    map.put("a", null); map.put("b", null); map.put("keep", "v");
    check(map.keySet().removeAll(Arrays.asList("A", "B", "missing")), "bulk null removals report change");
    check(map.size() == 1 && map.containsKey("keep"), "bulk removal preserves other entries");
    check(!map.keySet().removeAll(Arrays.asList("A", "missing")), "unchanged bulk removal");
    map.put("A", null);
    check(map.keySet().removeAll(map.keySet()) && map.isEmpty(), "aliased key view bulk removal");
  }
}
