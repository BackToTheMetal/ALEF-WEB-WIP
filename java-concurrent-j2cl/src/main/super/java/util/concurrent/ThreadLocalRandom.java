package java.util.concurrent;

import java.util.Random;

/** One ordinary random generator for the browser main thread. */
public class ThreadLocalRandom extends Random {
  private static final ThreadLocalRandom INSTANCE = new ThreadLocalRandom();
  private boolean initialized;
  private ThreadLocalRandom() { initialized = true; }
  public static ThreadLocalRandom current() { return INSTANCE; }
  @Override public void setSeed(long seed) {
    if (initialized) throw new UnsupportedOperationException("ThreadLocalRandom cannot be reseeded");
    super.setSeed(seed);
  }
  public int nextInt(int origin, int bound) {
    if (origin >= bound) throw new IllegalArgumentException();
    int width = bound - origin;
    if (width > 0) return origin + nextInt(width);
    int value;
    do { value = nextInt(); } while (value < origin || value >= bound);
    return value;
  }
  public long nextLong(long bound) {
    if (bound <= 0) throw new IllegalArgumentException();
    long bits, value;
    do { bits = nextLong() >>> 1; value = bits % bound; } while (bits - value + (bound - 1) < 0);
    return value;
  }
  public long nextLong(long origin, long bound) {
    if (origin >= bound) throw new IllegalArgumentException();
    long width = bound - origin;
    if (width > 0) return origin + nextLong(width);
    long value;
    do { value = nextLong(); } while (value < origin || value >= bound);
    return value;
  }
  public double nextDouble(double bound) { return nextDouble(0, bound); }
  public double nextDouble(double origin, double bound) {
    if (!(origin < bound) || Double.isInfinite(origin) || Double.isInfinite(bound)) throw new IllegalArgumentException();
    double fraction = nextDouble();
    double value = origin * (1 - fraction) + bound * fraction;
    return value < bound ? value : Math.nextDown(bound);
  }
}
