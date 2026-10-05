package com.intellij.openapi.editor.markup;
import java.awt.Color;
public class TextAttributes implements Cloneable {
  private Color foreground, background, effect, stripe;
  private EffectType type;
  private int fontType;
  public TextAttributes() {}
  public TextAttributes(Color foreground, Color background, Color effect, EffectType type, int fontType) {
    this.foreground = foreground; this.background = background; this.effect = effect; this.type = type; this.fontType = fontType;
  }
  public Color getForegroundColor() { return foreground; }
  public void setForegroundColor(Color color) { foreground = color; }
  public Color getBackgroundColor() { return background; }
  public void setBackgroundColor(Color color) { background = color; }
  public Color getEffectColor() { return effect; }
  public void setEffectColor(Color color) { effect = color; }
  public Color getErrorStripeColor() { return stripe; }
  public void setErrorStripeColor(Color color) { stripe = color; }
  public EffectType getEffectType() { return type; }
  public void setEffectType(EffectType value) { type = value; }
  public int getFontType() { return fontType; }
  public void setFontType(int value) { fontType = value; }
  public TextAttributes clone() { TextAttributes copy = new TextAttributes(foreground, background, effect, type, fontType); copy.stripe = stripe; return copy; }
}
