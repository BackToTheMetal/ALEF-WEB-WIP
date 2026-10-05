package jetbrains.mps.smodel;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import org.jetbrains.mps.openapi.model.SModel;

/** Shared model lock and active command context for the single browser event loop. */
public final class BrowserModelAccess extends DefaultModelAccess implements ModelCommandContext.Provider {
  private final ModelAccess previous;
  private ModelCommandContext.Provider command;
  private boolean disposed;

  public BrowserModelAccess() {
    previous = ModelAccess.instance();
    if (previous instanceof BrowserModelAccess) throw new IllegalStateException("A browser model-access host is already installed");
    ModelAccess.setInstance(this);
  }
  public static BrowserModelAccess getInstalled() {
    if (!(ModelAccess.instance() instanceof BrowserModelAccess)) throw new IllegalStateException("Install BrowserModelAccess before the MPS platform");
    return (BrowserModelAccess) ModelAccess.instance();
  }
  @Override public void runReadInEDT(Runnable action) {
    if (disposed) throw new IllegalStateException("Browser model access is disposed");
    ApplicationManager.getApplication().invokeLater(() -> {
      if (!disposed) runReadAction(action);
    });
  }
  @Override public void runWriteInEDT(Runnable action) {
    if (disposed) throw new IllegalStateException("Browser model access is disposed");
    ApplicationManager.getApplication().invokeLater(() -> {
      if (!disposed) ApplicationManager.getApplication().runWriteAction(() -> runWriteAction(action));
    });
  }
  public void installMCC(ModelCommandContext.Provider provider) {
    if (disposed || command != null) throw new IllegalStateException("Another project command is active");
    command = provider;
  }
  public void uninstallMCC(ModelCommandContext.Provider provider) {
    if (command != provider) throw new IllegalStateException("Mismatched model command context");
    command = null;
  }
  @Override public ModelCommandContext getCommandContext(SModel model) {
    return command == null ? null : command.getCommandContext(model);
  }
  public void executeCommand_(Runnable action, Project project, UndoRunnable description) {
    if (disposed) throw new IllegalStateException("Browser model access is disposed");
    ApplicationManager.getApplication().assertIsDispatchThread();
    CommandProcessor.getInstance().executeCommand(project,
        () -> ApplicationManager.getApplication().runWriteAction(() -> runWriteAction(action)),
        description == null ? "Edit model" : description.getName(), description == null ? null : description.getGroupId());
  }
  public void runCommandInEDT_(Runnable action, Project project, UndoRunnable description) {
    ApplicationManager.getApplication().invokeLater(() -> {
      if (!disposed && !project.isDisposed()) executeCommand_(action, project, description);
    });
  }
  public void runUndoTransparentCommand(Runnable action) {
    if (disposed) throw new IllegalStateException("Browser model access is disposed");
    ApplicationManager.getApplication().assertIsDispatchThread();
    CommandProcessor.getInstance().runUndoTransparentAction(
        () -> ApplicationManager.getApplication().runWriteAction(() -> runWriteAction(action)));
  }
  public void dispose() {
    if (disposed) return;
    if (command != null) throw new IllegalStateException("Cannot dispose during a model command");
    if (ModelAccess.instance() == this) ModelAccess.setInstance(previous);
    disposed = true;
  }
}
