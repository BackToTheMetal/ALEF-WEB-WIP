package com.intellij.openapi.editor.ex.util;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
public final class EditorUtil {
  private EditorUtil() {}
  public static Font getEditorFont() { EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme(); return new Font(scheme.getEditorFontName(), Font.PLAIN, scheme.getEditorFontSize()); }
  public static boolean isChangeFontSize(MouseWheelEvent event) { return event.isControlDown() || event.isMetaDown(); }
}
