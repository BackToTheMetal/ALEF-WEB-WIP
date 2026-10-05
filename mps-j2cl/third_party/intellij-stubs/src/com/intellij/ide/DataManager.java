package com.intellij.ide;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DataSink;
import com.intellij.openapi.actionSystem.UiDataProvider;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.JComponent;
public class DataManager {
  private static final DataManager INSTANCE = new DataManager();
  private static final String PROVIDER_KEY = "intellij.browser.dataProvider";
  public static DataManager getInstance() { return INSTANCE; }
  public static void registerDataProvider(JComponent component, DataProvider provider) { component.putClientProperty(PROVIDER_KEY, provider); }
  public static void registerDataProvider(JComponent component, UiDataProvider provider) { component.putClientProperty(PROVIDER_KEY, provider); }
  public static void removeDataProvider(JComponent component) { component.putClientProperty(PROVIDER_KEY, null); }
  public DataContext getDataContext() { return getDataContext(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()); }
  public DataContext getDataContext(Component component) {
    if (component == null) return DataContext.EMPTY_CONTEXT;
    DataContext parent = getDataContext(component.getParent());
    Snapshot sink = new Snapshot(parent);
    if (component instanceof UiDataProvider) ((UiDataProvider) component).uiDataSnapshot(sink);
    else if (component instanceof DataProvider) DataSink.uiDataSnapshot(sink, (DataProvider) component);
    if (component instanceof JComponent) {
      Object provider = ((JComponent) component).getClientProperty(PROVIDER_KEY);
      if (provider instanceof UiDataProvider) DataSink.uiDataSnapshot(sink, (UiDataProvider) provider);
      else if (provider instanceof DataProvider) DataSink.uiDataSnapshot(sink, (DataProvider) provider);
    }
    return sink;
  }
  public DataContext getDataContext(Component component, int x, int y) { return getDataContext(component); }
  public static DataContext createSnapshot(DataContext parent, com.intellij.openapi.actionSystem.DataSnapshotProvider provider) { Snapshot result = new Snapshot(parent); provider.dataSnapshot(result); return result; }
  private static final class Snapshot implements DataContext, DataSink, com.intellij.openapi.actionSystem.DataMap {
    private final DataContext parent;
    private final java.util.Map<String, java.util.function.Supplier<?>> values = new java.util.HashMap<>();
    private final java.util.Map<String, Object> cache = new java.util.HashMap<>();
    private final java.util.Set<String> evaluating = new java.util.HashSet<>();
    Snapshot(DataContext parent) { this.parent = parent; }
    public <T> void set(DataKey<T> key, T value) { values.put(key.getName(), () -> value); cache.remove(key.getName()); }
    public <T> void lazy(DataKey<T> key, java.util.function.Supplier<? extends T> value) { values.put(key.getName(), value); cache.remove(key.getName()); }
    public <T> void lazyValue(DataKey<T> key, java.util.function.Function<? super com.intellij.openapi.actionSystem.DataMap, ? extends T> value) { lazy(key, () -> value.apply(this)); }
    public <T> T get(DataKey<T> key) { return key.getData(this); }
    public Object getData(String id) {
      if (cache.containsKey(id)) return cache.get(id);
      if (!values.containsKey(id)) return parent == null ? null : parent.getData(id);
      if (!evaluating.add(id)) throw new IllegalStateException("Cyclic lazy data dependency: " + id);
      try { Object result = values.get(id).get(); cache.put(id, result); return result; }
      finally { evaluating.remove(id); }
    }
  }
}
