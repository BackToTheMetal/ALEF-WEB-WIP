# MPS concurrency usage inventory

The pinned MPS checkout at commit `1ae803e4f8c52e470c94563059eca4b4b8397c8c`
contains 29,907 tracked Java/Kotlin files. Of these, 294 refer to
`java.util.concurrent`, using 48 explicitly named classes; 341 refer to concurrency
or `Thread`/`ThreadLocal` collectively. The inventory includes tracked generated
sources and tests. It is not a claim that all of MPS can compile or run in J2CL.

The deterministic evidence is [mps-concurrent-usage.json](../compatibility/mps-concurrent-usage.json).
It records class-to-file mappings, per-file imports, heuristic method patterns,
and direct `Thread` call locations. Comments and strings are excluded. It does
not inspect serialized MPS models, IntelliJ jars, untracked files, or working-tree
edits. Method patterns are lexical: shadowed names can produce false positives;
chained calls and inherited methods can be missed. Use them to select follow-up
checks, not as an exact method coverage gate.

Regenerate against a local MPS checkout using Python 3 and Git:

```powershell
wsl.exe -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/java-concurrent-j2cl && python3 tools/inventory_mps.py /mnt/c/Work/MPS-Web --revision 1ae803e4f8c52e470c94563059eca4b4b8397c8c'
```

The script reads committed Git blobs and writes only its output file in this
repository. It uses a per-command `safe.directory` setting rather than changing
Git configuration. The script also runs on Windows when Python 3 is installed;
it needs no MPS build.

## Priority profile from actual callers

| Area | Evidence and required behavior |
| --- | --- |
| Atomic state | `AtomicReference` appears in 68 files, `AtomicInteger` in 38, `AtomicBoolean` in 30. Preserve read/write, compare-and-set using reference identity, arithmetic old/new return values, and update functions. Single-thread fields are sufficient. |
| Caches and listeners | `ConcurrentHashMap` appears in 55 files and `CopyOnWriteArrayList` in 40. Preserve map operations and snapshot iteration: listeners can mutate collections during callbacks even on one thread. |
| Executed tasks | `Future` appears in 31 files. Executors must actually run submitted `Runnable`/`Callable` bodies, expose results and failures, reject after shutdown, and preserve pool hooks used for accounting. |
| Model access | Lock acquisition need not arbitrate between threads, but lock hold counts and write ownership are observable MPS state. Retain balanced acquire/release and nested read/write state. |
| Completion chains | Eight files use `CompletableFuture`, six use `CompletionStage`. Preserve completion, mapping, failure propagation and callbacks; pending completion cannot synchronously block a browser event loop. |
| Scheduling | Editor completion/highlighting and caret code use delayed or periodic tasks. Timers must run on the main browser thread after the requested delay, and support cancellation. Immediately recursively executing periodic work would hang the browser. |
| Queues and signals | Producers that execute immediately can populate queues before consumers run. Keep queue contents, capacities, semaphore permits and latch counts; unsatisfied waits require explicit unsupported behavior or an adapted caller rather than a silent success. |

## Hotspots and acceptance examples

Paths and line numbers below refer to the pinned MPS commit.

- `core/generator/source/jetbrains/mps/generator/impl/GenerationTaskPool.java:58`
  overrides `ThreadPoolExecutor.afterExecute()` to decrement `tasksInQueue`.
  `waitForCompletion()` at line 98 loops until that counter reaches zero. Test
  several immediate tasks, nested submission, task failure, and the hook running
  exactly once per task; a missing hook can leave the caller spinning forever.
- `core/textgen/source/jetbrains/mps/text/TextGeneratorEngine.java:89` submits
  `queue::take` after scheduling text-unit generators. Each generator decrements
  an `AtomicInteger`, and the last writes the result queue. Test three generators
  executing, one final queued result, and `submit(queue::take).get()` returning it.
  A stubbed executor would lose all text generation.
- `core/smodel/source/jetbrains/mps/smodel/ModelAccess.java:88` implements
  `canRead()` with `getReadHoldCount()` and `isWriteLockedByCurrentThread()`;
  `canWrite()` at line 93 uses write ownership. Its subclass also calls
  `getQueuedWriterThreads()` at line 149. Test nested locks and restored state
  after `finally` blocks. Always-true or always-false lock queries are incorrect.
- `core/smodel/source/jetbrains/mps/smodel/ActionDispatcher.java:182` uses timed
  `Semaphore.tryAcquire()` to guard notification dispatch. Preserve permit
  changes so reentrant dispatch sees an already acquired guard.
- `core/openapi/source/org/jetbrains/mps/openapi/module/ModelAccess.java:106`
  creates a future and completes it from a read action. Test completion of a
  supplier result and attaching handlers both before and after completion.
- `core/kernel/source/jetbrains/mps/extapi/model/EditableSModelBase.java:181`
  chains a conflict-resolution stage through `thenApply`; several save outcomes
  use `completedFuture`. Test mapping, failure propagation and callback ordering.
- `editor/editor-runtime/source/jetbrains/mps/nodeEditor/Highlighter.java:526`
  schedules itself again. `cellMenu/CompletionHelper.java:67` schedules popup
  activation. Test that future timers do not run recursively on the same stack,
  canceled timers never fire, and repeating tasks cease on cancellation.

## Further migration boundaries

`java.lang.Thread` occurs in 60 source files and `ThreadLocal` in 18.
`NamedThreadFactory` also uses `ThreadGroup` and thread priorities. These APIs,
`Object.wait/notify`, `Runtime.availableProcessors()`, Swing dispatch, and IntelliJ
executors are separate integration dependencies: replacing `java.util.concurrent`
alone does not remove them from MPS. Blocking event-loop waits and worker loops
need caller adaptation; a Java `Thread.start()` implementation that directly runs
an infinite worker loop would freeze the browser.

Some less common imports are tied to optional desktop or test areas:

- `ForkJoinPool` is in the generated workbench background-task scheduler.
- `DelayQueue`/`Delayed` are in `TryRunPlatformWriteHelper`.
- `LinkedTransferQueue` is in an execution plugin's fake process handler.
- `ThreadLocalRandom` occurs only in `EDTExecutorTest` in this snapshot.
- `AtomicMarkableReference` is used by workbench make and repository virtual files.
- `CyclicBarrier` and related exceptions occur in several concurrency tests.

These classes belong in the coverage manifest even when a browser profile
intentionally excludes their callers. Keep unsupported classes and methods
explicit. The inventory establishes prioritization; consumer compilation and
browser execution remain independent acceptance gates.

The implementation now includes the less common referenced classes as explicit
main-thread subsets: ForkJoinPool supplies the executor/ManagedBlocker operations;
DelayQueue preserves expiry ordering; transfer and multi-party barrier waits fail
promptly when they cannot complete. Class presence does not imply full method or
blocking-protocol compatibility. The independent production fixture
`tests/mps/jetbrains/mps/util/SimpleLRUCache.java` is unchanged and exercises this
profile's map, linked queue and atomics together in the browser tests.
