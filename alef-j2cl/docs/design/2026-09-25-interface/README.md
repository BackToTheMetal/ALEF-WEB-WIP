# ALEF interface design study — 25 September 2026

**Revision 23 is the active prototype.** When no package icon is present, its
column collapses and the lock icon sits at the right edge of the project row.
When both icons are present, the lock remains before the package icon.

Package icons have a transparent
background, matching their project row while retaining the orange outline.

[Package icon visibility](REVISION-21.md):
only saved root versions included as package changes have an icon. Unchanged
baseline roots and selections not yet saved have none.

[Linked package edits](REVISION-20.md):
the Project tree shows package membership after the lock icon. Included changed
versions follow Editor saves into their open packages and reset approvals.
Editing an unchanged root offers a choice to update a package or keep the change
in the workspace for a new package. To try it, select **ibm**, lock it, then use
**Preview edit**. Finalized packages remain immutable.

[Additional changes and reviewer workflow](REVISION-19.md):
authors use **… → Add changes…** to select more saved versions into an open
package, preserving its existing contents. **Add reviewers** assigns the people
who must approve. Authors see **Finalize**; assigned reviewers see **Review…**
with approval and change-request actions. Diff comments record the current
person. Every content revision requires fresh approval from all reviewers.
Use **View as** in the study toolbar to try each fixture identity.

[Package creation consumes tags](REVISION-18.md):
successful **Create package for review** clears tags on exactly the selected saved
versions. Other versions retain their tags. Selection, cancellation and failed
creation do not clear anything. The Editor and subsequent New package filters
reflect the cleared tags immediately.

[Saved change tags and removal](REVISION-17.md)
extend package selection. In Editor, lock the file, use **Tags**, and save to attach
tags to that exact saved version. **New package** filters by those tags, including
retained tagged versions after a newer save. Its file list no longer previews diffs;
review them in Contents after creation. **Remove change** restores baseline content
in an open package, retaining revision history and resetting any approval.

[Multiple packages can be open](REVISION-16.md)
for normal releases and production fixes. **New package** opens a selection popup:
choose a packaged baseline (with a shortcut for the currently deployed Production
version), enter the version and name, filter by tag, and select saved root/resource changes.
All other baseline content is retained. Selections and reviews are independent
for each package. The main Editor no longer chooses membership for a single
"next release"; selection belongs to this creation popup.

Packaging retains the read-only
[Contents tab](REVISION-15.md) with **All roots** and **Changes only** filters.
The breadcrumb shows the package version. The package being inspected is fixed;
only the comparison baseline is selectable. The separate release Editor and its
draft controls are removed; changes will be updated in the real editor.
The main Editor preview represents the latest workspace and simulates linked
package saves; no real editor/backend integration is implemented here. The [collapsed project panel](REVISION-13.md)
uses a slim rail with a clear reopen control. Navigation is Editor, Compare,
Packaging and Deployment. The [project lock indicators](REVISION-12.md) show
ownership with hover details; unlocked roots show no icon. Editor and comparison trees use matching 16 px
outline folder/file icons in one aligned column. Read [the orange visual refresh](REVISION-09.md)
for the retained accent palette. The [compact file-header design](REVISION-08.md)
retains the single toolbar and expandable release controls. The [environment design](REVISION-07.md)
retains the simplified table, environment Settings and fixed package promotion path.
The [revision 06 lifecycle](REVISION-06.md) is retained, with its single-open-release
restriction superseded by revision 16. Each new package starts from a chosen
packaged baseline plus selected saved workspace changes; subsequent updates are
made in the real editor. Changes to an Approved
release must return it to In review. **Finalize**, after
approval, is the immutable boundary; the final status is **Packaged**. Rejected
releases are closed. Deployment remains separate and SIMULATED.
The existing generated editor/inspector captures and visual style are retained.
The text below records the original proposal. Earlier iteration HTML files are
historical; index.html shows the active revision.

**Design only.** The application, backend, models, permissions, approvals and
deployment records have not been modified. This directory contains an isolated
HTML prototype and screenshots. It makes no requests to the ALEF API. Buttons
demonstrate navigation or illustrative states; they do not acquire real locks,
save models, approve releases or deploy packages.

Open `index.html` locally, or use the running preview at
**http://127.0.0.1:18821/**. Select Editor, Compare, Packaging or Deployment in the top
navigation. The Preview selector demonstrates saved/draft, lock loss, loading
and disconnected states. Project, inspector and diagnostics panels collapse.

