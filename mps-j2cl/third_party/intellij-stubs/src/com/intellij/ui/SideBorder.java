package com.intellij.ui;
public class SideBorder implements javax.swing.border.Border {
  public static final int NONE = 0, TOP = 1, LEFT = 2, BOTTOM = 4, RIGHT = 8, ALL = 15;
  private final java.awt.Color color;
  private final int sides;
  public SideBorder(java.awt.Color color, int sides) { this.color = color; this.sides = sides; }
  public java.awt.Insets getBorderInsets(java.awt.Component component) { return new java.awt.Insets((sides & TOP) == 0 ? 0 : 1, (sides & LEFT) == 0 ? 0 : 1, (sides & BOTTOM) == 0 ? 0 : 1, (sides & RIGHT) == 0 ? 0 : 1); }
  public boolean isBorderOpaque() { return color.getAlpha() == 255; }
  public void paintBorder(java.awt.Component component, java.awt.Graphics g, int x, int y, int width, int height) {
    java.awt.Color old = g.getColor(); g.setColor(color);
    if ((sides & TOP) != 0) g.fillRect(x, y, width, 1);
    if ((sides & LEFT) != 0) g.fillRect(x, y, 1, height);
    if ((sides & BOTTOM) != 0) g.fillRect(x, y + height - 1, width, 1);
    if ((sides & RIGHT) != 0) g.fillRect(x + width - 1, y, 1, height);
    g.setColor(old);
  }
}
