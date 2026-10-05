package java.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Source-compatible pool facade: every accepted task executes inline. */
public class ThreadPoolExecutor extends AbstractExecutorService {
  private int corePoolSize;
  private int maximumPoolSize;
  private long keepAliveNanos;
  private final BlockingQueue<Runnable> queue;
  private ThreadFactory threadFactory;
  private RejectedExecutionHandler handler;
  private boolean shutdown;
  private boolean terminationNotified;
  private boolean coreTimeout;
  private int active;
  private long completed;
  private long submitted;

  public ThreadPoolExecutor(int core, int maximum, long keepAlive, TimeUnit unit,
      BlockingQueue<Runnable> queue) {
    this(core, maximum, keepAlive, unit, queue, Executors.defaultThreadFactory(), new AbortPolicy());
  }
  public ThreadPoolExecutor(int core, int maximum, long keepAlive, TimeUnit unit,
      BlockingQueue<Runnable> queue, ThreadFactory factory) {
    this(core, maximum, keepAlive, unit, queue, factory, new AbortPolicy());
  }
  public ThreadPoolExecutor(int core, int maximum, long keepAlive, TimeUnit unit,
      BlockingQueue<Runnable> queue, RejectedExecutionHandler handler) {
    this(core, maximum, keepAlive, unit, queue, Executors.defaultThreadFactory(), handler);
  }
  public ThreadPoolExecutor(int core, int maximum, long keepAlive, TimeUnit unit,
      BlockingQueue<Runnable> queue, ThreadFactory factory, RejectedExecutionHandler handler) {
    if (core < 0 || maximum <= 0 || maximum < core || keepAlive < 0) throw new IllegalArgumentException();
    corePoolSize = core; maximumPoolSize = maximum;
    keepAliveNanos = Objects.requireNonNull(unit).toNanos(keepAlive);
    this.queue = Objects.requireNonNull(queue);
    threadFactory = Objects.requireNonNull(factory); this.handler = Objects.requireNonNull(handler);
  }
  @Override public void execute(Runnable task) {
    Objects.requireNonNull(task);
    if (shutdown) { handler.rejectedExecution(task, this); return; }
    executeTask(task);
  }
  protected final void executeTask(Runnable task) {
    submitted++; active++;
    try {
      beforeExecute(Thread.currentThread(), task);
      Throwable failure = null;
      try { task.run(); }
      catch (RuntimeException | Error ex) { failure = ex; throw ex; }
      finally { afterExecute(task, failure); }
    } finally {
      active--; completed++;
      tryTerminate();
    }
  }
  @Override public void shutdown() {
    shutdown = true; tryTerminate();
  }
  @Override public List<Runnable> shutdownNow() {
    shutdown = true;
    List<Runnable> pending = new ArrayList<>();
    queue.drainTo(pending);
    // Delay-based queues may refuse to poll unexpired elements. They are still
    // pending work and must be removed and returned before termination.
    for (Runnable task : new ArrayList<>(queue)) {
      if (queue.remove(task)) pending.add(task);
    }
    tryTerminate();
    return pending;
  }
  @Override public boolean isShutdown() { return shutdown; }
  @Override public boolean isTerminated() { return shutdown && active == 0 && !hasPendingTasks(); }
  public boolean isTerminating() { return shutdown && !isTerminated(); }
  protected boolean hasPendingTasks() { return !queue.isEmpty(); }
  protected final void tryTerminate() {
    if (isTerminated() && !terminationNotified) { terminationNotified = true; terminated(); }
  }
  @Override public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    Objects.requireNonNull(unit); return isTerminated();
  }
  protected void beforeExecute(Thread thread, Runnable task) {}
  protected void afterExecute(Runnable task, Throwable failure) {}
  protected void terminated() {}
  public void setThreadFactory(ThreadFactory factory) { threadFactory = Objects.requireNonNull(factory); }
  public ThreadFactory getThreadFactory() { return threadFactory; }
  public void setRejectedExecutionHandler(RejectedExecutionHandler handler) { this.handler = Objects.requireNonNull(handler); }
  public RejectedExecutionHandler getRejectedExecutionHandler() { return handler; }
  public void setCorePoolSize(int size) {
    if (size < 0 || size > maximumPoolSize) throw new IllegalArgumentException(); corePoolSize = size;
  }
  public int getCorePoolSize() { return corePoolSize; }
  public void setMaximumPoolSize(int size) {
    if (size <= 0 || size < corePoolSize) throw new IllegalArgumentException(); maximumPoolSize = size;
  }
  public int getMaximumPoolSize() { return maximumPoolSize; }
  public void setKeepAliveTime(long time, TimeUnit unit) {
    if (time < 0 || (time == 0 && coreTimeout)) throw new IllegalArgumentException();
    keepAliveNanos = Objects.requireNonNull(unit).toNanos(time);
  }
  public long getKeepAliveTime(TimeUnit unit) { return Objects.requireNonNull(unit).convert(keepAliveNanos, TimeUnit.NANOSECONDS); }
  public void allowCoreThreadTimeOut(boolean value) {
    if (value && keepAliveNanos == 0) throw new IllegalArgumentException(); coreTimeout = value;
  }
  public boolean allowsCoreThreadTimeOut() { return coreTimeout; }
  public boolean prestartCoreThread() { return false; }
  public int prestartAllCoreThreads() { return 0; }
  public int getPoolSize() { return active == 0 ? 0 : 1; }
  public int getActiveCount() { return active == 0 ? 0 : 1; }
  public int getLargestPoolSize() { return submitted == 0 ? 0 : 1; }
  public long getTaskCount() { return submitted; }
  public long getCompletedTaskCount() { return completed; }
  /** Queue access is for inspection/removal; submit work through execute or submit. */
  public BlockingQueue<Runnable> getQueue() { return queue; }
  public boolean remove(Runnable task) {
    boolean removed = queue.remove(task);
    tryTerminate();
    return removed;
  }
  public void purge() {
    queue.removeIf(task -> task instanceof Future && ((Future<?>) task).isCancelled());
    tryTerminate();
  }
  public static class AbortPolicy implements RejectedExecutionHandler {
    @Override public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
      throw new RejectedExecutionException("Executor is shut down");
    }
  }
  public static class CallerRunsPolicy implements RejectedExecutionHandler {
    @Override public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
      if (!executor.isShutdown()) task.run();
    }
  }
  public static class DiscardPolicy implements RejectedExecutionHandler {
    @Override public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {}
  }
  public static class DiscardOldestPolicy implements RejectedExecutionHandler {
    @Override public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
      if (!executor.isShutdown()) { executor.getQueue().poll(); executor.execute(task); }
    }
  }
}
