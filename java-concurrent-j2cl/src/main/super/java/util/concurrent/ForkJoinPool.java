package java.util.concurrent;

/**
 * Main-thread executor subset used by MPS's background-task scheduler.
 * ForkJoinTask/work-stealing APIs are deliberately outside this profile.
 */
public class ForkJoinPool extends ThreadPoolExecutor {
  private static final ForkJoinPool COMMON = new ForkJoinPool(1);
  public ForkJoinPool() { this(1); }
  public ForkJoinPool(int parallelism) {
    super(parallelism, parallelism, 0, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>());
    if (parallelism > 32767) throw new IllegalArgumentException("parallelism exceeds 32767");
  }
  public static ForkJoinPool commonPool() { return COMMON; }
  public interface ManagedBlocker {
    boolean block() throws InterruptedException;
    boolean isReleasable();
  }
  public static void managedBlock(ManagedBlocker blocker) throws InterruptedException {
    java.util.Objects.requireNonNull(blocker);
    if (Thread.interrupted()) throw new InterruptedException();
    if (!blocker.isReleasable() && !blocker.block()) {
      throw new IllegalStateException("ManagedBlocker cannot wait for another browser thread");
    }
  }
  public static int getCommonPoolParallelism() { return 1; }
  public int getParallelism() { return 1; }
  public boolean getAsyncMode() { return false; }
  public boolean isQuiescent() { return getActiveCount() == 0; }
  public boolean awaitQuiescence(long timeout, TimeUnit unit) {
    java.util.Objects.requireNonNull(unit); return isQuiescent();
  }
  public long getStealCount() { return 0; }
  public long getQueuedTaskCount() { return 0; }
  public int getQueuedSubmissionCount() { return 0; }
  public boolean hasQueuedSubmissions() { return false; }
  public int getRunningThreadCount() { return getActiveCount(); }
  @Override public void shutdown() { if (this != COMMON) super.shutdown(); }
  @Override public java.util.List<Runnable> shutdownNow() {
    return this == COMMON ? java.util.Collections.emptyList() : super.shutdownNow();
  }
}
