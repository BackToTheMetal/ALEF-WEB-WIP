package com.intellij.openapi.ui;
import java.awt.Color;
import javax.swing.Icon;
public final class MessageType {
  public static final MessageType INFO = new MessageType(new Color(0xE8F2FF), "Information"), WARNING = new MessageType(new Color(0xFFF1CA), "Warning"), ERROR = new MessageType(new Color(0xFFE0E0), "Error");
  private final Color background; private final String name;
  private MessageType(Color background, String name) { this.background = background; this.name = name; }
  public Color getPopupBackground() { return background; }
  public Color getBorderColor() { return background.darker(); }
  public Icon getDefaultIcon() { return com.intellij.compat.BrowserIcons.named("AllIcons.General." + name); }
}
