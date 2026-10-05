package com.intellij.codeInsight;
public final class CodeInsightSettings {
  private static final CodeInsightSettings INSTANCE = new CodeInsightSettings();
  public boolean AUTO_POPUP_COMPLETION_LOOKUP = true;
  public static CodeInsightSettings getInstance() { return INSTANCE; }
}
