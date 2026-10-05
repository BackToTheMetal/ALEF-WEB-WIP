package com.intellij.openapi.ui.popup;
import com.intellij.openapi.Disposable;
public interface Balloon extends Disposable {
  enum Position { above, below, atLeft, atRight }
  void hide();
  void show(com.intellij.ui.awt.RelativePoint point, Position position);
  boolean isDisposed();
  @Override default void dispose() { hide(); }
}
