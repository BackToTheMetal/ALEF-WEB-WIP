package org.javaconcurrentj2cl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Reproductions for the four reviewed failures, executed against the browser overlay. */
final class ReviewRegressionTest {
  private static int checks;

  static int run() throws Exception {
    checks = 0;
    races();
    minimalStages();
    mapCallbacks();
    queuedShutdown();
    return checks;
  }

  private static void check(boolean condition, String message) {
    checks++;
    if (!condition) throw new AssertionError(message);
  }

  private static void expectNull(Runnable action) {
    checks++;
    try { action.run(); }
    catch (NullPointerException expected) { return; }
    throw new AssertionError("Expected NullPointerException");
  }

  private static void races() {
    CompletableFuture<Integer> loser = new CompletableFuture<>();
    long sum = 0;
    for (int i = 0; i < 1000; i++) {
      CompletableFuture<Integer> ready = CompletableFuture.completedFuture(i);
      sum += (Integer) CompletableFuture.anyOf(ready, loser).join();
      sum += (Integer) CompletableFuture.anyOf(loser, ready).join();
      sum += ready.applyToEither(loser, value -> value).join();
      ready.acceptEither(loser, value -> {}).join();
      ready.runAfterEither(loser, () -> {}).join();
    }
    check(sum == 1498500, "completed race values execute in either input order");
    check(loser.getNumberOfDependents() == 0, "completed races release the shared pending loser");
    check(!loser.isDone(), "race cleanup does not cancel the input");
    check(CompletableFuture.anyOf().getNumberOfDependents() == 0, "empty race has no registrations");
    expectNull(() -> CompletableFuture.anyOf(CompletableFuture.completedFuture(1), null));

    CompletableFuture<Integer> winner = new CompletableFuture<>();
    CompletableFuture<Object> failed = CompletableFuture.anyOf(loser, winner);
    IllegalArgumentException failure = new IllegalArgumentException("race failure");
    winner.completeExceptionally(failure);
    check(failed.isCompletedExceptionally(), "exceptional winner completes the aggregate");
    check(failed.handle((value, error) -> error.getCause() == failure).join(), "winner failure preserved");
    check(loser.getNumberOfDependents() == 0, "exceptional race releases loser");

    CompletableFuture<Integer> a = new CompletableFuture<>(), b = new CompletableFuture<>();
    CompletableFuture<Object> cancelled = CompletableFuture.anyOf(a, b, a);
    cancelled.cancel(false);
    check(a.getNumberOfDependents() == 0 && b.getNumberOfDependents() == 0,
        "cancellation detaches duplicate and distinct registrations");
    check(!a.isDone() && !b.isDone(), "cancelled aggregate leaves inputs pending");
    CompletableFuture<Object> externallyCompleted = CompletableFuture.anyOf(a, b);
    externallyCompleted.complete(3);
    check(a.getNumberOfDependents() == 0 && b.getNumberOfDependents() == 0,
        "external aggregate completion releases registrations");

    List<Runnable> queued = new ArrayList<>();
    AtomicInteger actions = new AtomicInteger();
    CompletableFuture<Integer> deferred = a.applyToEitherAsync(b,
        value -> { actions.incrementAndGet(); return value; }, queued::add);
    a.complete(4);
    check(!deferred.isDone() && queued.size() == 1, "explicit executor still defers the winning action");
    check(b.getNumberOfDependents() == 0, "loser detached before deferred action executes");
    deferred.cancel(false);
    queued.remove(0).run();
    check(actions.get() == 0, "cancelled winning action does not execute");

    CompletableFuture<Integer> pendingOne = new CompletableFuture<>();
    CompletableFuture<String> pendingTwo = new CompletableFuture<>();
    CompletableFuture<Void> afterEither = pendingOne.runAfterEither(pendingTwo, actions::incrementAndGet);
    afterEither.cancel(false);
    check(pendingOne.getNumberOfDependents() == 0 && pendingTwo.getNumberOfDependents() == 0,
        "runAfterEither cancellation releases both differently typed inputs");
    CompletableFuture<Integer> pendingThree = new CompletableFuture<>();
    CompletableFuture<Integer> mapped = pendingOne.applyToEither(pendingThree, value -> value);
    mapped.completeExceptionally(failure);
    check(pendingOne.getNumberOfDependents() == 0 && pendingThree.getNumberOfDependents() == 0,
        "external Either failure releases inputs");

    CompletableFuture<Integer> first = new CompletableFuture<>(), second = new CompletableFuture<>();
    CompletableFuture<Object> nested = CompletableFuture.anyOf(first, second);
    CompletableFuture<Integer> trigger = new CompletableFuture<>();
    trigger.thenRun(() -> { first.complete(11); second.complete(12); });
    trigger.complete(0);
    check(nested.join().equals(11), "already queued losing notifications cannot overwrite the winner");
    check(first.getNumberOfDependents() == 0 && second.getNumberOfDependents() == 0,
        "nested completion disposes queued race notifications");
  }

