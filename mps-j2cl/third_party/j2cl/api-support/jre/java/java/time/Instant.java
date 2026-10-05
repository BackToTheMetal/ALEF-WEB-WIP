package java.time;

import java.io.Serializable;

/**
 * Immutable seconds/nanoseconds timestamp. The browser profile supplies arithmetic,
 * ordering, epoch conversion and ISO output; it does not supply the Temporal API.
 * now() uses the browser wall clock, whose resolution may be one millisecond.
 */
public final class Instant implements Comparable<Instant>, Serializable {
  private static final long MIN_SECOND = -31557014167219200L;
  private static final long MAX_SECOND = 31556889864403199L;
  public static final Instant EPOCH = new Instant(0, 0);
  public static final Instant MIN = new Instant(MIN_SECOND, 0);
  public static final Instant MAX = new Instant(MAX_SECOND, 999999999);
  private final long seconds;
  private final int nanos;

  private Instant(long seconds, int nanos) { this.seconds = seconds; this.nanos = nanos; }

  public static Instant now() { return ofEpochMilli(System.currentTimeMillis()); }
  public static Instant ofEpochSecond(long second) { return ofEpochSecond(second, 0); }
  public static Instant ofEpochSecond(long second, long nanoAdjustment) {
    long value = Math.addExact(second, Math.floorDiv(nanoAdjustment, 1000000000L));
    if (value < MIN_SECOND || value > MAX_SECOND) throw new DateTimeException("Instant exceeds minimum or maximum instant");
    int nano = (int) Math.floorMod(nanoAdjustment, 1000000000L);
    return value == 0 && nano == 0 ? EPOCH : new Instant(value, nano);
  }
  public static Instant ofEpochMilli(long milli) {
    return ofEpochSecond(Math.floorDiv(milli, 1000), Math.floorMod(milli, 1000) * 1000000);
  }
  public long getEpochSecond() { return seconds; }
  public int getNano() { return nanos; }
  public long toEpochMilli() {
    if (seconds < 0 && nanos > 0) {
      return Math.addExact(Math.multiplyExact(seconds + 1, 1000), nanos / 1000000 - 1000);
    }
    return Math.addExact(Math.multiplyExact(seconds, 1000), nanos / 1000000);
  }
  public Instant plusSeconds(long amount) { return ofEpochSecond(Math.addExact(seconds, amount), nanos); }
  public Instant plusMillis(long amount) {
    return ofEpochSecond(Math.addExact(seconds, amount / 1000), nanos + (amount % 1000) * 1000000);
  }
  public Instant plusNanos(long amount) {
    return ofEpochSecond(Math.addExact(seconds, amount / 1000000000), nanos + amount % 1000000000);
  }
  public Instant minusSeconds(long amount) { return amount == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1) : plusSeconds(-amount); }
  public Instant minusMillis(long amount) { return amount == Long.MIN_VALUE ? plusMillis(Long.MAX_VALUE).plusMillis(1) : plusMillis(-amount); }
  public Instant minusNanos(long amount) { return amount == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1) : plusNanos(-amount); }
  public Instant plus(Duration amount) { return plusSeconds(amount.getSeconds()).plusNanos(amount.getNano()); }
  public Instant minus(Duration amount) { return minusSeconds(amount.getSeconds()).minusNanos(amount.getNano()); }
  public boolean isAfter(Instant other) { return compareTo(other) > 0; }
  public boolean isBefore(Instant other) { return compareTo(other) < 0; }
  @Override public int compareTo(Instant other) {
    int comparison = Long.compare(seconds, other.seconds);
    return comparison != 0 ? comparison : Integer.compare(nanos, other.nanos);
  }
  @Override public boolean equals(Object other) {
    return other instanceof Instant && seconds == ((Instant) other).seconds && nanos == ((Instant) other).nanos;
  }
  @Override public int hashCode() { return (int) (seconds ^ (seconds >>> 32)) + 51 * nanos; }

  @Override public String toString() {
    // Civil date conversion on the proleptic Gregorian calendar, including MIN/MAX.
    long day = Math.floorDiv(seconds, 86400L) + 719468;
    long era = Math.floorDiv(day, 146097L);
    long doe = day - era * 146097;
    long yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365;
    long year = yoe + era * 400;
    long doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
    int mp = (int) ((5 * doy + 2) / 153);
    int date = (int) (doy - (153 * mp + 2) / 5 + 1);
    int month = mp + (mp < 10 ? 3 : -9);
    if (month <= 2) year++;
    String y = Long.toString(Math.abs(year));
    while (y.length() < 4) y = "0" + y;
    if (year < 0) y = "-" + y;
    else if (year > 9999) y = "+" + y;
    int sod = (int) Math.floorMod(seconds, 86400L);
    String value = y + "-" + two(month) + "-" + two(date) + "T" + two(sod / 3600) + ":" + two(sod / 60 % 60) + ":" + two(sod % 60);
    if (nanos != 0) {
      String fraction = Integer.toString(nanos + 1000000000).substring(1);
      int digits = nanos % 1000000 == 0 ? 3 : nanos % 1000 == 0 ? 6 : 9;
      value += "." + fraction.substring(0, digits);
    }
    return value + "Z";
  }
  private static String two(int value) { return value < 10 ? "0" + value : Integer.toString(value); }
}
