package com.intellij.openapi.actionSystem;
import java.util.Objects;
import javax.swing.KeyStroke;
public final class KeyboardShortcut implements Shortcut {
  private final KeyStroke first, second;
  public KeyboardShortcut(KeyStroke first, KeyStroke second) { this.first = Objects.requireNonNull(first); this.second = second; }
  public static KeyboardShortcut fromString(String text) {
    int separator = text.indexOf(',');
    KeyStroke first = KeyStroke.getKeyStroke((separator < 0 ? text : text.substring(0, separator)).trim());
    KeyStroke second = separator < 0 ? null : KeyStroke.getKeyStroke(text.substring(separator + 1).trim());
    if (first == null || separator >= 0 && second == null) throw new IllegalArgumentException("Invalid shortcut: " + text);
    return new KeyboardShortcut(first, second);
  }
  public KeyStroke getFirstKeyStroke() { return first; }
  public KeyStroke getSecondKeyStroke() { return second; }
  @Override public boolean isKeyboard() { return true; }
  @Override public boolean equals(Object value) {
    return value instanceof KeyboardShortcut && first.equals(((KeyboardShortcut) value).first) && Objects.equals(second, ((KeyboardShortcut) value).second);
  }
  @Override public int hashCode() { return Objects.hash(first, second); }
}