  private static void minimalStages() {
    CompletableFuture<Integer> source = new CompletableFuture<>();
    CompletionStage<Integer> stage = source.minimalCompletionStage();
    CompletionStage<Integer> mapped = stage.thenApply(value -> value * 2);
    CompletionStage<Integer> composed = stage.thenCompose(value -> CompletableFuture.completedStage(value + 1));
    CompletableFuture<Integer> changed = stage.toCompletableFuture();
    CompletableFuture<Integer> cancelled = stage.toCompletableFuture();
    CompletableFuture<Integer> overwritten = stage.toCompletableFuture();
    CompletableFuture<Integer> failed = stage.toCompletableFuture();
    check(changed.complete(99) && cancelled.cancel(false), "exported futures can complete independently");
    overwritten.obtrudeValue(101);
    failed.obtrudeException(new IllegalStateException("isolated"));
    mapped.toCompletableFuture().complete(-1);
    check(!source.isDone(), "modifying conversions leaves the original pending");
    source.complete(7);
    check(source.join() == 7 && stage.toCompletableFuture().join() == 7,
        "minimal stage keeps source result after exported completion/cancellation/obtrusion");
    check(mapped.toCompletableFuture().join() == 14, "derived minimal stage is also isolated");
    check(composed.toCompletableFuture().join() == 8, "minimal composition executes");
    check(changed.join() == 99 && cancelled.isCancelled() && overwritten.join() == 101
        && failed.isCompletedExceptionally(), "source completion does not overwrite independent conversions");

    CompletionStage<Integer> ready = CompletableFuture.completedStage(13);
    ready.toCompletableFuture().obtrudeValue(-2);
    check(ready.toCompletableFuture().join() == 13, "completedStage conversions are defensive");
    IllegalArgumentException failure = new IllegalArgumentException("source failure");
    CompletionStage<Integer> failedStage = CompletableFuture.failedStage(failure);
    failedStage.toCompletableFuture().obtrudeValue(23);
    check(failedStage.handle((value, error) -> error == failure).toCompletableFuture().join(),
        "failedStage conversions cannot replace the original failure");

    CompletableFuture<Integer> failingSource = new CompletableFuture<>();
    CompletionStage<Integer> minimalFailure = failingSource.minimalCompletionStage();
    minimalFailure.toCompletableFuture().complete(5);
    failingSource.completeExceptionally(failure);
    check(minimalFailure.handle((value, error) -> error instanceof CompletionException
        && error.getCause() == failure).toCompletableFuture().join(), "minimal failure is relayed");
    check(minimalFailure.exceptionally(error -> 17).toCompletableFuture().join() == 17,
        "minimal stage failure recovery executes");

    List<Runnable> tasks = new ArrayList<>();
    AtomicInteger callbacks = new AtomicInteger();
    CompletionStage<Integer> deferred = ready.thenApplyAsync(value -> value + 1, tasks::add);
    check(!deferred.toCompletableFuture().isDone() && tasks.size() == 1, "minimal stage honors explicit executor");
    tasks.remove(0).run();
    check(deferred.toCompletableFuture().join() == 14, "deferred minimal stage completes");
    ready.thenAccept(callbacks::set).thenRun(callbacks::incrementAndGet).toCompletableFuture().join();
    check(callbacks.get() == 14, "minimal action stages execute");
    check(ready.thenCombine(CompletableFuture.completedStage(2), Integer::sum)
        .whenComplete((value, error) -> callbacks.set(value)).toCompletableFuture().join() == 15
        && callbacks.get() == 15, "minimal binary stage and completion callback execute");
  }

  private static ConcurrentHashMap<Integer, Integer> map() {
    ConcurrentHashMap<Integer, Integer> result = new ConcurrentHashMap<>();
    result.put(1, 1); result.put(2, 2); result.put(3, 3);
    return result;
  }

