package mpsj2cl.editor;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.Icon;

/** Known resource dimensions are available before asynchronous browser image decoding. */
final class BrowserResourceIcon implements Icon, ImageObserver {
  private final int width, height;
  private final String uri;
  private final Map<Component, Boolean> owners = new WeakHashMap<>();
  private Image image;
  BrowserResourceIcon(int width, int height, String uri) { this.width = width; this.height = height; this.uri = uri; }
  @Override public int getIconWidth() { return width; }
  @Override public int getIconHeight() { return height; }
  @Override public void paintIcon(Component owner, Graphics graphics, int x, int y) {
    if (owner != null) owners.put(owner, true);
    if (image == null) image = Toolkit.getDefaultToolkit().createImage(uri);
    graphics.drawImage(image, x, y, width, height, this);
  }
  @Override public boolean imageUpdate(Image source, int flags, int x, int y, int w, int h) {
    if (source != image) return false;
    for (Component owner : new ArrayList<>(owners.keySet())) owner.repaint();
    return (flags & (ALLBITS | ABORT | ERROR)) == 0;
  }
}
