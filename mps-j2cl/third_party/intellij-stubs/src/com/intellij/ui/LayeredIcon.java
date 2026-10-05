package com.intellij.ui;
import com.intellij.openapi.util.ScalableIcon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
/** Real ordered icon composition, painted through WebAWT's Graphics2D. */
public class LayeredIcon implements ScalableIcon {
  private final Icon[] icons;
  private final int[] xs, ys;
  private final boolean[] enabled;
  private float scale = 1;
  public LayeredIcon(int layers) {
    icons = new Icon[layers]; xs = new int[layers]; ys = new int[layers]; enabled = new boolean[layers];
    java.util.Arrays.fill(enabled, true);
  }
  public LayeredIcon(Icon... layers) { this(layers.length); System.arraycopy(layers, 0, icons, 0, layers.length); }
  public static LayeredIcon layeredIcon(Icon[] icons) { return new LayeredIcon(icons); }
  public void setIcon(Icon icon, int layer) { setIcon(icon, layer, 0, 0); }
  public void setIcon(Icon icon, int layer, int x, int y) { icons[layer] = icon; xs[layer] = x; ys[layer] = y; }
  public Icon getIcon(int layer) { return icons[layer]; }
  public void setLayerEnabled(int layer, boolean value) { enabled[layer] = value; }
  @Override public float getScale() { return scale; }
  @Override public Icon scale(float value) {
    if (!(value > 0) || Float.isInfinite(value)) throw new IllegalArgumentException("Scale must be positive and finite");
    LayeredIcon copy = new LayeredIcon(icons);
    System.arraycopy(xs, 0, copy.xs, 0, xs.length); System.arraycopy(ys, 0, copy.ys, 0, ys.length);
    System.arraycopy(enabled, 0, copy.enabled, 0, enabled.length); copy.scale = value; return copy;
  }
  @Override public int getIconWidth() {
    int width = 0;
    for (int i = 0; i < icons.length; i++) if (icons[i] != null) width = Math.max(width, xs[i] + icons[i].getIconWidth());
    return (int) Math.ceil(width * scale);
  }
  @Override public int getIconHeight() {
    int height = 0;
    for (int i = 0; i < icons.length; i++) if (icons[i] != null) height = Math.max(height, ys[i] + icons[i].getIconHeight());
    return (int) Math.ceil(height * scale);
  }
  @Override public void paintIcon(Component component, Graphics graphics, int x, int y) {
    Graphics copy = graphics.create();
    try {
      copy.translate(x, y);
      if (scale != 1) {
        if (!(copy instanceof Graphics2D)) throw new UnsupportedOperationException("Scaled icons require Graphics2D");
        ((Graphics2D) copy).scale(scale, scale);
      }
      for (int i = 0; i < icons.length; i++) if (enabled[i] && icons[i] != null) icons[i].paintIcon(component, copy, xs[i], ys[i]);
    } finally { copy.dispose(); }
  }
}
