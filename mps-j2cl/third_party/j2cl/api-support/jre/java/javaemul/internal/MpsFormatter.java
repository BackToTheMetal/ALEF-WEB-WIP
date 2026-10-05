package javaemul.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * The formatting subset used by the MPS browser profile, not a general Formatter.
 * Supports s/b/c/d/x/f, tT, percent and newline, argument indices/reuse, space
 * widths, left alignment, numeric zero padding, and s/b/f precision.
 * No-locale calls use ROOT punctuation. Other conversions, flags and locales
 * fail explicitly instead of silently producing an approximation.
 */
public final class MpsFormatter {
  private MpsFormatter() {}

  public static String format(String format, Object... arguments) {
    return format(Locale.ROOT, format, arguments);
  }

  public static String format(Locale locale, String format, Object... arguments) {
    Objects.requireNonNull(format, "format");
    if (locale != null && locale != Locale.ROOT && locale != Locale.ENGLISH && locale != Locale.US) {
      throw unsupported("locale " + locale);
    }
    StringBuilder output = new StringBuilder();
    int nextArgument = 0;
    int previousArgument = -1;
    for (int position = 0; position < format.length();) {
      char current = format.charAt(position++);
      if (current != '%') {
        output.append(current);
        continue;
      }
      int start = position;
      while (position < format.length() && digit(format.charAt(position))) position++;
      int explicitArgument = -1;
      if (position > start && position < format.length() && format.charAt(position) == '$') {
        explicitArgument = number(format, start, position) - 1;
        if (explicitArgument < 0) throw malformed("Argument indices start at one");
        position++;
      } else {
        position = start;
      }
      boolean left = false;
      boolean zero = false;
      boolean reuse = false;
      while (position < format.length()) {
        char flag = format.charAt(position);
        if (flag == '-') {
          if (left) throw malformed("Duplicate '-' flag");
          left = true;
        } else if (flag == '0') {
          if (zero) throw malformed("Duplicate '0' flag");
          zero = true;
        } else if (flag == '<') {
          if (reuse) throw malformed("Duplicate '<' flag");
          reuse = true;
        } else if (flag == '#' || flag == '+' || flag == ' ' || flag == ',' || flag == '(') {
          throw unsupported("flag " + flag);
        } else {
          break;
        }
        position++;
      }
      if (left && zero) throw malformed("Conflicting left alignment and zero padding");
      if (reuse && explicitArgument >= 0) throw unsupported("combined explicit index and reuse");
      start = position;
      while (position < format.length() && digit(format.charAt(position))) position++;
      int width = position == start ? -1 : number(format, start, position);
      if ((left || zero) && width < 0) throw malformed("Padding flag requires a width");
      int precision = -1;
      if (position < format.length() && format.charAt(position) == '.') {
        start = ++position;
        while (position < format.length() && digit(format.charAt(position))) position++;
        if (position == start) throw malformed("Precision requires digits");
        precision = number(format, start, position);
      }
      if (position == format.length()) throw malformed("Missing conversion after '%'");
      char conversion = format.charAt(position++);
      boolean time = conversion == 't' || conversion == 'T';
      if (time) {
        if (position == format.length()) throw malformed("Missing date/time conversion");
        char suffix = format.charAt(position++);
        if (suffix != 'T') throw unsupported("date/time conversion " + conversion + suffix);
      }
      char lower = conversion >= 'A' && conversion <= 'Z' ? (char) (conversion + ('a' - 'A')) : conversion;
      boolean upper = conversion != lower;
      if (!time && "sSbBcCdxXf%n".indexOf(conversion) < 0) throw unsupported("conversion " + conversion);
      if (precision >= 0 && (time || lower != 's' && lower != 'b' && lower != 'f')) {
        throw unsupported("precision for conversion " + conversion);
      }
      if (zero && (time || lower != 'd' && lower != 'x' && lower != 'f')) {
        throw unsupported("zero padding for conversion " + conversion);
      }
      if (conversion == '%' || conversion == 'n') {
        if (reuse || explicitArgument >= 0) throw unsupported("argument index for a non-argument conversion");
        if (conversion == 'n' && (left || width >= 0)) throw unsupported("newline width or flags");
        output.append(conversion == 'n' ? "\n" : pad("%", width, left, false));
        continue;
      }
      int argumentIndex = reuse ? previousArgument : explicitArgument >= 0 ? explicitArgument : nextArgument++;
      if (argumentIndex < 0 || arguments != null && argumentIndex >= arguments.length) {
        throw malformed("Missing argument for conversion " + conversion);
      }
      previousArgument = argumentIndex;
      // A null varargs array means null values, matching java.util.Formatter.
      Object argument = arguments == null ? null : arguments[argumentIndex];
      String value;
      boolean numericPadding = zero;
      if (argument == null) {
        value = lower == 'b' ? "false" : "null";
        numericPadding = false;
      } else if (time) {
        value = time(argument);
      } else {
        switch (lower) {
          case 's': value = String.valueOf(argument); break;
          case 'b': value = argument instanceof Boolean ? argument.toString() : "true"; break;
          case 'c': value = character(argument); break;
          case 'd': value = decimal(argument); break;
          case 'x': value = hexadecimal(argument); break;
          case 'f':
            value = fixed(argument, precision < 0 ? 6 : precision);
            if (value.endsWith("Infinity") || value.equals("NaN")) numericPadding = false;
            break;
          default: throw unsupported("conversion " + conversion);
        }
      }
      if (value == null) value = "null";
      if (precision >= 0 && (lower == 's' || lower == 'b' || argument == null) && value.length() > precision) {
        value = value.substring(0, precision);
      }
      if (upper && !time) value = value.toUpperCase(Locale.ROOT);
      output.append(pad(value, width, left, numericPadding));
    }
    return output.toString();
  }

