# ALEF design revision 18 — clear included tags on creation

Successful **Create package for review** clears all tags attached to the exact
saved versions included by the user's selections. This happens at creation,
not when checking a box and not at later approval or immutable final packaging.

Unselected files, other saved versions of the same file, and unselected versions
sharing a tag retain their tags. Cancelled or invalid creation preserves all tags.
Included deletions clear the tags of their saved deletion record. Package contents,
baseline records and existing packages remain unchanged by tag cleanup.

The main Editor's tags are synchronized so a subsequent save cannot accidentally
restore consumed tags. Newly entered pending tags remain pending; an unsaved new
content version retains its own tag choices. Removing a change from the created
package does not automatically recreate its consumed tags. New tags can be applied
explicitly in Editor when needed.

The creation popup and tag dialog explain this behavior. Subsequent New package
dialogs no longer offer consumed tag associations; shared tag names remain when
other versions still use them. Selection records retain version identity without
active tag metadata.

## Verification

`node verify-18.cjs` passes 10 isolated checks covering successful creation,
shared tags, cancellation and failed validation, deletions, older versus newer
saved versions, Editor synchronization, pending tags, removal and final packaging.
Complete saved/package graphs are checked for preservation. JavaScript syntax
checks pass for the three revision-18 scripts.

These are Node VM checks with a minimal document fixture, not browser layout tests.
The existing browser security restriction still prevents automated visual checks.
This remains an isolated, in-memory design prototype; real editor/backend data is
unchanged. index.html and iteration-18.html load revision 18; prior iterations remain.
