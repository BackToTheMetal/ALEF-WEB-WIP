package java.security;

import java.util.Random;
import javaemul.internal.JsUtils;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Browser cryptographic randomness backed exclusively by Web Crypto getRandomValues.
 * The browser owns entropy collection; explicit application seeding and provider selection
 * are not supported. No output is produced using java.util.Random's LCG.
 */
public class SecureRandom extends Random {
  public SecureRandom() {
    super(0);
  }

  @Override
  public void nextBytes(byte[] bytes) {
    if (bytes == null) {
      throw new NullPointerException();
    }
    Crypto crypto = getCrypto();
    if (crypto == null) {
      throw new UnsupportedOperationException("Web Crypto getRandomValues is unavailable");
    }
    // Web Crypto caps each request at 65536 bytes.
    for (int start = 0; start < bytes.length; start += 65536) {
      int size = Math.min(65536, bytes.length - start);
      Uint8Array values = new Uint8Array(size);
      crypto.getRandomValues(values);
      // Typed arrays expose indexed numeric elements and a length like JavaScript arrays.
      // An unchecked view avoids fabricating or depending on Java array type metadata.
      int[] view = JsUtils.uncheckedCast(values);
      for (int i = 0; i < size; i++) {
        bytes[start + i] = (byte) view[i];
      }
    }
  }

  public byte[] generateSeed(int numBytes) {
    if (numBytes < 0) {
      throw new IllegalArgumentException("numBytes must not be negative");
    }
    byte[] bytes = new byte[numBytes];
    nextBytes(bytes);
    return bytes;
  }

  public static byte[] getSeed(int numBytes) {
    return new SecureRandom().generateSeed(numBytes);
  }

  public String getAlgorithm() {
    return "WebCrypto";
  }

  @Override
  public void setSeed(long seed) {
    // java.util.Random's constructor invokes this method with zero.
    if (seed != 0) {
      throw new UnsupportedOperationException("Web Crypto does not expose application seeding");
    }
  }

  public void setSeed(byte[] seed) {
    if (seed == null) {
      throw new NullPointerException();
    }
    throw new UnsupportedOperationException("Web Crypto does not expose application seeding");
  }

  @Override
  protected final int next(int bits) {
    if (bits < 0 || bits > 32) {
      throw new IllegalArgumentException("bits must be between 0 and 32");
    }
    if (bits == 0) {
      return 0;
    }
    byte[] bytes = new byte[4];
    nextBytes(bytes);
    int value = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16
        | (bytes[2] & 255) << 8 | bytes[3] & 255;
    return value >>> (32 - bits);
  }

  // J2CL Random uses a private nextInternal() helper, unlike the JVM's virtual next().
  // Override every public generator so none of them can enter that LCG path.
  @Override
  public boolean nextBoolean() { return next(1) != 0; }

  @Override
  public int nextInt() { return next(32); }

  @Override
  public int nextInt(int bound) {
    if (bound <= 0) {
      throw new IllegalArgumentException("bound must be positive");
    }
    int mask = bound - 1;
    int value = next(31);
    if ((bound & mask) == 0) {
      return (int) ((bound * (long) value) >> 31);
    }
    int result = value % bound;
    while (value - result + mask < 0) {
      value = next(31);
      result = value % bound;
    }
    return result;
  }

  @Override
  public long nextLong() { return ((long) next(32) << 32) + next(32); }

  @Override
  public float nextFloat() { return next(24) / 16777216.0f; }

  @Override
  public double nextDouble() {
    return (((long) next(26) << 27) + next(27)) / 9007199254740992.0;
  }

  @Override
  public double nextGaussian() {
    double first, second, magnitude;
    do {
      first = 2 * nextDouble() - 1;
      second = 2 * nextDouble() - 1;
      magnitude = first * first + second * second;
    } while (magnitude >= 1 || magnitude == 0);
    return first * Math.sqrt(-2 * Math.log(magnitude) / magnitude);
  }

  @JsProperty(namespace = JsPackage.GLOBAL, name = "globalThis.crypto")
  private static native Crypto getCrypto();

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  private static class Crypto {
    public native Uint8Array getRandomValues(Uint8Array values);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Uint8Array")
  private static class Uint8Array {
    public Uint8Array(int length) {}
  }
}
