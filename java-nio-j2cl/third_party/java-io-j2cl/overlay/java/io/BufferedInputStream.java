package java.io;

/** Lightweight buffering-compatible wrapper over J2CL's existing InputStream. */
public class BufferedInputStream extends FilterInputStream {
  public BufferedInputStream(InputStream input) {
    this(input, 8192);
  }

  public BufferedInputStream(InputStream input, int size) {
    super(input);
    if (size <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
  }
}
