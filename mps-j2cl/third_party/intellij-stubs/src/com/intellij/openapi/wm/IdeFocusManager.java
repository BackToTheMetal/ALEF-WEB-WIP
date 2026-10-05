package com.intellij.openapi.wm;
import com.intellij.openapi.project.Project;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.SwingUtilities;
public final class IdeFocusManager {
  private static final IdeFocusManager INSTANCE = new IdeFocusManager();
  public static IdeFocusManager getGlobalInstance() { return INSTANCE; }
  public static IdeFocusManager getInstance(Project project) { return INSTANCE; }
  public static IdeFocusManager findInstanceByComponent(Component component) { return INSTANCE; }
  public void doWhenFocusSettlesDown(Runnable action) { SwingUtilities.invokeLater(action); }
  public void requestFocus(Component component, boolean forced) { if (forced) component.requestFocus(); else component.requestFocusInWindow(); }
  public Component getFocusOwner() { return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(); }
}
