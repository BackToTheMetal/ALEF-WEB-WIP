package com.intellij.ui.content;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.UserDataHolder;
import javax.swing.JComponent;
public interface Content extends UserDataHolder, Disposable {
  JComponent getComponent();
  void setComponent(JComponent component);
  String getDisplayName();
  void setDisplayName(String name);
  boolean isCloseable();
  void setCloseable(boolean closeable);
  void setDisposer(Disposable disposer);
}
