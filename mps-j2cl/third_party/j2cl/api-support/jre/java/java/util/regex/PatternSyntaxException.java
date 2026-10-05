package java.util.regex;

/** Syntax failure for the explicitly supported browser regular-expression subset. */
public class PatternSyntaxException extends IllegalArgumentException {
  private final String description;
  private final String pattern;
  private final int index;

  public PatternSyntaxException(String description, String pattern, int index) {
    this.description = description;
    this.pattern = pattern;
    this.index = index;
  }

  public String getDescription() { return description; }
  public String getPattern() { return pattern; }
  public int getIndex() { return index; }

  @Override
  public String getMessage() {
    StringBuilder message = new StringBuilder(description);
    if (index >= 0) message.append(" near index ").append(index);
    message.append('\n').append(pattern);
    if (index >= 0) {
      message.append('\n');
      for (int i = 0; i < index; i++) message.append(' ');
      message.append('^');
    }
    return message.toString();
  }
}
