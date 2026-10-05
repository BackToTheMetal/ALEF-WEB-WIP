package jetbrains.mps.smodel;

import com.intellij.openapi.progress.ProcessCanceledException;
import org.jetbrains.mps.openapi.repository.ReadActionListener;
import org.jetbrains.mps.openapi.repository.WriteActionListener;

/** Behavioral checks for the browser model-access and action-dispatch replacements. */
public final class SModelAdaptationTest {
  private static volatile boolean asyncDone;
  private static volatile Throwable asyncFailure;
  private static volatile String asyncResult;

  private SModelAdaptationTest() {
  }

  public static String run() {
    ActionDispatcherFailureTest.run();
    StringBuilder events = new StringBuilder();
    ActionDispatcher<String> dispatcher = new ActionDispatcher<>("outer",
        listener -> events.append('+').append(listener).append('|'),
        listener -> events.append('-').append(listener).append('|'));
    dispatcher.addActionListener("first");
    dispatcher.addActionListener("second");
    dispatcher.dispatch(() -> {
      require(dispatcher.isInsideAction(), "action phase");
      events.append("action|");
      dispatcher.addActionListener("later");
    });
    require("+outer|+first|+second|action|-second|-first|-outer|".equals(events.toString()),
        "listener snapshot and reverse finish order");
    require(!dispatcher.isInsideAction() && !dispatcher.isInsideNotificationDispatch(), "ready after action");

    events.setLength(0);
    RuntimeException cancellation = new Cancelled();
    require(ActionDispatcher.isControlFlowIDEA(cancellation), "platform cancellation subclasses recognized");
    require(!ActionDispatcher.isControlFlowIDEA(new RuntimeException()), "ordinary exception is not cancellation");
    try {
      dispatcher.dispatch(() -> {
        events.append("cancel|");
        throw cancellation;
      });
      throw new AssertionError("Cancellation was swallowed");
    } catch (RuntimeException actual) {
      require(actual == cancellation, "cancellation exception identity");
    }
    require("+outer|+first|+second|+later|cancel|-later|-second|-first|-outer|".equals(events.toString()),
        "cancellation retains finish notifications and order");
    require(!dispatcher.isInsideAction() && !dispatcher.isInsideNotificationDispatch(), "ready after cancellation");
    return "smodel-dispatch: snapshot, reverse finish order, cancellation identity, restored state";
  }

  /** Browser harness polls isAsyncDone()/getAsyncResult(); no blocking browser waits. */
  public static void startAsync() {
    asyncDone = false;
    asyncFailure = null;
    asyncResult = null;
    ModelAccess access = ModelAccess.newInstance();
    StringBuilder events = new StringBuilder();
    int[] writes = {0};
    access.addReadActionListener(new ReadActionListener() {
      @Override public void readStarted() {
        checkAsync(access.canRead(), "read-start lock");
        events.append("r+|");
      }
      @Override public void readFinished() {
        checkAsync(access.canRead(), "read-finish lock");
        events.append("r-|");
      }
    });
    access.addWriteActionListener(new WriteActionListener() {
      @Override public void actionStarted() {
        checkAsync(access.canWrite(), "write-start lock");
        writes[0]++;
        events.append("w+|");
      }
      @Override public void actionFinished() {
        checkAsync(access.canWrite(), "write-finish lock");
        events.append("w-|");
        if (writes[0] == 2) {
          checkAsync("w+|submit|w-|r+|read|r-|w+|write|w-|".equals(events.toString()),
              "queued action and notification order: " + events);
          asyncResult = "smodel-async: deferred FIFO read/write, lock-protected notifications";
          asyncDone = true;
        }
      }
    });
    try {
      access.runWriteAction(() -> {
        events.append("submit|");
        access.runReadInEDT(() -> {
          checkAsync(access.canRead() && !access.canWrite(), "queued read lock");
          events.append("read|");
        });
        access.runWriteInEDT(() -> {
          checkAsync(access.canRead() && access.canWrite(), "queued write lock");
          events.append("write|");
        });
        checkAsync("w+|submit|".equals(events.toString()), "queued callbacks did not execute inline");
      });
    } catch (Throwable failure) {
      asyncFailure = failure;
      asyncDone = true;
    }
  }

  public static boolean isAsyncDone() {
    return asyncDone;
  }

  public static String getAsyncResult() {
    if (asyncFailure != null) {
      throw new AssertionError("Asynchronous model access failed", asyncFailure);
    }
    require(asyncDone, "asynchronous model access completed");
    return asyncResult;
  }

  private static void checkAsync(boolean condition, String message) {
    if (!condition && asyncFailure == null) {
      asyncFailure = new AssertionError(message);
      asyncDone = true;
    }
  }

  private static void require(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private static final class Cancelled extends ProcessCanceledException {
  }
}
