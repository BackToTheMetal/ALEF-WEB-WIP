package mpsj2cl.apibasics;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/** JVM/browser differential checks for the small JRE API additions. */
@Deprecated(since = "test", forRemoval = false)
public final class CoreApiTest {
  public static String run() throws Exception {
    readRemainingBytes();
    StringBuilder result = new StringBuilder();
    long[] values = {0, 1, 9, 10, 35, 255, 65535, Integer.MAX_VALUE, 1L << 32,
        Long.MAX_VALUE, Long.MIN_VALUE, -1, -2, -1234567890123456L};
    for (int radix = 2; radix <= 36; radix++) {
      for (long value : values) {
        String text = Long.toUnsignedString(value, radix);
        long parsed = Long.parseUnsignedLong(text, radix);
        if (parsed != value) throw new AssertionError("unsigned round trip");
        result.append(radix).append(':').append(value).append('=').append(text).append('\n');
      }
    }
    String[] samples = {"0", "+0", "-0", "-1", "+", "", "18446744073709551615",
        "18446744073709551616", "00018446744073709551615", "123x", " 1", "١٢٣", "１２３", null};
    for (String text : samples) {
      result.append("parse ").append(text).append('=');
      try { result.append(Long.parseUnsignedLong(text)); }
      catch (NumberFormatException expected) { result.append("error"); }
      result.append('\n');
    }
    for (int radix : new int[] {-1, 0, 1, 37, 100}) {
      result.append("radix ").append(radix).append('=').append(Long.toUnsignedString(-1, radix)).append('\n');
      try { Long.parseUnsignedLong("0", radix); throw new AssertionError("bad parse radix"); }
      catch (NumberFormatException expected) {}
    }
    BufferedReader reader = new BufferedReader(new StringReader("one\ntwo"));
    if (!(reader instanceof Closeable)) throw new AssertionError("Reader must be Closeable");
    try (reader) {
      if (!"one".equals(reader.readLine()) || !"two".equals(reader.readLine()) || reader.readLine() != null) {
        throw new AssertionError("buffered reader");
      }
    }
    LogRecord record = new LogRecord(Level.INFO, "message");
    Object[] parameters = {new Object()};
    if (record.getParameters() != null) throw new AssertionError("initial log parameters");
    record.setParameters(parameters);
    if (record.getParameters() != parameters) throw new AssertionError("log parameter identity");
    record.setParameters(null);
    if (record.getParameters() != null) throw new AssertionError("clear log parameters");
    List<String> valuesList = new ArrayList<>();
    try { valuesList.getFirst(); throw new AssertionError("empty first"); }
    catch (NoSuchElementException expected) {}
    try { valuesList.getLast(); throw new AssertionError("empty last"); }
    catch (NoSuchElementException expected) {}
    valuesList.addFirst("middle");
    valuesList.addFirst("first");
    valuesList.addLast("last");
    if (!"first".equals(valuesList.getFirst()) || !"last".equals(valuesList.getLast())) throw new AssertionError("list ends");
    if (!"first".equals(valuesList.removeFirst()) || !"last".equals(valuesList.removeLast())) throw new AssertionError("remove ends");
    List<String> snapshot = Arrays.asList("a", null, "b").stream().toList();
    if (!snapshot.equals(Arrays.asList("a", null, "b"))) throw new AssertionError("ordered stream with null");
    try { snapshot.set(0, "c"); throw new AssertionError("mutable stream result"); }
    catch (UnsupportedOperationException expected) {}
    result.append("streams/logging:PASS\n");
    return result.toString();
  }

  public static void main(String[] args) throws Exception { System.out.print(run()); }

  private static void readRemainingBytes() throws Exception {
    byte[] expected = new byte[20003];
    for (int i = 0; i < expected.length; i++) expected[i] = (byte) (i * 73);
    final boolean[] closed = {false};
    java.io.InputStream input = new java.io.InputStream() {
      int offset;
      boolean zero = true;
      @Override public int read() { return offset == expected.length ? -1 : expected[offset++] & 255; }
      @Override public int read(byte[] bytes, int start, int length) {
        if (zero) { zero = false; return 0; }
        if (offset == expected.length) return -1;
        int count = Math.min(137, Math.min(length, expected.length - offset));
        System.arraycopy(expected, offset, bytes, start, count);
        offset += count;
        return count;
      }
      @Override public void close() { closed[0] = true; }
    };
    if (!Arrays.equals(expected, input.readAllBytes()) || input.readAllBytes().length != 0 || closed[0]) {
      throw new AssertionError("readAllBytes must preserve chunks, EOF and stream ownership");
    }
    java.io.IOException failure = new java.io.IOException("read failure");
    java.io.InputStream broken = new java.io.InputStream() {
      @Override public int read() throws java.io.IOException { throw failure; }
    };
    try { broken.readAllBytes(); throw new AssertionError("read failure lost"); }
    catch (java.io.IOException expectedFailure) { if (expectedFailure != failure) throw new AssertionError("read failure identity"); }
  }
}
