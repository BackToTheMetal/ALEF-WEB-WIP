# ALEF design revision 15 — read-only package contents

Design-only refinement, 27 September 2026. Active preview:
http://127.0.0.1:18821/#release

- The breadcrumb shows Packaging > v1.4.0 (or the selected package version).
- Contents replaces Changes. The separate release Editor tab, its editing
  controls and links are removed. Edits are made in the real editor.
- All roots shows every root present in the selected package, including
  unchanged roots. Managed resources remain available in their own group.
- Changes only shows differences against the selected baseline, including
  deleted roots. Switching filters keeps the selected node when it is visible;
  otherwise the first available node is selected.
- The package source is plain text. Only Against remains a selector. The main
  Compare page retains its two independently selectable sources.
- Revision history still supports explicit read-only inspection. Earlier
  revisions are labeled and provide Back to current package. Selecting Contents
  normally always opens the current revision of the selected package.
- Side by side / Unified and revision-scoped review comments work in both views.

## Verification

JavaScript syntax checks passed. A Node VM loaded the active scripts with a
minimal document fixture and passed 11 rendering/workflow checks: breadcrumbs,
tab/control removal, complete contents and deletion filtering, unchanged-node
inspection, selection fallback, baseline changes, historical inspection,
comments across filters, packaged releases, main Editor/Compare navigation,
and review through packaging. These are simulated script checks, not a browser
layout test. The preview server returned HTTP 200.

Automated visual verification was unavailable because browser access to the
local preview was blocked by the browser security policy. No alternate browser
access method was used.

## Boundary

This is an isolated prototype. All package contents, comments, approvals and
checks are fixtures in page memory. No real models, editor integration, backend
or deployment state was changed. Revision 14 remains available as a historical
iteration; index.html and iteration-15.html load the current design.
