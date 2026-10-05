package com.intellij.openapi.editor.ex;
public class EditorSettingsExternalizable {
  private static final EditorSettingsExternalizable INSTANCE = new EditorSettingsExternalizable();
  private boolean wheelFontChange = true;
  public static EditorSettingsExternalizable getInstance() { return INSTANCE; }
  public boolean isWheelFontChangeEnabled() { return wheelFontChange; }
  public void setWheelFontChangeEnabled(boolean value) { wheelFontChange = value; }
}
