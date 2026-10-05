package java.util.regex;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Validated Java regex subset implemented through native Unicode RegExp.
 * Unsupported Java constructs fail before execution; no arbitrary pattern is
 * silently treated as ECMAScript syntax. See tests/regex/README.md.
 */
public final class Pattern {
  public static final int UNIX_LINES = 1;
  public static final int CASE_INSENSITIVE = 2;
  public static final int COMMENTS = 4;
  public static final int MULTILINE = 8;
  public static final int LITERAL = 16;
  public static final int DOTALL = 32;
  public static final int UNICODE_CASE = 64;
  public static final int CANON_EQ = 128;
  public static final int UNICODE_CHARACTER_CLASS = 256;
  private final String original;
  private final String translated;
  private final int compileFlags;
  final int groupCount;
  final boolean hasBoundaries;

  private Pattern(String regex, int flags) {
    if (regex == null) throw new NullPointerException("regex");
    original = regex;
    compileFlags = flags;
    Translator translator = new Translator(regex, (flags & CASE_INSENSITIVE) != 0);
    translated = translator.translate();
    groupCount = translator.groupCount;
    hasBoundaries = translator.boundaryCount > 0;
    expression(false);
  }

  public static Pattern compile(String regex) { return new Pattern(regex, 0); }

  public static Pattern compile(String regex, int flags) {
    if ((flags & ~511) != 0) throw new IllegalArgumentException("Unknown regex flags");
    if ((flags & ~CASE_INSENSITIVE) != 0) throw new UnsupportedOperationException("Browser regex profile supports only flags=0 or CASE_INSENSITIVE");
    return new Pattern(regex, flags);
  }

  public static boolean matches(String regex, CharSequence input) {
    return compile(regex).matcher(input).matches();
  }

  public Matcher matcher(CharSequence input) { return new Matcher(this, input); }
  public String pattern() { return original; }
  public int flags() { return compileFlags; }
  @Override public String toString() { return original; }

  public static String quote(String value) {
    if (value == null) throw new NullPointerException("value");
    return "\\Q" + value.replace("\\E", "\\E\\\\E\\Q") + "\\E";
  }

  NativeRegExp expression(boolean fullMatch) {
    return expression(fullMatch, false);
  }

  NativeRegExp expression(boolean fullMatch, boolean sticky) {
    // An absolute end assertion forces backtracking to consume the whole input.
    // Native dollar would also accept a position before a terminal newline.
    // Keep dollar as well: WebKit's Unicode any-character assertion can fail at
    // the low half of an intact surrogate pair, which alone looks like EOF.
    String source = fullMatch ? "^(?:" + translated + ")$(?![\\s\\S])" : translated;
    try {
      return new NativeRegExp(source, sticky ? "uy" : "gu");
    } catch (Throwable error) {
      throw new PatternSyntaxException("Invalid expression in browser regex subset", original, -1);
    }
  }

  private static final class Translator {
    private static final String SPACE = " \\t\\n\\x0B\\f\\r";
    private static final String WORD = "a-zA-Z_0-9";
    private final String source;
    private final boolean asciiCaseInsensitive;
    private int position;
    private int groupCount;
    private int boundaryCount;

    Translator(String source, boolean asciiCaseInsensitive) {
      this.source = source;
      this.asciiCaseInsensitive = asciiCaseInsensitive;
    }

    String translate() {
      String result = expression(false);
      if (position != source.length()) throw syntax("Unmatched closing parenthesis");
      return result;
    }

