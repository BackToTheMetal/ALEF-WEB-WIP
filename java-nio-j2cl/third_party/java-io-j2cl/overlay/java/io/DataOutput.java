package java.io;

public interface DataOutput {
  void write(int value) throws IOException;

  void write(byte[] buffer) throws IOException;

  void write(byte[] buffer, int offset, int length) throws IOException;

  void writeBoolean(boolean value) throws IOException;

  void writeByte(int value) throws IOException;

  void writeShort(int value) throws IOException;

  void writeChar(int value) throws IOException;

  void writeInt(int value) throws IOException;

  void writeLong(long value) throws IOException;

  void writeFloat(float value) throws IOException;

  void writeDouble(double value) throws IOException;

  void writeBytes(String value) throws IOException;

  void writeChars(String value) throws IOException;

  void writeUTF(String value) throws IOException;
}
