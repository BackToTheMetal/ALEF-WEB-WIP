package mpsj2cl.dynamiccomponents;

import java.util.function.Consumer;
import java.util.function.Supplier;
import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.core.platform.DynamicComponentPlugin;
import jetbrains.mps.core.platform.DynamicComponentWarden;
import jetbrains.mps.core.platform.DynamicComponentWarden.Token;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/** Drive prepare/poll/finish in separate browser jobs, requesting GC between jobs. */
public final class DynamicComponentGcTest {
  interface AbandonedFactoryService extends CoreComponent {}
  interface AbandonedListenerService extends CoreComponent {}
  interface PublishedService extends CoreComponent {}
  interface RetainedFactoryService extends CoreComponent {}
  private static State state;

  private static final class Component implements PublishedService, RetainedFactoryService {
    int initialized;
    int disposed;
    public void init() { initialized++; }
    public void dispose() { disposed++; }
  }

  private static final class CyclicFactory implements Supplier<AbandonedFactoryService> {
    Token ownToken;
    public AbandonedFactoryService get() {
      throw new AssertionError("An abandoned factory must not be invoked: " + ownToken);
    }
  }

  private static final class CyclicListener implements Consumer<AbandonedListenerService> {
    Token ownToken;
    public void accept(AbandonedListenerService component) {
      throw new AssertionError("An abandoned listener must not be invoked: " + ownToken);
    }
  }

  private static final class RetainedFactory implements Supplier<RetainedFactoryService> {
    public RetainedFactoryService get() { return new Component(); }
  }

  private static final class State {
    final DynamicComponentPlugin plugin = new DynamicComponentPlugin();
    final Component published = new Component();
    DynamicComponentWarden warden;
    NativeWeakRef<Token> factoryToken;
    NativeWeakRef<CyclicFactory> factory;
    NativeWeakRef<Token> listenerToken;
    NativeWeakRef<CyclicListener> listener;
    NativeWeakRef<Token> publishedToken;
    NativeWeakRef<RetainedFactory> retainedFactory;
    Token retainedToken;
  }

  public static void prepare() {
    state = new State();
    state.plugin.init();
    state.warden = state.plugin.findComponent(DynamicComponentWarden.class);
    publishAbandonedFactory(state);
    publishAbandonedListener(state);
    state.publishedToken = new NativeWeakRef<>(state.warden.publish(PublishedService.class, state.published));
    RetainedFactory retained = new RetainedFactory();
    state.retainedToken = state.warden.publish(RetainedFactoryService.class, retained);
    state.retainedFactory = new NativeWeakRef<>(retained);
  }

  private static void publishAbandonedFactory(State target) {
    CyclicFactory factory = new CyclicFactory();
    Token token = target.warden.publish(AbandonedFactoryService.class, factory);
    factory.ownToken = token; // Tests ephemeron semantics, not just an unreferenced token.
    target.factoryToken = new NativeWeakRef<>(token);
    target.factory = new NativeWeakRef<>(factory);
  }

  private static void publishAbandonedListener(State target) {
    CyclicListener listener = new CyclicListener();
    Token token = target.warden.whenAvailable(AbandonedListenerService.class, listener);
    listener.ownToken = token;
    target.listenerToken = new NativeWeakRef<>(token);
    target.listener = new NativeWeakRef<>(listener);
  }

  public static String poll() {
    if (state == null) throw new IllegalStateException("Call prepare first");
    state.plugin.findComponent(DynamicComponentWarden.class); // Purges weak key indexes.
    if (state.retainedFactory.deref() == null) {
      throw new AssertionError("A retained token must retain its pending factory");
    }
    return state.factoryToken.deref() == null && state.factory.deref() == null
        && state.listenerToken.deref() == null && state.listener.deref() == null
        && state.publishedToken.deref() == null ? "COLLECTED" : "PENDING";
  }

  public static String finish() {
    DynamicComponentLifecycleTest.require("COLLECTED".equals(poll()), "GC must have collected abandoned contributions");
    DynamicComponentLifecycleTest.require(state.plugin.findComponent(AbandonedFactoryService.class) == null,
        "expired factory contribution is removed");
    DynamicComponentLifecycleTest.require(state.plugin.findComponent(PublishedService.class) == state.published
        && state.published.disposed == 0, "abandoned instance token leaves plugin-owned component alive, as upstream");
    Component retained = (Component) state.plugin.findComponent(RetainedFactoryService.class);
    DynamicComponentLifecycleTest.require(retained.initialized == 1, "retained factory still instantiates");
    state.plugin.dispose();
    state.retainedToken.discard();
    DynamicComponentLifecycleTest.require(retained.disposed == 1 && state.published.disposed == 1,
        "plugin disposes both components once");
    state = null;
    return "PASS";
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakRef")
  private static final class NativeWeakRef<T> {
    NativeWeakRef(T value) {}
    native T deref();
  }
}
