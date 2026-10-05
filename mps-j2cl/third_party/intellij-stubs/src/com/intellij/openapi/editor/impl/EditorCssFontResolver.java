package com.intellij.openapi.editor.impl;
public final class EditorCssFontResolver {
  public static final String EDITOR_FONT_NAME_NO_LIGATURES_PLACEHOLDER = "Monospaced";
  private static final EditorCssFontResolver INSTANCE = new EditorCssFontResolver();
  public static EditorCssFontResolver getGlobalInstance() { return INSTANCE; }
  public String resolveFontFamily(String family) { return EDITOR_FONT_NAME_NO_LIGATURES_PLACEHOLDER.equals(family) ? com.intellij.openapi.editor.colors.EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName() : family; }
}
