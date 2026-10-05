package org.javaconcurrentj2cl;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Runs in the same optimized J2CL consumer as the executor contract tests. */
public final class CollectionsLocksTest {
  private static final int ILLEGAL_STATE = 1;
  private static final int NULL_POINTER = 2;
  private static final int ILLEGAL_ARGUMENT = 3;
  private static final int UNSUPPORTED = 4;
  private static final int MONITOR = 5;
  private static final int CONCURRENT_MODIFICATION = 6;
  private static final int INTERRUPTED = 7;
  private static int checks;

  private CollectionsLocksTest() {}

  public static int run() {
    checks = 0;
    try {
      testLocks();
      testReadWriteLocks();
      testPermitAccounting();
      testCopyOnWriteCollections();
      testConcurrentQueues();
      testBlockingQueue(new LinkedBlockingQueue<>(2));
      testBlockingQueue(new ArrayBlockingQueue<>(2, true));
      testQueueConstructorsAndDrainFailure();
    } catch (Exception exception) {
      throw new AssertionError(exception);
    }
    return checks;
  }

  private static void testLocks() throws Exception {
    ReentrantLock lock = new ReentrantLock(true);
    check(lock.isFair() && !lock.isLocked(), "new fair lock is unlocked");
    expect(MONITOR, lock::unlock);
    lock.lock();
    check(lock.isHeldByCurrentThread() && lock.getHoldCount() == 1, "lock ownership");
    check(lock.tryLock(0, TimeUnit.NANOSECONDS), "reentrant timed lock");
    check(lock.getHoldCount() == 2, "nested holds");
    Condition condition = lock.newCondition();
    expect(ILLEGAL_STATE, condition::await);
    check(lock.getHoldCount() == 2, "failed await retains nested ownership");
    check(!condition.await(1, TimeUnit.DAYS), "timed condition cannot wait");
    check(condition.awaitNanos(50) <= 0, "timed nanos expires");
    condition.signal();
    condition.signalAll();
    check(!lock.hasWaiters(condition) && lock.getWaitQueueLength(condition) == 0,
        "no blocked condition waiters");
    expect(ILLEGAL_ARGUMENT, () -> lock.hasWaiters(new ReentrantLock().newCondition()));
    expect(NULL_POINTER, () -> lock.tryLock(0, null));
    lock.unlock();
    lock.unlock();
    check(!lock.isLocked() && lock.getHoldCount() == 0, "balanced unlocks");
    expect(MONITOR, condition::signal);
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, lock::lockInterruptibly);
    check(!Thread.currentThread().isInterrupted() && !lock.isLocked(), "interrupt is cleared");

