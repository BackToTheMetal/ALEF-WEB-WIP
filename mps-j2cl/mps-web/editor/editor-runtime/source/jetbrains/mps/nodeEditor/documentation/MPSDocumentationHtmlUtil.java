/* Copyright 2000-2023 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.nodeEditor.documentation;

import javax.swing.text.html.HTMLEditorKit;

/** The selected browser documentation pane uses normalized text, without Swing HTML CSS. */
class MPSDocumentationHtmlUtil {
  private MPSDocumentationHtmlUtil() {}
  static void addDocumentationPaneDefaultCssRules(HTMLEditorKit editorKit) {
    throw new UnsupportedOperationException("Swing HTML stylesheet rendering is outside the browser documentation profile");
  }
}
