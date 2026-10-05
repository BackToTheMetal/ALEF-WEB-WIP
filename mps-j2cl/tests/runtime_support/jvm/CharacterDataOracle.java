package mpsj2cl.runtime;

import javaemul.internal.CharacterData;

/** Exhaustively compares the actual generated helper with the stock Java 21 runtime. */
public final class CharacterDataOracle {
  private static void test(int cp) {
    int expected = (Character.isDigit(cp) ? 1 : 0) | (Character.isLetter(cp) ? 2 : 0)
        | (Character.isLowerCase(cp) ? 4 : 0) | (Character.isUpperCase(cp) ? 8 : 0)
        | (Character.isTitleCase(cp) ? 16 : 0) | (Character.isWhitespace(cp) ? 32 : 0)
        | (Character.isSpaceChar(cp) ? 64 : 0) | (Character.isJavaIdentifierStart(cp) ? 128 : 0)
        | (Character.isJavaIdentifierPart(cp) ? 256 : 0);
    if (CharacterData.properties(cp) != expected
        || CharacterData.toLowerCase(cp) != Character.toLowerCase(cp)
        || CharacterData.toUpperCase(cp) != Character.toUpperCase(cp)
        || CharacterData.toTitleCase(cp) != Character.toTitleCase(cp)) {
      throw new AssertionError("Character properties/case mismatch at " + cp);
    }
    for (int radix = 0; radix <= 37; radix++) {
      if (CharacterData.digit(cp, radix) != Character.digit(cp, radix)) {
        throw new AssertionError("Character digit mismatch at " + cp + " radix " + radix);
      }
    }
    if (cp >= 0 && cp <= 65535) {
      char c = (char) cp;
      if ((char) CharacterData.toLowerCase(c) != Character.toLowerCase(c)
          || (char) CharacterData.toUpperCase(c) != Character.toUpperCase(c)
          || (char) CharacterData.toTitleCase(c) != Character.toTitleCase(c)
          || ((CharacterData.properties(c) & 128) != 0) != Character.isJavaIdentifierStart(c)
          || ((CharacterData.properties(c) & 256) != 0) != Character.isJavaIdentifierPart(c)) {
        throw new AssertionError("Character char mapping mismatch at " + cp);
      }
    }
  }

  public static void main(String[] args) {
    for (int cp = 0; cp <= 0x10ffff; cp++) { test(cp); }
    for (int cp : new int[] {Integer.MIN_VALUE, -1, 0x110000, Integer.MAX_VALUE}) { test(cp); }
    System.out.println("CharacterData: PASS; all 1114112 code points, char mappings, radices 0..37, invalid int values");
  }
}
