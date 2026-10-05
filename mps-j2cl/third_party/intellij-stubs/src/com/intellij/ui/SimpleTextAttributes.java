package com.intellij.ui;
import java.awt.Color;
/** Presentation value for a single text fragment. */
public class SimpleTextAttributes {
  public static final int STYLE_PLAIN = 0, STYLE_BOLD = 1, STYLE_ITALIC = 2, STYLE_STRIKEOUT = 4, STYLE_WAVED = 8, STYLE_UNDERLINE = 16;
  public static final SimpleTextAttributes REGULAR_ATTRIBUTES = new SimpleTextAttributes(STYLE_PLAIN, null);
  public static final SimpleTextAttributes GRAYED_ATTRIBUTES = new SimpleTextAttributes(STYLE_PLAIN, Color.GRAY);
  public static final SimpleTextAttributes ERROR_ATTRIBUTES = new SimpleTextAttributes(STYLE_PLAIN, Color.RED);
  private final int style;
  private final Color foreground, background, wave;
  public SimpleTextAttributes(int style, Color foreground) { this(null, foreground, null, style); }
  public SimpleTextAttributes(Color background, Color foreground, Color wave, int style) { this.style = style; this.foreground = foreground; this.background = background; this.wave = wave; }
  public int getStyle() { return style; }
  public int getFontStyle() { return style & (STYLE_BOLD | STYLE_ITALIC); }
  public Color getFgColor() { return foreground; }
  public Color getBgColor() { return background; }
  public Color getWaveColor() { return wave; }
}
