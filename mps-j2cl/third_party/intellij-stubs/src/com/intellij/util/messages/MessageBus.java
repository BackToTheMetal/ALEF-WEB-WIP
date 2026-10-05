package com.intellij.util.messages;

import com.intellij.openapi.Disposable;

public interface MessageBus extends Disposable {
  MessageBus getParent();
  MessageBusConnection connect();
  MessageBusConnection connect(Disposable parent);
  SimpleMessageBusConnection simpleConnect();
  <L> L syncPublisher(Topic<L> topic);
  default <L> L syncAndPreloadPublisher(Topic<L> topic) { return syncPublisher(topic); }
  boolean isDisposed();
  boolean hasUndeliveredEvents(Topic<?> topic);
}
