package com.intellij.openapi.util;

public class TextRange {
  public static final TextRange EMPTY_RANGE = new TextRange(0, 0);
  private final int start;
  private final int end;
  public TextRange(int start, int end) {
    if (start < 0 || end < start) throw new IllegalArgumentException("Invalid range " + start + ":" + end);
    this.start = start; this.end = end;
  }
  public static TextRange create(int start, int end) { return new TextRange(start, end); }
  public static TextRange from(int start, int length) { return create(start, start + length); }
  public int getStartOffset() { return start; }
  public int getEndOffset() { return end; }
  public int getLength() { return end - start; }
  public boolean isEmpty() { return start == end; }
  public boolean contains(int offset) { return start <= offset && offset <= end; }
  public boolean containsOffset(int offset) { return start <= offset && offset < end; }
  public boolean contains(TextRange range) { return start <= range.start && range.end <= end; }
  public boolean intersects(TextRange range) { return start <= range.end && range.start <= end; }
  public boolean intersectsStrict(TextRange range) { return start < range.end && range.start < end; }
  public TextRange intersection(TextRange range) {
    int a = Math.max(start, range.start), b = Math.min(end, range.end);
    return a <= b ? create(a, b) : null;
  }
  public TextRange shiftRight(int delta) { return create(start + delta, end + delta); }
  public TextRange shiftLeft(int delta) { return shiftRight(-delta); }
  public String substring(String text) { return text.substring(start, end); }
  @Override public boolean equals(Object value) {
    return value instanceof TextRange && ((TextRange) value).start == start && ((TextRange) value).end == end;
  }
  @Override public int hashCode() { return 31 * start + end; }
  @Override public String toString() { return "(" + start + "," + end + ")"; }
}
