# Backend implementation status

Work in progress. This document records verified results rather than treating planned
components as delivered. The acceptance contract is section 16 of
[the technical design](backend-technical-design.md).

## Preview checkpoint, 2026-09-25

The local application is running at `http://localhost:8080/` with real local
Keycloak, PostgreSQL, Git model storage and the supervised native MPS worker.
See [the preview guide](backend-preview.md). A fresh served optimized Chromium
journey passes login, complete authorized loading, lease acquisition, keyboard
editing, governed Save and reload (`out/backend-demo-editor.log`). The sample
workspace is intentionally small. The user has deferred 100k-workspace performance
tuning to try the application first; the measured limitations below remain open.

The installed runtime still uses `native-idea-discovery`. File-per-root native
persistence, a narrower plugin dependency closure, an OpenAPI endpoint and the
two-build release verification CLI have additional source changes staged under
`.work/backend-implementation`; those changes are **not installed or verified**.
They must not be counted as delivered. This checkpoint is a working preview,
not completion of the full section 16 acceptance checklist. Execution-platform
results are durable simulations and never execute ALEF services.

## Workspace baseline

- Browser integration: `C:\Codex\alef-j2cl` (the outer directory has no Git repository).
- Existing backend: `C:\Users\niekh\Documents\ChatGPT\ALEFBackend` (Git `master`,
  no commits at inspection; existing files are untracked and must be preserved).
- The upstream ALEF checkout has not been modified by backend implementation.
- The existing four-module backend was rebuilt from source with tests rerun on
  2026-09-24. Gradle reported success. Evidence:
  `C:\Users\niekh\Documents\ChatGPT\ALEFBackend\build\implementation-baseline.log`.
- The pinned ALEF cache contains MPS 2025.1, JBR 21.0.6, compiled language classes
  and persisted ALEF example models. These are compatibility inputs, not a claim
  that the application already integrates with that runtime.

## Acceptance tracking

| Requirement | Implementation and verification status |
| --- | --- |
| Native ALEF runtime and persistence | All four native language runtimes loaded. 157 real persisted models / 909 roots / 43,298 nodes passed native serialization/reload and an edit in each profile. Native IDEA services work for six representative models. Five browser allowance cases now match native Testspraak and types; remaining persistence edge cases and runtime minimization are pending. |
| Provider equivalence | `alef-workspace-store` provides immutable snapshots on Git, filesystem and PostgreSQL. Seventeen shared provider contracts pass, including all six migration routes. Fifteen real ALEF recovery cases also pass across all three providers and all five publication boundaries, with content restored into another provider before recovery. |
| Root authorization and protected data | Real OIDC/API leakage tests pass root denial, stale snapshot/cursor refusal, hidden-ID exclusion, opaque-edge preservation and forged-edge rejection. Current local account disablement and Keycloak revocation are exercised against real sessions. Remaining review/download and structural-operation cases are tracked below. |
| Exclusive leases and tab ownership | Server-registered tab clients, session revocation and transactionally checked ownership/expiry; governance suite: 56 tests, zero failures in the earlier run. Real tests pass distinct tabs, stale generations, administrative recovery, the same lease after restart, and complete local rollback when a command edits a locked root then attempts an unlocked root. Deferred automatic quick-fix lease checks are being verified. |
| Shared files and structural edits | Real OIDC/API test saves separate roots in one native file without lost updates. Browser factory creation/deletion passes all four root profiles. Cross-model root moves and contained-node moves there and back preserve identities through native Save/reload. Chromium also passes guarded undo/redo and explicit insertion order. Metadata operations and remaining reference cases are under verification. |
| Governed durable save and restart | Fifteen real ALEF/MPS crash-boundary tests pass using all three storage providers and fresh native processes (474.776 seconds of test execution). Served Chromium and WebKit complete login, full load, lease, keyboard edit, Save and reload; both verify saved graph equality after backend/native-worker restart. |
| Full authorized loading and progress | Consistent session-owned full graph snapshots, checksums, cancellation/expiry and policy-epoch checks implemented. The browser initializes nodes in bounded batches with measured progress. Chromium/WebKit pass runtime cancellation and retry (244 nodes in the current integration corpus). The editor test compares every loaded authorized root, descendant and reference with the API snapshot before editing. Large-load capacity remains pending. |
| Live collaboration and event recovery | Durable ordered SQL outbox and authenticated opaque replay cursors implemented. Real OIDC/API replay and revocation tests pass. Served Chromium applies a remote graph while preserving an independent dirty root, clears prior undo history, and saves the draft. Administrative lock-loss rejection, reacquisition/comparison and draft Save also pass. Overlapping conflict choices and cross-browser collaboration remain under verification. |
| Release immutability and independent approval | Browser submission, isolated native semantics/Testspraak, retained complete source package, model/resource diffs, whole-package ACL and independent digest-bound browser approval pass. V10 joins the reused review/job transactions. Changes-requested/rejected/revoked transitions are exposed; post-submission-edit and revocation-at-dispatch integration checks remain pending. |
| Multiple simulated deployment targets | V11 durable stub adapter and versioned test/production configuration are integrated with the browser. Real OIDC test passes simulated delivery, promotion, idempotency, timeout reconciliation, failures, cancellation and independent environment approval. Restart reconciliation and rollback remain under verification. Real execution integration is excluded. |
| Provider migration and coordinated restore | All six synthetic migration/restore routes and fifteen native provider-change recovery cases pass. A matching 1.11 GB SQL backup plus model-history archive was restored to an isolated PostgreSQL model provider, then migrated to filesystem and Git. Real login/edit/save/restart and historical package/source/deployment provenance pass Chromium, Firefox and WebKit on filesystem and Git. The complete 1.49 GB retained dependency archive also verifies after restore. |
| Capacity measurement | Native and full-application measurements completed at 10k/100k/1m requested nodes. The default 512 MiB API supports the measured 10k workload, but misses the Save target; 100k Save and 1m import failed. A 1 GiB API trial completes 100k Saves and browser loading, with slow responses and snapshot quota rejections under concurrency. Background-job interference remains unmeasured; further performance work is deferred at the user's request. |
| Served Chromium/Firefox/WebKit integration | Real login, complete authorized graph comparison, generated-editor Save/reload and backend-restart comparison pass all three engines on the migrated filesystem and Git providers. The optimized 16.24 MB bundle passes the Git journey. Firefox uses the existing Linux installation and a private loopback relay to the real Windows PostgreSQL and Keycloak; Windows Firefox still has its mozglue assembly error. Final reference changes require another served-bundle regression. |

