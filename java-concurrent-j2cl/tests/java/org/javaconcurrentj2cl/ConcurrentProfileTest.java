package org.javaconcurrentj2cl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;

/** Executes the real overlay in optimized JavaScript, including browser timers. */
public final class ConcurrentProfileTest {
  private static int checks;
  private static String phase;
  @JsProperty(namespace = JsPackage.GLOBAL, name = "concurrentProbeResult")
  private static native void setResult(String result);
  private static void check(boolean condition, String message) {
    checks++;
    if (!condition) throw new AssertionError(message);
  }
  private interface Throwing { void run() throws Exception; }
  private static void expect(Class<? extends Throwable> type, Throwing action) {
    checks++;
    try { action.run(); }
    catch (Throwable ex) {
      // J2CL intentionally has no Class.isInstance; use concrete type checks below.
      if ((type == IllegalStateException.class && ex instanceof IllegalStateException)
          || (type == RejectedExecutionException.class && ex instanceof RejectedExecutionException)
          || (type == CancellationException.class && ex instanceof CancellationException)
          || (type == TimeoutException.class && ex instanceof TimeoutException)
          || (type == ExecutionException.class && ex instanceof ExecutionException)
          || (type == CompletionException.class && ex instanceof CompletionException)
          || (type == NullPointerException.class && ex instanceof NullPointerException)
          || (type == IllegalArgumentException.class && ex instanceof IllegalArgumentException)) return;
      throw new AssertionError("Unexpected failure: " + ex);
    }
    throw new AssertionError("Expected failure: " + type.getName());
  }
  @JsMethod public static void start() {
    try {
      phase = "atomics";
      checks += AtomicsTest.run();
      phase = "collections and locks";
      checks += CollectionsLocksTest.run();
      phase = "additional queues";
      checks += AdditionalQueuesTest.run();
      phase = "scheduler state";
      checks += SchedulerEdgeTest.run();
      phase = "executors"; executors();
      phase = "futures"; futures();
      phase = "stages"; stages();
      phase = "review regressions"; checks += ReviewRegressionTest.run();
      phase = "threads"; threads();
      phase = "MPS cache"; mpsCache();
      phase = "browser timers"; scheduling();
    } catch (Throwable failure) { fail(failure); }
  }
  private static void fail(Throwable failure) {
    StringBuilder message = new StringBuilder("FAIL: ").append(phase).append(" after ")
        .append(checks).append(" checks: ").append(failure);
    for (StackTraceElement frame : failure.getStackTrace()) message.append("\n").append(frame);
    setResult(message.toString());
  }
  private static void executors() throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(4);
    Thread main = Thread.currentThread();
    List<Integer> order = new ArrayList<>();
    executor.execute(() -> {
      order.add(1);
      executor.submit(() -> { check(Thread.currentThread() == main, "main thread identity"); order.add(2); });
      order.add(3);
    });
    check(order.equals(Arrays.asList(1, 2, 3)), "nested submission executes inline");
    check(executor.submit(() -> 42).get() == 42, "callable value");
    check(executor.submit(() -> order.add(4), "result").get().equals("result"), "runnable result");
    Future<?> failure = executor.submit(() -> { throw new IllegalArgumentException("task failure"); });
    expect(ExecutionException.class, failure::get);
    try { failure.get(); } catch (ExecutionException ex) { check(ex.getCause() instanceof IllegalArgumentException, "original exception"); }
    expect(IllegalArgumentException.class, () -> executor.execute(() -> { throw new IllegalArgumentException(); }));
    List<Future<Integer>> all = executor.invokeAll(Arrays.asList(() -> 1, () -> { throw new IllegalStateException(); }, () -> 3));
    check(all.get(0).get() == 1 && all.get(2).get() == 3, "invokeAll continues after failure");
    expect(ExecutionException.class, all.get(1)::get);
    check(executor.<Integer>invokeAny(Arrays.asList(() -> { throw new IllegalStateException(); }, () -> 9)) == 9, "invokeAny tries next");
    List<Future<Integer>> expired = executor.invokeAll(Arrays.asList(() -> 1, () -> 2), 0, TimeUnit.SECONDS);
    check(expired.get(0).isCancelled() && expired.get(1).isCancelled(), "zero timeout does not execute");
    expect(TimeoutException.class, () -> executor.invokeAny(Arrays.asList(() -> 1), 0, TimeUnit.SECONDS));
    expect(NullPointerException.class, () -> executor.invokeAll(Arrays.asList(() -> 1, null)));
    ExecutorCompletionService<Integer> completion = new ExecutorCompletionService<>(executor);
    completion.submit(() -> 5); completion.submit(() -> 8);
    check(completion.take().get() == 5 && completion.take().get() == 8, "completion queue order");
    check(completion.poll() == null, "completion queue empties");
    expect(IllegalStateException.class, completion::take);
    executor.shutdown();
    check(executor.isShutdown() && executor.isTerminated(), "executor shutdown state");
    check(executor.awaitTermination(1, TimeUnit.DAYS), "inline termination");
    expect(RejectedExecutionException.class, () -> executor.submit(() -> 1));
    expect(IllegalArgumentException.class, () -> Executors.newFixedThreadPool(0));

