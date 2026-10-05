# Editor and type-engine foundation

The foundation builds the existing Kernel/OpenAPI closure plus editor-api,
editorlang-runtime, editing-runtime, pattern-runtime, typechecking and typesystemEngine.
The implementation preserves real inference, equation-solving, subtyping and rule
selection algorithms. IntelliJ editor/workbench integration is a separate layer.

## Shared time profile

`java.time.Instant` retains second/nanosecond values, Java MIN/MAX sentinels,
normalization, ordering, equality/hash, ISO rendering and epoch/arithmetic methods.
`now()` uses browser wall-clock milliseconds. It does not invent monotonic instants.
`Duration` supplies the arithmetic/unit conversions required by MPS timeouts.
The general `Temporal`, `Clock`, parsing, zones and formatter interfaces are not
implemented by this bounded profile. In particular `Duration.between` currently
accepts two `Instant` values rather than the general `Temporal` interface.

`tools/run_foundation_time_oracle.py` compared 539 observations against Java 21.0.12:
full-range positive/negative ISO instants, MIN/MAX, epoch round trips including
`Long.MIN_VALUE`/`MAX_VALUE`, nanosecond arithmetic/normalization and duration units.
The exact observations are in `tests/typechecking/time-jvm-expected.txt`.

MPS incremental checking uses conservative equal-timestamp comparisons: a completed
check or invalidation in the same millisecond remains visible to a consumer. This
can report an extra update within one tick, avoiding lost updates without changing
the public `Instant`-based checking interface.

## References and disposal

`WeakReference` uses native JavaScript `WeakRef`. `ReferenceQueue.poll()` checks
watched native references and enqueues actual collected references; it is not an
empty compatibility queue. The queue watches the reference objects weakly as well,
so abandoning both the reference and its referent does not pin them. Explicit
`clear()` does not enqueue; explicit `enqueue()` clears and enqueues at most once.
`PhantomReference.get()` is always null. Empty blocking `ReferenceQueue.remove()`
fails explicitly because browser event-thread blocking is unavailable.

Browser Java primitive-backed String, Boolean and Double values cannot be native
weak targets and are held strongly. `SoftReference` uses an explicit strong-cache
policy: clear/enqueue or owner disposal evicts it; browser memory pressure is not
observable as the JVM's soft-reference pressure policy.

`WeakHashMap` retains Java key equality, null keys, mutable entry views, iterator
removal and strong values. Expired keys are purged on access. Its current index is
linear. A value that strongly references its key prevents collection, as in Java's
WeakHashMap; this is not JavaScript WeakMap's ephemeron behavior.

Typechecking controller shutdown now disposes active/overridden/on-demand sessions
and auxiliary data explicitly. Workbench handles cannot recreate a session after
controller shutdown. Facade shutdown clears thread context and live disposal
records and invalidates retained facade references; restarting installs a fresh
context. Browser lifecycle must call these shutdown paths rather than relying on GC.

## Compiled language deployment and rule ownership

`LanguageRegistry.registerCompiledLanguage(runtime)` deploys an already linked
runtime with empty runtime/generation dependencies. The overload additionally takes
a classloader, runtime-module references and generation-target references from the
language deployment manifest. The existing language reinitialization, aspect
contributions, ModuleRuntime activation and deployment listeners are used.

`unregisterCompiledLanguage(runtime)` requires extending languages to be removed
first. Deployment removal listeners see the runtime before disposal, and their
`acquireRemovedTrackingLock()` tokens can postpone final disposal. Final removal
notifies language listeners, removes extension contributions, disposes aspects,
deinitializes/removes the runtime and updates remaining language relationships.
This API does not scan host JARs or generate missing classes.

`RuleSet.registerRuleNamespace(ruleClass, languageNamespace)` records exact language
ownership by Class identity. Both one-concept and two-concept rule registries use
that mapping, so language filtering and priority survive stripped class metadata.
Conflicting registrations and missing ownership fail explicitly. A deployment must
unregister these classes after disposing sessions and rule caches. No class-name
guessing is used.

`BrowserPlatform` composes the real MPS core and both typechecking plugins. It owns
compiled language registrations, checks duplicate deployment before changing rule
ownership, and removes languages in reverse registration order during disposal.
Sessions and caches are disposed before rule mappings. If a deployment listener
still holds a removal token, disposal fails explicitly; releasing the token permits
disposal to be retried. Project repositories must be disposed before the platform.

## Tests and boundaries

`editor-foundation-tests.bzl` declares root-owned shared-JRE tests and a handwritten
runtime-language fixture. The fixture is **not generated MPS output**. It feeds
literal inference equations and integer-to-number subtyping rules to the real MPS
engine. It also exercises explicit incremental invalidation and shared editor
session release. Its detached root has no repository listeners; automatic model
event invalidation belongs to the live editor acceptance test.

The separate `BrowserPlatformTest` fixture covers full core/type-plugin startup and
shutdown twice, real ProjectRepository commands, language inheritance, duplicate
deployment rollback, delayed removal tracking, aspect disposal, rule ownership
cleanup and invalidation of retained typechecking facade instances. Its handwritten
language runtimes supply lifecycle aspects; they are not generated language output.

The test entrypoint freezes browser Date.now during the synchronous fixture to
exercise the equal-clock-tick case. It restores the original clock in `finally`.
The GC probe `tools/test_foundation_gc.mjs` uses separate Chromium CDP jobs to force
collection and verifies native weak/phantom queue notifications and weak-key map
cleanup. Deterministic browser tests are run separately across all three engines.
The iterator probe forces collection after `hasNext()` and after `next()`: the
selected/current key remains live until `remove()`, then collects on the next forced
collection. Both development and optimized JavaScript passed this check.

The typechecking fixture and the full platform lifecycle fixture each passed all
nine Chromium, Firefox and WebKit runs: development, optimized and stripped class
metadata. Results are in `out/typechecking_foundation-browser-results.json` and
`out/browser_platform-browser-results.json`.

The shared reference/map/time contract test passed in Chromium, Firefox and WebKit
for development and optimized builds. The native collection probe passed both
Chromium builds on the first forced collection. These results are in
`out/foundation_jre-browser-results.json` and `out/foundation-gc-results.json`.
