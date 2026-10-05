package com.intellij.openapi.editor;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolder;
import javax.swing.JComponent;
/** The browser projectional editor exposes UI services, not a fake mutable text document. */
public interface Editor extends UserDataHolder {
  JComponent getComponent();
  JComponent getContentComponent();
  ScrollingModel getScrollingModel();
  EditorColorsScheme getColorsScheme();
  Project getProject();
  boolean isDisposed();
  int getLineHeight();
}
