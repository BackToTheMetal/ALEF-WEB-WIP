package com.intellij.openapi.application;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
public interface Application extends ComponentManager {
  void runReadAction(Runnable action);
  <T> T runReadAction(Computable<T> action);
  void runWriteAction(Runnable action);
  <T> T runWriteAction(Computable<T> action);
  boolean isReadAccessAllowed();
  boolean isWriteAccessAllowed();
  boolean isDispatchThread();
  void assertReadAccessAllowed();
  void assertWriteAccessAllowed();
  void assertIsDispatchThread();
  void assertIsNonDispatchThread();
  void invokeLater(Runnable action);
  void invokeLater(Runnable action, Condition<?> expired);
  void invokeLater(Runnable action, ModalityState modality);
  void invokeLater(Runnable action, ModalityState modality, Condition<?> expired);
  void invokeAndWait(Runnable action);
  default void invokeAndWait(Runnable action, ModalityState modality) { invokeAndWait(action); }
  Future<?> executeOnPooledThread(Runnable action);
  <T> Future<T> executeOnPooledThread(Callable<T> action);
  void addApplicationListener(ApplicationListener listener, Disposable parent);
  void addApplicationListener(ApplicationListener listener);
  void removeApplicationListener(ApplicationListener listener);
  boolean isUnitTestMode();
  boolean isHeadlessEnvironment();
  default boolean isInternal() { return false; }
  default boolean isEAP() { return false; }
  default boolean isCommandLine() { return false; }
  default boolean isActive() { return !isDisposed(); }
  default boolean holdsReadLock() { return isReadAccessAllowed(); }
  default boolean isWriteIntentLockAcquired() { return isDispatchThread(); }
  default void assertWriteIntentLockAcquired() { assertIsDispatchThread(); }
  default void assertReadAccessNotAllowed() { if (isReadAccessAllowed()) throw new IllegalStateException("Read action active"); }
  default ModalityState getCurrentModalityState() { return ModalityState.current(); }
  default ModalityState getDefaultModalityState() { return ModalityState.defaultModalityState(); }
  default ModalityState getNoneModalityState() { return ModalityState.nonModal(); }
  default ModalityState getAnyModalityState() { return ModalityState.any(); }
  default void saveAll() { throw new UnsupportedOperationException("Register browser persistence explicitly"); }
  default void saveSettings() { throw new UnsupportedOperationException("Register browser settings persistence explicitly"); }
  default void exit() { throw new UnsupportedOperationException("Browser application lifecycle is owned by the host"); }
  default boolean isRestartCapable() { return false; }
  default void restart() { throw new UnsupportedOperationException("Cannot restart a host JVM in the browser"); }
}
