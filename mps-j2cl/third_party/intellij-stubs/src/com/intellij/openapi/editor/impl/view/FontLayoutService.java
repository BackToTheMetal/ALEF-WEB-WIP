package com.intellij.openapi.editor.impl.view;
import java.awt.FontMetrics;
public class FontLayoutService {
  private static final FontLayoutService INSTANCE = new FontLayoutService();
  public static FontLayoutService getInstance() { return INSTANCE; }
  public float charWidth2D(FontMetrics metrics, int codePoint) { return metrics.charWidth(codePoint); }
  public int charWidth(FontMetrics metrics, char character) { return metrics.charWidth(character); }
  public int stringWidth(FontMetrics metrics, String text) { return metrics.stringWidth(text); }
}
