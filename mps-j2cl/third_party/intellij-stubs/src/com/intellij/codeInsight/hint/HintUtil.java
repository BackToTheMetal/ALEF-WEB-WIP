package com.intellij.codeInsight.hint;
import com.intellij.ui.HintHint;
public final class HintUtil {
  private HintUtil() {}
  public static HintHint getInformationHint() { return new HintHint(); }
}
