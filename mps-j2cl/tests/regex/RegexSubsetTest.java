package mpsj2cl.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Exact result oracle for the supported Java/browser regular-expression subset. */
public final class RegexSubsetTest {
  private static final String[] PATTERNS = {
      "", ".", ".*", ".+", ".?", ".??", "a*", "a*?", "a|", "(a)?b", "(?:a|aa)*",
      "a{0,2}", "x{2,4}?", "[a-z[A-Z]]+", "[a-zA-Z$[_]][a-zA-Z0-9$[_]]*",
      "\\d+", "\\s+", "\\S+", "\\w+", "\\W+", "\\D+", "^.$", "$", "^", "a$",
      "\\A.\\z", Pattern.quote("[]$" + "{}"), Pattern.quote("a\\Eb"), "[\\Q-]\\E]",
      "[^a]+", "[a-z0-9]+", "[]a]+",
      "(.*)(<\\{)(.+)(\\}>)(.*)",
      "(\\w+)=\"(([^\"]|\\\\\")*)\";",
      "\\x41", "\\u0041", "😀+", "[😀-🙏]", "[😀]", "\uD800", "\uDC00",
      "\\uD83D\\uDE00", "[\\uD800-\\uDFFF]", "(a|(b))", "\\a\\e", "[\\s_]+",
      "\\b", "\\B", "\\bword\\b", "\\b" + Pattern.quote("a.b") + "\\b", "\\b.\\b", "\\B.\\B",
      "\\b?", "\\B??", "\\b+", "\\B*", "\\b{0,2}", "\\B{2}", "(\\b)a", "(?:\\b|a)",
      "aBc", "[a-z]+", "[^a-z]+", "[a-c[X-Z]]+", "[@-C]+", "[X-a]+", "[-a]+", "[a-]+",
      "[^-a]+", "[^a-]+", "[\\x41-\\u0043]+", "[\\QK-s\\E]+", "k", "s", "é", "Σ"
      , "[a[-]z]+", "[[-]az]+", "[a[b-]z]+", "[--a]+"
  };
  private static final String[] INPUTS = {
      "", "a", "b", "aa", "ab", "aab", "A", "_", "$", "a0_b", "0a", "123",
      "a\n", "a\r", "a\r\n", "a\u0085", "a\u2028", "a\u2029",
      "\n", "\r\n", "\u0085", "\u2028", "\t \u000B\f\r\n", "\u00A0", "\u2003",
      "é", "😀", "😀x", "x😀", "\uD800", "\uDC00", "\uD800x", "x\uDC00",
      "[]$" + "{}", "a\\Eb", "-", "]", "aaaa", "xxx",
      "prefix<{role}>suffix", "<{one}><{two}>", "<{}>", "<{a\nb}>",
      "name=\"value\";other=\"v\\\"x\";", "name=\"a\";\n", "\u0007\u001B",
      "aBc", "ABC", "abc", "word", "WORD", "sword", "wordé", "éword", " word ", "word_word",
      "a.b", "A.B", "a.bé", "éa.b", "a\u0301", "\u0301a", "kKK", "sSſ", "éÉ", "σΣς",
      "iIİı", "@AZ[\\]^_`az-", "é😀é", "a😀a",
      "é\u0301", "_\u0301", "1\u0301", "a\u0301\u0301", "a\uD834\uDD67",
      "a\uD834\uDD67\u0301", "\uD801\uDC00\u0301", "\u0301\u0301a", "a\u0903"
  };