  private static String decimal(Object argument) {
    if (argument instanceof Byte || argument instanceof Short || argument instanceof Integer
        || argument instanceof Long || argument instanceof BigInteger) {
      return argument.toString();
    }
    throw malformed("Decimal conversion requires an integral value");
  }

  private static String hexadecimal(Object argument) {
    if (argument instanceof Byte) return Integer.toHexString(((Byte) argument).byteValue() & 0xff);
    if (argument instanceof Short) return Integer.toHexString(((Short) argument).shortValue() & 0xffff);
    if (argument instanceof Integer) return Integer.toHexString((Integer) argument);
    if (argument instanceof Long) return Long.toHexString((Long) argument);
    if (argument instanceof BigInteger) return ((BigInteger) argument).toString(16);
    throw malformed("Hexadecimal conversion requires an integral value");
  }

  private static String character(Object argument) {
    if (argument instanceof Character) return argument.toString();
    if (argument instanceof Byte || argument instanceof Short || argument instanceof Integer) {
      int point = ((Number) argument).intValue();
      if (Character.isValidCodePoint(point)) return new String(Character.toChars(point));
    }
    throw malformed("Character conversion requires a character or a valid integral Unicode code point");
  }

  private static String fixed(Object argument, int precision) {
    if (argument instanceof BigDecimal) {
      BigDecimal decimal = (BigDecimal) argument;
      String magnitude = decimal.abs().setScale(precision, RoundingMode.HALF_UP).toPlainString();
      return decimal.signum() < 0 ? "-" + magnitude : magnitude;
    }
    if (!(argument instanceof Float) && !(argument instanceof Double)) {
      throw malformed("Fixed decimal conversion requires Float, Double or BigDecimal");
    }
    double number = ((Number) argument).doubleValue();
    if (Double.isNaN(number)) return "NaN";
    if (Double.isInfinite(number)) return number < 0 ? "-Infinity" : "Infinity";
    boolean negative = number < 0 || number == 0 && 1.0 / number < 0;
    String magnitude = BigDecimal.valueOf(Math.abs(number)).setScale(precision, RoundingMode.HALF_UP).toPlainString();
    return negative ? "-" + magnitude : magnitude;
  }

  @SuppressWarnings("deprecation")
  private static String time(Object argument) {
    Date date;
    if (argument instanceof Date) {
      date = (Date) argument;
    } else if (argument instanceof Long) {
      long timestamp = (Long) argument;
      if (timestamp < -8640000000000000L || timestamp > 8640000000000000L) {
        throw unsupported("timestamp outside the browser Date range");
      }
      date = new Date(timestamp);
    } else {
      throw malformed("Time conversion requires Date or a Long millisecond timestamp");
    }
    return twoDigits(date.getHours()) + ":" + twoDigits(date.getMinutes()) + ":" + twoDigits(date.getSeconds());
  }

  private static String twoDigits(int value) {
    return value < 10 ? "0" + value : Integer.toString(value);
  }

  private static String pad(String value, int width, boolean left, boolean zero) {
    int count = width - value.length();
    if (count <= 0) return value;
    StringBuilder result = new StringBuilder();
    boolean sign = zero && value.charAt(0) == '-';
    if (left) result.append(value);
    if (sign) result.append('-');
    for (int i = 0; i < count; i++) result.append(zero ? '0' : ' ');
    if (!left) result.append(sign ? value.substring(1) : value);
    return result.toString();
  }

  private static boolean digit(char value) {
    return value >= '0' && value <= '9';
  }

  private static int number(String format, int start, int end) {
    try {
      return Integer.parseInt(format.substring(start, end));
    } catch (NumberFormatException invalid) {
      throw malformed("Format index, width or precision is too large");
    }
  }

  private static IllegalArgumentException malformed(String message) {
    return new IllegalArgumentException(message);
  }

  private static UnsupportedOperationException unsupported(String feature) {
    return new UnsupportedOperationException("MPS browser formatting does not support " + feature);
  }
}
