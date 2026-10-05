package com.intellij.compat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.SwingUtilities;
/** Timer expiry queues on the actual Swing event queue; cancellation remains effective after posting. */
public final class BrowserEdtExecutor extends AbstractExecutorService implements ScheduledExecutorService {
  private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
  private final List<ScheduledTask<?>> tasks = new ArrayList<>();
  private boolean stopped;
  @Override public void execute(Runnable command) { schedule(command, 0, TimeUnit.MILLISECONDS); }
  @Override public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return schedule(() -> { command.run(); return null; }, delay, unit);
  }
  @Override public <V> ScheduledFuture<V> schedule(Callable<V> command, long delay, TimeUnit unit) {
    if (stopped) throw new java.util.concurrent.RejectedExecutionException();
    ScheduledTask<V> task = new ScheduledTask<>(command);
    tasks.add(task);
    task.timer = timer.schedule(() -> SwingUtilities.invokeLater(() -> {
      try { if (!stopped) task.value.run(); else task.value.cancel(false); }
      finally { tasks.remove(task); }
    }), delay, unit);
    return task;
  }
  @Override public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return periodic(command, initialDelay, period, unit, true);
  }
  @Override public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return periodic(command, initialDelay, delay, unit, false);
  }
  private ScheduledFuture<?> periodic(Runnable command, long initialDelay, long period, TimeUnit unit, boolean fixedRate) {
    if (period <= 0) throw new IllegalArgumentException("Period must be positive");
    if (stopped) throw new java.util.concurrent.RejectedExecutionException();
    // Reschedule after EDT completion: a slow UI cannot accumulate an unbounded timer backlog.
    ScheduledTask<Void> task = new ScheduledTask<>(() -> null);
    tasks.add(task);
    Runnable[] run = new Runnable[1];
    long[] deadline = {System.nanoTime() + unit.toNanos(initialDelay)};
    run[0] = () -> SwingUtilities.invokeLater(() -> {
      if (stopped || task.isCancelled()) return;
      try { command.run(); }
      catch (RuntimeException | Error error) { task.value.fail(error); tasks.remove(task); return; }
      if (!stopped && !task.isCancelled()) {
        long nanos = unit.toNanos(period);
        deadline[0] += nanos;
        long next = fixedRate ? Math.max(0, deadline[0] - System.nanoTime()) : nanos;
        task.timer = timer.schedule(run[0], next, TimeUnit.NANOSECONDS);
      }
    });
    task.timer = timer.schedule(run[0], initialDelay, unit);
    return task;
  }
  @Override public void shutdown() { shutdownNow(); }
  @Override public List<Runnable> shutdownNow() {
    stopped = true;
    for (ScheduledTask<?> task : new ArrayList<>(tasks)) task.cancel(false);
    timer.shutdownNow(); return java.util.Collections.emptyList();
  }
  @Override public boolean isShutdown() { return stopped; }
  @Override public boolean isTerminated() { return stopped && tasks.isEmpty(); }
  @Override public boolean awaitTermination(long timeout, TimeUnit unit) {
    if (!isTerminated()) throw new UnsupportedOperationException("Browser EDT termination cannot block");
    return true;
  }
  private final class ScheduledTask<V> implements ScheduledFuture<V> {
    final CompletionTask<V> value;
    ScheduledFuture<?> timer;
    ScheduledTask(Callable<V> callable) { value = new CompletionTask<>(callable); }
    @Override public boolean cancel(boolean mayInterrupt) { boolean result = value.cancel(false); if (timer != null) timer.cancel(false); tasks.remove(this); return result; }
    @Override public boolean isCancelled() { return value.isCancelled(); }
    @Override public boolean isDone() { return value.isDone(); }
    @Override public V get() throws InterruptedException, ExecutionException { return value.get(); }
    @Override public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException { return value.get(timeout, unit); }
    @Override public long getDelay(TimeUnit unit) { return timer == null ? 0 : timer.getDelay(unit); }
    @Override public int compareTo(Delayed other) { return Long.compare(getDelay(TimeUnit.NANOSECONDS), other.getDelay(TimeUnit.NANOSECONDS)); }
  }
  private static final class CompletionTask<V> extends FutureTask<V> {
    CompletionTask(Callable<V> callable) { super(callable); }
    void fail(Throwable error) { setException(error); }
  }
}
