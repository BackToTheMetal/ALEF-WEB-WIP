package javax.swing;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import java.awt.Graphics;
import java.awt.WebSwingContainer;
import java.util.function.Consumer;

/**
 * A canvas behind a Swing component's native child controls. The surface is not
 * an AWT child: application removeAll(), layout and focus traversal see only the
 * application's actual components.
 */
public final class BrowserPaintSurface {
  private final JComponent owner;
  private final Consumer<Graphics> paint;
  private boolean mounted, disposed;
  private elemental2.dom.HTMLCanvasElement resizeBuffer;
  private final JComponent canvas = new JComponent("canvas") {
    @Override protected void paintComponent(Graphics graphics) {
      if (mounted && !disposed) {
        if (owner.getForeground() != null) graphics.setColor(owner.getForeground());
        graphics.clipRect(0, 0, owner.getWidth(), owner.getHeight());
        paint.accept(graphics);
      }
    }
  };

  public BrowserPaintSurface(JComponent owner, Consumer<Graphics> paint) {
    if (owner == null || paint == null) throw new NullPointerException();
    this.owner = owner; this.paint = paint;
    canvas.setFocusable(false);
  }
  public void mount() {
    if (disposed || mounted || !owner.isDisplayable()) return;
    Element host = WebSwingContainer.componentElement(owner);
    if (host == null) return;
    canvas.setFont(owner.getFont());
    canvas.setForeground(owner.getForeground());
    resize();
    canvas.addNotify();
    Element element = WebSwingContainer.componentElement(canvas);
    if (element == null) { canvas.removeNotify(); return; }
    element.setAttribute("aria-hidden", "true");
    element.setAttribute("data-webswing-paint-surface", "true");
    element.setAttribute("tabindex", "-1");
    HTMLElement html = (HTMLElement) element;
    html.style.setProperty("position", "absolute");
    html.style.setProperty("left", "0px"); html.style.setProperty("top", "0px");
    html.style.setProperty("pointer-events", "none");
    host.insertBefore(element, host.firstChild);
    mounted = true;
    canvas.repaint();
  }
  public void resize() {
    if (!disposed) {
      if (canvas.getFont() != owner.getFont()) canvas.setFont(owner.getFont());
      elemental2.dom.HTMLCanvasElement surface = mounted
          ? (elemental2.dom.HTMLCanvasElement) WebSwingContainer.componentElement(canvas) : null;
      boolean resized = surface != null && (canvas.getWidth() != owner.getWidth() || canvas.getHeight() != owner.getHeight());
      if (resized) {
        // Canvas resize clears all pixels immediately, while the editor repaint
        // runs in a later task. Keep the previous frame visible across that gap.
        if (resizeBuffer == null) resizeBuffer = (elemental2.dom.HTMLCanvasElement) elemental2.dom.DomGlobal.document.createElement("canvas");
        // Grow in chunks to reuse the buffer during character-by-character edits.
        if (resizeBuffer.width < surface.width) resizeBuffer.width = ((surface.width + 255) / 256) * 256;
        if (resizeBuffer.height < surface.height) resizeBuffer.height = ((surface.height + 255) / 256) * 256;
        elemental2.dom.CanvasRenderingContext2D buffer = (elemental2.dom.CanvasRenderingContext2D) (Object) resizeBuffer.getContext("2d");
        buffer.clearRect(0, 0, resizeBuffer.width, resizeBuffer.height);
        if (surface.width > 0 && surface.height > 0) buffer.drawImage(surface, 0, 0);
      }
      canvas.setBounds(0, 0, owner.getWidth(), owner.getHeight());
      if (resized) {
        elemental2.dom.CanvasRenderingContext2D context = (elemental2.dom.CanvasRenderingContext2D) (Object) surface.getContext("2d");
        context.save();
        context.setTransform(1, 0, 0, 1, 0, 0);
        context.drawImage(resizeBuffer, 0, 0);
        context.restore();
        canvas.repaint();
      }
    }
  }
  public Graphics getGraphics() {
    if (!mounted || disposed) return null;
    resize();
    Graphics graphics = canvas.getGraphics();
    if (graphics != null) {
      if (owner.getForeground() != null) graphics.setColor(owner.getForeground());
      graphics.clipRect(0, 0, owner.getWidth(), owner.getHeight());
    }
    return graphics;
  }
  public void unmount() {
    if (!mounted) return;
    mounted = false;
    canvas.removeNotify();
    if (resizeBuffer != null) { resizeBuffer.width = 0; resizeBuffer.height = 0; resizeBuffer = null; }
  }
  public void dispose() { unmount(); disposed = true; }
  public boolean isMounted() { return mounted; }
}
