/*
 * Copyright 2003-2022 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.nodeEditor.highlighter;

import com.intellij.openapi.project.IndexNotReadyException;
import jetbrains.mps.logging.Logger;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.EditorMessage;
import jetbrains.mps.nodeEditor.EditorSettings;
import jetbrains.mps.nodeEditor.NodeHighlightManager;
import jetbrains.mps.nodeEditor.PriorityComparator;
import jetbrains.mps.nodeEditor.checking.EditorChecker;
import jetbrains.mps.nodeEditor.checking.UpdateResult;
import jetbrains.mps.nodeEditor.checking.UpdateResult.Completed;
import jetbrains.mps.smodel.CancellableReadAction;
import jetbrains.mps.smodel.ModelAccessHelper;
import jetbrains.mps.smodel.SNodePointer;
import jetbrains.mps.typechecking.TypecheckingFacade;
import jetbrains.mps.util.Cancellable;
import jetbrains.mps.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.model.SNode;
import org.jetbrains.mps.openapi.model.SNodeUtil;
import org.jetbrains.mps.openapi.module.ModelAccess;
import org.jetbrains.mps.openapi.module.SRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class HighlighterUpdateSession {
  private static final Logger LOG = Logger.getLogger(HighlighterUpdateSession.class);

  private final IHighlighter myHighlighter;
  private final Collection<EditorCheckerWrapper> myCheckers;
  private final List<EditorComponent> myAllEditorComponents;
  @Nullable
  private final EditorComponent myInspector;

  public HighlighterUpdateSession(IHighlighter highlighter, Collection<EditorCheckerWrapper> checkers,
      List<EditorComponent> allEditorComponents, @Nullable EditorComponent inspector) {
    myHighlighter = highlighter;
    myCheckers = checkers;
    myAllEditorComponents = allEditorComponents;
    myInspector = inspector;
  }

  private void doUpdate() {
    if (myCheckers.isEmpty()) {
      return;
    }

    List<Pair<EditorComponent, Boolean>> input = collectInput();

    final boolean[] isUpdated = {false};
    for (Pair<EditorComponent, Boolean> pair : input) {
      final EditorComponent editorComponent = pair.o1;
      final Boolean applyQuickFixes = pair.o2;

      if (myHighlighter.isPausedOrStopping()) {
        return;
      }
      if (editorComponent.getTypecheckingSession() != null) {
        TypecheckingFacade
            .getFromContext()
            .runWithSession(editorComponent.getTypecheckingSession(),
                            (session) -> {
                              if (updateEditorComponent(editorComponent, false, applyQuickFixes)) {
                                isUpdated[0] = true;
                              }
                            });
      }
    }

    if (myHighlighter.isPausedOrStopping()) {
      return;
    }

    if (myInspector != null && myInspector.getTypecheckingSession() != null) {
      TypecheckingFacade
          .getFromContext()
          .runWithSession(myInspector.getTypecheckingSession(),
                          (session) -> {
                                  updateEditorComponent(myInspector, isUpdated[0], false);
                                });
    }
  }

  private List<Pair<EditorComponent, Boolean>> collectInput() {
    boolean immediateQuickFixesEnabled = !EditorSettings.getInstance().isDisableImmediateQuickFix();
    List<Pair<EditorComponent, Boolean>> input = new ArrayList<>();
    HashSet<SNodePointer> visited = new HashSet<>();
    for (EditorComponent ecomp : myAllEditorComponents) {
      if (ecomp == null || ecomp.isDisposed()) continue;
      final SNode nodeForTypechecking = ecomp.getNodeForTypechecking();
      final SModel model = nodeForTypechecking == null ? null : nodeForTypechecking.getModel();
      final boolean readOnly = ecomp.isReadOnly() || (model != null && model.isReadOnly());
      SNodePointer pointer = new SNodePointer(nodeForTypechecking);
      input.add(new Pair<>(ecomp, immediateQuickFixesEnabled && !readOnly && !visited.contains(pointer)));
      visited.add(pointer);
    }

    return input;
  }

  private boolean updateEditorComponent(final EditorComponent component, final boolean mainEditorMessagesChanged, final boolean applyQuickFixes) {
    if (component.isDisposed() || myHighlighter.isPausedOrStopping()) return false;
    HighlighterEditorTracker editorTracker = myHighlighter.getEditorTracker();
    final SRepository repository = component.getEditorContext().getRepository();
    boolean needsUpdate = new ModelAccessHelper(repository).runReadAction(() -> {
      final SNode editedNode = component.getEditedNode();
      return editedNode != null && SNodeUtil.isAccessible(editedNode, repository);
    });
    if (!needsUpdate) {
      return false;
    }

    final Set<EditorCheckerWrapper> checkersToRecheck = new LinkedHashSet<>();
    boolean rootWasCheckedOnce = editorTracker.wasCheckedOnce(component);
    Instant rootWasLastChecked = editorTracker.wasCheckedWhen(component);
    boolean recreateInspectorMessages =
        myHighlighter.getEditorTracker().isInspector(component) && (mainEditorMessagesChanged || !editorTracker.wereInspectorMessagesCreated());
    editorTracker.markCheckedOnce(component);
    // Messages are associated with an editor component. Unlike regular editor, the one of inspector is disposed/re-created for each new selection
    // therefore we have to re-assign messages for inspector, and that's what recreateInspectorMessages flag indicates.
    // There's MPS-28277 that has been fixed (e189953f) with checkers telling needsUpdate() true for inspector's EC. However, this leads to constant re-check
    // even if nothing changes in the model. The fix could be improved (as its comments suggest) by passing context to needsUpdate() that would tell checker
    // if we truly need to re-check or just care to re-create messages. However, I don't want checker to be aware of inspector and its lifecycle implementation detail
    // (i.e. inspector is disposed on re-selection), nor to care if checker implementor did respect this case. Therefore, we now force all checkers in case
    // inspector messages are needed. Indeed rootWasCheckedOnce used to exclude most of the checkers from working with inspector component, now I'm eager to see
    // if their involvement could cause any issue. Extra time to perform a check has been  addressed with cancellable read actions, so that
    // model commands could start promptly.
    if (!rootWasCheckedOnce || recreateInspectorMessages) {
      checkersToRecheck.addAll(myCheckers);
    } else {
      repository.getModelAccess().runReadAction(new CancellableReadAction() {
        @Override
        protected void execute() {
          if (myHighlighter.isPausedOrStopping()) {
            return;
          }

          Iterator<EditorCheckerWrapper> iterator = myCheckers.iterator();
          while (iterator.hasNext()) {
            EditorCheckerWrapper checker = iterator.next();
            try {
              boolean needsUpdate = checker.needsUpdate(component);
              if (needsUpdate) {
                checkersToRecheck.add(checker);
                if (isCancelRequested()) {
                  confirmCancel();
                  return;
                }
              }
            } catch (LinkageError error) {
              LOG.warning("Caught a linkage error presumably from an extension; the checker will be dropped " + error);
              iterator.remove();
            }
          }
        }
      });
    }

    if (checkersToRecheck.isEmpty() || myHighlighter.isPausedOrStopping()) {
      return false;
    }

    List<EditorCheckerWrapper> checkersToRecheckList = new ArrayList<>(checkersToRecheck);
    checkersToRecheckList.sort(new PriorityComparator());

    return updateEditor(component, rootWasCheckedOnce, rootWasLastChecked, checkersToRecheckList, recreateInspectorMessages, applyQuickFixes);
  }

  private boolean updateEditor(final EditorComponent editor,
                               final boolean wasCheckedOnce,
                               final Instant wasLastChecked,
                               List<EditorCheckerWrapper> checkersToRecheck,
                               boolean recreateInspectorMessages, 
                               final boolean applyQuickFixes)
  {
    if (editor == null || editor.getRootCell() == null) {
      return false;
    }

    final ModelAccess projectModelAccess = myHighlighter.getProject().getModelAccess();
    boolean anyMessageChanged = false;
    for (final EditorCheckerWrapper checker : checkersToRecheck) {
      if (editor.isDisposed() || myHighlighter.isPausedOrStopping()) {
        myHighlighter.getEditorTracker().markUnchecked(editor);
        break;
      }
      final HighlighterReadAction ra = new HighlighterReadAction(checker, editor, wasCheckedOnce, wasLastChecked, applyQuickFixes);
      projectModelAccess.runReadAction(ra);
      UpdateResult checkResult = ra.getUpdateResult();
      if (checkResult == null || checkResult instanceof UpdateResult.Cancelled || myHighlighter.isPausedOrStopping() || editor.isDisposed()) {
        // A browser callback must return to let reload/write/disposal work run.
        // Retain the dirty state and let the next highlighter tick retry.
        myHighlighter.getEditorTracker().markUnchecked(editor);
        break;
      }

      if (checkResult instanceof Completed) {
        Completed completed = (Completed) checkResult;
        if (completed.myMessagesChanged || recreateInspectorMessages) {
          anyMessageChanged = true;
          final NodeHighlightManager highlightManager = editor.getHighlightManager();
          highlightManager.clearForOwner(checker.getEditorMessageOwner(), false);
          for (EditorMessage message : completed.myMessages) {
            highlightManager.mark(message);
          }
        }
      }
    }
    if (myHighlighter.isStopping() || editor.isDisposed()) {
      return false;
    }

    if (anyMessageChanged) {
      editor.getHighlightManager().repaintAndRebuildEditorMessages();
      editor.updateStatusBarMessage();
    }

    return anyMessageChanged;
  }

  private void doneUpdating() {
    for (EditorCheckerWrapper checker : myCheckers) {
      checker.doneUpdating();
    }
  }

  public void update() {
    try {
      doUpdate();
    } finally {
      doneUpdating();
    }
  }

  /**
   * Runs one editor per browser task. Each read action and typechecking context
   * is fully exited before yielding; cancellation/disposal is checked again at
   * the next task. Individual checker algorithms remain synchronous.
   */
  public void updateAsync(Executor executor, Consumer<Throwable> completed) {
    java.util.Objects.requireNonNull(executor);
    java.util.Objects.requireNonNull(completed);
    final List<Pair<EditorComponent, Boolean>> input = collectInput();
    class Work implements Runnable {
      private int next;
      private boolean messagesChanged;
      private boolean finished;

      private void finish(Throwable failure) {
        if (finished) return;
        finished = true;
        try {
          doneUpdating();
        } catch (Throwable cleanupFailure) {
          if (failure == null) failure = cleanupFailure;
          else failure.addSuppressed(cleanupFailure);
        }
        completed.accept(failure);
      }

      @Override public void run() {
        if (finished) return;
        try {
          if (myHighlighter.isPausedOrStopping() || myCheckers.isEmpty()) {
            finish(null);
            return;
          }
          if (next < input.size()) {
            Pair<EditorComponent, Boolean> pair = input.get(next++);
            EditorComponent editor = pair.o1;
            if (!editor.isDisposed() && editor.getTypecheckingSession() != null) {
              TypecheckingFacade.getFromContext().runWithSession(editor.getTypecheckingSession(), session -> {
                messagesChanged |= updateEditorComponent(editor, false, pair.o2);
              });
            }
            executor.execute(this);
          } else {
            if (myInspector != null && !myInspector.isDisposed() && myInspector.getTypecheckingSession() != null) {
              TypecheckingFacade.getFromContext().runWithSession(myInspector.getTypecheckingSession(), session -> {
                updateEditorComponent(myInspector, messagesChanged, false);
              });
            }
            finish(null);
          }
        } catch (Throwable failure) {
          finish(failure);
        }
      }
    }
    Work work = new Work();
    try {
      executor.execute(work);
    } catch (Throwable failure) {
      work.finish(failure);
    }
  }

  private class HighlighterReadAction extends CancellableReadAction implements Cancellable {
    private final EditorCheckerWrapper myChecker;
    private final EditorComponent myEditor;
    private final boolean myWasCheckedOnce;
    private Instant myWasLastChecked;
    private final boolean myApplyQuickFixes;
    // initial state corresponds to isCancelRequested() branch, just in case MA cancels this read action prior to execute()
    private UpdateResult myUpdateResult = UpdateResult.CANCELLED;

    HighlighterReadAction(EditorCheckerWrapper checker, EditorComponent editor, boolean wasCheckedOnce, Instant wasLastChecked, boolean applyQuickFixes) {
      myChecker = checker;
      myEditor = editor;
      myWasCheckedOnce = wasCheckedOnce;
      myWasLastChecked = wasLastChecked;
      myApplyQuickFixes = applyQuickFixes;
    }

    /**
     *
     * @return null value indicates need to re-run; {@code UpdateResult.CANCELLED} indicates action has not been completed but doesn't need a re-run
     */
    public UpdateResult getUpdateResult() {
      return myUpdateResult;
    }

    @Override
    protected void execute() {
      if (myHighlighter.isPausedOrStopping()) {
        myUpdateResult = UpdateResult.CANCELLED;
        return;
      }

      SNode node = myEditor.getEditedNode(); // XXX perhaps, shall use getEditedNodePointer and resolve it, rather than check isAccessible?
      if (node == null) {
        myUpdateResult = UpdateResult.CANCELLED;
        return;
      }
      if (!SNodeUtil.isAccessible(node, myEditor.getEditorContext().getRepository())) {
        // asking runLoPrioRead() implementation to re-execute this task later:
        // editor was not updated in accordance with last modelReload event yet.
        myUpdateResult = null;
        return;
      }

      if (isCancelRequested()) {
        confirmCancel();
        myUpdateResult = UpdateResult.CANCELLED;
        return;
      }

      myUpdateResult = myChecker.withChecker(this::perform, UpdateResult.CANCELLED);
    }

    UpdateResult perform(EditorChecker checker1) {
      try {
        HighlighterUpdateSessionCancellable cancel = new HighlighterUpdateSessionCancellable(this, checker1.toString(), myEditor);
        return checker1.update(myEditor, myWasCheckedOnce, myWasLastChecked, myApplyQuickFixes, cancel);
      } catch (IndexNotReadyException ex) {
        myEditor.getHighlightManager().clearForOwner(checker1.getEditorMessageOwner(), true);
        throw ex;
      }
    }

    @Override
    public boolean isCancelled() {
      if (myHighlighter.isPausedOrStopping()) {
        HighlighterUpdateSessionCancellable.debugReason("highlighter is paused");
        return true;
      }
      if (isCancelRequested()) {
        HighlighterUpdateSessionCancellable.debugReason("ModelAccess requested cancellation");
        return true;
      }
      return false;
    }
  }
}
