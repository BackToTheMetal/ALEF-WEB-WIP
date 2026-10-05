package com.intellij.openapi.ui.popup;
import com.intellij.openapi.Disposable;
import com.intellij.ui.awt.RelativePoint;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
public interface JBPopup extends Disposable {
  void show(RelativePoint point);
  void show(java.awt.Window owner);
  void showUnderneathOf(java.awt.Component component);
  void showInCenterOf(java.awt.Component component);
  void cancel();
  void closeOk(java.awt.event.InputEvent event);
  boolean isVisible();
  boolean isDisposed();
  JComponent getContent();
  void setSize(Dimension size);
  Dimension getSize();
  Point getLocationOnScreen();
  default void setMinimumSize(Dimension size) { getContent().setMinimumSize(size); }
  void setLocation(Point point);
  void setRequestFocus(boolean value);
  default void pack(boolean width, boolean height) { getContent().revalidate(); }
  @Override default void dispose() { cancel(); }
}
