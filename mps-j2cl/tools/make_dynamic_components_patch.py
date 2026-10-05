#!/usr/bin/env python3
"""Adapt DynamicComponentPlugin to native browser WeakMap/WeakRef primitives."""
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RELATIVE = "core/kernel/source/jetbrains/mps/core/platform/DynamicComponentPlugin.java"


def replace(text, before, after):
    if text.count(before) != 1:
        raise RuntimeError("Upstream fragment changed: " + before[:100])
    return text.replace(before, after)


def generate():
    text = (ROOT / "mps" / RELATIVE).read_text(encoding="utf-8")
    for imp in ["java.lang.ref.Reference", "java.lang.ref.ReferenceQueue",
                "java.lang.ref.WeakReference", "java.util.WeakHashMap"]:
        text = text.replace("import " + imp + ";\n", "")
    text = replace(text, "import java.util.ArrayList;", """import java.util.ArrayList;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;""")
    text = replace(text, "myWarden.myInstances.values().removeAll(components);",
                   "myWarden.myInstances.removeValues(components);")
    for before, after in [
        ('String.format("Failed to retrieve proper instance of component %s", componentIface.getName())',
         '"Failed to retrieve proper instance of component " + componentIface.getName()'),
        ('String.format("Component instance %s removed", componentInstance)',
         '"Component instance " + componentInstance + " removed"'),
        ('String.format("Attempt to remove unknown component instance %s", componentInstance)',
         '"Attempt to remove unknown component instance " + componentInstance'),
    ]:
        text = replace(text, before, after)

    start = text.index("    // (A) Token as a POJO")
    end = text.index("\n    @Nullable", start)
    text = text[:start] + """    // Native WeakMap keeps contributions ephemerally; a factory/listener that
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
""" + text[end:]

    start = text.index("    /*package*/ synchronized void purge()")
    end = text.index("\n    /*package*/ synchronized void discard(", start)
    text = text[:start] + """    /*package*/ synchronized void purge() {
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
""" + text[end:]
    text = replace(text, "for (Map.Entry<Class<?>, List<TokenWeakRef>> e : myIssuedTokens.entrySet())",
                   "for (WeakEntry<Class<?>, List<TokenWeakRef>> e : myIssuedTokens.entries())")
    text = replace(text, "e.getValue().iterator()", "e.value.iterator()")
    text = replace(text, "key = e.getKey();", "key = e.key;")
    for signature in [
        "public synchronized <T extends CoreComponent> Token publish(@NotNull Class<T> componentIface, @NotNull T componentInstance) {",
        "public synchronized <T extends CoreComponent> Token publish(@NotNull Class<T> componentIface, @NotNull Supplier<? extends T> componentFactory) {",
        "public synchronized <T extends CoreComponent> Token whenAvailable(@NotNull Class<T> componentIface, @NotNull Consumer<? super T> componentListener) {",
    ]:
        text = replace(text, signature, signature + "\n      checkActive();\n      purge();")
    text = replace(text, """      final List<TokenWeakRef> tokens = myIssuedTokens.computeIfAbsent(componentIface, (ci) -> new ArrayList<>(4));
      tokens.add(new TokenWeakRef(t, myTokenQueue));""", """      List<TokenWeakRef> tokens = myIssuedTokens.get(componentIface);
      if (tokens == null) {
        tokens = new ArrayList<>(4);
        myIssuedTokens.put(componentIface, tokens);
      }
      tokens.add(new TokenWeakRef(t));""")
    text = text.replace("// token it references to reach myTokenQueue", "// its expired native weak reference to be swept on access")
    start = text.index("  // XXX perhaps, it's TokenRecord")
    text = text[:start] + """  private static class TokenWeakRef {
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
"""
    return text


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    target = ROOT / "mps-web" / RELATIVE
    result = generate()
    if args.check:
        if not target.is_file() or target.read_text(encoding="utf-8") != result:
            raise SystemExit("Dynamic component replacement differs")
    else:
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(result, encoding="utf-8", newline="\n")
    print("Dynamic component replacement " + ("checked" if args.check else "generated"))


if __name__ == "__main__":
    main()
