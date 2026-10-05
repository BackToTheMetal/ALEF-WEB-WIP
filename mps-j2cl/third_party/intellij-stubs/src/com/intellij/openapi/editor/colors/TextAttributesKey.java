package com.intellij.openapi.editor.colors;
import com.intellij.openapi.editor.markup.TextAttributes;
import java.util.HashMap;
import java.util.Map;
public final class TextAttributesKey {
  private static final Map<String, TextAttributesKey> KEYS = new HashMap<>();
  private final String name;
  private TextAttributes fallback;
  private TextAttributesKey(String name) { this.name = name; }
  public static TextAttributesKey createTextAttributesKey(String name) { return KEYS.computeIfAbsent(name, TextAttributesKey::new); }
  public static TextAttributesKey createTextAttributesKey(String name, TextAttributes fallback) { TextAttributesKey key = createTextAttributesKey(name); key.fallback = fallback; return key; }
  public String getExternalName() { return name; }
  public TextAttributes getDefaultAttributes() { return fallback; }
}
