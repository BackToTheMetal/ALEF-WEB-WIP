package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class JViewport extends JComponent {
  public static final int SIMPLE_SCROLL_MODE = 0, BLIT_SCROLL_MODE = 1, BACKINGSTORE_SCROLL_MODE = 2;
  private Component view;
  private Point position = new Point();
  private boolean viewSizeSet;
  private int scrollMode = BLIT_SCROLL_MODE;
  private final EventListenerList listeners = new EventListenerList();
  private ChangeEvent changeEvent;
  private static boolean peerStyleInstalled;
  private int verticalPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
  private int horizontalPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
  public JViewport() { super("div"); setLayout(null); setOpaque(true); setBackground(new java.awt.Color(238, 238, 238)); }
  public void setView(Component view) {
    if (this.view != null) {
      elemental2.dom.Element element = java.awt.WebSwingContainer.componentElement(this.view);
      if (element != null) element.removeAttribute("data-swing-viewport-view");
      remove(this.view);
    }
    this.view = view; viewSizeSet = false; position = new Point();
    if (view != null) add(view);
    if (getParent() instanceof JScrollPane && ((JScrollPane) getParent()).getViewport() == this)
      ((JScrollPane) getParent()).configureTableHeader(view);
    revalidate(); repaint(); fireStateChanged();
  }
  public Component getView() { return view; }
  @Override public Dimension getPreferredSize() {
    if (isPreferredSizeSet()) return super.getPreferredSize();
    if (view instanceof Scrollable) return ((Scrollable) view).getPreferredScrollableViewportSize();
    return view instanceof JList ? ((JList<?>) view).getPreferredScrollableViewportSize() : view == null ? new Dimension() : view.getPreferredSize();
  }
  public void setViewPosition(Point point) {
    if (view == null) return;
    if (position.equals(point)) return;
    position = new Point(point);
    view.setLocation(-position.x, -position.y);
    syncPeerPosition(); fireStateChanged();
    repaintView();
  }
  public Point getViewPosition() { return new Point(position); }
  public Rectangle getViewRect() { return new Rectangle(position, getExtentSize()); }
  public Dimension getExtentSize() { return getSize(); }
  public void setExtentSize(Dimension size) { setSize(size); }
  public Dimension toViewCoordinates(Dimension size) { return new Dimension(size); }
  public Point toViewCoordinates(Point point) { return new Point(point); }
  public void setViewSize(Dimension size) {
    if (view != null) { viewSizeSet = true; if (!view.getSize().equals(size)) { view.setSize(size); fireStateChanged(); } }
  }
  public Dimension getViewSize() { return view == null ? new Dimension() : viewSizeSet ? view.getSize() : view.getPreferredSize(); }
  public void setScrollMode(int mode) {
    if (mode < SIMPLE_SCROLL_MODE || mode > BACKINGSTORE_SCROLL_MODE) throw new IllegalArgumentException("scrollMode");
    scrollMode = mode;
  }
  public int getScrollMode() { return scrollMode; }
  public void addChangeListener(ChangeListener listener) { listeners.add(ChangeListener.class, listener); }
  public void removeChangeListener(ChangeListener listener) { listeners.remove(ChangeListener.class, listener); }
  public ChangeListener[] getChangeListeners() { return listeners.getListeners(ChangeListener.class); }
  @Override public void setBounds(int x, int y, int width, int height) {
    boolean resized = width != getWidth() || height != getHeight();
    super.setBounds(x, y, width, height);
    if (resized && listeners != null) fireStateChanged();
  }
  @Override public void doLayout() {
    if (view == null) return;
    Dimension preferred = view.getPreferredSize();
    int width = preferred.width, height = preferred.height;
    if (view instanceof Scrollable) {
      Scrollable scrollable = (Scrollable) view;
      if (scrollable.getScrollableTracksViewportWidth()) width = getWidth();
      if (scrollable.getScrollableTracksViewportHeight()) height = getHeight();
    } else if (view instanceof JTable) {
      JTable table = (JTable) view;
      width = getWidth(); height = Math.max(preferred.height, table.getRowCount() * table.getRowHeight());
    } else if (view instanceof javax.swing.table.JTableHeader) {
      JTable table = ((javax.swing.table.JTableHeader) view).getTable();
      width = table == null ? getWidth() : table.getWidth();
    } else { width = Math.max(width, getWidth()); height = Math.max(height, getHeight()); }
    position.x = Math.max(0, Math.min(position.x, Math.max(0, width - getWidth())));
    position.y = Math.max(0, Math.min(position.y, Math.max(0, height - getHeight())));
    view.setBounds(-position.x, -position.y, width, height); viewSizeSet = true;
    syncPeerPosition(); fireStateChanged();
  }
  public void scrollRectToVisible(Rectangle rectangle) {
    if (view == null) return;
    int x = position.x + adjustment(rectangle.x, rectangle.width, getWidth());
    int y = position.y + adjustment(rectangle.y, rectangle.height, getHeight());
    Dimension size = getViewSize();
    setViewPosition(new Point(Math.max(0, Math.min(x, Math.max(0, size.width - getWidth()))),
                              Math.max(0, Math.min(y, Math.max(0, size.height - getHeight())))));
  }
  private static int adjustment(int start, int length, int extent) {
    if (start >= 0 && start + length <= extent || start <= 0 && start + length >= extent) return 0;
    if (length <= extent) return start < 0 ? start : start + length - extent;
    return start > 0 ? start : start + length - extent;
  }
  @Override public void addNotify() {
    super.addNotify(); syncScrollPolicies(); syncPeerPosition();
    updatePeerScrollHandler(() -> {
      Point next = getPeerScrollPosition();
      if (!next.equals(position)) {
        position = next;
        if (view != null) view.setLocation(-position.x, -position.y);
        // Native scrolling already moved the view. Do not write scrollTop/Left:
        // that cancels smooth wheel scrolling in WebKit.
        alignViewPeer();
        fireStateChanged();
        repaintView();
      }
    });
  }
  private void repaintView() {
    // Native scrolling exposes existing canvas pixels, but MPS only repaints
    // the visible region when its cells change. Paint the newly visible view
    // after both wheel scrolling and programmatic/scrollbar movement.
    if (view != null) view.repaint(position.x, position.y, getWidth(), getHeight());
  }
  void setScrollPolicies(int vertical, int horizontal) { verticalPolicy = vertical; horizontalPolicy = horizontal; syncScrollPolicies(); }
  private void syncScrollPolicies() {
    updatePeerScrollPolicy(0);
    if (peerElement() != null) {
      elemental2.dom.HTMLElement element = (elemental2.dom.HTMLElement) peerElement();
      element.style.setProperty("overflow-y", verticalPolicy == JScrollPane.VERTICAL_SCROLLBAR_NEVER ? "hidden" : "auto");
      element.style.setProperty("overflow-x", horizontalPolicy == JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ? "hidden" : "auto");
      // JScrollPane exposes its own Java scrollbar components, including the MPS gutter.
      if (getParent() instanceof JScrollPane) element.style.setProperty("scrollbar-width", "none");
    }
  }
  private void syncPeerPosition() { alignViewPeer(); updatePeerScrollPosition(position.x, position.y); }
  private void alignViewPeer() {
    if (view == null) return;
    elemental2.dom.Element element = java.awt.WebSwingContainer.componentElement(view);
    if (element != null) {
      if (!peerStyleInstalled) {
        elemental2.dom.Element style = elemental2.dom.DomGlobal.document.createElement("style");
        // Component.setLocation writes inline offsets before alignViewPeer runs.
        // Override those transient offsets as well: otherwise the browser shrinks
        // the overflow and clamps the native scroll position midway through a scroll.
        style.textContent = "[data-swing-viewport-view] { left: 0 !important; top: 0 !important; }";
        elemental2.dom.DomGlobal.document.head.appendChild(style);
        peerStyleInstalled = true;
      }
      element.setAttribute("data-swing-viewport-view", "");
      // Java coordinates include the viewport displacement; DOM scrolling applies
      // that displacement itself. Keeping the peer at the origin avoids applying it twice.
      ((elemental2.dom.HTMLElement) element).style.setProperty("left", "0px");
      ((elemental2.dom.HTMLElement) element).style.setProperty("top", "0px");
    }
  }
  protected void fireStateChanged() { if (changeEvent == null) changeEvent = new ChangeEvent(this); for (ChangeListener listener : getChangeListeners()) listener.stateChanged(changeEvent); }
  @Override public AccessibleContext getAccessibleContext() { return new AccessibleJViewport(); }
  protected class AccessibleJViewport extends AccessibleJComponent { @Override public AccessibleRole getAccessibleRole() { return AccessibleRole.VIEWPORT; } }
}
