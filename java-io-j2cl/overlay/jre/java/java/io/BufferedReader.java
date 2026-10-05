package java.io;

/** Reader wrapper with line handling and delegated mark/reset behavior. */
public class BufferedReader extends Reader {
  private final Reader input;
  private int pending = -1;
  private int markedPending = -1;
  private boolean closed;

  public BufferedReader(Reader input, int size) {
    if (input == null) {
      throw new NullPointerException("input");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    this.input = input;
  }

  public BufferedReader(Reader input) {
    this(input, 8192);
  }

  private void ensureOpen() throws IOException {
    if (closed) {
      throw new IOException("Stream closed");
    }
  }

  @Override
  public int read() throws IOException {
    ensureOpen();
    if (pending != -1) {
      int value = pending;
      pending = -1;
      return value;
    }
    return input.read();
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
    int count = 0;
    if (pending != -1) {
      buffer[offset++] = (char) pending;
      pending = -1;
      length--;
      count++;
    }
    if (length == 0) {
      return count;
    }
    int read = input.read(buffer, offset, length);
    return read == -1 ? (count == 0 ? -1 : count) : count + read;
  }

  public String readLine() throws IOException {
    ensureOpen();
    StringBuilder line = new StringBuilder();
    int value;
    while ((value = read()) != -1) {
      if (value == '\n') {
        break;
      }
      if (value == '\r') {
        int next = read();
        if (next != '\n' && next != -1) {
          pending = next;
        }
        break;
      }
      line.append((char) value);
    }
    return value == -1 && line.length() == 0 ? null : line.toString();
  }

  @Override
  public long skip(long count) throws IOException {
    ensureOpen();
    if (count < 0) {
      throw new IllegalArgumentException("skip value is negative");
    }
    long skipped = 0;
    while (skipped < count && read() != -1) {
      skipped++;
    }
    return skipped;
  }

  @Override
  public boolean ready() throws IOException {
    ensureOpen();
    return pending != -1 || input.ready();
  }

  @Override
  public boolean markSupported() {
    return input.markSupported();
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    ensureOpen();
    input.mark(readAheadLimit);
    markedPending = pending;
  }

  @Override
  public void reset() throws IOException {
    ensureOpen();
    input.reset();
    pending = markedPending;
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      input.close();
    }
  }
}
