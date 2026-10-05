package javax.swing;

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

public class JScrollBar extends JComponent implements Adjustable {
  private final List<AdjustmentListener> listeners = new ArrayList<>();
  private int orientation;
  private int unitIncrement = 1;
  private int blockIncrement = 10;
  private BoundedRangeModel model;
  private final JComponent painter = new JComponent("canvas") {
    @Override protected void paintComponent(java.awt.Graphics graphics) {
      // Transparent delegates (including MPS's diagnostic gutter) only paint
      // current marks. Erase the damaged pixels on our own backing canvas first.
      // The inherited clip preserves pixels outside a partial repaint.
      java.awt.Graphics2D clear = (java.awt.Graphics2D) graphics.create();
      try {
        clear.setComposite(java.awt.AlphaComposite.Clear);
        clear.fillRect(0, 0, getWidth(), getHeight());
      } finally { clear.dispose(); }
      JScrollBar.this.paint(graphics);
    }
  };
  private final javax.swing.event.ChangeListener modelChanges = event -> changed();
  // Keep compatibility mouse events on this peer until release, even when the
  // pointer leaves the track. Child arrow/status buttons retain their own input.
  private final elemental2.dom.EventListener capturePointer = event -> {
    elemental2.dom.PointerEvent pointer = (elemental2.dom.PointerEvent) event;
    if (isEnabled() && event.target == peerElement() && pointer.button == 0)
      peerElement().setPointerCapture(pointer.pointerId);
  };
  private final elemental2.dom.EventListener releasePointer = event -> {
    if (getValueIsAdjusting()) dispatchEvent(new java.awt.event.MouseEvent(this,
        java.awt.event.MouseEvent.MOUSE_RELEASED, (long) event.timeStamp, 0,
        0, 0, 1, false, java.awt.event.MouseEvent.BUTTON1));
  };
  // Keyboard movement uses the same directional increments as pointer controls.
  private final elemental2.dom.EventListener pageKey = event -> {
    if (event.defaultPrevented || !isEnabled()) return;
    String key = ((elemental2.dom.KeyboardEvent) event).key;
    int next;
    if ("Home".equals(key)) next = getMinimum();
    else if ("End".equals(key)) next = getMaximum() - getVisibleAmount();
    else if ("PageUp".equals(key)) next = getValue() - getBlockIncrement(-1);
    else if ("PageDown".equals(key)) next = getValue() + getBlockIncrement(1);
    else if ("ArrowLeft".equals(key) || "ArrowUp".equals(key)) next = getValue() - getUnitIncrement(-1);
    else if ("ArrowRight".equals(key) || "ArrowDown".equals(key)) next = getValue() + getUnitIncrement(1);
    else return;
    event.preventDefault(); event.stopPropagation(); setValue(next);
  };
  public JScrollBar() { this(Adjustable.VERTICAL, 0, 10, 0, 100); }
  public JScrollBar(int orientation) { this(orientation, 0, 10, 0, 100); }
  public JScrollBar(int orientation, int value, int extent, int min, int max) {
    super("div"); setOrientation(orientation); model = new DefaultBoundedRangeModel(value, extent, min, max); model.addChangeListener(modelChanges);
    add(painter); setUI(new javax.swing.plaf.basic.BasicScrollBarUI());
  }
  @Override public int getOrientation() { return orientation; }
  public void setOrientation(int orientation) { if (orientation != HORIZONTAL && orientation != VERTICAL) throw new IllegalArgumentException("orientation"); this.orientation = orientation; if (model != null) syncPeer(); }
  public BoundedRangeModel getModel() { return model; }
  public void setModel(BoundedRangeModel value) { if (value == null) throw new IllegalArgumentException("model"); BoundedRangeModel previous = model; if (previous != null) previous.removeChangeListener(modelChanges); model = value; model.addChangeListener(modelChanges); firePropertyChange("model", previous, value); changed(); }
  public void setValues(int value, int extent, int minimum, int maximum) { model.setRangeProperties(value, extent, minimum, maximum, model.getValueIsAdjusting()); }
  public boolean getValueIsAdjusting() { return model.getValueIsAdjusting(); }
  public void setValueIsAdjusting(boolean adjusting) { model.setValueIsAdjusting(adjusting); }
  public int getUnitIncrement(int direction) { return unitIncrement; }
  public int getBlockIncrement(int direction) { return blockIncrement; }
  public AdjustmentListener[] getAdjustmentListeners() { return listeners.toArray(new AdjustmentListener[0]); }
  public void setUI(javax.swing.plaf.ScrollBarUI ui) { super.setUI(ui); }
  @Override public javax.swing.plaf.ScrollBarUI getUI() { return (javax.swing.plaf.ScrollBarUI) super.getUI(); }
  @Override public java.awt.Dimension getPreferredSize() { if (isPreferredSizeSet()) return super.getPreferredSize(); java.awt.Dimension preferred = getUI() == null ? null : getUI().getPreferredSize(this); return preferred != null ? preferred : orientation == VERTICAL ? new java.awt.Dimension(16, 48) : new java.awt.Dimension(48, 16); }
  @Override public int getMinimum() { return model.getMinimum(); } @Override public void setMinimum(int value) { model.setMinimum(value); }
  @Override public int getMaximum() { return model.getMaximum(); } @Override public void setMaximum(int value) { model.setMaximum(value); }
  @Override public int getValue() { return model.getValue(); } @Override public void setValue(int value) { model.setValue(value); }
  @Override public int getVisibleAmount() { return model.getExtent(); } @Override public void setVisibleAmount(int value) { model.setExtent(value); }
  @Override public int getUnitIncrement() { return unitIncrement; } @Override public void setUnitIncrement(int value) { unitIncrement = value; syncPeer(); }
  @Override public int getBlockIncrement() { return blockIncrement; } @Override public void setBlockIncrement(int value) { blockIncrement = value; }
  @Override public void addAdjustmentListener(AdjustmentListener listener) { if (listener != null) listeners.add(listener); }
  @Override public void removeAdjustmentListener(AdjustmentListener listener) { listeners.remove(listener); }
  @Override public void addNotify() {
    super.addNotify(); syncPeer();
    if (peerElement() != null) {
      peerElement().setAttribute("role", "scrollbar"); peerElement().setAttribute("tabindex", "0");
      peerElement().addEventListener("keydown", pageKey);
      peerElement().addEventListener("pointerdown", capturePointer);
      peerElement().addEventListener("lostpointercapture", releasePointer);
    }
    elemental2.dom.Element canvas = java.awt.WebSwingContainer.componentElement(painter);
    if (canvas != null) ((elemental2.dom.HTMLElement) canvas).style.setProperty("pointer-events", "none");
  }
  @Override public java.awt.Graphics getGraphics() { return painter == null ? null : painter.getGraphics(); }
  @Override public void paintImmediately(int x, int y, int width, int height) {
    // Route both scrollbar damage and direct canvas damage through the same
    // clear-and-paint path. Ordinary paint(Graphics) retains Swing semantics.
    painter.paintImmediately(x, y, width, height);
  }
  @Override public void doLayout() { super.doLayout(); painter.setBounds(0, 0, getWidth(), getHeight()); }
  @Override protected void paintChildren(java.awt.Graphics graphics) {
    for (java.awt.Component child : getComponents()) {
      if (child == painter || !child.isVisible()) continue;
      java.awt.Graphics copy = graphics.create(child.getX(), child.getY(), child.getWidth(), child.getHeight());
      try { child.paint(copy); } finally { copy.dispose(); }
    }
  }
  @Override public void removeNotify() {
    if (peerElement() != null) {
      peerElement().removeEventListener("keydown", pageKey);
      peerElement().removeEventListener("pointerdown", capturePointer);
      peerElement().removeEventListener("lostpointercapture", releasePointer);
    }
    super.removeNotify();
  }
  @Override protected void processKeyEvent(java.awt.event.KeyEvent event) {
    if (event.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
      int key = event.getKeyCode(), delta = 0;
      if (key == java.awt.event.KeyEvent.VK_PAGE_UP) delta = -getBlockIncrement(-1);
      else if (key == java.awt.event.KeyEvent.VK_PAGE_DOWN) delta = getBlockIncrement(1);
      if (delta != 0) { setValue(getValue() + delta); event.consume(); }
    }
    super.processKeyEvent(event);
  }
  private void changed() { syncPeer(); AdjustmentEvent event = new AdjustmentEvent(this, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, AdjustmentEvent.TRACK, getValue(), model.getValueIsAdjusting()); for (AdjustmentListener listener : new ArrayList<>(listeners)) listener.adjustmentValueChanged(event); }
  private void syncPeer() {
    if (peerElement() != null) {
      peerElement().setAttribute("aria-orientation", orientation == VERTICAL ? "vertical" : "horizontal");
      peerElement().setAttribute("aria-valuemin", Integer.toString(getMinimum()));
      peerElement().setAttribute("aria-valuemax", Integer.toString(getMaximum() - getVisibleAmount()));
      peerElement().setAttribute("aria-valuenow", Integer.toString(getValue()));
    }
    repaint();
  }
}
