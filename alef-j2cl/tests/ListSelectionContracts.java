package alefj2cl;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.ImageData;
import elemental2.dom.DomGlobal;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.WebSwingContainer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import org.webawt.WebAwt;

/** Browser contracts for selection painting and model invalidation. */
public final class ListSelectionContracts {
  private static String result;
  private static int checks, renders, events;
  public static String result() { return result; }
  public static void start() {
    WebAwt.install("list-root");
    SwingUtilities.invokeLater(() -> {
      JFrame window = new JFrame();
      try {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (int i = 0; i < 12; i++) model.addElement("Row " + i);
        JList<String> list = new JList<>(model);
        JLabel renderer = new JLabel();
        renderer.setOpaque(true);
        list.setCellRenderer((owner, value, index, selected, focused) -> {
          renders++;
          renderer.setText(value);
          renderer.setBackground(selected ? Color.BLUE : Color.WHITE);
          return renderer;
        });
        window.add(list);
        window.setSize(300, 300);
        window.setVisible(true);
        HTMLElement root = (HTMLElement) WebSwingContainer.componentElement(list);
        list.setSelectedIndex(0);
        Element unchanged = row(root, 5);
        list.addListSelectionListener(event -> {
          require(event.getSource() == list, "selection event source");
          events++;
        });
        renders = 0;
        list.setSelectedIndex(1);
        require(renders == 2, "only old and new selections rendered");
        require(events == 1, "one synchronous selection notification");
        require(row(root, 5) == unchanged, "unaffected DOM identity");
        require("false".equals(row(root, 0).getAttribute("aria-selected")), "old row deselected");
        require("true".equals(row(root, 1).getAttribute("aria-selected")), "new row selected");
        require(row(root, 1).id.equals(root.getAttribute("aria-activedescendant")), "accessible lead follows selection");
        require("rgb(0, 0, 255)".equals(((HTMLElement) row(root, 1)).style.getPropertyValue("background-color")), "selected renderer appearance");
        renders = 0;
        list.getSelectionModel().setAnchorSelectionIndex(8);
        require(renders == 0, "anchor-only event does not repaint");
        ((DefaultListSelectionModel) list.getSelectionModel()).moveLeadSelectionIndex(8);
        require(renders == 0, "lead-only event does not repaint unchanged selection");
        require(row(root, 8).id.equals(root.getAttribute("aria-activedescendant")), "lead-only accessibility update");
        list.clearSelection();
        require("false".equals(row(root, 1).getAttribute("aria-selected")), "clear selection appearance");
        list.getSelectionModel().setSelectionInterval(2, 6);
        for (int i = 0; i < 12; i++) require(Boolean.toString(i >= 2 && i <= 6).equals(row(root, i).getAttribute("aria-selected")), "interval selection " + i);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(3);
        model.add(0, "Inserted");
        require(list.getSelectedIndex() == 4 && root.childNodes.length == 13, "insert shifts selection and rebuilds rows");
        require("true".equals(row(root, 4).getAttribute("aria-selected")), "inserted model selection appearance");
        model.remove(0);
        require(list.getSelectedIndex() == 3 && root.childNodes.length == 12, "remove shifts selection and rebuilds rows");
        model.set(3, "Updated");
        require("Updated".equals(row(root, 3).textContent), "content changes invalidate renderer");
        DefaultListModel<String> replacement = new DefaultListModel<>();
        for (int i = 0; i < 12; i++) replacement.addElement("Replacement " + i);
        list.setModel(replacement);
        require(list.getSelectedIndex() == -1 && "Replacement 3".equals(row(root, 3).textContent), "same-size model replacement invalidates rows");
        DefaultListSelectionModel selection = new DefaultListSelectionModel();
        selection.setSelectionInterval(5, 5);
        list.setSelectionModel(selection);
        require("true".equals(row(root, 5).getAttribute("aria-selected")), "selection-model replacement updates rows");
        replacement.clear();
        require(root.childNodes.length == 0 && root.getAttribute("aria-activedescendant") == null, "empty model clears rows and accessible lead");
        JPanel panel = new JPanel(new BorderLayout()) {
          @Override protected void paintComponent(Graphics graphics) {
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, getWidth(), getHeight());
          }
        };
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JLabel label = new JLabel();
        panel.add(label);
        list.setCellRenderer((owner, value, index, selected, focused) -> {
          panel.setBackground(selected ? Color.BLUE : Color.WHITE);
          label.setText(value);
          return panel;
        });
        replacement.addElement("Custom first");
        replacement.addElement("Custom second");
        list.setSelectedIndex(0);
        HTMLCanvasElement oldCanvas = canvas(root, 0);
        pixel(oldCanvas, 0, 0, 255);
        pixel(canvas(root, 1), 255, 255, 255);
        require(oldCanvas.height == Math.round(oldCanvas.getBoundingClientRect().height * DomGlobal.window.devicePixelRatio), "snapshot resolution matches device scale");
        require("Custom first".equals(row(root, 0).getAttribute("aria-label")), "custom renderer accessible text");
        list.setSelectedIndex(1);
        require(canvas(root, 0) != oldCanvas, "custom selection repaint replaces snapshot");
        pixel(canvas(root, 0), 255, 255, 255);
        pixel(canvas(root, 1), 0, 0, 255);
        int widthBefore = canvas(root, 1).width;
        list.setSize(480, 300);
        require(canvas(root, 1).width > widthBefore, "resize invalidates snapshot width");
        replacement.set(1, "Changed custom label");
        require("Changed custom label".equals(row(root, 1).getAttribute("aria-label")), "custom model change invalidates snapshot text");
        result = "PASS: " + checks + " list selection checks";
      } catch (Throwable failure) {
        failure.printStackTrace();
        result = "FAIL after " + checks + " checks: " + failure;
      } finally { window.dispose(); WebAwt.uninstall(); }
    });
  }
  private static HTMLCanvasElement canvas(HTMLElement root, int index) {
    return (HTMLCanvasElement) row(root, index).querySelector("canvas");
  }
  private static Element row(HTMLElement root, int index) {
    return (Element) root.childNodes.item(index);
  }
  private static void pixel(HTMLCanvasElement canvas, int red, int green, int blue) {
    CanvasRenderingContext2D context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");
    ImageData pixel = context.getImageData(1, 1, 1, 1);
    require(pixel.data.getAt(0) == red && pixel.data.getAt(1) == green && pixel.data.getAt(2) == blue && pixel.data.getAt(3) == 255,
        "custom snapshot pixels: " + pixel.data.getAt(0) + "," + pixel.data.getAt(1) + "," + pixel.data.getAt(2) + "," + pixel.data.getAt(3));
  }
  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
    checks++;
  }
}
