package mpsj2cl.formatting;

import java.util.Locale;

/** JVM-only oracle. Its output is committed so browser checks do not calculate their own expectation. */
public final class GenerateFormattingGolden {
  public static void main(String[] arguments) {
    System.out.println("package mpsj2cl.formatting;\n\n// Generated with the JVM java.util.Formatter, Locale.ROOT; do not edit.\npublic final class FormattingGolden {\n  public static final String[] VALUES = {");
    for (FormattingCases.Case example : FormattingCases.all()) {
      String expected = String.format(Locale.ROOT, example.format, example.arguments);
      System.out.println("      " + literal(expected) + ",");
    }
    System.out.println("  };\n  private FormattingGolden() {}\n}");
  }

  private static String literal(String text) {
    StringBuilder value = new StringBuilder("\"");
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '\\') value.append("\\\\");
      else if (c == '\"') value.append("\\\"");
      else if (c == '\n') value.append("\\n");
      else if (c == '\r') value.append("\\r");
      else if (c == '\t') value.append("\\t");
      else if (c < 32 || c > 126) value.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
      else value.append(c);
    }
    return value.append('\"').toString();
  }
}
