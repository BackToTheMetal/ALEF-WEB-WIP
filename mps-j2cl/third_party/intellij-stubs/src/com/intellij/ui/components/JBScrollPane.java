package com.intellij.ui.components;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
public class JBScrollPane extends JScrollPane {
  public enum Flip { NONE, VERTICAL, HORIZONTAL, BOTH }
  public JBScrollPane() { this(null); }
  public JBScrollPane(Component view) { this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED); }
  public JBScrollPane(Component view, int vertical, int horizontal) {
    super(view, vertical, horizontal);
    setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 196, 208)));
  }
  public JBScrollPane(int vertical, int horizontal) { this(null, vertical, horizontal); }
  public static boolean canBePreprocessed(MouseEvent event, JScrollBar scrollbar) { return event instanceof java.awt.event.MouseWheelEvent; }
}
