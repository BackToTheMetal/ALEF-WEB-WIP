package com.intellij.openapi.components;
public interface BaseComponent {
  default void initComponent() {}
  default void disposeComponent() {}
  default String getComponentName() { return getClass().getName(); }
}
