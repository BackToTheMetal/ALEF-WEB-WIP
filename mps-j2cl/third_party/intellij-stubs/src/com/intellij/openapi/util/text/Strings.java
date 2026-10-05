package com.intellij.openapi.util.text;
public final class Strings {
  private Strings() {}
  public static String escapeXmlEntities(String value) { return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); }
  public static boolean isEmpty(String value) { return value == null || value.isEmpty(); }
  public static boolean isEmptyOrSpaces(String value) { return value == null || value.trim().isEmpty(); }
}
