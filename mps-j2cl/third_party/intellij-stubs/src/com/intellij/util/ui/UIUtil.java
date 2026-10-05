package com.intellij.util.ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
public final class UIUtil {
  public static final String BR = "<br>", BORDER_LINE = "<hr>";
  private UIUtil() {}
  private static Color color(String key, Color fallback) { Color value = javax.swing.UIManager.getColor(key); return value == null ? fallback : value; }
  public static Color getListBackground() { return color("List.background", Color.WHITE); }
  public static Color getListForeground() { return color("List.foreground", Color.BLACK); }
  public static Color getListSelectionBackground(boolean focused) { return color("List.selectionBackground", new Color(82, 109, 165)); }
  public static Color getListSelectionForeground(boolean focused) { return color("List.selectionForeground", Color.WHITE); }
  public static Color getTreeSelectionBackground(boolean focused) { return color("Tree.selectionBackground", getListSelectionBackground(focused)); }
  public static Color getToolTipActionBackground() { return color("ToolTip.background", new Color(255, 255, 220)); }
  public static Color getTooltipSeparatorColor() { return color("Separator.foreground", Color.GRAY); }
  public static Font getFontWithFallback(String family, int style, int size) { return new Font(family, style, size); }
  public static int getLcdContrastValue() { return 140; }
  public static boolean isUnderDarcula() { return !com.intellij.ui.JBColor.isBright(); }
  public static boolean isReallyTypedEvent(KeyEvent event) { return event.getID() == KeyEvent.KEY_TYPED && event.getKeyChar() != KeyEvent.CHAR_UNDEFINED && !event.isControlDown() && !event.isMetaDown(); }
  public static void drawVDottedLine(Graphics graphics, int x, int start, int end, Color background, Color foreground) {
    if (background != null) { graphics.setColor(background); graphics.drawLine(x, start, x, end); }
    graphics.setColor(foreground); for (int y = start; y <= end; y += 2) graphics.drawLine(x, y, x, y);
  }
}
