package com.intellij.openapi.actionSystem;
import java.util.function.Supplier;
public interface DataSink {
  <T> void set(DataKey<T> key, T value);
  default <T> void setNull(DataKey<T> key) { set(key, null); }
  default <T> void lazy(DataKey<T> key, Supplier<? extends T> value) { set(key, value.get()); }
  default <T> void lazyNull(DataKey<T> key) { setNull(key); }
  default <T> void lazyValue(DataKey<T> key, java.util.function.Function<? super DataMap, ? extends T> value) { throw new UnsupportedOperationException("This sink has no data lookup context"); }
  static void uiDataSnapshot(DataSink sink, UiDataProvider provider) { provider.uiDataSnapshot(sink); }
  static void uiDataSnapshot(DataSink sink, DataProvider provider) {
    for (DataKey<?> key : DataKey.allKeys()) copyValue(sink, key, provider);
  }
  private static <T> void copyValue(DataSink sink, DataKey<T> key, DataProvider provider) {
    T value = key.getData(provider);
    // A legacy provider uses null for an unknown key; only an explicit setNull masks parents.
    if (value != null) sink.set(key, value);
  }
}
