package java.io;

/** Big-endian primitive output over J2CL's existing OutputStream. */
public class DataOutputStream extends FilterOutputStream implements DataOutput {
  protected int written;

  public DataOutputStream(OutputStream output) {
    super(output);
  }

  private void increment(int count) {
    int next = written + count;
    written = next < 0 ? Integer.MAX_VALUE : next;
  }

  @Override
  public synchronized void write(int value) throws IOException {
    out.write(value);
    increment(1);
  }

  @Override
  public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
    out.write(buffer, offset, length);
    increment(length);
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public final void writeBoolean(boolean value) throws IOException {
    write(value ? 1 : 0);
  }

  @Override
  public final void writeByte(int value) throws IOException {
    write(value);
  }

  @Override
  public final void writeShort(int value) throws IOException {
    write(value >>> 8);
    write(value);
  }

  @Override
  public final void writeChar(int value) throws IOException {
    writeShort(value);
  }

  @Override
  public final void writeInt(int value) throws IOException {
    write(value >>> 24);
    write(value >>> 16);
    write(value >>> 8);
    write(value);
  }

  @Override
  public final void writeLong(long value) throws IOException {
    writeInt((int) (value >>> 32));
    writeInt((int) value);
  }

  @Override
  public final void writeFloat(float value) throws IOException {
    writeInt(Float.floatToIntBits(value));
  }

  @Override
  public final void writeDouble(double value) throws IOException {
    writeLong(Double.doubleToLongBits(value));
  }

  @Override
  public final void writeBytes(String value) throws IOException {
    for (int i = 0; i < value.length(); i++) {
      writeByte(value.charAt(i));
    }
  }

  @Override
  public final void writeChars(String value) throws IOException {
    for (int i = 0; i < value.length(); i++) {
      writeChar(value.charAt(i));
    }
  }

  @Override
  public final void writeUTF(String value) throws IOException {
    int encodedLength = 0;
    for (int i = 0; i < value.length(); i++) {
      int character = value.charAt(i);
      encodedLength += character >= 0x0001 && character <= 0x007f ? 1 : character > 0x07ff ? 3 : 2;
    }
    if (encodedLength > 65535) {
      throw new UTFDataFormatException("encoded string too long: " + encodedLength + " bytes");
    }
    writeShort(encodedLength);
    for (int i = 0; i < value.length(); i++) {
      int character = value.charAt(i);
      if (character >= 0x0001 && character <= 0x007f) {
        writeByte(character);
      } else if (character > 0x07ff) {
        writeByte(0xe0 | ((character >> 12) & 0x0f));
        writeByte(0x80 | ((character >> 6) & 0x3f));
        writeByte(0x80 | (character & 0x3f));
      } else {
        writeByte(0xc0 | ((character >> 6) & 0x1f));
        writeByte(0x80 | (character & 0x3f));
      }
    }
  }

  public final int size() {
    return written;
  }
}
