package com.intellij.ui;
public final class IdeBorderFactory {
  private IdeBorderFactory() {}
  public static javax.swing.border.Border createBorder(java.awt.Color color, int sides) { return new SideBorder(color, sides); }
  public static javax.swing.border.Border createBorder() { return new SideBorder(JBColor.border(), SideBorder.ALL); }
}
