package java.nio.charset;

public class CoderResult {
  public static final CoderResult UNDERFLOW = new CoderResult(0, 0);
  public static final CoderResult OVERFLOW = new CoderResult(1, 0);
  private final int kind;
  private final int length;
  private CoderResult(int kind, int length) { this.kind = kind; this.length = length; }
  public boolean isUnderflow() { return kind == 0; }
  public boolean isOverflow() { return kind == 1; }
  public boolean isError() { return kind >= 2; }
  public boolean isMalformed() { return kind == 2; }
  public boolean isUnmappable() { return kind == 3; }
  public int length() { if (!isError()) throw new UnsupportedOperationException(); return length; }
  public static CoderResult malformedForLength(int length) { if (length <= 0) throw new IllegalArgumentException(); return new CoderResult(2, length); }
  public static CoderResult unmappableForLength(int length) { if (length <= 0) throw new IllegalArgumentException(); return new CoderResult(3, length); }
  public void throwException() throws CharacterCodingException {
    if (isUnderflow()) throw new java.nio.BufferUnderflowException();
    if (isOverflow()) throw new java.nio.BufferOverflowException();
    if (isMalformed()) throw new MalformedInputException(length);
    throw new UnmappableCharacterException(length);
  }
  @Override public String toString() {
    return kind == 0 ? "UNDERFLOW" : kind == 1 ? "OVERFLOW" : (kind == 2 ? "MALFORMED" : "UNMAPPABLE") + "[" + length + "]";
  }
}