## Observed application

The live application at `http://localhost:8080/` was inspected in the in-app
browser. The initial narrow tab had an expired change connection; its screenshot
is preserved as `current-interface.jpg`. A second existing tab was reauthenticated
through the existing local SSO session, without entering or exposing credentials.
Its desktop editor and approved release were inspected without editing content.

- The authenticated workspace displayed six models and fifteen roots, a project
  search/tree, document tabs, a generated ObjectModel editor and generated
  metatags inspector.
- The existing toolbar puts locking, Save, structural operations, recovery,
  logout and release navigation in one horizontal row. At the original narrow
  width it wraps over multiple lines and leaves little editor space.
- The inspector currently sits below the editor. Long root/model names are
  clipped in the project tree and document tabs.
- Release history and review use stacked dialogs. The inspected retained release
  was approved, with one passing Testspraak test and two informational mapping
  diagnostics. Changes are expandable and identified by long model/node IDs.
- The signed-in `author` account does not expose deployment management. Its
  existing permission checks, platform configuration, history, promotion,
  rollback, progress, cancellation and reconciliation controls were inspected in
  `C:\Codex\alef-j2cl\example\backend-workflows.js`. The deployment proposal is
  grounded in those implemented controls; it is not a screenshot of a live
  administrator session or evidence of a newly executed deployment.

The two original diagnostics are shown in shortened English in the prototype.
Counts, names, revisions, release diffs, timestamps, hashes, identities and
workflow states in proposed screens are illustrative. In particular, the proposed
awaiting-review candidate is not the approved candidate inspected in the live UI.

## Design direction

A quiet IDE shell: warm off-white surfaces, dark green-gray text, a single muted
green action color, subtle one-pixel dividers, small corner radii and no decorative
gradients. Amber indicates pending/attention states, not successful execution.
Status is always accompanied by text or an icon rather than color alone.

| Token | Proposed value |
| --- | --- |
| Main text | `#263832` |
| Secondary text | `#65736d` |
| Primary action | `#286650` |
| Background / panels | `#f5f7f5` / white |
| Borders | `#dfe5e1` |
| Spacing | 4 / 8 / 12 / 16 / 24 / 32 px |
| Shell typography | System UI; 12–14 px controls, 22–25 px page titles |

### Model editing

The top bar holds workspace navigation and the account. Model, Releases and
Deployments are distinct work areas. Permission-based visibility still applies;
showing an administrator view in this prototype is not a proposal to give authors
deployment rights.

A compact project panel sits left. The central editor retains document tabs and
gets most of the workspace width. An optional right inspector holds selected-node
context and the existing generated inspector. Diagnostics occupy a shallow,
collapsible bottom panel. The screenshots use the **real captured generated
editor**, displayed at its original scale; the DSL cells, colors and metatags
controls have not been redesigned or replaced with an HTML text editor.

The contextual toolbar makes the editing contract explicit: “Read only / Lock
root,” “Locked by you · this tab / Unsaved changes / Save,” or “Lock expired /
Resume draft.” Less frequent actions move into More: release lock, lock model,
move root, move content, delete root, reload and resume draft. New root stays in
the project panel. Existing shortcuts, intentions, quick fixes, reference
navigation and undo/redo remain part of the generated editor.

No live presence avatars or simultaneous editing are suggested. Collaboration is
represented by the connection, lock owner/scope, draft state and update/recovery
messages already supported by the backend. A future implementation must keep
dirty drafts through navigation and distinguish connected from stale states.

### Loading and recovery

Use a focused workspace-opening view with authentication, snapshot transfer,
model initialization and reference resolution as separate stages. Show measured
bytes/nodes/roots for a stage when the backend/runtime supplies reliable totals.
Otherwise use an indeterminate indicator with the stage name; do not invent an
overall ETA or weighted percentage. The illustrative loading mockup shows known
root initialization progress, not measured current loading performance.

Cancellation disposes partial initialization and offers retry. On session expiry,
change-stream loss or lease loss, preserve the local draft and show a durable
inline notice rather than burying it in the toolbar. Reauthentication/reacquisition
must precede comparison and Save. Successful Save should only appear after the
existing governed publication finishes.

### Release review

A full review page replaces nested dialogs. The revision, baseline and complete
package scope remain above the evidence. Tabs separate model changes, resource
diffs, native validation and the manifest. A readable semantic summary puts model
names before raw identifiers; full IDs and checksums remain available in details.

