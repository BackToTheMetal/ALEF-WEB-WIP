package com.intellij.ui;
import java.awt.Color;
public class JBColor extends Color {
  public static final JBColor BLACK = new JBColor(Color.BLACK, Color.WHITE);
  public static final JBColor WHITE = new JBColor(Color.WHITE, Color.BLACK);
  public static final JBColor GRAY = new JBColor(Color.GRAY, Color.GRAY);
  public static final JBColor LIGHT_GRAY = new JBColor(Color.LIGHT_GRAY, Color.DARK_GRAY);
  public static final JBColor DARK_GRAY = new JBColor(Color.DARK_GRAY, Color.LIGHT_GRAY);
  public static final JBColor RED = new JBColor(Color.RED, Color.RED);
  public static final JBColor BLUE = new JBColor(Color.BLUE, new Color(100, 160, 255));
  public static final JBColor GREEN = new JBColor(Color.GREEN, Color.GREEN);
  public static final JBColor YELLOW = new JBColor(Color.YELLOW, Color.YELLOW);
  public static final JBColor ORANGE = new JBColor(Color.ORANGE, Color.ORANGE);
  private static boolean dark;
  private final Color lightColor, darkColor;
  private java.util.function.Supplier<? extends Color> supplier;
  public JBColor(Color light, Color dark) { super(light.getRGB(), true); lightColor = light; darkColor = dark; }
  public JBColor(int light, int dark) { this(new Color(light), new Color(dark)); }
  public static void setDark(boolean value) { dark = value; }
  public static boolean isBright() { return !dark; }
  public static Color namedColor(String name, Color fallback) { Color color = javax.swing.UIManager.getColor(name); return color == null ? fallback : color; }
  public static Color namedColor(String name, int fallback) { return namedColor(name, new Color(fallback)); }
  public static JBColor lazy(java.util.function.Supplier<? extends Color> supplier) { JBColor color = new JBColor(Color.BLACK, Color.BLACK); color.supplier = supplier; return color; }
  @Override public int getRGB() { return (supplier != null ? supplier.get() : dark ? darkColor : lightColor).getRGB(); }
  @Override public int getRed() { return (getRGB() >> 16) & 255; }
  @Override public int getGreen() { return (getRGB() >> 8) & 255; }
  @Override public int getBlue() { return getRGB() & 255; }
  @Override public int getAlpha() { return getRGB() >>> 24; }
  public static Color foreground() { return namedColor("Label.foreground", BLACK); }
  public static Color background() { return namedColor("Panel.background", WHITE); }
  public static Color border() { return namedColor("Component.borderColor", GRAY); }
}
