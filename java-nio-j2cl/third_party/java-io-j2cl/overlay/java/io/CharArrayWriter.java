package java.io;

/** Growable in-memory character writer. */
public class CharArrayWriter extends Writer {
  protected char[] buf;
  protected int count;

  public CharArrayWriter() {
    this(32);
  }

  public CharArrayWriter(int initialSize) {
    if (initialSize < 0) {
      throw new IllegalArgumentException("Negative initial size: " + initialSize);
    }
    buf = new char[initialSize];
  }

  private void ensureCapacity(int minimum) {
    if (minimum <= buf.length) {
      return;
    }
    int nextLength = Math.max(minimum, Math.max(1, buf.length * 2));
    char[] next = new char[nextLength];
    for (int i = 0; i < count; i++) {
      next[i] = buf[i];
    }
    buf = next;
  }

  @Override
  public void write(int value) {
    ensureCapacity(count + 1);
    buf[count++] = (char) value;
  }

  @Override
  public void write(char[] source, int offset, int length) {
    if (source == null) {
      throw new NullPointerException("source");
    }
    if (offset < 0 || length < 0 || offset > source.length - length) {
      throw new IndexOutOfBoundsException();
    }
    ensureCapacity(count + length);
    for (int i = 0; i < length; i++) {
      buf[count + i] = source[offset + i];
    }
    count += length;
  }

  @Override
  public void write(String value, int offset, int length) {
    if (value == null) {
      throw new NullPointerException("value");
    }
    if (offset < 0 || length < 0 || offset > value.length() - length) {
      throw new IndexOutOfBoundsException();
    }
    ensureCapacity(count + length);
    for (int i = 0; i < length; i++) {
      buf[count + i] = value.charAt(offset + i);
    }
    count += length;
  }

  public void writeTo(Writer output) throws IOException {
    output.write(buf, 0, count);
  }

  public void reset() {
    count = 0;
  }

  public char[] toCharArray() {
    char[] result = new char[count];
    for (int i = 0; i < count; i++) {
      result[i] = buf[i];
    }
    return result;
  }

  public int size() {
    return count;
  }

  @Override
  public String toString() {
    return new String(buf, 0, count);
  }

  @Override
  public void flush() {}

  @Override
  public void close() {}
}
