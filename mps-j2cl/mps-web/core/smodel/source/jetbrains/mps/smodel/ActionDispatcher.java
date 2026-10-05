/*
 * Copyright 2000-2026 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package jetbrains.mps.smodel;

import jetbrains.mps.RuntimeFlags;
import jetbrains.mps.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Thread-safe, but not-reentrant action execution mechanism, with notification dispatch on first and last action.
 * Maintains inner state to tell different execution phases like event notification and actual action execution.
 * Not reentrant means one shall not get into {@code dispatch()} when the same thread is {@link #isInsideNotificationDispatch()}
 * <br/>
 * Use {@link #dispatch(Runnable)} to execute {@link Runnable action} with proper event dispatching.
 * If you need to postpone execution (and event dispatching), you can get appropriate runnable with {@link #wrap(Runnable)}.
 * <br/>
 * {@code onActionStart} is notified prior to first action of a first thread that asked for {@code dispatch} and
 * {@code onActionFinish} is notified after the last {@code dispatch} is over.
 * <br/>
 * Actions executed from threads other than the one dispatching pre/post notifications wait for notifications to complete
 * and then make their decision whether there's a need for new round of notifications. In other words, two actions started from
 * two different thread may result in single pair of {@code onActionStart}/{@code onActionFinish} or in two independent pairs of
 * notifications. Besides, in first case there's no guarantee it would be the same thread to dispatch {@code onActionFinish} as the one
 * that fired {@code onActionStart}.
 *
 * @param <T> listener to notify
 *
 * @author Artem Tikhomirov
 * @since 2017.3
 */
