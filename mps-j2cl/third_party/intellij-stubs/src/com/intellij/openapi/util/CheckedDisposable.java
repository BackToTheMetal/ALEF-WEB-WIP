package com.intellij.openapi.util;

import com.intellij.openapi.Disposable;

public interface CheckedDisposable extends Disposable {
  boolean isDisposed();
}
