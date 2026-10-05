package org.javaconcurrentj2cl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TransferQueue;

/** Deterministic browser contracts for delayed queues, transfers, and barriers. */
public final class AdditionalQueuesTest {
  private static final int ILLEGAL_STATE = 1;
  private static final int NULL_POINTER = 2;
  private static final int ILLEGAL_ARGUMENT = 3;
  private static final int UNSUPPORTED = 4;
  private static final int INTERRUPTED = 5;
  private static final int BROKEN_BARRIER = 6;
  private static final int TIMEOUT = 7;
  private static int checks;
  private static long nowNanos;

  private AdditionalQueuesTest() {}

  public static int run() {
    checks = 0;
    try {
      testDelayQueue();
      testDelayQueueIteration();
      testTransferQueue();
      testBarrier();
    } catch (Exception failure) {
      throw new AssertionError(failure);
    }
    return checks;
  }

  private static void testDelayQueue() throws Exception {
    nowNanos = 10;
    DelayedItem first = new DelayedItem("first", 3);
    DelayedItem second = new DelayedItem("second", 7);
    DelayedItem future = new DelayedItem("future", 12);
    DelayQueue<DelayedItem> queue = new DelayQueue<>(Arrays.asList(future, second, first));
    check(queue.size() == 3 && queue.peek() == first, "delay queue orders by expiry");
    check(queue.remainingCapacity() == Integer.MAX_VALUE, "delay queue is unbounded");
    check(queue.poll() == first, "earliest eligible head removed");
    ArrayList<DelayedItem> destination = new ArrayList<>();
    check(queue.drainTo(destination, 1) == 1 && destination.get(0) == second,
        "drain honors priority and limit");
    check(queue.peek() == future && queue.poll() == null && queue.size() == 1,
        "unexpired head is visible but cannot be removed by poll");
    check(queue.poll(1, TimeUnit.DAYS) == null, "timed delay poll fails immediately");
    check(queue.drainTo(destination) == 0, "drain excludes unexpired items");
    expect(ILLEGAL_STATE, queue::take);
    nowNanos = 12;
    check(queue.take() == future && queue.isEmpty(), "clock advancement makes head eligible");
    expect(ILLEGAL_STATE, queue::take);
    expect(NULL_POINTER, () -> queue.offer(null));
    expect(NULL_POINTER, () -> queue.poll(0, null));
    expect(ILLEGAL_ARGUMENT, () -> queue.drainTo(queue));
    expect(NULL_POINTER, () -> new DelayQueue<DelayedItem>(null));

    Thread.currentThread().interrupt();
    queue.put(first);
    check(Thread.interrupted() && queue.peek() == first, "unbounded put preserves interruption");
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, queue::take);
    check(!Thread.currentThread().isInterrupted() && queue.peek() == first,
        "interrupted take preserves eligible data");
    expect(UNSUPPORTED, () -> queue.drainTo(Collections.emptyList()));
    check(queue.peek() == first, "failed drain retains head");
    check(queue.drainTo(destination, -1) == 0, "negative drain limit does nothing");
    queue.clear();
    check(queue.isEmpty(), "clear removes all delays");
  }

  private static void testDelayQueueIteration() {
    nowNanos = 0;
    DelayedItem early = new DelayedItem("equal", 10);
    DelayedItem late = new DelayedItem("equal", 20);
    DelayQueue<DelayedItem> queue = new DelayQueue<>(Arrays.asList(early, late));
    Iterator<DelayedItem> iterator = queue.iterator();
    expect(ILLEGAL_STATE, iterator::remove);
    DelayedItem kept = iterator.next();
    DelayedItem removed = iterator.next();
    check(kept.equals(removed) && kept != removed, "identity removal setup uses equal objects");
    iterator.remove();
    check(queue.size() == 1 && queue.peek() == kept, "iterator removes returned identity");
    expect(ILLEGAL_STATE, iterator::remove);
    Iterator<DelayedItem> snapshot = queue.iterator();
    queue.offer(new DelayedItem("new", 1));
    check(snapshot.next() == kept && !snapshot.hasNext(), "delay queue iterator remains a snapshot");
    check(queue.remove(kept) && queue.size() == 1, "explicit removal may remove unexpired data");
  }

  private static void testTransferQueue() throws Exception {
    LinkedTransferQueue<String> concrete = new LinkedTransferQueue<>(Arrays.asList("a", "b"));
    TransferQueue<String> queue = concrete;
    check(queue.remainingCapacity() == Integer.MAX_VALUE && queue.size() == 2,
        "transfer queue remains unbounded with stored elements");
    check(!queue.hasWaitingConsumer() && queue.getWaitingConsumerCount() == 0,
        "no consumers are blocked on the main thread");
    check(!queue.tryTransfer("no enqueue") && queue.size() == 2, "failed transfer does not enqueue");
    check(!queue.tryTransfer("timed", 1, TimeUnit.DAYS) && queue.size() == 2,
        "timed transfer fails without enqueueing");
    expect(ILLEGAL_STATE, () -> queue.transfer("blocked"));
    check(queue.size() == 2, "blocking transfer failure preserves queue");
    check(queue.take().equals("a") && queue.poll().equals("b"), "ordinary FIFO operations execute");
    expect(ILLEGAL_STATE, queue::take);
    check(queue.poll(1, TimeUnit.DAYS) == null, "empty timed take returns immediately");
    expect(NULL_POINTER, () -> queue.tryTransfer(null));
    expect(NULL_POINTER, () -> queue.transfer(null));
    expect(NULL_POINTER, () -> queue.tryTransfer("x", 0, null));
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, () -> queue.transfer("interrupted"));
    check(!Thread.currentThread().isInterrupted() && queue.isEmpty(), "transfer consumes interruption");
    Thread.currentThread().interrupt();
    concrete.put("put");
    concrete.offer("offer", 1, TimeUnit.DAYS);
    check(Thread.interrupted() && queue.size() == 2, "unbounded insertions preserve interruption");
    ArrayList<String> drained = new ArrayList<>();
    check(queue.drainTo(drained) == 2 && drained.equals(Arrays.asList("put", "offer")),
        "transfer queue drains ordinary stored work");
  }

  private static void testBarrier() throws Exception {
    expect(ILLEGAL_ARGUMENT, () -> new CyclicBarrier(0));
    int[] actions = {0};
    CyclicBarrier one = new CyclicBarrier(1, () -> actions[0]++);
    check(one.getParties() == 1 && one.getNumberWaiting() == 0, "one-party barrier initial state");
    check(one.await() == 0 && one.await(0, TimeUnit.NANOSECONDS) == 0 && actions[0] == 2,
        "one-party barrier action runs every generation");
    check(!one.isBroken() && one.getNumberWaiting() == 0, "successful arrival leaves no waiter");
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, one::await);
    check(one.isBroken() && !Thread.currentThread().isInterrupted() && actions[0] == 2,
        "interrupt breaks generation before action");
    expect(BROKEN_BARRIER, one::await);
    one.reset();
    check(!one.isBroken() && one.await() == 0 && actions[0] == 3, "reset enables next generation");

    CyclicBarrier many = new CyclicBarrier(2, () -> actions[0]++);
    expect(ILLEGAL_STATE, many::await);
    check(many.isBroken() && many.getNumberWaiting() == 0 && actions[0] == 3,
        "unavailable parties break barrier without executing action");
    expect(BROKEN_BARRIER, () -> many.await(1, TimeUnit.SECONDS));
    many.reset();
    expect(TIMEOUT, () -> many.await(1, TimeUnit.DAYS));
    check(many.isBroken(), "failed timed arrival breaks generation");
    many.reset();
    expect(NULL_POINTER, () -> many.await(1, null));
    check(!many.isBroken(), "invalid time unit does not consume arrival");

    CyclicBarrier failedAction = new CyclicBarrier(1, () -> {
      throw new IllegalArgumentException("barrier action failed");
    });
    expect(ILLEGAL_ARGUMENT, failedAction::await);
    check(failedAction.isBroken() && failedAction.getNumberWaiting() == 0,
        "action failure breaks barrier and clears waiter");
    expect(BROKEN_BARRIER, failedAction::await);

    CyclicBarrier[] recursive = new CyclicBarrier[1];
    recursive[0] = new CyclicBarrier(1, () -> {
      try {
        recursive[0].await();
      } catch (InterruptedException | BrokenBarrierException failure) {
        throw new AssertionError(failure);
      }
    });
    expect(ILLEGAL_STATE, recursive[0]::await);
    check(recursive[0].isBroken(), "recursive action fails instead of recursing forever");
    check(new BrokenBarrierException("message").getMessage().equals("message"),
        "broken barrier exception preserves message");
  }

  private static final class DelayedItem implements Delayed {
    private final String name;
    private final long deadlineNanos;

    DelayedItem(String name, long deadlineNanos) {
      this.name = name;
      this.deadlineNanos = deadlineNanos;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      return unit.convert(deadlineNanos - nowNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
      return Long.compare(deadlineNanos, ((DelayedItem) other).deadlineNanos);
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof DelayedItem && name.equals(((DelayedItem) object).name);
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }
  }

  private static void check(boolean condition, String message) {
    checks++;
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private static void expect(int kind, CheckedAction action) {
    Throwable failure = null;
    try {
      action.run();
    } catch (Throwable thrown) {
      failure = thrown;
    }
    boolean matches;
    switch (kind) {
      case ILLEGAL_STATE:
        matches = failure instanceof IllegalStateException;
        break;
      case NULL_POINTER:
        matches = failure instanceof NullPointerException;
        break;
      case ILLEGAL_ARGUMENT:
        matches = failure instanceof IllegalArgumentException;
        break;
      case UNSUPPORTED:
        matches = failure instanceof UnsupportedOperationException;
        break;
      case INTERRUPTED:
        matches = failure instanceof InterruptedException;
        break;
      case BROKEN_BARRIER:
        matches = failure instanceof BrokenBarrierException;
        break;
      case TIMEOUT:
        matches = failure instanceof TimeoutException;
        break;
      default:
        throw new AssertionError("Unknown expected exception");
    }
    check(matches, "Expected exception kind " + kind + ", got " + failure);
  }

  private interface CheckedAction {
    void run() throws Exception;
  }
}
