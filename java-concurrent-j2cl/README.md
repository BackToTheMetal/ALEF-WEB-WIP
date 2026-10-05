# java-concurrent-j2cl

A main-thread `java.util.concurrent` profile for migrating JetBrains MPS to the web.
It targets **J2CL 20260402**, **Bazel 8.3.1**, and the Java 21 build toolchain.
The [implementation plan](IMPLEMENTATION-PLAN.md) and
[MPS inventory](docs/mps-usage.md) explain the scope and evidence.

Verified: **496 optimized browser checks**, all **48 MPS-referenced concurrency
classes emitted**, and an independent consumer build/browser test.
See [validation evidence](docs/validation.md) for the exact scope.

## Behavior

Submitted work executes. `execute`, `submit`, `invokeAll`, `invokeAny`,
`CompletableFuture` stages (including `Async` variants), and `Thread.start()` run
on the calling main thread. Nested submission is immediate. Results, exceptions,
cancellation, completion callbacks, pool hooks, and shutdown state remain useful.

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
Future<Integer> result = executor.submit(() -> calculateModelSize());
// calculateModelSize has already run on the main thread.
int size = result.get();
executor.shutdown();
```

Delayed and periodic tasks use the browser's `setTimeout`/`clearTimeout` through
J2CL's built-in JsInterop annotations. No Elemental2 dependency, web worker, native
thread, filesystem, reflection, or server runtime is required. Timer callbacks run
after the main thread yields. Browser timer clamping/throttling still applies.

```java
ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
ScheduledFuture<?> refresh = scheduler.scheduleAtFixedRate(
    () -> refreshEditor(), 100, 100, TimeUnit.MILLISECONDS);
// Later:
refresh.cancel(false);
scheduler.shutdown();
```

| API family | Main-thread behavior |
| --- | --- |
| Atomic values and arrays | Ordinary fields/arrays; arithmetic, CAS identity, functional updates, marks/stamps and adders execute. |
| Concurrent maps | Plain map storage with mutation-tolerant traversal and live views; reuse J2CL's `ConcurrentMap` interface. |
| Copy-on-write lists/sets | Mutations copy storage; iterators and streams retain their original snapshot. |
| Linked queues/deques | Real FIFO/deque storage and removable weakly consistent iterators. |
| Blocking/transfer/delay queues | Store data and enforce capacity/expiry. Unsatisfied waits cannot suspend the main thread. |
| Locks | Track balanced acquisition, reentrancy and read/write ownership. Read-to-write upgrades cannot wait. |
| Latches, semaphores, barriers | Track counts/permits/broken state. A one-party barrier executes its action. |
| Executor services and futures | Inline work with results, error propagation, completion queues, hooks and lifecycle state. |
| Completion stages | Java 8 stage API plus common completion/timeout helpers. Long pending chains drain without recursive stack growth. |
| Scheduled executors | Main-thread timer execution; cancellation, delayed/periodic shutdown policies and failure suppression. |
| ForkJoinPool | MPS's `commonPool`/`ManagedBlocker` and Runnable/Callable executor subset. No fork/join tasks or work stealing. |
| Thread/ThreadGroup | Minimal migration facade: `start()` executes once inline; names/priorities are metadata; `currentThread()` is always the main thread. |
| ThreadLocal | One main-thread slot with lazy `initialValue`/`withInitial`, explicit null values and reinitialization after `remove`. |

This is a deliberately different execution model from Java SE concurrency. An
unfinished `Future.get()`/`join()`, empty queue `take()`, exhausted semaphore
`acquire()`, positive `Thread.sleep()`, or unsatisfied condition/latch/barrier wait
fails promptly with `IllegalStateException`. Timed waits return their unsuccessful
result immediately (`false`, `null`, or `TimeoutException`). A callback cannot be
preempted when its timeout expires. `cancel(true)` never interrupts executing code.

Use completion callbacks when a value will arrive from a later browser event:

```java
CompletableFuture<Model> loaded = new CompletableFuture<>();
loaded.thenAccept(model -> updateEditor(model));
// A later browser callback invokes loaded.complete(model).
```

Pool sizes, fairness flags and thread factories are compatibility configuration;
they do not create threads. A direct `execute` exception propagates to its caller;
`submit` stores it in the returned future. Existing busy loops, `Object.wait/notify`,
worker-thread identity assumptions, thread-local isolation, and heavy calculations
that require UI responsiveness need caller adaptation. This profile cannot turn
arbitrary blocking desktop code into an asynchronous browser application.

Schedule work through the scheduling methods. Removing a queued scheduled task
detaches its timer; it leaves the removed future pending so it can still be run
explicitly. Direct additions to a scheduler's exposed queue are rejected.

For ordinary executors, submit work through `execute` or `submit`; `getQueue()` is
for inspection and removal. Direct queue additions are stored but do not submit
work for execution. They prevent graceful termination until removed, and
`shutdownNow()` drains and returns them to the caller without executing or
cancelling them. `remove()` and `purge()` update termination state.

Completion races release losing input listeners when they finish or are cancelled.
Minimal completion stages (including their derived stages) expose a separate future
on each `toCompletableFuture()` call, so completing, cancelling or obtruding that
future cannot change the stage shared with other consumers.

Concurrent map iterators capture entries when traversal starts. Callback mutations
cannot invalidate that traversal; view removals and entry `setValue()` still update
the live map. This costs an allocation proportional to the map size per iterator.

## Consume the overlay

These classes extend/replace JRE classes, so they must have **one source owner in
the J2CL JRE**. Adding them as an ordinary second `j2cl_library` creates duplicate
types and is not the supported installation method.

First copy the generated patch into the consuming application's own repository.
Bazel 8.3.1 requires override patches to belong to the root repository. For a
consumer beside this checkout, run these commands from the consumer directory:

```powershell
New-Item -ItemType Directory -Force third_party\java-concurrent | Out-Null
Copy-Item ..\java-concurrent-j2cl\patches\j2cl-concurrent.patch third_party\java-concurrent\j2cl-concurrent.patch
'exports_files(["j2cl-concurrent.patch"])' | Set-Content third_party\java-concurrent\BUILD.bazel -Encoding ascii
```

Then add this to the consuming application's root `MODULE.bazel`:

```starlark
bazel_dep(name = "java_concurrent_j2cl", version = "0.1.0")
local_path_override(
    module_name = "java_concurrent_j2cl",
    path = "../java-concurrent-j2cl",
)
bazel_dep(name = "j2cl", version = "20260402")
single_version_override(
    module_name = "j2cl",
    version = "20260402",
    patch_strip = 1,
    patches = ["//third_party/java-concurrent:j2cl-concurrent.patch"],
)
```

The root override and root-owned patch copy are required: Bazel ignores
dependency-owned overrides and rejects external patch labels here. Refresh the
copy when updating the library. Version
`0.1.0` is a local module version, not a claim of publication to a registry. Use
the normal J2CL rules to compile the consumer; the patched JRE supplies the APIs.
`//:overlay-sources` exports the canonical source files for integrated JRE builds.
The validation script also builds and runs an independent consuming root module.

