package java.nio.file;

public class InvalidPathException extends IllegalArgumentException {
  private final String input;
  private final String reason;
  private final int index;
  public InvalidPathException(String input, String reason) { this(input, reason, -1); }
  public InvalidPathException(String input, String reason, int index) {
    if (input == null || reason == null) throw new NullPointerException();
    if (index < -1) throw new IllegalArgumentException();
    this.input = input; this.reason = reason; this.index = index;
  }
  public String getInput() { return input; }
  public String getReason() { return reason; }
  public int getIndex() { return index; }
  @Override public String getMessage() { return reason + (index < 0 ? "" : " at index " + index) + ": " + input; }
}
