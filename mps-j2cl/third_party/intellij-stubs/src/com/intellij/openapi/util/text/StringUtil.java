package com.intellij.openapi.util.text;
public final class StringUtil {
  private StringUtil() {}
  public static boolean isEmpty(String value) { return Strings.isEmpty(value); }
  public static boolean isNotEmpty(String value) { return !isEmpty(value); }
  public static boolean isEmptyOrSpaces(String value) { return Strings.isEmptyOrSpaces(value); }
  public static String notNullize(String value) { return value == null ? "" : value; }
  public static String notNullize(String value, String fallback) { return value == null ? fallback : value; }
  public static String escapeXmlEntities(String value) { return Strings.escapeXmlEntities(value); }
  public static String escapeXml(String value) { return escapeXmlEntities(value); }
  public static String escapeToRegexp(String value) { StringBuilder result = new StringBuilder(); for (int i = 0; i < value.length(); i++) { char c = value.charAt(i); if ("\\.[]{}()*+-?^$|".indexOf(c) >= 0) result.append('\\'); result.append(c); } return result.toString(); }
  public static boolean startsWithIgnoreCase(String value, String prefix) { return value.regionMatches(true, 0, prefix, 0, prefix.length()); }
  public static boolean containsIgnoreCase(String value, String part) { for (int i = 0; i <= value.length() - part.length(); i++) if (value.regionMatches(true, i, part, 0, part.length())) return true; return false; }
  public static String capitalize(String value) { return isEmpty(value) ? value : Character.toUpperCase(value.charAt(0)) + value.substring(1); }
  public static String join(Iterable<?> values, String separator) { StringBuilder result = new StringBuilder(); boolean first = true; for (Object value : values) { if (!first) result.append(separator); first = false; result.append(value); } return result.toString(); }
  public static String repeat(String text, int count) { StringBuilder result = new StringBuilder(); for (int i = 0; i < count; i++) result.append(text); return result.toString(); }
}
