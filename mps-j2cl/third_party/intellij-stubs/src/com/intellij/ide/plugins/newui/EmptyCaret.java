package com.intellij.ide.plugins.newui;
/** Read-only documentation caret keeps selection mechanics without a blinking insertion marker. */
public class EmptyCaret extends javax.swing.text.DefaultCaret {
  @Override public void paint(java.awt.Graphics graphics) {}
  @Override public void setVisible(boolean visible) { super.setVisible(false); }
}