The adjacent decision panel shows whole-package access, contributor independence,
validation and quorum, followed by the reason and approve/request-changes/reject
actions. Approval still binds the exact immutable package. An approved view would
replace decision controls with approval evidence and authorized revoke/deliver
actions; it must not alter the approved package or let later edits inherit approval.

### Deployment management

Test and Production have separate cards for configuration version, installed
package, required gates and delivery state. Promote clearly carries the same
package identity forward. Rollback remains a new governed deployment of an
eligible previous package. A history table separates platform/purpose/status and
shows uncertain outcomes as reconciliation work, not successful delivery.

The page banner, platform badges and detail dialog say **SIMULATED**. This design
does not introduce real execution. Configuring a stub, choosing faults or cancelling
delivery stays within the existing backend contracts.

## Existing functionality versus proposed presentation

| Existing capability | Proposed presentation / new UI work |
| --- | --- |
| Tree, search, tabs, root creation | More space-efficient tree, root type glyphs, predictable truncation, collapsible panel |
| Generated editor and inspector | Unchanged editor host; inspector placed in a side panel with a bottom-dock fallback for wide language-specific inspectors |
| Root/model locks, Save, undo/redo, recovery | Contextual toolbar, explicit tab ownership, inline draft/recovery states, More menu |
| Complete authorized loading with progress/cancel | Dedicated stage-based opening view |
| Native diagnostics and release validation | Consolidated bottom diagnostics panel; wiring workspace diagnostics into this panel is proposed UI work |
| Release submission and immutable review evidence | Dedicated review pages, readable model names, highlighted semantic before/after comparison |
| Review/reject/request changes/revoke | Role-aware decision panel and approved-state actions; backend gates unchanged |
| Test/production durable stub configuration and jobs | Environment cards, history table, promotion and rollback detail views |
| Ordered remote changes, exclusive leases | Clear connection/lock/draft messaging; no new live-presence service |

The human-readable comparison is a proposed rendering of existing semantic
changes; it is not a claim that the current application has a native graphical
diff editor. The prototype's editor and inspector captures are static, not runnable
MPS controls. A later implementation must mount the current generated components
and notify their hosts of panel resize rather than scale screenshots.

## Responsive behavior

- **Desktop, over 1180 px:** project + central editor + right inspector. All
  supporting panels can collapse. The model toolbar stays separate from global
  navigation.
- **Tablet, 801–1180 px:** the inspector becomes an on-demand drawer; the project
  remains visible. Review comparisons stack when needed.
- **Narrow, 800 px and below:** project and inspector become drawers, navigation
  takes its own row, and Lock/Save remains visible with text status. The generated
  projectional editor scrolls within its own surface instead of shrinking text.
  Release evidence precedes the decision panel; environment cards stack. Wide
  deployment history scrolls inside its table wrapper.

Dense projectional editing remains better suited to a larger display. This design
improves small-screen access and review without asserting that complex generated
editors have become touch-first editors. Keyboard focus, drawer dismissal and
native host sizing require implementation-level accessibility testing later.

## Artifacts and verification

| Artifact | Contents |
| --- | --- |
| `current-interface-desktop.jpg` | Live authenticated editor, 1440 × 960 |
| `current-interface.jpg` | Original narrow live interface, including expired connection notice |
| `current-release-review.jpg` | Actual retained approved-release review |
| `proposed-editor.jpg` | Main editing proposal, 1440 × 960 |
| `proposed-release-review.jpg` | Independent review proposal, 1440 × 960 |
| `proposed-deployments.jpg` | Separate simulated platform management, 1440 × 960 |
| `proposed-editor-narrow.jpg` | Compact editor proposal, 390 × 844 |
| `proposed-loading.jpg` | Full-model loading proposal |
| `proposed-lock-loss.jpg` | Preserved-draft / lock-loss proposal |

Visual checks were performed in the Codex in-app browser at 1440 × 960,
1024 × 768 and 390 × 844, with an additional initial 1280 × 720 rendering. Both images used by
the editor view load. Desktop project collapse/reopen was exercised. On the
390 px viewport, model/release/deployment document scroll widths were each 390 px;
the project drawer opened and closed. The 1024 px editor also had no document
overflow and its inspector drawer opened and closed. No browser console errors
were recorded during these checks. Screens were visually inspected after
responsive adjustments. These are **prototype checks**, not cross-browser or
backend acceptance tests.

To restart the standalone preview, run `node preview-server.cjs` from this
directory. It binds only `127.0.0.1:18821` and serves this design directory.
The source application continues on port 8080.
