package org.javaconcurrentj2cl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/** Plain-Java acceptance checks, run against the overlaid J2CL runtime. */
public final class AtomicsTest {
  private static int checks;

  public static int run() {
    checks = 0;
    scalarValues();
    scalarUpdates();
    arrays();
    references();
    markedAndStampedReferences();
    adders();
    return checks;
  }

  private static void scalarValues() {
    AtomicBoolean flag = new AtomicBoolean();
    check(!flag.get(), "boolean default");
    check(flag.compareAndSet(false, true), "boolean CAS success");
    check(!flag.compareAndSet(false, false), "boolean CAS failure");
    check(flag.getAndSet(false), "boolean previous value");
    flag.lazySet(true);
    check(flag.weakCompareAndSet(true, false), "boolean weak CAS");

    AtomicInteger integer = new AtomicInteger();
    check(integer.get() == 0, "integer default");
    integer.set(4);
    check(integer.getAndAdd(3) == 4 && integer.get() == 7, "integer getAndAdd");
    check(integer.getAndSet(2) == 7 && integer.get() == 2, "integer getAndSet");
    check(!integer.compareAndSet(1, 8) && integer.get() == 2, "integer CAS failure");
    check(integer.weakCompareAndSet(2, 9) && integer.get() == 9, "integer weak CAS");
    integer.lazySet(Integer.MAX_VALUE);
    check(integer.getAndIncrement() == Integer.MAX_VALUE, "integer old max");
    check(integer.get() == Integer.MIN_VALUE, "integer overflow");
    check(integer.decrementAndGet() == Integer.MAX_VALUE, "integer underflow");

    long precise = 9007199254740993L;
    AtomicLong number = new AtomicLong(precise);
    check(number.getAndAdd(2) == precise && number.get() == precise + 2, "long JS precision");
    check(number.compareAndSet(precise + 2, 7), "long CAS success");
    check(!number.compareAndSet(precise, 8) && number.get() == 7, "long CAS failure");
    check(number.getAndSet(4) == 7 && number.get() == 4, "long getAndSet");
    check(number.weakCompareAndSet(4, 5), "long weak CAS");
    number.lazySet(Long.MAX_VALUE);
    check(number.getAndIncrement() == Long.MAX_VALUE, "long old max");
    check(number.get() == Long.MIN_VALUE, "long overflow");
    check(number.decrementAndGet() == Long.MAX_VALUE, "long underflow");
  }

  private static void scalarUpdates() {
    AtomicInteger integer = new AtomicInteger(2);
    check(integer.getAndUpdate(value -> value * 3) == 2 && integer.get() == 6,
        "integer getAndUpdate");
    check(integer.updateAndGet(value -> value - 1) == 5, "integer updateAndGet");
    check(integer.getAndAccumulate(3, (a, b) -> a - b) == 5 && integer.get() == 2,
        "integer getAndAccumulate operand order");
    check(integer.accumulateAndGet(4, (a, b) -> a * b) == 8, "integer accumulateAndGet");
    try {
      integer.updateAndGet(value -> { throw new IllegalArgumentException("update failed"); });
      throw new AssertionError("Expected update failure");
    } catch (IllegalArgumentException expected) {
      check(integer.get() == 8, "failed update leaves integer unchanged");
    }
    try {
      integer.getAndAccumulate(1, null);
      throw new AssertionError("Expected null function failure");
    } catch (NullPointerException expected) {
      check(integer.get() == 8, "null update leaves integer unchanged");
    }

    AtomicLong number = new AtomicLong(3);
    check(number.getAndUpdate(value -> value * 4) == 3 && number.get() == 12,
        "long getAndUpdate");
    check(number.updateAndGet(value -> value + 2) == 14, "long updateAndGet");
    check(number.getAndAccumulate(5, (a, b) -> a - b) == 14 && number.get() == 9,
        "long getAndAccumulate operand order");
    check(number.accumulateAndGet(3, (a, b) -> a * b) == 27, "long accumulateAndGet");
  }

