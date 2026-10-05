package com.intellij.ui.content;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import javax.swing.JComponent;
public final class ContentFactory {
  private static final ContentFactory INSTANCE = new ContentFactory();
  public static ContentFactory getInstance() { return INSTANCE; }
  public Content createContent(JComponent component, String displayName, boolean isLockable) { return new BrowserContent(component, displayName); }
  private static final class BrowserContent extends UserDataHolderBase implements Content {
    private JComponent component; private String name; private boolean closeable;
    BrowserContent(JComponent component, String name) { this.component = component; this.name = name; }
    public JComponent getComponent() { return component; }
    public void setComponent(JComponent value) {
      JComponent old = component; component = value;
      if (old != null && old.getParent() != null) {
        java.awt.Container parent = old.getParent(); parent.remove(old); parent.add(value, java.awt.BorderLayout.CENTER); parent.validate(); parent.repaint();
      }
    }
    public String getDisplayName() { return name; }
    public void setDisplayName(String value) { name = value; }
    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean value) { closeable = value; }
    public void setDisposer(Disposable value) { Disposer.register(this, value); }
    public void dispose() { Disposer.disposeChildren(this, null); if (component != null && component.getParent() != null) component.getParent().remove(component); clearUserData(); }
  }
}
