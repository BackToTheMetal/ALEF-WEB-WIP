package java.util.concurrent.atomic;

/** A plain long accumulator for the main-thread execution profile. */
public class LongAdder extends Number implements java.io.Serializable {
  private long value;

  public LongAdder() {}

  public void add(long x) {
    value += x;
  }

  public void increment() {
    add(1);
  }

  public void decrement() {
    add(-1);
  }

  public long sum() {
    return value;
  }

  public void reset() {
    value = 0;
  }

  public long sumThenReset() {
    long previous = value;
    reset();
    return previous;
  }

  @Override
  public int intValue() {
    return (int) value;
  }

  @Override
  public long longValue() {
    return value;
  }

  @Override
  public float floatValue() {
    return (float) value;
  }

  @Override
  public double doubleValue() {
    return (double) value;
  }

  @Override
  public String toString() {
    return Long.toString(value);
  }
}
