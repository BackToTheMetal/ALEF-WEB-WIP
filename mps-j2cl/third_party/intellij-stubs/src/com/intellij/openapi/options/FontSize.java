package com.intellij.openapi.options;
public enum FontSize {
  XX_SMALL(10), X_SMALL(12), SMALL(13), MEDIUM(14), LARGE(16), X_LARGE(18), XX_LARGE(24);
  private final int size;
  FontSize(int size) { this.size = size; }
  public int getSize() { return size; }
  public FontSize larger() { return values()[Math.min(ordinal() + 1, values().length - 1)]; }
  public FontSize smaller() { return values()[Math.max(ordinal() - 1, 0)]; }
}
