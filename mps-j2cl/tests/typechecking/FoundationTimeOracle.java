package mpsj2cl.typechecking;

import java.time.Duration;
import java.time.Instant;

/** The same observations run against Java 21 and the browser time sources. */
public final class FoundationTimeOracle {
  public static void main(String[] args) { System.out.print(run()); }
  public static String run() {
    StringBuilder out = new StringBuilder();
    long[] values = {Long.MIN_VALUE, Long.MIN_VALUE + 1000, -62167219200000L, -2208988800000L,
        -1001001, -1001, -1000, -999, -1, 0, 1, 999, 1000, 1001, 951782400123L,
        253402300799999L, Long.MAX_VALUE - 1000, Long.MAX_VALUE};
    for (long millis : values) {
      Instant value = Instant.ofEpochMilli(millis);
      out.append(value).append('|').append(value.getEpochSecond()).append('|').append(value.getNano())
          .append('|').append(value.toEpochMilli()).append('|').append(value.hashCode()).append('\n');
    }
    out.append(Instant.MIN).append('\n').append(Instant.MAX).append('\n');
    Duration negatedEdge = Duration.ofSeconds(Long.MIN_VALUE, 1).negated();
    out.append(negatedEdge.getSeconds()).append('|').append(negatedEdge.getNano()).append('\n');
    long seed = 73;
    for (int i = 0; i < 500; i++) {
      seed = seed * 6364136223846793005L + 1442695040888963407L;
      long seconds = seed % 31556889864403100L;
      int nanos = (int) ((seed >>> 33) % 1000000000);
      long adjust = seed % 9000000000L;
      Instant value = Instant.ofEpochSecond(seconds, nanos);
      Instant changed = value.plusNanos(adjust);
      Duration difference = Duration.between(value, changed);
      out.append(value).append('|').append(changed).append('|').append(difference.getSeconds()).append('|')
          .append(difference.getNano()).append('|').append(difference.toNanos()).append('|')
          .append(difference.toMillis()).append('|').append(difference.toMinutes()).append('|')
          .append(difference.hashCode()).append('|').append(Integer.signum(value.compareTo(changed))).append('\n');
      if (!changed.minusNanos(adjust).equals(value)) throw new AssertionError("arithmetic round trip");
    }
    for (long millis : values) {
      Duration duration = Duration.ofMillis(millis);
      out.append(duration.getSeconds()).append('|').append(duration.getNano()).append('|')
          .append(duration.toMillis()).append('|').append(duration.toSeconds()).append('|')
          .append(duration.toMinutes()).append('|').append(duration.toHours()).append('|')
          .append(duration.toDays()).append('\n');
    }
    return out.toString();
  }
}
