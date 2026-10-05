package alefj2cl;

/** Explicit host configuration replaces process environment access in the browser. */
public final class BrowserEnvironment {
  private static final java.util.Map<String, String> values = new java.util.HashMap<>();
  public static String get(String name) { return values.get(name); }
  public static void set(String name, String value) {
    if (value == null) values.remove(name); else values.put(name, value);
  }
  public static void clear() { values.clear(); }
}
