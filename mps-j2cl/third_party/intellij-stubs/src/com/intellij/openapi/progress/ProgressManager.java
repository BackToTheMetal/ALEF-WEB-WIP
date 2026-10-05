package com.intellij.openapi.progress;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.ThrowableComputable;
public class ProgressManager {
  private static final ProgressManager INSTANCE = new ProgressManager();
  private ProgressIndicator current;
  private int nonCancelable;
  public static ProgressManager getInstance() { return INSTANCE; }
  public static ProgressManager getInstanceOrNull() { return INSTANCE; }
  public ProgressIndicator getProgressIndicator() { return current; }
  public boolean hasProgressIndicator() { return current != null; }
  public boolean hasModalProgressIndicator() { return current != null && current.isModal(); }
  public boolean hasUnsafeProgressIndicator() { return false; }
  public void runProcess(Runnable action, ProgressIndicator indicator) { runProcess(() -> { action.run(); return null; }, indicator); }
  public <T> T runProcess(Computable<T> action, ProgressIndicator indicator) {
    ProgressIndicator previous = current; current = indicator;
    try { checkCanceled(); return action.compute(); } finally { current = previous; }
  }
  public void executeProcessUnderProgress(Runnable action, ProgressIndicator indicator) { runProcess(action, indicator); }
  public void executeNonCancelableSection(Runnable action) {
    nonCancelable++;
    try { action.run(); } finally { nonCancelable--; }
  }
  public <T, E extends Exception> T computeInNonCancelableSection(ThrowableComputable<T, E> action) throws E {
    nonCancelable++;
    try { return action.compute(); } finally { nonCancelable--; }
  }
  public boolean isInNonCancelableSection() { return nonCancelable > 0; }
  public static void checkCanceled() { if (INSTANCE.nonCancelable == 0 && INSTANCE.current != null) INSTANCE.current.checkCanceled(); }
  public static void canceled(ProgressIndicator indicator) { indicator.cancel(); }
  public static void progress(String text) { checkCanceled(); if (INSTANCE.current != null) INSTANCE.current.setText(text); }
  public static void progress2(String text) { checkCanceled(); if (INSTANCE.current != null) INSTANCE.current.setText2(text); }
  public static void progress(String text, String text2) { progress(text); progress2(text2); }
  public boolean runInReadActionWithWriteActionPriority(Runnable action, ProgressIndicator indicator) {
    runProcess(action, indicator); return true;
  }
}
