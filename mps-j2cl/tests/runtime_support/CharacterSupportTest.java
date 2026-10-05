package mpsj2cl.runtime;

/** Digests all classification/mapping boundaries through the public Character APIs. */
final class CharacterSupportTest {
  private static int flags(int cp) {
    return (Character.isDigit(cp) ? 1 : 0) | (Character.isLetter(cp) ? 2 : 0)
        | (Character.isLowerCase(cp) ? 4 : 0) | (Character.isUpperCase(cp) ? 8 : 0)
        | (Character.isTitleCase(cp) ? 16 : 0) | (Character.isWhitespace(cp) ? 32 : 0)
        | (Character.isSpaceChar(cp) ? 64 : 0) | (Character.isLetterOrDigit(cp) ? 128 : 0)
        | (Character.isJavaIdentifierStart(cp) ? 256 : 0) | (Character.isJavaIdentifierPart(cp) ? 512 : 0);
  }

  private static int flags(char cp) {
    return (Character.isDigit(cp) ? 1 : 0) | (Character.isLetter(cp) ? 2 : 0)
        | (Character.isLowerCase(cp) ? 4 : 0) | (Character.isUpperCase(cp) ? 8 : 0)
        | (Character.isTitleCase(cp) ? 16 : 0) | (Character.isWhitespace(cp) ? 32 : 0)
        | (Character.isSpaceChar(cp) ? 64 : 0) | (Character.isLetterOrDigit(cp) ? 128 : 0)
        | (Character.isJavaIdentifierStart(cp) ? 256 : 0) | (Character.isJavaIdentifierPart(cp) ? 512 : 0);
  }

  static String run() {
    int properties = 1, mappings = 1, digits = 1, chars = 1, count = 0;
    int[] radices = {-1, 0, 1, 2, 8, 10, 16, 35, 36, 37};
    for (int[] part : CharacterBoundaryCases.CODEPOINTS) {
      for (int cp : part) {
        count++;
        properties = properties * 31 + flags(cp);
        mappings = mappings * 31 + Character.toLowerCase(cp);
        mappings = mappings * 31 + Character.toUpperCase(cp);
        mappings = mappings * 31 + Character.toTitleCase(cp);
        for (int radix : radices) { digits = digits * 31 + Character.digit(cp, radix); }
        if (cp >= 0 && cp <= 65535) {
          char value = (char) cp;
          chars = chars * 31 + flags(value);
          chars = chars * 31 + Character.toLowerCase(value);
          chars = chars * 31 + Character.toUpperCase(value);
          chars = chars * 31 + Character.toTitleCase(value);
          for (int radix : radices) { chars = chars * 31 + Character.digit(value, radix); }
        }
      }
    }
    return "Character:" + count + ":" + Integer.toHexString(properties) + ":"
        + Integer.toHexString(mappings) + ":" + Integer.toHexString(digits) + ":"
        + Integer.toHexString(chars) + "\n";
  }
}
