package java.io;

import java.nio.charset.Charset;
import javaemul.internal.EmulatedCharset;

/** Character reader backed by J2CL's existing charset implementations. */
public class InputStreamReader extends Reader {
  private final InputStream input;
  private final Charset charset;
  private char[] characters;
  private int position;
  private boolean closed;

  public InputStreamReader(InputStream input) {
    this(input, Charset.defaultCharset());
  }

  public InputStreamReader(InputStream input, String charsetName)
      throws UnsupportedEncodingException {
    if (input == null) {
      throw new NullPointerException("input");
    }
    if (charsetName == null) {
      throw new NullPointerException("charsetName");
    }
    try {
      this.input = input;
      charset = Charset.forName(charsetName);
    } catch (RuntimeException exception) {
      throw new UnsupportedEncodingException(charsetName);
    }
  }

  public InputStreamReader(InputStream input, Charset charset) {
    if (input == null || charset == null) {
      throw new NullPointerException();
    }
    this.input = input;
    this.charset = charset;
  }

  public String getEncoding() {
    return closed ? null : charset.name();
  }

  private void load() throws IOException {
    if (closed) {
      throw new IOException("Stream closed");
    }
    if (characters != null) {
      return;
    }
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int count;
    while ((count = input.read(buffer, 0, buffer.length)) != -1) {
      if (count == 0) {
        int value = input.read();
        if (value == -1) {
          break;
        }
        bytes.write(value);
      } else {
        bytes.write(buffer, 0, count);
      }
    }
    byte[] encoded = bytes.toByteArray();
    if (!(charset instanceof EmulatedCharset)) {
      throw new UnsupportedEncodingException(charset.name());
    }
    characters = ((EmulatedCharset) charset).decodeString(encoded, 0, encoded.length);
  }

  @Override
  public int read() throws IOException {
    load();
    return position >= characters.length ? -1 : characters[position++];
  }

  @Override
  public int read(char[] buffer, int offset, int length) throws IOException {
    if (buffer == null) {
      throw new NullPointerException("buffer");
    }
    if (offset < 0 || length < 0 || offset > buffer.length - length) {
      throw new IndexOutOfBoundsException();
    }
    if (length == 0) {
      return 0;
    }
    load();
    if (position >= characters.length) {
      return -1;
    }
    int count = Math.min(length, characters.length - position);
    for (int i = 0; i < count; i++) {
      buffer[offset + i] = characters[position + i];
    }
    position += count;
    return count;
  }

  @Override
  public boolean ready() throws IOException {
    if (closed) {
      throw new IOException("Stream closed");
    }
    return characters != null ? position < characters.length : input.available() > 0;
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      input.close();
    }
  }
}
