package com.intellij.ui.content;
import com.intellij.openapi.Disposable;
import javax.swing.JComponent;
public interface ContentManager extends Disposable {
  void addContent(Content content);
  boolean removeContent(Content content, boolean dispose);
  Content[] getContents();
  Content getSelectedContent();
  void setSelectedContent(Content content);
  JComponent getComponent();
}
