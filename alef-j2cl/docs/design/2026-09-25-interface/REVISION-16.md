# ALEF design revision 16 — parallel packages and selective creation

Design-only refinement, 27 September 2026. Active preview:
http://127.0.0.1:18821/#releases

## Packaging

Multiple packages may remain open in review, changes requested or approved
states. New package is always available. Each table row shows its own baseline,
change count, revision and status. Creating or finalizing one package leaves
other packages untouched. Approval and immutable final packaging remain separate.

## New package popup

1. Enter a unique version and a name.
2. Choose a retained packaged baseline. A shortcut selects the confirmed version
   currently installed in Production; its value comes from the deployment fixture.
3. Choose complete root or resource changes from the saved workspace. Nothing is
   preselected. Select all and Clear support larger releases and small fixes.
4. Expand View change to inspect before/after content. The popup indicates changes
   also selected in another open package; these can be included independently.
5. Review the selected-change count, retained baseline items and resulting root /
   resource counts, then Create package for review.

Composition copies the complete baseline and applies only checked changes.
Unchecked modifications, renames, moves and deletions retain the baseline item;
unchecked additions stay out. Selected deletions remove only that item. Unchanged
roots and resources remain in the complete graph. Source content is captured from
the saved workspace when the popup opens; unsaved edits are excluded. Changing
the baseline resets the selection while preserving the entered version and name.
Cancellation creates nothing. Duplicate versions and empty selections are blocked.

The main Editor's workspace menu now links to New package and all open packages.
Its previous single-next-release inclusion controls are removed. Contents retains
All roots / Changes only and the fixed current package with selectable Against.

## Verification

Run `node verify-16.cjs` from this directory. Twelve Node VM checks passed, including
creation while other packages remain open; Production baseline selection; duplicate
and empty-selection guards; a config-only production fix retaining every other
baseline item; selected addition/deletion composition; unchanged-root inspection;
independent approval/finalization; cancellation; and saved-versus-unsaved content.
Composition is checked against complete expected graphs, not just counts.
JavaScript syntax checks passed for the active revision's four new scripts.

These are script/rendering checks with a minimal document fixture. Browser layout
verification remains unavailable because local preview access was blocked by the
browser security policy; no alternate browser access was attempted.

## Boundary

This is an in-memory design prototype, not a real release operation. All models,
checks, comments, approval and deployment data remain illustrative. Dependency
validation and real editor/package synchronization are not implemented. Refreshing
resets created packages. Existing orange styling and read-only package review are
retained. Earlier iteration files remain available; index.html and iteration-16.html
load the new design.
