package com.intellij.openapi.actionSystem.impl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import java.util.HashMap;
import java.util.Map;
public final class SimpleDataContext implements DataContext {
  private final Map<String, Object> values;
  private final DataContext parent;
  private SimpleDataContext(Map<String, Object> values, DataContext parent) { this.values = new HashMap<>(values); this.parent = parent; }
  @Override public Object getData(String id) { return values.containsKey(id) ? values.get(id) : parent.getData(id); }
  public static Builder builder() { return new Builder(); }
  public static Builder builder(DataContext parent) { return new Builder().setParent(parent); }
  public static <T> DataContext getSimpleContext(DataKey<T> key, T value, DataContext parent) { return builder(parent).add(key, value).build(); }
  public static DataContext getSimpleContext(String id, Object value, DataContext parent) {
    Map<String, Object> values = new HashMap<>(); values.put(id, value); return new SimpleDataContext(values, parent);
  }
  public static final class Builder {
    private final Map<String, Object> values = new HashMap<>();
    private DataContext parent = DataContext.EMPTY_CONTEXT;
    public <T> Builder add(DataKey<T> key, T value) { values.put(key.getName(), value); return this; }
    public Builder addAll(Map<String, Object> data) { values.putAll(data); return this; }
    public Builder setParent(DataContext parent) { this.parent = parent == null ? DataContext.EMPTY_CONTEXT : parent; return this; }
    public SimpleDataContext build() { return new SimpleDataContext(values, parent); }
  }
}
