package com.intellij.openapi.editor.impl;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
public final class FontInfo {
  private FontInfo() {}
  public static FontMetrics getFontMetrics(Font font, FontRenderContext context) { return Toolkit.getDefaultToolkit().getFontMetrics(font); }
}
