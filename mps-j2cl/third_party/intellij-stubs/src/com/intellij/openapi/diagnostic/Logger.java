package com.intellij.openapi.diagnostic;
import java.util.function.BiConsumer;
/** A host-installable logger; errors are retained as diagnostics rather than silently ignored. */
public class Logger {
  private static BiConsumer<String, Throwable> sink = (message, error) -> {
    System.err.println(message);
    if (error != null) error.printStackTrace();
  };
  private final String name;
  private static boolean debugEnabled;
  protected Logger(String name) { this.name = name; }
  public static Logger getInstance(Class<?> type) { return getInstance(type.getName()); }
  public static Logger getInstance(String name) { return new Logger(name); }
  public static void setBrowserSink(BiConsumer<String, Throwable> newSink) { sink = java.util.Objects.requireNonNull(newSink); }
  public static void setBrowserDebugEnabled(boolean value) { debugEnabled = value; }
  public boolean isDebugEnabled() { return debugEnabled; }
  public boolean isTraceEnabled() { return debugEnabled; }
  public void debug(String message) { if (debugEnabled) log("DEBUG", message, null); }
  public void debug(String message, Throwable error) { if (debugEnabled) log("DEBUG", message, error); }
  public void debug(Throwable error) { debug(error.toString(), error); }
  public void debug(String message, Object... details) { debug(message + java.util.Arrays.toString(details)); }
  public void trace(String message) { debug(message); }
  public void trace(Throwable error) { debug(error); }
  public void info(String message) { log("INFO", message, null); }
  public void info(Throwable error) { info(error.toString(), error); }
  public void info(String message, Throwable error) { log("INFO", message, error); }
  public void warn(String message) { log("WARN", message, null); }
  public void warn(Throwable error) { warn(error.toString(), error); }
  public void warn(String message, Throwable error) { log("WARN", message, error); }
  public void error(String message) { log("ERROR", message, null); }
  public void error(Throwable error) { error(error.toString(), error); }
  public void error(String message, Throwable error, String... details) { log("ERROR", message + java.util.Arrays.toString(details), error); }
  public void error(String message, String... details) { log("ERROR", message + java.util.Arrays.toString(details), null); }
  public boolean assertTrue(boolean value) { return assertTrue(value, "Assertion failed"); }
  public boolean assertTrue(boolean value, Object message) { if (!value) throw new AssertionError(message); return value; }
  private void log(String level, String message, Throwable error) { sink.accept(level + " [" + name + "] " + message, error); }
}
