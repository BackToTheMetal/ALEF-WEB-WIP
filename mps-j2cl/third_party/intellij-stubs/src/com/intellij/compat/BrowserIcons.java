package com.intellij.compat;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
/** IntelliJ placeholder symbols can be replaced with real host-provided assets at startup. */
public final class BrowserIcons {
  private static final Map<String, Icon> REGISTERED = new HashMap<>();
  private static final Map<String, Icon> REFERENCES = new HashMap<>();
  private static long generation;
  private BrowserIcons() {}
  public static void register(String name, Icon icon) { REGISTERED.put(name, java.util.Objects.requireNonNull(icon)); generation++; }
  public static void unregister(String name, Icon icon) { if (REGISTERED.remove(name, icon)) generation++; }
  /** Resource lookup keeps missing icons absent; symbolic IntelliJ placeholders use named(). */
  public static Icon findRegistered(String name) { return REGISTERED.containsKey(name) ? named(name) : null; }
  public static long getGeneration() { return generation; }
  public static Icon named(String name) { return REFERENCES.computeIfAbsent(name, NamedIcon::new); }
  private static final class NamedIcon implements Icon {
    final String name;
    NamedIcon(String name) { this.name = name; }
    private Icon asset() { Icon icon = REGISTERED.get(name); return icon == this ? null : icon; }
    @Override public int getIconWidth() { Icon icon = asset(); return icon == null ? 16 : icon.getIconWidth(); }
    @Override public int getIconHeight() { Icon icon = asset(); return icon == null ? 16 : icon.getIconHeight(); }
    @Override public void paintIcon(Component owner, Graphics graphics, int x, int y) {
      Icon icon = asset();
      if (icon != null) { icon.paintIcon(owner, graphics, x, y); return; }
      Graphics copy = graphics.create();
      try {
        copy.setColor(new Color(100, 110, 125));
        copy.drawRect(x + 3, y + 3, 9, 9);
        copy.drawLine(x + 4, y + 4, x + 11, y + 11);
        copy.drawLine(x + 4, y + 11, x + 11, y + 4);
      } finally { copy.dispose(); }
    }
    @Override public String toString() { return name; }
  }
}
