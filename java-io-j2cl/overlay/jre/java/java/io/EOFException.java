package java.io;

/** Signals that an input stream ended before a complete value was read. */
public class EOFException extends IOException {
  public EOFException() {}

  public EOFException(String message) {
    super(message);
  }
}
