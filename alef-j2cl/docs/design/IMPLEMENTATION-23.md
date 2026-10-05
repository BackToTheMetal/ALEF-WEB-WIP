# Approved interface implementation

Reference: `archive/alef-interface-approved-r23-2026-09-27.zip` (immutable).
The current prototype and its history remain available unchanged.

## Accepted behavior

- Orange compact shell: Editor, Compare, Packaging, Deployment, Settings.
- Real generated Editor stays on latest workspace; package Contents is read-only.
- Equal outlined project icons; only held locks and changed package memberships
  have status icons. Lock comes before package, and stands rightmost when alone.
- Multiple open packages; semantic version, title, packaged baseline, selected
  saved changes. Unselected baseline contents are retained exactly.
- Add/remove package changes makes a new review revision and resets approvals.
- Exact saved-version tags filter the picker and are consumed on successful
  package inclusion, never on selection/cancellation/failure.
- Changed versions linked to open packages follow saves; inherited unchanged
  roots are edited in the workspace and can be added through Packaging.
  Historical versions stay pinned.
- Author assigns reviewers and finalizes; assigned independent reviewers approve
  or request changes against exact revision/digest. Diff discussions are retained.
- Finalized packages are immutable. Deploy/promote exact package in path order.
- Environment management lives in Settings; existing durable simulator remains
  explicitly identified until a real execution adapter exists.

## Delivery tracking

- [x] Archive complete preview and its history, including assets.
- [x] Implement real shell and project status presentation.
- [x] Implement durable selected package revisions and API.
- [x] Integrate version tags, saved edits and package linkage.
- [x] Implement review assignments, diff comments and finalization.
- [x] Integrate deployment and settings.
- [x] Run relevant isolated backend/frontend tests and package served assets.

Backend changes must preserve current OIDC, authorization, immutable retained
artifacts, native validation, editing leases, idempotency and save recovery.
Do not copy fixture identities, sample model data or simulated approvals into
the application. Keep legacy release evidence readable.

## Installed application

The real application is served at `http://localhost:8080/`. Sign in with an
existing local account. Navigation and controls follow that account's actual
permissions; the prototype's identity switcher and sample states are not used.

Frontend source lives in `example/interface.js`, `interface-ui.js`,
`interface.css` and the existing editor integration files. Served files are in
`out/backend-web`; `tools/package_backend_browser.py` includes the new assets.
The generated J2CL editor bundle was retained; its Java editing behavior was not
replaced by a mock editor.

Backend source is installed in
`C:\Users\niekh\Documents\ChatGPT\ALEFBackend`:

- `PackageService`, `PackageController`: selected package revisions, exact-version
  tags, linked saves, independent reviewers, comments, immutable finalization.
- `PackageComposer`, `ReleaseBuilder`: compose retained root versions using the
  native serializer and validate the resulting package. Retain unchanged
  baseline roots and resources; reject unexpected changes to unselected roots.
- Existing workspace/release/deployment services keep the authenticated account,
  leases, idempotency, immutable artifacts and save recovery.
- Migrations 16 and 17 add durable package workflow data and deployment order.
  Both were applied to the existing local database after backup.

Previously created releases remain accessible. The new selected-change picker
works on saved root versions. Configuration and other managed resources are
retained from the chosen baseline; they do not have a separate file editor in
this application. Deployment still uses the existing durable simulator, clearly
labelled in the UI, and does not execute services in production.

## Verification on 2026-09-27

- 11 isolated package workflow tests passed: parallel packages, exact version tag
  consumption, removal/baseline retention, linked saves across authors, permission
  withdrawal, reviewer aliases/service exclusion, approval reset, finalization,
  comment persistence and ordered deployment gating.
- 57 governance/database regression tests passed against disposable PostgreSQL.
- A real native-serializer composition test passed using isolated copies of the
  ALEF corpus: selecting one of two changes retained every other baseline root.
- Browser component checks passed for popup/tag filtering, read-only Contents,
  all-roots/changes filtering, line anchors, tab navigation, project collapse,
  status icons and responsive layout.
