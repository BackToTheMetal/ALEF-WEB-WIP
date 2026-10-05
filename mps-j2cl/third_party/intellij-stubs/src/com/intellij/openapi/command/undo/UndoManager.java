package com.intellij.openapi.command.undo;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/** Per-project browser history. It stores model undo actions, not IntelliJ document snapshots. */
public class UndoManager implements Disposable {
  private static final Key<UndoManager> KEY = Key.create("browser.undo.manager");
  private static final UndoManager GLOBAL = new UndoManager();
  private final List<Frame> undo = new ArrayList<>(), redo = new ArrayList<>();
  private boolean undoing, redoing, disposed, mergeAllowed = true;
  private long historyVersion;
  public static UndoManager getInstance(Project project) {
    if (project == null) return GLOBAL;
    UndoManager manager = project.getUserData(KEY);
    if (manager == null) { manager = new UndoManager(); project.putUserData(KEY, manager); Disposer.register(project, manager); }
    return manager;
  }
  public static UndoManager getGlobalInstance() { return GLOBAL; }
  public void undoableActionPerformed(UndoableAction action) {
    ensureLive();
    if (isUndoOrRedoInProgress()) return;
    CommandProcessor processor = CommandProcessor.getInstance();
    Object token = processor.getBrowserCommandToken(), group = processor.getCurrentCommandGroupId();
    Frame previous = undo.isEmpty() ? null : undo.get(undo.size() - 1);
    boolean sameCommand = token != null && previous != null && token == previous.token;
    boolean sameGroup = mergeAllowed && group != null && previous != null && Objects.equals(group, previous.group);
    if (sameCommand || sameGroup) previous.actions.add(action);
    else undo.add(new Frame(token, group, processor.getCurrentCommandName(), action));
    redo.clear(); mergeAllowed = true;
  }
  public void nonundoableActionPerformed(DocumentReference reference, boolean global) { clearHistory(); }
  public boolean isUndoInProgress() { return undoing; }
  public boolean isRedoInProgress() { return redoing; }
  public boolean isUndoOrRedoInProgress() { return undoing || redoing; }
  public boolean isUndoAvailable(FileEditor editor) { return !disposed && !undo.isEmpty() && !isUndoOrRedoInProgress(); }
  public boolean isRedoAvailable(FileEditor editor) { return !disposed && !redo.isEmpty() && !isUndoOrRedoInProgress(); }
  public void undo(FileEditor editor) { play(false); }
  public void redo(FileEditor editor) { play(true); }
  public void undo() { undo(null); }
  public void redo() { redo(null); }
  public void clearHistory() { undo.clear(); redo.clear(); mergeAllowed = false; historyVersion++; }
  private void play(boolean forward) {
    ensureLive();
    if (isUndoOrRedoInProgress()) throw new IllegalStateException("Recursive undo/redo");
    List<Frame> from = forward ? redo : undo, to = forward ? undo : redo;
    if (from.isEmpty()) return;
    Frame frame = from.get(from.size() - 1);
    long version = historyVersion;
    undoing = !forward; redoing = forward;
    try {
      if (forward) for (UndoableAction action : frame.actions) action.redo();
      else for (int i = frame.actions.size() - 1; i >= 0; i--) frame.actions.get(i).undo();
      if (historyVersion == version) { from.remove(from.size() - 1); to.add(frame); }
    } catch (UnexpectedUndoException | RuntimeException error) {
      clearHistory();
      throw new IllegalStateException("Undo/redo failed; potentially stale history was cleared", error);
    } catch (Error error) {
      clearHistory();
      throw error;
    } finally { undoing = false; redoing = false; mergeAllowed = false; }
  }
  public Pair<String, String> getUndoActionNameAndDescription(FileEditor editor) { return describe(undo, "Undo"); }
  public Pair<String, String> getRedoActionNameAndDescription(FileEditor editor) { return describe(redo, "Redo"); }
  private Pair<String, String> describe(List<Frame> frames, String operation) {
    String name = frames.isEmpty() ? "" : frames.get(frames.size() - 1).name;
    return Pair.create(operation + (name == null || name.isEmpty() ? "" : " " + name), name);
  }
  @Override public void dispose() { disposed = true; clearHistory(); }
  private void ensureLive() { if (disposed) throw new IllegalStateException("Undo manager disposed"); }
  private static final class Frame {
    final Object token, group;
    final String name;
    final List<UndoableAction> actions = new ArrayList<>();
    Frame(Object token, Object group, String name, UndoableAction action) { this.token = token; this.group = group; this.name = name; actions.add(action); }
  }
}
