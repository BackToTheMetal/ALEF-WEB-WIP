package alefj2cl.example;

import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.markup.TextAttributes;
import java.awt.Color;

/** Match the web shell while retaining the generated language's semantic styles. */
final class ExampleTheme implements EditorColorsScheme, AutoCloseable {
  static final Color BORDER = new Color(231, 216, 206);
  private static final Color TEXT = new Color(48, 39, 31);
  private static final Color MUTED = new Color(128, 105, 84);
  private static final Color ACCENT = new Color(196, 60, 0);
  private final EditorColorsScheme previous = EditorColorsManager.getInstance().getGlobalScheme();
  private final java.util.Map<ColorKey, Color> colors = new java.util.HashMap<>();
  private final java.util.Map<TextAttributesKey, TextAttributes> attributes = new java.util.HashMap<>();
  private final java.util.Map<String, javax.swing.Icon> icons = new java.util.HashMap<>();
  ExampleTheme() {
    colors.put(EditorColors.CARET_COLOR, ACCENT);
    colors.put(EditorColors.CARET_ROW_COLOR, new Color(255, 246, 238));
    colors.put(EditorColors.SELECTION_BACKGROUND_COLOR, new Color(255, 224, 194));
    colors.put(EditorColors.SELECTION_FOREGROUND_COLOR, TEXT);
    colors.put(EditorColors.RIGHT_MARGIN_COLOR, new Color(242, 234, 228));
    colors.put(EditorColors.GUTTER_BACKGROUND, new Color(255, 250, 246));
    colors.put(EditorColors.TEARLINE_COLOR, BORDER);
    colors.put(EditorColors.SELECTED_TEARLINE_COLOR, ACCENT);
    colors.put(ColorKey.createColorKey("NOT_USED_ELEMENT_ATTRIBUTES"), MUTED);
    attributes.put(EditorColors.REFERENCE_HYPERLINK_COLOR, new TextAttributes(ACCENT, null, null, null, 0));
    attributes.put(EditorColors.IDENTIFIER_UNDER_CARET_ATTRIBUTES, new TextAttributes(null, new Color(255, 240, 226), null, null, 0));
    EditorColorsManager.getInstance().setGlobalScheme(this);
    installIcon("InspectionsOK", 0);
    installIcon("InspectionsError", 1);
    installIcon("InspectionsEye", 2);
    installBulb("IntentionBulb", new Color(255, 175, 15));
    installBulb("RealIntentionBulb", new Color(255, 175, 15));
    installBulb("QuickfixBulb", new Color(224, 82, 82));
    installBulb("QuickfixOffBulb", new Color(150, 156, 166));
  }
  private void installBulb(String name, Color color) {
    String key = "AllIcons.Actions." + name;
    if (com.intellij.compat.BrowserIcons.findRegistered(key) != null) return;
    javax.swing.Icon icon = new BulbIcon(color);
    icons.put(key, icon); com.intellij.compat.BrowserIcons.register(key, icon);
  }
  /** Vector geometry from MPS's Apache-2.0 intentionBulb_newui.svg. */
  private static final class BulbIcon implements javax.swing.Icon {
    private final Color color;
    BulbIcon(Color color) { this.color = color; }
    public int getIconWidth() { return 16; }
    public int getIconHeight() { return 16; }
    public void paintIcon(java.awt.Component owner, java.awt.Graphics graphics, int x, int y) {
      java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
      try {
        g.translate(x, y);
        java.awt.geom.Path2D bulb = new java.awt.geom.Path2D.Double();
        bulb.moveTo(10.87, 9.15); bulb.curveTo(12.04, 8.27, 12.8, 6.87, 12.8, 5.3);
        bulb.curveTo(12.8, 2.65, 10.65, .5, 8, .5); bulb.curveTo(5.35, .5, 3.2, 2.65, 3.2, 5.3);
        bulb.curveTo(3.2, 6.87, 3.96, 8.27, 5.13, 9.15); bulb.lineTo(5.5, 11); bulb.lineTo(10.5, 11); bulb.closePath();
        g.setColor(color); g.fill(bulb);
        g.setColor(new Color(108, 112, 126));
        g.fill(new java.awt.geom.Rectangle2D.Double(5.7, 12, 4.6, 1));
        java.awt.geom.Path2D base = new java.awt.geom.Path2D.Double();
        base.moveTo(6, 14); base.lineTo(10, 14); base.curveTo(10, 14.55, 9.55, 15, 9, 15);
        base.lineTo(7, 15); base.curveTo(6.45, 15, 6, 14.55, 6, 14); base.closePath(); g.fill(base);
      } finally { g.dispose(); }
    }
  }
  private void installIcon(String name, int status) {
    String key = "AllIcons.General." + name;
    if (com.intellij.compat.BrowserIcons.findRegistered(key) != null) return;
    javax.swing.Icon icon = new StatusIcon(status);
    icons.put(key, icon);
    com.intellij.compat.BrowserIcons.register(key, icon);
  }
  private static final class StatusIcon implements javax.swing.Icon {
    private final int status;
    StatusIcon(int status) { this.status = status; }
    public int getIconWidth() { return 16; }
    public int getIconHeight() { return 16; }
    public void paintIcon(java.awt.Component owner, java.awt.Graphics graphics, int x, int y) {
      java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
      try {
        g.translate(x, y);
        g.setStroke(new java.awt.BasicStroke(1.6f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
        g.setColor(status == 0 ? new Color(46, 125, 91) : status == 1 ? new Color(184, 58, 58) : new Color(89, 112, 128));
        java.awt.geom.Path2D path = new java.awt.geom.Path2D.Double();
        if (status == 0) {
          path.moveTo(4, 8); path.lineTo(7, 11); path.lineTo(12, 5);
          g.draw(path);
        } else if (status == 1) {
          g.drawOval(2, 2, 12, 12); g.drawLine(8, 5, 8, 8); g.fillOval(7, 10, 2, 2);
        } else {
          path.moveTo(1.5, 8); path.curveTo(5, 2, 11, 2, 14.5, 8);
          path.curveTo(11, 14, 5, 14, 1.5, 8); path.closePath();
          g.draw(path); g.fillOval(6, 6, 4, 4);
        }
      } finally { g.dispose(); }
    }
  }
  static void style(jetbrains.mps.nodeEditor.EditorComponent editor) {
    editor.getScrollPane().setBorder(javax.swing.BorderFactory.createLineBorder(BORDER));
  }
  @Override public Color getColor(ColorKey key) { return colors.containsKey(key) ? colors.get(key) : previous.getColor(key); }
  @Override public void setColor(ColorKey key, Color value) { colors.put(key, value); }
  @Override public TextAttributes getAttributes(TextAttributesKey key) { return attributes.containsKey(key) ? attributes.get(key) : previous.getAttributes(key); }
  @Override public void setAttributes(TextAttributesKey key, TextAttributes value) { attributes.put(key, value); }
  @Override public Color getDefaultBackground() { return Color.WHITE; }
  @Override public Color getDefaultForeground() { return TEXT; }
  @Override public int getEditorFontSize() { return 14; }
  @Override public String getEditorFontName() { return elemental2.dom.DomGlobal.navigator.platform.startsWith("Win") ? "Segoe UI" : "SansSerif"; }
  @Override public float getLineSpacing() { return 1.45f; }
  @Override public FontPreferences getFontPreferences() { return previous.getFontPreferences(); }
  @Override public void close() {
    icons.forEach(com.intellij.compat.BrowserIcons::unregister);
    icons.clear();
    EditorColorsManager.getInstance().setGlobalScheme(previous);
  }
}
