#!/usr/bin/env python3
"""Generate the small JRE API extensions needed by unchanged MPS sources."""
import argparse
from pathlib import Path
from make_api_patch import ROOT, DIRECTORY, preimage

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    edits = {
      'jre/java/java/io/InputStream.java': ('public abstract class InputStream extends Object implements Closeable {', '''public abstract class InputStream extends Object implements Closeable {
  /** Consume the remaining bytes, without closing the stream. */
  public byte[] readAllBytes() throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[8192];
    int count;
    while ((count = read(buffer, 0, buffer.length)) != -1) {
      if (count == 0) {
        int value = read();
        if (value == -1) break;
        result.write(value);
      } else {
        result.write(buffer, 0, count);
      }
    }
    return result.toByteArray();
  }
'''),
      'jre/java/module-info.java': ('  exports java.util;', '  exports java.util;\n  exports java.util.regex;'),
      'jre/java/java/lang/Long.java': ('  public static long parseLong(String s) throws NumberFormatException {', '''  public static long parseUnsignedLong(String s) {
    return parseUnsignedLong(s, 10);
  }

  public static long parseUnsignedLong(String s, int radix) {
    if (s == null || s.isEmpty() || s.charAt(0) == '-' || radix < 2 || radix > 36) {
      throw new NumberFormatException("Invalid unsigned long: " + s);
    }
    int first = s.charAt(0) == '+' ? 1 : 0;
    if (first == s.length()) throw new NumberFormatException("Missing unsigned digits");
    StringBuilder normalized = new StringBuilder(s.length());
    for (int index = first; index < s.length(); index++) {
      int digit = Character.digit(s.charAt(index), radix);
      if (digit < 0) throw new NumberFormatException("Invalid unsigned digit: " + s);
      normalized.append(Character.forDigit(digit, radix));
    }
    java.math.BigInteger value = new java.math.BigInteger(normalized.toString(), radix);
    if (value.signum() < 0 || value.bitLength() > 64) {
      throw new NumberFormatException("Unsigned long overflow: " + s);
    }
    return value.longValue();
  }

  public static String toUnsignedString(long value) {
    return toUnsignedString(value, 10);
  }

  public static String toUnsignedString(long value, int radix) {
    if (radix < 2 || radix > 36) radix = 10;
    if (value >= 0) return toString(value, radix);
    return java.math.BigInteger.valueOf(value).add(java.math.BigInteger.ONE.shiftLeft(64)).toString(radix);
  }

  public static long parseLong(String s) throws NumberFormatException {'''),
      'jre/java/java/io/Reader.java': ('public abstract class Reader {', 'public abstract class Reader implements Closeable {'),
      'jre/java/java/util/List.java': ('public interface List<E> extends Collection<E> {', '''public interface List<E> extends Collection<E> {
  default E getFirst() {
    if (isEmpty()) throw new NoSuchElementException();
    return get(0);
  }

  default E getLast() {
    if (isEmpty()) throw new NoSuchElementException();
    return get(size() - 1);
  }

  default void addFirst(E element) { add(0, element); }
  default void addLast(E element) { add(element); }

  default E removeFirst() {
    if (isEmpty()) throw new NoSuchElementException();
    return remove(0);
  }

  default E removeLast() {
    if (isEmpty()) throw new NoSuchElementException();
    return remove(size() - 1);
  }
'''),
      'jre/java/java/util/stream/Stream.java': ('public interface Stream<T> extends BaseStream<T, Stream<T>> {', '''public interface Stream<T> extends BaseStream<T, Stream<T>> {
  default java.util.List<T> toList() {
    return java.util.Collections.unmodifiableList(collect(Collectors.toList()));
  }
'''),
      'jre/java/java/io/PrintStream.java': ('  public void print(boolean x) {', '''  public PrintStream format(String format, Object... args) {
    print(javaemul.internal.MpsFormatter.format(format, args));
    return this;
  }

  public PrintStream format(java.util.Locale locale, String format, Object... args) {
    print(javaemul.internal.MpsFormatter.format(locale, format, args));
    return this;
  }

  public PrintStream printf(String format, Object... args) {
    return format(format, args);
  }

  public PrintStream printf(java.util.Locale locale, String format, Object... args) {
    return format(locale, format, args);
  }

  public void print(boolean x) {'''),
    }
    for relative, (before_fragment, after_fragment) in edits.items():
      before, exists = preimage(ROOT / '.work/j2cl-20260402', relative)
      if not exists or before.count(before_fragment) != 1:
        raise SystemExit('JRE baseline fragment changed: ' + relative)
      after = before.replace(before_fragment, after_fragment)
      if relative == 'jre/java/module-info.java':
        after = after.replace('  exports java.lang.reflect;', '  exports java.lang.reflect;\n  exports java.lang.ref;')
      output = DIRECTORY / relative
      if args.check:
        if not output.exists() or output.read_text(encoding='utf-8') != after:
          raise SystemExit('Stale JRE source: ' + relative)
      else:
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(after, encoding='utf-8', newline='\n')
    print('Basic JRE API replacements: ' + str(len(edits)))

if __name__ == '__main__':
    main()
