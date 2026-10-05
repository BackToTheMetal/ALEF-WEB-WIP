package com.intellij.execution.process;
public final class ProcessIOExecutorService {
  public static final java.util.concurrent.Executor INSTANCE = action -> com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(action);
  private ProcessIOExecutorService() {}
}
