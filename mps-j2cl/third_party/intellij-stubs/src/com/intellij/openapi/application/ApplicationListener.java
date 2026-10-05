package com.intellij.openapi.application;
public interface ApplicationListener extends java.util.EventListener {
  default boolean canExitApplication() { return true; }
  default boolean canRestartApplication() { return true; }
  default void applicationExiting() {}
  default void beforeWriteActionStart(Object action) {}
  default void writeActionStarted(Object action) {}
  default void writeActionFinished(Object action) {}
  default void afterWriteActionFinished(Object action) {}
}
