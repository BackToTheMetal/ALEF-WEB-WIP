package java.io;

/** Big-endian primitive input over J2CL's existing InputStream. */
public class DataInputStream extends FilterInputStream implements DataInput {
  public DataInputStream(InputStream input) {
    super(input);
  }

  @Override
  public final int read(byte[] buffer) throws IOException {
    return in.read(buffer, 0, buffer.length);
  }

  @Override
  public final int read(byte[] buffer, int offset, int length) throws IOException {
    return in.read(buffer, offset, length);
  }

  @Override
  public final void readFully(byte[] buffer) throws IOException {
    readFully(buffer, 0, buffer.length);
  }

  @Override
  public final void readFully(byte[] buffer, int offset, int length) throws IOException {
    if (buffer == null) {
      throw new NullPointerException("buffer");
    }
    if (offset < 0 || length < 0 || offset > buffer.length - length) {
      throw new IndexOutOfBoundsException();
    }
    int consumed = 0;
    while (consumed < length) {
      int count = in.read(buffer, offset + consumed, length - consumed);
      if (count < 0) {
        throw new EOFException();
      }
      if (count == 0) {
        int value = in.read();
        if (value < 0) {
          throw new EOFException();
        }
        buffer[offset + consumed] = (byte) value;
        consumed++;
      } else {
        consumed += count;
      }
    }
  }

  @Override
  public final int skipBytes(int count) throws IOException {
    int skipped = 0;
    while (skipped < count) {
      long step = in.skip(count - skipped);
      if (step <= 0) {
        if (in.read() < 0) {
          break;
        }
        step = 1;
      }
      skipped += (int) step;
    }
    return skipped;
  }

  private int required() throws IOException {
    int value = in.read();
    if (value < 0) {
      throw new EOFException();
    }
    return value;
  }

  @Override
  public final boolean readBoolean() throws IOException {
    return required() != 0;
  }

  @Override
  public final byte readByte() throws IOException {
    return (byte) required();
  }

  @Override
  public final int readUnsignedByte() throws IOException {
    return required();
  }

  @Override
  public final short readShort() throws IOException {
    return (short) ((required() << 8) | required());
  }

  @Override
  public final int readUnsignedShort() throws IOException {
    return (required() << 8) | required();
  }

  @Override
  public final char readChar() throws IOException {
    return (char) readUnsignedShort();
  }

  @Override
  public final int readInt() throws IOException {
    return (required() << 24) | (required() << 16) | (required() << 8) | required();
  }

  @Override
  public final long readLong() throws IOException {
    return ((long) readInt() << 32) | (readInt() & 0xffffffffL);
  }

  @Override
  public final float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  @Override
  public final double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }

  @Override
  public final String readLine() throws IOException {
    StringBuilder result = new StringBuilder();
    int value;
    while ((value = in.read()) >= 0) {
      if (value == '\n' || value == '\r') {
        break;
      }
      result.append((char) value);
    }
    return value < 0 && result.length() == 0 ? null : result.toString();
  }

  @Override
  public final String readUTF() throws IOException {
    return readUTF(this);
  }

  public static final String readUTF(DataInput input) throws IOException {
    int byteLength = input.readUnsignedShort();
    byte[] data = new byte[byteLength];
    input.readFully(data);
    char[] characters = new char[byteLength];
    int inputIndex = 0;
    int outputIndex = 0;
    while (inputIndex < byteLength) {
      int first = data[inputIndex++] & 0xff;
      if ((first & 0x80) == 0) {
        characters[outputIndex++] = (char) first;
      } else if ((first & 0xe0) == 0xc0) {
        if (inputIndex >= byteLength) {
          throw new UTFDataFormatException("partial character at end");
        }
        int second = data[inputIndex++] & 0xff;
        if ((second & 0xc0) != 0x80) {
          throw new UTFDataFormatException("malformed input");
        }
        characters[outputIndex++] = (char) (((first & 0x1f) << 6) | (second & 0x3f));
      } else if ((first & 0xf0) == 0xe0) {
        if (inputIndex + 1 >= byteLength) {
          throw new UTFDataFormatException("partial character at end");
        }
        int second = data[inputIndex++] & 0xff;
        int third = data[inputIndex++] & 0xff;
        if ((second & 0xc0) != 0x80 || (third & 0xc0) != 0x80) {
          throw new UTFDataFormatException("malformed input");
        }
        characters[outputIndex++] =
            (char) (((first & 0x0f) << 12) | ((second & 0x3f) << 6) | (third & 0x3f));
      } else {
        throw new UTFDataFormatException("malformed input");
      }
    }
    return new String(characters, 0, outputIndex);
  }
}
