package com.intellij.ide.ui;
public enum AntialiasingType {
  OFF, GREYSCALE, SUBPIXEL;
  public static Object getKeyForCurrentScope(boolean editor) { return java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON; }
}
