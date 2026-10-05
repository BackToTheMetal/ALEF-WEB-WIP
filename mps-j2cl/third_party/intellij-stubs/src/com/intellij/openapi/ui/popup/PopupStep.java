package com.intellij.openapi.ui.popup;
public interface PopupStep<T> {
  PopupStep<?> FINAL_CHOICE = null;
  String getTitle();
  PopupStep<?> onChosen(T value, boolean finalChoice);
  boolean hasSubstep(T value);
  default void canceled() {}
  default Runnable getFinalRunnable() { return null; }
  default boolean isAutoSelectionEnabled() { return true; }
}