    private String expression(boolean group) {
      StringBuilder result = new StringBuilder();
      while (position < source.length() && source.charAt(position) != ')') {
        char c = source.charAt(position++);
        if (c == '|') {
          result.append('|');
          continue;
        }
        if (c == '*' || c == '+' || c == '?') throw syntax("Dangling quantifier");
        String atom;
        int nestedCaptures = 0;
        int nestedBoundaries = 0;
        if (c == '(') {
          int capturesBefore = groupCount;
          int boundariesBefore = boundaryCount;
          boolean capturing = true;
          String prefix = "(";
          if (position < source.length() && source.charAt(position) == '?') {
            if (source.startsWith("?:", position)) {
              prefix = "(?:";
              capturing = false;
              position += 2;
            } else {
              throw unsupported("lookaround, atomic groups, named groups, or inline flags");
            }
          } else {
            groupCount++;
          }
          atom = prefix + expression(true) + ")";
          nestedCaptures = groupCount - capturesBefore - (capturing ? 1 : 0);
          nestedBoundaries = boundaryCount - boundariesBefore;
        } else if (c == '[') {
          atom = foldedClass(characterClass(false));
        } else if (c == '\\') {
          atom = escape(false);
        } else if (c == '.') {
          atom = "[^\\n\\r\\u0085\\u2028\\u2029]";
        } else if (c == '^') {
          atom = "^";
        } else if (c == '$') {
          atom = "(?=(?:\\r\\n|(?<!\\r)\\n|[\\r\\u0085\\u2028\\u2029])?$(?![\\s\\S]))";
        } else if (c == '{') {
          throw syntax("Illegal repetition");
        } else {
          atom = caseLiteral(c);
        }
        result.append(atom);
        if (position < source.length()) {
          char q = source.charAt(position);
          if (q == '*' || q == '+' || q == '?') {
            if (nestedBoundaries > 0) throw unsupported("quantified groups containing word boundaries");
            if (q != '?' && nestedCaptures > 0) {
              throw unsupported("repeated groups containing nested captures");
            }
            result.append(q);
            position++;
            quantifierSuffix(result);
          } else if (q == '{') {
            if (nestedBoundaries > 0) throw unsupported("quantified groups containing word boundaries");
            if (nestedCaptures > 0) throw unsupported("counted groups containing nested captures");
            int start = position++;
            while (position < source.length() && source.charAt(position) != '}') {
              char number = source.charAt(position++);
              if ((number < '0' || number > '9') && number != ',') throw syntax("Illegal repetition");
            }
            if (position >= source.length()) throw syntax("Unclosed repetition");
            validateCount(source.substring(start + 1, position));
            result.append(source, start, ++position);
            quantifierSuffix(result);
          }
        }
      }
      if (group) {
        if (position >= source.length()) throw syntax("Unclosed group");
        position++;
      }
      return result.toString();
    }

    private void quantifierSuffix(StringBuilder result) {
      if (position < source.length()) {
        if (source.charAt(position) == '+') throw unsupported("possessive quantifiers");
        if (source.charAt(position) == '?') {
          result.append('?');
          position++;
        }
      }
    }

    private void validateCount(String count) {
      int comma = count.indexOf(',');
      if (comma != count.lastIndexOf(',')) throw syntax("Illegal repetition");
      int minimum = integer(comma < 0 ? count : count.substring(0, comma));
      if (comma >= 0 && comma + 1 < count.length() && integer(count.substring(comma + 1)) < minimum) {
        throw syntax("Illegal repetition range");
      }
    }

    private int integer(String number) {
      if (number.isEmpty()) throw syntax("Illegal repetition");
      int value = 0;
      for (int i = 0; i < number.length(); i++) {
        int digit = number.charAt(i) - '0';
        if (digit < 0 || digit > 9 || value > (Integer.MAX_VALUE - digit) / 10) {
          throw syntax("Illegal repetition");
        }
        value = value * 10 + digit;
      }
      return value;
    }

    private String characterClass(boolean nested) {
      StringBuilder result = new StringBuilder();
      boolean first = true;
      if (position < source.length() && source.charAt(position) == '^') {
        if (nested) throw unsupported("negated nested character classes");
        result.append('^');
        position++;
      }
      while (position < source.length()) {
        char c = source.charAt(position++);
        if (c == ']' && !first) return result.toString();
        if (c == '&' && position < source.length() && source.charAt(position) == '&') {
          throw unsupported("character class intersection");
        }
        if (c == '[') {
          if (result.length() > 0 && result.charAt(result.length() - 1) == '-') {
            throw unsupported("ranges crossing nested character classes");
          }
          result.append(characterClass(true));
          if (position < source.length() && source.charAt(position) == '-') {
            throw unsupported("ranges crossing nested character classes");
          }
        } else if (c == '\\') {
          result.append(escape(true));
        } else if (c == '-') {
          // Class-edge hyphens are literals. Preserve that fact when a nested
          // union is flattened; [a[-]z] must never turn into the range [a-z].
          result.append(first || (position < source.length() && source.charAt(position) == ']') ? "\\-" : "-");
        } else {
          result.append(literal(c));
        }
        first = false;
      }
      throw syntax("Unclosed character class");
    }

