# Durable package contents

Implemented and installed locally on 29 September 2026. Backend source lives in `C:\Users\niekh\Documents\ChatGPT\ALEFBackend`.

Package display previously started an isolated MPS process when its four-entry, candidate-based memory cache missed. A review-only revision therefore lost reuse, as did a backend restart. An isolated empty worker startup measured 11.6 seconds.

## Implementation

`PackageContentsStore` retains derived model graphs as checksummed, chunked artifacts. Migration `V18__durable_package_contents.sql` adds the internal cache-key-to-artifact mapping. These records contain display contents only; they confer no validation, approval, or deployment eligibility.

- Workspace activation and saves retain the already decoded revision graph. This uses the existing graph; it performs no validation or native reload. A cache-write failure is logged and does not invalidate an otherwise successful save.
- A package view is composed from exact retained root versions and model context. Revision graphs and composed selections survive backend restarts.
- Keys include the configured decoder/toolchain fingerprint and a view-format version. Selection keys include the baseline digest and ordered root identities, versions, storage revisions, locations and deletion state. Reviewer/candidate metadata is excluded. Returned workspace revision metadata is updated to the requesting candidate.
- Old uncached revisions are decoded once using a separate contents worker. All missing dependencies for that selection share one worker, and their graphs are retained for later selections too.
- Only legacy native cache misses are serialized. Stored reads and compositions from available revision graphs do not wait behind an unrelated native load.
- Existing `PackageService` authorization and revision checks remain before and after loading. There is no public unauthenticated graph-cache endpoint.

## Verification

The package-store, contents, workflow and comparison tests passed against isolated PostgreSQL databases. Coverage includes restart/reviewer reuse, mixed retained revisions, edits/deletions, decoder invalidation, retries, concurrent loads, independent cached reads and denied access to cached data.

The real MPS save integration passed: incomplete drafts remain saveable, the exact saved graph is reusable without starting a second worker, and native model-load and validation counters do not increase. The two fixture saves took 0.900 and 0.256 seconds.

Live verification used the existing First version package, revision 6, without modifying its contents or reviewers:

| Stage | Contents request | Click to native editor |
|---|---:|---:|
| One-time migration of older uncached revisions | 13,295 ms | — |
| Subsequent stored reads | 52 / 54 ms | 253 ms |
| After another complete backend restart | 186 / 137 / 135 ms | 378 ms |

After the second restart, the contents-worker log did not grow: no new contents worker was needed. The complete retained graph and root records matched the pre-update data exactly; the browser reported no JavaScript errors.

Evidence and source backups: `out/package-contents-cache/`. Browser regression/profiling script: `tools/verify_package_contents_cache_live.mjs`. Its API requests only read existing packages; it expects the saved pre-update baseline in that evidence directory.

## Boundaries

An older revision that has never been decoded into the durable cache still has a one-time native loading cost. Changes to the configured toolchain fingerprint or view-format version intentionally invalidate derived views. MPS startup for the active workspace itself is separate from package loading.
