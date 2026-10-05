package com.intellij.openapi.editor.colors;
import com.intellij.openapi.editor.markup.TextAttributes;
import java.awt.Color;
public interface EditorColorsScheme {
  Color getColor(ColorKey key);
  void setColor(ColorKey key, Color value);
  TextAttributes getAttributes(TextAttributesKey key);
  void setAttributes(TextAttributesKey key, TextAttributes value);
  Color getDefaultBackground();
  Color getDefaultForeground();
  int getEditorFontSize();
  String getEditorFontName();
  float getLineSpacing();
  default FontPreferences getFontPreferences() { return () -> false; }
}
