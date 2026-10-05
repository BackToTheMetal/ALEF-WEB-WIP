package com.intellij.openapi.util;

@FunctionalInterface
public interface Condition<T> {
  boolean value(T value);
}
