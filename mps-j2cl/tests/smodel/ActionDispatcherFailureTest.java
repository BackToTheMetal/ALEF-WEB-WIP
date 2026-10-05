package jetbrains.mps.smodel;

/** Finish failures must not strand the command context or hide the originating failure. */
public final class ActionDispatcherFailureTest {
  public static void run() {
    finishFailures();
    bodyAndFinishFailures();
    startAndFinishFailures();
    repeatedFailureIdentity();
  }

  private static void finishFailures() {
    StringBuilder events = new StringBuilder();
    boolean[] commandActive = {false};
    boolean[] fail = {true};
    AssertionError secondFailure = new AssertionError("second finish");
    IllegalStateException firstFailure = new IllegalStateException("first finish");
    AssertionError cleanupFailure = new AssertionError("mandatory cleanup");
    ActionDispatcher<String> dispatcher = new ActionDispatcher<>("context", listener -> {
      if (listener.equals("context")) { require(!commandActive[0], "previous command context released"); commandActive[0] = true; }
    }, listener -> {
      events.append(listener).append('|');
      if (listener.equals("context")) commandActive[0] = false;
      if (fail[0]) {
        if (listener.equals("second")) throw secondFailure;
        if (listener.equals("first")) throw firstFailure;
        throw cleanupFailure;
      }
    });
    dispatcher.addActionListener("first"); dispatcher.addActionListener("second");
    Throwable actual = failure(() -> dispatcher.dispatch(() -> events.append("body|")));
    require(actual == secondFailure, "first finish failure identity");
    require(actual.getSuppressed().length == 2 && actual.getSuppressed()[0] == firstFailure && actual.getSuppressed()[1] == cleanupFailure,
        "later failures retained in reverse listener then mandatory cleanup order");
    require("body|second|first|context|".equals(events.toString()), "all finish listeners execute in order");
    require(!commandActive[0] && !dispatcher.isInsideAction() && !dispatcher.isInsideNotificationDispatch(), "context and dispatcher released after failed finish");
    fail[0] = false;
    dispatcher.dispatch(() -> require(commandActive[0], "next command enters context"));
    require(!commandActive[0], "next command releases context");
  }

  private static void bodyAndFinishFailures() {
    AssertionError body = new AssertionError("body");
    AssertionError finish = new AssertionError("finish");
    boolean[] cleaned = {false};
    ActionDispatcher<String> dispatcher = new ActionDispatcher<>("context", listener -> {}, listener -> {
      if (listener.equals("context")) cleaned[0] = true;
      else throw finish;
    });
    dispatcher.addActionListener("listener");
    Throwable actual = failure(() -> dispatcher.dispatch(() -> { throw body; }));
    require(actual == body && actual.getSuppressed().length == 1 && actual.getSuppressed()[0] == finish,
        "body failure stays primary when finish fails");
    require(cleaned[0] && !dispatcher.isInsideAction(), "mandatory cleanup after body and finish failures");
  }

  private static void startAndFinishFailures() {
    AssertionError start = new AssertionError("start");
    AssertionError finish = new AssertionError("finish");
    boolean[] cleaned = {false};
    ActionDispatcher<String> dispatcher = new ActionDispatcher<>("context", listener -> {
      if (!listener.equals("context")) throw start;
    }, listener -> {
      if (listener.equals("context")) cleaned[0] = true;
      else throw finish;
    });
    dispatcher.addActionListener("listener");
    Throwable actual = failure(() -> dispatcher.dispatch(() -> { throw new AssertionError("body must not run"); }));
    require(actual == start && actual.getSuppressed()[0] == finish, "start failure stays primary");
    require(cleaned[0] && !dispatcher.isInsideNotificationDispatch(), "mandatory cleanup after start failure");
  }

  private static void repeatedFailureIdentity() {
    AssertionError same = new AssertionError("same failure");
    ActionDispatcher<String> dispatcher = new ActionDispatcher<>(listener -> {}, listener -> { throw same; });
    dispatcher.addActionListener("first"); dispatcher.addActionListener("second");
    Throwable actual = failure(() -> dispatcher.dispatch(() -> { throw same; }));
    require(actual == same && same.getSuppressed().length == 0, "same exception is not suppressed onto itself");
  }

  private static Throwable failure(Runnable action) {
    try { action.run(); } catch (RuntimeException | Error expected) { return expected; }
    throw new AssertionError("Expected failure");
  }
  private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
  public static void main(String[] args) { run(); System.out.println("ActionDispatcher failure cleanup: PASS"); }
}
