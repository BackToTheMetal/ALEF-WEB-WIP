package mpsj2cl.editorutilities;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/** Shared JVM/browser observations, including state after lookahead and failed calls. */
public final class EditorUtilitiesTest {
  private static final String[] INPUTS = {
      "", "a", " ", "  a b  ", "a,,b,", "a\tb\nc\rd\fe", "\u000Ba\u0085b\u00A0c",
      "😀", "a😀b😀", "😀😁😀", "\uD83D", "\uDE00", "\uD83Da\uDE00",
      "x\uD83D\uDE00y\uD83Dz\uDE00", "a.b/c\\d", "a:b;c,d", "雪éî"
  };
  private static final String[] DELIMITERS = {
      "", " ", " \t\n\r\f", ",", "abc", "a:a", "😀", "😀😁", "\uD83D", "\uDE00",
      "\uD83Da\uDE00", ".\\/", ":;,", "\u000B\u0085\u00A0", "雪"
  };

  public static String run() {
    StringBuilder result = new StringBuilder();
    for (String input : INPUTS) {
      for (String delimiters : DELIMITERS) {
        for (boolean returned : new boolean[] {false, true}) {
          result.append(escape(input)).append('|').append(escape(delimiters)).append('|').append(returned).append(':');
          appendTokens(result, new StringTokenizer(input, delimiters, returned));
          result.append('\n');
        }
      }
    }
    result.append("defaults:");
    appendTokens(result, new StringTokenizer(" a\tb\nc\rd\fe\u000Bf "));
    result.append('\n');
    for (boolean returned : new boolean[] {false, true}) {
      StringTokenizer tokenizer = new StringTokenizer("  a,b;c ", " ", returned);
      result.append("change:").append(returned).append(':').append(tokenizer.hasMoreTokens());
      result.append(':').append(escape(tokenizer.nextToken(",")));
      result.append(':').append(tokenizer.countTokens()).append(':').append(tokenizer.hasMoreElements());
      result.append(':').append(escape(tokenizer.nextToken(";")));
      result.append(':').append(escape(tokenizer.nextToken("")));
      result.append(':').append(exhausted(tokenizer)).append('\n');
    }
    // A failed nextToken must retain already consumed delimiters; hasMoreTokens must not consume them.
    StringTokenizer exhausted = new StringTokenizer("   ", " ");
    result.append("lookahead:").append(exhausted.hasMoreTokens()).append(':');
    result.append(escape(exhausted.nextToken(""))).append('\n');
    exhausted = new StringTokenizer("   ", " ");
    result.append("exhaustion:").append(exhausted(exhausted)).append(':');
    result.append(exhausted(exhausted, "")).append('\n');
    boolean nullInput = false;
    try { new StringTokenizer(null, null); } catch (NullPointerException expected) { nullInput = true; }
    require(nullInput, "null input constructor");
    for (String input : new String[] {"", "a"}) {
      for (boolean returned : new boolean[] {false, true}) {
        StringTokenizer tokenizer = new StringTokenizer(input, null, returned);
        int failures = 0;
        try { tokenizer.hasMoreTokens(); } catch (NullPointerException expected) { failures++; }
        try { tokenizer.countTokens(); } catch (NullPointerException expected) { failures++; }
        try { tokenizer.nextToken(); } catch (NullPointerException expected) { failures++; }
        result.append("null-recovery:").append(input).append(':').append(returned).append(':').append(failures).append(':');
        result.append(exhausted(tokenizer, "")).append('\n');
      }
    }
    StringTokenizer state = new StringTokenizer("a,b", ",");
    require(state.nextToken().equals("a"), "initial token");
    try { state.nextToken(null); throw new AssertionError("null delimiter change accepted"); }
    catch (NullPointerException expected) { /* Cursor must still point to the comma. */ }
    result.append("null-change:").append(escape(state.nextToken(""))).append('\n');
    return result.toString();
  }

  private static void appendTokens(StringBuilder result, StringTokenizer tokenizer) {
    int previous = tokenizer.countTokens();
    result.append(previous).append('[');
    while (tokenizer.hasMoreElements()) {
      require(tokenizer.hasMoreTokens(), "repeated lookahead");
      require(tokenizer.countTokens() == previous, "count must not consume");
      result.append(escape((String) tokenizer.nextElement())).append(';');
      require(tokenizer.countTokens() == --previous, "count after token");
    }
    require(previous == 0 && !tokenizer.hasMoreTokens(), "exhausted count");
    result.append(']').append(exhausted(tokenizer));
  }

  private static String exhausted(StringTokenizer tokenizer) {
    try { return escape(tokenizer.nextToken()); } catch (NoSuchElementException expected) { return "<end>"; }
  }

  private static String exhausted(StringTokenizer tokenizer, String delimiters) {
    try { return escape(tokenizer.nextToken(delimiters)); } catch (NoSuchElementException expected) { return "<end>"; }
  }

  private static String escape(String text) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c < 32 || c > 126 || "\\|:;[]".indexOf(c) >= 0) {
        result.append("\\u");
        for (int shift = 12; shift >= 0; shift -= 4) result.append("0123456789abcdef".charAt(c >>> shift & 15));
      } else result.append(c);
    }
    return result.toString();
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  public static void main(String[] args) { System.out.print(run()); }
}
