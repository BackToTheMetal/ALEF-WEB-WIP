package com.intellij.ui.popup;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.awt.RelativePoint;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
public class AbstractPopup implements JBPopup {
  protected final JComponent content;
  protected final JPopupMenu popup = new JPopupMenu();
  private final JComponent focus;
  private boolean requestFocus = true;
  private boolean disposed, closing;
  private Point requestedLocation;
  private java.util.function.Supplier<Boolean> cancelCallback = () -> true;
  public AbstractPopup(JComponent content, JComponent focus) {
    this.content = content; this.focus = focus;
    // Component popups use a single flat frame, not the Basic menu's beveled chrome.
    popup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 196, 208)));
    popup.setBackground(content.getBackground());
    popup.setFont(content.getFont());
    popup.add(content);
    popup.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
      public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent event) {}
      public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent event) {
        if (disposed || closing) return;
        closing = true;
        try { cancelCallback.get(); }
        finally { try { closeAsCanceled(); } finally { closing = false; } }
      }
      public void popupMenuCanceled(javax.swing.event.PopupMenuEvent event) {}
    });
  }
  public void setCancelCallback(java.util.function.Supplier<Boolean> callback) { cancelCallback = callback; }
  @Override public void show(RelativePoint point) {
    if (disposed) throw new IllegalStateException("Popup disposed");
    popup.show(point.getComponent(), point.getPoint().x, point.getPoint().y);
    if (requestFocus && popup.isFocusable() && focus != null) focus.requestFocusInWindow();
  }
  public void show(Component owner, int x, int y, boolean absolute) {
    Point point = new Point(x, y);
    if (absolute) { Point origin = owner.getLocationOnScreen(); point.translate(-origin.x, -origin.y); }
    show(new RelativePoint(owner, point));
  }
  @Override public void show(Window owner) {
    if (requestedLocation != null && owner != null) {
      show(owner, requestedLocation.x, requestedLocation.y, true);
    } else if (requestedLocation != null) show(new RelativePoint(requestedLocation));
    else if (owner != null) showInCenterOf(owner);
    else show(new RelativePoint(new Point()));
  }
  @Override public void showUnderneathOf(Component component) { show(new RelativePoint(component, new Point(0, component.getHeight()))); }
  @Override public void showInCenterOf(Component component) { Dimension size = content.getPreferredSize(); show(new RelativePoint(component, new Point((component.getWidth() - size.width) / 2, (component.getHeight() - size.height) / 2))); }
  @Override public void cancel() {
    if (disposed || closing) return;
    closing = true;
    try { if (cancelCallback.get()) closeAsCanceled(); } finally { closing = false; }
  }
  /** Invoked once after cancellation through either the API or Swing's outside/Escape close. */
  protected void afterCanceled() {}
  private void closeAsCanceled() {
    try { Disposer.dispose(this); } finally { afterCanceled(); }
  }
  @Override public void closeOk(java.awt.event.InputEvent event) { if (!disposed) Disposer.dispose(this); }
  @Override public void dispose() {
    if (!Disposer.isDisposed(this)) { Disposer.dispose(this); return; }
    disposed = true; popup.setVisible(false);
  }
  @Override public boolean isVisible() { return popup.isVisible(); }
  @Override public boolean isDisposed() { return disposed; }
  @Override public JComponent getContent() { return content; }
  public Window getPopupWindow() { return null; } // This implementation is a lightweight Swing popup.
  @Override public void setSize(Dimension size) { content.setPreferredSize(size); popup.setPopupSize(size); }
  @Override public Dimension getSize() { return popup.getSize(); }
  @Override public void pack(boolean width, boolean height) {
    if (disposed) return;
    Dimension previous = popup.getSize();
    // Revalidate alone leaves a visible lightweight popup at its original size
    // when filtering changes the number or width of its completion rows.
    popup.pack();
    if (!width || !height) popup.setPopupSize(width ? popup.getWidth() : previous.width,
        height ? popup.getHeight() : previous.height);
  }
  public Dimension getPreferredContentSize() { return content.getPreferredSize(); }
  @Override public Point getLocationOnScreen() { return popup.getLocationOnScreen(); }
  @Override public void setLocation(Point point) { requestedLocation = new Point(point); popup.setLocation(point); }
  public void setFocusable(boolean value) { popup.setFocusable(value); }
  @Override public void setRequestFocus(boolean value) { requestFocus = value; popup.setRequestFocusEnabled(value); }
}
