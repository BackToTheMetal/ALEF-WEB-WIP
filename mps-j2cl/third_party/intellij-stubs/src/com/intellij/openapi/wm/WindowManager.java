package com.intellij.openapi.wm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ex.StatusBarEx;
import javax.swing.JComponent;
import javax.swing.JLabel;
/** Logical frames backed by Swing components; the browser does not create native OS windows. */
public final class WindowManager {
  private static final WindowManager INSTANCE = new WindowManager();
  private static final Key<IdeFrame> FRAME = Key.create("browser.ide.frame");
  private final IdeFrame fallback = new BrowserFrame(null);
  public static WindowManager getInstance() { return INSTANCE; }
  public IdeFrame getIdeFrame(Project project) {
    if (project == null) return fallback;
    IdeFrame frame = project.getUserData(FRAME);
    if (frame == null) { frame = new BrowserFrame(project); project.putUserData(FRAME, frame); }
    return frame;
  }
  public void registerBrowserFrame(Project project, IdeFrame frame) { project.putUserData(FRAME, frame); }
  public StatusBar getStatusBar(Project project) { return getIdeFrame(project).getStatusBar(); }
  public java.awt.Frame getFrame(Project project) {
    for (java.awt.Component component = getIdeFrame(project).getComponent(); component != null; component = component.getParent()) if (component instanceof java.awt.Frame) return (java.awt.Frame) component;
    return null;
  }
  private static final class BrowserFrame implements IdeFrame {
    private final Project project;
    private final JLabel label = new JLabel();
    private final StatusBarEx status = new StatusBarEx() {
      public String getInfo() { return label.getText(); }
      public void setInfo(String value) { label.setText(value == null ? "" : value); }
    };
    BrowserFrame(Project project) { this.project = project; }
    public StatusBar getStatusBar() { return status; }
    public Project getProject() { return project; }
    public JComponent getComponent() { return label; }
  }
}
