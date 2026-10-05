package com.intellij.openapi.wm;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Key;
import com.intellij.ui.content.ContentManager;
import javax.swing.JComponent;
public interface ToolWindow extends Disposable {
  Key<Boolean> SHOW_CONTENT_ICON = Key.create("ShowContentIcon");
  ContentManager getContentManager();
  JComponent getComponent();
  boolean isVisible();
  boolean isAvailable();
  void setAvailable(boolean value);
  void show();
  void hide();
  void activate(Runnable callback);
}
