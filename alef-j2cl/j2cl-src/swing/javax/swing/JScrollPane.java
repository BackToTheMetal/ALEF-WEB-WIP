package javax.swing;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

/** Browser scrolling with live Swing models and synchronized editor headers. */
public class JScrollPane extends JComponent implements SwingConstants {
  public static final int VERTICAL_SCROLLBAR_AS_NEEDED = 20, VERTICAL_SCROLLBAR_NEVER = 21, VERTICAL_SCROLLBAR_ALWAYS = 22;
  public static final int HORIZONTAL_SCROLLBAR_AS_NEEDED = 30, HORIZONTAL_SCROLLBAR_NEVER = 31, HORIZONTAL_SCROLLBAR_ALWAYS = 32;
  private JViewport viewport, columnHeader, rowHeader;
  private JScrollBar verticalScrollBar, horizontalScrollBar;
  private int verticalScrollBarPolicy, horizontalScrollBarPolicy;
  private boolean synchronizing, layingOut, wheelScrollingEnabled = true;
  private Border viewportBorder;
  private final ChangeListener viewportChanges = event -> synchronizeModels();
  private final java.awt.event.AdjustmentListener verticalChanges = event -> scrollFromBar(true);
  private final java.awt.event.AdjustmentListener horizontalChanges = event -> scrollFromBar(false);
  public JScrollPane() { this(null); }
  public JScrollPane(Component view) { this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED); }
  public JScrollPane(int vertical, int horizontal) { this(null, vertical, horizontal); }
  public JScrollPane(Component view, int vertical, int horizontal) {
    setLayout(null); setBorder(BorderFactory.createEmptyBorder(1, 1, 2, 2));
    viewport = createViewport(); add(viewport); viewport.addChangeListener(viewportChanges);
    setVerticalScrollBar(createVerticalScrollBar()); setHorizontalScrollBar(createHorizontalScrollBar());
    setVerticalScrollBarPolicy(vertical); setHorizontalScrollBarPolicy(horizontal); viewport.setView(view);
  }
  protected JViewport createViewport() { return new JViewport(); }
  public JScrollBar createVerticalScrollBar() { return new ScrollBar(Adjustable.VERTICAL); }
  public JScrollBar createHorizontalScrollBar() { return new ScrollBar(Adjustable.HORIZONTAL); }
  public void setViewportView(Component view) { viewport.setView(view); }
  public JViewport getViewport() { return viewport; }
  public void setViewport(JViewport next) {
    if (next == viewport) return;
    JViewport previous = viewport;
    if (previous != null) { previous.removeChangeListener(viewportChanges); remove(previous); }
    viewport = next;
    if (next != null) { add(next); next.addChangeListener(viewportChanges); configureTableHeader(next.getView()); }
    updateScrollPolicy(); synchronizeModels(); revalidate(); firePropertyChange("viewport", previous, next);
  }
  public JViewport getColumnHeader() { return columnHeader; }
  public void setColumnHeader(JViewport next) {
    JViewport previous = columnHeader;
    if (previous != null) remove(previous);
    columnHeader = next;
    if (next != null) { add(next); next.setScrollPolicies(VERTICAL_SCROLLBAR_NEVER, HORIZONTAL_SCROLLBAR_NEVER); }
    revalidate(); firePropertyChange("columnHeader", previous, next);
  }
  public void setColumnHeaderView(Component view) {
    if (columnHeader == null && view != null) setColumnHeader(createViewport());
    if (columnHeader != null) columnHeader.setView(view);
  }
  public JViewport getRowHeader() { return rowHeader; }
  public void setRowHeader(JViewport next) {
    JViewport previous = rowHeader;
    if (previous != null) remove(previous);
    rowHeader = next;
    if (next != null) { add(next); next.setScrollPolicies(VERTICAL_SCROLLBAR_NEVER, HORIZONTAL_SCROLLBAR_NEVER); }
    revalidate(); firePropertyChange("rowHeader", previous, next);
  }
  public void setRowHeaderView(Component view) {
    if (rowHeader == null && view != null) setRowHeader(createViewport());
    if (rowHeader != null) rowHeader.setView(view);
  }
  void configureTableHeader(Component view) { setColumnHeaderView(view instanceof JTable ? ((JTable) view).getTableHeader() : null); }
  public JScrollBar getVerticalScrollBar() { return verticalScrollBar; }
  public void setVerticalScrollBar(JScrollBar next) {
    JScrollBar previous = verticalScrollBar;
    if (previous != null) { previous.removeAdjustmentListener(verticalChanges); remove(previous); }
    verticalScrollBar = next;
    if (next != null) { add(next); next.addAdjustmentListener(verticalChanges); }
    synchronizeModels(); revalidate(); firePropertyChange("verticalScrollBar", previous, next);
  }
  public JScrollBar getHorizontalScrollBar() { return horizontalScrollBar; }
  public void setHorizontalScrollBar(JScrollBar next) {
    JScrollBar previous = horizontalScrollBar;
    if (previous != null) { previous.removeAdjustmentListener(horizontalChanges); remove(previous); }
    horizontalScrollBar = next;
    if (next != null) { add(next); next.addAdjustmentListener(horizontalChanges); }
    synchronizeModels(); revalidate(); firePropertyChange("horizontalScrollBar", previous, next);
  }
  public int getVerticalScrollBarPolicy() { return verticalScrollBarPolicy; }
  public void setVerticalScrollBarPolicy(int policy) {
    if (policy < VERTICAL_SCROLLBAR_AS_NEEDED || policy > VERTICAL_SCROLLBAR_ALWAYS) throw new IllegalArgumentException("vertical policy");
    int previous = verticalScrollBarPolicy; verticalScrollBarPolicy = policy; updateScrollPolicy(); revalidate(); firePropertyChange("verticalScrollBarPolicy", previous, policy);
  }
  public int getHorizontalScrollBarPolicy() { return horizontalScrollBarPolicy; }
  public void setHorizontalScrollBarPolicy(int policy) {
    if (policy < HORIZONTAL_SCROLLBAR_AS_NEEDED || policy > HORIZONTAL_SCROLLBAR_ALWAYS) throw new IllegalArgumentException("horizontal policy");
    int previous = horizontalScrollBarPolicy; horizontalScrollBarPolicy = policy; updateScrollPolicy(); revalidate(); firePropertyChange("horizontalScrollBarPolicy", previous, policy);
  }
  public Border getViewportBorder() { return viewportBorder; }
  public void setViewportBorder(Border border) { Border previous = viewportBorder; viewportBorder = border; revalidate(); repaint(); firePropertyChange("viewportBorder", previous, border); }
  public java.awt.Rectangle getViewportBorderBounds() { return viewport == null ? new java.awt.Rectangle() : viewport.getBounds(); }
  public boolean isWheelScrollingEnabled() { return wheelScrollingEnabled; }
  public void setWheelScrollingEnabled(boolean value) { wheelScrollingEnabled = value; updateScrollPolicy(); }
  private void updateScrollPolicy() {
    if (viewport != null) viewport.setScrollPolicies(wheelScrollingEnabled ? verticalScrollBarPolicy : VERTICAL_SCROLLBAR_NEVER,
                                                    wheelScrollingEnabled ? horizontalScrollBarPolicy : HORIZONTAL_SCROLLBAR_NEVER);
  }
  private void scrollFromBar(boolean vertical) {
    if (synchronizing || viewport == null) return;
    Point position = viewport.getViewPosition();
    if (vertical) position.y = verticalScrollBar.getValue(); else position.x = horizontalScrollBar.getValue();
    viewport.setViewPosition(position);
  }
  private void synchronizeModels() {
    if (synchronizing || viewport == null) return;
    synchronizing = true;
    try {
      Point position = viewport.getViewPosition();
      Dimension size = viewport.getViewSize(), extent = viewport.getExtentSize();
      if (verticalScrollBar != null) verticalScrollBar.setValues(position.y, extent.height, 0, Math.max(size.height, extent.height));
      if (horizontalScrollBar != null) horizontalScrollBar.setValues(position.x, extent.width, 0, Math.max(size.width, extent.width));
      if (rowHeader != null) rowHeader.setViewPosition(new Point(0, position.y));
      if (columnHeader != null) columnHeader.setViewPosition(new Point(position.x, 0));
    } finally { synchronizing = false; }
  }
  @Override public Dimension getPreferredSize() {
    if (isPreferredSizeSet()) return super.getPreferredSize();
    Dimension extent = viewport == null ? new Dimension() : viewport.getPreferredSize();
    Insets insets = getInsets();
    return new Dimension(extent.width + insets.left + insets.right + (rowHeader == null ? 0 : rowHeader.getPreferredSize().width)
        + (verticalScrollBar != null && verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS ? verticalScrollBar.getPreferredSize().width : 0),
        extent.height + insets.top + insets.bottom + (columnHeader == null ? 0 : columnHeader.getPreferredSize().height)
        + (horizontalScrollBar != null && horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS ? horizontalScrollBar.getPreferredSize().height : 0));
  }
  @Override public void doLayout() {
    if (viewport == null || layingOut) return;
    layingOut = true;
    try {
      Insets insets = getInsets();
      int rowWidth = rowHeader == null ? 0 : rowHeader.getPreferredSize().width;
      int columnHeight = columnHeader == null ? 0 : columnHeader.getPreferredSize().height;
      int totalWidth = Math.max(0, getWidth() - insets.left - insets.right - rowWidth);
      int totalHeight = Math.max(0, getHeight() - insets.top - insets.bottom - columnHeight);
      int barWidth = verticalScrollBar == null ? 0 : verticalScrollBar.getPreferredSize().width;
      int barHeight = horizontalScrollBar == null ? 0 : horizontalScrollBar.getPreferredSize().height;
      // Begin with the current geometry. Starting every layout without optional
      // bars temporarily expands the viewport, clamps its scroll offset and
      // clears editor canvases before restoring the exact same geometry.
      boolean vertical = verticalScrollBar != null && (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS
          || verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED && verticalScrollBar.isVisible());
      boolean horizontal = horizontalScrollBar != null && (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS
          || horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED && horizontalScrollBar.isVisible());
      // Two optional bars must not keep each other alive after content shrinks
      // to fit the space available without them.
      Dimension preferred = viewport.getView() == null ? new Dimension() : viewport.getView().getPreferredSize();
      if (preferred.width <= totalWidth - (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS ? barWidth : 0)
          && preferred.height <= totalHeight - (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS ? barHeight : 0)) {
        vertical = verticalScrollBar != null && verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS;
        horizontal = horizontalScrollBar != null && horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS;
      }
      int width = totalWidth, height = totalHeight;
      for (int pass = 0; pass < 3; pass++) {
        width = Math.max(0, totalWidth - (vertical ? barWidth : 0));
        height = Math.max(0, totalHeight - (horizontal ? barHeight : 0));
        viewport.setBounds(insets.left + rowWidth, insets.top + columnHeight, width, height);
        viewport.doLayout();
        Dimension size = viewport.getViewSize();
        boolean nextVertical = vertical, nextHorizontal = horizontal;
        if (verticalScrollBar != null && verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) nextVertical = size.height > height;
        if (horizontalScrollBar != null && horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) nextHorizontal = size.width > width;
        if (nextVertical == vertical && nextHorizontal == horizontal) break;
        vertical = nextVertical; horizontal = nextHorizontal;
      }
      if (verticalScrollBar != null) { verticalScrollBar.setVisible(vertical); verticalScrollBar.setBounds(insets.left + rowWidth + width, insets.top + columnHeight, vertical ? barWidth : 0, height); }
      if (horizontalScrollBar != null) { horizontalScrollBar.setVisible(horizontal); horizontalScrollBar.setBounds(insets.left + rowWidth, insets.top + columnHeight + height, width, horizontal ? barHeight : 0); }
      if (rowHeader != null) { rowHeader.setBounds(insets.left, insets.top + columnHeight, rowWidth, height); rowHeader.doLayout(); }
      if (columnHeader != null) { columnHeader.setBounds(insets.left + rowWidth, insets.top, width, columnHeight); columnHeader.doLayout(); }
      synchronizeModels();
    } finally { layingOut = false; }
  }
  protected class ScrollBar extends JScrollBar {
    private boolean unitExplicit, blockExplicit;
    public ScrollBar(int orientation) { super(orientation); }
    @Override public void setUnitIncrement(int value) { super.setUnitIncrement(value); unitExplicit = true; }
    @Override public void setBlockIncrement(int value) { super.setBlockIncrement(value); blockExplicit = true; }
    @Override public int getUnitIncrement(int direction) {
      Component view = viewport == null ? null : viewport.getView();
      return !unitExplicit && view instanceof Scrollable ? ((Scrollable) view).getScrollableUnitIncrement(viewport.getViewRect(), getOrientation(), direction) : super.getUnitIncrement(direction);
    }
    @Override public int getBlockIncrement(int direction) {
      Component view = viewport == null ? null : viewport.getView();
      if (!blockExplicit && view instanceof Scrollable) return ((Scrollable) view).getScrollableBlockIncrement(viewport.getViewRect(), getOrientation(), direction);
      return !blockExplicit && viewport != null ? getOrientation() == VERTICAL ? viewport.getHeight() : viewport.getWidth() : super.getBlockIncrement(direction);
    }
  }
  @Override public AccessibleContext getAccessibleContext() { return new AccessibleJScrollPane(); }
  protected class AccessibleJScrollPane extends AccessibleJComponent { @Override public AccessibleRole getAccessibleRole() { return AccessibleRole.SCROLL_PANE; } }
}