  private static void arrays() {
    int[] original = {4, Integer.MAX_VALUE};
    AtomicIntegerArray integers = new AtomicIntegerArray(original);
    original[0] = 9;
    check(integers.length() == 2 && integers.get(0) == 4, "integer array owns copy");
    check(integers.getAndAdd(0, 3) == 4 && integers.get(0) == 7, "integer array add");
    check(integers.incrementAndGet(1) == Integer.MIN_VALUE, "integer array overflow");
    check(integers.compareAndSet(0, 7, 10), "integer array CAS");
    check(!integers.weakCompareAndSet(0, 7, 20), "integer array failed CAS");
    check(integers.getAndUpdate(0, value -> value / 2) == 10 && integers.get(0) == 5,
        "integer array getAndUpdate");
    check(integers.updateAndGet(0, value -> value + 2) == 7, "integer array updateAndGet");
    check(integers.getAndAccumulate(0, 3, (a, b) -> a - b) == 7 && integers.get(0) == 4,
        "integer array getAndAccumulate");
    check(integers.accumulateAndGet(0, 3, (a, b) -> a * b) == 12,
        "integer array accumulateAndGet");
    check(original[0] == 9, "integer array updates do not alias caller");

    long[] source = {9007199254740993L, Long.MAX_VALUE};
    AtomicLongArray longs = new AtomicLongArray(source);
    source[0] = 0;
    check(longs.length() == 2 && longs.get(0) == 9007199254740993L, "long array copy and precision");
    check(longs.incrementAndGet(1) == Long.MIN_VALUE, "long array overflow");
    check(longs.getAndSet(0, 10) == 9007199254740993L, "long array previous value");
    check(longs.compareAndSet(0, 10, 12), "long array CAS");
    check(!longs.weakCompareAndSet(0, 10, 20), "long array failed CAS");
    check(longs.getAndUpdate(0, value -> value / 2) == 12 && longs.get(0) == 6,
        "long array getAndUpdate");
    check(longs.updateAndGet(0, value -> value + 1) == 7, "long array updateAndGet");
    check(longs.getAndAccumulate(0, 4, (a, b) -> a - b) == 7 && longs.get(0) == 3,
        "long array getAndAccumulate");
    check(longs.accumulateAndGet(0, 3, (a, b) -> a * b) == 9, "long array accumulateAndGet");
  }

  private static void references() {
    EqualValue first = new EqualValue();
    EqualValue equalButDistinct = new EqualValue();
    check(first != equalButDistinct && first.equals(equalButDistinct), "reference test fixture");
    AtomicReference<EqualValue> reference = new AtomicReference<>(first);
    check(!reference.compareAndSet(equalButDistinct, null) && reference.get() == first,
        "reference CAS uses identity");
    check(reference.weakCompareAndSet(first, equalButDistinct), "reference weak CAS");
    check(reference.getAndSet(null) == equalButDistinct && reference.get() == null,
        "reference null state");
    reference.lazySet(first);
    check(reference.get() == first, "reference lazy set");

    AtomicReference<String> text = new AtomicReference<>("a");
    check("a".equals(text.getAndUpdate(value -> value + "b")) && "ab".equals(text.get()),
        "reference getAndUpdate");
    check("abc".equals(text.updateAndGet(value -> value + "c")), "reference updateAndGet");
    check("abc".equals(text.getAndAccumulate("d", (a, b) -> a + b)) && "abcd".equals(text.get()),
        "reference getAndAccumulate");
    check("abcde".equals(text.accumulateAndGet("e", (a, b) -> a + b)),
        "reference accumulateAndGet");
    try {
      text.getAndUpdate(value -> { throw new IllegalArgumentException("update failed"); });
      throw new AssertionError("Expected reference update failure");
    } catch (IllegalArgumentException expected) {
      check("abcde".equals(text.get()), "failed update leaves reference unchanged");
    }

    EqualValue[] original = {first};
    AtomicReferenceArray<EqualValue> references = new AtomicReferenceArray<>(original);
    original[0] = equalButDistinct;
    check(references.get(0) == first, "reference array owns copy");
    check(!references.compareAndSet(0, equalButDistinct, null), "reference array CAS identity");
    check(references.weakCompareAndSet(0, first, null), "reference array weak CAS");
    check(references.get(0) == null && original[0] == equalButDistinct, "reference array isolation");
    AtomicReferenceArray<Number> covariant = new AtomicReferenceArray<Number>(new Integer[] {1});
    covariant.set(0, 2.5);
    check(covariant.get(0).doubleValue() == 2.5, "reference array accepts other subtype");

    AtomicReferenceArray<String> strings = new AtomicReferenceArray<>(new String[] {"a"});
    check("a".equals(strings.getAndUpdate(0, value -> value + "b"))
        && "ab".equals(strings.get(0)), "reference array getAndUpdate");
    check("abc".equals(strings.updateAndGet(0, value -> value + "c")),
        "reference array updateAndGet");
    check("abc".equals(strings.getAndAccumulate(0, "d", (a, b) -> a + b))
        && "abcd".equals(strings.get(0)), "reference array getAndAccumulate");
    check("abcde".equals(strings.accumulateAndGet(0, "e", (a, b) -> a + b)),
        "reference array accumulateAndGet");
  }

