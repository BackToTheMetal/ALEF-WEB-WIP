package com.intellij.util.concurrency;
import com.intellij.compat.BrowserEdtExecutor;
import java.util.concurrent.ScheduledExecutorService;
public final class EdtExecutorService {
  private static final BrowserEdtExecutor EXECUTOR = new BrowserEdtExecutor();
  private EdtExecutorService() {}
  public static ScheduledExecutorService getScheduledExecutorInstance() { return EXECUTOR; }
  public static java.util.concurrent.ExecutorService getInstance() { return EXECUTOR; }
}
