package com.intellij.openapi.ui.popup.util;
import com.intellij.openapi.ui.popup.ListPopupStep;
import com.intellij.openapi.ui.popup.ListSeparator;
import com.intellij.openapi.ui.popup.MnemonicNavigationFilter;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.SpeedSearchFilter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
public class BaseListPopupStep<T> implements ListPopupStep<T>, SpeedSearchFilter<T>, MnemonicNavigationFilter<T> {
  private final String title;
  private final List<T> values;
  private int defaultIndex;
  private Runnable finalRunnable;
  public BaseListPopupStep(String title, List<? extends T> values) { this.title = title; this.values = new ArrayList<>(values); }
  public BaseListPopupStep(String title, T[] values) { this(title, java.util.Arrays.asList(values)); }
  @Override public String getTitle() { return title; }
  @Override public List<T> getValues() { return java.util.Collections.unmodifiableList(values); }
  @Override public String getTextFor(T value) { return String.valueOf(value); }
  @Override public boolean isSelectable(T value) { return true; }
  @Override public Icon getIconFor(T value) { return null; }
  @Override public ListSeparator getSeparatorAbove(T value) { return null; }
  @Override public int getDefaultOptionIndex() { return defaultIndex; }
  public void setDefaultOptionIndex(int value) { defaultIndex = value; }
  @Override public boolean isSpeedSearchEnabled() { return true; }
  @Override public SpeedSearchFilter<T> getSpeedSearchFilter() { return this; }
  @Override public boolean canBeHidden(T value) { return true; }
  @Override public String getIndexedString(T value) { return getTextFor(value); }
  @Override public boolean isMnemonicsNavigationEnabled() { return false; }
  @Override public MnemonicNavigationFilter<T> getMnemonicNavigationFilter() { return this; }
  @Override public int getMnemonicPos(T value) { return -1; }
  @Override public boolean hasSubstep(T value) { return false; }
  @Override public PopupStep<?> onChosen(T value, boolean finalChoice) { return FINAL_CHOICE; }
  public PopupStep<?> doFinalStep(Runnable action) { finalRunnable = action; return FINAL_CHOICE; }
  @Override public Runnable getFinalRunnable() { return finalRunnable; }
}
