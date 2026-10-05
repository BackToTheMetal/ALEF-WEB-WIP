package mpsj2cl.runtime;

import java.util.UUID;

/** Exercises every UTF-16 char and malformed field boundaries against the JVM parser. */
public final class UuidParseOracle {
  private static long hash = 0xcbf29ce484222325L;
  private static int count;

  private static void test(String text) {
    String result;
    try {
      result = UUID.fromString(text).toString();
    } catch (Exception failure) {
      result = failure.getClass().getName();
    }
    count++;
    for (int i = 0; i < result.length(); i++) {
      hash = (hash ^ result.charAt(i)) * 0x100000001b3L;
    }
    hash = (hash ^ 0xffff) * 0x100000001b3L;
  }

  public static void main(String[] args) {
    for (int c = 0; c <= 65535; c++) {
      test((char) c + "-1-2-3-4");
      test("1-2-3-4-" + (char) c);
    }
    String[] pieces = {"", "+", "++", "-", "a", "f", "F", "0", "7", "8", "9", " ", "\u0000", "\uff46", "\u0662"};
    for (String piece : pieces) {
      for (int length = 0; length < 38; length++) {
        String value = piece.repeat(length);
        test(value + "-1-2-3-4");
        test("1-" + value + "-2-3-4");
        test("1-2-3-4-" + value);
      }
    }
    System.out.println(count + ":" + Long.toHexString(hash));
  }
}
