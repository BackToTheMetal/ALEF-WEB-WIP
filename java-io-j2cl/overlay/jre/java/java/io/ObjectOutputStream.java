package java.io;

/** Primitive writes are supported; JVM object serialization fails explicitly. */
public class ObjectOutputStream extends OutputStream implements ObjectOutput {
  private final OutputStream output;
  private final DataOutputStream data;

  public ObjectOutputStream(OutputStream output) throws IOException {
    if (output == null) {
      throw new NullPointerException("output");
    }
    this.output = output;
    data = new DataOutputStream(output);
  }

  public final void writeObject(Object value) throws IOException {
    String description = value == null ? "null" : "object";
    throw new NotSerializableException(
        description + " (object serialization is unavailable in J2CL)");
  }

  @Override
  public void write(int value) throws IOException {
    data.write(value);
  }

  @Override
  public void write(byte[] buffer) throws IOException {
    data.write(buffer, 0, buffer.length);
  }

  @Override
  public void write(byte[] buffer, int offset, int length) throws IOException {
    data.write(buffer, offset, length);
  }

  @Override
  public void flush() throws IOException {
    output.flush();
  }

  @Override
  public void close() throws IOException {
    output.close();
  }

  @Override
  public void writeBoolean(boolean value) throws IOException {
    data.writeBoolean(value);
  }

  @Override
  public void writeByte(int value) throws IOException {
    data.writeByte(value);
  }

  @Override
  public void writeShort(int value) throws IOException {
    data.writeShort(value);
  }

  @Override
  public void writeChar(int value) throws IOException {
    data.writeChar(value);
  }

  @Override
  public void writeInt(int value) throws IOException {
    data.writeInt(value);
  }

  @Override
  public void writeLong(long value) throws IOException {
    data.writeLong(value);
  }

  @Override
  public void writeFloat(float value) throws IOException {
    data.writeFloat(value);
  }

  @Override
  public void writeDouble(double value) throws IOException {
    data.writeDouble(value);
  }

  @Override
  public void writeBytes(String value) throws IOException {
    data.writeBytes(value);
  }

  @Override
  public void writeChars(String value) throws IOException {
    data.writeChars(value);
  }

  @Override
  public void writeUTF(String value) throws IOException {
    data.writeUTF(value);
  }
}