- The existing generated-editor browser regression passed: root navigation,
  scoped folders, filtering, keyboard actions, real cell rename/undo, closing
  tabs, and narrow layout.
- Authenticated browser checks against the installed application passed for the
  existing 15-root workspace, package selection, retained package comparison, and
  the administrator’s deployment view.
  These checks made no model, tag, package or deployment changes.

Loading follow-up: a signed-out session previously left the generated editor's
"Opening workspace" placeholder visible, even though login was required.
The application now shows a dedicated sign-in screen, explicit startup progress,
and retry/cancel recovery. The backend owns those states so the idle language
runtime cannot replace them with a loading message. Signing in restores the
requested Packaging or other workspace route. Live checks cover direct
`/#releases` entry and reload, real authentication and all 15 roots, plus
injected unavailable/expired responses and recovery without model writes.

Screenshots are in `out/interface-tests`. Test entry points:
`tools/test_interface.mjs`, `tools/test_interface_live.mjs`,
`tools/test_project_view.mjs`; backend tests live with the backend source.

## MPS comparison follow-up, 2026-09-27

Compare and package Contents now render each version with the generated MPS
editors. Both panes share the existing MPS runtime and repository. Each version
is loaded into temporary read-only models with unique model identities; native
node IDs are retained and cross-model references remapped within that version.
Each pane uses a read-only `NodeEditorComponent`. Native node IDs identify added,
removed, modified and moved nodes; Previous/Next selects the corresponding node on both
sides. Side by side and stacked layouts are available.

Package comments can be attached to the selected model node. Existing comment
anchors and revisions are retained. Editing remains in the main Editor.
Switching roots reuses the loaded models and replaces only the displayed editor
views. No extra bundle downloads, frames, WebAWT installation or language-runtime
startup are involved. Leaving comparison disposes its views and temporary models.
Snapshot loading runs outside editing commands, preserving workspace undo/redo.
It does not update packages or acquire editing leases.

The comparison API preserves each version's original model references and
reference context. The browser gives each retained model a temporary identity
to prevent collisions with the other pane or writable workspace. Missing
version-specific models remain unresolved instead of resolving in the workspace.
Missing read-only accessory models use the application's loaded
language dependencies; historical language/toolchain switching is not provided.

Validation: `tools/test_model_diff.mjs`, the interface component suite, three
backend `PackageComparisonTest` cases, the J2CL browser build, and
`tools/test_mps_diff_live.mjs`. The latter uses authenticated retained models
and isolated response copies to exercise a known change without persisting
test changes. It checks generated notation, node navigation, read-only typing
and Delete, comment selection, stacked layout, cleanup and workspace isolation.
Canvas pixel checks confirm the change highlighting is actually painted. The
live suite also checks one runtime, no new bundle/model loads on root switches,
and generated reference cells resolving to distinct before/after targets. The
standalone `tools/test_comparison_history.mjs` checks real edit/undo/redo across
comparison loading and disposal, including overlapping node/model IDs and
cancellation. Both suites passed against the published optimized bundle.
The installed package v1.6.1 was also checked in the browser, including an added
root with an absent baseline side. Screenshots are in `out/mps-diff`.

Local Chromium measurements for the same 244-node workspace: opening Compare
fell from 2,293 ms to 486 ms; selecting another root fell from 2,247 ms to 81 ms.
The optimized bundle is 16,247,169 bytes versus the previous 218,574,957-byte
development bundle. One bundle request serves the workspace and both comparison
panes. Results are recorded in `out/mps-diff/single-runtime-loading.json`; these
are local measurements, not a production latency guarantee.

## Save without reopening the workspace

Confirmed saves now acknowledge the submitted root snapshots in place, update
their version/context metadata and advance the snapshot used by the next save.
They do not navigate the page, recreate MPS models or editors, or clear history.
The current editor state and undo/redo are retained. Existing editing leases
remain active while valid. Saves cannot overlap, and their own workspace events
do not trigger a refresh. Resolve save follows the same completion path.

