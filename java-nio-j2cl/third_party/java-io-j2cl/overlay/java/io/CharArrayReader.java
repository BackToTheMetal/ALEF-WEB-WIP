package java.io;

/** In-memory character reader. */
public class CharArrayReader extends Reader {
  protected char[] buf;
  protected int pos;
  protected int markedPos;
  protected int count;

  public CharArrayReader(char[] buffer) {
    this(buffer, 0, buffer.length);
  }

  public CharArrayReader(char[] buffer, int offset, int length) {
    if (buffer == null) {
      throw new NullPointerException("buffer");
    }
    if (offset < 0 || length < 0 || offset > buffer.length - length) {
      throw new IllegalArgumentException();
    }
    buf = buffer;
    pos = offset;
    markedPos = offset;
    count = Math.min(offset + length, buffer.length);
  }

  private void ensureOpen() throws IOException {
    if (buf == null) {
      throw new IOException("Stream closed");
    }
  }

  @Override
  public int read() throws IOException {
    ensureOpen();
    return pos >= count ? -1 : buf[pos++];
  }

  @Override
  public int read(char[] buffer, int offset, int length) throws IOException {
    ensureOpen();
    if (buffer == null) {
      throw new NullPointerException("buffer");
    }
    if (offset < 0 || length < 0 || offset > buffer.length - length) {
      throw new IndexOutOfBoundsException();
    }
    if (length == 0) {
      return 0;
    }
    if (pos >= count) {
      return -1;
    }
    int actual = Math.min(length, count - pos);
    for (int i = 0; i < actual; i++) {
      buffer[offset + i] = buf[pos + i];
    }
    pos += actual;
    return actual;
  }

  @Override
  public long skip(long amount) throws IOException {
    ensureOpen();
    if (amount <= 0) {
      return 0;
    }
    int actual = (int) Math.min(amount, count - pos);
    pos += actual;
    return actual;
  }

  @Override
  public boolean ready() throws IOException {
    ensureOpen();
    return pos < count;
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    ensureOpen();
    markedPos = pos;
  }

  @Override
  public void reset() throws IOException {
    ensureOpen();
    pos = markedPos;
  }

  @Override
  public void close() {
    buf = null;
  }
}
