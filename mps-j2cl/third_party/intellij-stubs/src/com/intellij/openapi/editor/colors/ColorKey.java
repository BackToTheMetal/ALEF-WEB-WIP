package com.intellij.openapi.editor.colors;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
public final class ColorKey {
  private static final Map<String, ColorKey> KEYS = new HashMap<>();
  private final String name;
  private Color fallback;
  private ColorKey(String name) { this.name = name; }
  public static ColorKey createColorKey(String name) { return KEYS.computeIfAbsent(name, ColorKey::new); }
  public static ColorKey createColorKey(String name, Color fallback) { ColorKey key = createColorKey(name); key.fallback = fallback; return key; }
  public String getExternalName() { return name; }
  public Color getDefaultColor() { return fallback; }
}