The native acknowledgement validates all submitted snapshots before clearing
their dirty markers. It does not change model contents. A server-side relocation,
reference repair or newer conflicting version therefore requires an explicit
refresh; the successful save leaves the current view intact and explains this.
If obtaining the saved metadata fails, the original operation remains resolvable
without submitting a second write. Validation refusals retain the editable draft.

`tools/test_save_without_reload.mjs` exercises actual generated MPS editors with
isolated in-memory API fixtures: consecutive saves/version advancement, retained
editor state, root switching with unsaved drafts, undo/redo, stale acknowledgement refusal, delayed own events,
422 validation refusal, lost HTTP responses, metadata-download failures and
unexpected server content. These checks make no live model or package writes.
The regression passed against both the development build and the optimized
bundle served by `http://localhost:8080/` on 2026-09-27. The optimized artifact
is 16,247,476 bytes; its SHA-256 is recorded in
`out/backend-browser-artifacts.json`. Live persistence mutation suites were
updated to await save acknowledgement but were not run against user content.

## Saving incomplete workspace drafts

The backend Save path now separates representability from model validation.
Native MPS nodes with missing required references or other model diagnostics can
be saved. Known concepts/features, unique node identities, permissions, editing
leases, expected versions and durable publication still govern acceptance.
Model validation remains explicit and is still used for release evidence.

Normal publication activates the worker's already loaded proposal instead of
loading all model documents again. Untouched root instances remain loaded;
references are rebound to the current nodes after submitted content changes.
Startup and failed-operation recovery still load the durable committed revision.
The browser continues to acknowledge saved versions in place.

Verification uses isolated worker state, a disposable PostgreSQL database and a
temporary Git repository. The native integration covers incomplete drafts,
unrelated-root preservation, tentative-operation fencing, malformed proposal
recovery and restart persistence. The complete WorkspaceService test covers two
consecutive incomplete saves, stale-version rejection, unchanged validation/load
counters, durable restart recovery and explicit validation afterwards.
PublicationCoordinator tests cover the five publication failure boundaries.
No test saves edits into the user's live workspace.

Installed and restarted locally on 2026-09-27. The authenticated workspace
snapshot returned READY with 15 roots and 131 nodes. Original backend sources
are retained in `out/backend-save-native/backup`, with before/after hashes in
`out/backend-save-native/changes.json`. Reference tests also cover dynamic and
unresolved 64-bit associations across native serialization and reload.

## Lock controls and immediate tree status

Locking now starts editing directly, without a package-selection dialog. New
package inclusion is chosen through Add changes in Packaging; already linked
changes continue to follow saves.

Lock acquisition, renewal and release now notify the interface using the
confirmed lease response. The project tree updates immediately; a package-status
refresh follows, with older responses ignored so they cannot reinstate a released
lock. Model leases update all affected roots. Periodic status polling remains for
changes made by other users.

One icon-only toolbar button switches between Lock root and Release lock, with
open/closed lock icons, accessible labels and tooltips. Routine lock confirmation
text is omitted. Pending actions and unsafe releases with unsaved drafts are disabled.
Successful saves leave the status area empty and disable Save when no changes
remain. Errors and actionable recovery messages remain visible.
Save and Tags use matching icons and button styling. Delete is an icon button
beside the lock, and Log out sits beside Settings. The editor overflow menu,
Move root/Move content buttons, and workspace name in the header are removed.
Reload workspace and Lock model are available in Settings; save/draft recovery
actions appear directly in the toolbar when needed. The approved preview
remains unchanged.

`tools/test_editor_locks.mjs` uses real generated MPS editors and isolated API
fixtures to check immediate status painting, stale responses, failed releases,
model leases, selection changes during release, draft protection and toolbar
layout. It makes no live workspace writes.

### View-only input without a lock

Native `EditorComponent.isReadOnly()` now checks the current root/model lease
before accepting cell input. This includes the Inspector, generated grammar
cells, typing and text insertion. Unlocked roots stay selectable; pending,
released, expired or frozen leases cannot accept temporary text or invoke
editing actions. A successful acquisition enables the existing view immediately,
without rebuilding the workspace. Undo/redo controls and shortcuts also respect
the active root's lease, retaining history for reacquisition.

