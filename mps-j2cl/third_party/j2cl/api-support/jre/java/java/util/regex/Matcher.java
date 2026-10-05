package java.util.regex;

/** Stateful matching for Pattern's validated browser subset. */
public final class Matcher {
  private final Pattern pattern;
  private CharSequence input;
  private Pattern.NativeMatch match;
  private int first = -1;
  private int last;

  Matcher(Pattern pattern, CharSequence input) {
    this.pattern = pattern;
    reset(input);
  }

  public Pattern pattern() { return pattern; }

  public Matcher reset() {
    match = null;
    first = -1;
    last = 0;
    return this;
  }

  public Matcher reset(CharSequence input) {
    if (input == null) throw new NullPointerException("input");
    this.input = input;
    return reset();
  }

  public boolean matches() {
    Pattern.NativeRegExp expression = pattern.expression(true);
    match = expression.exec(input.toString());
    first = match == null ? -1 : match.index;
    if (match != null) last = end();
    return match != null;
  }

  public boolean find(int start) {
    if (start < 0 || start > input.length()) throw new IndexOutOfBoundsException("Illegal start index");
    reset();
    return search(start);
  }

  public boolean find() {
    int next = last;
    if (next == first) next++;
    if (next > input.length()) {
      match = null;
      return false;
    }
    return search(next);
  }

  private boolean search(int next) {
    match = null;
    String text = input.toString();
    Pattern.NativeRegExp expression = pattern.expression(false, pattern.hasBoundaries);
    do {
      expression.lastIndex = next;
      String candidate = text;
      if (next > 0 && next < text.length() && Character.isHighSurrogate(text.charAt(next - 1))
          && Character.isLowSurrogate(text.charAt(next))) {
        // Java can begin a boundary search at the low half of a pair. Mask only
        // the skipped high half; it remains non-word and cannot be a mark base.
        candidate = text.substring(0, next - 1) + "\u0000" + text.substring(next);
      }
      match = expression.exec(candidate);
      if (match != null || !pattern.hasBoundaries) break;
      // Native Unicode global scanning skips surrogate halves, while Java's
      // boundary search considers every UTF-16 position. Sticky attempts retain
      // native backtracking and captures while controlling candidate positions.
    } while (++next <= text.length());
    if (match == null) {
      first = -1;
      return false;
    }
    first = match.index;
    last = end();
    return true;
  }

  public int start() {
    checkMatch();
    return match.index;
  }

  public int end() {
    checkMatch();
    return match.index + group().length();
  }

  public String group() { return group(0); }

  public String group(int number) {
    checkMatch();
    if (number < 0 || number >= match.length) throw new IndexOutOfBoundsException("No group " + number);
    String result = match.at(number);
    return result == null ? null : result;
  }

  public int groupCount() {
    return pattern.groupCount;
  }

  private void checkMatch() {
    if (match == null) throw new IllegalStateException("No match found");
  }
}
