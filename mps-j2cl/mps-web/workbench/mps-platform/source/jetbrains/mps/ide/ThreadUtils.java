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
package jetbrains.mps.ide;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import jetbrains.mps.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

/**
 * Interface to platform-specific access to Event Dispatch Thread.
 * For IDEA, use <code>ApplicationManager.getApplication()</code>
 */
public class ThreadUtils {

  public static void submitToBGT(@NotNull Runnable code) {
    Application application = ApplicationManager.getApplication();
    if (application == null) throw new IllegalStateException("Initialize the browser application before submitting background work");
    // BrowserApplication schedules deferred work on its owned executor; no JVM process-I/O threads.
    application.executeOnPooledThread(code);
  }

  @Nullable
  public static Exception runInUIThreadAndWait(Runnable r) {
    LogExceptionsRunnable wrap = new LogExceptionsRunnable(Logger.getLogger(ThreadUtils.class), r);
    if (ApplicationManager.getApplication() != null) {
      // Application#invokeAndWait() executes runnable immediately if in EDT thread (well, at least it is stated in javadoc)
      ApplicationManager.getApplication().invokeAndWait(wrap, ModalityState.defaultModalityState());
    } else {
      if (isInEDT()) {
        wrap.run();
      } else {
        try {
          SwingUtilities.invokeAndWait(wrap);
        } catch (InterruptedException | InvocationTargetException e) {
          Logger.getLogger(ThreadUtils.class).error(e.getMessage(), e);
          return e;
        }
      }
    }
    return wrap.getException();
  }

  public static void runInUIThreadNoWait(Runnable r) {
    LogExceptionsRunnable wrap = new LogExceptionsRunnable(Logger.getLogger(ThreadUtils.class), r);
    if (isInEDT()) {
      wrap.run();
    } else {
      if (ApplicationManager.getApplication() != null) {
        ApplicationManager.getApplication().invokeLater(wrap);
      } else {
        SwingUtilities.invokeLater(wrap);
      }
    }
  }

  private static class LogExceptionsRunnable implements Runnable {
    private final Logger myLog;
    private final Runnable myDelegate;
    private Exception myException;

    public LogExceptionsRunnable(Logger log, Runnable delegate) {
      myLog = log;
      myDelegate = delegate;
    }

    @Override
    public void run() {
      try {
        myDelegate.run();
      } catch (Exception e) {
        myLog.error(ThreadUtils.class.getName(), e);
        myException = e;
      }
    }

    @Nullable
    public Exception getException() {
      return myException;
    }
  }

  /**
   * Handy wrap for {@link #runInUIThreadAndWait(Runnable)} and {@link #runInUIThreadNoWait(Runnable)} as a Runnable
   * one could pass to a facility that accepts Runnable
   */
  public static class RunInUIRunnable implements Runnable {
    private final Runnable myDelegate;
    private final boolean myWaitDelegateToComplete;

    public RunInUIRunnable(@NotNull Runnable delegate, boolean wait) {
      myDelegate = delegate;
      myWaitDelegateToComplete = wait;
    }

    @Override
    public void run() {
      if (myWaitDelegateToComplete) {
        runInUIThreadAndWait(myDelegate);
      } else {
        runInUIThreadNoWait(myDelegate);
      }
    }
  }

  public static boolean isInEDT() {
    final Application ideaApp = ApplicationManager.getApplication();
    return ideaApp == null ? SwingUtilities.isEventDispatchThread() : ideaApp.isDispatchThread();
  }

  public static void assertEDT() {
    final Application ideaApp = ApplicationManager.getApplication();
    if (ideaApp != null) {
      ideaApp.assertIsDispatchThread();
    } else {
      if (!SwingUtilities.isEventDispatchThread()) {
        Logger.getLogger(ThreadUtils.class).error("NOT EDT THREAD", new Throwable());
      }
      assert false;
    }
  }
}
