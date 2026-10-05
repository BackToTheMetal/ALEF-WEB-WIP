package java.io;

/** Primitive reads are supported; JVM object deserialization fails explicitly. */
public class ObjectInputStream extends InputStream implements ObjectInput {
  private final InputStream input;
  private final DataInputStream data;

  public ObjectInputStream(InputStream input) throws IOException {
    if (input == null) {
      throw new NullPointerException("input");
    }
    this.input = input;
    data = new DataInputStream(input);
  }

  public final Object readObject() throws IOException, ClassNotFoundException {
    throw new NotSerializableException("Object deserialization is unavailable in J2CL");
  }

  @Override
  public int read() throws IOException {
    return input.read();
  }

  @Override
  public int read(byte[] buffer, int offset, int length) throws IOException {
    return input.read(buffer, offset, length);
  }

  @Override
  public int available() throws IOException {
    return input.available();
  }

  @Override
  public void close() throws IOException {
    input.close();
  }

  @Override
  public void readFully(byte[] buffer) throws IOException {
    data.readFully(buffer);
  }

  @Override
  public void readFully(byte[] buffer, int offset, int length) throws IOException {
    data.readFully(buffer, offset, length);
  }

  @Override
  public int skipBytes(int count) throws IOException {
    return data.skipBytes(count);
  }

  @Override
  public boolean readBoolean() throws IOException {
    return data.readBoolean();
  }

  @Override
  public byte readByte() throws IOException {
    return data.readByte();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    return data.readUnsignedByte();
  }

  @Override
  public short readShort() throws IOException {
    return data.readShort();
  }

  @Override
  public int readUnsignedShort() throws IOException {
    return data.readUnsignedShort();
  }

  @Override
  public char readChar() throws IOException {
    return data.readChar();
  }

  @Override
  public int readInt() throws IOException {
    return data.readInt();
  }

  @Override
  public long readLong() throws IOException {
    return data.readLong();
  }

  @Override
  public float readFloat() throws IOException {
    return data.readFloat();
  }

  @Override
  public double readDouble() throws IOException {
    return data.readDouble();
  }

  @Override
  public String readLine() throws IOException {
    return data.readLine();
  }

  @Override
  public String readUTF() throws IOException {
    return data.readUTF();
  }
}
