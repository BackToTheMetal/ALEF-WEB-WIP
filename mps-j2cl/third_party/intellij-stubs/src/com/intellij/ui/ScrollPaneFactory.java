package com.intellij.ui;
import java.awt.Component;
import javax.swing.JScrollPane;
public final class ScrollPaneFactory {
  private ScrollPaneFactory() {}
  public static JScrollPane createScrollPane(Component view) { return new com.intellij.ui.components.JBScrollPane(view); }
  public static JScrollPane createScrollPane(Component view, boolean borderless) {
    JScrollPane pane = createScrollPane(view); if (borderless) pane.setBorder(javax.swing.BorderFactory.createEmptyBorder()); return pane;
  }
  public static JScrollPane createScrollPane(Component view, int vertical, int horizontal) { return new com.intellij.ui.components.JBScrollPane(view, vertical, horizontal); }
}
