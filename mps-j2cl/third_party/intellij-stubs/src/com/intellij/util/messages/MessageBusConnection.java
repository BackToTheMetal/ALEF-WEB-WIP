package com.intellij.util.messages;

import com.intellij.openapi.Disposable;

public interface MessageBusConnection extends SimpleMessageBusConnection, Disposable {
  void deliverImmediately();
  @Override default void dispose() { disconnect(); }
}
