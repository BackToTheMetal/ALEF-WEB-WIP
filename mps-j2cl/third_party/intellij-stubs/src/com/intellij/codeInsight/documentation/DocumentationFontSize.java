package com.intellij.codeInsight.documentation;
import com.intellij.openapi.options.FontSize;
public final class DocumentationFontSize {
  private static FontSize size = FontSize.SMALL;
  private DocumentationFontSize() {}
  public static FontSize getDocumentationFontSize() { return size; }
  public static void setDocumentationFontSize(FontSize value) { size = java.util.Objects.requireNonNull(value); }
}
