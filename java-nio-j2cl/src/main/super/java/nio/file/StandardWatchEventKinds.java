package java.nio.file;

public final class StandardWatchEventKinds {
  public static final WatchEvent.Kind<Object> OVERFLOW = new Kind<>("OVERFLOW", Object.class);
  public static final WatchEvent.Kind<Path> ENTRY_CREATE = new Kind<>("ENTRY_CREATE", Path.class);
  public static final WatchEvent.Kind<Path> ENTRY_DELETE = new Kind<>("ENTRY_DELETE", Path.class);
  public static final WatchEvent.Kind<Path> ENTRY_MODIFY = new Kind<>("ENTRY_MODIFY", Path.class);
  private StandardWatchEventKinds() {}
  private static final class Kind<T> implements WatchEvent.Kind<T> {
    private final String name;
    private final Class<T> type;
    Kind(String name, Class<T> type) { this.name = name; this.type = type; }
    @Override public String name() { return name; }
    @Override public Class<T> type() { return type; }
    @Override public String toString() { return name; }
  }
}