  private static void mapCallbacks() {
    ConcurrentHashMap<Integer, Integer> map = map();
    AtomicInteger visits = new AtomicInteger();
    map.forEach((key, value) -> { visits.incrementAndGet(); map.remove(key); });
    check(map.isEmpty() && visits.get() == 3, "map forEach permits callback removal of each entry");

    map.putAll(map());
    Set<Integer> keys = map.keySet();
    for (Integer key : keys) map.remove(key);
    check(keys.isEmpty(), "key iterator tolerates live removal and key view remains live");
    map.putAll(map());
    Collection<Integer> values = map.values();
    for (Integer value : values) map.remove(value);
    check(values.isEmpty(), "value iterator tolerates live removal and value view remains live");
    map.putAll(map());
    map.entrySet().stream().forEach(entry -> map.remove(entry.getKey()));
    check(map.isEmpty(), "entry stream tolerates callback removal");

    map.putAll(map());
    Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
    Map.Entry<Integer, Integer> entry = iterator.next();
    Integer key = entry.getKey();
    check(entry.setValue(42).equals(key) && map.get(key) == 42, "entry setValue writes through");
    expectNull(() -> entry.setValue(null));
    check(map.get(key) == 42, "null entry update leaves existing value intact");
    map.put(key, 43);
    iterator.remove();
    check(!map.containsKey(key), "iterator removal targets the live key after replacement");
    while (iterator.hasNext()) iterator.next();
    map.clear();
    check(keys.isEmpty() && values.isEmpty(), "previously obtained views track clear");

    map.put(1, 1);
    boolean removed = map.entrySet().removeIf(item -> { map.put(item.getKey(), 2); return true; });
    check(!removed && map.get(1) == 2, "entry removeIf preserves mappings replaced by its predicate");
    removed = map.values().removeIf(value -> { map.put(1, 3); return true; });
    check(!removed && map.get(1) == 3, "value removeIf preserves mappings replaced by its predicate");
    check(map.values().removeIf(value -> value == 3) && map.isEmpty(), "value removeIf removes matching mappings");
    map.putAll(map());
    map.replaceAll((itemKey, value) -> { map.remove(itemKey); return value + 1; });
    check(map.isEmpty(), "replaceAll does not resurrect mappings removed by its callback");

    Set<Integer> concurrentSet = ConcurrentHashMap.newKeySet();
    concurrentSet.addAll(Arrays.asList(1, 2, 3));
    concurrentSet.forEach(concurrentSet::remove);
    check(concurrentSet.isEmpty(), "newKeySet traversal tolerates live mutations");
    expectNull(() -> concurrentSet.add(null));
  }

  private static final class TrackingPool extends ThreadPoolExecutor {
    int terminations;
    boolean emptyAtTermination;
    TrackingPool(BlockingQueue<Runnable> queue) { super(1, 1, 0, TimeUnit.SECONDS, queue); }
    @Override protected void terminated() {
      terminations++;
      emptyAtTermination = getQueue().isEmpty();
    }
  }

  private static void queuedShutdown() throws Exception {
    AtomicInteger runs = new AtomicInteger();
    LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    Runnable first = runs::incrementAndGet;
    FutureTask<Integer> second = new FutureTask<>(runs::incrementAndGet);
    queue.add(first); queue.add(second);
    TrackingPool pool = new TrackingPool(queue);
    List<Runnable> pending = pool.shutdownNow();
    check(pending.equals(Arrays.asList(first, second)), "shutdownNow returns pre-existing queued tasks in order");
    check(queue.isEmpty() && pool.isTerminated(), "shutdownNow drains queue before termination");
    check(runs.get() == 0 && !second.isDone(), "shutdownNow returns unexecuted tasks without discarding or cancelling them");
    check(pool.terminations == 1 && pool.emptyAtTermination, "termination hook sees the drained queue exactly once");
    check(pool.shutdownNow().isEmpty() && pool.terminations == 1, "repeated shutdown does not repeat termination hook");
    for (Runnable task : pending) task.run();
    check(runs.get() == 2 && second.get() == 2, "returned tasks remain executable by the caller");

    TrackingPool graceful = new TrackingPool(new LinkedBlockingQueue<>());
    graceful.getQueue().add(first);
    graceful.shutdown();
    check(!graceful.isTerminated() && graceful.isTerminating()
        && !graceful.awaitTermination(0, TimeUnit.SECONDS), "stored queue entries prevent premature graceful termination");
    check(graceful.remove(first) && graceful.isTerminated() && graceful.terminations == 1,
        "removing the final queued entry updates termination");

    TrackingPool purged = new TrackingPool(new LinkedBlockingQueue<>());
    FutureTask<Integer> cancelled = new FutureTask<>(runs::incrementAndGet);
    purged.getQueue().add(cancelled); cancelled.cancel(false); purged.shutdown();
    purged.purge();
    check(purged.isTerminated() && purged.terminations == 1, "purging cancelled queue entries updates termination");

    TrackingPool active = new TrackingPool(new LinkedBlockingQueue<>());
    active.execute(() -> {
      active.getQueue().add(first);
      check(active.shutdownNow().equals(Arrays.asList(first)), "nested shutdown returns waiting task");
      check(!active.isTerminated() && active.terminations == 0, "active body prevents early termination");
    });
    check(active.isTerminated() && active.terminations == 1, "termination follows active body completion");

    // Reproduce a delay-based queue whose unexpired elements cannot be drained by poll.
    LinkedBlockingQueue<Runnable> delayed = new LinkedBlockingQueue<Runnable>() {
      @Override public int drainTo(Collection<? super Runnable> target) { return 0; }
      @Override public Runnable poll() { return null; }
    };
    delayed.add(first);
    TrackingPool delayedPool = new TrackingPool(delayed);
    check(delayedPool.shutdownNow().equals(Arrays.asList(first)) && delayed.isEmpty()
        && delayedPool.isTerminated(), "shutdownNow removes unexpired queue entries as well");
  }

  private ReviewRegressionTest() {}
}
