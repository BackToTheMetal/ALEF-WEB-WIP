package java.util.concurrent;

import java.util.Objects;

/** A task whose computation runs at most once, on the caller's main thread. */
public class FutureTask<V> implements RunnableFuture<V> {
  private Callable<V> callable;
  private V value;
  private Throwable failure;
  private boolean running;
  private boolean done;
  private boolean cancelled;

  public FutureTask(Callable<V> callable) { this.callable = Objects.requireNonNull(callable); }
  public FutureTask(Runnable runnable, V result) { this(Executors.callable(runnable, result)); }

  @Override public void run() {
    if (done || running) return;
    running = true;
    try {
      V result;
      try { result = callable.call(); }
      catch (Throwable ex) { setException(ex); return; }
      set(result);
    }
    finally { running = false; callable = null; }
  }

  protected boolean runAndReset() {
    if (done || running) return false;
    running = true;
    try { callable.call(); return !done; }
    catch (Throwable ex) { setException(ex); return false; }
    finally { running = false; }
  }

  protected void set(V result) {
    if (done) return;
    value = result; done = true; callable = null; done();
  }
  protected void setException(Throwable ex) {
    if (done) return;
    failure = ex; done = true; callable = null; done();
  }
  protected void done() {}
  @Override public boolean cancel(boolean mayInterruptIfRunning) {
    if (done) return false;
    cancelled = true; done = true;
    if (!running) callable = null;
    done(); return true;
  }
  @Override public boolean isCancelled() { return cancelled; }
  @Override public boolean isDone() { return done; }
  @Override public V get() throws InterruptedException, ExecutionException {
    if (!done) throw new IllegalStateException("Future is pending; the browser main thread cannot block");
    return report();
  }
  @Override public V get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    Objects.requireNonNull(unit);
    if (!done) throw new TimeoutException("Future is pending on the browser main thread");
    return report();
  }
  private V report() throws ExecutionException {
    if (cancelled) throw new CancellationException();
    if (failure != null) throw new ExecutionException(failure);
    return value;
  }
}
