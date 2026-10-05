/** Pins the Java 21 nonspacing-mark property used by Pattern's boundary semantics. */
public final class RegexUnicodeDump {
  public static void main(String[] args) {
    System.out.println("{\"java_version\":\"" + System.getProperty("java.version") + "\",\"nonspacing_mark_ranges\":[" + ranges(false)
        + "],\"bmp_letter_digit_ranges\":[" + ranges(true) + "]}");
  }
  private static String ranges(boolean base) {
    StringBuilder ranges = new StringBuilder();
    int start = -1;
    for (int cp = 0; cp <= Character.MAX_CODE_POINT + 1; cp++) {
      boolean mark = cp <= Character.MAX_CODE_POINT && (base ? cp <= 0xFFFF && Character.isLetterOrDigit(cp)
          : Character.getType(cp) == Character.NON_SPACING_MARK);
      if (mark && start < 0) start = cp;
      if (!mark && start >= 0) {
        if (ranges.length() > 0) ranges.append(',');
        ranges.append(start).append(',').append(cp - 1);
        start = -1;
      }
    }
    return ranges.toString();
  }
}
