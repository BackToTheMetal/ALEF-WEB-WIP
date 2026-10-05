package com.intellij.openapi.util;

@FunctionalInterface
public interface ThrowableComputable<T, E extends Throwable> {
  T compute() throws E;
}