## Capacity evidence, 2026-09-25

Hardware: Intel Core i7-12800H, 20 logical processors, 34,029,113,344 bytes
Windows RAM. WSL Linux 6.6.114.1-microsoft-standard-WSL2 exposes 16,215,088 KiB
RAM and 4 GiB swap. The baseline API heap is 512 MiB; the native worker heap is
2,048 MiB. These are local measurements, not production sizing guarantees.

The synthetic scaling corpus retains six real ALEF models (128 nodes) and adds
ObjectModels with native identities, copied ObjectTypes/facts/roles and remapped
references. Added roots contain at most 1,000 nodes, with ten roots per added
model and approximately 0.124 references per node. This measures scaling;
the separate 157-model corpus establishes real ALEF compatibility.

| Requested nodes | Native load | Native proposal samples | Full application at 512 MiB API |
| --- | --- | --- | --- |
| 10,000 | 2.128 s | Five: 2.14–3.26 s, then 0.18–0.35 s activation | Passed 1/10/20 clients and Chromium full load |
| 100,000 | 6.325 s | Five: 5.15–6.58 s, then 1.94–2.98 s activation | Bootstrap passed; 7.12 s single-client graph load; first Save failed with `WORKSPACE_RECOVERING` |
| 1,000,000 | Refused | 154,109,194-byte native request exceeds 128 MiB protocol limit | Bootstrap exhausted the 512 MiB API heap before publication |

For 10,000 nodes, 1/10/20 distinct editing clients share one real authenticated
login. One writes a 1,000-node root five times while the remaining clients load
snapshots. They are API clients representing tabs, not twenty rendered browsers.

| Clients | Full snapshot load p95 | Governed Save p95 |
| --- | --- | --- |
| 1 | 534.5 ms | 3,692.6 ms |
| 10 | 2,941.8 ms | 8,556.7 ms |
| 20 | 4,330.9 ms | 4,704.0 ms |

The served optimized Chromium application loaded the complete 10k workspace in
2,899.6 ms at 100 Mbit/s and 10 ms configured latency, including assets. First
visible progress appeared at 203.4 ms. JS heap used 86,175,128 bytes; total heap
146,227,200 bytes. API plus worker peak RSS was 4,259,092 KiB. Native-only process
peak RSS was 2,251,780 KiB at 10k and 3,453,180 KiB at 100k. Sampled native heap
reached 936,111.9 KiB and 2,097,152 KiB respectively; GC time was 0.855 s and
3.646 s. The API's 100k failure run showed repeated full collections at its heap
limit; it returned `WORKSPACE_RECOVERING` during Save.

