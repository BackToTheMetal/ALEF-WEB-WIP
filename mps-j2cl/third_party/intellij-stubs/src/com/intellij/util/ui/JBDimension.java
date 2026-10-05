package com.intellij.util.ui;
public class JBDimension extends java.awt.Dimension {
  public JBDimension(int width, int height) { super(JBUI.scale(width), JBUI.scale(height)); }
}
