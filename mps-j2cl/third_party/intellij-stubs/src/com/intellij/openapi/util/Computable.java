package com.intellij.openapi.util;

@FunctionalInterface
public interface Computable<T> {
  T compute();
}
