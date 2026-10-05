package com.intellij.util.ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
public final class JBUI {
  private JBUI() {}
  public static int scale(int value) { return com.intellij.ui.scale.JBUIScale.scale(value); }
  public static float scale(float value) { return com.intellij.ui.scale.JBUIScale.scale(value); }
  public static Insets emptyInsets() { return new Insets(0, 0, 0, 0); }
  public static Insets insets(int all) { return insets(all, all, all, all); }
  public static Insets insets(int vertical, int horizontal) { return insets(vertical, horizontal, vertical, horizontal); }
  public static Insets insets(int top, int left, int bottom, int right) { return new Insets(scale(top), scale(left), scale(bottom), scale(right)); }
  public static JBDimension size(int width, int height) { return new JBDimension(width, height); }
  public static final class Borders {
    public static Border empty() { return BorderFactory.createEmptyBorder(); }
    public static Border empty(int all) { return empty(all, all, all, all); }
    public static Border empty(int vertical, int horizontal) { return empty(vertical, horizontal, vertical, horizontal); }
    public static Border empty(int top, int left, int bottom, int right) { return BorderFactory.createEmptyBorder(scale(top), scale(left), scale(bottom), scale(right)); }
    public static Border emptyLeft(int value) { return empty(0, value, 0, 0); }
    public static Border emptyRight(int value) { return empty(0, 0, 0, value); }
    public static Border emptyTop(int value) { return empty(value, 0, 0, 0); }
    public static Border emptyBottom(int value) { return empty(0, 0, value, 0); }
    public static Border customLine(Color color) { return BorderFactory.createLineBorder(color); }
  }
  public static final class Fonts { public static Font label() { Font font = javax.swing.UIManager.getFont("Label.font"); return font == null ? new Font("Dialog", Font.PLAIN, 13) : font; } }
  public static final class Panels { public static JPanel simplePanel() { return new JPanel(); } }
  public static final class CurrentTheme {
    public static final class Link { public static final class Foreground { public static final Color ENABLED = new Color(0, 102, 204); } }
  }
}
