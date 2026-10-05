# J2CL main-thread concurrency implementation

## Objective and execution contract

Provide `java.util.concurrent` source compatibility for the MPS web migration using
only the pinned J2CL JRE and ordinary Java state. Submitted work must actually run
on the calling browser main thread. This profile does not provide parallelism.

* `execute`, `submit`, bulk invocation and asynchronous future stages execute
  inline. Results, exceptions, cancellation and executor shutdown remain observable.
* Reuse J2CL's existing atomics and concurrent collections where they already
  implement the required single-thread behavior. Add missing value-backed APIs.
* Lock acquisition uses local state; no native locks, monitors, workers, spin loops
  or operating-system services. A wait that cannot complete synchronously fails
  explicitly instead of freezing the browser or pretending it succeeded.
* Delayed and periodic work uses main-thread browser timers through J2CL's built-in
  JsInterop annotations. It never becomes an immediate infinite recurrence and
  requires no extra library or worker. Tests cover actual timer execution.

## Implementation sequence

1. Inventory the pinned J2CL JRE and actual MPS Java/Kotlin references. Record
   existing type ownership, source revision and unsupported migration patterns.
2. Establish Bazel 8.3.1 / J2CL 20260402 / Java 21 build integration using a
   reproducible JRE source overlay. Export a stable consumer target and document
   composition with IO/NIO overlays without duplicate classes.
3. Implement execution: futures, executor services, bulk work, completion queues,
   completion stages and main-thread timer scheduling. Test nested submission, failures,
   cancellation, shutdown and scheduled recurrence.
4. Implement missing atomic values, collections and synchronization helpers used
   by MPS. Preserve useful data semantics; explicitly bound blocking operations.
5. Compile independent consumer code and execute optimized J2CL JavaScript in a
   real headless browser. Include normal and edge-case behavior and source-level
   checks excluding JVM-only dependencies.
6. Deliver usage examples, API/behavior coverage, MPS migration boundaries and
   reproducible Windows/WSL validation commands. Report verified evidence separately
   from future MPS integration work.

## WSL coordination

The NIO worker confirmed no active NIO build and recommended the shared lock
`/tmp/j2cl-build.lock`. All heavy validation will hold that lock and use one Bazel
job, one local test job and one worker/multiplex worker. Always select Ubuntu
explicitly; the machine's default WSL distribution is not the build environment.
Do not stop WSL, change its configuration, or shut down another project's server.

## Acceptance boundaries

This is a documented main-thread compatibility profile, not Java SE concurrency
equivalence or proof that the complete MPS IDE runs in a browser. Reflection-based
field updaters, thread identity/interruption and blocking cross-thread protocols
require separate migration decisions. The concrete inventory will determine which
APIs can be safely reused, implemented, or explicitly excluded.

## Completed implementation and verification (2026-09-08)

All six implementation steps are complete for the documented main-thread profile.

* Inventoried 29,907 pinned MPS Java/Kotlin source files and all 48 referenced
  concurrency class names. The emitted JRE contains those 48 plus Thread and
  ThreadLocal (50/50 checked names).
* Implemented field-backed atomic extensions, executors/futures/completion stages,
  main-thread timers, collection storage, synchronization state and thread facades.
  Real browser testing also identified and corrected stock J2CL ThreadLocal's
  missing lazy reinitialization behavior.
* Passed 496 checks in optimized J2CL JavaScript in Chromium, including an unchanged
  MPS SimpleLRUCache source fixture, generation-pool hooks, queue completion,
  10,000-stage propagation and asynchronous timer lifecycle tests.
* Fixed the four follow-up review defects with 60 additional checks: losing race
  listeners are detached, minimal stages expose isolated futures, map callbacks
  may mutate live views during traversal, and immediate shutdown returns queued
  work before reporting termination.
* Passed an independently compiled root-module consumer and its browser test.
  The consumer uses a root-owned patch copy; source and copied hashes match.
* Verified Windows-to-WSL path conversion with `--exec`, canonical patch
  reproducibility, source snapshot checksums and the compiled type inventory.
  Builds held the shared WSL lock; only this task's Bazel servers were shut down.

See `docs/validation.md` and `compatibility/validation.json` for evidence. Remaining
work is integration of a selected MPS module graph with IO/NIO/WebAWT and adaptation
of actual desktop blocking/worker loops; full MPS compatibility is not claimed.
