package com.intellij.openapi.project;
import com.intellij.openapi.util.Key;
import java.util.ArrayList;
import java.util.List;
public class DumbService {
  private static final Key<DumbService> KEY = Key.create("browser.dumbService");
  private final List<Runnable> waiting = new ArrayList<>();
  private boolean dumb;
  public static DumbService getInstance(Project project) {
    DumbService service = project.getUserData(KEY);
    if (service == null) { service = new DumbService(); project.putUserData(KEY, service); }
    return service;
  }
  public boolean isDumb() { return dumb; }
  public static boolean isDumb(Project project) { return getInstance(project).isDumb(); }
  public void setBrowserDumb(boolean value) {
    dumb = value;
    if (!value) { List<Runnable> ready = new ArrayList<>(waiting); waiting.clear(); ready.forEach(Runnable::run); }
  }
  public void runWhenSmart(Runnable action) { if (dumb) waiting.add(action); else action.run(); }
  public void smartInvokeLater(Runnable action) { runWhenSmart(() -> javax.swing.SwingUtilities.invokeLater(action)); }
  public void waitForSmartMode() { if (dumb) throw new UnsupportedOperationException("Waiting for browser indexing must be asynchronous"); }
}