    private String escape(boolean inClass) {
      if (position >= source.length()) throw syntax("Trailing backslash");
      char escaped = source.charAt(position++);
      switch (escaped) {
        case 'Q':
          StringBuilder quoted = new StringBuilder();
          while (position < source.length()) {
            if (source.startsWith("\\E", position)) {
              position += 2;
              break;
            }
            char c = source.charAt(position++);
            quoted.append(inClass ? (c == '-' ? "\\-" : literal(c)) : caseLiteral(c));
          }
          return quoted.toString();
        case 'd': return inClass ? "0-9" : "[0-9]";
        case 'w': return inClass ? WORD : "[" + WORD + "]";
        case 's': return inClass ? SPACE : "[" + SPACE + "]";
        case 'D': case 'W': case 'S':
          if (inClass) throw unsupported("complement escapes inside character classes");
          return "[^" + (escaped == 'D' ? "0-9" : escaped == 'W' ? WORD : SPACE) + "]";
        case 't': return "\\t";
        case 'n': return "\\n";
        case 'r': return "\\r";
        case 'f': return "\\f";
        case 'a': return "\\x07";
        case 'e': return "\\x1B";
        case 'x': return hexEscape(2, "\\x", inClass);
        case 'u': return hexEscape(4, "\\u", inClass);
        case 'b': case 'B':
          if (inClass) throw syntax("Illegal character class escape");
          if (source.startsWith("{g}", position)) throw unsupported("grapheme boundary");
          boundaryCount++;
          // Pinned Unicode ranges preserve Java 21's nonspacing-mark/base rule.
          // These assertions are input-independent and introduce no captures.
          return escaped == 'b' ? JavaRegexUnicodeData.BOUNDARY : JavaRegexUnicodeData.NON_BOUNDARY;
        case 'A':
          if (inClass) throw syntax("Illegal character class escape");
          return "^";
        case 'z':
          if (inClass) throw syntax("Illegal character class escape");
          return "$(?![\\s\\S])";
        case 'p': case 'P': case 'G': case 'Z':
        case 'R': case 'X': case 'h': case 'H': case 'v': case 'V': case 'k':
          throw unsupported("escape \\" + escaped);
        default:
          if (escaped >= '0' && escaped <= '9') throw unsupported("backreferences or octal escapes");
          if ((escaped >= 'a' && escaped <= 'z') || (escaped >= 'A' && escaped <= 'Z')) {
            throw syntax("Illegal escape");
          }
          return inClass && escaped == '-' ? "\\-" : literal(escaped);
      }
    }

    private String hexEscape(int count, String prefix, boolean inClass) {
      if (position < source.length() && source.charAt(position) == '{') throw unsupported("braced code-point escapes");
      if (position + count > source.length()) throw syntax("Incomplete hex escape");
      String hex = source.substring(position, position + count);
      for (int i = 0; i < hex.length(); i++) {
        char c = hex.charAt(i);
        if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
          throw syntax("Illegal hex escape");
        }
      }
      position += count;
      if (!inClass && asciiCaseInsensitive) {
        char value = (char) Integer.parseInt(hex, 16);
        if (isAsciiLetter(value)) return caseLiteral(value);
      }
      return prefix + hex;
    }

    private String caseLiteral(char value) {
      if (asciiCaseInsensitive && isAsciiLetter(value)) {
        char lower = (char) (value | 32);
        return "[" + (char) (lower - 32) + lower + "]";
      }
      return literal(value);
    }

    private static boolean isAsciiLetter(char value) {
      return (value >= 'A' && value <= 'Z') || (value >= 'a' && value <= 'z');
    }

    private String foldedClass(String body) {
      if (!asciiCaseInsensitive) return "[" + body + "]";
      boolean negated = body.startsWith("^");
      String positive = negated ? body.substring(1) : body;
      if (positive.startsWith("-")) positive = "\\-" + positive.substring(1);
      NativeRegExp members;
      try {
        // The validated union/range syntax is already equivalent to Java. Test
        // its finite ASCII domain to close the class under ASCII case mapping,
        // before applying negation. Non-ASCII members are left untouched.
        members = new NativeRegExp("[" + positive + "]", "u");
      } catch (Throwable error) {
        throw syntax("Invalid character class");
      }
      StringBuilder closure = new StringBuilder();
      for (char upper = 'A'; upper <= 'Z'; upper++) {
        char lower = (char) (upper + 32);
        if (members.exec(String.valueOf(upper)) != null || members.exec(String.valueOf(lower)) != null) {
          closure.append(upper).append(lower);
        }
      }
      // Place added letters first, so an existing terminal '-' stays literal.
      return "[" + (negated ? "^" : "") + closure + positive + "]";
    }

    private static String literal(char c) {
      return "\\^$.*+?()[]{}|".indexOf(c) >= 0 ? "\\" + c : String.valueOf(c);
    }

    private PatternSyntaxException syntax(String message) {
      return new PatternSyntaxException(message, source, Math.max(0, position - 1));
    }

    private UnsupportedOperationException unsupported(String feature) {
      return new UnsupportedOperationException("Browser regex does not support " + feature + ": " + source);
    }
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "RegExp")
  static final class NativeRegExp {
    public int lastIndex;
    NativeRegExp(String source, String flags) {}
    native NativeMatch exec(String input);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Array")
  static final class NativeMatch {
    public int index;
    public int length;
    native String at(int index);
  }
}
