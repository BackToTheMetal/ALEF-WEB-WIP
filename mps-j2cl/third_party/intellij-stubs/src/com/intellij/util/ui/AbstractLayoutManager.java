package com.intellij.util.ui;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
public abstract class AbstractLayoutManager implements LayoutManager2 {
  public void addLayoutComponent(Component component, Object constraints) {}
  public void addLayoutComponent(String name, Component component) {}
  public void removeLayoutComponent(Component component) {}
  public void invalidateLayout(Container target) {}
  public float getLayoutAlignmentX(Container target) { return 0.5f; }
  public float getLayoutAlignmentY(Container target) { return 0.5f; }
  public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
  public Dimension minimumLayoutSize(Container target) { return preferredLayoutSize(target); }
}
