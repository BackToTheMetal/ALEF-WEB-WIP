package com.intellij.util;

@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable> {
  void run() throws E;
}
