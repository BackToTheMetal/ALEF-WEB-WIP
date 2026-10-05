package java.io;

public interface ObjectInput extends DataInput, AutoCloseable {
  Object readObject() throws ClassNotFoundException, IOException;

  int read() throws IOException;

  int read(byte[] buffer) throws IOException;

  int read(byte[] buffer, int offset, int length) throws IOException;

  long skip(long count) throws IOException;

  int available() throws IOException;

  @Override
  void close() throws IOException;
}
