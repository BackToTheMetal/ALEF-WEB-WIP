# GNU Trove for J2CL

A Java source compatibility port of JetBrains
`org.jetbrains.intellij.deps:trove4j:1.0.20200330`, using only stock J2CL's
emulated JRE at runtime. It retains the `gnu.trove` package layout, hash
strategies, primitive storage, decorators, views, and callbacks.

The library includes 201 upstream source files plus two internal portability
helpers, selected from the pinned MPS sources and language models with their
implementation dependencies. Unused stacks and intrusive lists are removed.
See [the MPS requirements](docs/mps-requirements.md) for the exact retained set.
It targets the JetBrains platform API used by the tested MPS node-map
consumers. It does **not** replace the separately bundled Trove 2.1.0 used by
MPS's collections language, or Trove 3. See
[the exact compatibility boundary](compatibility/exclusions.md).

## Consume

Use the module `gnu_trove_j2cl` and depend on
`@gnu_trove_j2cl//:trove-j2cl` from a `j2cl_library`. Pin an immutable source
archive/revision with its checksum; this module has not been published to a
registry. The JVM target is `@gnu_trove_j2cl//:trove`.

Remove the original Trove dependency from the same consumer's compilation
graph. Both implementations define the same Java classes. No changes to
supported `gnu.trove` imports are needed.

```java
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TObjectIntHashMap;

TLongObjectHashMap<String> nodes = new TLongObjectHashMap<>();
nodes.put(9007199254740993L, "node");
TObjectIntHashMap<String> counts = new TObjectIntHashMap<>();
counts.put("node", 1);
counts.increment("node");
```

Java serialization is unavailable. Collection clones are explicit copies;
subclasses needing their own runtime type/state must override `clone()`.
Documented fixes correct several upstream collection defects. Review the
exclusions document before switching an existing application.

## Validate

Requirements: JDK 21, Python 3, Node.js, Bazelisk; Linux or Ubuntu under WSL.
The project pins Bazel 8.3.1, J2CL 20260402, and Playwright 1.62.0.

```sh
npm ci --ignore-scripts
npx playwright install --with-deps chromium firefox webkit
bash tools/validate.sh
```

On Windows, after installing the tools in Ubuntu:

```powershell
.\tools\validate.ps1
```

Validation checks artifact integrity and generated sources, compiles isolated
original/replacement JVM runs, compares public/protected APIs, builds J2CL
development and optimized artifacts, and executes them in Chromium, Firefox,
and WebKit. Four deterministic seeds exercise all primitive map combinations,
other collection families, and a 20,000-node MPS fixture workload. Separate
regressions assert intended corrections to original Trove defects.

The MPS map/strategy source files are unchanged. Their model objects are
test-only boundary values, not an implementation of MPS's runtime. These tests
verify the collection interactions; they do not establish that all of MPS can
run in the browser.

Results are written to `out/`. `compatibility/baseline.json` records exact
artifact and MPS fixture provenance. `compatibility/api.json` records the
compiled signature comparison. See `docs/architecture.md` for implementation
choices and `THIRD_PARTY_NOTICES.md` / `LICENSE` for source licensing.

To regenerate after an intentional port change, run `python3
tools/port_sources.py`; family test generation is `python3
tools/generate_family_tests.py`. Ordinary validation checks reproducibility
without overwriting sources. `tools/package_source.py` creates a reproducible
source archive including the original source JAR, modifications, tests, and
notices.

The independent consumer check builds the checksummed archive in a separate
Bzlmod workspace and runs its optimized bundle in all three browsers. Weekly
CI uses `tests/campaign-seeds.json` for 32 deterministic seeds. Run that campaign
locally with `TROVE_SEEDS_FILE=tests/campaign-seeds.json bash tools/validate.sh`.

The validation entry point and standalone archive-consumer check acquire the
shared Linux/WSL lock `/tmp/j2cl-build.lock`. Do not wrap these commands in a
second `flock` on the same file. They wait for other J2CL projects, hold the lock
through the complete run, and shut down their Bazel servers on stage failure or
interruption. Failed runs invalidate previous success reports. Direct manual
Bazel builds must use the same shared lock and shut down their server before
releasing it.

Validation includes tool tests for locking and failure cleanup, plus independent
regressions for upstream defects that differential comparison cannot detect.
`compatibility/api-adaptations.json` contains the exact reviewed clone-signature
changes; other signature differences fail the API gate.
