package com.intellij.openapi.actionSystem;
import java.util.LinkedHashMap;
import java.util.Map;
public final class DataKey<T> {
  private static final Map<String, DataKey<?>> KEYS = new LinkedHashMap<>();
  private final String name;
  private DataKey(String name) { this.name = name; }
  @SuppressWarnings("unchecked")
  public static <T> DataKey<T> create(String name) { return (DataKey<T>) KEYS.computeIfAbsent(name, DataKey::new); }
  public String getName() { return name; }
  public boolean is(String value) { return name.equals(value); }
  @SuppressWarnings("unchecked") public T getData(DataContext context) { return (T) context.getData(name); }
  @SuppressWarnings("unchecked") public T getData(DataProvider provider) { return (T) provider.getData(name); }
  public static DataKey<?>[] allKeys() { return KEYS.values().toArray(new DataKey<?>[0]); }
  public static int allKeysCount() { return KEYS.size(); }
  @Override public String toString() { return name; }
}
