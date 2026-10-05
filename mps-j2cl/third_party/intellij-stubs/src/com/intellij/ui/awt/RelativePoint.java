package com.intellij.ui.awt;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
public final class RelativePoint {
  private final Component component;
  private final Point point;
  public RelativePoint(Component component, Point point) { this.component = component; this.point = new Point(point); }
  public RelativePoint(MouseEvent event) { this((Component) event.getSource(), event.getPoint()); }
  public RelativePoint(Point screenPoint) { this(null, screenPoint); }
  public Component getComponent() { return component; }
  public Point getPoint() { return new Point(point); }
  public Point getPoint(Component target) { return javax.swing.SwingUtilities.convertPoint(component, point, target); }
  public Point getScreenPoint() { if (component == null) return getPoint(); Point origin = component.getLocationOnScreen(); return new Point(origin.x + point.x, origin.y + point.y); }
  public static RelativePoint getCenterOf(Component component) { return new RelativePoint(component, new Point(component.getWidth() / 2, component.getHeight() / 2)); }
}
