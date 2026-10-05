package org.javaconcurrentj2cl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Checks queue mutation, cancellation retention and timer cleanup. */
public final class SchedulerEdgeTest {
  private static int checks;

  public static int run() {
    checks = 0;
    queueRemoval();
    cancellationPolicies();
    delayedQueueOperations();
    return checks;
  }

  private static void queueRemoval() {
    ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    try {
      ScheduledFuture<?> direct = later(scheduler);
      check(scheduler.remove((Runnable) direct), "executor remove finds scheduled task");
      check(!direct.isDone() && scheduler.getQueue().isEmpty(), "remove detaches without cancellation");
      check(!scheduler.remove((Runnable) direct), "removed task is absent");

      ScheduledFuture<?> viaQueue = later(scheduler);
      check(scheduler.getQueue().remove(viaQueue), "queue remove finds scheduled task");
      check(!viaQueue.isDone(), "queue remove preserves future state");

      ScheduledFuture<?> viaIterator = later(scheduler);
      Iterator<Runnable> iterator = scheduler.getQueue().iterator();
      check(iterator.next() == viaIterator, "queue iterator exposes actual future");
      iterator.remove();
      check(scheduler.getQueue().isEmpty() && !viaIterator.isDone(), "iterator removal detaches timer");
      try {
        iterator.remove();
        throw new AssertionError("Expected iterator state failure");
      } catch (IllegalStateException expected) { checks++; }

      ScheduledFuture<?> cleared = later(scheduler);
      later(scheduler);
      scheduler.getQueue().clear();
      check(scheduler.getQueue().isEmpty() && !cleared.isDone(), "clear detaches all timers");

      ScheduledFuture<?> removed = later(scheduler);
      ScheduledFuture<?> kept = later(scheduler);
      check(scheduler.getQueue().removeAll(Arrays.asList(removed)), "removeAll detaches selected timer");
      check(scheduler.getQueue().contains(kept) && scheduler.getQueue().size() == 1,
          "removeAll preserves remaining task");
      check(scheduler.getQueue().retainAll(new ArrayList<Runnable>()), "retainAll detaches remaining timer");
      scheduler.shutdown();
      check(scheduler.isTerminated(), "removed timers do not delay termination");
    } finally { scheduler.shutdownNow(); }
  }

  private static void cancellationPolicies() {
    ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    try {
      check(!scheduler.getRemoveOnCancelPolicy(), "default cancellation retention policy");
      ScheduledFuture<?> retained = later(scheduler);
      retained.cancel(false);
      check(retained.isCancelled() && scheduler.getQueue().contains(retained),
          "default cancellation retains queue entry until deadline");
      scheduler.purge();
      check(scheduler.getQueue().isEmpty(), "purge removes canceled timer");

      scheduler.setRemoveOnCancelPolicy(true);
      ScheduledFuture<?> immediate = later(scheduler);
      immediate.cancel(false);
      check(scheduler.getRemoveOnCancelPolicy() && scheduler.getQueue().isEmpty(),
          "removeOnCancel removes entry immediately");

      scheduler.setRemoveOnCancelPolicy(false);
      ScheduledFuture<?> shutdownCleanup = later(scheduler);
      shutdownCleanup.cancel(false);
      scheduler.shutdown();
      check(scheduler.isTerminated() && scheduler.getQueue().isEmpty(),
          "shutdown clears canceled entries without waiting for deadline");
    } finally { scheduler.shutdownNow(); }
  }

  private static void delayedQueueOperations() {
    ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
    try {
      ScheduledFuture<?> distant = scheduler.schedule(() -> {}, 2, TimeUnit.DAYS);
      ScheduledFuture<?> earlier = scheduler.schedule(() -> {}, 1, TimeUnit.DAYS);
      check(scheduler.getQueue().peek() == earlier, "queue peek selects earliest deadline");
      check(scheduler.getQueue().poll() == null, "queue poll does not release future timers");
      List<Runnable> drained = new ArrayList<>();
      check(scheduler.getQueue().drainTo(drained) == 0, "drainTo preserves future timers");

      ScheduledFuture<?> due = scheduler.schedule(() -> 7, 0, TimeUnit.NANOSECONDS);
      check(scheduler.getQueue().poll() == due && !due.isDone(), "poll detaches due task without running it");
      ((Runnable) due).run();
      check(due.isDone(), "detached runnable future can still execute explicitly");

      ScheduledFuture<?> dueToDrain = scheduler.schedule(() -> {}, 0, TimeUnit.NANOSECONDS);
      check(scheduler.getQueue().drainTo(drained, 1) == 1 && drained.get(0) == dueToDrain,
          "drainTo detaches only due task");
      check(!dueToDrain.isDone(), "drainTo preserves future state");
      try {
        scheduler.getQueue().offer(() -> {});
        throw new AssertionError("Expected direct queue insertion failure");
      } catch (UnsupportedOperationException expected) { checks++; }
      List<Runnable> stopped = scheduler.shutdownNow();
      check(stopped.size() == 2 && stopped.contains(earlier) && stopped.contains(distant),
          "shutdownNow returns queued tasks only");
      check(earlier.isCancelled() && distant.isCancelled() && scheduler.isTerminated(),
          "shutdownNow cancels pending timers");
    } finally { scheduler.shutdownNow(); }
  }

  /** Completes after actual browser timers verify that removed work cannot fire. */
  public static CompletableFuture<Integer> runAsync() {
    CompletableFuture<Integer> result = new CompletableFuture<>();
    AtomicInteger calls = new AtomicInteger();
    AtomicInteger hooks = new AtomicInteger();
    AtomicInteger queuedDuringHook = new AtomicInteger();
    ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1) {
      @Override protected void beforeExecute(Thread thread, Runnable task) {
        if (getQueue().contains(task)) queuedDuringHook.incrementAndGet();
      }
      @Override protected void afterExecute(Runnable task, Throwable failure) { hooks.incrementAndGet(); }
    };
    ScheduledFuture<?> removed = scheduler.schedule(calls::incrementAndGet, 5, TimeUnit.MILLISECONDS);
    scheduler.remove((Runnable) removed);
    ScheduledFuture<?> cleared = scheduler.schedule(calls::incrementAndGet, 5, TimeUnit.MILLISECONDS);
    scheduler.getQueue().clear();
    ScheduledFuture<?> canceled = scheduler.schedule(calls::incrementAndGet, 5, TimeUnit.MILLISECONDS);
    canceled.cancel(false);
    ScheduledFuture<?> accepted = scheduler.schedule(calls::incrementAndGet, 5, TimeUnit.MILLISECONDS);
    scheduler.schedule(() -> {
      try {
        if (calls.get() != 1 || !accepted.isDone()) throw new AssertionError("Removed timers fired or accepted timer was lost");
        if (removed.isDone() || cleared.isDone()) throw new AssertionError("Removal changed future state");
        if (scheduler.getQueue().contains(canceled)) throw new AssertionError("Canceled entry survived its deadline");
        if (hooks.get() != 1) throw new AssertionError("Scheduled execution hooks did not match executed tasks");
        if (queuedDuringHook.get() != 0) throw new AssertionError("Running tasks remained in scheduler queue");
        scheduler.shutdown();
        result.complete(5);
      } catch (Throwable failure) {
        scheduler.shutdownNow();
        result.completeExceptionally(failure);
      }
    }, 40, TimeUnit.MILLISECONDS);
    return result;
  }

  private static ScheduledFuture<?> later(ScheduledThreadPoolExecutor scheduler) {
    return scheduler.schedule(() -> {}, 1, TimeUnit.DAYS);
  }

  private static void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
    checks++;
  }
}
