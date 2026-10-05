DynamicComponentLifecycleTest.run() uses the same source against upstream JVM MPS and the browser replacement. It checks publication, factory order and reuse, discard, listener order and one-time notification, reentrant resolution, and shutdown.

tools/run_dynamic_components_oracle.py runs the shared lifecycle suite on untouched upstream JVM sources. tools/test_dynamic_components.mjs checks both development and optimized bundles in Chromium, Firefox, and WebKit, and additionally forces the GC probe through Chromium CDP. The browser results record exact bundle hashes in out/dynamic-components-results.json; forced collection is not claimed for Firefox or WebKit.

runBrowserBoundaries() checks explicit browser lifecycle tightening: a disposed warden rejects new registrations, all contribution stores are released, and live instances replaced under the same component class are disposed during shutdown.

The GC probe must run in separate browser jobs:

1. Call prepareDynamicGc().
2. Return to the browser event loop and request collection through test instrumentation, such as Chromium's HeapProfiler.collectGarbage.
3. Call pollDynamicGc(). If it returns PENDING, return to the event loop before another collection request. Require COLLECTED within a bounded number of attempts.
4. Call finishDynamicGc() and require PASS.

The probe checks abandoned token/factory and token/listener cycles, a retained factory token, and an abandoned published component token. Garbage collection of a published component token preserves the upstream behavior: the plugin continues owning the component until plugin shutdown.

Production uses native WeakMap for key/value associations and WeakRef only for iterable key indexes. This avoids a strong Java-side value cache that could retain a value-to-token cycle. Native weak primitives are required; missing support throws UnsupportedOperationException. Cleanup timing depends on garbage collection; key indexes are swept on access. Java class unloading is not modeled by the statically compiled browser runtime.

The [ECMAScript WeakMap specification](https://tc39.es/ecma262/multipage/keyed-collections.html#sec-weakmap-objects) defines weak key reachability. The [WeakRef specification](https://tc39.es/ecma262/multipage/managing-memory.html#sec-weak-ref-objects) explains why a successful deref() retains its target until the current job ends; repeated synchronous polling cannot validate collection.
