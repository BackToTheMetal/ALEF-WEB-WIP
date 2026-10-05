package com.intellij.openapi.components;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.extensions.AreaInstance;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.util.messages.MessageBus;
public interface ComponentManager extends UserDataHolder, Disposable, AreaInstance {
  <T> T getComponent(Class<T> type);
  <T> T getService(Class<T> type);
  default <T> T getServiceIfCreated(Class<T> type) { return getComponent(type); }
  boolean hasComponent(Class<?> type);
  MessageBus getMessageBus();
  boolean isDisposed();
  default Condition<?> getDisposed() { return ignored -> isDisposed(); }
  default boolean isInjectionForExtensionSupported() { return false; }
}
