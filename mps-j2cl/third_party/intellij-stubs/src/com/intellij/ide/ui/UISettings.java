package com.intellij.ide.ui;
import java.awt.RenderingHints;
import javax.swing.JComponent;
public class UISettings {
  private static final UISettings INSTANCE = new UISettings();
  public static UISettings getInstance() { return INSTANCE; }
  public static void setupEditorAntialiasing(JComponent component) { component.putClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); }
}