    // MPS GenerationTaskPool relies on hooks to balance queued task accounting.
    AtomicInteger queued = new AtomicInteger();
    AtomicInteger hooks = new AtomicInteger();
    ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 3, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>()) {
      @Override protected void afterExecute(Runnable task, Throwable error) { hooks.incrementAndGet(); queued.decrementAndGet(); }
    };
    queued.incrementAndGet(); pool.submit(() -> {
      queued.incrementAndGet(); pool.submit(() -> 2);
    });
    queued.incrementAndGet(); pool.submit(() -> { throw new IllegalStateException(); });
    check(queued.get() == 0 && hooks.get() == 3, "MPS generation hook accounting");
    check(pool.getCompletedTaskCount() == 3 && pool.getActiveCount() == 0, "pool completed counters");
    // MPS TextGeneratorEngine produces before submit(queue::take).
    LinkedBlockingQueue<String> results = new LinkedBlockingQueue<>();
    AtomicInteger remaining = new AtomicInteger(3);
    for (int i = 0; i < 3; i++) pool.execute(() -> { if (remaining.decrementAndGet() == 0) results.add("generated"); });
    check(pool.submit(results::take).get().equals("generated"), "MPS producer then queued result");
    pool.execute(() -> { pool.shutdown(); check(!pool.isTerminated(), "running task delays termination"); });
    check(pool.isTerminated(), "termination after running task");
    check(CompletableFuture.supplyAsync(() -> 13, ForkJoinPool.commonPool()).join() == 13, "MPS common pool executes");
    ForkJoinPool.commonPool().shutdown();
    check(!ForkJoinPool.commonPool().isShutdown(), "common pool remains available");
    AtomicInteger blockCalls = new AtomicInteger();
    ForkJoinPool.managedBlock(new ForkJoinPool.ManagedBlocker() {
      public boolean isReleasable() { return false; }
      public boolean block() { blockCalls.incrementAndGet(); return true; }
    });
    check(blockCalls.get() == 1, "managed blocker executes available work");
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = 0; i < 20; i++) {
      int number = random.nextInt(-8, 3);
      long large = random.nextLong(9007199254740993L, 9007199254741993L);
      double fraction = random.nextDouble(-2, 4);
      check(number >= -8 && number < 3 && large >= 9007199254740993L && large < 9007199254741993L
          && fraction >= -2 && fraction < 4, "bounded random values");
    }
  }
  private static void futures() throws Exception {
    AtomicInteger runs = new AtomicInteger();
    FutureTask<Integer> future = new FutureTask<>(runs::incrementAndGet);
    expect(IllegalStateException.class, future::get);
    expect(TimeoutException.class, () -> future.get(1, TimeUnit.SECONDS));
    future.run(); future.run();
    check(future.get() == 1 && runs.get() == 1 && future.isDone(), "FutureTask at most once");
    check(!future.cancel(false), "completed task cannot cancel");
    FutureTask<Integer> cancelled = new FutureTask<>(runs::incrementAndGet);
    check(cancelled.cancel(true), "pending cancellation"); cancelled.run();
    check(runs.get() == 1 && cancelled.isCancelled(), "cancelled body never runs");
    expect(CancellationException.class, cancelled::get);
    FutureTask<Integer> error = new FutureTask<>(() -> { throw new AssertionError("stored"); });
    error.run(); expect(ExecutionException.class, error::get);
    FutureTask<Integer> hookFailure = new FutureTask<Integer>(() -> 8) {
      @Override protected void done() { throw new IllegalArgumentException("done hook"); }
    };
    expect(IllegalArgumentException.class, hookFailure::run);
    check(hookFailure.get() == 8, "done hook failure does not erase result");
  }
  private static void stages() throws Exception {
    CompletableFuture<Integer> pending = new CompletableFuture<>();
    CompletableFuture<Integer> mapped = pending.thenApply(v -> v + 2).thenCompose(v -> CompletableFuture.completedFuture(v * 3));
    check(!mapped.isDone() && pending.getNumberOfDependents() == 1, "pending dependent retained");
    expect(IllegalStateException.class, pending::join);
    expect(TimeoutException.class, () -> pending.get(3, TimeUnit.SECONDS));
    check(pending.getNow(7) == 7, "getNow fallback");
    check(pending.complete(4) && !pending.complete(9), "completion happens once");
    check(mapped.join() == 18, "map and compose run");
    check(pending.thenApply(v -> v + 1).join() == 5, "handler after completion");
    AtomicInteger async = new AtomicInteger();
    check(CompletableFuture.supplyAsync(async::incrementAndGet).join() == 1, "async supplier inline");
    CompletableFuture.runAsync(async::incrementAndGet).join();
    check(async.get() == 2, "async runnable inline");
    List<Runnable> manual = new ArrayList<>();
    CompletableFuture<Integer> deferred = pending.thenApplyAsync(v -> v + 8, manual::add);
    check(!deferred.isDone() && manual.size() == 1, "explicit executor honored");
    manual.remove(0).run(); check(deferred.join() == 12, "explicit executor callback");
    CompletableFuture<Integer> failed = new CompletableFuture<>();
    CompletableFuture<Integer> recovered = failed.exceptionally(ex -> 21);
    failed.completeExceptionally(new IllegalArgumentException("failure"));
    check(recovered.join() == 21, "failure recovery");
    expect(ExecutionException.class, failed::get); expect(CompletionException.class, failed::join);
    check(failed.handle((v, ex) -> ex != null ? 5 : 0).join() == 5, "handle failure");
    expect(CompletionException.class, () -> pending.thenApply(v -> { throw new IllegalStateException(); }).join());
    CompletableFuture<Integer> cancelled = new CompletableFuture<>();
    CompletableFuture<Integer> dependent = cancelled.thenApply(v -> v + 1);
    cancelled.cancel(true);
    expect(CancellationException.class, cancelled::join);
    check(dependent.isCompletedExceptionally() && !dependent.isCancelled(), "dependent cancellation wrapped");
    expect(CompletionException.class, dependent::join);
    CompletableFuture<Integer> a = new CompletableFuture<>(), b = new CompletableFuture<>();
    CompletableFuture<Integer> combined = a.thenCombine(b, Integer::sum);
    a.complete(10); check(!combined.isDone(), "combine waits both");
    b.complete(20); check(combined.join() == 30, "combine results");
    CompletableFuture<Integer> c = new CompletableFuture<>(), d = new CompletableFuture<>();
    CompletableFuture<Integer> either = c.applyToEither(d, v -> v * 2);
    d.complete(6); c.complete(20); check(either.join() == 12, "either completes once");
    CompletableFuture<Integer> e = new CompletableFuture<>(), f = new CompletableFuture<>();
    CompletableFuture<Void> all = CompletableFuture.allOf(e, f);
    e.completeExceptionally(new IllegalStateException()); check(!all.isDone(), "allOf waits even after failure");
    f.complete(2); expect(CompletionException.class, all::join);
    check(CompletableFuture.allOf().isDone() && !CompletableFuture.anyOf().isDone(), "empty fanin");
    check(CompletableFuture.anyOf(a, b).join().equals(10), "anyOf value");
    AtomicInteger callback = new AtomicInteger();
    check(a.whenComplete((v, ex) -> callback.set(v)).join() == 10 && callback.get() == 10, "whenComplete value");
    a.thenAcceptBoth(b, (x, y) -> callback.set(x + y)).join();
    a.runAfterBoth(b, callback::incrementAndGet).join();
    a.acceptEither(b, callback::set).join();
    a.runAfterEither(b, callback::incrementAndGet).join();
    check(callback.get() == 11, "binary action stages");
    CompletableFuture<Integer> root = new CompletableFuture<>(), tail = root;
    for (int i = 0; i < 10000; i++) tail = tail.thenApply(v -> v + 1);
    root.complete(0); check(tail.join() == 10000, "deep chain drains without stack overflow");
    check(CompletableFuture.completedFuture(1).thenApply(v ->
        CompletableFuture.completedFuture(2).thenApply(w -> w + 1).getNow(-1)).join() == 3,
        "nested already-completed mapping is immediate");
    check(CompletableFuture.completedFuture(1).thenApply(v -> {
      CompletableFuture<Integer> source = new CompletableFuture<>();
      CompletableFuture<Integer> target = source.thenApply(w -> w + 1);
      source.complete(2); return target.join();
    }).join() == 3, "nested join helps runnable completion notifications");
    CompletableFuture<Integer> overwritten = CompletableFuture.completedFuture(1);
    overwritten.obtrudeValue(3); check(overwritten.join() == 3, "obtrude value");
    overwritten.obtrudeException(new IllegalStateException()); expect(CompletionException.class, overwritten::join);
    expect(NullPointerException.class, () -> pending.thenApply(null));
    ExecutorService rejected = Executors.newSingleThreadExecutor(); rejected.shutdown();
    expect(RejectedExecutionException.class, () -> CompletableFuture.supplyAsync(() -> 1, rejected));
    expect(CompletionException.class, () -> pending.thenApplyAsync(v -> v + 1, rejected).join());
  }
  private static void threads() throws Exception {
    Thread main = Thread.currentThread();
    AtomicInteger executed = new AtomicInteger();
    Thread task = new Thread(() -> { check(Thread.currentThread() == main, "Thread.start stays on main"); executed.incrementAndGet(); });
    task.start(); task.join();
    check(executed.get() == 1 && task.getState() == Thread.State.TERMINATED, "Thread body executes and terminates");
    expect(IllegalArgumentException.class, task::start);
    Thread.currentThread().interrupt();
    check(Thread.currentThread().isInterrupted() && Thread.interrupted() && !Thread.interrupted(), "interruption flag");
    expect(IllegalStateException.class, () -> Thread.sleep(1));
    ThreadLocal<Integer> local = ThreadLocal.withInitial(() -> 7);
    check(local.get() == 7, "ThreadLocal lazy value"); local.set(9);
    check(local.get() == 9, "ThreadLocal set"); local.remove(); check(local.get() == 7, "ThreadLocal remove");
    AtomicInteger initializations = new AtomicInteger();
    ThreadLocal<Integer> lazy = ThreadLocal.withInitial(initializations::incrementAndGet);
    check(initializations.get() == 0, "ThreadLocal supplier is lazy");
    check(lazy.get() == 1 && lazy.get() == 1, "ThreadLocal initializes once");
    lazy.set(null); check(lazy.get() == null && initializations.get() == 1, "ThreadLocal explicit null stays initialized");
    lazy.remove(); check(lazy.get() == 2, "ThreadLocal remove reinitializes");
    ThreadLocal<Integer> custom = new ThreadLocal<Integer>() {
      @Override protected Integer initialValue() { return 17; }
    };
    check(custom.get() == 17, "MPS-style initialValue override");
  }
  private static void scheduling() {
    CompletableFuture<Integer> schedulerEdges = SchedulerEdgeTest.runAsync();
    ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    AtomicInteger ticks = new AtomicInteger();
    AtomicInteger cancelledRuns = new AtomicInteger();
    ScheduledFuture<?> cancelled = scheduler.schedule(cancelledRuns::incrementAndGet, 30, TimeUnit.MILLISECONDS);
    cancelled.cancel(false);
    ScheduledFuture<Integer> oneShot = scheduler.schedule(() -> 123, 5, TimeUnit.MILLISECONDS);
    check(!oneShot.isDone(), "scheduled work waits for a timer");
    ScheduledFuture<?>[] repeating = new ScheduledFuture<?>[1];
    repeating[0] = scheduler.scheduleAtFixedRate(() -> {
      if (ticks.incrementAndGet() == 3) repeating[0].cancel(false);
    }, 1, 2, TimeUnit.MILLISECONDS);
    ScheduledFuture<?> periodicFailure = scheduler.scheduleWithFixedDelay(() -> { throw new IllegalStateException("periodic"); }, 1, 1, TimeUnit.MILLISECONDS);
    CompletableFuture<Integer> timeoutValue = new CompletableFuture<Integer>().completeOnTimeout(77, 2, TimeUnit.MILLISECONDS);
    CompletableFuture<Integer> timeoutError = new CompletableFuture<Integer>().orTimeout(2, TimeUnit.MILLISECONDS);
    CompletableFuture<Integer> delayed = CompletableFuture.supplyAsync(() -> 88, CompletableFuture.delayedExecutor(2, TimeUnit.MILLISECONDS));
    ScheduledThreadPoolExecutor stopped = new ScheduledThreadPoolExecutor(1);
    stopped.schedule(cancelledRuns::incrementAndGet, 1, TimeUnit.DAYS);
    check(stopped.shutdownNow().size() == 1 && stopped.isTerminated(), "shutdownNow cancels scheduled work");
    scheduler.schedule(() -> {
      try {
        check(oneShot.get() == 123, "browser timer executes callable");
        check(ticks.get() == 3 && repeating[0].isCancelled(), "periodic timer cancels itself");
        check(cancelledRuns.get() == 0, "cancelled timers never execute");
        check(periodicFailure.isDone(), "periodic exception suppresses recurrence");
        expect(ExecutionException.class, periodicFailure::get);
        check(timeoutValue.join() == 77, "completion timeout value");
        check(timeoutError.isCompletedExceptionally(), "completion timeout failure");
        check(delayed.join() == 88, "delayed executor executes");
        checks += schedulerEdges.join();
        scheduler.shutdown();
        check(scheduler.isShutdown(), "scheduler shutdown");
        setResult("PASS: " + checks + " checks in optimized J2CL JavaScript; main-thread timers verified");
      } catch (Throwable failure) { fail(failure); scheduler.shutdownNow(); }
    }, 80, TimeUnit.MILLISECONDS);
  }
  private static void mpsCache() {
    jetbrains.mps.util.SimpleLRUCache<String> cache = new jetbrains.mps.util.SimpleLRUCache<>(100);
    String original = new String("canonical");
    check(cache.cacheObject(original) == original, "unchanged MPS cache stores value");
    check(cache.cacheObject(new String("canonical")) == original, "unchanged MPS cache canonicalizes");
    for (int i = 0; i < 1000; i++) cache.cacheObject("entry-" + i);
    check(cache.size() > 0 && cache.size() <= 100, "unchanged MPS cache evicts entries");
  }
  private ConcurrentProfileTest() {}
}
