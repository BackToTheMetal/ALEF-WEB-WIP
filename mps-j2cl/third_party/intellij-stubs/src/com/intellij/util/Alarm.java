package com.intellij.util;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.concurrency.EdtExecutorService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
public class Alarm implements Disposable {
  public enum ThreadToUse { SWING_THREAD, POOLED_THREAD }
  private final ScheduledExecutorService executor;
  private final boolean owned;
  private final List<Request> requests = new ArrayList<>();
  private boolean disposed;
  public Alarm() { this(ThreadToUse.SWING_THREAD); }
  public Alarm(Disposable parent) { this(ThreadToUse.SWING_THREAD, parent); }
  public Alarm(ThreadToUse thread) {
    owned = thread == ThreadToUse.POOLED_THREAD;
    executor = owned ? Executors.newSingleThreadScheduledExecutor() : EdtExecutorService.getScheduledExecutorInstance();
  }
  public Alarm(ThreadToUse thread, Disposable parent) { this(thread); if (parent != null) Disposer.register(parent, this); }
  public void addRequest(Runnable runnable, int delay) { addRequest(runnable, (long) delay); }
  public void addRequest(Runnable runnable, long delay) {
    if (disposed) throw new IllegalStateException("Alarm disposed");
    Request request = new Request(runnable); requests.add(request);
    request.future = executor.schedule(() -> {
      requests.remove(request);
      if (!disposed && !request.canceled) runnable.run();
    }, delay, TimeUnit.MILLISECONDS);
  }
  public void addRequest(Runnable runnable, int delay, ModalityState state) { addRequest(runnable, delay); }
  public boolean cancelRequest(Runnable runnable) {
    boolean found = false;
    for (Request request : new ArrayList<>(requests)) if (request.runnable == runnable) { request.cancel(); found = true; }
    return found;
  }
  public int cancelAllRequests() { int count = requests.size(); for (Request request : new ArrayList<>(requests)) request.cancel(); return count; }
  public int getActiveRequestCount() { return requests.size(); }
  public boolean isEmpty() { return requests.isEmpty(); }
  public boolean isDisposed() { return disposed; }
  @Override public void dispose() { disposed = true; cancelAllRequests(); if (owned) executor.shutdownNow(); }
  private final class Request {
    final Runnable runnable; ScheduledFuture<?> future; boolean canceled;
    Request(Runnable runnable) { this.runnable = runnable; }
    void cancel() { canceled = true; if (future != null) future.cancel(false); requests.remove(this); }
  }
}
