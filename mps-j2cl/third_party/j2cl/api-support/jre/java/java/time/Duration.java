package java.time;

import java.io.Serializable;

/** Immutable duration arithmetic used by browser MPS settings and timeouts. */
public final class Duration implements Comparable<Duration>, Serializable {
  public static final Duration ZERO = new Duration(0, 0);
  private final long seconds;
  private final int nanos;
  private Duration(long seconds, int nanos) { this.seconds = seconds; this.nanos = nanos; }
  public static Duration ofSeconds(long seconds) { return ofSeconds(seconds, 0); }
  public static Duration ofSeconds(long seconds, long nanoAdjustment) {
    long s = Math.addExact(seconds, Math.floorDiv(nanoAdjustment, 1000000000L));
    int n = (int) Math.floorMod(nanoAdjustment, 1000000000L);
    return s == 0 && n == 0 ? ZERO : new Duration(s, n);
  }
  public static Duration ofMillis(long amount) { return ofSeconds(amount / 1000, amount % 1000 * 1000000); }
  public static Duration ofNanos(long amount) { return ofSeconds(0, amount); }
  public static Duration ofMinutes(long amount) { return ofSeconds(Math.multiplyExact(amount, 60)); }
  public static Duration ofHours(long amount) { return ofSeconds(Math.multiplyExact(amount, 3600)); }
  public static Duration ofDays(long amount) { return ofSeconds(Math.multiplyExact(amount, 86400)); }
  public static Duration between(Instant start, Instant end) {
    return ofSeconds(Math.subtractExact(end.getEpochSecond(), start.getEpochSecond()), (long) end.getNano() - start.getNano());
  }
  public long getSeconds() { return seconds; }
  public int getNano() { return nanos; }
  public boolean isZero() { return seconds == 0 && nanos == 0; }
  public boolean isNegative() { return seconds < 0; }
  public long toSeconds() { return seconds; }
  public long toMinutes() { return seconds / 60; }
  public long toHours() { return seconds / 3600; }
  public long toDays() { return seconds / 86400; }
  public long toMillis() {
    long s = seconds; int n = nanos;
    if (s < 0) { s++; n -= 1000000000; }
    return Math.addExact(Math.multiplyExact(s, 1000), n / 1000000);
  }
  public long toNanos() {
    long s = seconds; int n = nanos;
    if (s < 0) { s++; n -= 1000000000; }
    return Math.addExact(Math.multiplyExact(s, 1000000000), n);
  }
  public Duration plus(Duration other) { return ofSeconds(Math.addExact(seconds, other.seconds), (long) nanos + other.nanos); }
  public Duration minus(Duration other) { return ofSeconds(Math.subtractExact(seconds, other.seconds), (long) nanos - other.nanos); }
  public Duration plusSeconds(long amount) { return ofSeconds(Math.addExact(seconds, amount), nanos); }
  public Duration plusMillis(long amount) { return plus(ofMillis(amount)); }
  public Duration plusNanos(long amount) { return plus(ofNanos(amount)); }
  public Duration negated() {
    return nanos == 0 ? ofSeconds(Math.negateExact(seconds)) : ofSeconds(~seconds, 1000000000L - nanos);
  }
  public Duration abs() { return isNegative() ? negated() : this; }
  @Override public int compareTo(Duration other) { int c = Long.compare(seconds, other.seconds); return c == 0 ? Integer.compare(nanos, other.nanos) : c; }
  @Override public boolean equals(Object other) { return other instanceof Duration && compareTo((Duration) other) == 0; }
  @Override public int hashCode() { return (int) (seconds ^ (seconds >>> 32)) + 51 * nanos; }
}
