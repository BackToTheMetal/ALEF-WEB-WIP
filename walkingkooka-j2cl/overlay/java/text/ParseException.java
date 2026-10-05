package java.text;

/** Minimal stock-J2CL signature prerequisite for formatted fields. */
public class ParseException extends Exception {
  private final int errorOffset;
  public ParseException(String message, int errorOffset) { super(message); this.errorOffset = errorOffset; }
  public int getErrorOffset() { return errorOffset; }
}
