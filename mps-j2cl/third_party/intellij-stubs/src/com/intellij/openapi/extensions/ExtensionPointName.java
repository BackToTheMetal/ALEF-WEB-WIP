package com.intellij.openapi.extensions;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
public final class ExtensionPointName<T> {
  private static final Map<String, Map<AreaInstance, List<Object>>> REGISTRY = new HashMap<>();
  private final String name;
  public ExtensionPointName(String name) { this.name = name; }
  public static <T> ExtensionPointName<T> create(String name) { return new ExtensionPointName<>(name); }
  public String getName() { return name; }
  public void registerBrowserExtension(AreaInstance area, T value, Disposable parent) {
    List<Object> values = REGISTRY.computeIfAbsent(name, ignored -> new IdentityHashMap<>()).computeIfAbsent(area, ignored -> new ArrayList<>());
    values.add(value);
    Disposer.register(parent, () -> {
      values.remove(value);
      Map<AreaInstance, List<Object>> areas = REGISTRY.get(name);
      if (values.isEmpty()) areas.remove(area);
      if (areas.isEmpty()) REGISTRY.remove(name);
    });
  }
  public List<T> getExtensionList() { return getExtensionList(null); }
  @SuppressWarnings("unchecked")
  public List<T> getExtensionList(AreaInstance area) {
    Map<AreaInstance, List<Object>> areas = REGISTRY.get(name);
    List<?> values = areas == null ? null : areas.get(area);
    return values == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>((List<T>) values));
  }
  public List<T> getExtensionsIfPointIsRegistered() { return getExtensionList(); }
  public List<T> getExtensionsIfPointIsRegistered(AreaInstance area) { return getExtensionList(area); }
  public boolean hasAnyExtensions() { return !getExtensionList().isEmpty(); }
  public Iterable<T> getIterable() { return getExtensionList(); }
  public java.util.stream.Stream<T> extensions() { return getExtensionList().stream(); }
  public void forEachExtensionSafe(Consumer<? super T> consumer) { getExtensionList().forEach(consumer); }
  public T findFirstSafe(Predicate<? super T> predicate) {
    for (T value : getExtensionList()) if (predicate.test(value)) return value;
    return null;
  }
  public <V extends T> V findExtension(Class<V> type) {
    for (T value : getExtensionList()) if (type.isInstance(value)) return type.cast(value);
    return null;
  }
  public <V extends T> V findExtensionOrFail(Class<V> type) { return java.util.Objects.requireNonNull(findExtension(type), type.getName()); }
  @Override public String toString() { return name; }
}
