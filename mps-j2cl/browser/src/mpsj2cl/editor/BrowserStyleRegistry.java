package mpsj2cl.editor;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.ColorUtil;
import com.intellij.util.messages.MessageBusConnection;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jetbrains.mps.editor.runtime.style.StyleAttributes;
import jetbrains.mps.editor.runtime.style.StyleImpl;
import jetbrains.mps.openapi.editor.style.Style;
import jetbrains.mps.openapi.editor.style.StyleRegistry;

/** Browser counterpart of the desktop style service, backed by the host's current color scheme. */
public final class BrowserStyleRegistry extends StyleRegistry {
  private final Map<String, String> aliases = new HashMap<>();
  private StyleRegistry previous;
  private MessageBusConnection colors;
  private boolean installed;

  public BrowserStyleRegistry() {
    alias("DEFAULT_NULL_TEXT_COLOR", "NOT_USED_ELEMENT_ATTRIBUTES");
    alias("FOLDED_TEXT", "FOLDED_TEXT_ATTRIBUTES");
    alias("URL", "HYPERLINK_ATTRIBUTES");
    alias("INSTANCE_FIELD", "INSTANCE_FIELD_ATTRIBUTES");
    alias("STATIC_FIELD", "STATIC_FIELD_ATTRIBUTES");
    alias("STATIC_FINAL_FIELD", "STATIC_FINAL_FIELD_ATTRIBUTES");
    alias("DEPRECATED", "DEPRECATED_ATTRIBUTES");
    alias("ANNOTATION", "ANNOTATION_NAME_ATTRIBUTES");
    alias("NOT_USED_ELEMENT", "NOT_USED_ELEMENT_ATTRIBUTES");
    alias("TODO", "TODO_DEFAULT_ATTRIBUTES");
    alias("BREAKPOINT", "BREAKPOINT_ATTRIBUTES");
    alias("EXECUTIONPOINT", "EXECUTIONPOINT_ATTRIBUTES");
    alias("WARNING_PANEL", "WARNING_ATTRIBUTES");
    alias("INFORMATION_PANEL", "INFORMATION_ATTRIBUTES");
    defaults();
  }

  private void alias(String mps, String scheme) { aliases.put(mps, scheme); }
  private String schemeKey(String key) { return aliases.getOrDefault(key, key); }
  private EditorColorsScheme scheme() { return EditorColorsManager.getInstance().getGlobalScheme(); }

  @Override public void init() {
    if (installed) throw new IllegalStateException("Browser styles already installed");
    Application application = Objects.requireNonNull(ApplicationManager.getApplication(), "Browser application required");
    colors = application.getMessageBus().connect();
    colors.subscribe(EditorColorsManager.TOPIC, changed -> { clearCache(); defaults(); });
    previous = ourInstance;
    ourInstance = this;
    installed = true;
  }

  @Override public void dispose() {
    if (!installed) return;
    installed = false;
    try { colors.disconnect(); }
    finally {
      if (ourInstance == this) ourInstance = previous;
      previous = null;
      colors = null;
      clearCache();
    }
  }

  @Override public Color getEditorBackground() { return scheme().getDefaultBackground(); }
  @Override public Color getEditorForeground() { return scheme().getDefaultForeground(); }
  @Override public boolean isDarkTheme() { return ColorUtil.isDark(getEditorBackground()); }

  @Override public Color getColor(String key) {
    String mapped = schemeKey(key);
    Color color = scheme().getColor(ColorKey.createColorKey(mapped));
    if (color != null) return color;
    TextAttributes attributes = scheme().getAttributes(TextAttributesKey.createTextAttributesKey(mapped));
    if (attributes != null && attributes.getForegroundColor() != null) return attributes.getForegroundColor();
    if ("DEFAULT_NULL_TEXT_COLOR".equals(key)) return isDarkTheme() ? new Color(170, 170, 170) : Color.GRAY;
    Style cached = super.getStyle(key);
    return cached == null ? null : cached.get(StyleAttributes.TEXT_COLOR);
  }

