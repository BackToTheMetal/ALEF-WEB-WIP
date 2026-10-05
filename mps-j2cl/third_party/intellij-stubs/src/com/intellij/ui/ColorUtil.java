package com.intellij.ui;
import java.awt.Color;
public final class ColorUtil {
  private ColorUtil() {}
  public static boolean isDark(Color color) { return color != null && (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) < 128000; }
  public static Color withAlpha(Color color, double alpha) { return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) Math.round(Math.max(0, Math.min(1, alpha)) * 255)); }
  public static Color shift(Color color, double factor) { return new Color(clamp(color.getRed() * factor), clamp(color.getGreen() * factor), clamp(color.getBlue() * factor), color.getAlpha()); }
  private static int clamp(double value) { return Math.max(0, Math.min(255, (int) Math.round(value))); }
  public static String toHex(Color color) { String hex = Integer.toHexString(color.getRGB() & 0xffffff); return "000000".substring(hex.length()) + hex; }
  public static String toHtmlColor(Color color) { return "#" + toHex(color); }
  public static Color fromHex(String text) { return new Color(Integer.parseInt(text.startsWith("#") ? text.substring(1) : text, 16)); }
}
