package com.intellij.compat;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
public class BrowserComponentManager extends UserDataHolderBase implements ComponentManager {
  private final Map<Class<?>, Object> services = new IdentityHashMap<>();
  private final BrowserMessageBus messageBus;
  private boolean disposed;
  private boolean disposing;
  public BrowserComponentManager() { this(null); }
  public BrowserComponentManager(BrowserMessageBus parent) { messageBus = new BrowserMessageBus(parent); }
  public final <T> void registerService(Class<T> type, T service) {
    checkLive();
    if (disposing || Disposer.isDisposed(this)) throw new IllegalStateException("Cannot register services during disposal");
    Objects.requireNonNull(service);
    if (!type.isInstance(service)) throw new IllegalArgumentException("Invalid service " + type.getName());
    Object previous = services.put(type, service);
    if (previous != null && previous != service) {
      services.put(type, previous);
      throw new IllegalStateException("Service already registered: " + type.getName());
    }
    if (previous == null && service instanceof Disposable) Disposer.register(this, (Disposable) service);
  }
  @SuppressWarnings("unchecked")
  @Override public <T> T getComponent(Class<T> type) { checkLive(); return (T) services.get(type); }
  @Override public <T> T getService(Class<T> type) {
    T service = getComponent(type);
    if (service == null) throw new IllegalStateException("Browser host must register service " + type.getName());
    return service;
  }
  @Override public boolean hasComponent(Class<?> type) { return services.containsKey(type); }
  @Override public BrowserMessageBus getMessageBus() { checkLive(); return messageBus; }
  @Override public boolean isDisposed() { return disposed; }
  @Override public void dispose() {
    if (disposed || disposing) return;
    disposing = true;
    try { Disposer.disposeChildren(this, ignored -> true); }
    finally {
      try { services.clear(); messageBus.dispose(); clearUserData(); }
      finally { disposed = true; disposing = false; }
    }
  }
  protected final void checkLive() { if (disposed) throw new IllegalStateException("Component manager disposed"); }
}
