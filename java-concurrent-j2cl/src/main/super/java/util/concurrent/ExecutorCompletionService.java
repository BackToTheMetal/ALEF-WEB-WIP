package java.util.concurrent;

import java.util.Objects;

public class ExecutorCompletionService<V> implements CompletionService<V> {
  private final Executor executor;
  private final BlockingQueue<Future<V>> completed;
  public ExecutorCompletionService(Executor executor) { this(executor, new LinkedBlockingQueue<>()); }
  public ExecutorCompletionService(Executor executor, BlockingQueue<Future<V>> queue) {
    this.executor = Objects.requireNonNull(executor); completed = Objects.requireNonNull(queue);
  }
  @Override public Future<V> submit(Callable<V> task) {
    FutureTask<V> future = new FutureTask<V>(task) {
      @Override protected void done() { completed.add(this); }
    };
    executor.execute(future); return future;
  }
  @Override public Future<V> submit(Runnable task, V result) { return submit(Executors.callable(task, result)); }
  @Override public Future<V> take() throws InterruptedException { return completed.take(); }
  @Override public Future<V> poll() { return completed.poll(); }
  @Override public Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException { return completed.poll(timeout, unit); }
}
