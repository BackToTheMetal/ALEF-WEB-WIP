package com.intellij.openapi.actionSystem;
@FunctionalInterface
public interface DataContext {
  DataContext EMPTY_CONTEXT = id -> null;
  Object getData(String dataId);
  default <T> T getData(DataKey<T> key) { return key.getData(this); }
}
