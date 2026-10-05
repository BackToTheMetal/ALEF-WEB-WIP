package com.intellij.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
/** Lightweight fragment renderer with measured fonts, icons and text decorations. */
public class SimpleColoredComponent extends JComponent {
  private final List<Fragment> fragments = new ArrayList<>();
  private Icon icon;
  private int iconTextGap = 4;
  public SimpleColoredComponent() {
    setFont(new Font("Dialog", Font.PLAIN, 12)); setForeground(Color.BLACK);
    setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4));
  }
  public void clear() { fragments.clear(); icon = null; updateAccessibleName(); revalidate(); repaint(); }
  public void append(String text) { append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES); }
  public void append(String text, SimpleTextAttributes attributes) { fragments.add(new Fragment(text == null ? "" : text, attributes)); updateAccessibleName(); revalidate(); repaint(); }
  private void updateAccessibleName() { javax.accessibility.AccessibleContext context = getAccessibleContext(); if (context != null) context.setAccessibleName(getCharSequence(false)); }
  public void append(String text, SimpleTextAttributes attributes, Object tag) { append(text, attributes); }
  public void setIcon(Icon value) { icon = value; revalidate(); repaint(); }
  public Icon getIcon() { return icon; }
  public void setIconTextGap(int gap) { iconTextGap = gap; revalidate(); }
  public String getCharSequence(boolean mainOnly) { StringBuilder text = new StringBuilder(); for (Fragment fragment : fragments) text.append(fragment.text); return text.toString(); }
  public int getFragmentCount() { return fragments.size(); }
  public String getFragmentText(int index) { return fragments.get(index).text; }
  public SimpleTextAttributes getFragmentAttributes(int index) { return fragments.get(index).attributes; }
  private Font font(Fragment fragment) { Font base = getFont(); return base.deriveFont(base.getStyle() | fragment.attributes.getFontStyle()); }
  @Override public Dimension getPreferredSize() {
    Insets insets = getInsets(); int width = 0, height = getFontMetrics(getFont()).getHeight();
    for (Fragment fragment : fragments) { FontMetrics metrics = getFontMetrics(font(fragment)); width += metrics.stringWidth(fragment.text); height = Math.max(height, metrics.getHeight()); }
    if (icon != null) { width += icon.getIconWidth() + (fragments.isEmpty() ? 0 : iconTextGap); height = Math.max(height, icon.getIconHeight()); }
    return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
  }
  @Override protected void paintComponent(Graphics graphics) {
    Graphics g = graphics.create();
    try {
      if (isOpaque()) { g.setColor(getBackground()); g.fillRect(0, 0, getWidth(), getHeight()); }
      Insets insets = getInsets(); int x = insets.left, available = getHeight() - insets.top - insets.bottom;
      if (icon != null) { icon.paintIcon(this, g, x, insets.top + (available - icon.getIconHeight()) / 2); x += icon.getIconWidth() + iconTextGap; }
      for (Fragment fragment : fragments) {
        Font font = font(fragment); FontMetrics metrics = getFontMetrics(font); int width = metrics.stringWidth(fragment.text);
        int top = insets.top + (available - metrics.getHeight()) / 2, baseline = top + metrics.getAscent();
        SimpleTextAttributes attributes = fragment.attributes;
        if (attributes.getBgColor() != null) { g.setColor(attributes.getBgColor()); g.fillRect(x, top, width, metrics.getHeight()); }
        g.setFont(font); g.setColor(attributes.getFgColor() == null ? getForeground() : attributes.getFgColor()); g.drawString(fragment.text, x, baseline);
        if ((attributes.getStyle() & SimpleTextAttributes.STYLE_STRIKEOUT) != 0) g.drawLine(x, baseline - metrics.getAscent() / 3, x + width, baseline - metrics.getAscent() / 3);
        if ((attributes.getStyle() & SimpleTextAttributes.STYLE_UNDERLINE) != 0) g.drawLine(x, baseline + 1, x + width, baseline + 1);
        if ((attributes.getStyle() & SimpleTextAttributes.STYLE_WAVED) != 0) {
          if (attributes.getWaveColor() != null) g.setColor(attributes.getWaveColor());
          for (int i = 0; i < width; i++) g.drawLine(x + i, baseline + 2 + (i % 4 < 2 ? 0 : 1), x + i, baseline + 2 + (i % 4 < 2 ? 0 : 1));
        }
        x += width;
      }
    } finally { g.dispose(); }
  }
  private static final class Fragment {
    final String text; final SimpleTextAttributes attributes;
    Fragment(String text, SimpleTextAttributes attributes) { this.text = text; this.attributes = java.util.Objects.requireNonNull(attributes); }
  }
}
