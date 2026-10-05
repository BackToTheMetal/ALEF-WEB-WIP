package com.intellij.util.ui;
import java.awt.Component;
import java.awt.event.MouseEvent;
public final class MouseEventAdapter {
  private MouseEventAdapter() {}
  public static MouseEvent convert(MouseEvent event, Component target, int id, long when, int modifiers, int x, int y) { return new MouseEvent(target, id, when, modifiers, x, y, event.getClickCount(), event.isPopupTrigger(), event.getButton()); }
  public static MouseEvent convert(MouseEvent event, Component target, int id) {
    java.awt.Point point = javax.swing.SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), target);
    return new MouseEvent(target, id, event.getWhen(), event.getModifiersEx(), point.x, point.y, event.getClickCount(), event.isPopupTrigger(), event.getButton());
  }
}
