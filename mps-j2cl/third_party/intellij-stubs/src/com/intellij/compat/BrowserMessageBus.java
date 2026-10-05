package com.intellij.compat;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.SimpleMessageBusConnection;
import com.intellij.util.messages.Topic;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** Synchronous event delivery with typed factories, without Java proxy reflection. */
public final class BrowserMessageBus implements MessageBus {
  private final BrowserMessageBus parent;
  private final List<BrowserMessageBus> children = new ArrayList<>();
  private final List<Connection> connections = new ArrayList<>();
  private final Map<Topic<?>, Function<?, ?>> factories = new IdentityHashMap<>();
  private final Map<Topic<?>, Object> publishers = new IdentityHashMap<>();
  private boolean disposed;
  public BrowserMessageBus() { this(null); }
  public BrowserMessageBus(BrowserMessageBus parent) {
    this.parent = parent;
    if (parent != null) { parent.ensureLive(); parent.children.add(this); }
  }
  public <L> void registerPublisher(Topic<L> topic, Function<Consumer<Consumer<L>>, L> factory) {
    ensureLive(); factories.put(topic, factory); publishers.remove(topic);
  }
  @SuppressWarnings("unchecked")
  @Override public <L> L syncPublisher(Topic<L> topic) {
    ensureLive();
    if (!publishers.containsKey(topic)) {
      Function<Consumer<Consumer<L>>, L> factory = (Function<Consumer<Consumer<L>>, L>) findFactory(topic);
      if (factory == null) throw new UnsupportedOperationException("Register a typed publisher for topic " + topic);
      publishers.put(topic, factory.apply(event -> publish(topic, event)));
    }
    return (L) publishers.get(topic);
  }
  private Function<?, ?> findFactory(Topic<?> topic) {
    Function<?, ?> factory = factories.get(topic);
    return factory != null || parent == null ? factory : parent.findFactory(topic);
  }
  public <L> void publish(Topic<L> topic, Consumer<L> event) {
    ensureLive(); deliverLocal(topic, event);
    switch (topic.getBroadcastDirection()) {
      case TO_CHILDREN: deliverChildren(topic, event, true); break;
      case TO_DIRECT_CHILDREN: deliverChildren(topic, event, false); break;
      case TO_PARENT:
        for (BrowserMessageBus bus = parent; bus != null; bus = bus.parent) bus.deliverLocal(topic, event);
        break;
      default: break;
    }
  }
  private <L> void deliverChildren(Topic<L> topic, Consumer<L> event, boolean recursive) {
    for (BrowserMessageBus child : new ArrayList<>(children)) {
      if (!child.disposed) {
        child.deliverLocal(topic, event);
        if (recursive) child.deliverChildren(topic, event, true);
      }
    }
  }
  @SuppressWarnings("unchecked")
  private <L> void deliverLocal(Topic<L> topic, Consumer<L> event) {
    List<Connection> snapshot = new ArrayList<>(connections);
    for (Connection connection : snapshot) {
      List<Object> list = connection.listeners.get(topic);
      if (list != null) for (Object listener : new ArrayList<>(list)) {
        if (!connection.closed) event.accept((L) listener);
      }
    }
  }
  @Override public MessageBus getParent() { return parent; }
  @Override public MessageBusConnection connect() {
    ensureLive(); Connection result = new Connection(); connections.add(result); return result;
  }
  @Override public MessageBusConnection connect(Disposable parentDisposable) {
    MessageBusConnection result = connect();
    try { Disposer.register(parentDisposable, result); }
    catch (RuntimeException error) { result.disconnect(); throw error; }
    return result;
  }
  @Override public SimpleMessageBusConnection simpleConnect() { return connect(); }
  @Override public boolean isDisposed() { return disposed; }
  @Override public boolean hasUndeliveredEvents(Topic<?> topic) { return false; }
  @Override public void dispose() {
    if (disposed) return;
    disposed = true;
    for (BrowserMessageBus child : new ArrayList<>(children)) child.dispose();
    for (Connection connection : new ArrayList<>(connections)) connection.disconnect();
    children.clear(); factories.clear(); publishers.clear();
    if (parent != null) parent.children.remove(this);
  }
  private void ensureLive() { if (disposed) throw new IllegalStateException("Message bus disposed"); }
  private final class Connection implements MessageBusConnection {
    private final Map<Topic<?>, List<Object>> listeners = new IdentityHashMap<>();
    private boolean closed;
    @Override public <L> void subscribe(Topic<L> topic, L listener) {
      ensureLive();
      if (closed) throw new IllegalStateException("Connection closed");
      if (!topic.getListenerClass().isInstance(listener)) throw new IllegalArgumentException("Wrong listener type for " + topic);
      listeners.computeIfAbsent(topic, ignored -> new ArrayList<>()).add(listener);
    }
    @Override public void disconnect() { closed = true; listeners.clear(); connections.remove(this); }
    @Override public void deliverImmediately() { /* Delivery is synchronous. */ }
  }
}
