package java.io;

public interface ObjectOutput extends DataOutput, AutoCloseable {
  void writeObject(Object value) throws IOException;

  @Override
  void write(int value) throws IOException;

  @Override
  void write(byte[] buffer) throws IOException;

  @Override
  void write(byte[] buffer, int offset, int length) throws IOException;

  void flush() throws IOException;

  @Override
  void close() throws IOException;
}
