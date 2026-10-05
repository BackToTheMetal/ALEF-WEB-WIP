package com.intellij.openapi.editor.colors;
import com.intellij.openapi.editor.markup.TextAttributes;
import java.awt.Color;
import java.util.IdentityHashMap;
import java.util.Map;
public class EditorColorsManager {
  public static final com.intellij.util.messages.Topic<EditorColorsListener> TOPIC = com.intellij.util.messages.Topic.create("EditorColors", EditorColorsListener.class);
  private static final EditorColorsManager INSTANCE = new EditorColorsManager();
  private EditorColorsScheme global = new Scheme();
  public static EditorColorsManager getInstance() { return INSTANCE; }
  public EditorColorsScheme getGlobalScheme() { return global; }
  public void setGlobalScheme(EditorColorsScheme scheme) {
    global = java.util.Objects.requireNonNull(scheme);
    com.intellij.openapi.application.Application app = com.intellij.openapi.application.ApplicationManager.getApplication();
    if (app != null && app.getMessageBus() instanceof com.intellij.compat.BrowserMessageBus) {
      ((com.intellij.compat.BrowserMessageBus) app.getMessageBus()).publish(TOPIC, listener -> listener.globalSchemeChange(scheme));
    }
  }
  private static final class Scheme implements EditorColorsScheme {
    private final ModifiableFontPreferences fontPreferences = new ModifiableFontPreferences();
    @Override public FontPreferences getFontPreferences() { return fontPreferences; }
    final Map<ColorKey, Color> colors = new IdentityHashMap<>();
    final Map<TextAttributesKey, TextAttributes> attributes = new IdentityHashMap<>();
    Scheme() {
      // Desktop schemes load these eagerly; MPS deletion resolves them by name.
      attributes.put(CodeInsightColors.ERRORS_ATTRIBUTES, CodeInsightColors.ERRORS_ATTRIBUTES.getDefaultAttributes());
      attributes.put(CodeInsightColors.WARNINGS_ATTRIBUTES, CodeInsightColors.WARNINGS_ATTRIBUTES.getDefaultAttributes());
      attributes.put(CodeInsightColors.WEAK_WARNING_ATTRIBUTES, CodeInsightColors.WEAK_WARNING_ATTRIBUTES.getDefaultAttributes());
    }
    @Override public Color getColor(ColorKey key) { return colors.containsKey(key) ? colors.get(key) : key.getDefaultColor(); }
    @Override public void setColor(ColorKey key, Color color) { colors.put(key, color); }
    @Override public TextAttributes getAttributes(TextAttributesKey key) { return attributes.containsKey(key) ? attributes.get(key) : key.getDefaultAttributes(); }
    @Override public void setAttributes(TextAttributesKey key, TextAttributes value) { attributes.put(key, value); }
    @Override public Color getDefaultBackground() { return Color.WHITE; }
    @Override public Color getDefaultForeground() { return Color.BLACK; }
    @Override public int getEditorFontSize() { return 13; }
    @Override public String getEditorFontName() { return "Monospaced"; }
    @Override public float getLineSpacing() { return 1.2f; }
  }
}
