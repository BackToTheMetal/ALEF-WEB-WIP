package java.time;

/** Exception for invalid time values in the browser time profile. */
public class DateTimeException extends RuntimeException {
  public DateTimeException(String message) { super(message); }
  public DateTimeException(String message, Throwable cause) { super(message, cause); }
}
