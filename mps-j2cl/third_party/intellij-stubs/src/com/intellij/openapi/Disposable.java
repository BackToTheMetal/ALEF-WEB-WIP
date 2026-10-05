package com.intellij.openapi;

/** Browser compatibility contract; implementations release their own resources. */
public interface Disposable {
  void dispose();
}
