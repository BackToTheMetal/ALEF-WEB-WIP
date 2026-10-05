package com.intellij.openapi.command;
import com.intellij.compat.BrowserMessageBus;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
public class CommandProcessor {
  private static final CommandProcessor INSTANCE = new CommandProcessor();
  private final List<CommandListener> listeners = new ArrayList<>();
  private Runnable command;
  private String name;
  private Object group, token;
  private Project project;
  private int transparentDepth;
  public interface BrowserTransactionGuard { void begin(); void finish(boolean success); }
  private BrowserTransactionGuard browserGuard;
  private int browserDepth;
  private boolean browserFailed;
  public void setBrowserTransactionGuard(BrowserTransactionGuard guard) { browserGuard = guard; }
  public void beginBrowserTransaction() {
    if (browserDepth++ == 0) { browserFailed = false; if (browserGuard != null) browserGuard.begin(); }
  }
  public void endBrowserTransaction(boolean success) {
    browserFailed |= !success;
    if (--browserDepth == 0 && browserGuard != null) browserGuard.finish(!browserFailed);
  }

  public static CommandProcessor getInstance() { return INSTANCE; }
  public void executeCommand(Project project, Runnable action, String name, Object group) {
    executeCommand(project, action, name, group, UndoConfirmationPolicy.DEFAULT, true, null);
  }
  public void executeCommand(Project project, Runnable action, String name, Object group, Document document) {
    executeCommand(project, action, name, group, UndoConfirmationPolicy.DEFAULT, true, document);
  }
  public void executeCommand(Project project, Runnable action, String name, Object group, UndoConfirmationPolicy policy) {
    executeCommand(project, action, name, group, policy, true, null);
  }
  public void executeCommand(Project project, Runnable action, String name, Object group, UndoConfirmationPolicy policy, Document document) {
    executeCommand(project, action, name, group, policy, true, document);
  }
  public void executeCommand(Project project, Runnable action, String name, Object group, UndoConfirmationPolicy policy, boolean original) {
    executeCommand(project, action, name, group, policy, original, null);
  }
  public void executeCommand(Project project, Runnable action, String name, Object group, UndoConfirmationPolicy policy, boolean original, Document document) {
    if (command != null) {
      if (this.project != project) throw new IllegalStateException("Nested commands cannot cross browser projects");
      action.run(); return;
    }
    this.command = action; this.name = name; this.group = group; this.project = project; token = new Object();
    CommandEvent event = new CommandEvent(this, action, name, group, project, policy, original, document);
    beginBrowserTransaction(); boolean succeeded = false;
    try {
      notifyListeners(listener -> listener.commandStarted(event));
      action.run(); succeeded = true;
    } finally {
      try { notifyListeners(listener -> listener.beforeCommandFinished(event)); }
      finally {
        try { notifyListeners(listener -> listener.commandFinished(event)); }
        finally { command = null; this.name = null; this.group = null; this.project = null; token = null; endBrowserTransaction(succeeded); }
      }
    }
  }
  public Runnable getCurrentCommand() { return command; }
  public String getCurrentCommandName() { return name; }
  public Object getCurrentCommandGroupId() { return group; }
  public Project getCurrentCommandProject() { return project; }
  public Object getBrowserCommandToken() { return token; }
  public boolean isCommandInProgress() { return command != null; }
  public void setCurrentCommandName(String value) { name = value; }
  public void setCurrentCommandGroupId(Object value) { group = value; }
  public void runUndoTransparentAction(Runnable action) {
    boolean outer = transparentDepth++ == 0;
    beginBrowserTransaction(); boolean succeeded = false;
    try { if (outer) notifyListeners(CommandListener::undoTransparentActionStarted); action.run(); succeeded = true; }
    finally {
      try { if (outer) notifyListeners(CommandListener::beforeUndoTransparentActionFinished); }
      finally { try { transparentDepth--; if (outer) notifyListeners(CommandListener::undoTransparentActionFinished); } finally { endBrowserTransaction(succeeded); } }
    }
  }
  public boolean isUndoTransparentActionInProgress() { return transparentDepth > 0; }
  public void addCommandListener(CommandListener listener) { listeners.add(listener); }
  public void addCommandListener(CommandListener listener, Disposable parent) { listeners.add(listener); Disposer.register(parent, () -> listeners.remove(listener)); }
  public void removeCommandListener(CommandListener listener) { listeners.remove(listener); }
  public void markCurrentCommandAsGlobal(Project project) { /* Browser histories are per project, not per text document. */ }
  private void notifyListeners(Consumer<CommandListener> event) {
    for (CommandListener listener : new ArrayList<>(listeners)) event.accept(listener);
    Application application = ApplicationManager.getApplication();
    if (application != null && application.getMessageBus() instanceof BrowserMessageBus) {
      ((BrowserMessageBus) application.getMessageBus()).publish(CommandListener.TOPIC, event);
    }
  }
}
