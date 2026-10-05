package com.intellij.diagnostic;
public class StartUpPerformanceService {
  private long editorHighlightedAt;
  public void editorRestoringTillHighlighted() { editorHighlightedAt = System.currentTimeMillis(); }
  public long getBrowserEditorHighlightedAt() { return editorHighlightedAt; }
}