The subsequent 100k trial used a 1,024 MiB API heap and the same 2,048 MiB worker.
Bootstrap took 36.988 seconds. All five Saves completed at each concurrency level;
snapshot quotas rejected some concurrent loads explicitly with HTTP 429.

| Clients | Successful full snapshot load p95 | Governed Save p95 | Snapshot quota rejections |
| --- | --- | --- | --- |
| 1 | 6,391.7 ms | 18,981.8 ms | 0 |
| 10 | 15,214.0 ms | 11,440.9 ms | 4 |
| 20 | 24,733.0 ms | 27,658.6 ms | 14 |

Chromium loaded the complete authorized 100k graph and optimized assets in
9,898.8 ms at 100 Mbit/s and 10 ms configured latency. Progress first appeared at
276 ms. JS heap used 303,679,296 bytes (345,632,768 bytes total); API plus worker
peak RSS was 5,140,532 KiB. Evidence: `out/backend-capacity-1024-run.log`,
`out/backend-capacity-application.json` and
`out/backend-capacity-browser-100000.json`. Successful requests do not establish
that the latency targets or twenty simultaneous browser sessions are supported.

Evidence is retained in `out/backend-capacity-baseline-512/`, including JSON,
native logs, application logs and `jstat` samples. Generators and measurement
scripts are in `tools/make_backend_capacity_corpus.py`,
`tools/measure_backend_native_capacity.py`,
`tools/measure_backend_application_capacity.py` and
`tools/measure_backend_browser_capacity.mjs`. The installed default 10k node
admission limit and 32 MiB import limit refuse the whole workspace explicitly;
they never substitute partial/lazy loading. Larger configurations need separately
measured heap and limit settings. Background-job interference remains pending.

## Native compatibility details

The probe runs against the pinned JVM distribution rather than the J2CL port.
Assembling and loading the actual ALEF and JSON plugin artifacts resolved the
Testspraak source-module classloading issue. The real corpus passes native graph
round trips, with stable model attributes and serialization. No source-backed model
is saved or changed. This evidence does not yet cover every acceptance edge case.

Servicespraak validation calls ALEF's `MPSUtil.tryGetMpsProject`, which needs native
application/project services absent from the minimal `MpsEnvironment`. The probe
detects exceptions that MPS logs instead of returning as diagnostics and fails
closed. `IdeaEnvironment` now starts with separate native plugin classloaders;
flattening plugin JARs had introduced a conflicting JSON platform descriptor.
Six real representative cross-model and Testspraak models validate without rule
exceptions and produce two semantic diagnostics. Full-corpus checking encounters
upstream Tijdspraak rule failures and generation; it is not a release-ready corpus.
Five synthetic allowance examples exported from the actual browser repository
now produce matching native Testspraak outcomes (PASS, FAIL, PASS, FAIL, PASS),
native/browser type results, and identity/reference-preserving native reloads.
The generated browser editor cases pass Chromium, Firefox and WebKit. Evidence:
`out/backend-allowance-parity-run.log`, `out/backend-allowance-parity.json` and
`out/service-test-optimized/`. These 60-node synthetic examples supplement the
separate real persisted ALEF corpus; they are not additional upstream samples.

The native worker implements private inherited-pipe RPC, an in-memory native
repository, root proposals, fenced tentative state, activation and committed-state
reload. Runtime API tests use the actual pinned Java 21/MPS installation and real
persisted models exported through MPS. Evidence:
`C:\Codex\alef-j2cl\out\backend-worker-integration.log`.
The additional served browser/governance evidence is listed below.

The pinned IDEA bootstrap emits a platform service-initialization warning, and
`IdeaEnvironment.dispose` emits an asynchronous shutdown exception in the probe.
Native worker shutdown closes the project and then exits its owned process. These
lifecycle boundaries need further hardening before the full application is ready.

The local Keycloak 26.7.4 provider is installed and serves OIDC discovery at
`http://localhost:8180/realms/alef/.well-known/openid-configuration`. Its archive
SHA-256 is pinned in `tools/setup-keycloak.ps1`; generated development credentials
and realm import are ignored local files. Real Chromium/WebKit OIDC evidence is in
`C:\Codex\alef-j2cl\out\backend-login-tests.log`. PostgreSQL 18.6 runs locally on
port 54329; application APIs run on port 8080. No external target is deployed.

