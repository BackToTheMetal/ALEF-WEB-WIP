/*
 * Copyright 2003-2024 JetBrains s.r.o.
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
package jetbrains.mps.smodel;

import com.intellij.openapi.application.ApplicationManager;
import jetbrains.mps.ide.undo.WorkbenchUndoHandler;
import jetbrains.mps.project.MPSProject;
import jetbrains.mps.smodel.undo.DefaultUndoContext;
import jetbrains.mps.smodel.undo.UndoContext;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.annotations.Immutable;
import org.jetbrains.mps.openapi.model.SModel;
import org.jetbrains.mps.openapi.repository.CommandListener;

/**
 * @author Artem Tikhomirov
 * @since 2018.3
 */
public final class ProjectModelAccess2 extends ModelAccessBase implements ModelCommandContext.Provider {
  private final MPSProject myProject;
  private final BrowserModelAccess mySharedImpl;
  private final CommandContextProvider myCommandContextProvider = new CommandContextProvider();
  private final ActionDispatcher<CommandListener> myCommandActionDispatcher;
  private final WorkbenchUndoHandler myUndoHandler;

  public ProjectModelAccess2(MPSProject mpsProject, BrowserModelAccess sharedImpl) {
    super(sharedImpl, false);
    myProject = mpsProject;
    mySharedImpl = sharedImpl;
    myUndoHandler = new WorkbenchUndoHandler();
    myCommandActionDispatcher = new ActionDispatcher<>(new CommandListener() {
      @Override
      public void commandStarted() {
        myCommandContextProvider.engage();
        mySharedImpl.installMCC(ProjectModelAccess2.this);
      }

      @Override
      public void commandFinished() {
        mySharedImpl.uninstallMCC(ProjectModelAccess2.this);
        myCommandContextProvider.discard();
      }
    }, CommandListener::commandStarted, CommandListener::commandFinished);
  }

  public void dispose() { myUndoHandler.dispose(); }

  @Override
  public void executeCommand(Runnable r) {


    if (canRead() && !canWrite()) {
      // This check is to avoid deadlocks like in https://youtrack.jetbrains.com/issue/MPS-31083
      // XXX !canWrite() here is to limit check to outer runReadAction(). However, not sure whether it's ok to start a command when we are inside a
      //     runWriteAction() (i.e. canWrite() == true), perhaps, it's not ok as well.
      throw new IllegalModelAccessException("deadlock prevention: can not elevate model read to a command");
    }

    if (isCommandAction()) {
      // no apparent reason to go a long way and to notify IDEA's CommandProcessor.
      // Besides, and it's IMPORTANT, wrapTopCommandRunnable() and UndoContextSetup expect runnable to be the top command
      // as they use it to configure undo context, which is not the thing we'd like to do for an executeCommand() inside another executeCommand().
      r.run();
      return;
    }

    final UndoRunnable undoDescription = r instanceof UndoRunnable ? (UndoRunnable) r : null;

    Runnable cmd = wrapTopCommandRunnable(r, myProject);
    if (canWrite()) {
      // see https://youtrack.jetbrains.com/issue/MPS-29602, we could be out of a command (isInsideCommand() == false, but still in a write
      // e.g. dispatching post-write action notifications. We shall avoid write action notification dispatch until the previous one is over,
      // we don't expect implementations of WriteActionListener to be re-enterable.
      // Though it's tempting to wrap with platform write here
      //   cmd = myPlatformWriteHelper.withPlatformWrite(cmd)
      // -- doesn't seem to hurt to have and extra platform write (canWrite tells us state of our model RW lock only, not combined with that of IDEA).
      //    myPlatformWriteActionTracker in TryRunPlatformWriteHelper merely counts write attempts, and Application.runWriteAction is
      //    re-enterable and a precondition for a command anyway
      // it seems to be wrong as it may lead to improper lock sequence and deadlock. Usually we grab platform write first, then MPS write. If
      // we happen to get here with MPS write but without IDEA's, we might get into a deadlock, which is far more complicated to discover than
      // a failed assertion.
      ApplicationManager.getApplication().assertWriteAccessAllowed();
      // just go on with cmd as is
      if (myCommandActionDispatcher.isInsideNotificationDispatch()) {
        // ... unless we're inside previous command notification. See MPS-33474 and MPS-33432 for reasons.
        throw new IllegalModelAccessException("Do not start a new command while start/finish notification for another command is in process");
      }
    }
    mySharedImpl.executeCommand_(cmd, myProject.getProject(), undoDescription);
  }

