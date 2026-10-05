package com.intellij.openapi.ui.popup;
import java.util.List;
import javax.swing.Icon;
public interface ListPopupStep<T> extends PopupStep<T> {
  List<T> getValues();
  String getTextFor(T value);
  boolean isSelectable(T value);
  Icon getIconFor(T value);
  ListSeparator getSeparatorAbove(T value);
  int getDefaultOptionIndex();
  boolean isSpeedSearchEnabled();
  SpeedSearchFilter<T> getSpeedSearchFilter();
  boolean isMnemonicsNavigationEnabled();
  MnemonicNavigationFilter<T> getMnemonicNavigationFilter();
}
