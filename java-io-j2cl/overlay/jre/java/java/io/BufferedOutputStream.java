package java.io;

/** Lightweight buffering-compatible wrapper over J2CL's existing OutputStream. */
public class BufferedOutputStream extends FilterOutputStream {
  public BufferedOutputStream(OutputStream output) {
    this(output, 8192);
  }

  public BufferedOutputStream(OutputStream output, int size) {
    super(output);
    if (size <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
  }
}
