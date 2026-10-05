package java.util;

import java.io.Serializable;
import java.security.SecureRandom;

/** Immutable 128-bit UUID with Java's signed-long ordering and Web Crypto v4 generation. */
public final class UUID implements Serializable, Comparable<UUID> {
  private static final long serialVersionUID = -4856846361193249489L;
  private final long mostSigBits;
  private final long leastSigBits;

  public UUID(long mostSigBits, long leastSigBits) {
    this.mostSigBits = mostSigBits;
    this.leastSigBits = leastSigBits;
  }

  public static UUID randomUUID() {
    byte[] bytes = new byte[16];
    new SecureRandom().nextBytes(bytes);
    return fromBytes(bytes, 4);
  }

  private static UUID fromBytes(byte[] bytes, int version) {
    bytes[6] = (byte) ((bytes[6] & 15) | (version << 4));
    bytes[8] = (byte) ((bytes[8] & 63) | 128);
    long high = 0, low = 0;
    for (int i = 0; i < 8; i++) {
      high = (high << 8) | (bytes[i] & 255L);
      low = (low << 8) | (bytes[i + 8] & 255L);
    }
    return new UUID(high, low);
  }

  public static UUID fromString(String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    if (name.length() > 36) {
      throw new IllegalArgumentException("UUID string too large");
    }
    int[] boundaries = new int[6];
    boundaries[0] = -1;
    boundaries[5] = name.length();
    int found = 0;
    for (int i = 0; i < name.length(); i++) {
      if (name.charAt(i) == '-') {
        if (++found > 4) {
          throw new IllegalArgumentException("Invalid UUID string: " + name);
        }
        boundaries[found] = i;
      }
    }
    if (found != 4) {
      throw new IllegalArgumentException("Invalid UUID string: " + name);
    }
    long[] fields = new long[5];
    for (int field = 0; field < 5; field++) {
      int start = boundaries[field] + 1, end = boundaries[field + 1];
      if (start < end && name.charAt(start) == '+') {
        start++;
      }
      if (start == end) {
        throw new NumberFormatException("Empty UUID field");
      }
      long value = 0;
      for (int i = start; i < end; i++) {
        int digit = Character.digit(name.charAt(i), 16);
        if (digit < 0 || value > (Long.MAX_VALUE - digit) / 16) {
          throw new NumberFormatException("Invalid UUID field");
        }
        value = value * 16 + digit;
      }
      fields[field] = value;
    }
    // The JVM accepts short and oversized fields (within its total-length/long limits),
    // masking each field to its RFC width rather than silently tightening this API.
    return new UUID(((fields[0] & 0xffffffffL) << 32) | ((fields[1] & 0xffffL) << 16)
        | (fields[2] & 0xffffL), ((fields[3] & 0xffffL) << 48) | (fields[4] & 0xffffffffffffL));
  }

  public long getMostSignificantBits() { return mostSigBits; }
  public long getLeastSignificantBits() { return leastSigBits; }
  public int version() { return (int) ((mostSigBits >>> 12) & 15); }
  public int variant() {
    if (leastSigBits >= 0) { return 0; }
    return (leastSigBits & 0x4000000000000000L) == 0 ? 2
        : (leastSigBits & 0x2000000000000000L) == 0 ? 6 : 7;
  }

  private void requireTimeBased() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
  }

  public long timestamp() {
    requireTimeBased();
    return ((mostSigBits & 0xfffL) << 48) | (((mostSigBits >>> 16) & 0xffffL) << 32)
        | (mostSigBits >>> 32);
  }

  public int clockSequence() {
    requireTimeBased();
    return (int) ((leastSigBits >>> 48) & 0x3fff);
  }

  public long node() {
    requireTimeBased();
    return leastSigBits & 0xffffffffffffL;
  }

  @Override
  public int compareTo(UUID other) {
    if (other == null) {
      throw new NullPointerException();
    }
    int high = Long.compare(mostSigBits, other.mostSigBits);
    return high != 0 ? high : Long.compare(leastSigBits, other.leastSigBits);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof UUID)) { return false; }
    UUID value = (UUID) other;
    return mostSigBits == value.mostSigBits && leastSigBits == value.leastSigBits;
  }

  @Override
  public int hashCode() {
    long bits = mostSigBits ^ leastSigBits;
    return (int) (bits ^ (bits >>> 32));
  }

  @Override
  public String toString() {
    char[] text = new char[36];
    int position = 0;
    for (int digit = 0; digit < 32; digit++) {
      if (digit == 8 || digit == 12 || digit == 16 || digit == 20) { text[position++] = '-'; }
      long bits = digit < 16 ? mostSigBits : leastSigBits;
      int nibble = (int) ((bits >>> ((15 - (digit & 15)) * 4)) & 15);
      text[position++] = "0123456789abcdef".charAt(nibble);
    }
    return new String(text);
  }
}
