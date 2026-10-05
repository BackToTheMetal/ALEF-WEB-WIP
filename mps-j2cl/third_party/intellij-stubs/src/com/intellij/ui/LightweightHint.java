package com.intellij.ui;
import com.intellij.openapi.util.UserDataHolderBase;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
public class LightweightHint extends UserDataHolderBase {
  private final JComponent component;
  private final JPopupMenu popup = new JPopupMenu();
  public LightweightHint(JComponent component) { this.component = component; popup.add(component); }
  public JComponent getComponent() { return component; }
  public void show(JComponent parent, int x, int y, JComponent focus, HintHint hint) { popup.setRequestFocusEnabled(hint.isRequestFocus()); popup.show(parent, x, y); if (hint.isRequestFocus() && focus != null) focus.requestFocusInWindow(); }
  public boolean isVisible() { return popup.isVisible(); }
  public void hide() { popup.setVisible(false); }
  public void hide(boolean ok) { hide(); }
  public java.awt.Dimension getSize() { return component.getSize(); }
  public void setSize(java.awt.Dimension size) { component.setPreferredSize(size); }
  public void pack() { popup.pack(); }
}
