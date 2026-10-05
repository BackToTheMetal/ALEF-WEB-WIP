package java.util;

/** Legacy delimiter tokenizer. Delimiters are Unicode code points, as on the JVM. */
public class StringTokenizer implements Enumeration<Object> {
  private final String input;
  private String delimiters;
  private final boolean returnDelimiters;
  private int position;

  public StringTokenizer(String str, String delim, boolean returnDelims) {
    if (str == null) throw new NullPointerException("str");
    input = str;
    delimiters = delim;
    returnDelimiters = returnDelims;
  }

  public StringTokenizer(String str, String delim) { this(str, delim, false); }
  public StringTokenizer(String str) { this(str, " \t\n\r\f", false); }

  public boolean hasMoreTokens() { return skipDelimiters(position) < input.length(); }

  public String nextToken() {
    position = skipDelimiters(position);
    if (position >= input.length()) throw new NoSuchElementException();
    int start = position;
    position = scanToken(position);
    return input.substring(start, position);
  }

  public String nextToken(String delim) {
    delimiters = delim;
    return nextToken();
  }

  @Override public boolean hasMoreElements() { return hasMoreTokens(); }
  @Override public Object nextElement() { return nextToken(); }

  public int countTokens() {
    int count = 0;
    int cursor = position;
    while (cursor < input.length()) {
      cursor = skipDelimiters(cursor);
      if (cursor >= input.length()) break;
      cursor = scanToken(cursor);
      count++;
    }
    return count;
  }

  private int skipDelimiters(int cursor) {
    if (delimiters == null) throw new NullPointerException("delim");
    if (!returnDelimiters) {
      while (cursor < input.length()) {
        int cp = input.codePointAt(cursor);
        if (!isDelimiter(cp)) break;
        cursor += Character.charCount(cp);
      }
    }
    return cursor;
  }

  private int scanToken(int cursor) {
    int start = cursor;
    while (cursor < input.length()) {
      int cp = input.codePointAt(cursor);
      if (isDelimiter(cp)) break;
      cursor += Character.charCount(cp);
    }
    if (returnDelimiters && cursor == start) {
      cursor += Character.charCount(input.codePointAt(cursor));
    }
    return cursor;
  }

  private boolean isDelimiter(int cp) {
    for (int cursor = 0; cursor < delimiters.length();) {
      int delimiter = delimiters.codePointAt(cursor);
      if (delimiter == cp) return true;
      cursor += Character.charCount(delimiter);
    }
    return false;
  }
}
