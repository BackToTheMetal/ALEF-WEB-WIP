package com.intellij.openapi.ui.popup;
public interface SpeedSearchFilter<T> {
  boolean canBeHidden(T value);
  String getIndexedString(T value);
}
