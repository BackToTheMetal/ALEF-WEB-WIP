package javax.swing.plaf.basic;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ScrollBarUI;

/** Basic scrollbar geometry, painting, buttons and pointer interaction. */
public class BasicScrollBarUI extends ScrollBarUI implements LayoutManager {
  protected JScrollBar scrollbar;
  protected JButton incrButton, decrButton;
  protected Rectangle thumbRect = new Rectangle(), trackRect = new Rectangle();
  protected Color thumbColor = new Color(160, 160, 160), trackColor = new Color(238, 238, 238);
  protected boolean isDragging;
  private int dragOffset;
  private final java.awt.event.ActionListener decreaseAction = event -> scrollByUnit(-1);
  private final java.awt.event.ActionListener increaseAction = event -> scrollByUnit(1);
  private final AdjustmentListener modelListener = event -> { layoutContainer(scrollbar); scrollbar.repaint(); };
  private final MouseAdapter pointer = new MouseAdapter() {
    @Override public void mousePressed(MouseEvent event) {
      if (!scrollbar.isEnabled() || event.getButton() != MouseEvent.BUTTON1) return;
      layoutContainer(scrollbar);
      int coordinate = vertical() ? event.getY() : event.getX();
      if (thumbRect.contains(event.getPoint())) {
        isDragging = true; dragOffset = coordinate - (vertical() ? thumbRect.y : thumbRect.x);
        scrollbar.setValueIsAdjusting(true);
      } else if (trackRect.contains(event.getPoint())) {
        scrollByBlock(coordinate < (vertical() ? thumbRect.y : thumbRect.x) ? -1 : 1);
      }
    }
    @Override public void mouseDragged(MouseEvent event) {
      if (!isDragging) return;
      int origin = vertical() ? trackRect.y : trackRect.x;
      int track = vertical() ? trackRect.height : trackRect.width;
      int thumb = vertical() ? thumbRect.height : thumbRect.width;
      int coordinate = (vertical() ? event.getY() : event.getX()) - dragOffset - origin;
      long range = (long) scrollbar.getMaximum() - scrollbar.getMinimum() - scrollbar.getVisibleAmount();
      int travel = track - thumb;
      if (travel > 0 && range > 0) scrollbar.setValue(scrollbar.getMinimum() + (int) Math.round(Math.max(0, Math.min(travel, coordinate)) * (double) range / travel));
    }
    @Override public void mouseReleased(MouseEvent event) {
      if (isDragging) { isDragging = false; scrollbar.setValueIsAdjusting(false); }
    }
  };
  public static BasicScrollBarUI createUI(JComponent component) { return new BasicScrollBarUI(); }
  @Override public void installUI(JComponent component) {
    scrollbar = (JScrollBar) component;
    scrollbar.setLayout(this);
    decrButton = createDecreaseButton(vertical() ? SwingConstants.NORTH : SwingConstants.WEST);
    incrButton = createIncreaseButton(vertical() ? SwingConstants.SOUTH : SwingConstants.EAST);
    if (decrButton != null) { configureButton(decrButton); scrollbar.add(decrButton); decrButton.addActionListener(decreaseAction); }
    if (incrButton != null) { configureButton(incrButton); scrollbar.add(incrButton); incrButton.addActionListener(increaseAction); }
    installListeners();
  }
  private void configureButton(JButton button) {
    // The UI assigns the entire button bounds. Ordinary button margins would
    // enlarge its DOM box beyond a narrow scrollbar, even at zero height.
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setIconTextGap(0);
    button.setFocusable(false);
    button.setBorderPainted(false);
    button.setBorder(javax.swing.BorderFactory.createEmptyBorder());
  }
  @Override public void uninstallUI(JComponent component) {
    uninstallListeners();
    if (decrButton != null) { decrButton.removeActionListener(decreaseAction); scrollbar.remove(decrButton); }
    if (incrButton != null) { incrButton.removeActionListener(increaseAction); scrollbar.remove(incrButton); }
    if (scrollbar.getLayout() == this) scrollbar.setLayout(null);
    scrollbar = null; decrButton = null; incrButton = null;
  }
  protected void installListeners() {
    scrollbar.addAdjustmentListener(modelListener);
    scrollbar.addMouseListener(pointer); scrollbar.addMouseMotionListener(pointer);
  }
  protected void uninstallListeners() {
    scrollbar.removeAdjustmentListener(modelListener);
    scrollbar.removeMouseListener(pointer); scrollbar.removeMouseMotionListener(pointer);
    if (isDragging) { isDragging = false; scrollbar.setValueIsAdjusting(false); }
  }
  protected JButton createDecreaseButton(int orientation) { return button(orientation); }
  protected JButton createIncreaseButton(int orientation) { return button(orientation); }
  private JButton button(int orientation) {
    JButton button = new JButton(new ScrollArrowIcon(orientation));
    button.getAccessibleContext().setAccessibleName(orientation == SwingConstants.NORTH ? "Scroll up" : orientation == SwingConstants.SOUTH ? "Scroll down" : orientation == SwingConstants.WEST ? "Scroll left" : "Scroll right");
    button.setPreferredSize(new Dimension(16, 16)); button.setFocusable(false); return button;
  }
  private static final class ScrollArrowIcon implements javax.swing.Icon {
    private final int direction;
    ScrollArrowIcon(int direction) { this.direction = direction; }
    public int getIconWidth() { return 12; }
    public int getIconHeight() { return 12; }
    public void paintIcon(Component owner, Graphics graphics, int x, int y) {
      java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
      try {
        g.setColor(new Color(75, 96, 91));
        g.setStroke(new java.awt.BasicStroke(1.6f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
        java.awt.geom.Path2D path = new java.awt.geom.Path2D.Double();
        if (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH) {
          int edge = direction == SwingConstants.NORTH ? 8 : 4, tip = 12 - edge;
          path.moveTo(x + 3, y + edge); path.lineTo(x + 6, y + tip); path.lineTo(x + 9, y + edge);
        } else {
          int edge = direction == SwingConstants.WEST ? 8 : 4, tip = 12 - edge;
          path.moveTo(x + edge, y + 3); path.lineTo(x + tip, y + 6); path.lineTo(x + edge, y + 9);
        }
        g.draw(path);
      } finally { g.dispose(); }
    }
  }
  private boolean vertical() { return scrollbar.getOrientation() == Adjustable.VERTICAL; }
  protected Rectangle getThumbBounds() { return thumbRect; }
  protected Rectangle getTrackBounds() { return trackRect; }
  protected Dimension getMinimumThumbSize() { return new Dimension(8, 8); }
  protected Dimension getMaximumThumbSize() { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
  protected void setThumbBounds(int x, int y, int width, int height) { thumbRect.setBounds(x, y, width, height); }
  protected void scrollByUnit(int direction) { if (scrollbar.isEnabled()) scrollbar.setValue(scrollbar.getValue() + direction * scrollbar.getUnitIncrement(direction)); }
  protected void scrollByBlock(int direction) { if (scrollbar.isEnabled()) scrollbar.setValue(scrollbar.getValue() + direction * scrollbar.getBlockIncrement(direction)); }
  @Override public void paint(Graphics graphics, JComponent component) {
    layoutContainer(scrollbar); paintTrack(graphics, component, trackRect);
    if (!thumbRect.isEmpty()) paintThumb(graphics, component, thumbRect);
  }
  protected void paintTrack(Graphics graphics, JComponent component, Rectangle bounds) {
    Color previous = graphics.getColor(); graphics.setColor(trackColor); graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height); graphics.setColor(previous);
  }
  protected void paintThumb(Graphics graphics, JComponent component, Rectangle bounds) {
    Color previous = graphics.getColor(); graphics.setColor(thumbColor); graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height); graphics.setColor(previous);
  }
  @Override public Dimension getPreferredSize(JComponent component) { return ((JScrollBar) component).getOrientation() == Adjustable.VERTICAL ? new Dimension(16, 48) : new Dimension(48, 16); }
  @Override public Dimension preferredLayoutSize(Container parent) { return getPreferredSize((JComponent) parent); }
  @Override public Dimension minimumLayoutSize(Container parent) { return new Dimension(8, 8); }
  @Override public void addLayoutComponent(String name, Component component) {}
  @Override public void removeLayoutComponent(Component component) {}
  @Override public void layoutContainer(Container parent) {
    if (scrollbar == null) return;
    Insets insets = scrollbar.getInsets();
    int x = insets.left, y = insets.top, width = Math.max(0, scrollbar.getWidth() - insets.left - insets.right), height = Math.max(0, scrollbar.getHeight() - insets.top - insets.bottom);
    int total = vertical() ? height : width;
    int decrease = decrButton == null ? 0 : vertical() ? decrButton.getPreferredSize().height : decrButton.getPreferredSize().width;
    int increase = incrButton == null ? 0 : vertical() ? incrButton.getPreferredSize().height : incrButton.getPreferredSize().width;
    decrease = Math.min(decrease, total / 2); increase = Math.min(increase, total - decrease);
    if (vertical()) {
      if (decrButton != null) decrButton.setBounds(x, y, width, decrease);
      if (incrButton != null) incrButton.setBounds(x, y + height - increase, width, increase);
      trackRect.setBounds(x, y + decrease, width, height - decrease - increase);
    } else {
      if (decrButton != null) decrButton.setBounds(x, y, decrease, height);
      if (incrButton != null) incrButton.setBounds(x + width - increase, y, increase, height);
      trackRect.setBounds(x + decrease, y, width - decrease - increase, height);
    }
    long range = (long) scrollbar.getMaximum() - scrollbar.getMinimum();
    int extent = scrollbar.getVisibleAmount();
    if (range <= 0 || extent >= range) { thumbRect.setBounds(0, 0, 0, 0); return; }
    int length = vertical() ? trackRect.height : trackRect.width;
    Dimension minimum = getMinimumThumbSize(), maximum = getMaximumThumbSize();
    int thumb = Math.min(length, Math.min(vertical() ? maximum.height : maximum.width,
        Math.max(vertical() ? minimum.height : minimum.width, (int) (length * (long) extent / range))));
    int travel = length - thumb;
    int offset = (int) Math.round(travel * ((double) scrollbar.getValue() - scrollbar.getMinimum()) / (range - extent));
    offset = Math.max(0, Math.min(travel, offset));
    if (vertical()) setThumbBounds(trackRect.x, trackRect.y + offset, trackRect.width, thumb);
    else setThumbBounds(trackRect.x + offset, trackRect.y, thumb, trackRect.height);
  }
}
