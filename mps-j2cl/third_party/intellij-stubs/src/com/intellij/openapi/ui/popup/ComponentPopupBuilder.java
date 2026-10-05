package com.intellij.openapi.ui.popup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.popup.AbstractPopup;
import javax.swing.JComponent;
public class ComponentPopupBuilder {
  private final JComponent content, focus;
  private boolean requestFocus;
  private boolean focusable = true;
  private java.awt.Dimension minimumSize;
  private java.util.function.Supplier<Boolean> cancel;
  private String advertisement;
  public ComponentPopupBuilder(JComponent content, JComponent focus) { this.content = content; this.focus = focus; }
  public ComponentPopupBuilder setProject(Project project) { return this; }
  public ComponentPopupBuilder setLocateWithinScreenBounds(boolean value) { return this; }
  public ComponentPopupBuilder setAdText(String value) { advertisement = value; return this; }
  public ComponentPopupBuilder setResizable(boolean value) { return this; }
  public ComponentPopupBuilder setMovable(boolean value) { return this; }
  public ComponentPopupBuilder setFocusable(boolean value) { focusable = value; return this; }
  public ComponentPopupBuilder setRequestFocus(boolean value) { requestFocus = value; return this; }
  public ComponentPopupBuilder setModalContext(boolean value) { return this; }
  public ComponentPopupBuilder setCancelOnClickOutside(boolean value) { return this; }
  public ComponentPopupBuilder setCancelOnWindowDeactivation(boolean value) { return this; }
  public ComponentPopupBuilder setCancelOnOtherWindowOpen(boolean value) { return this; }
  public ComponentPopupBuilder setCancelKeyEnabled(boolean value) { return this; }
  public ComponentPopupBuilder setBelongsToGlobalPopupStack(boolean value) { return this; }
  public ComponentPopupBuilder setShowShadow(boolean value) { return this; }
  public ComponentPopupBuilder setShowBorder(boolean value) { if (!value) content.setBorder(javax.swing.BorderFactory.createEmptyBorder()); return this; }
  public ComponentPopupBuilder setTitle(String title) { content.setName(title); return this; }
  public ComponentPopupBuilder setDimensionServiceKey(Project project, String key, boolean useForXY) { return this; }
  public ComponentPopupBuilder setMinSize(java.awt.Dimension size) { minimumSize = size; return this; }
  public ComponentPopupBuilder setCancelCallback(java.util.function.Supplier<Boolean> callback) { cancel = callback; return this; }
  public JBPopup createPopup() {
    if (minimumSize != null) content.setMinimumSize(minimumSize);
    JComponent displayed = content;
    if (advertisement != null && !advertisement.isEmpty()) {
      javax.swing.JPanel wrapper = new javax.swing.JPanel(new java.awt.BorderLayout());
      wrapper.add(content, java.awt.BorderLayout.CENTER); wrapper.add(new javax.swing.JLabel(advertisement), java.awt.BorderLayout.SOUTH); displayed = wrapper;
    }
    AbstractPopup result = new AbstractPopup(displayed, focus); result.setFocusable(focusable); result.setRequestFocus(requestFocus);
    if (cancel != null) result.setCancelCallback(cancel); return result;
  }
}
