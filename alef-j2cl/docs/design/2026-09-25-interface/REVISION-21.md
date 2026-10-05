# ALEF design revision 21 — package icons for included changes only

Project rows show a package icon only when the saved root version is included
as a change in an open or finalized package. Unchanged baseline roots and
pending selections have no package icon or package-status accessibility label.
The empty column preserves alignment after locks.

An inherited root gets its icon after the selected edit is saved into a package.
Workspace-only edits keep no icon until included. Finalized changed versions
retain their icon; inherited content alone does not qualify. The save-destination
choice, linked saves, approvals and locking behavior remain as in revision 20.

This is an isolated design change. Browser visual checks remain unavailable
under the existing security restriction. Earlier iterations are preserved.
