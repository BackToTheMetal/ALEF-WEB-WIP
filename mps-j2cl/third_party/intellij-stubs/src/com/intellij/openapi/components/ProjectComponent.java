package com.intellij.openapi.components;
public interface ProjectComponent extends BaseComponent {
  default void projectOpened() {}
  default void projectClosed() {}
}
