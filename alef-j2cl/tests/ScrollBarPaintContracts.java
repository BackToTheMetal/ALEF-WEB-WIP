package alefj2cl;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.ImageData;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.WebSwingContainer;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ScrollBarUI;
import org.webawt.WebAwt;

/** Pixel contracts for transparent delegates such as the MPS messages gutter. */
public final class ScrollBarPaintContracts {
  private static String result;
  private static boolean error = true;
  private static int checks;
  public static String result() { return result; }
  public static void start() {
    WebAwt.install("scrollbar-root");
    SwingUtilities.invokeLater(() -> {
      JFrame window = new JFrame();
      try {
        javax.swing.JPanel content = new javax.swing.JPanel();
        javax.swing.JScrollPane pane = new javax.swing.JScrollPane(content);
        pane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        pane.setSize(200, 200); content.setPreferredSize(new java.awt.Dimension(300, 300));
        pane.doLayout();
        require(pane.getHorizontalScrollBar().isVisible() && pane.getVerticalScrollBar().isVisible(), "both axes overflow");
        content.setPreferredSize(new java.awt.Dimension(190, 190)); pane.doLayout();
        require(!pane.getHorizontalScrollBar().isVisible() && !pane.getVerticalScrollBar().isVisible(), "optional bars disappear together when content fits");
        content.setPreferredSize(new java.awt.Dimension(300, 190)); pane.doLayout();
        require(pane.getHorizontalScrollBar().isVisible() && pane.getVerticalScrollBar().isVisible(), "horizontal bar can require vertical scrolling");
        content.setPreferredSize(new java.awt.Dimension(300, 100)); pane.doLayout();
        require(pane.getHorizontalScrollBar().isVisible() && !pane.getVerticalScrollBar().isVisible(), "one optional bar can disappear independently");
        JScrollBar bar = new JScrollBar();
        bar.setUI(new ScrollBarUI() {
          @Override public void paint(Graphics g, JComponent c) {
            if (error) { g.setColor(Color.RED); g.fillRect(2, 20, 6, 8); }
            g.setColor(Color.BLUE); g.fillRect(2, 60, 6, 8);
          }
        });
        bar.setOpaque(false);
        window.add(bar); window.setSize(80, 200); window.setVisible(true);
        RepaintManager.currentManager(bar).paintDirtyRegions();
        HTMLCanvasElement canvas = (HTMLCanvasElement) WebSwingContainer.componentElement(bar).querySelector("canvas");
        bar.paintImmediately(0, 0, bar.getWidth(), bar.getHeight());
        pixel(canvas, 3, 22, 255, 0, 0, 255, "initial error mark");
        pixel(canvas, 3, 62, 0, 0, 255, 255, "second mark");
        bar.doLayout();
        pixel(canvas, 3, 22, 255, 0, 0, 255, "unchanged layout retains painted error mark");
        pixel(canvas, 3, 62, 0, 0, 255, 255, "unchanged layout retains undamaged pixels");
        error = false;
        bar.paintImmediately(0, 16, bar.getWidth(), 18);
        pixel(canvas, 3, 22, 0, 0, 0, 0, "partial repaint erases removed mark to transparency");
        pixel(canvas, 3, 62, 0, 0, 255, 255, "partial repaint preserves undamaged pixels");
        error = true;
        bar.paintImmediately(0, 0, bar.getWidth(), bar.getHeight());
        pixel(canvas, 3, 22, 255, 0, 0, 255, "error can reappear");
        error = false;
        ((JComponent) bar.getComponent(0)).paintImmediately(0, 0, bar.getWidth(), bar.getHeight());
        pixel(canvas, 3, 22, 0, 0, 0, 0, "direct canvas repaint erases removed mark");
        error = true;
        bar.repaint(); RepaintManager.currentManager(bar).paintDirtyRegions();
        pixel(canvas, 3, 22, 255, 0, 0, 255, "scheduled error repaint");
        error = false;
        bar.repaint(); RepaintManager.currentManager(bar).paintDirtyRegions();
        pixel(canvas, 3, 22, 0, 0, 0, 0, "scheduled resolution repaint");
        pixel(canvas, 3, 62, 0, 0, 255, 255, "clearing does not affect subsequent paint composite");
        BufferedImage image = new BufferedImage(80, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        try {
          g.setColor(Color.GREEN); g.fillRect(0, 0, 80, 100);
          bar.paint(g);
        } finally { g.dispose(); }
        require(image.getRGB(3, 22) == Color.GREEN.getRGB(), "external painting preserves underlying background");
        bar.setOpaque(true); bar.setBackground(Color.GREEN);
        bar.paintImmediately(0, 0, bar.getWidth(), bar.getHeight());
        pixel(canvas, 3, 22, 0, 0, 0, 0, "cleared canvas exposes the DOM background");
        require("rgb(0, 255, 0)".equals(((elemental2.dom.HTMLElement) WebSwingContainer.componentElement(bar)).style.getPropertyValue("background-color")),
            "opaque scrollbar retains its background");
        result = "PASS: " + checks + " scrollbar paint checks";
      } catch (Throwable failure) {
        failure.printStackTrace(); result = "FAIL after " + checks + " checks: " + failure;
      } finally { window.dispose(); WebAwt.uninstall(); }
    });
  }
  private static void pixel(HTMLCanvasElement canvas, int x, int y, int r, int g, int b, int a, String message) {
    CanvasRenderingContext2D context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");
    double scale = DomGlobal.window.devicePixelRatio;
    ImageData pixel = context.getImageData((int) (x * scale), (int) (y * scale), 1, 1);
    require(pixel.data.getAt(0) == r && pixel.data.getAt(1) == g && pixel.data.getAt(2) == b && pixel.data.getAt(3) == a, message);
  }
  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
    checks++;
  }
}
