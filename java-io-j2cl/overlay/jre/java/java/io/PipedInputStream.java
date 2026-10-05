package java.io;

/** Thread-coordinated pipes are outside the single-threaded browser profile. */
public class PipedInputStream extends InputStream {
  public PipedInputStream(PipedOutputStream source) throws IOException {
    throw unsupported();
  }

  public PipedInputStream(PipedOutputStream source, int pipeSize) throws IOException {
    throw unsupported();
  }

  public PipedInputStream() {}

  public PipedInputStream(int pipeSize) {
    if (pipeSize <= 0) {
      throw new IllegalArgumentException("Pipe Size <= 0");
    }
  }

  public void connect(PipedOutputStream source) throws IOException {
    throw unsupported();
  }

  @Override
  public int read() throws IOException {
    throw unsupported();
  }

  private static IOException unsupported() {
    return new IOException("Piped streams require blocking threads and are unavailable in J2CL");
  }
}
