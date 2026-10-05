/*
 * Copyright 2003-2026 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.core.platform;

import jetbrains.mps.components.ComponentHost;
import jetbrains.mps.components.ComponentPlugin;
import jetbrains.mps.components.CoreComponent;
import jetbrains.mps.core.platform.DynamicComponentWarden.Token;
import jetbrains.mps.logging.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * EXPERIMENTAL API
 * @author Artem Tikhomirov
 * @since 2021.1
 */
@ApiStatus.Experimental
public class DynamicComponentPlugin extends ComponentPlugin implements ComponentHost {
  private WardenImpl myWarden;
  private boolean myIsLocked;
  // at the moment, just Class->instance map, perhaps, shall keep more complex object,
  // some ComponentAdapter
  private final Map<Class<?>, CoreComponent> myRegistered= new HashMap<>();

  // ComponentPlugin#init
  @Override
  public void init() {
    myIsLocked = false;
    myWarden = init(new WardenImpl());
  }

  @Override
  public void dispose() {
    myIsLocked = true;
    synchronized (myRegistered) {
      final Collection<CoreComponent> components = myRegistered.values();
      components.forEach(CoreComponent::dispose);
      // FIXME Likely, necessary for tests only, but still worth distinct method and threading addressed
      myWarden.myInstances.removeValues(components);
      myRegistered.clear();
    }
    super.dispose();
  }

  @Nullable
  @Override
  public <T extends CoreComponent> T findComponent(@NotNull Class<T> componentClass) {
    myWarden.purge();
    if (DynamicComponentWarden.class == componentClass) {
      return componentClass.cast(myWarden);
    }
    // here comes the code that discovers already instantiated components
    final T existing = findDynamicIfExists(componentClass);
    if (existing != null) {
      return existing;
    }
    //
    // XXX while in CP.dispose, shall not give access to components that have been already disposed.
    //     likely, would need to alter CP superclass to notify when CC has gone to reflect this in the state.
    //     CC in its dispose may request other CC, and this class shall not respond with a value if latter has been
    //     disposed already.
    //     However, not clear if CC.init order (and, therefore, that of CC.dispose) corresponds to dependencies between
    //     CCs. E.g. CC2 manifests its factory, CC1.init asks for CC2, CC2.init. Dispose goes in a reverse order, CC2.dispose, CC1.dispose asks for CC2
    //     and gets what?
    //
    // When none found, ask warden for any late-moment/on-demand initializations
    if (myIsLocked) {
      return null;
    }
    return myWarden.instantiate(componentClass);
  }

  /*package*/  <T extends CoreComponent> T findDynamicIfExists(Class<T> componentIface) {
    try {
      synchronized (myRegistered) {
        final Object instance = myRegistered.get(componentIface);
        return instance == null ? null : componentIface.cast(instance);
      }
    } catch (Exception ex) {
      // fail gracefully if there's anything wrong with the instance (although shall not happen
      // provided typesafety in #initAndRecord() holds)
      // Perhaps, shall move exception handling outside, to happen only for findComponent uses?
      String m = "Failed to retrieve proper instance of component " + componentIface.getName();
      Logger.getLogger(DynamicComponentPlugin.class).error(m, ex);
      // XXX perhaps, can return a proxy that would complain about "null safety" for every method invoked?
      return null;
    }
  }

  /*package*/  <T extends CoreComponent> void initAndRecord(Class<T> componentIface, T componentInstance) {
    // component initialization may trigger instantiation and registration of other components
    // NOT {@code super.init(componentInstance)} as I want to manage list of dynamic CC myself
    componentInstance.init(); // FIXME guard with try/catch?
    synchronized (myRegistered) {
      myRegistered.put(componentIface, componentInstance);
    }
  }

  /*package*/  void forget(CoreComponent componentInstance) {
    synchronized (myRegistered) {
      final boolean anyRemoved = myRegistered.values().removeIf(v -> v == componentInstance);
      if (anyRemoved) {
        String m = "Component instance " + componentInstance + " removed";
        Logger.getLogger(DynamicComponentPlugin.class).info(m);
      } else {
        String m = "Attempt to remove unknown component instance " + componentInstance;
        Logger.getLogger(DynamicComponentPlugin.class).warning(m);
      }
    }
    componentInstance.dispose(); // FIXME guard with try/catch?
  }


  /*
   * What are possible scenarios, memory/reference-wise?
   * Registration of a factory/listener/instance gave component client a token instance.
   * - we may face component iface class gone
   * - we may see token gone without being discarded
   * - class with factory/listener get replaced with a new one
   * Either token or component iface class gone, shall clear respective records!
   */