The source changes are root-owned overlays reproduced by
`tools/adapt_backend_guard.py`, composed on the existing MPS extensions editor.
The input-method listener checks the same policy because browser text insertion
can bypass key-typed handling. The model mutation guard and backend lease checks
remain in place.

The expanded native lock fixture reproduces the former object-type exception
and temporary Inspector text before the fix. It now passes for unlocked,
pending, released, expired and frozen states, mouse selection, text insertion
after acquisition and retained undo after reacquisition. The optimized build,
source audit, native save-without-reload checks and comparison checks (including
text insertion) pass. These checks use isolated fixtures or read-only snapshots;
no live workspace content was changed. Evidence is in `out/lock-regression`.

## Single editor view

The root tab strip has been removed. Selecting a root in the project tree or
following a reference displays it in the one workspace editor view. Switching
roots preserves in-memory drafts and undo history; Save still saves dirty roots
in the workspace. Package workflow tabs are unaffected. The archived design
preview remains unchanged.

Verified against the served optimized bundle with isolated API fixtures:
`test_save_without_reload.mjs` covers root switching with an unsaved draft,
retained undo, one editor view, and disabled Save without confirmation text.
`test_editor_locks.mjs` covers direct locking of a root inherited by an open
package, without a package-choice dialog. The interface component suite also
passes. No checks wrote to the live workspace or rebuilt the native bundle.

## MPS editor appearance

The native editor theme uses 14px Segoe UI on Windows (the interface font),
with a generic sans-serif fallback on other platforms and 1.45 line spacing.
Generated ALEF keyword and natural-language styles inherit the theme font.
Warm neutral text, gutters and borders match the shell, with pale orange
current-row and selection highlights. Language-specific semantic colours and
comparison change highlights are retained. Editor, Inspector and read-only
comparison panes share the theme. These changes affect presentation only.

The optimized build passed. Browser paint inspection confirmed regular, bold
and italic 14px Segoe UI text. Isolated native edit/lock/save/undo checks passed,
as did authenticated read-only workspace and comparison checks against the
published bundle. Screenshots and font evidence are under `out/mps-theme`,
`out/interface-tests` and `out/mps-diff`. The previous native bundle and its
manifest are retained under `out/mps-theme` for recovery.

### Text-width correction

MPS cell widths now measure complete text runs with fractional browser font
metrics and round up once. Previously the compatibility layer rounded every
character before adding its width, so long Segoe UI names could extend into
the next table column. The root-owned `EditorFontMetricsImpl` override is
registered in `mps-access.json` and reproduced by `tools/adapt_tables.py`;
upstream MPS sources are unchanged. The same metrics handle caret hit testing.

`tools/test_mps_text_layout.mjs` reproduces the original 5.45px overlap and
verifies a 2.55px gap after the fix. Long names, repeated fractional advances,
kerning, precomposed accented text, cursor placement, editing and undo pass
at device scale factors 1, 1.5 and 2. Screenshots and measurements are under
`out/text-layout*`. This layout check does not cover composition/IME behavior.
The optimized build and source audit pass, as do native save/lock fixtures
and the published read-only MPS comparison checks. No live workspace data
was changed. The pre-fix themed bundle is retained in `out/text-layout`.

## Comparison text highlights

The comparison tree uses single-letter status badges with full hover and
accessible labels. Its native MPS panes show removed text in red and added
text in green, with neutral unchanged text. Generated labels are paired by
node, cell and role, then their text is aligned to highlight only changed
ranges. New/deleted identities, concept replacements and structural moves
mark whole node presentations; a retargeted reference is marked even when
its visible name is unchanged. The normal workspace editor keeps its syntax
colours. Both panes remain read-only and reuse the existing MPS runtime.

The root-owned `TextLine` overlay paints ranges using native text metrics;
`tools/templates/text-line-highlights.java.inc` and `tools/adapt_tables.py`
reproduce it. Text alignment respects surrogate pairs and bounds its LCS
matrix to one million entries, falling back to the differing middle of very
large labels. This does not change node contents, editor geometry or saves.

