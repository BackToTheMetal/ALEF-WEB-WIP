package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javaemul.internal.concurrent.MainThreadTimers;

/** Scheduled tasks use browser main-thread timers; direct submissions remain inline. */
public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService {
  private final List<ScheduledTask<?>> scheduled = new ArrayList<>();
  private final TimerQueue timerQueue;
  private boolean continuePeriodic;
  private boolean executeDelayed = true;
  private boolean removeOnCancel;
  public ScheduledThreadPoolExecutor(int corePoolSize) { this(corePoolSize, Executors.defaultThreadFactory(), new AbortPolicy()); }
  public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory factory) { this(corePoolSize, factory, new AbortPolicy()); }
  public ScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) { this(corePoolSize, Executors.defaultThreadFactory(), handler); }
  public ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory factory, RejectedExecutionHandler handler) {
    this(corePoolSize, factory, handler, new TimerQueue());
  }
  private ScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory factory,
      RejectedExecutionHandler handler, TimerQueue queue) {
    super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS, queue, factory, handler);
    timerQueue = queue;
    queue.owner = this;
  }
  @Override public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) { return schedule(Executors.callable(task), delay, unit); }
  @Override public <V> ScheduledFuture<V> schedule(Callable<V> task, long delay, TimeUnit unit) {
    return enqueue(task, delay, unit, 0);
  }
  @Override public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
    Objects.requireNonNull(unit);
    if (period <= 0) throw new IllegalArgumentException("period must be positive");
    return enqueue(Executors.callable(task), initialDelay, unit, unit.toNanos(period));
  }
  @Override public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
    Objects.requireNonNull(unit);
    if (delay <= 0) throw new IllegalArgumentException("delay must be positive");
    return enqueue(Executors.callable(task), initialDelay, unit, -unit.toNanos(delay));
  }
  private <V> ScheduledFuture<V> enqueue(Callable<V> task, long delay, TimeUnit unit, long period) {
    Objects.requireNonNull(task); Objects.requireNonNull(unit);
    ScheduledTask<V> result = new ScheduledTask<>(task, Math.max(0, unit.toNanos(delay)), period);
    if (isShutdown()) {
      getRejectedExecutionHandler().rejectedExecution(result, this);
      result.cancel(false); return result;
    }
    scheduled.add(result); timerQueue.addInternal(result); result.arm(); return result;
  }

  private void detach(Runnable task) {
    ScheduledTask<?> scheduledTask = (ScheduledTask<?>) task;
    MainThreadTimers.clearTimeout(scheduledTask.timerId);
    scheduled.remove(scheduledTask);
    tryTerminate();
  }

  /** Queue removals also remove the browser timer; additions must use schedule(). */
  private static final class TimerQueue extends LinkedBlockingQueue<Runnable> {
    private ScheduledThreadPoolExecutor owner;

    void addInternal(Runnable task) { super.offer(task); }
    boolean removeInternal(Object task) { return super.remove(task); }

    @Override public boolean offer(Runnable task) {
      Objects.requireNonNull(task);
      throw new UnsupportedOperationException("Add scheduled work through the scheduling methods");
    }
    @Override public boolean remove(Object task) {
      Iterator<Runnable> iterator = super.iterator();
      while (iterator.hasNext()) {
        Runnable candidate = iterator.next();
        if (Objects.equals(task, candidate) && super.remove(candidate)) {
          owner.detach(candidate);
          return true;
        }
      }
      return false;
    }
    @Override public Runnable peek() {
      RunnableScheduledFuture<?> first = null;
      Iterator<Runnable> iterator = super.iterator();
      while (iterator.hasNext()) {
        RunnableScheduledFuture<?> candidate = (RunnableScheduledFuture<?>) iterator.next();
        if (first == null || candidate.compareTo(first) < 0) first = candidate;
      }
      return first;
    }
    @Override public Runnable poll() {
      RunnableScheduledFuture<?> first = (RunnableScheduledFuture<?>) peek();
      if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) return null;
      remove(first);
      return first;
    }
    @Override public Iterator<Runnable> iterator() {
      Iterator<Runnable> iterator = super.iterator();
      return new Iterator<Runnable>() {
        private Runnable current;
        private boolean removable;
        @Override public boolean hasNext() { return iterator.hasNext(); }
        @Override public Runnable next() {
          current = iterator.next(); removable = true; return current;
        }
        @Override public void remove() {
          if (!removable) throw new IllegalStateException();
          removable = false;
          TimerQueue.this.remove(current);
        }
      };
    }
    @Override public void clear() {
      Iterator<Runnable> iterator = iterator();
      while (iterator.hasNext()) { iterator.next(); iterator.remove(); }
    }
    @Override public int drainTo(Collection<? super Runnable> target) {
      return drainTo(target, Integer.MAX_VALUE);
    }
    @Override public int drainTo(Collection<? super Runnable> target, int limit) {
      Objects.requireNonNull(target);
      if (target == this) throw new IllegalArgumentException("Cannot drain queue to itself");
      int transferred = 0;
      while (transferred < limit) {
        RunnableScheduledFuture<?> task = (RunnableScheduledFuture<?>) peek();
        if (task == null || task.getDelay(TimeUnit.NANOSECONDS) > 0) break;
        target.add(task);
        remove(task);
        transferred++;
      }
      return transferred;
    }
  }
  private static long addDelay(long start, long delay) {
    return start > Long.MAX_VALUE - delay ? Long.MAX_VALUE : start + delay;
  }
  private final class ScheduledTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
    private long deadline;
    private final long period;
    private double timerId;
    ScheduledTask(Callable<V> task, long delay, long period) {
      super(task); deadline = addDelay(System.nanoTime(), delay); this.period = period;
    }
    void arm() {
      long remaining = Math.max(0, deadline - System.nanoTime());
      double millis = Math.min(2147483647d, Math.ceil(remaining / 1000000d));
      timerId = MainThreadTimers.setTimeout(this::fire, millis);
    }
    void fire() {
      if (!timerQueue.contains(this)) return;
      if (getDelay(TimeUnit.NANOSECONDS) > 0) { arm(); return; }
      if (isDone()) { timerQueue.remove(this); return; }
      timerQueue.removeInternal(this);
      try { executeTask(this); }
      finally {
        // A throwing executor hook must not leave an unreachable pending timer.
        if (!isDone() && !timerQueue.contains(this)) cancel(false);
      }
    }
    @Override public void run() {
      if (isDone()) { timerQueue.remove(this); return; }
      timerQueue.removeInternal(this);
      MainThreadTimers.clearTimeout(timerId);
      if (isShutdown() && (isPeriodic() ? !continuePeriodic : !executeDelayed)) { cancel(false); return; }
      if (!isPeriodic()) { super.run(); return; }
      if (super.runAndReset()) {
        if (isShutdown() && !continuePeriodic) { cancel(false); return; }
        deadline = period > 0 ? addDelay(deadline, period) : addDelay(System.nanoTime(), -period);
        if (!scheduled.contains(this)) scheduled.add(this);
        timerQueue.addInternal(this);
        arm();
      }
    }
    @Override protected void done() {
      if (isCancelled() && !removeOnCancel && !isShutdown() && timerQueue.contains(this)) return;
      timerQueue.removeInternal(this);
      detach(this);
    }
    @Override public boolean isPeriodic() { return period != 0; }
    @Override public long getDelay(TimeUnit unit) { return Objects.requireNonNull(unit).convert(deadline - System.nanoTime(), TimeUnit.NANOSECONDS); }
    @Override public int compareTo(Delayed other) {
      if (other == this) return 0;
      long a = getDelay(TimeUnit.NANOSECONDS), b = other.getDelay(TimeUnit.NANOSECONDS);
      return Long.compare(a, b);
    }
  }
  @Override protected boolean hasPendingTasks() { return !scheduled.isEmpty(); }
  @Override public void shutdown() {
    super.shutdown(); cancelDisallowed(); tryTerminate();
  }
  @Override public List<Runnable> shutdownNow() {
    List<Runnable> pending = new ArrayList<>(timerQueue);
    super.shutdown();
    for (ScheduledTask<?> task : new ArrayList<>(scheduled)) {
      task.cancel(false);
      timerQueue.removeInternal(task);
      detach(task);
    }
    tryTerminate(); return pending;
  }
  private void cancelDisallowed() {
    if (!isShutdown()) return;
    for (ScheduledTask<?> task : new ArrayList<>(scheduled)) {
      if (task.isCancelled() || (task.isPeriodic() ? !continuePeriodic : !executeDelayed)) {
        task.cancel(false);
        timerQueue.removeInternal(task);
        detach(task);
      }
    }
  }
  public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value) { continuePeriodic = value; cancelDisallowed(); }
  public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() { return continuePeriodic; }
  public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) { executeDelayed = value; cancelDisallowed(); }
  public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() { return executeDelayed; }
  public void setRemoveOnCancelPolicy(boolean value) { removeOnCancel = value; }
  public boolean getRemoveOnCancelPolicy() { return removeOnCancel; }
}
