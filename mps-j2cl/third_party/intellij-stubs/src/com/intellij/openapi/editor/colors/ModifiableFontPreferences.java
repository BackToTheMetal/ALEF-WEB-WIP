package com.intellij.openapi.editor.colors;
public final class ModifiableFontPreferences implements FontPreferences {
  private boolean ligatures;
  public boolean useLigatures() { return ligatures; }
  public void setUseLigatures(boolean value) { ligatures = value; }
}
