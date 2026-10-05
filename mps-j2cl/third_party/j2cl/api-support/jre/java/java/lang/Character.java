/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.lang;

import static javaemul.internal.InternalPreconditions.checkArgument;

import java.io.Serializable;
import javaemul.internal.CharacterData;
import javaemul.internal.annotations.HasNoSideEffects;

/**
 * Wraps a native <code>char</code> as an object.
 *
 * <p>Unicode classification and simple case mappings use generated Java 21 data.
 */
public final class Character implements Comparable<Character>, Serializable {

  public static int digit(int codePoint, int radix) {
    return CharacterData.digit(codePoint, radix);
  }

  public static int toTitleCase(int c) {
    return CharacterData.toTitleCase(c);
  }

  public static boolean isJavaIdentifierPart(int codePoint) {
    return (CharacterData.properties(codePoint) & 256) != 0;
  }

  public static boolean isJavaIdentifierPart(char c) {
    return isJavaIdentifierPart((int) c);
  }

  public static boolean isJavaIdentifierStart(int codePoint) {
    return (CharacterData.properties(codePoint) & 128) != 0;
  }

  public static boolean isJavaIdentifierStart(char c) {
    return isJavaIdentifierStart((int) c);
  }

  public static boolean isTitleCase(int codePoint) {
    return (CharacterData.properties(codePoint) & 16) != 0;
  }

  public static boolean isUpperCase(int codePoint) {
    return (CharacterData.properties(codePoint) & 8) != 0;
  }

  public static boolean isLowerCase(int codePoint) {
    return (CharacterData.properties(codePoint) & 4) != 0;
  }

  public static boolean isLetterOrDigit(int codePoint) {
    return (CharacterData.properties(codePoint) & 3) != 0;
  }

  public static boolean isLetter(int codePoint) {
    return (CharacterData.properties(codePoint) & 2) != 0;
  }

  public static boolean isDigit(int codePoint) {
    return (CharacterData.properties(codePoint) & 1) != 0;
  }
  /**
   * Helper class to share code between implementations, by making a char array look like a
   * CharSequence.
   */
  static class CharSequenceAdapter implements CharSequence {
    private char[] charArray;
    private int start;
    private int end;

    public CharSequenceAdapter(char[] charArray) {
      this(charArray, 0, charArray.length);
    }

    public CharSequenceAdapter(char[] charArray, int start, int end) {
      this.charArray = charArray;
      this.start = start;
      this.end = end;
    }

    @Override
    public char charAt(int index) {
      return charArray[index + start];
    }

    @Override
    public int length() {
      return end - start;
    }

    @Override
    public java.lang.CharSequence subSequence(int start, int end) {
      return new CharSequenceAdapter(charArray, this.start + start, this.start + end);
    }
  }

  /** Use nested class to avoid clinit on outer. */
  private static class BoxedValues {
    private static final Character[] boxedValues;

    static {
      // Box values according to JLS - from \u0000 to \u007f
      Character[] values = new Character[128];
      for (char i = 0; i < 128; i++) {
        values[i] = new Character(i);
      }
      boxedValues = values;
    }

    @HasNoSideEffects
    private static Character get(char c) {
      return boxedValues[c];
    }
  }

  public static final Class<Character> TYPE = char.class;
  public static final int MIN_RADIX = 2;

  public static final int MAX_RADIX = 36;
  public static final char MIN_VALUE = '\u0000';

  public static final char MAX_VALUE = '\uFFFF';
  public static final char MIN_SURROGATE = '\uD800';
  public static final char MAX_SURROGATE = '\uDFFF';
  public static final char MIN_LOW_SURROGATE = '\uDC00';
  public static final char MAX_LOW_SURROGATE = '\uDFFF';
  public static final char MIN_HIGH_SURROGATE = '\uD800';

  public static final char MAX_HIGH_SURROGATE = '\uDBFF';
  public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x10000;
  public static final int MIN_CODE_POINT = 0x0000;

  public static final int MAX_CODE_POINT = 0x10FFFF;

  public static final int SIZE = 16;
  public static final int BYTES = SIZE / Byte.SIZE;

  public static int charCount(int codePoint) {
    return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT ? 2 : 1;
  }

  public static int codePointAt(char[] a, int index) {
    return codePointAt(new CharSequenceAdapter(a), index, a.length);
  }

  public static int codePointAt(char[] a, int index, int limit) {
    return codePointAt(new CharSequenceAdapter(a), index, limit);
  }

  public static int codePointAt(CharSequence seq, int index) {
    return codePointAt(seq, index, seq.length());
  }