Validation: optimized build, pristine source audit, overlay reproduction,
JVM text-range tests, model-diff tests and interface component checks pass.
`test_mps_diff_live.mjs` checks actual canvas colours, text-only replacement
and insertion ranges, changed node IDs, same-name reference retargeting,
compact badges, retained models, read-only input, package comment anchors
and stacked layout. `test_mps_text_layout.mjs` confirms long names, caret
placement and undo still work in the regular native editor. Native checks
use read-only snapshots or isolated fixtures and make no live workspace
writes. Screenshots are retained in `out/mps-diff` and `out/text-layout`.
The prior served bundle and asset manifest are retained in `out/mps-diff`.

## Package contents independent of preparation (2026-09-28)

Contents no longer requires a completed release manifest. If preparation is
pending or failed, the backend loads the package's immutable selected root
versions through an isolated native contents reader. It retains baseline model
context and excludes unselected roots/versions. Reading never calls propose,
validation or publication, and does not reload the active editing workspace.
Prepared packages still use their retained manifest. The selected-contents cache
is keyed by immutable candidate; permissions and package revision are rechecked
after loading, outside the repository write lock.

The reader is configured by `ALEF_CONTENTS_WORKER_COMMAND` in the backend's
`tools/local-environment.ps1`, with a separate native state directory. Failed
preparation remains visible in Checks and continues to block finalization.
Pending-status polling preserves the MPS comparison while status is unchanged.

Validation: 20 package projection, comparison and SQL workflow tests pass,
including mixed saved versions, unchanged baseline roots, missing selections,
pending/failed jobs, permission changes and concurrent revision changes.
`tools/test_package_contents_live.mjs` passes against the served app and actual
v1.6.1 revision 10: 15 retained roots, All roots/Changes only, read-only native
MPS panes, Checks, disabled Finalize, pending-state polling and unchanged workspace.
`tools/test_interface.mjs` also passes. No live model/package writes were made.

The observed preparation job stopped at RETAINING_DEPENDENCIES: four compiled
RuntimeWorker classes no longer match the pinned release toolchain. This change
does not re-pin or rewrite that immutable candidate's validation evidence.
Source/served recovery copies and the verification screenshot are in
`out/package-contents-fix`.

## Deployment tab (2026-09-28)

Deployment follows the saved r23 design: an ordered environment path, installed
packages and status, a package-selection dialog, promotion to the next environment,
delivery progress and history. Readers can inspect this information. Only users
with the existing Deploy permission can deploy, cancel or reconcile deliveries;
this change grants no permissions. Environment approval retains its separate
permission check.

The picker offers only finalized packages with a current, approved, unrevoked
candidate. Deployment begins at the first environment; promotion carries the
same package digest forward in order. Backend permission, approval, configuration
and installed-digest checks remain authoritative. The configured targets are
durable simulators, explicitly labelled in the interface; no real runtime
deployment adapter was added.

Validation: 21 backend package-workflow and deployment-recovery tests pass,
including reader permissions and revoked-package rejection. The isolated
`tools/test_deployment_tab.mjs` checks selection, idempotent retries, promotion,
progress/history, read-only controls and responsive layout. The existing
interface suite also passes. `tools/test_deployment_tab_live.mjs` verifies the
published application as author and administrator, blocking deployment and
workspace writes. Served asset hashes match the publication manifest. No live
deployment, permission grant or package/workspace mutation was performed.

The native MPS bundle is unchanged. Source backups, prior served assets and
backend jar, and browser evidence are retained in `out/deployment-tab`.

## Recovery copies

- Approved preview and full design history:
  `archive/alef-interface-approved-r23-2026-09-27.zip` (see its adjacent README
  for the verified hash and restore command). The original preview is untouched.
- Pre-migration database:
  `C:\Users\niekh\Documents\ChatGPT\ALEFBackend\.local\backups\before-interface-r23-20260927.dump`.
  Its archive directory was verified using `pg_restore -l` before migrations.
- Replaced backend source:
  `out/design-implementation/backup/backend-source`, with a before/after SHA-256
  manifest in `out/design-implementation/backend-changes.json`.

These are local recovery copies; no remote backup or push was performed. The
preview archive preserves its files and iterations, not transient browser storage.
