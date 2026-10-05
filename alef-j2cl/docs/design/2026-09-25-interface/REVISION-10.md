# ALEF design revision 10 — comparison tree icons

Design-only refinement to Changes and the shared release comparison tree:

- Replace boxed R/F letter badges with the existing outline document icon.
- Give folder and document icons the same 16 × 16 px frame and 1.6 px stroke.
- Reserve a separate chevron column so folder/file icons and their labels align.
- Preserve change markers, tree typography, orange styling and navigation.

The active index.html and iteration-10.html use design-10.js, which only changes
the comparison row icon markup from design-08.js, and the focused revision-10.css.
The earlier iterations, application/backend and generated editor are unchanged.

Verified the served prototype at 878 × 791: folder and file icon frames both start
at x=37 in Changes and x=57.8 in release review, both measuring 16 × 16 px. No
letter badges remain in those trees. Selecting leeg opens its Deleted comparison.
The document width remains 878 px; no new horizontal page overflow was introduced.
JavaScript syntax checking passed for design-10.js.

- [Changes](revision-10-changes.jpg)
- [Release comparison](revision-10-review.jpg)