  @Override public Style getStyle(String key) {
    Style cached = super.getStyle(key);
    if (cached != null) return cached;
    StyleImpl style = new StyleImpl();
    TextAttributes attributes = scheme().getAttributes(TextAttributesKey.createTextAttributesKey(schemeKey(key)));
    if (attributes != null) {
      style.set(StyleAttributes.TEXT_COLOR, attributes.getForegroundColor());
      style.set(StyleAttributes.TEXT_BACKGROUND_COLOR, attributes.getBackgroundColor());
      style.set(StyleAttributes.FONT_STYLE, attributes.getFontType());
      if (attributes.getEffectColor() != null) {
        style.set(StyleAttributes.UNDERLINED, attributes.getEffectType() == EffectType.LINE_UNDERSCORE);
        style.set(StyleAttributes.STRIKE_OUT, attributes.getEffectType() == EffectType.STRIKEOUT);
      }
    }
    if ("MATCHED_BRACE_ATTRIBUTES".equals(key) && (attributes == null || attributes.getBackgroundColor() == null)) {
      style.set(StyleAttributes.TEXT_BACKGROUND_COLOR, isDarkTheme() ? new Color(60, 90, 70) : new Color(205, 235, 205));
    }
    style.setParent(super.getStyle("EDITOR_DEFAULTS"), List.of(StyleAttributes.TEXT_COLOR, StyleAttributes.TEXT_BACKGROUND_COLOR));
    setStyle(key, style);
    return style;
  }

  private void defaults() {
    StyleImpl editor = new StyleImpl();
    editor.set(StyleAttributes.TEXT_COLOR, getEditorForeground());
    editor.set(StyleAttributes.TEXT_BACKGROUND_COLOR, getEditorBackground());
    setStyle("EDITOR_DEFAULTS", editor);
    // MPS custom style names and light/dark colors mirror StyleRegistryIdeaImpl.
    background("HIGHLIGHTED_LABEL_IN_EDITOR", new Color(255, 233, 89, 100), new Color(17, 73, 87, 200));
    background("HIGHLIGHTED_CELL", new Color(255, 233, 89, 160), new Color(17, 73, 87));
    background("TYPESYSTEM_OPERATOR", Color.BLACK, Color.WHITE);
    background("TYPESYSTEM_CHECKONLY_OPERATOR", Color.GRAY, new Color(200, 200, 200));
    background("QUOTATION_BRACE", new Color(0, 200, 200, 90), new Color(0, 155, 155, 200));
    background("LIGHT_QUOTATION_BRACE", new Color(116, 164, 200, 200), new Color(116, 180, 216, 140));
    background("ERROR_CELL", new Color(255, 220, 220, 250), new Color(214, 77, 91, 100));
    background("REFLECTIVE_EDITOR_FIRST_LABEL", new Color(107, 142, 20, 60), new Color(107, 142, 20, 100));
    background("CELL_COLLECTION_TAG_BACKGROUND", new Color(230, 230, 230), new Color(84, 84, 84));
    Style completion = background("COMPLETION_POPUP", new Color(235, 244, 254), new Color(20, 29, 41));
    completion.set(StyleAttributes.TEXT_COLOR, getEditorForeground());
    completion.set(StyleAttributes.SELECTED_TEXT_COLOR, scheme().getColor(EditorColors.SELECTION_FOREGROUND_COLOR));
    completion.set(StyleAttributes.SELECTED_TEXT_BACKGROUND_COLOR, scheme().getColor(EditorColors.SELECTION_BACKGROUND_COLOR));
    background("INFORMATION_PANEL", new Color(235, 244, 254), new Color(40, 48, 60));
    background("WARNING_PANEL", new Color(255, 248, 210), new Color(60, 52, 35));
  }

  private Style background(String key, Color light, Color dark) {
    StyleImpl style = new StyleImpl();
    Color color = isDarkTheme() ? dark : light;
    style.set(StyleAttributes.TEXT_BACKGROUND_COLOR, color);
    style.set(StyleAttributes.BACKGROUND_COLOR, color);
    style.setParent(super.getStyle("EDITOR_DEFAULTS"), List.of(StyleAttributes.TEXT_COLOR));
    setStyle(key, style);
    return style;
  }
}