  public static int codePointBefore(char[] a, int index) {
    return codePointBefore(new CharSequenceAdapter(a), index, 0);
  }

  public static int codePointBefore(char[] a, int index, int start) {
    return codePointBefore(new CharSequenceAdapter(a), index, start);
  }

  public static int codePointBefore(CharSequence cs, int index) {
    return codePointBefore(cs, index, 0);
  }

  public static int codePointCount(char[] a, int offset, int count) {
    return codePointCount(new CharSequenceAdapter(a), offset, offset + count);
  }

  public static int codePointCount(CharSequence seq, int beginIndex, int endIndex) {
    int count = 0;
    for (int idx = beginIndex; idx < endIndex; ) {
      char ch = seq.charAt(idx++);
      if (isHighSurrogate(ch) && idx < endIndex && (isLowSurrogate(seq.charAt(idx)))) {
        // skip the second char of surrogate pairs
        ++idx;
      }
      ++count;
    }
    return count;
  }

  public static int compare(char x, char y) {
    // JLS specifies that the chars are promoted to int before subtraction.
    return x - y;
  }

  public static int digit(char c, int radix) {
    return CharacterData.digit(c, radix);
  }

  public static char forDigit(int digit, int radix) {
    if (radix < MIN_RADIX || radix > MAX_RADIX) {
      return 0;
    }

    if (digit < 0 || digit >= radix) {
      return 0;
    }

    return forDigit(digit);
  }

  public static int hashCode(char c) {
    return c;
  }

  public static boolean isBmpCodePoint(int codePoint) {
    return codePoint >= MIN_VALUE && codePoint <= MAX_VALUE;
  }

  public static boolean isDigit(char c) {
    return isDigit((int) c);
  }

  public static boolean isHighSurrogate(char ch) {
    return ch >= MIN_HIGH_SURROGATE && ch <= MAX_HIGH_SURROGATE;
  }

  public static boolean isLetter(char c) {
    return isLetter((int) c);
  }

  public static boolean isLetterOrDigit(char c) {
    return isLetterOrDigit((int) c);
  }

  public static boolean isLowerCase(char c) {
    return isLowerCase((int) c);
  }

  public static boolean isLowSurrogate(char ch) {
    return ch >= MIN_LOW_SURROGATE && ch <= MAX_LOW_SURROGATE;
  }

  public static boolean isSurrogate(char ch) {
    return ch >= MIN_SURROGATE && ch <= MAX_SURROGATE;
  }

  /** Deprecated - see isWhitespace(char). */
  @Deprecated
  public static boolean isSpace(char c) {
    switch (c) {
      case ' ':
        return true;
      case '\n':
        return true;
      case '\t':
        return true;
      case '\f':
        return true;
      case '\r':
        return true;
      default:
        return false;
    }
  }

  public static boolean isWhitespace(char c) {
    return isWhitespace((int) c);
  }

  public static boolean isWhitespace(int codePoint) {
    return (CharacterData.properties(codePoint) & 32) != 0;
  }

  public static boolean isSpaceChar(char c) {
    return isSpaceChar((int) c);
  }

  public static boolean isSpaceChar(int codePoint) {
    return (CharacterData.properties(codePoint) & 64) != 0;
  }

  public static boolean isSupplementaryCodePoint(int codePoint) {
    return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT && codePoint <= MAX_CODE_POINT;
  }

  public static boolean isSurrogatePair(char highSurrogate, char lowSurrogate) {
    return isHighSurrogate(highSurrogate) && isLowSurrogate(lowSurrogate);
  }

  public static boolean isTitleCase(char c) {
    return isTitleCase((int) c);
  }

  public static char toTitleCase(char c) {
    return (char) CharacterData.toTitleCase(c);
  }

  public static boolean isUpperCase(char c) {
    return isUpperCase((int) c);
  }

  public static boolean isValidCodePoint(int codePoint) {
    return codePoint >= MIN_CODE_POINT && codePoint <= MAX_CODE_POINT;
  }

  public static int offsetByCodePoints(
      char[] a, int start, int count, int index, int codePointOffset) {
    return offsetByCodePoints(new CharSequenceAdapter(a, start, count), index, codePointOffset);
  }

  public static int offsetByCodePoints(CharSequence seq, int index, int codePointOffset) {
    if (codePointOffset < 0) {
      // move backwards
      while (codePointOffset < 0) {
        --index;
        if (Character.isLowSurrogate(seq.charAt(index))
            && Character.isHighSurrogate(seq.charAt(index - 1))) {
          --index;
        }
        ++codePointOffset;
      }
    } else {
      // move forwards
      while (codePointOffset > 0) {
        if (Character.isHighSurrogate(seq.charAt(index))
            && Character.isLowSurrogate(seq.charAt(index + 1))) {
          ++index;
        }
        ++index;
        --codePointOffset;
      }
    }
    return index;
  }

