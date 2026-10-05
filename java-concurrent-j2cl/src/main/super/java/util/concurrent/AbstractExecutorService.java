package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** Shared inline submission and bulk-execution implementation. */
public abstract class AbstractExecutorService implements ExecutorService {
  protected <T> RunnableFuture<T> newTaskFor(Runnable task, T value) {
    return new FutureTask<>(task, value);
  }
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> task) { return new FutureTask<>(task); }
  @Override public Future<?> submit(Runnable task) { return submit(task, null); }
  @Override public <T> Future<T> submit(Runnable task, T result) {
    RunnableFuture<T> future = newTaskFor(task, result); execute(future); return future;
  }
  @Override public <T> Future<T> submit(Callable<T> task) {
    RunnableFuture<T> future = newTaskFor(task); execute(future); return future;
  }
  @Override public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
      throws InterruptedException {
    List<Future<T>> futures = prepare(tasks);
    boolean success = false;
    try {
      for (Future<T> future : futures) execute((Runnable) future);
      for (Future<T> future : futures) {
        if (!future.isDone()) {
          try { future.get(); }
          catch (ExecutionException | CancellationException ignored) { /* Results retain failures. */ }
        }
      }
      success = true;
      return futures;
    } finally {
      if (!success) for (Future<T> future : futures) if (!future.isDone()) future.cancel(false);
    }
  }
  @Override public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
      long timeout, TimeUnit unit) throws InterruptedException {
    long budget = Objects.requireNonNull(unit).toNanos(timeout);
    long start = System.nanoTime();
    List<Future<T>> futures = prepare(tasks);
    try {
      for (Future<T> future : futures) {
        if (budget <= 0 || System.nanoTime() - start >= budget) break;
        execute((Runnable) future);
      }
      return futures;
    } finally {
      for (Future<T> future : futures) if (!future.isDone()) future.cancel(false);
    }
  }
  private <T> List<Future<T>> prepare(Collection<? extends Callable<T>> tasks) {
    Objects.requireNonNull(tasks);
    List<Future<T>> futures = new ArrayList<>();
    for (Callable<T> task : tasks) futures.add(newTaskFor(Objects.requireNonNull(task)));
    return futures;
  }
  @Override public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException {
    try { return invokeAnyInternal(tasks, false, 0); }
    catch (TimeoutException impossible) { throw new AssertionError(impossible); }
  }
  @Override public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout,
      TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return invokeAnyInternal(tasks, true, Objects.requireNonNull(unit).toNanos(timeout));
  }
  private <T> T invokeAnyInternal(Collection<? extends Callable<T>> tasks, boolean timed,
      long budget) throws InterruptedException, ExecutionException, TimeoutException {
    Objects.requireNonNull(tasks);
    if (tasks.isEmpty()) throw new IllegalArgumentException("tasks must not be empty");
    for (Callable<T> task : tasks) Objects.requireNonNull(task);
    long start = System.nanoTime();
    ExecutionException last = null;
    for (Callable<T> task : tasks) {
      if (timed && (budget <= 0 || System.nanoTime() - start >= budget)) throw new TimeoutException();
      Future<T> future = submit(task);
      try { return future.get(); }
      catch (ExecutionException failure) { last = failure; }
      finally { if (!future.isDone()) future.cancel(false); }
    }
    throw last;
  }
}
