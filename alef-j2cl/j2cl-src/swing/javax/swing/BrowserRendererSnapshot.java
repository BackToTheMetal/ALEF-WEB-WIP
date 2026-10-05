package javax.swing;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import org.webawt.internal.graphics.CanvasGraphics2D;
import org.webawt.internal.peer.CanvasPeer;
import org.webawt.internal.peer.WebAwtRuntime;

/** Paints a reusable custom list renderer before the next row changes its state. */
final class BrowserRendererSnapshot {
  private BrowserRendererSnapshot() {}

  static void paint(HTMLElement cell, Component renderer, String fallback, int width, int height) {
    if (width <= 0 || height <= 0) return;
    String label = accessibleText(renderer);
    cell.setAttribute("aria-label", label.isEmpty() ? fallback : label);
    Rectangle previous = renderer.getBounds();
    // Paint directly into a browser canvas. A BufferedImage forces text through
    // software rasterization and copies every pixel back into another canvas.
    cell.textContent = "";
    cell.style.setProperty("padding", "0");
    CanvasPeer surface = (CanvasPeer) WebAwtRuntime.createPeer("canvas");
    surface.attachToElement(cell.id);
    surface.setBounds(0, 0, width, height);
    HTMLElement canvas = (HTMLElement) DomGlobal.document.getElementById(surface.getElementId());
    canvas.setAttribute("aria-hidden", "true");
    canvas.setAttribute("data-webswing-renderer", "snapshot");
    canvas.style.setProperty("position", "static");
    canvas.style.setProperty("pointer-events", "none");
    canvas.style.setProperty("display", "block");
    Graphics2D graphics = new CanvasGraphics2D(surface);
    try {
      renderer.setBounds(0, 0, width, height);
      layout(renderer);
      graphics.clipRect(0, 0, width, height);
      if (renderer.getFont() != null) graphics.setFont(renderer.getFont());
      if (renderer.getForeground() != null) graphics.setColor(renderer.getForeground());
      renderer.paint(graphics);
    } finally { graphics.dispose(); renderer.setBounds(previous); }
  }
  private static void layout(Component component) {
    if (component instanceof Container) {
      Container container = (Container) component;
      container.doLayout();
      for (Component child : container.getComponents()) layout(child);
    }
  }
  private static String accessibleText(Component component) {
    if (component instanceof JLabel) return plain(((JLabel) component).getText());
    if (component instanceof AbstractButton) return plain(((AbstractButton) component).getText());
    if (component instanceof javax.swing.text.JTextComponent) return ((javax.swing.text.JTextComponent) component).getText();
    if (component instanceof Accessible) {
      AccessibleContext context = ((Accessible) component).getAccessibleContext();
      String name = context == null ? null : context.getAccessibleName();
      if (name != null && !name.isEmpty()) return name;
    }
    StringBuilder result = new StringBuilder();
    if (component instanceof Container) for (Component child : ((Container) component).getComponents()) {
      String text = accessibleText(child);
      if (!text.isEmpty()) { if (result.length() > 0) result.append(' '); result.append(text); }
    }
    return result.toString();
  }
  private static String plain(String text) {
    return text == null ? "" : javax.swing.plaf.basic.BasicHTML.isHTMLString(text) ? javax.swing.plaf.basic.BasicHTML.toPlainText(text) : text;
  }
}