  @Override
  public void executeCommandInEDT(Runnable r) {

    mySharedImpl.runCommandInEDT_(wrapTopCommandRunnable(r, myProject), myProject.getProject(), r instanceof UndoRunnable ? (UndoRunnable) r : null);
  }

  @Override
  public void executeUndoTransparentCommand(Runnable r) {
    if (myCommandActionDispatcher.isInsideAction()) {
      // XXX why not, except for the newly introduced wrapTopCommandRunnable() limitation of nested commands?
      throw new IllegalStateException("undo transparent action cannot be invoked in a command");
    }
    mySharedImpl.runUndoTransparentCommand(wrapTopCommandRunnable(r, myProject));
  }

  @Override
  public boolean isCommandAction() {
    return mySharedImpl.canWrite() && myCommandActionDispatcher.isInsideAction();
  }

  @Override
  public void addCommandListener(CommandListener listener) {
    myCommandActionDispatcher.addActionListener(listener);
  }

  @Override
  public void removeCommandListener(CommandListener listener) {
    myCommandActionDispatcher.removeActionListener(listener);
  }

  @Nullable
  @Override
  public ModelCommandContext getCommandContext(org.jetbrains.mps.openapi.model.SModel model) {
    // isCommandAction might be excessive, just want to make sure there's not access to MCC from a thread other than the command one.
    return isCommandAction() ? myCommandContextProvider.get(model, getUndoHandler(model)) : null;
  }

  @Nullable
  private UndoHandler getUndoHandler(/*NotNull*/ SModel model) {
    return myUndoHandler;
  }

  /**
   * Bears 'TOP' in the name to stress we don't expect nested command here.
   * myCommandActionDispatcher indeed tolerates nested commands, however UndoContextSetup DOES NOT.
   * This might be worth a refactoring, so that even nested commands go through myCommandActionDispatcher, and only for the top one
   * there'd be an extra responsibility to setup undo context.
   */
  private Runnable wrapTopCommandRunnable(Runnable r, MPSProject project) {
    // first, commandStarted notification is dispatched, then undo context set,
    // at the end, undo context is flushed, and then commandFinished() is dispatched.
    // The start sequence used to be other way round, does it matter?
    UndoContext context;
    if (r instanceof UndoContext) {
      context = (UndoContext) r;
    } else {
      context = new DefaultUndoContext(project.getRepository());
    }
    return myCommandActionDispatcher.wrap(new UndoContextSetup(r, context));
  }

  /**
   * Responsible to prepare and cleanup undo context for the command. Has to run prior to any client-supplied command code, only for the very first command.
   * Shall get executed inside platform write and under model write lock.
   * As long as executeCommand() runs a delegate directly in case of a nested command, undo-transparent is explicit
   * about top-level, and async command is always top-level, we don't care about command nesting here.
   */
  @Immutable
  private final class UndoContextSetup implements Runnable {
    private final Runnable myCommand;
    private final UndoContext myUndoContext;

    UndoContextSetup(Runnable r, UndoContext uc){
      myCommand = r;
      myUndoContext = uc;
    }

    @Override
    public void run() {
      checkWriteAccess();
      // XXX pass MPSProject right to undoHandler, don't be shy
      myUndoHandler.startCommand(myUndoContext);
      try {
        myCommand.run();
      } finally {
        checkWriteAccess();
        myUndoHandler.flushCommand();
      }
    }
  }
}
