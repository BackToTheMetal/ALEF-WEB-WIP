package java.io;

/** Thread-coordinated pipes are outside the single-threaded browser profile. */
public class PipedOutputStream extends OutputStream {
  public PipedOutputStream(PipedInputStream sink) throws IOException {
    throw unsupported();
  }

  public PipedOutputStream() {}

  public synchronized void connect(PipedInputStream sink) throws IOException {
    throw unsupported();
  }

  @Override
  public void write(int value) throws IOException {
    throw unsupported();
  }

  private static IOException unsupported() {
    return new IOException("Piped streams require blocking threads and are unavailable in J2CL");
  }
}
