package com.intellij.util.messages;

public interface SimpleMessageBusConnection {
  <L> void subscribe(Topic<L> topic, L listener);
  void disconnect();
}
