package com.intellij.ide.ui;
public interface UISettingsListener {
  com.intellij.util.messages.Topic<UISettingsListener> TOPIC = com.intellij.util.messages.Topic.create("UI settings", UISettingsListener.class);
  void uiSettingsChanged(UISettings settings);
}
