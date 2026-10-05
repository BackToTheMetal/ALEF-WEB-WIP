import java.util.ArrayList;
import java.util.List;

/** Exports Java 21 Unicode character properties and simple mappings without external data. */
public final class JavaCharacterData {
  private static int value(String table, int cp) {
    switch (table) {
      case "properties":
        return (Character.isDigit(cp) ? 1 : 0) | (Character.isLetter(cp) ? 2 : 0)
            | (Character.isLowerCase(cp) ? 4 : 0) | (Character.isUpperCase(cp) ? 8 : 0)
            | (Character.isTitleCase(cp) ? 16 : 0) | (Character.isWhitespace(cp) ? 32 : 0)
            | (Character.isSpaceChar(cp) ? 64 : 0) | (Character.isJavaIdentifierStart(cp) ? 128 : 0)
            | (Character.isJavaIdentifierPart(cp) ? 256 : 0);
      case "lower": return Character.toLowerCase(cp) - cp;
      case "upper": return Character.toUpperCase(cp) - cp;
      case "title": return Character.toTitleCase(cp) - cp;
      case "digit":
        int digit = Character.digit(cp, 36);
        return digit < 0 ? 0 : digit - cp;
      default: throw new AssertionError(table);
    }
  }

  private static List<Integer> ranges(String table) {
    List<Integer> output = new ArrayList<>();
    int start = 0, previous = value(table, 0);
    for (int cp = 1; cp <= 0x110000; cp++) {
      int current = cp == 0x110000 ? 0 : value(table, cp);
      if (current != previous) {
        if (previous != 0) {
          output.add(start); output.add(cp - 1); output.add(previous);
        }
        start = cp; previous = current;
      }
    }
    return output;
  }

  public static void main(String[] args) {
    if (Runtime.version().feature() != 21) throw new IllegalStateException("Java 21 is required");
    System.out.println("{\"java_version\":\"" + System.getProperty("java.version") + "\",");
    String[] tables = {"properties", "lower", "upper", "title", "digit"};
    for (int i = 0; i < tables.length; i++) {
      System.out.print("\"" + tables[i] + "\":");
      System.out.print(ranges(tables[i]));
      System.out.println(i == tables.length - 1 ? "}" : ",");
    }
  }
}