  // FIXME initial idea was this class to be unaware of threading, while its outer shall take care
  //       (findComponent(SameIface) may come from different threads)
  //       HOWEVER, not clear what's the contract for discard(), can we ensure it doesn't come from different threads?
  //       AND ACTUAL TAKE is to stick to plain old Java mutexes (don't expect to be a performance issue) to address
  //       the fact access (instantiate) and population (publish) could happen in unpredictable moments.
  private class WardenImpl implements DynamicComponentWarden {
    // Native WeakMap keeps contributions ephemerally; a factory/listener that
    // references its own token must not turn the token into a strong map key.
    // WeakRef indexes allow access-time sweeping without retaining token objects.
    private final WeakStore<Class<?>, List<TokenWeakRef>> myIssuedTokens = new WeakStore<>();
    private final WeakStore<TokenImpl, Supplier<?>> myFactories = new WeakStore<>();
    private final WeakStore<TokenImpl, Consumer<Object>> myListeners = new WeakStore<>();
    private final WeakStore<TokenImpl, CoreComponent> myInstances = new WeakStore<>();
    private boolean myDisposed;

    @Override
    public synchronized void dispose() {
      myDisposed = true;
      try {
        // The outer plugin already removed its current components from this
        // store. Dispose any still-live instance replaced under the same class
        // before releasing its registration, so shutdown cannot strand it.
        for (WeakEntry<TokenImpl, CoreComponent> entry : myInstances.entries()) {
          entry.value.dispose();
        }
      } finally {
        myIssuedTokens.clear();
        myFactories.clear();
        myListeners.clear();
        myInstances.clear();
      }
    }

    private void checkActive() {
      if (myDisposed || myIsLocked) {
        throw new IllegalStateException("Dynamic component plugin has been disposed");
      }
    }

    @Nullable
    /*package*/synchronized <T extends CoreComponent> T instantiate(Class<T> componentClass) {
      final List<TokenWeakRef> tokenRefs = myIssuedTokens.get(componentClass);
      if (tokenRefs == null) {
        return null;
      }
      Supplier<?> supplier = null;
      TokenImpl key = null;
      for (Iterator<TokenWeakRef> it = tokenRefs.iterator(); it.hasNext(); ) {
        TokenWeakRef tokenRef = it.next();
        key = tokenRef.get();
        if (key == null) {
          // no idea what's the token, but no reason to keep the reference anyway, expect
          // its expired native weak reference to be swept on access
          it.remove();
          continue;
        }
        supplier = myFactories.remove(key);
        if (supplier == null) {
          continue;
        }
        // no use for this token for us any more
        it.remove();
        break; // call supplier outside of the cycle
      }
      if (supplier == null) {
        return null;
      }
      assert key != null;
      // consult supplier outside ot for tokenRefs loop just in case it gonna #publish() anything
      T instance = componentClass.cast(supplier.get());
      initAndRecord(componentClass, instance);
      myInstances.put(key, instance);
      // if any listeners, notify them
      notifyListeners(tokenRefs, instance);
      return instance;
    }

    private void notifyListeners(@Nullable List<TokenWeakRef> tokenRefs, CoreComponent instance) {
      if (tokenRefs == null || tokenRefs.isEmpty()) {
        return;
      }
      ArrayList<Consumer<Object>> listeners = new ArrayList<>(4);
      for (Iterator<TokenWeakRef> it = tokenRefs.iterator(); it.hasNext(); ) {
        TokenWeakRef tokenRef = it.next();
        TokenImpl key = tokenRef.get();
        if (key == null) {
          it.remove();
        }
        final Consumer<Object> listener = myListeners.remove(key);
        if (listener != null) {
          listeners.add(listener);
        }
      }
      // perhaps, shall copy original tokenRefs list right away in #instantiate() instead of this 'out of the loop' approach?
      for (Consumer<Object> c : listeners) {
        c.accept(instance);
      }
    }

    /*package*/ synchronized void purge() {
      myListeners.size();
      myFactories.size();
      myInstances.size();
      for (WeakEntry<Class<?>, List<TokenWeakRef>> entry : myIssuedTokens.entries()) {
        entry.value.removeIf(reference -> reference.get() == null);
        if (entry.value.isEmpty()) {
          myIssuedTokens.remove(entry.key);
        }
      }
      // As upstream, losing a component token does not dispose the registered
      // component. It remains owned by myRegistered until plugin.dispose().
    }

    /*package*/ synchronized void discard(final TokenImpl token) {
      // FIXME what about threading?
      final CoreComponent instance = myInstances.remove(token);
      if (instance != null) {
        forget(instance);
      }

      myListeners.remove(token);
      myFactories.remove(token);
      Class<?> key = null;
      L1: for (WeakEntry<Class<?>, List<TokenWeakRef>> e : myIssuedTokens.entries()) {
        for (Iterator<TokenWeakRef> it = e.value.iterator(); key == null && it.hasNext();) {
          final TokenWeakRef next = it.next();
          if (next.get() == null) {
            // clear stale right away
            it.remove();
            continue;
          }
          if (next.get() == token) {
            it.remove();
            key = e.key;
            break L1;
          }
        }
      }
      if (key != null && myIssuedTokens.get(key).isEmpty()) {
        // don't keep reference to class
        myIssuedTokens.remove(key);
      }
    }

