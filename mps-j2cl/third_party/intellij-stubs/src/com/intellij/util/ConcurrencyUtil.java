package com.intellij.util;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
public final class ConcurrencyUtil {
  private ConcurrencyUtil() {}
  public static ScheduledExecutorService newSingleScheduledThreadExecutor(String name) { return Executors.newSingleThreadScheduledExecutor(); }
  public static ScheduledExecutorService newSingleScheduledThreadExecutor(String name, int priority) { return newSingleScheduledThreadExecutor(name); }
  public static ExecutorService newSingleThreadExecutor(String name) { return Executors.newSingleThreadExecutor(); }
  public static void runUnderThreadName(String name, Runnable action) { action.run(); }
}
