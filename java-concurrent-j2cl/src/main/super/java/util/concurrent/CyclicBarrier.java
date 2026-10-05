package java.util.concurrent;

import java.util.Objects;

/**
 * A one-party barrier runs its action inline on each arrival. A multi-party barrier
 * cannot complete synchronously: an untimed arrival fails explicitly, a timed arrival
 * times out immediately, and either failure breaks the generation until reset.
 */
public class CyclicBarrier {
  private final int parties;
  private final Runnable barrierAction;
  private boolean broken;
  private boolean runningAction;

  public CyclicBarrier(int parties) {
    this(parties, null);
  }

  public CyclicBarrier(int parties, Runnable barrierAction) {
    if (parties <= 0) {
      throw new IllegalArgumentException("parties must be positive");
    }
    this.parties = parties;
    this.barrierAction = barrierAction;
  }

  public int getParties() {
    return parties;
  }

  public int await() throws InterruptedException, BrokenBarrierException {
    checkArrival();
    if (parties != 1) {
      broken = true;
      throw new IllegalStateException("CyclicBarrier.await cannot block the browser main thread");
    }
    return runBarrierAction();
  }

  /** Insufficient arrivals time out immediately, irrespective of the supplied duration. */
  public int await(long timeout, TimeUnit unit)
      throws InterruptedException, BrokenBarrierException, TimeoutException {
    Objects.requireNonNull(unit);
    checkArrival();
    if (parties != 1) {
      broken = true;
      throw new TimeoutException("CyclicBarrier cannot await other parties on the browser main thread");
    }
    return runBarrierAction();
  }

  public boolean isBroken() {
    return broken;
  }

  /** Starts a fresh generation; no tasks are blocked or resumed by this operation. */
  public void reset() {
    broken = false;
  }

  public int getNumberWaiting() {
    return runningAction ? 1 : 0;
  }

  private void checkArrival() throws InterruptedException, BrokenBarrierException {
    if (broken) {
      throw new BrokenBarrierException();
    }
    if (Thread.interrupted()) {
      broken = true;
      throw new InterruptedException();
    }
    if (runningAction) {
      broken = true;
      throw new IllegalStateException("A barrier action cannot reenter its own barrier");
    }
  }

  private int runBarrierAction() {
    runningAction = true;
    try {
      if (barrierAction != null) {
        barrierAction.run();
      }
      return 0;
    } catch (RuntimeException | Error failure) {
      broken = true;
      throw failure;
    } finally {
      runningAction = false;
    }
  }
}
