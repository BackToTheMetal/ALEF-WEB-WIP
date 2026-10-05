# Editor refresh after test execution

The reported workflow is editing a test while its failed result is displayed,
then running that test again. The existing isolated sportclub reproduction could
pass on the previously published bundle, so it did not establish a unique cause
for every stale result seen in the user's session. The investigation found two
gaps in the common editor update path and reproduced a separate debug navigation
exception from that session.

## Update path

MPS normally collects model events during a command. `UpdaterModelListener`
rebuilds or synchronizes dependent cells, then calls `relayout()` and validates
the external component. These operations update geometry, scrollbars and paint.

ALEF also stores transient test results in node user objects. Its generated
`EditorUtil.verversEditor` lays out the old cells and then calls
`editor.getUpdater().update()`. That call builds new cells but does not finish
layout and repaint. Other result controls call the updater directly too.
Incidental mouse activity or a browser resize can make the new cells appear,
concealing the missing completion step.

Separately, the normal MPS `ModelsEventsCollector` collects node events only
inside commands. Temporary/runtime models may legitimately change within a
write action without a command. Such writes need to notify dependent editors
too, without creating user undo entries or changing workspace persistence.

The consumer overlay in `tools/adapt_editor_updates.py` now:

- Queues one layout, validation and repaint after each editor update or cell
  synchronization. Multiple updates in the same action coalesce.
- Observes property, reference, addition and removal events on the models already
  tracked by each editor. Writes outside commands queue a rebuild followed by
  the same layout and paint completion.
- Retains normal incremental updates for command-driven edits and ignores queued
  work after an editor is disposed.

This applies to MPS editor components, including inspectors. It does not reload
the workspace, validate user content or save transient debug results.

## Debug navigation

Clicking `?` beside a failed actual value reproduced the browser's null-service
exception. Generated ALEF code called `NavigationSupport.getInstance(project)`,
but the browser host had no registered service. `BrowserNavigationSupport` now
routes generated node navigation to the same cached editor and project selection
used by browser reference navigation. It does not create another workspace.

## Refresh geometry and regression coverage

The previous debug regression resized the browser to collect canvas text. This
could conceal missing repaints. Removing that resize also exposed a separate
geometry problem: `refreshRemote` recreated the inspector with its default
position even when the page's dimensions had not changed. The application now
retains and applies the actual editor and inspector dimensions when recreating
the inspector.

`tools/test_debug_results.mjs` observes rendering without forcing resize or
repaint. It edits a still-displayed failed test, clears and reruns, switches the
executed rule through an input change, corrects a runtime error, and checks that
debugging never modifies persistent workspace nodes. All APIs use isolated
fixtures. It also follows failed-value navigation to its service output mapping.

`tools/test_editor_updates.mjs` exercises two real generated editors. It checks
command edits, writes outside commands and explicit updater calls. Assertions
cover rendered text, removal of old canvas pixels and restored nodes without
mouse or resize assistance. Results are recorded under `out/editor-refresh`.

## Verified and published — 29 September 2026

- The two-editor update and pixel-removal contracts pass in Chromium and WebKit.
- The full failed-test correction, runtime-error recovery, batch cleanup and
  debug-navigation regression passes in both browsers. Chromium also passes
  against the published HTTP asset, without a bundle override.
- The lock/undo regression passes. A read-only check of the real authorized
  workspace opens and refreshes twice with all four roots and five standard
  models, without leases, saves or dirty nodes.
- The source audit confirms pristine upstream files and no duplicate types.

The new application bundle is served at `http://localhost:8080/`; its SHA-256
matches the publication manifest. `out/editor-refresh/publication.json` records
the hash and the previous-bundle backup. The user's open browser and unsaved
changes were left intact; an ordinary reload after saving loads the new code.

## Follow-up: failed-value cross appears only after a click

The specific remaining failure is reproducible when the test output is below
the editor's viewport when an individual test finishes. At 1168 by 764, running
the first sportclub test with expected contribution 175 and actual contribution
75, then scrolling down, shows the old output without the red cross. Clicking
that result causes the cross and actual value to appear. The larger viewport in
the earlier regression had already painted the output, masking this defect.

The cells and test result are current; the offscreen canvas pixels are stale.
MPS repaints within its visible rectangle. The local Swing `JViewport` moved the
view and synchronized its scrollbars but did not request painting when scrolling
exposed another rectangle. Both its native scroll callback and `setViewPosition`
now repaint the visible portion of the view. This keeps normal clipped rendering
and does not rebuild or reload the workspace.

`tools/test_debug_results.mjs` now includes the short-viewport reproduction and
checks actual red pixels, clipping, and unchanged workspace nodes after wheel
and scrollbar navigation. Before the fix, it fails with “Expected value was not
repainted after scrolling”; the saved screenshot shows the missing indicator.

With the fix, the complete debug regression passes in Chromium and WebKit. Both
show the cross's red pixels immediately after wheel scrolling and after dragging
the editor scrollbar, without a click in the result. The source audit also
passes. The verified bundle and previous bundle are recorded in
`out/test-indicator/publication.json`; publication does not reload the user's
open editor or discard its unsaved changes.
