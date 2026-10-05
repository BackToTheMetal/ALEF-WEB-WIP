package mpsj2cl.openapi;

import java.util.function.Supplier;
import org.jetbrains.mps.openapi.model.SModelName;
import org.jetbrains.mps.openapi.model.SModelName.SModelNameCheck;

/** The same cases run against untouched MPS on the JVM and the browser replacement. */
public final class SModelNameTest {
  private SModelNameTest() {
  }

  public static String run() {
    StringBuilder result = new StringBuilder();
    String[] fullNames = {
        "", "a", "a.b", "a.b@generator", " a.b@tests ", "\ta.b\n", "a@x.y",
        "a@b@c", "a..b", ".a", "a.", "a.@tests", "@tests", "a@", "a b", "a\tb",
        "a\nb", "a/b", "1name", "class", "_", "var", "$", "a.$", "a@_",
        "\u00e9.\u4e2d\u6587", "\ud801\udc00.name", "name\ud800", "name\udc00",
        "name\u0301", "\u0301name", "a\u200cb", "a\u200db", "\u00a0name", "name\u00a0",
        "a" + (char) 0 + "b", "cl" + (char) 0 + "ass", "class" + (char) 0,
    };
    for (int index = 0; index < fullNames.length; index++) {
      final String value = fullNames[index];
      result.append("full/").append(index).append('|').append(quote(value)).append('|');
      result.append(construct(() -> new SModelName(value))).append('|');
      result.append(check(SModelName.checkModelName(value))).append('\n');
    }

    String[][] parts = {
        {null, "a", null}, {"", "a", ""}, {"pkg", "Model", "tests"},
        {"pkg.deep", "Model", "generator"}, {null, "", null}, {"", "", ""},
        {"pkg", "", null}, {"pkg", "", "tests"}, {"pkg@bad", "a", null},
        {"pkg", "a.b", null}, {"pkg", "a@b", null}, {"pkg", "a", "x@y"},
        {"a b", "c", null}, {"pkg", "a b", null}, {"pkg", "a", "x y"},
        {"pkg", "a", "x.y"}, {"pkg.", "a", null}, {".pkg", "a", null},
        {"pkg", "class", null}, {null, "_", null}, {"pkg", "record", "record"},
        {"\u4e2d\u6587", "\ud801\udc00", "tests"}, {null, "\ta", null},
    };
    for (int index = 0; index < parts.length; index++) {
      String[] value = parts[index];
      result.append("parts/").append(index).append('|');
      result.append(construct(() -> new SModelName(value[0], new StringBuilder(value[1]), value[2]))).append('|');
      result.append(check(SModelName.checkModelName(value[0], new StringBuilder(value[1]), value[2]))).append('\n');
    }

    String[][] qualified = {
        {"a", null}, {"pkg.a", "tests"}, {"", null}, {"a.", null}, {".a", "tests"},
        {"a@b", "tests"}, {"a.b", "x@y"}, {"a..b", "x.y"}, {"class", null},
        {"\u03b1.\u03b2", "tests"}, {" a ", null},
    };
    for (int index = 0; index < qualified.length; index++) {
      String[] value = qualified[index];
      result.append("qualified/").append(index).append('|');
      result.append(construct(() -> new SModelName(new StringBuilder(value[0]), value[1]))).append('|');
      result.append(check(SModelName.checkModelName(new StringBuilder(value[0]), value[1]))).append('\n');
    }

    String keywords = "abstract assert boolean break byte case catch char class const continue default do double else "
        + "enum extends final finally float for goto if implements import instanceof int interface long native new "
        + "package private protected public return short static strictfp super switch synchronized this throw throws "
        + "transient try void volatile while true false null _ var yield record sealed permits module open requires "
        + "transitive exports opens to uses provides with when";
    for (String word : keywords.split(" ")) {
      result.append("word/").append(word).append('|').append(check(SModelName.checkModelName(word)))
          .append('|').append(check(SModelName.checkModelName("pkg." + word)))
          .append('|').append(check(SModelName.checkModelName(word + "x"))).append('\n');
    }
    for (SModelNameCheck value : SModelNameCheck.values()) {
      result.append("diagnostic/").append(check(value)).append('\n');
    }

    // Preserve every result, including both sides of every pinned Unicode range.
    StringBuilder unicodeResults = new StringBuilder();
    for (int codepoint : IdentifierBoundaryCases.CODEPOINTS) {
      String value = new String(Character.toChars(codepoint));
      for (String candidate : new String[] {value, value + "x", "x" + value, "pkg." + value, "x" + value + "y"}) {
        unicodeResults.append((char) ('A' + SModelName.checkModelName(candidate).ordinal()));
      }
    }
    result.append("unicode-boundaries/").append(IdentifierBoundaryCases.CODEPOINTS.length)
        .append('|').append(unicodeResults).append('\n');
    return result.toString();
  }

  private static String construct(Supplier<SModelName> supplier) {
    try {
      SModelName value = supplier.get();
      return "ok|" + quote(value.getValue()) + '|' + quote(value.getLongName()) + '|'
          + quote(value.getNamespace()) + '|' + quote(value.getSimpleName()) + '|'
          + quote(value.getShortNameWithStereotype()) + '|' + quote(value.getStereotype()) + '|'
          + value.isEmpty() + '|' + value.hasStereotype() + '|' + value.hasStereotype(null) + '|'
          + value.hasStereotype("") + '|' + value.hasStereotype("tests") + '|'
          + value.hasStereotype(new StringBuilder(value.getStereotype())) + '|'
          + value.hashCode() + '|' + quote(value.toString()) + '|'
          + value.equals(new SModelName(value.getValue())) + '|'
          + equalsNull(value) + '|' + value.equals(value.getValue()) + '|'
          + (value.withStereotype(value.getStereotype()) == value) + '|'
          + transform(value::withoutStereotype) + '|'
          + transform(() -> value.withStereotype(null)) + '|'
          + transform(() -> value.withStereotype("tests"));
    } catch (IllegalArgumentException exception) {
      return "IllegalArgumentException|" + quote(exception.getMessage());
    }
  }

  // Intentionally verify the upstream equals(null) contract, rather than nullness.
  @SuppressWarnings("EqualsNull")
  private static boolean equalsNull(SModelName value) { return value.equals(null); }

  private static String transform(Supplier<SModelName> supplier) {
    try {
      return quote(supplier.get().getValue());
    } catch (IllegalArgumentException exception) {
      return "IllegalArgumentException:" + quote(exception.getMessage());
    }
  }

  private static String check(SModelNameCheck value) {
    return value.name() + ':' + quote(value.getProblemDescription());
  }

  private static String quote(String value) {
    StringBuilder result = new StringBuilder();
    String hex = "0123456789abcdef";
    for (int index = 0; index < value.length(); index++) {
      char c = value.charAt(index);
      if (c < 32 || c > 126 || c == '|' || c == '\\') {
        result.append("\\u").append(hex.charAt((c >>> 12) & 15)).append(hex.charAt((c >>> 8) & 15))
            .append(hex.charAt((c >>> 4) & 15)).append(hex.charAt(c & 15));
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }

  public static void main(String[] args) {
    System.out.print(run());
  }
}