For combined MPS builds, merge IO/NIO/WebAWT and concurrency sources into one JRE
overlay and one root J2CL override. In particular, the current NIO profile has
signature-only `java.lang.Thread` and `java.util.concurrent.ThreadFactory`; replace
those with this profile's implementations. Merge `module-info.java` exports and
retain one source per fully qualified class. **Do not blindly apply the NIO and
concurrency patches together.** The independent consumer check verifies this
profile alone; a composed full MPS dependency graph is a separate integration test.

## Validate on Windows / WSL

Install the same Bazelisk/Ubuntu/JDK prerequisites used by the sibling migration
projects, plus a Chromium executable (the script discovers existing Playwright
Chromium installations). From PowerShell:

```powershell
.\tools\validate.ps1
# Or select the Ubuntu distribution and an existing Linux browser explicitly:
.\tools\validate.ps1 -Distribution Ubuntu -Browser /path/to/chrome
```

Direct WSL invocation:

```powershell
wsl.exe -d Ubuntu -- bash /mnt/c/Codex/java-concurrent-j2cl/tools/validate.sh
```

The scripts hold `/tmp/j2cl-build.lock` across validation and serialize Bazel jobs,
tests and workers. This was coordinated with the NIO worker. They stop only their
own Bazel servers between isolated workspaces; they do not restart WSL or change
shared memory settings. Cold J2CL builds also compile native tool dependencies and
can take several minutes.

Validation checks source ownership, pinned source hashes and patch reproducibility;
compiles all emulation code and consumer probes; then executes optimized JavaScript
in headless Chromium. Browser reports are written under `out/browser` and
`out/consumer/browser`. Tests cover actual task execution, exceptional paths,
cancellation, nesting, 10,000-stage propagation, collection/lock state, real timers,
and an unchanged MPS `SimpleLRUCache` production source fixture.

After changing canonical sources, regenerate the patch before validating:

```powershell
wsl.exe -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/java-concurrent-j2cl && python3 tools/make_patch.py'
```

The source inventory records all referenced class names; it is not a complete
Java 21 method-compatibility claim. Reflection-based atomic field updaters,
`ForkJoinTask`/recursive tasks, synchronizer frameworks, virtual threads, desktop
thread diagnostics and complete Java SE serialization are outside this profile.
No full MPS IDE build or browser migration is claimed.
