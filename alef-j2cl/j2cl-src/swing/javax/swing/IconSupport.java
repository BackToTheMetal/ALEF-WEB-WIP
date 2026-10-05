package javax.swing;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import java.awt.Graphics;
import org.webawt.internal.graphics.CanvasGraphics2D;
import org.webawt.internal.peer.CanvasPeer;
import org.webawt.internal.peer.WebAwtRuntime;

/** A small Canvas peer preserves arbitrary Swing Icon painting inside semantic controls. */
final class IconSupport {
  private static final String KEY = "webswing.iconPeer";
  private final JComponent owner;
  private Icon icon;
  private CanvasPeer canvas;
  private IconSupport(JComponent owner) { this.owner = owner; }
  static void update(JComponent component, Icon icon) {
    IconSupport support = (IconSupport) component.getClientProperty(KEY);
    if (icon == null || !component.isDisplayable()) { if (support != null) support.dispose(); component.putClientProperty(KEY, null); return; }
    if (support == null) { support = new IconSupport(component); component.putClientProperty(KEY, support); }
    if (support.icon != icon) {
      if (support.icon instanceof ImageIcon) ((ImageIcon) support.icon).removeOwner(component);
      support.icon = icon;
      if (icon instanceof ImageIcon) ((ImageIcon) icon).addOwner(component);
    }
    support.paint();
  }
  static void repaint(JComponent component) { IconSupport support = (IconSupport) component.getClientProperty(KEY); if (support != null) support.paint(); }
  static void remove(JComponent component) { update(component, null); }
  private void paint() {
    if (owner.componentElement() == null || icon == null) return;
    if (canvas == null) canvas = (CanvasPeer) WebAwtRuntime.createPeer("canvas");
    canvas.attachToElement(owner.componentElement().id);
    int width = Math.max(1, icon.getIconWidth()), height = Math.max(1, icon.getIconHeight());
    canvas.setBounds(0, 0, width, height);
    HTMLElement element = (HTMLElement) DomGlobal.document.getElementById(canvas.getElementId());
    if (element != null) {
      element.setAttribute("aria-hidden", "true"); element.setAttribute("data-swing-icon", "true");
      element.style.setProperty("position", "static"); element.style.setProperty("flex", "0 0 auto");
      int gap = 4;
      if (owner instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) owner;
        gap = button.getText() == null || button.getText().isEmpty() ? 0 : button.getIconTextGap();
      }
      element.style.setProperty("pointer-events", "none"); element.style.setProperty("margin-right", gap + "px");
      element.style.setProperty("vertical-align", "middle");
      element.style.setProperty("opacity", owner.isEnabled() ? "1" : "0.45");
      owner.componentElement().insertBefore(element, owner.componentElement().firstChild);
    }
    canvas.clearRect(0, 0, width, height);
    Graphics graphics = new CanvasGraphics2D(canvas);
    try { graphics.clipRect(0, 0, width, height); icon.paintIcon(owner, graphics, 0, 0); } finally { graphics.dispose(); }
  }
  private void dispose() { if (icon instanceof ImageIcon) ((ImageIcon) icon).removeOwner(owner); if (canvas != null) canvas.detach(); canvas = null; icon = null; }
}
