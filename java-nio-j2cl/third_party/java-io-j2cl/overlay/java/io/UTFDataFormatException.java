package java.io;

/** Signals malformed or oversized modified UTF-8 data. */
public class UTFDataFormatException extends IOException {
  public UTFDataFormatException() {}

  public UTFDataFormatException(String message) {
    super(message);
  }
}
