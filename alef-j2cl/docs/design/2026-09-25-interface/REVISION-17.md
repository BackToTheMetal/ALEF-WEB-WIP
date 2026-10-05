# ALEF design revision 17 — tagged saved versions and package removal

Design-only refinement, 27 September 2026.

## Tag changes in Editor

The compact file toolbar has a Tags control beside Save. Acquire the file lock,
apply comma-separated tags, then save the file. Tag edits show as pending until
saved. Tags are normalized for consistent filtering (case-insensitive, trimmed,
duplicates removed). Tag metadata is separate from model contents and cannot
change an existing package's graph or approval.

Tags identify the exact saved snapshot. A subsequent edit starts with no tags;
the prior tagged snapshot remains available. Editing a pending draft retains its
unsaved tag choices. The generated-editor preview demonstrates this with the name
root. Seeded tagged versions for other roots/resources demonstrate grouped selection:
name-update, mapping, production-fix and cleanup.

## New package

View change is removed. The popup lists file names, change types, saved versions
and tags. All tags, individual tag filters, and Selected changes are available.
Select shown and Clear shown affect only matching versions. Selections outside
the active filter are retained and counted explicitly.

If several saved versions of one file match, choose one in its Saved version
selector. Selecting another version replaces the previous choice for that file.
Tagged older versions retain their actual saved content even when newer content
exists. Baseline composition and retention remain as described in revision 16.
Review diffs after the package has been created.

## Remove changes in Contents

Open packages expose Remove change on selected changes. Removal restores that
root/resource's exact baseline record; selected additions are omitted, while
selected deletions restore the deleted baseline root. It updates only this package
and records a new revision. Earlier graphs and comments remain in history.

Removing from an Approved package clears approval and returns it to In review.
Packaged, rejected, historical and stale revisions cannot be changed. Eligibility
is checked against the package's own baseline, regardless of the Against selector.
An empty package remains open but cannot be approved or finalized.

## Verification and boundary

`node verify-17.cjs` runs the prior package-composition checks and checks tags,
retained saved versions, filters, single-version selection, exact content inclusion,
removal of additions/deletions/renames/modifications, approval reset and immutable
history. These run in a Node VM with a minimal document fixture. JavaScript syntax
is checked separately; no browser rendering or real backend is exercised.

Automated browser layout verification remains blocked by the local-preview browser
security policy. No alternate browser access was attempted. All tag catalogs,
packages and edits are prototype data in page memory and reset on refresh. The real
editor and backend are unchanged. index.html and iteration-17.html load this design;
earlier iterations remain available.
