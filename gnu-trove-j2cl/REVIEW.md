# Code review — 2026-09-08

Follow-up: the findings below are the original review record. Fixes and regression
tests have since been implemented and verified by the full Java 21 and
192-run browser campaign, including the related aliased map-view bulk removal
case. All seven validation-tool tests and the final archive consumer also pass.
See VALIDATION.md and the current validation results for verification.

There is more work before calling this compatibility profile complete. Three
functional defects were reproduced with a fresh compilation of the current
production sources and with the pinned original Trove binary. Matching the
original is therefore insufficient evidence of the documented collection
contracts in these cases.

## Confirmed functional defects

### P1 — Bulk list assignment overwrites the caller's array

`TIntArrayList.set(int, int[], int, int)` at line 301 calls
`ArraySupport.copy(_data, offset, values, valOffset, length)`. Source and
destination are reversed. The other four primitive list implementations have
the same defect.

Reproduction: start with `[1, 2, 3]`, then call `set(1, new int[]{8, 9})`.
The list remains `[1, 2, 3]` and the supplied array becomes `[2, 3]` instead of
the list becoming `[1, 8, 9]`. Integer and float variants were executed.

Correct the adaptation generator, regenerate all five families, and add
independent tests of both overloads, source/destination offsets, source-array
preservation, and aliasing. Record this as another deliberate upstream fix.

### P2 — Removing a null-valued mapping reports no change

`THashMap.KeyView.removeElement` at line 710 returns
`null != THashMap.this.remove(key)`. A present key mapped to null is removed,
but the method returns false. `keySet().removeAll(...)` inherits the incorrect
change result through `MapBackedView.removeAll`.

Reproduction: `map.put("x", null); map.keySet().remove("x")` returns false,
while `map.containsKey("x")` is then false. Distinguish presence from the old
value, preserving custom hashing-strategy equality. Test present-null,
present-non-null, and absent keys through both single and bulk view removal.

### P2 — Shuffle excludes valid permutations

The five primitive list `shuffle(Random)` implementations use
`rand.nextInt(i)` while swapping position `i`. This excludes that position
from the random choice. For two elements it always swaps the elements;
the original order is impossible. The integer reproduction returned the
swapped order for all 100 seeds.

Use the inclusive choice represented by `nextInt(i + 1)` and add a deterministic
test with a controlled `Random` implementation that selects the current
position. Test singleton, empty, and larger permutations as well. This also
requires an explicit upstream-behavior correction.

## Validation weaknesses found by source inspection

### P2 — Clone API differences bypass signature validation

`tools/check_api.py:36` labels any existing `clone()` member as adapted before
comparing its signature. This accepts more than the intended abstract-base and
covariant-copy changes: unexpected changes to return types, visibility, and
exceptions are not checked by this branch.

Replace the blanket exception with explicit expected adaptations for each
affected signature. Add a checker regression that deliberately changes a clone
signature and verifies rejection. Other compilation tests may catch some such
changes, but the API gate itself currently does not enforce this contract.

### P2 — Shared build locking and failure cleanup are missing

`tools/validate.sh:10` and `tools/test_consumer.py:63` start Bazel without the
agreed `/tmp/j2cl-build.lock`. Thus the documented validation commands can still
overlap NIO/IO or other project builds. One-job limits are local to each Bazel
server and do not serialize projects.

The consumer's shutdown at `tools/test_consumer.py:66` also runs only after
successful compilation and browser execution. An exception leaves its server
running; `set -e` similarly bypasses the shell shutdown on earlier failures.
Acquire the shared lock for the complete heavy validation interval, avoid
recursive lock acquisition by the nested consumer, and guarantee server cleanup
through `finally`/shell traps. Invalidate stale success reports at run start.

## What was checked

- Inspected the source generator, primitive lists, object-map views, hash and
  iterator code, contract generation, API comparison, and validation scripts.
- Freshly compiled all production Java sources into `out/review/current` with
  native JDK 17 and executed the focused probes. The original binary ran on a
  separate classpath.
- Empty-map callback/retention operations passed; the initial static suspicion
  about them was not substantiated and is not a finding.
- No heavy WSL build or new browser campaign was run for this review. The
  previous broad campaign remains useful evidence for the scenarios it covers,
  but it does not cover the three confirmed defects above.
- Production source and the existing release archive were not modified.

Probe source: [ReviewProbe.java](out/review/ReviewProbe.java).
Results: [current source](out/review/current-source.txt) and
[original binary](out/review/reference.txt).

Fix the functional defects and expand independent contracts first, then harden
the validation scripts and rerun JVM/development/optimized browser checks under
the shared lock. The broader MPS dependency inventory, Trove 2.1.0 profile,
full runtime integration, and release-environment gates remain as recorded in
[VALIDATION.md](VALIDATION.md).