/*package*/ final class ActionDispatcher<T> {
  private final Logger LOG = Logger.getLogger(ActionDispatcher.class);
  private final List<T> myListeners = new CopyOnWriteArrayList<>();
  private final Consumer<T> myOnActionStart;
  private final Consumer<T> myOnActionFinish;
  private final T myPrePostListener;
  // number of "active" clients of this dispatcher. First client to run fires {@code onActionStart},
  // last client to leave fires {@code onActionFinish}.
  private final AtomicInteger myActiveThreads = new AtomicInteger(0);
  // fine-grained phase transition.
  private final AtomicReference<State> myState = new AtomicReference<>(State.READY);
  // guards myState transitions (iow, notification dispatch phase)
  // impl note: I could have picked synchronized() approach instead, makes it easier to handle exceptions (i.e. monitor release).
  //    picked semaphore as I want to have a time limit for lock acquisition, which may come handy e.g. when
  //    same thread gets into dispatch() more than once. Besides, synchronized block could be re-entered for the same thread,
  //    which on one hand may help to avoid deadlocks, on the other, we may not enforce semantics "no action before notification
  //    dispatch is over" then.
  private final Semaphore myNotifyGuard = new Semaphore(1);

  // all arguments are non-null
  public ActionDispatcher(Consumer<T> onActionStart, Consumer<T> onActionFinish) {
    myPrePostListener = null;
    myOnActionStart = onActionStart;
    myOnActionFinish = onActionFinish;
  }

  // @param prePostListener optional T to get pre and post notification around action dispatch for the listeners.
  //        receives same onActionStart/onActionFinish events as other listeners, the difference is that
  //        prePostListener is guaranteed to be invoked as first and last.
  //        Note, now that there's a reversed order of onActionStart/onActionFinish event dispatch, any regular listener
  //        added early in the construction process is pretty much the same as prePostListener.
  // all arguments are non-null
  public ActionDispatcher(T prePostListener, Consumer<T> onActionStart, Consumer<T> onActionFinish) {
    myPrePostListener = prePostListener;
    myOnActionStart = onActionStart;
    myOnActionFinish = onActionFinish;
  }

  /**
   * Execute an action with notification about action started/finished dispatching for top-most action.
   * @param r action to execute
   */
  public void dispatch(Runnable r) {
    InterruptedException interruptedException = null;
    Throwable failure = null;
    // we dispatch events over a copy of the list, and deliberately ignore any listeners added during the command.
    final ArrayList<T> toNotify = new ArrayList<>(myListeners);
    try {
      onEnter(toNotify);
      try {
        r.run();
      } catch (RuntimeException ex) {
        // re-throw an exception, if any, just to let user code to use exceptions to control code flow (alas, we can't prevent this unfortunate practice)
        // but at least mention it in the log in case it's not a control flow and there are chances for the exception to get obscured by a subsequent one.
        logUnexpectedRuntimeException(ex);
        throw ex;
      }
    } catch (InterruptedException ex) {
      // XXX not quite nice, but could not come up with a better mechanism to skip onExit() when onEnter() fails
      //     to grab a lock, and there's no reason for onExit() to attempt to modify inner state (like decrement counter),
      //     which may lead to further inconsistencies.
      //     I assume there's no guarantee that attempt to grab a lock onExit() would fail too if it failed for onEnter().
      interruptedException = ex;
    } catch (RuntimeException | Error ex) {
      failure = ex;
    } finally {
      if (interruptedException == null) {
        try {
          // dispatch on a reversed list to resemble an order of start notification, in case there are some interdependencies b/w different listeners.
          // Also helps clients that receive MA early to establish a listener that they may expect to be invoked (close to) first/last, e.g. repository
          // that re-dispatches MA's CommandListener events as its own SRepositoryListener's.
          final ArrayList<T> finishing = new ArrayList<>(toNotify);
          Collections.reverse(finishing);
          onExit(finishing);
        } catch (InterruptedException ex) {
          // FIXME here we are likely in an invalid state (onEntry succeed, onExit didn't grab myNotifyGuard),
          //       although no idea how to bring it back to 'normal', seems that it's not possible right here - there
          //       could be other threads and we can not reset the state right now.
          //       Perhaps, another State could be introduced, and checked as a first thing in onEntry() under myNotifyGuard,
          //       where we can try to start all over again with a blank page.
          interruptedException = ex;
        } catch (RuntimeException | Error ex) {
          failure = addFailure(failure, ex);
        }
      }
    }
    if (interruptedException != null) {
      failure = addFailure(failure, new RuntimeException(interruptedException));
    }
    rethrow(failure);
  }

  /**
   * @param r original action to {@linkplain #dispatch(Runnable) dispatch}
   * @return a runnable, which, when executed, will {@linkplain #dispatch(Runnable) dispatch} original action.
   */
  public Runnable wrap(final Runnable r) {
    return () -> dispatch(r);
  }

  /*package*/ static boolean isControlFlowIDEA(RuntimeException ex) {
    // Browser builds have no IntelliJ marker-interface reflection. Recognize the
    // linked platform cancellation types by name, including their subclasses.
    // Other exceptions still propagate unchanged and retain ordinary error logging.
    for (Class<?> cls = ex.getClass(); cls != null; cls = cls.getSuperclass()) {
      String name = cls.getName();
      if ("com.intellij.openapi.actionSystem.impl.AwaitSharedData".equals(name)
          || "com.intellij.openapi.progress.ProcessCanceledException".equals(name)) {
        return true;
      }
    }
    return false;
  }

  private void logUnexpectedRuntimeException(RuntimeException ex) {
    if (isControlFlowIDEA(ex)) {
      // don't treat IDEA's control flow exceptions as errors. We can do nothing about IDEA's approach to use RuntimeException implements ControlFlowException
      // but at least shall not log it as an error to avoid perception something's wrong with MPS>
      if (RuntimeFlags.isInternalMode() || LOG.isDebugLevel()) {
        final String msg = formatMessageDetails("Action dispatch cancelled with control flow exception");
        if (RuntimeFlags.isInternalMode()) {
          LOG.warning(msg, ex);
        } else {
          LOG.debug(msg, ex);
        }
      }
      return;
    }
    // we need this catch not to obscure errors during run with errors from finally block
    // (e.g. if both onActionStarted and onActionFinished fail with exception, withou this code one would
    // see only the last one from onActionFinished)
    LOG.error(formatMessageDetails("Action dispatch failed"), ex);
  }

  private String formatMessageDetails(String msg) {
    final String threadName = Thread.currentThread().getName();
    final State actualState = myState.get();
    return msg + ". Thread " + threadName + ", state " + actualState + ", " + myActiveThreads.get() + " active clients.";
  }

  private void onEnter(final List<T> toNotify) throws InterruptedException {
    if (!myNotifyGuard.tryAcquire(3, TimeUnit.MINUTES)) {
      throw new InterruptedException(formatMessageDetails("Deadlock prevention. Notify 'start' takes too long"));
    }
    final int cnt = myActiveThreads.getAndIncrement();
    try {
      if (cnt == 0) {
        try {
          // in case any listener fails, shall get into respective finally with onExit() to ensure myActiveThreads is correct
          transitState(State.READY, State.NOTIFY_START);
          if (myPrePostListener != null) {
            myOnActionStart.accept(myPrePostListener);
          }
          toNotify.forEach(myOnActionStart);
        } finally {
          // in any case, treat notification complete, and let other threads, if any, continue with an expected 'ACTIVE' state
          transitState(State.NOTIFY_START, State.ACTIVE);
        }
      }
    } catch (RuntimeException ex) {
      logUnexpectedRuntimeException(ex);
      throw ex;
    } finally {
      checkState(State.ACTIVE);
      myNotifyGuard.release();
    }
  }

  private void onExit(final List<T> toNotify) throws InterruptedException {
    if (!myNotifyGuard.tryAcquire(3, TimeUnit.MINUTES)) {
      throw new InterruptedException(formatMessageDetails("Deadlock prevention. Notify 'finish' takes too long"));
    }
    final int cnt = myActiveThreads.decrementAndGet();
    try {
      if (cnt == 0) {
        transitState(State.ACTIVE, State.NOTIFY_FINISH);
        try {
          // caller controls the order of event dispatch
          Throwable failure = null;
          for (T listener : toNotify) {
            try { myOnActionFinish.accept(listener); }
            catch (RuntimeException | Error ex) { failure = addFailure(failure, ex); }
          }
          if (myPrePostListener != null) {
            try { myOnActionFinish.accept(myPrePostListener); }
            catch (RuntimeException | Error ex) { failure = addFailure(failure, ex); }
          }
          rethrow(failure);
        } finally {
          transitState(State.NOTIFY_FINISH, State.READY);
        }
      }
    } catch (RuntimeException ex) {
      logUnexpectedRuntimeException(ex);
      throw ex;
    } finally {
      checkState(cnt == 0 ? State.READY : State.ACTIVE);
      myNotifyGuard.release();
    }
  }

  private void checkState(State expected) {
    final State actual = myState.get();
    if (actual != expected) {
      LOG.info(formatMessageDetails("Expected " + expected + ", actual is " + actual), new Throwable());
    }
  }

  private static Throwable addFailure(Throwable first, Throwable next) {
    if (first == null) return next;
    if (first != next) first.addSuppressed(next);
    return first;
  }

  private static void rethrow(Throwable failure) {
    if (failure instanceof Error) throw (Error) failure;
    if (failure != null) throw (RuntimeException) failure;
  }

  private void transitState(State expected, State desired) {
    checkState(expected);
    // intentionally not compareAndSet(), to deal with potential inconsistencies in case some listener fails with
    // with an exception. It's quite tricky to restore a reasonable state in the case of exception/throwable, and I
    // don't want subsequent execution attempts to fail due to broken state.
    myState.set(desired);
  }

  // not perfect naming, tells whether this dispatcher is in a state where actual client code is executed, not
  // in idle (ready for any new client) nor in 'technological' phase of notification dispatch
  public boolean isInsideAction() {
    return myState.get() == State.ACTIVE;
  }

  public boolean isInsideNotificationDispatch() {
    final State state = myState.get();
    return state == State.NOTIFY_FINISH || state == State.NOTIFY_START;
  }

  /**
   * @param listener not {@code null}, duplicate listeners are not tolerated.
   * @throws ListenersConsistenceException if there's already such listener
   */
  public void addActionListener(T listener) {
    if (myListeners.contains(listener)) {
      throw new ListenersConsistenceException("Adding the same listener again");
    }
    myListeners.add(listener);
    // here, we used to dispatch onActionStart for new listeners coming isInsideAction().
    // now that we dispatch events over a copy of the listener list, there's no way a new listener that received onActionStart
    // here would receive corresponding onActionFinish. Nor I think it's desirable, and the only use of this logic (I'm aware of)
    // inside ClassLoaderManager.init that attaches listeners inside model write and expects to receive 'start' notification
    // (otherwise, internal state of BatchEventsProcessor breaks), is most likely flawed and shall get fixed in another way.
  }

  /**
   * @param listener not {@code null}, listener has to be {@link #addActionListener(Object) registered} beforehand.
   * @throws ListenersConsistenceException if there's no such listener
   */
  public void removeActionListener(T listener) {
    if (!myListeners.contains(listener)) {
      throw new ListenersConsistenceException("The listener you are trying to remove does not exist");
    }
    if (isInsideAction()) {
      myOnActionFinish.accept(listener);
    }
    myListeners.remove(listener);
  }

  public static class ListenersConsistenceException extends RuntimeException {
    ListenersConsistenceException(String msg) {
      super(msg);
    }
  }

  /**
   * <ul>
   * <li>{@code READY} is initial state with no actions being dispatched.
   * <li>{@code NOTIFY_START} when one of the action dispatches {@code onActionStart} notifications. Others (if any) wait.
   *                          {@code READY} --> {@code NOTIFY_START} --> {@code ACTIVE}
   * <li>{@code ACTIVE} one of threads done with {@code onActionStart} dispatch, any thread could go on executing an action.
   * <li>{@code NOTIFY_FINISH} when there are no more actions, the last one dispatches {@code onActionFinish} notifications.
   *                           {@code ACTIVE} --> {@code NOTIFY_FINISH} --> {@code READY}
   *                           If a new action comes during dispatch, it waits for state change and then starts with {@code READY} state.
   * </ul>
   */
  private enum State {
    READY, NOTIFY_START, ACTIVE, NOTIFY_FINISH
  }
}
