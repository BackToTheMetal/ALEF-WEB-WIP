import java.util.function.IntPredicate;
import javax.lang.model.SourceVersion;

/** Exports the pinned JVM's complete Unicode identifier rules for the browser port. */
class JavaIdentifiers {
  public static void main(String[] args) {
    System.out.print("{\"java_version\":\"" + System.getProperty("java.version") + "\",\"start\":");
    ranges(Character::isJavaIdentifierStart);
    System.out.print(",\"part\":");
    ranges(Character::isJavaIdentifierPart);
    System.out.print(",\"keywords\":[");
    String words = "abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for goto if implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while true false null _";
    boolean separator = false;
    for (String word : words.split(" ")) {
      if (SourceVersion.isKeyword(word)) {
        if (separator) System.out.print(',');
        System.out.print("\"" + word + "\"");
        separator = true;
      }
    }
    System.out.println("]}");
  }

  private static void ranges(IntPredicate predicate) {
    System.out.print('[');
    boolean separator = false;
    for (int codepoint = 0; codepoint <= Character.MAX_CODE_POINT; codepoint++) {
      if (!predicate.test(codepoint)) continue;
      int first = codepoint;
      while (codepoint < Character.MAX_CODE_POINT && predicate.test(codepoint + 1)) codepoint++;
      if (separator) System.out.print(',');
      System.out.print(first + "," + codepoint);
      separator = true;
    }
    System.out.print(']');
  }
}