  public static char[] toChars(int codePoint) {
    checkArgument(isValidCodePoint(codePoint));

    if (codePoint >= MIN_SUPPLEMENTARY_CODE_POINT) {
      return new char[] {
        highSurrogate(codePoint), lowSurrogate(codePoint),
      };
    } else {
      return new char[] {
        (char) codePoint,
      };
    }
  }

  public static int toChars(int codePoint, char[] dst, int dstIndex) {
    checkArgument(isValidCodePoint(codePoint));

    if (codePoint >= MIN_SUPPLEMENTARY_CODE_POINT) {
      dst[dstIndex++] = highSurrogate(codePoint);
      dst[dstIndex] = lowSurrogate(codePoint);
      return 2;
    } else {
      dst[dstIndex] = (char) codePoint;
      return 1;
    }
  }

  public static int toCodePoint(char highSurrogate, char lowSurrogate) {
    /*
     * High and low surrogate chars have the bottom 10 bits to store the value
     * above MIN_SUPPLEMENTARY_CODE_POINT, so grab those bits and add the
     * offset.
     */
    return MIN_SUPPLEMENTARY_CODE_POINT + ((highSurrogate & 1023) << 10) + (lowSurrogate & 1023);
  }

  public static int toLowerCase(int c) {
    return CharacterData.toLowerCase(c);
  }

  public static int toUpperCase(int c) {
    return CharacterData.toUpperCase(c);
  }

  public static char toLowerCase(char c) {
    return (char) CharacterData.toLowerCase(c);
  }

  public static char toUpperCase(char c) {
    return (char) CharacterData.toUpperCase(c);
  }

  public static String toString(char x) {
    return String.valueOf(x);
  }

  public static String toString(int x) {
    return String.fromCodePoint(x);
  }

  public static Character valueOf(char c) {
    if (c < 128) {
      return BoxedValues.get(c);
    }
    return new Character(c);
  }

  static int codePointAt(CharSequence cs, int index, int limit) {
    char hiSurrogate = cs.charAt(index++);
    char loSurrogate;
    if (Character.isHighSurrogate(hiSurrogate)
        && index < limit
        && Character.isLowSurrogate(loSurrogate = cs.charAt(index))) {
      return Character.toCodePoint(hiSurrogate, loSurrogate);
    }
    return hiSurrogate;
  }

  static int codePointBefore(CharSequence cs, int index, int start) {
    char loSurrogate = cs.charAt(--index);
    char highSurrogate;
    if (isLowSurrogate(loSurrogate)
        && index > start
        && isHighSurrogate(highSurrogate = cs.charAt(index - 1))) {
      return toCodePoint(highSurrogate, loSurrogate);
    }
    return loSurrogate;
  }

  /**
   * Shared implementation with {@link Long#toString}.
   *
   * @skip
   */
  static char forDigit(int digit) {
    final int overBaseTen = digit - 10;
    return (char) (overBaseTen < 0 ? '0' + digit : 'a' + overBaseTen);
  }

  /**
   * Computes the high surrogate character of the UTF16 representation of a non-BMP code point. See
   * {@link lowSurrogate}.
   *
   * @param codePoint requested codePoint, required to be >= MIN_SUPPLEMENTARY_CODE_POINT
   * @return high surrogate character
   */
  public static char highSurrogate(int codePoint) {
    return (char)
        (MIN_HIGH_SURROGATE + (((codePoint - MIN_SUPPLEMENTARY_CODE_POINT) >> 10) & 1023));
  }

  /**
   * Computes the low surrogate character of the UTF16 representation of a non-BMP code point. See
   * {@link highSurrogate}.
   *
   * @param codePoint requested codePoint, required to be >= MIN_SUPPLEMENTARY_CODE_POINT
   * @return low surrogate character
   */
  public static char lowSurrogate(int codePoint) {
    return (char) (MIN_LOW_SURROGATE + ((codePoint - MIN_SUPPLEMENTARY_CODE_POINT) & 1023));
  }

  private final char value;

  public Character(char value) {
    this.value = value;
  }

  public char charValue() {
    return value;
  }

  @Override
  public int compareTo(Character c) {
    return compare(value, c.value);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Character) && (((Character) o).value == value);
  }

  @Override
  public int hashCode() {
    return hashCode(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
