package com.intellij.util.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
public class ButtonlessScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
  public static class Transparent extends ButtonlessScrollBarUI {}
  public boolean alwaysShowTrack() { return true; }
  protected boolean isMacOverlayScrollbar() { return false; }
  protected boolean isDark() { return com.intellij.ui.ColorUtil.isDark(scrollbar.getBackground()); }
  protected Color adjustColor(Color color) { return color; }
  protected int getThickness() { return 12; }
  protected int getDecrementButtonHeight() { return 0; }
  protected int getIncrementButtonHeight() { return 0; }
  @Override public Dimension getPreferredSize(JComponent component) {
    return scrollbar.getOrientation() == javax.swing.JScrollBar.VERTICAL ? new Dimension(getThickness(), 48) : new Dimension(48, getThickness());
  }
  @Override protected JButton createIncreaseButton(int orientation) { JButton button = new JButton(); button.setPreferredSize(new Dimension(0, 0)); return button; }
  @Override protected JButton createDecreaseButton(int orientation) { return createIncreaseButton(orientation); }
  protected void doPaintTrack(Graphics graphics, JComponent component, Rectangle bounds) { super.paintTrack(graphics, component, bounds); }
  @Override protected void paintTrack(Graphics graphics, JComponent component, Rectangle bounds) { doPaintTrack(graphics, component, bounds); }
}
