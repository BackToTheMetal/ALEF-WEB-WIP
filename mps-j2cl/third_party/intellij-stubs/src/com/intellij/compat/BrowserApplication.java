package com.intellij.compat;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Disposer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.SwingUtilities;
public final class BrowserApplication extends BrowserComponentManager implements Application {
  private final List<ApplicationListener> listeners = new ArrayList<>();
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private final boolean headless;
  private final boolean testing;
  private int readDepth;
  private int writeDepth;
  public BrowserApplication() { this(false, false); }
  public BrowserApplication(boolean headless, boolean testing) {
    this.headless = headless; this.testing = testing;
    registerService(com.intellij.diagnostic.StartUpPerformanceService.class, new com.intellij.diagnostic.StartUpPerformanceService());
  }
  @Override public void runReadAction(Runnable action) { runReadAction(() -> { action.run(); return null; }); }
  @Override public <T> T runReadAction(Computable<T> action) {
    checkLive(); readDepth++;
    try { return action.compute(); } finally { readDepth--; }
  }
  @Override public void runWriteAction(Runnable action) { runWriteAction(() -> { action.run(); return null; }); }
  @Override public <T> T runWriteAction(Computable<T> action) {
    checkLive();
    for (ApplicationListener listener : new ArrayList<>(listeners)) listener.beforeWriteActionStart(action);
    writeDepth++;
    try {
      for (ApplicationListener listener : new ArrayList<>(listeners)) listener.writeActionStarted(action);
      return action.compute();
    } finally {
      try { for (ApplicationListener listener : new ArrayList<>(listeners)) listener.writeActionFinished(action); }
      finally {
        writeDepth--;
        for (ApplicationListener listener : new ArrayList<>(listeners)) listener.afterWriteActionFinished(action);
      }
    }
  }
  @Override public boolean isReadAccessAllowed() { return readDepth > 0 || writeDepth > 0; }
  @Override public boolean isWriteAccessAllowed() { return writeDepth > 0; }
  @Override public boolean isDispatchThread() { return SwingUtilities.isEventDispatchThread(); }
  @Override public void assertReadAccessAllowed() { if (!isReadAccessAllowed()) throw new IllegalStateException("Read action required"); }
  @Override public void assertWriteAccessAllowed() { if (!isWriteAccessAllowed()) throw new IllegalStateException("Write action required"); }
  @Override public void assertIsDispatchThread() { if (!isDispatchThread()) throw new IllegalStateException("Event dispatch thread required"); }
  @Override public void assertIsNonDispatchThread() { if (isDispatchThread()) throw new IllegalStateException("Event dispatch thread forbidden"); }
  @Override public void invokeLater(Runnable action) { invokeLater(action, ModalityState.any(), ignored -> false); }
  @Override public void invokeLater(Runnable action, Condition<?> expired) { invokeLater(action, ModalityState.any(), expired); }
  @Override public void invokeLater(Runnable action, ModalityState state) { invokeLater(action); }
  @Override public void invokeLater(Runnable action, ModalityState state, Condition<?> expired) {
    checkLive(); SwingUtilities.invokeLater(() -> { if (!isDisposed() && !expired.value(null)) action.run(); });
  }
  @Override public void invokeAndWait(Runnable action) {
    if (!isDispatchThread()) throw new UnsupportedOperationException("Blocking a browser worker for EDT dispatch is unavailable");
    action.run();
  }
  @Override public Future<?> executeOnPooledThread(Runnable action) { checkLive(); return executor.schedule(action, 0, java.util.concurrent.TimeUnit.MILLISECONDS); }
  @Override public <T> Future<T> executeOnPooledThread(Callable<T> action) { checkLive(); return executor.schedule(action, 0, java.util.concurrent.TimeUnit.MILLISECONDS); }
  @Override public void addApplicationListener(ApplicationListener listener, Disposable parent) {
    addApplicationListener(listener); Disposer.register(parent, () -> removeApplicationListener(listener));
  }
  @Override public void addApplicationListener(ApplicationListener listener) { checkLive(); listeners.add(listener); }
  @Override public void removeApplicationListener(ApplicationListener listener) { listeners.remove(listener); }
  @Override public boolean isUnitTestMode() { return testing; }
  @Override public boolean isHeadlessEnvironment() { return headless; }
  @Override public void dispose() { executor.shutdownNow(); listeners.clear(); super.dispose(); }
}
