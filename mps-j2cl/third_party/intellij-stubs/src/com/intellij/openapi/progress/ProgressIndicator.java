package com.intellij.openapi.progress;
import com.intellij.openapi.application.ModalityState;
public interface ProgressIndicator {
  void start(); void stop(); boolean isRunning(); void cancel(); boolean isCanceled();
  void setText(String text); String getText(); void setText2(String text); String getText2();
  double getFraction(); void setFraction(double fraction); void pushState(); void popState();
  boolean isIndeterminate(); void setIndeterminate(boolean indeterminate);
  void checkCanceled();
  default boolean isModal() { return false; }
  default ModalityState getModalityState() { return ModalityState.nonModal(); }
  default void setModalityProgress(ProgressIndicator indicator) {
    if (indicator != null && indicator.isModal()) throw new UnsupportedOperationException("Native modal progress is unavailable");
  }
  default boolean isPopupWasShown() { return false; }
  default boolean isShowing() { return false; }
}
