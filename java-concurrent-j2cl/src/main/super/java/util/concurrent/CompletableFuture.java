package java.util.concurrent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** Completion stages executed on the browser main thread, including Async methods. */
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
  private static final Executor DIRECT = Runnable::run;
  private static final ArrayDeque<Runnable> NOTIFICATIONS = new ArrayDeque<>();
  private static boolean notifying;
  private T value;
  private Throwable failure;
  private boolean done;
  private final List<Runnable> listeners = new ArrayList<>();

  private static void dispatch(Runnable listener) {
    NOTIFICATIONS.add(listener);
    if (notifying) return;
    notifying = true;
    try { while (!NOTIFICATIONS.isEmpty()) NOTIFICATIONS.remove().run(); }
    finally { notifying = false; }
  }
  private void observe(Runnable listener) { if (done) listener.run(); else listeners.add(listener); }

  /** Owns input registrations only until a race has a winner or is completed externally. */
  private static final class CompletionRace {
    private final List<RaceRegistration> registrations = new ArrayList<>();
    private boolean closed;

    void observe(CompletableFuture<?> source, Runnable action) {
      if (closed) return;
      RaceRegistration registration = new RaceRegistration(source, action);
      registrations.add(registration);
      source.observe(registration);
    }

    void close() {
      if (closed) return;
      closed = true;
      for (RaceRegistration registration : registrations) registration.dispose();
      registrations.clear();
    }
  }

  private static final class RaceRegistration implements Runnable {
    private CompletableFuture<?> source;
    private Runnable action;

    RaceRegistration(CompletableFuture<?> source, Runnable action) {
      this.source = source;
      this.action = action;
    }

    void dispose() {
      if (source != null) source.listeners.remove(this);
      source = null;
      action = null;
    }

    @Override public void run() {
      Runnable pending = action;
      dispose();
      // A losing notification may already be queued when the winner disposes it.
      if (pending != null) pending.run();
    }
  }
  private void helpComplete() {
    // Nested callbacks may synchronously finish another source. Its notifications
    // are already runnable, so process them before deciding that a get would block.
    while (!done && !NOTIFICATIONS.isEmpty()) NOTIFICATIONS.remove().run();
  }
  private boolean finish(T result, Throwable error) {
    if (done) return false;
    value = result; failure = error; done = true;
    List<Runnable> pending = new ArrayList<>(listeners); listeners.clear();
    // Enqueue the whole batch before draining to avoid recursive completion chains.
    dispatch(() -> { for (Runnable listener : pending) dispatch(listener); });
    return true;
  }
  public boolean complete(T value) { return finish(value, null); }
  public boolean completeExceptionally(Throwable failure) { return finish(null, Objects.requireNonNull(failure)); }
  @Override public boolean cancel(boolean mayInterruptIfRunning) {
    return completeExceptionally(new CancellationException()) || isCancelled();
  }
  @Override public boolean isCancelled() { return failure instanceof CancellationException; }
  @Override public boolean isDone() { return done; }
  public boolean isCompletedExceptionally() { return failure != null; }
  private static Throwable unwrapped(Throwable failure) {
    return failure instanceof CompletionException && failure.getCause() != null ? failure.getCause() : failure;
  }
  private static CompletionException completion(Throwable failure) {
    return failure instanceof CompletionException ? (CompletionException) failure : new CompletionException(failure);
  }
  private void relayFailure(Throwable failure) { completeExceptionally(completion(failure)); }
  @Override public T get() throws InterruptedException, ExecutionException {
    helpComplete();
    if (!done) throw new IllegalStateException("Future is pending; use a completion stage on the browser main thread");
    return getResult();
  }
  private T getResult() throws ExecutionException {
    if (isCancelled()) throw (CancellationException) failure;
    if (failure != null) throw new ExecutionException(unwrapped(failure));
    return value;
  }
  @Override public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    Objects.requireNonNull(unit);
    helpComplete();
    if (!done) throw new TimeoutException("Future is pending on the browser main thread");
    return getResult();
  }
  public T join() {
    helpComplete();
    if (!done) throw new IllegalStateException("Future is pending; use a completion stage on the browser main thread");
    if (isCancelled()) throw (CancellationException) failure;
    if (failure != null) throw completion(failure);
    return value;
  }
  public T getNow(T valueIfAbsent) { return done ? join() : valueIfAbsent; }
  public void obtrudeValue(T value) { this.value = value; failure = null; if (!done) finish(value, null); }
  public void obtrudeException(Throwable failure) {
    Objects.requireNonNull(failure); this.failure = failure; value = null; if (!done) finish(null, failure);
  }
  public int getNumberOfDependents() { return listeners.size(); }
  @Override public CompletableFuture<T> toCompletableFuture() { return this; }
  public Executor defaultExecutor() { return DIRECT; }
  public <U> CompletableFuture<U> newIncompleteFuture() { return new CompletableFuture<>(); }
  public CompletableFuture<T> copy() { return thenApply(Function.identity()); }
  public CompletionStage<T> minimalCompletionStage() { return new MinimalCompletionStage<>(copy()); }

  private static <U> void execute(Executor executor, CompletableFuture<U> result, Supplier<? extends U> action) {
    try {
      executor.execute(() -> {
        if (result.isDone()) return;
        try { result.complete(action.get()); }
        catch (Throwable failure) { result.relayFailure(failure); }
      });
    } catch (Throwable failure) { result.relayFailure(failure); }
  }
  public static <U> CompletableFuture<U> completedFuture(U value) {
    CompletableFuture<U> result = new CompletableFuture<>(); result.complete(value); return result;
  }
  public static <U> CompletableFuture<U> failedFuture(Throwable failure) {
    CompletableFuture<U> result = new CompletableFuture<>(); result.completeExceptionally(failure); return result;
  }
  public static <U> CompletionStage<U> completedStage(U value) { return new MinimalCompletionStage<>(completedFuture(value)); }
  public static <U> CompletionStage<U> failedStage(Throwable failure) { return new MinimalCompletionStage<>(failedFuture(failure)); }
  public static CompletableFuture<Void> runAsync(Runnable action) { return runAsync(action, DIRECT); }
  public static CompletableFuture<Void> runAsync(Runnable action, Executor executor) {
    Objects.requireNonNull(action); return supplyAsync(() -> { action.run(); return null; }, executor);
  }
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> action) { return supplyAsync(action, DIRECT); }
  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> action, Executor executor) {
    Objects.requireNonNull(action); Objects.requireNonNull(executor);
    CompletableFuture<U> result = new CompletableFuture<>();
    executeSubmission(executor, result, action); return result;
  }
  private static <U> void executeSubmission(Executor executor, CompletableFuture<U> result, Supplier<? extends U> action) {
    // Submission itself rejects synchronously; exceptions from the body belong to the future.
    executor.execute(() -> {
      if (result.isDone()) return;
      try { result.complete(action.get()); }
      catch (Throwable failure) { result.relayFailure(failure); }
    });
  }

  @Override public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) { return thenApplyAsync(fn, DIRECT); }
  @Override public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) { return thenApplyAsync(fn, defaultExecutor()); }
  @Override public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
    Objects.requireNonNull(fn); Objects.requireNonNull(executor);
    CompletableFuture<U> result = newIncompleteFuture();
    observe(() -> { if (failure != null) result.relayFailure(failure); else execute(executor, result, () -> fn.apply(value)); });
    return result;
  }
  @Override public CompletableFuture<Void> thenAccept(Consumer<? super T> action) { return thenAcceptAsync(action, DIRECT); }
  @Override public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) { return thenAcceptAsync(action, defaultExecutor()); }
  @Override public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
    Objects.requireNonNull(action); return thenApplyAsync(v -> { action.accept(v); return null; }, executor);
  }
  @Override public CompletableFuture<Void> thenRun(Runnable action) { return thenRunAsync(action, DIRECT); }
  @Override public CompletableFuture<Void> thenRunAsync(Runnable action) { return thenRunAsync(action, defaultExecutor()); }
  @Override public CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
    Objects.requireNonNull(action); return thenApplyAsync(v -> { action.run(); return null; }, executor);
  }

  @Override public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) { return thenComposeAsync(fn, DIRECT); }
  @Override public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) { return thenComposeAsync(fn, defaultExecutor()); }
  @Override public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
    Objects.requireNonNull(fn); Objects.requireNonNull(executor);
    CompletableFuture<U> result = newIncompleteFuture();
    observe(() -> {
      if (failure != null) { result.relayFailure(failure); return; }
      try {
        executor.execute(() -> {
          if (result.isDone()) return;
          try {
            CompletableFuture<U> next = Objects.requireNonNull(fn.apply(value)).toCompletableFuture();
            next.observe(() -> { if (next.failure != null) result.relayFailure(next.failure); else result.complete(next.value); });
          } catch (Throwable error) { result.relayFailure(error); }
        });
      } catch (Throwable error) { result.relayFailure(error); }
    });
    return result;
  }
  @Override public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
    Objects.requireNonNull(fn);
    return handle((v, error) -> error == null ? v : fn.apply(error));
  }
  @Override public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) { return handleAsync(fn, DIRECT); }
  @Override public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) { return handleAsync(fn, defaultExecutor()); }
  @Override public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
    Objects.requireNonNull(fn); Objects.requireNonNull(executor);
    CompletableFuture<U> result = newIncompleteFuture();
    observe(() -> execute(executor, result, () -> fn.apply(value, failure))); return result;
  }
  @Override public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) { return whenCompleteAsync(action, DIRECT); }
  @Override public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) { return whenCompleteAsync(action, defaultExecutor()); }
  @Override public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
    Objects.requireNonNull(action); Objects.requireNonNull(executor);
    CompletableFuture<T> result = newIncompleteFuture();
    observe(() -> execute(executor, result, () -> {
      try { action.accept(value, failure); }
      catch (Throwable error) { throw completion(failure == null ? error : failure); }
      if (failure != null) throw completion(failure);
      return value;
    })); return result;
  }

  public static CompletableFuture<Void> allOf(CompletableFuture<?>... futures) {
    Objects.requireNonNull(futures);
    for (CompletableFuture<?> future : futures) Objects.requireNonNull(future);
    CompletableFuture<Void> result = new CompletableFuture<>();
    if (futures.length == 0) { result.complete(null); return result; }
    CompletableFuture<?>[] copy = java.util.Arrays.copyOf(futures, futures.length);
    int[] remaining = {copy.length};
    for (CompletableFuture<?> future : copy) future.observe(() -> {
      if (--remaining[0] != 0) return;
      for (CompletableFuture<?> part : copy) if (part.failure != null) { result.relayFailure(part.failure); return; }
      result.complete(null);
    });
    return result;
  }
  public static CompletableFuture<Object> anyOf(CompletableFuture<?>... futures) {
    Objects.requireNonNull(futures);
    for (CompletableFuture<?> future : futures) Objects.requireNonNull(future);
    CompletableFuture<Object> result = new CompletableFuture<>();
    if (futures.length == 0) return result;
    CompletionRace race = new CompletionRace();
    result.observe(race::close);
    for (CompletableFuture<?> future : futures) race.observe(future, () -> {
      race.close();
      if (future.failure != null) result.relayFailure(future.failure); else result.complete(future.value);
    });
    return result;
  }
  public CompletableFuture<T> completeAsync(Supplier<? extends T> supplier) { return completeAsync(supplier, defaultExecutor()); }
  public CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
    Objects.requireNonNull(supplier); Objects.requireNonNull(executor); executeSubmission(executor, this, supplier); return this;
  }
  public CompletableFuture<T> orTimeout(long timeout, TimeUnit unit) {
    return timeoutAfter(null, timeout, unit, true);
  }
  public CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit) {
    return timeoutAfter(value, timeout, unit, false);
  }
  private CompletableFuture<T> timeoutAfter(T value, long timeout, TimeUnit unit, boolean exceptional) {
    Objects.requireNonNull(unit);
    if (!done) {
      ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1);
      ScheduledFuture<?> scheduled = timer.schedule(() -> {
        if (exceptional) completeExceptionally(new TimeoutException()); else complete(value);
      }, timeout, unit);
      observe(() -> { scheduled.cancel(false); timer.shutdown(); });
    }
    return this;
  }
  public static Executor delayedExecutor(long delay, TimeUnit unit) { return delayedExecutor(delay, unit, DIRECT); }
  public static Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
    Objects.requireNonNull(unit); Objects.requireNonNull(executor);
    return task -> {
      Objects.requireNonNull(task);
      ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1);
      timer.schedule(() -> { try { executor.execute(task); } finally { timer.shutdown(); } }, delay, unit);
    };
  }


  @Override public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) { return thenCombineAsync(other, fn, DIRECT); }
  @Override public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) { return thenCombineAsync(other, fn, defaultExecutor()); }
  @Override public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
    Objects.requireNonNull(other); Objects.requireNonNull(fn); Objects.requireNonNull(executor);
    CompletableFuture<? extends U> second = other.toCompletableFuture();
    CompletableFuture<V> result = newIncompleteFuture();
    boolean[] triggered = {false};
    Runnable check = () -> {
      if (!done || !second.done || triggered[0]) return;
      triggered[0] = true;
      if (failure != null) result.relayFailure(failure);
      else if (second.failure != null) result.relayFailure(second.failure);
      else execute(executor, result, () -> fn.apply(value, second.value));
    };
    observe(check); second.observe(check); return result;
  }
  @Override public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) { return thenAcceptBothAsync(other, action, DIRECT); }
  @Override public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) { return thenAcceptBothAsync(other, action, defaultExecutor()); }
  @Override public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
    Objects.requireNonNull(action); return thenCombineAsync(other, (a, b) -> { action.accept(a, b); return null; }, executor);
  }
  @Override public CompletableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) { return runAfterBothAsync(other, action, DIRECT); }
  @Override public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) { return runAfterBothAsync(other, action, defaultExecutor()); }
  @Override public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
    Objects.requireNonNull(action); return thenCombineAsync(other, (a, b) -> { action.run(); return null; }, executor);
  }
  @Override public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) { return applyToEitherAsync(other, fn, DIRECT); }
  @Override public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) { return applyToEitherAsync(other, fn, defaultExecutor()); }
  @Override public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
    Objects.requireNonNull(other); Objects.requireNonNull(fn); Objects.requireNonNull(executor);
    CompletableFuture<? extends T> second = other.toCompletableFuture();
    return either(second, selected -> fn.apply(selected == this ? value : second.value), executor);
  }
  private <U> CompletableFuture<U> either(CompletableFuture<?> second,
      Function<CompletableFuture<?>, U> fn, Executor executor) {
    CompletableFuture<U> result = newIncompleteFuture();
    CompletionRace race = new CompletionRace();
    result.observe(race::close);
    Runnable check = () -> {
      if (race.closed || (!done && !second.done)) return;
      race.close();
      CompletableFuture<?> selected = done ? this : second;
      if (selected.failure != null) result.relayFailure(selected.failure);
      else execute(executor, result, () -> fn.apply(selected));
    };
    race.observe(this, check); race.observe(second, check); return result;
  }
  @Override public CompletableFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) { return acceptEitherAsync(other, action, DIRECT); }
  @Override public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) { return acceptEitherAsync(other, action, defaultExecutor()); }
  @Override public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
    Objects.requireNonNull(action); return applyToEitherAsync(other, value -> { action.accept(value); return null; }, executor);
  }
  @Override public CompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) { return runAfterEitherAsync(other, action, DIRECT); }
  @Override public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) { return runAfterEitherAsync(other, action, defaultExecutor()); }
  @Override public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
    Objects.requireNonNull(other); Objects.requireNonNull(action); Objects.requireNonNull(executor);
    return either(other.toCompletableFuture(), selected -> { action.run(); return null; }, executor);
  }
}