The initial Git workspace contains six real source-backed samples imported through
MPS, with identities and initial root revisions registered in PostgreSQL. The
application now owns a supervised worker and gates readiness on matching native,
storage and governance revisions. API idempotency evidence is in additive migration
V7. The original migration files remain unchanged.

Diagnostic output: `C:\Codex\alef-j2cl\out\backend-runtime-probe.log`.
The broad plugin registration is a discovery configuration; a supported runtime
closure must be established before production use.

Fresh governance regression evidence: `C:\Codex\alef-j2cl\out\backend-governance-test.log`
and backend `alef-governance-postgres/build/test-results/test` (56 tests, six suites,
zero failures/errors). Baseline across all original modules: 93 tests, eight suites.

Storage evidence: `C:\Codex\alef-j2cl\out\workspace-store-linux-tests.log`.
The filesystem provider requires atomic rename and directory fsync and fails closed
where unavailable (including the tested Windows filesystem API). Run that provider
on Linux/WSL; Git and PostgreSQL also run on Windows. All revisions and prepared
evidence are retained initially; garbage collection is not implemented.

## Integrated verification evidence

- `out/backend-resume-browser-build.log`: served development bundle builds
  successfully with the new guards and loading batches (316.878 seconds).
- `out/backend-content-move-integration-3.log`: actual ObjectType containment
  move between roots, native persistence, undo/redo, insertion order and cleanup
  pass in Chromium with no uncaught page errors.
- `out/backend-loading-recovery.log`: Chromium and WebKit cancel initialization,
  dispose the partial runtime and retry to a complete 244-node authorized graph.
- `out/backend-matching-backup.log`, `out/backend-matching-restore.log`:
  offline matching model/governance/artifact capture and isolated restore pass.
  Backup revision: `ecdfab4765be4636bb48fd7da1ac7bd2bfca3206`.
- `out/backend-restored-application-journey.log`, `out/backend-restored-editor.log`,
  `out/backend-restored-restart-editor.log`, `out/backend-restored-provenance.log`:
  restored PostgreSQL provider passes actual editor Save/restart, historical
  approved release and deployment inspection, and checksum reconstruction of
  1,490,934,146 dependency bytes from 356 authenticated chunks. Original Git-backed
  application is restored afterward; its live model contents were not overwritten.

- `out/backend-native-all-provider-recovery.log` and `.xml`: fifteen real native
  recovery cases pass with no failures, errors or skips. Each restores model
  content to another provider; these cases retain their original governance
  database, so they do not prove coordinated database restore.
- `out/backend-moves-integration-2.log`: Chromium restores an interrupted earlier
  move, verifies root moves in both directions, then verifies complete rollback
  of an actual MPS command rejected on its second root.
- `out/backend-session-rotation.log`: real Keycloak strict single-use refresh
  tokens survive twelve concurrent requests and application restart; local and
  provider account revocation pass. Temporary realm settings are restored.
- `out/backend-backup-compile.log`: the new offline matching backup/restore CLI
  compiles. Later matching backup/restore and provider journeys above establish
  end-to-end verification.
- `out/backend-content-move-integration-2.log`: failed test exposed deferred
  automatic quick fixes attempting writes without a lease and automatic whitespace
  insertion during a whitespace-node move. Dispatch checks and explicit insertion
  positions were added; the later `backend-content-move-integration-3.log`
  verifies the corrected served bundle.

- `out/backend-native-recovery-tests.log`: five actual ALEF/MPS process-restart
  cases at prepare, governance acceptance, storage publication, finalization and
  activation; zero failures, errors or skips.
- `out/backend-application-tests.log`: three authorization/projection and five
  PostgreSQL/Git coordinator boundary tests passed. A subsequent distribution
  install initially encountered a Windows JAR lock, then succeeded after stopping
  only the owned application.
- `out/backend-workspace-integration.log`: native shared-file concurrency and
  original-operation idempotency with real OIDC sessions.
- `out/backend-webkit-editor.log`, `out/backend-editor-integration.log`:
  successful WebKit and Chromium generated-editor journeys respectively. The
  latter also contains the earlier failed WebKit login attempt; use the dedicated
  WebKit log for the corrected run.
- `out/backend-browser-restart.log`: both engines reload the exact saved graph
  after the Java application and native worker restart.
