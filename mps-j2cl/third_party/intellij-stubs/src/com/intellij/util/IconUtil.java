package com.intellij.util;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
public final class IconUtil {
  private IconUtil() {}
  public static Icon colorize(Icon icon, Color color) {
    return new Icon() {
      public int getIconWidth() { return icon.getIconWidth(); }
      public int getIconHeight() { return icon.getIconHeight(); }
      public void paintIcon(Component component, Graphics graphics, int x, int y) {
        int width = getIconWidth(), height = getIconHeight();
        if (width <= 0 || height <= 0) return;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try { icon.paintIcon(component, g, 0, 0); g.setComposite(AlphaComposite.SrcIn); g.setColor(color); g.fillRect(0, 0, width, height); }
        finally { g.dispose(); }
        graphics.drawImage(image, x, y, component);
      }
    };
  }
}
