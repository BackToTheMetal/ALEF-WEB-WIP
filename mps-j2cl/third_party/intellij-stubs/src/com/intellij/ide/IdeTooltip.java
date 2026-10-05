package com.intellij.ide;
import java.awt.Component;
import java.awt.Point;
import javax.swing.JComponent;
public final class IdeTooltip {
  private final Component component; private final Point point; private final JComponent tip;
  public IdeTooltip(Component component, Point point, JComponent tip) { this.component = component; this.point = new Point(point); this.tip = tip; }
  public Component getComponent() { return component; }
  public Point getPoint() { return new Point(point); }
  public JComponent getTipComponent() { return tip; }
}