- `out/backend-event-tests.log`: cursor binding and five durable publication/
  outbox-replay tests pass against real PostgreSQL and Git.
- `out/backend-events-integration.log`: full native graph, shared-file saves,
  session-bound SSE cursor, replay and acknowledged-cursor checks pass.
- `out/backend-authorization-integration.log`: real root denial, stale snapshot/
  cursor refusal, hidden native-ID exclusion, exact protected-reference preservation,
  forged-handle rejection, audited administrative lease recovery, stale generations
  and separate-tab ownership pass with actual OIDC sessions and native publication.
- `out/backend-firefox-linux.log`: Firefox completes real login, full loading,
  generated-editor keyboard input, governed Save and reload. The relay gzip-compresses
  the unchanged served development bundle; no API or identity responses are mocked.
- `out/backend-structure-integration.log`: ObjectModel, Regelgroep and Service pass
  factory creation, guarded undo/redo, native Save/reload and deletion. ServiceTestSet
  correctly rejects its initially missing obligatory entrypoint; selecting that
  reference through the generated editor is under verification.
- `out/backend-testset-complete.log`: ServiceTestSet now also passes after selecting
  its required entrypoint through the actual completion menu. Native-derived model
  imports are accepted only with the model structural lease, advancing every
  affected root context in the same governed publication.
- `out/backend-collaboration-integration.log`: two real browser sessions verify
  draft-preserving remote refresh, exclusion of remote work from local undo, and
  administrative lease-loss recovery followed by durable Save.
- `out/backend-release-integration-3.log`: real browser submission and independent
  approval, native Testspraak PASS, exact package hashes, retained dependencies,
  contributor exclusion and complete package-scope authorization pass.
- `out/backend-deployment-integration-2.log`: real browser deployment/configuration,
  test/production promotion, durable idempotency, timeout-after-acceptance lookup,
  deterministic failure modes, cancellation and independent environment gate pass.
  All target outcomes explicitly identify simulation; no services execute there.
- `out/backend-release-evidence.json`: original retained six-model sample validates
  with two informational diagnostics, one passing native ServiceTest, and unchanged
  persisted source. Later integration-created names containing spaces correctly
  failed release validation. `out/backend-draft-repair.log` records their correction
  through ordinary root leases and governed Save; no workspace reset was used.
- `out/backend-release-tests.log`: package archive reproducibility and binding-based
  resource rename tests pass. This proves deterministic archive assembly, not yet
  two independent end-to-end native builds.
- `out/backend-session-tests.log`: real OAuth2 token objects round-trip through
  authenticated encryption and reject tampering/wrong keys.
- `out/backend-persistent-login-4.log`: Chromium and WebKit pass real code/PKCE
  login, PostgreSQL identities, HttpOnly cookies, CSRF, separate tabs and logout.
- `out/backend-session-lifecycle-3.log`: encrypted PostgreSQL sessions and the
  same-tab root lease survive an application restart; real 75-second Keycloak
  tokens refresh under twelve concurrent requests; local account disablement
  takes effect immediately, and provider disablement within the 60-second check.
  Temporary provider settings and disabled accounts are restored by the test.
- `out/backend-portable-history-linux.log`: seventeen provider contracts pass,
  including all six migration routes, canonical historical revision identities,
  exact prepared evidence, restart and new writes after migration. These fixtures
  are synthetic bytes; native model evidence is recorded separately.
- `out/backend-structural-browser-build.log`: actual served browser bundle built
  successfully; structural user-journey verification is in progress.

The native save implementation reconstructs all roots from reference descriptors
captured before any detachment. This fixes an observed MPS direct-reference issue
that otherwise lost target-model identities after replacing another root. Its
cost must be included in capacity measurement; no scaling claim is made yet.

The retained discovery runtime archive contains 2,046 files and 1,490,934,146 bytes.
It pins the installed MPS/ALEF/plugin closure and compiled native worker. This is a
broad dependency profile, not a claimed minimal production runtime. Immutable source
packages reference that independently retained, checksum-addressed artifact; the
stub validates delivery only. Heavy checksum scans now run outside the governance
coordination transaction and immutable verified artifacts are cached per process.

Keycloak 26.7 requires the introspecting confidential client in the access-token
audience. Development provisioning now installs an explicit `alef-browser`
audience mapper (`tools/ensure-keycloak-audience.ps1` updates an existing local
realm without resetting users). The provider's audience check remains enabled.
Reference: [Keycloak audience validation](https://www.keycloak.org/docs/latest/server_admin/#_audience).
