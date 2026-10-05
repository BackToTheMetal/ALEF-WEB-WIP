package com.intellij.ui.paint;
public final class LinePainter2D {
  private LinePainter2D() {}
  public static void paint(java.awt.Graphics2D g, double x1, double y1, double x2, double y2) { g.draw(new java.awt.geom.Line2D.Double(x1, y1, x2, y2)); }
}
