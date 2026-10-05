// CHECKSTYLE_OFF: Copyrighted to members of JCP JSR-166 Expert Group.
/*
 * This file is a modified version of
 * http://gee.cs.oswego.edu/cgi-bin/viewcvs.cgi/jsr166/src/main/java/util/concurrent/Executors.java?revision=1.90
 * which contained the following notice:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */
// CHECKSTYLE_ON

package java.util.concurrent;

/** Emulation of executors. */
public class Executors {

  public static ExecutorService newSingleThreadExecutor() { return newFixedThreadPool(1); }
  public static ExecutorService newWorkStealingPool() { return new ForkJoinPool(); }
  public static ExecutorService newWorkStealingPool(int parallelism) { return new ForkJoinPool(parallelism); }
  public static ExecutorService newSingleThreadExecutor(ThreadFactory factory) { return newFixedThreadPool(1, factory); }
  public static ExecutorService newFixedThreadPool(int count) { return newFixedThreadPool(count, defaultThreadFactory()); }
  public static ExecutorService newFixedThreadPool(int count, ThreadFactory factory) {
    return new ThreadPoolExecutor(count, count, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), factory);
  }
  public static ExecutorService newCachedThreadPool() { return newCachedThreadPool(defaultThreadFactory()); }
  public static ExecutorService newCachedThreadPool(ThreadFactory factory) {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), factory);
  }
  public static ScheduledExecutorService newSingleThreadScheduledExecutor() { return newScheduledThreadPool(1); }
  public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory factory) { return newScheduledThreadPool(1, factory); }
  public static ScheduledExecutorService newScheduledThreadPool(int count) { return new ScheduledThreadPoolExecutor(count); }
  public static ScheduledExecutorService newScheduledThreadPool(int count, ThreadFactory factory) { return new ScheduledThreadPoolExecutor(count, factory); }
  public static ThreadFactory defaultThreadFactory() { return Thread::new; }
  public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
    return new DelegatingExecutor(java.util.Objects.requireNonNull(executor));
  }
  public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
    return new DelegatingScheduler(java.util.Objects.requireNonNull(executor));
  }

  private static class DelegatingExecutor extends AbstractExecutorService {
    final ExecutorService delegate;
    DelegatingExecutor(ExecutorService delegate) { this.delegate = delegate; }
    public void execute(Runnable task) { delegate.execute(task); }
    public void shutdown() { delegate.shutdown(); }
    public java.util.List<Runnable> shutdownNow() { return delegate.shutdownNow(); }
    public boolean isShutdown() { return delegate.isShutdown(); }
    public boolean isTerminated() { return delegate.isTerminated(); }
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException { return delegate.awaitTermination(timeout, unit); }
  }
  private static final class DelegatingScheduler extends DelegatingExecutor implements ScheduledExecutorService {
    final ScheduledExecutorService scheduler;
    DelegatingScheduler(ScheduledExecutorService scheduler) { super(scheduler); this.scheduler = scheduler; }
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) { return scheduler.schedule(task, delay, unit); }
    public <V> ScheduledFuture<V> schedule(Callable<V> task, long delay, TimeUnit unit) { return scheduler.schedule(task, delay, unit); }
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initial, long period, TimeUnit unit) { return scheduler.scheduleAtFixedRate(task, initial, period, unit); }
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initial, long delay, TimeUnit unit) { return scheduler.scheduleWithFixedDelay(task, initial, delay, unit); }
  }

  public static <T> Callable<T> callable(Runnable task, T result) {
    if (task == null) {
      throw new NullPointerException();
    }
    return new RunnableAdapter<T>(task, result);
  }

  public static Callable<Object> callable(Runnable task) {
    if (task == null) {
      throw new NullPointerException();
    }
    return new RunnableAdapter<Object>(task, null);
  }

  private static final class RunnableAdapter<T> implements Callable<T> {

    final Runnable task;
    final T result;

    RunnableAdapter(Runnable task, T result) {
      this.task = task;
      this.result = result;
    }

    public T call() {
      task.run();
      return result;
    }
  }

  private Executors() {}
}