    @Override
    public synchronized <T extends CoreComponent> Token publish(@NotNull Class<T> componentIface, @NotNull T componentInstance) {
      checkActive();
      purge();
      final List<TokenWeakRef> existingTokens = myIssuedTokens.get(componentIface);
      // FIXME likely, shall check if there's already component instance published for this iface.
      TokenImpl t = new TokenImpl(this);
      // next code is similar to that in #instantiate(), with the difference in token to activate listeners
      recordIssued(componentIface, t);
      initAndRecord(componentIface, componentInstance);
      myInstances.put(t, componentInstance);
      // activate any listener associated with this component iface
      notifyListeners(existingTokens, componentInstance);
      return t;
    }

    @Override
    public synchronized <T extends CoreComponent> Token publish(@NotNull Class<T> componentIface, @NotNull Supplier<? extends T> componentFactory) {
      checkActive();
      purge();
      TokenImpl t = new TokenImpl(this);
      recordIssued(componentIface, t);
      myFactories.put(t, componentFactory);
      return t;
    }

    @Override
    public synchronized <T extends CoreComponent> Token whenAvailable(@NotNull Class<T> componentIface, @NotNull Consumer<? super T> componentListener) {
      checkActive();
      purge();
      final T instance = findDynamicIfExists(componentIface);
      if (instance != null) {
        componentListener.accept(instance);
        return () -> {}; // blank, no-op discard
      }
      TokenImpl t = new TokenImpl(this);
      recordIssued(componentIface, t);
      myListeners.put(t, (Consumer<Object>) componentListener);
      return t;
    }

    private void recordIssued(@NotNull Class<?> componentIface, TokenImpl t) {
      List<TokenWeakRef> tokens = myIssuedTokens.get(componentIface);
      if (tokens == null) {
        tokens = new ArrayList<>(4);
        myIssuedTokens.put(componentIface, tokens);
      }
      tokens.add(new TokenWeakRef(t));
    }
  }

  private static class TokenImpl implements Token {
    private final WardenImpl myWarden;
    /*package*/ TokenImpl(WardenImpl warden) {
      myWarden = warden;
    }
    @Override
    public void discard() {
      myWarden.discard(this);
    }
  }

  private static class TokenWeakRef {
    private final NativeWeakRef<TokenImpl> reference;

    TokenWeakRef(TokenImpl token) {
      reference = new NativeWeakRef<>(token);
    }

    TokenImpl get() {
      return reference.deref();
    }
  }

  /**
   * Private identity-keyed store for object-backed Class and Token instances.
   * Values exist only in the native WeakMap, never in a strong Java-side cache.
   * Enumeration snapshots live keys only for the duration of an operation.
   */
  private static final class WeakStore<K, V> {
    private NativeWeakMap<K, V> values;
    private final List<NativeWeakRef<K>> keys = new ArrayList<>();

    WeakStore() {
      if (getWeakRefConstructor() == null || getWeakMapConstructor() == null) {
        throw new UnsupportedOperationException("Dynamic components require native WeakRef and WeakMap support");
      }
      values = new NativeWeakMap<>();
    }

    V get(K key) {
      return values.get(key);
    }

    void put(K key, V value) {
      if (key == null) {
        throw new NullPointerException("Weak component key");
      }
      sweep();
      if (!values.has(key)) {
        keys.add(new NativeWeakRef<>(key));
      }
      values.set(key, value);
    }

    V remove(K key) {
      V result = values.get(key);
      values.delete(key);
      keys.removeIf(reference -> {
        K current = reference.deref();
        return current == null || current == key;
      });
      return result;
    }

    void removeValues(Collection<?> removed) {
      for (Iterator<NativeWeakRef<K>> iterator = keys.iterator(); iterator.hasNext();) {
        K key = iterator.next().deref();
        if (key == null) {
          iterator.remove();
        } else if (removed.contains(values.get(key))) {
          values.delete(key);
          iterator.remove();
        }
      }
    }

    int size() {
      sweep();
      return keys.size();
    }

    List<WeakEntry<K, V>> entries() {
      List<WeakEntry<K, V>> result = new ArrayList<>();
      for (Iterator<NativeWeakRef<K>> iterator = keys.iterator(); iterator.hasNext();) {
        K key = iterator.next().deref();
        if (key == null || !values.has(key)) {
          iterator.remove();
        } else {
          result.add(new WeakEntry<>(key, values.get(key)));
        }
      }
      return result;
    }

    void clear() {
      values = new NativeWeakMap<>();
      keys.clear();
    }

    private void sweep() {
      keys.removeIf(reference -> {
        K key = reference.deref();
        return key == null || !values.has(key);
      });
    }
  }

  private static final class WeakEntry<K, V> {
    final K key;
    final V value;

    WeakEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  @JsProperty(namespace = JsPackage.GLOBAL, name = "globalThis.WeakRef")
  private static native Object getWeakRefConstructor();

  @JsProperty(namespace = JsPackage.GLOBAL, name = "globalThis.WeakMap")
  private static native Object getWeakMapConstructor();

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakRef")
  private static final class NativeWeakRef<T> {
    NativeWeakRef(T value) {}
    native T deref();
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakMap")
  private static final class NativeWeakMap<K, V> {
    NativeWeakMap() {}
    native V get(K key);
    native boolean has(K key);
    native void set(K key, V value);
    native boolean delete(K key);
  }
}
