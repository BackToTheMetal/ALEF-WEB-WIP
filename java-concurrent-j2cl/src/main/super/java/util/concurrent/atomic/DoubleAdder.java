package java.util.concurrent.atomic;

/** A plain double accumulator for the main-thread execution profile. */
public class DoubleAdder extends Number implements java.io.Serializable {
  private double value;

  public DoubleAdder() {}

  public void add(double x) {
    value += x;
  }

  public double sum() {
    return value;
  }

  public void reset() {
    value = 0;
  }

  public double sumThenReset() {
    double previous = value;
    reset();
    return previous;
  }

  @Override
  public int intValue() {
    return (int) value;
  }

  @Override
  public long longValue() {
    return (long) value;
  }

  @Override
  public float floatValue() {
    return (float) value;
  }

  @Override
  public double doubleValue() {
    return value;
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }
}
