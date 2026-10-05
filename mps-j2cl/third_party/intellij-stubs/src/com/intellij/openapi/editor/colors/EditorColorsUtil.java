package com.intellij.openapi.editor.colors;
public final class EditorColorsUtil {
  private EditorColorsUtil() {}
  public static EditorColorsScheme getColorSchemeForBackground(java.awt.Color background) { return EditorColorsManager.getInstance().getGlobalScheme(); }
}