  public static String run() {
    StringBuilder output = new StringBuilder();
    for (int flags : new int[]{0, Pattern.CASE_INSENSITIVE}) {
    for (String regex : PATTERNS) {
      Pattern pattern = Pattern.compile(regex, flags);
      if (!regex.equals(pattern.pattern()) || pattern.flags() != flags) throw new AssertionError("Pattern identity");
      for (String input : INPUTS) {
        Matcher matcher = pattern.matcher(input);
        output.append(flags).append(':').append(escape(regex)).append('\t').append(escape(input)).append('\t').append(matcher.groupCount()).append('|');
        if (matcher.matches()) {
          appendMatch(output, matcher);
        } else {
          output.append('-');
        }
        output.append('|');
        matcher.reset();
        int count = 0;
        while (matcher.find()) {
          if (++count > input.length() + 2) throw new AssertionError("Non-progressing find");
          appendMatch(output, matcher);
          output.append(';');
        }
        output.append("|retry=");
        if (matcher.find()) appendMatch(output, matcher); else output.append('-');
        output.append('\n');
      }
    }
    }
    for (String regex : new String[]{"", ".", ".*?", "(.)?", "\\b", "\\B", "\\b.\\b", "\\B.\\B"}) {
      for (int start = 0; start <= 3; start++) {
        Matcher matcher = Pattern.compile(regex).matcher("😀x");
        output.append("offset:").append(escape(regex)).append(':').append(start).append('\t');
        if (matcher.find(start)) appendMatch(output, matcher); else output.append('-');
        output.append('\n');
      }
    }
    Matcher state = Pattern.compile("(a)?(b)").matcher("b");
    expectNoMatch(state);
    if (state.groupCount() != 2 || !state.matches() || state.group(1) != null || !"b".equals(state.group(2))) {
      throw new AssertionError("Optional group state");
    }
    state.reset("ab");
    expectNoMatch(state);
    if (!state.find() || !"a".equals(state.group(1))) throw new AssertionError("Reset input");
    boolean badGroup = false;
    try { state.group(3); } catch (IndexOutOfBoundsException expected) { badGroup = true; }
    if (!badGroup) throw new AssertionError("Group bounds");
    Matcher empty = Pattern.compile("").matcher("");
    if (!empty.matches() || empty.find()) throw new AssertionError("Find after empty matches");
    for (String invalid : new String[]{"[", "(", "\\", "*", "a{", "[z-a]", "\\xZ0", "\\q",
        "a{,2}", "a{2,1}", "a{1,2,3}", "a{2147483648}", "[\\b]", "[\\B]"}) {
      boolean rejected = false;
      try { Pattern.compile(invalid); } catch (PatternSyntaxException expected) { rejected = true; }
      if (!rejected) throw new AssertionError("Invalid regex accepted: " + invalid);
    }
    output.append("state-and-syntax\tPASS\n");
    StringBuilder longWords = new StringBuilder();
    for (int i = 0; i < 2000; i++) longWords.append("WORD ");
    longWords.append("needle");
    Matcher words = Pattern.compile("\\bword\\b", Pattern.CASE_INSENSITIVE).matcher(longWords);
    int wordCount = 0;
    while (words.find()) wordCount++;
    if (wordCount != 2000) throw new AssertionError("Long editor text search");
    Matcher needle = Pattern.compile("\\bneedle\\b").matcher(longWords);
    if (!needle.find()) throw new AssertionError("Long editor text suffix search");
    output.append("long-word-search\t").append(wordCount).append('|').append(needle.start()).append(':').append(needle.end()).append('\n');
    StringBuilder astral = new StringBuilder();
    for (int i = 0; i < 500; i++) astral.append("😀x ");
    astral.append("needle");
    needle.reset(astral);
    if (!needle.find()) throw new AssertionError("Long supplementary editor text search");
    output.append("long-supplementary-search\t").append(needle.start()).append(':').append(needle.end()).append('\n');
    return output.toString();
  }

  public static String runUnsupported() {
    for (String regex : new String[]{"(?=a)", "(?<=a)", "(?!a)", "(?>a)", "(?<name>a)",
        "(?i)a", "(a)\\1", "\\p{L}", "\\b{g}", "[a-z&&[^b]]", "[a[^b]]", "a++", "a*+",
        "\\R", "\\X", "\\x{1F600}", "[\\S_]", "(a(b)?)+", "(?:(a)|b)*", "[[a]-z]", "(\\b)?a", "(?:\\b|a)*"}) {
      boolean rejected = false;
      try { Pattern.compile(regex); } catch (UnsupportedOperationException expected) { rejected = true; }
      if (!rejected) throw new AssertionError("Unsupported Java feature accepted: " + regex);
    }
    for (int flags : new int[]{Pattern.UNIX_LINES, Pattern.MULTILINE, Pattern.LITERAL, Pattern.DOTALL,
        Pattern.COMMENTS, Pattern.UNICODE_CASE, Pattern.CANON_EQ, Pattern.UNICODE_CHARACTER_CLASS,
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE}) {
      boolean flagsRejected = false;
      try { Pattern.compile("a", flags); } catch (UnsupportedOperationException expected) { flagsRejected = true; }
      if (!flagsRejected) throw new AssertionError("Unsupported flags accepted: " + flags);
    }
    return "PASS";
  }

  private static void expectNoMatch(Matcher matcher) {
    boolean rejected = false;
    try { matcher.group(); } catch (IllegalStateException expected) { rejected = true; }
    if (!rejected) throw new AssertionError("Group available before match");
  }

  private static void appendMatch(StringBuilder output, Matcher matcher) {
    output.append(matcher.start()).append(':').append(matcher.end());
    for (int group = 0; group <= matcher.groupCount(); group++) {
      String value = matcher.group(group);
      output.append(',').append(value == null ? "<null>" : escape(value));
    }
  }

  private static String escape(String text) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '\\') {
        result.append("\\\\");
      } else if (c < 32 || c >= 127) {
        result.append("\\u");
        for (int shift = 12; shift >= 0; shift -= 4) {
          result.append("0123456789ABCDEF".charAt((c >>> shift) & 15));
        }
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
