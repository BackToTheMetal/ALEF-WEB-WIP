package com.intellij.ui.popup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/** Flat IntelliJ/MPS action-list presentation, scoped away from completion rows. */
public final class BrowserActionPopupStyle {
  private static boolean installed;
  private static final Icon EMPTY_ICON = new Icon() {
    public int getIconWidth() { return 16; }
    public int getIconHeight() { return 16; }
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {}
  };
  private BrowserActionPopupStyle() {}
  public static void install(JPopupMenu popup) {
    popup.putClientProperty("webswing.styleClass", "mps-action-popup");
    popup.setBackground(Color.WHITE);
    popup.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
    if (!installed) {
      elemental2.dom.Element style = elemental2.dom.DomGlobal.document.createElement("style");
      style.textContent = """
          .mps-action-popup { background:#fff!important; border-radius:4px!important;
            box-shadow:inset 0 0 0 1px #b8c4d0,0 4px 14px #0002!important; }
          .mps-action-popup .mps-action-row { display:flex; align-items:center; justify-content:flex-start;
            padding:0 16px 0 10px!important; background:#fff!important; color:#202124!important;
            text-align:left; white-space:pre; outline:none!important; box-shadow:none!important; }
          .mps-action-popup .mps-action-row:focus,
          .mps-action-popup .mps-action-row:hover { background:#526da5!important; color:#fff!important; }
          .mps-action-popup .mps-action-row[aria-disabled=true] { color:#8a8e96!important; background:#fff!important; }
          .mps-action-popup .mps-action-row>[data-swing-icon] { margin-right:8px!important; }
          """;
      elemental2.dom.DomGlobal.document.head.appendChild(style);
      installed = true;
    }
  }
  public static void row(JMenuItem item) {
    item.putClientProperty("webswing.styleClass", "mps-action-row");
    item.setFont(new Font("SansSerif", Font.PLAIN, 14));
    item.setBackground(Color.WHITE); item.setForeground(new Color(32, 33, 36));
    item.setBorderPainted(false); item.setIconTextGap(8);
    if (item.getIcon() == null) item.setIcon(EMPTY_ICON);
  }
  public static Dimension rowSize(JMenuItem item, boolean submenu) {
    int iconWidth = item.getIcon() == null ? 16 : item.getIcon().getIconWidth();
    int iconHeight = item.getIcon() == null ? 16 : item.getIcon().getIconHeight();
    return new Dimension(item.getFontMetrics(item.getFont()).stringWidth(item.getText()) + iconWidth + 34 + (submenu ? 16 : 0),
        Math.max(26, Math.max(iconHeight, item.getFontMetrics(item.getFont()).getHeight()) + 8));
  }
}