    LockSupport.unpark(Thread.currentThread());
    LockSupport.unpark(Thread.currentThread());
    LockSupport.park();
    expect(ILLEGAL_STATE, LockSupport::park);
    LockSupport.parkNanos(1);
    Object blocker = new Object();
    LockSupport.setCurrentBlocker(blocker);
    check(LockSupport.getBlocker(Thread.currentThread()) == blocker, "explicit blocker");
    LockSupport.setCurrentBlocker(null);
    Thread.currentThread().interrupt();
    LockSupport.park();
    check(Thread.interrupted(), "park preserves interruption");
  }

  private static void testReadWriteLocks() {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    check(lock.getReadHoldCount() == 0 && !lock.isWriteLockedByCurrentThread(),
        "MPS ModelAccess starts without access");
    lock.readLock().lock();
    lock.readLock().lock();
    check(lock.getReadHoldCount() == 2 && lock.getReadLockCount() == 2,
        "MPS canRead sees nested read actions");
    check(!lock.writeLock().tryLock(), "read-to-write upgrade cannot succeed");
    expect(ILLEGAL_STATE, () -> lock.writeLock().lock());
    check(!lock.isWriteLocked(), "failed upgrade does not acquire write lock");
    lock.readLock().unlock();
    lock.readLock().unlock();
    expect(MONITOR, () -> lock.readLock().unlock());
    lock.writeLock().lock();
    lock.writeLock().lock();
    check(lock.isWriteLockedByCurrentThread() && lock.getWriteHoldCount() == 2,
        "MPS canWrite sees nested write actions");
    lock.readLock().lock();
    check(lock.getReadHoldCount() == 1 && lock.writeLock().getHoldCount() == 2,
        "writer may enter read action");
    Condition condition = lock.writeLock().newCondition();
    condition.signal();
    check(!lock.hasWaiters(condition), "write condition belongs to read-write lock");
    lock.writeLock().unlock();
    lock.writeLock().unlock();
    check(lock.getReadHoldCount() == 1 && !lock.isWriteLocked(), "write-to-read downgrade");
    lock.readLock().unlock();
    expect(UNSUPPORTED, () -> lock.readLock().newCondition());
  }

  private static void testPermitAccounting() throws Exception {
    CountDownLatch latch = new CountDownLatch(2);
    expect(ILLEGAL_STATE, latch::await);
    check(!latch.await(1, TimeUnit.SECONDS), "pending latch timed await");
    latch.countDown();
    check(latch.getCount() == 1, "count down once");
    latch.countDown();
    latch.countDown();
    latch.await();
    check(latch.getCount() == 0 && latch.await(0, TimeUnit.SECONDS), "open latch stays open");
    expect(ILLEGAL_ARGUMENT, () -> new CountDownLatch(-1));
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, latch::await);
    check(!Thread.currentThread().isInterrupted(), "latch consumes interruption");

    Semaphore semaphore = new Semaphore(2, true);
    semaphore.acquire();
    semaphore.acquireUninterruptibly();
    check(semaphore.availablePermits() == 0 && semaphore.isFair(), "permit accounting");
    expect(ILLEGAL_STATE, semaphore::acquire);
    check(!semaphore.tryAcquire(1, 1, TimeUnit.DAYS), "timed permit failure");
    semaphore.release(3);
    semaphore.acquire(2);
    check(semaphore.drainPermits() == 1 && semaphore.availablePermits() == 0, "permit drain");
    expect(ILLEGAL_ARGUMENT, () -> semaphore.release(-1));
    expect(ILLEGAL_ARGUMENT, () -> semaphore.acquire(-1));
    Semaphore deficit = new Semaphore(-2);
    deficit.release();
    check(!deficit.tryAcquire(0), "negative permits do not allow acquisition");
    check(deficit.drainPermits() == -1 && deficit.tryAcquire(0), "negative permit drain");
    semaphore.release();
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, semaphore::acquire);
    check(semaphore.availablePermits() == 1 && !Thread.currentThread().isInterrupted(),
        "interrupted acquisition leaves permit");
  }

  private static void testCopyOnWriteCollections() {
    CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(new String[] {"a", "b"});
    Iterator<String> before = list.iterator();
    Spliterator<String> beforeStream = list.spliterator();
    list.set(0, "changed");
    list.add("c");
    check(before.next().equals("a") && before.next().equals("b") && !before.hasNext(),
        "copy-on-write iterator keeps old values and size");
    ArrayList<String> streamValues = new ArrayList<>();
    beforeStream.forEachRemaining(streamValues::add);
    check(streamValues.equals(Arrays.asList("a", "b")), "stream traversal keeps creation snapshot");
    expect(UNSUPPORTED, before::remove);
    ListIterator<String> immutable = list.listIterator();
    immutable.next();
    expect(UNSUPPORTED, () -> immutable.set("x"));
    expect(UNSUPPORTED, () -> immutable.add("x"));
    check(!list.addIfAbsent("b") && list.addIfAbsent(null), "absent insert and null support");
    check(list.addAllAbsent(Arrays.asList("c", "d", "d", null)) == 1,
        "bulk absent insert deduplicates");
    check(list.indexOf("d", 2) == 4 && list.lastIndexOf(null, 4) == 3,
        "indexed searches");
    Object[] copy = list.toArray();
    copy[0] = "external";
    check(list.get(0).equals("changed"), "toArray is detached");
    List<String> view = list.subList(1, 3);
    Iterator<String> viewSnapshot = view.iterator();
    view.set(0, "B");
    view.add("C");
    check(list.equals(Arrays.asList("changed", "B", "c", "C", null, "d")),
        "sublist writes through");
    check(viewSnapshot.next().equals("b") && viewSnapshot.next().equals("c")
        && !viewSnapshot.hasNext(), "sublist iterator snapshot");
    check(view.removeIf(value -> value.equals("c")) && view.size() == 2,
        "sublist bulk removal updates bounds");
    view.replaceAll(String::toLowerCase);
    check(view.equals(Arrays.asList("b", "c")), "sublist replaceAll works despite immutable iterators");
    list.add("outside");
    expect(CONCURRENT_MODIFICATION, view::size);
    check(list.removeIf(value -> value == null), "bulk remove supports null");
    list.sort(String::compareTo);
    check(list.get(0).equals("b"), "copy-on-write sorting");
    check(list.removeAll(Arrays.asList("b", "c")), "bulk removeAll does not use iterator mutation");
    check(list.retainAll(Collections.singleton("d")) && list.equals(Collections.singletonList("d")),
        "bulk retainAll");
    CopyOnWriteArrayList<String> reentrant = new CopyOnWriteArrayList<>(new String[] {"a"});
    expect(CONCURRENT_MODIFICATION, () -> reentrant.removeIf(value -> {
      reentrant.add("side effect");
      return true;
    }));
    check(reentrant.size() == 2, "reentrant callback mutation is not silently overwritten");

    CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>(Arrays.asList("a", "a", null));
    Iterator<String> oldSet = set.iterator();
    Spliterator<String> oldSetStream = set.spliterator();
    check(set.size() == 2 && !set.add("a") && set.add("b"), "set uniqueness and null");
    set.remove("a");
    check(oldSet.next().equals("a") && oldSet.next() == null && !oldSet.hasNext(),
        "set iterator snapshot");
    ArrayList<String> setStreamValues = new ArrayList<>();
    oldSetStream.forEachRemaining(setStreamValues::add);
    check(setStreamValues.equals(Arrays.asList("a", null)), "set spliterator creation snapshot");
    check(set.removeIf(value -> value == null) && set.equals(Collections.singleton("b")),
        "set bulk removal");
  }

  private static void testConcurrentQueues() {
    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>(Arrays.asList("a", "b"));
    Iterator<String> iterator = queue.iterator();
    check(iterator.next().equals("a"), "queue iterator order");
    queue.add("c");
    queue.poll();
    iterator.remove();
    check(queue.size() == 2 && queue.poll().equals("b") && queue.poll().equals("c"),
        "iterator remove after external removal is harmless");
    check(queue.poll() == null && queue.peek() == null, "empty queue");
    expect(NULL_POINTER, () -> queue.offer(null));
    expect(ILLEGAL_ARGUMENT, () -> queue.addAll(queue));
    queue.add("unchanged");
    expect(NULL_POINTER, () -> queue.addAll(Arrays.asList("x", null)));
    check(queue.size() == 1, "invalid batch is rejected before linking");

    ConcurrentLinkedDeque<String> deque = new ConcurrentLinkedDeque<>();
    deque.addFirst("b");
    deque.addFirst("a");
    deque.addLast("b");
    check(deque.removeLastOccurrence("b") && deque.size() == 2,
        "last occurrence is removed");
    Iterator<String> reverse = deque.descendingIterator();
    check(reverse.next().equals("b"), "descending order");
    reverse.remove();
    check(deque.getFirst().equals("a") && deque.getLast().equals("a"), "descending remove");
    deque.push("top");
    check(deque.pop().equals("top") && deque.removeLast().equals("a"), "stack and tail operations");
    check(deque.isEmpty() && !deque.remove(null), "empty deque and null removal");
    expect(NULL_POINTER, () -> deque.addFirst(null));

    ConcurrentLinkedDeque<String> traversal = new ConcurrentLinkedDeque<>(Arrays.asList("a", "b", "c"));
    Iterator<String> live = traversal.iterator();
    check(live.next().equals("a"), "live traversal begins");
    traversal.remove("b");
    check(live.next().equals("c") && !live.hasNext(), "iterator skips removed next node");
    live.remove();
    check(traversal.size() == 1, "iterator removes exactly returned node");
  }

  private static void testBlockingQueue(BlockingQueue<String> queue) throws Exception {
    check(queue.remainingCapacity() == 2, "bounded capacity");
    queue.put("a");
    check(queue.offer("b", 1, TimeUnit.SECONDS), "timed offer with capacity");
    check(!queue.offer("c") && !queue.offer("c", 1, TimeUnit.DAYS), "full queue offer fails");
    expect(ILLEGAL_STATE, () -> queue.put("c"));
    expect(NULL_POINTER, () -> queue.offer(null));
    check(queue.take().equals("a") && queue.poll(0, TimeUnit.SECONDS).equals("b"), "blocking FIFO");
    expect(ILLEGAL_STATE, queue::take);
    check(queue.poll(1, TimeUnit.DAYS) == null, "timed empty poll returns immediately");
    queue.addAll(Arrays.asList("first", "second"));
    ArrayList<String> destination = new ArrayList<>();
    check(queue.drainTo(destination, 1) == 1 && destination.equals(Collections.singletonList("first")),
        "bounded drain preserves order");
    check(queue.peek().equals("second") && queue.remainingCapacity() == 1, "partial drain state");
    expect(ILLEGAL_ARGUMENT, () -> queue.drainTo(queue));
    Thread.currentThread().interrupt();
    expect(INTERRUPTED, queue::take);
    check(queue.size() == 1 && !Thread.currentThread().isInterrupted(), "interrupted take preserves data");
    queue.clear();
    check(queue.remainingCapacity() == 2, "clear restores capacity");
  }

  private static void testQueueConstructorsAndDrainFailure() {
    expect(ILLEGAL_ARGUMENT, () -> new LinkedBlockingQueue<>(0));
    expect(ILLEGAL_ARGUMENT, () -> new ArrayBlockingQueue<String>(-1));
    expect(ILLEGAL_ARGUMENT, () -> new ArrayBlockingQueue<>(1, false, Arrays.asList("a", "b")));
    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(Arrays.asList("a", "b"));
    check(queue.remainingCapacity() == Integer.MAX_VALUE - 2, "unbounded constructor capacity");
    AbstractCollection<String> rejecting = new AbstractCollection<String>() {
      @Override
      public Iterator<String> iterator() {
        return Collections.<String>emptyList().iterator();
      }

      @Override
      public int size() {
        return 0;
      }

      @Override
      public boolean add(String element) {
        throw new IllegalArgumentException("Destination rejects the element");
      }
    };
    expect(ILLEGAL_ARGUMENT, () -> queue.drainTo(rejecting));
    check(queue.size() == 2 && queue.peek().equals("a"), "failed drain retains source item");
    check(queue.drainTo(new ArrayList<>(), -1) == 0 && queue.size() == 2,
        "negative drain limit does nothing");
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
      case MONITOR:
        matches = failure instanceof IllegalMonitorStateException;
        break;
      case CONCURRENT_MODIFICATION:
        matches = failure instanceof ConcurrentModificationException;
        break;
      case INTERRUPTED:
        matches = failure instanceof InterruptedException;
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