  private static void markedAndStampedReferences() {
    EqualValue first = new EqualValue();
    EqualValue second = new EqualValue();
    AtomicMarkableReference<EqualValue> marked = new AtomicMarkableReference<>(first, false);
    boolean[] mark = new boolean[1];
    check(marked.get(mark) == first && !mark[0], "reference and mark read together");
    check(!marked.compareAndSet(second, null, false, true), "mark CAS reference identity");
    check(!marked.compareAndSet(first, second, true, false), "mark CAS expected mark");
    check(marked.compareAndSet(first, second, false, true), "mark CAS success");
    check(marked.isMarked() && marked.getReference() == second, "mark CAS updates both");
    check(!marked.attemptMark(first, false) && marked.isMarked(), "attemptMark mismatch");
    check(marked.attemptMark(second, false) && !marked.isMarked(), "attemptMark success");
    check(marked.weakCompareAndSet(second, null, false, true), "mark weak CAS");
    marked.set(first, false);
    check(marked.getReference() == first && !marked.isMarked(), "mark set");

    AtomicStampedReference<EqualValue> stamped = new AtomicStampedReference<>(first, 4);
    int[] stamp = new int[1];
    check(stamped.get(stamp) == first && stamp[0] == 4, "reference and stamp read together");
    check(!stamped.compareAndSet(second, null, 4, 5), "stamp CAS reference identity");
    check(!stamped.compareAndSet(first, second, 3, 5), "stamp CAS expected stamp");
    check(stamped.compareAndSet(first, second, 4, 5), "stamp CAS success");
    check(stamped.getStamp() == 5 && stamped.getReference() == second, "stamp CAS updates both");
    check(!stamped.attemptStamp(first, 8) && stamped.getStamp() == 5, "attemptStamp mismatch");
    check(stamped.attemptStamp(second, -1) && stamped.getStamp() == -1, "attemptStamp success");
    check(stamped.weakCompareAndSet(second, null, -1, 0), "stamp weak CAS");
    stamped.set(first, Integer.MIN_VALUE);
    check(stamped.getReference() == first && stamped.getStamp() == Integer.MIN_VALUE,
        "stamp set accepts all integers");
  }

  private static void adders() {
    LongAdder longs = new LongAdder();
    longs.add(9007199254740993L);
    longs.increment();
    longs.decrement();
    check(longs.sum() == 9007199254740993L, "long adder precision");
    check(longs.sumThenReset() == 9007199254740993L && longs.sum() == 0, "long sumThenReset");
    longs.add(Long.MAX_VALUE);
    longs.increment();
    check(longs.sum() == Long.MIN_VALUE, "long adder overflow");
    longs.reset();
    longs.add(7);
    check(longs.intValue() == 7 && longs.longValue() == 7 && longs.floatValue() == 7
        && longs.doubleValue() == 7 && "7".equals(longs.toString()), "long adder Number methods");

    DoubleAdder doubles = new DoubleAdder();
    doubles.add(1.5);
    doubles.add(2.25);
    check(doubles.sum() == 3.75, "double adder sum");
    check(doubles.intValue() == 3 && doubles.longValue() == 3 && doubles.floatValue() == 3.75
        && doubles.doubleValue() == 3.75 && "3.75".equals(doubles.toString()),
        "double adder Number methods");
    check(doubles.sumThenReset() == 3.75 && doubles.sum() == 0, "double sumThenReset");
    doubles.add(Double.NaN);
    check(Double.isNaN(doubles.sum()), "double adder NaN");
    doubles.reset();
    check(doubles.sum() == 0, "double reset");
  }

  private static void check(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
    checks++;
  }

  private static final class EqualValue {
    @Override
    public boolean equals(Object other) {
      return other instanceof EqualValue;
    }

    @Override
    public int hashCode() {
      return 1;
    }
  }
}
