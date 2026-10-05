package com.intellij.openapi.wm;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
/** Host-mountable tool panels. The browser host adds getComponent() to its layout. */
public final class ToolWindowManager implements Disposable {
  private static final Key<ToolWindowManager> KEY = Key.create("browser.tool.windows");
  private final Map<String, ToolWindow> windows = new LinkedHashMap<>();
  public static ToolWindowManager getInstance(Project project) {
    ToolWindowManager manager = project.getUserData(KEY);
    if (manager == null) { manager = new ToolWindowManager(); project.putUserData(KEY, manager); Disposer.register(project, manager); manager.registerBrowserToolWindow("Documentation", new BrowserToolWindow()); }
    return manager;
  }
  public ToolWindow getToolWindow(String id) { return windows.get(id); }
  public void registerBrowserToolWindow(String id, ToolWindow window) {
    if (windows.containsKey(id)) throw new IllegalArgumentException("Tool window already registered: " + id);
    windows.put(id, window); Disposer.register(this, window);
  }
  public void dispose() { Disposer.disposeChildren(this, null); windows.clear(); }
  private static final class BrowserToolWindow implements ToolWindowEx {
    private final JPanel panel = new JPanel(new BorderLayout());
    private final BrowserContentManager contents = new BrowserContentManager();
    private List<? extends AnAction> titleActions = new ArrayList<>();
    private ActionGroup gearActions;
    private boolean available = true;
    BrowserToolWindow() { panel.add(contents.getComponent(), BorderLayout.CENTER); panel.setVisible(false); Disposer.register(this, contents); }
    public ContentManager getContentManager() { return contents; }
    public JComponent getComponent() { return panel; }
    public boolean isVisible() { return panel.isVisible(); }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean value) { available = value; if (!value) hide(); }
    public void show() { if (!available) throw new IllegalStateException("Tool window unavailable"); panel.setVisible(true); }
    public void hide() { panel.setVisible(false); }
    public void activate(Runnable callback) { show(); Content selected = contents.getSelectedContent(); if (selected != null) selected.getComponent().requestFocusInWindow(); if (callback != null) callback.run(); }
    public void setTitleActions(List<? extends AnAction> actions) { titleActions = new ArrayList<>(actions); rebuildToolbar(); }
    public void setAdditionalGearActions(ActionGroup actions) { gearActions = actions; rebuildToolbar(); }
    private JComponent toolbar;
    private void rebuildToolbar() {
      if (toolbar != null) panel.remove(toolbar);
      DefaultActionGroup group = new DefaultActionGroup(titleActions);
      if (gearActions != null) group.add(gearActions);
      ActionToolbar actions = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, true);
      actions.setTargetComponent(panel); toolbar = actions.getComponent(); panel.add(toolbar, BorderLayout.NORTH); panel.revalidate(); panel.repaint();
    }
    public void dispose() { Disposer.disposeChildren(this, null); panel.removeAll(); }
  }
  private static final class BrowserContentManager implements ContentManager {
    private final List<Content> contents = new ArrayList<>();
    private final JPanel panel = new JPanel(new BorderLayout());
    private Content selected;
    public void addContent(Content content) { if (contents.contains(content)) return; contents.add(content); Disposer.register(this, content); setSelectedContent(content); }
    public boolean removeContent(Content content, boolean dispose) {
      if (!contents.remove(content)) return false;
      if (dispose) Disposer.dispose(content);
      if (selected == content) { selected = null; panel.removeAll(); if (!contents.isEmpty()) setSelectedContent(contents.get(contents.size() - 1)); }
      return true;
    }
    public Content[] getContents() { return contents.toArray(new Content[0]); }
    public Content getSelectedContent() { return selected; }
    public void setSelectedContent(Content content) { if (!contents.contains(content)) throw new IllegalArgumentException("Unknown content"); selected = content; panel.removeAll(); panel.add(content.getComponent(), BorderLayout.CENTER); panel.revalidate(); panel.repaint(); }
    public JComponent getComponent() { return panel; }
    public void dispose() { Disposer.disposeChildren(this, null); contents.clear(); selected = null; panel.removeAll(); }
  }
}
