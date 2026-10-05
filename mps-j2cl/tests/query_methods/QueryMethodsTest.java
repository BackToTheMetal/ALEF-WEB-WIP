package mpsj2cl.queries;

import java.lang.reflect.InvocationTargetException;
import jetbrains.mps.util.QueryMethods;
import jetbrains.mps.util.QueryMethods.IllegalQueryMethodException;
import jetbrains.mps.util.QueryMethods.RegisteredMethod;

/** Runs against the same QueryMethods source on the JVM and in optimized JavaScript. */
public final class QueryMethodsTest {
  public static final class Queries {
    public static String text(CharSequence context) { return context == null ? "null" : context.toString() + "!"; }
    public static long number(Long context) { return context + 1; }
  }
  public static final class OtherQueries {}

  private QueryMethodsTest() {}

  public static String run() throws Exception {
    QueryMethods beforeRegistration = new QueryMethods(Queries.class);
    Exception callbackFailure = new Exception("from the query body");
    Error callbackError = new AssertionError("from the query body");
    try (RegisteredMethod<CharSequence, String> text = QueryMethods.register(Queries.class, "text",
             CharSequence.class, String.class, Queries::text);
         RegisteredMethod<Long, Long> number = QueryMethods.register(Queries.class, "number",
             long.class, long.class, Queries::number);
         RegisteredMethod<Object, Object> failure = QueryMethods.register(Queries.class, "failure",
             Object.class, Object.class, context -> { throw callbackFailure; });
         RegisteredMethod<Object, Object> error = QueryMethods.register(Queries.class, "error",
             Object.class, Object.class, context -> { throw callbackError; });
         RegisteredMethod<Object, Void> nothing = QueryMethods.register(Queries.class, "nothing",
             Object.class, void.class, context -> null);
         RegisteredMethod<Object, String> nullString = QueryMethods.register(Queries.class, "nullString",
             Object.class, String.class, context -> null);
         RegisteredMethod<Object, Long> nullPrimitive = QueryMethods.register(Queries.class, "nullPrimitive",
             Object.class, long.class, context -> null)) {
      QueryMethods methods = new QueryMethods(Queries.class);
      require(!beforeRegistration.hasMethod("text"), "snapshot excludes later registrations");
      require(!new QueryMethods(OtherQueries.class).hasMethod("text"), "registry uses actual declaring class identity");
      require(methods.hasMethod("text") && methods.getMethodPrim("text") == text, "named descriptor lookup");
      require(text.getDeclaringClass() == Queries.class && text.getContextType() == CharSequence.class
          && text.getReturnType() == String.class && "text".equals(text.getName()), "explicit descriptor metadata");
      require("hello!".equals(methods.<String>getMethod("text").invoke("hello")), "typed direct callback");
      require("null".equals(methods.<String>getMethod("text").invoke(null)), "null reference context");
      require("ignored receiver!".equals(text.invoke(new Object(), "ignored receiver")), "static receiver is ignored");
      require(methods.<Long>getMethod("number").invoke(Byte.valueOf((byte) 4)) == 5L, "unboxing and widening");
      require(methods.<Long>getMethod("number").invoke(Character.valueOf('A')) == 66L, "character widening");
      require(methods.getMethod("nothing").invoke(new Object()) == null, "void callback result");
      require(methods.getMethod("nullString").invoke(null) == null, "nullable reference result");

      expectInputError(() -> methods.getMethod("text").invoke(Integer.valueOf(1)), IllegalArgumentException.class);
      expectInputError(() -> methods.getMethod("number").invoke(Double.valueOf(2)), IllegalArgumentException.class);
      expectInputError(() -> methods.getMethod("number").invoke(null), IllegalArgumentException.class);
      expectInputError(() -> methods.getMethod("nullPrimitive").invoke(null), IllegalArgumentException.class);
      expectInputError(() -> methods.getMethod("missing").invoke(null), NoSuchMethodException.class);
      require(!methods.hasMethod("missing"), "unknown method is absent");
      expectTargetFailure(() -> methods.getMethod("failure").invoke(null), callbackFailure);
      expectTargetFailure(() -> methods.getMethod("error").invoke(null), callbackError);
      expectDirectInputError(() -> text.invoke(null));
      expectDirectInputError(() -> text.invoke(null, "one", "two"));

      boolean duplicateRejected = false;
      try {
        QueryMethods.register(Queries.class, "text", CharSequence.class, String.class, Queries::text);
      } catch (IllegalStateException expected) {
        duplicateRejected = true;
      }
      require(duplicateRejected, "unique registered method names");
      text.close();
      text.close();
      require(!methods.hasMethod("text") && !text.isActive(), "close invalidates snapshots and is idempotent");
      expectInputError(() -> methods.getMethod("text").invoke("x"), NoSuchMethodException.class);
      expectDirectInputError(() -> text.invoke(null, "x"));
      try (RegisteredMethod<CharSequence, String> replacement = QueryMethods.register(Queries.class, "text",
          CharSequence.class, String.class, context -> "replacement")) {
        text.close();
        require(!methods.hasMethod("text"), "old snapshot cannot acquire a replacement callback");
        require("replacement".equals(new QueryMethods(Queries.class).getMethod("text").invoke("x")),
            "new registration survives closing the previous handle");
      }
    }
    require(!new QueryMethods(Queries.class).hasMethod("number"), "closed registrations are removed");
    return "QueryMethods: typed callbacks, snapshots, lookup/input/target errors, primitive widening, nulls and registration lifetime passed";
  }

  private static void expectInputError(Action action, Class<? extends Throwable> causeType) throws Exception {
    try {
      action.run();
      throw new AssertionError("Expected query lookup/input failure");
    } catch (IllegalQueryMethodException expected) {
      require(causeType.isInstance(expected.getCause()), "query failure retains its real cause");
    }
  }

  private static void expectTargetFailure(Action action, Throwable cause) throws Exception {
    try {
      action.run();
      throw new AssertionError("Expected wrapped callback failure");
    } catch (InvocationTargetException expected) {
      require(expected.getCause() == cause && expected.getTargetException() == cause, "callback failure identity");
    }
  }

  private static void expectDirectInputError(Action action) throws Exception {
    try {
      action.run();
      throw new AssertionError("Expected direct invocation argument failure");
    } catch (IllegalArgumentException expected) {
    }
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  private interface Action { void run() throws Exception; }
}
