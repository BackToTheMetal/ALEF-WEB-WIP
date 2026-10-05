package mpsj2cl.formatting;

import java.util.List;
import java.util.Locale;
import javaemul.internal.MpsFormatter;

public final class FormattingTest {
  private FormattingTest() {}

  public static String run() {
    List<FormattingCases.Case> cases = FormattingCases.all();
    require(cases.size() == FormattingGolden.VALUES.length, "golden inventory size");
    Locale[] locales = {Locale.ROOT, Locale.ENGLISH, Locale.US, null};
    for (int i = 0; i < cases.size(); i++) {
      FormattingCases.Case example = cases.get(i);
      String expected = FormattingGolden.VALUES[i];
      String actual = MpsFormatter.format(example.format, example.arguments);
      require(expected.equals(actual), "helper case " + i + " expected [" + expected + "] actual [" + actual + "]");
      for (Locale locale : locales) {
        require(expected.equals(MpsFormatter.format(locale, example.format, example.arguments)), "explicit locale helper case " + i);
        require(expected.equals(String.format(locale, example.format, example.arguments)), "String.format locale case " + i);
      }
      require(expected.equals(String.format(example.format, example.arguments)), "String.format default case " + i);
      require(expected.equals(example.format.formatted(example.arguments)), "String.formatted case " + i);
    }
    expectUnsupported(() -> MpsFormatter.format("%e", 1.0));
    expectUnsupported(() -> MpsFormatter.format("%g", 1.0));
    expectUnsupported(() -> MpsFormatter.format("%+d", 1));
    expectUnsupported(() -> MpsFormatter.format("%,d", 1000));
    expectUnsupported(() -> MpsFormatter.format("%#x", 15));
    expectUnsupported(() -> MpsFormatter.format("%tY", 0L));
    expectUnsupported(() -> MpsFormatter.format("%tT", Long.MAX_VALUE));
    expectUnsupported(() -> MpsFormatter.format("%.2d", 1));
    expectUnsupported(() -> MpsFormatter.format("%01s", "s"));
    Locale defaultLocale = Locale.getDefault();
    if (defaultLocale != Locale.ROOT && defaultLocale != Locale.ENGLISH && defaultLocale != Locale.US) {
      expectUnsupported(() -> MpsFormatter.format(defaultLocale, "%d", 1));
    }
    expectMalformed(() -> MpsFormatter.format("%s"));
    expectMalformed(() -> MpsFormatter.format("%"));
    expectMalformed(() -> MpsFormatter.format("%<s", "x"));
    expectMalformed(() -> MpsFormatter.format("%0$s", "x"));
    expectMalformed(() -> MpsFormatter.format("%-03d", 1));
    expectMalformed(() -> MpsFormatter.format("%-s", "x"));
    expectMalformed(() -> MpsFormatter.format("%d", 1.0));
    expectMalformed(() -> MpsFormatter.format("%x", 'x'));
    expectMalformed(() -> MpsFormatter.format("%c", -1));
    expectMalformed(() -> MpsFormatter.format("%c", 0x110000));
    expectMalformed(() -> MpsFormatter.format("%f", 1));
    return "Formatting: " + cases.size() + " JVM golden cases passed through helper, String.format and formatted; unsupported features fail explicitly";
  }

  public static void main(String[] arguments) {
    System.out.println(run());
  }

  private static void expectUnsupported(Runnable action) {
    try {
      action.run();
      throw new AssertionError("Expected unsupported formatting feature");
    } catch (UnsupportedOperationException expected) {
    }
  }

  private static void expectMalformed(Runnable action) {
    try {
      action.run();
      throw new AssertionError("Expected invalid format or argument");
    } catch (IllegalArgumentException expected) {
    }
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
