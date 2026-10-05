# ALEF design revision 03 — one release workflow

**Design only.** This updates the standalone prototype, not the browser application
or backend. The prototype makes no API calls. Package contents, root versions,
hashes, review identities and decisions are illustrative. Reload resets the demo.

Open http://127.0.0.1:18821/#release for the package review screen, or select
**Releases** for the combined list. The former `#pulls` route opens this same screen.
The main navigation is now **Model · Changes · Releases · Environments**.

## Combined workflow

There is one Releases area. It contains packages being prepared and reviewed as
well as accepted and released packages; there is no separate pull-request entity,
navigation destination, or later package-building step.

1. **Create release package** from a saved workspace revision, after inspecting
   Changes. Choose its title, intended release version and independent reviewer.
2. Freeze and retain the complete package before review. Validation evidence is
   attached to this exact package. A real implementation exposes preparation and
   validation progress; the visual preview starts with illustrated completed checks.
3. Review **Changes, Included roots, Package history and Checks**, with an Overview
   tab for description and activity. Accept, request changes or reject.
4. **Release package** becomes available after independent acceptance. It releases
   the already accepted bytes. It never rebuilds, refreshes roots, or serializes a
   different package under the accepted digest.
5. Deploy the released package to the selected environments. Each delivery retains
   its own authorization, gates and status. Acceptance and release remain separate
   from deployment outcomes.

## Root versions are permanently pinned

The Included roots view makes the invariant visible: stable root identity, model,
fixed root version and content hash. All roots in the complete workspace package
are pinned, including roots with no changes relative to the baseline. The default
view highlights three example roots; Show all exposes the complete 15-root manifest.
The proposal also pins model metadata, reference context, managed resources,
dependencies and toolchain versions needed to reconstruct that exact graph.

Root names come from the previously inspected workspace. The `demo-root-*` IDs,
version numbers and shortened hashes are explicitly illustrative, not extracted
from the running backend. A real manifest must use actual stable MPS identities
and retained version/content identifiers, never a pointer to “latest”.

The logical release is the continuing review conversation. Each package is an
immutable candidate within it. Mutable workflow metadata is kept outside the
package, with append-only review/audit events in the eventual implementation.

| Object | Behavior |
| --- | --- |
| Correction draft | Editable under normal permissions, leases and Save rules |
| Package contents and root manifest | Never modified after package creation |
| Release's current candidate | Advances only when an explicit replacement is created |
| Acceptance | Bound to one package ID, digest, manifest and validation evidence |
| Released package | Same bytes and root versions that were accepted |

## Requested changes

**Prepare replacement** opens a correction draft based on the exact reviewed
package, rather than whatever happens to be current in the shared workspace.
After Save, review the correction and choose **Create replacement package**.
This produces a new package ID and digest in the same logical release. It gets
new validation evidence and starts with zero acceptances.

Example shown by the prototype:

| Root | Package 2 | Replacement package 3 |
| --- | --- | --- |
| Gegevensmodel | v18 · a731d1c0 | v18 · a731d1c0 |
| serviceapartmodel | v9 · 63b94ae2 | v9 · 63b94ae2 |
| ServiceTest | v12 · 12a30b8d | v13 · 82ab34ce |
| Other 12 roots | Fixed versions | Same fixed versions |

Package 2 permanently keeps ServiceTest v12 and digest `9c1f72b4…6f08`.
Package 3 contains the corrected v13 and has digest `c93a403d…201a`. Only package
3 can then receive the current acceptance and be released. The earlier package,
its evidence, feedback and decisions remain inspectable in Package history.

Corrections do not silently incorporate unrelated workspace edits. Changing a
dependency or model/reference context also produces a new package. Replacing an
included root must validate the complete resulting graph. An already released
version is not reopened in place; later work requires a new release version.

The isolated correction-draft workflow and explicit per-root manifest are proposed
extensions. The prototype does not implement isolated MPS repositories, root
version storage or a new backend review engine. Existing one-workspace behavior
must not be implemented by resetting the live shared workspace to an old package.

## Preserved layout and environments

The generated inspector remains the only Inspector content, in the bottom panel
beside Diagnostics. The generated editor capture, project tree, lock/Save states
and loading preview retain revision 02's layout. No shortcut panel is restored.

Integration, Regression and Acceptance remain separate test environments, alongside
Production EU. More environments can be added in the visual preview. Promotion
uses the exact released package and each target's selected prerequisites. All
targets remain visibly **SIMULATED**.

The environment status table is an independent illustrative deployment scenario;
reviewing a package does not update installed versions or submit deployment jobs.
The deploy dialog offers the current candidate only after it is released and not
revoked, or the retained prior release. Revocation blocks new jobs without changing
package bytes, previous installations or history.

## Responsive behavior and prototype boundaries

On narrow screens, review panels stack, the package selector occupies its own row,
and wide manifests scroll inside their container. The editor retains its project
drawer and generated-inspector/diagnostics bottom panel.

The demo automatically shows an illustrative author/reviewer role per screen to
explain the workflow. It is not authentication or an authorization implementation.
Generated editor content is a screenshot, comments are illustrative, and Save and
acceptance only demonstrate visual states. Creating a new release resets the
single active illustrative candidate; this is not a persistent release database.

## Verification

The in-app browser walkthrough exercised request changes on package 2, opening
its correction draft, Save, review correction, and creation of package 3. The new
package showed v18/v9/v13 and zero acceptances. Selecting package 2 again showed
the original v18/v9/v12, original source/digest, and no current acceptance button.
Accepting package 3 and releasing it retained its exact digest and manifest.

The combined list's Create release package flow was also exercised: it opens
package 1 directly in review, with zero acceptances and all roots pinned. Show all
15 roots rendered 15 manifest rows. Desktop screenshots were captured at 1440 ×
960. At 390 × 844, the Changes and Included roots views and the environment page
each had a document width of 390 px, with wide tables contained in their panels.
The JavaScript syntax check passed and the browser reported no console errors
during the final walkthrough.

These checks cover the standalone design interactions, not application/backend
functionality. Captures: `revision-03-releases.jpg`, `revision-03-review.jpg`,
`revision-03-pinned-roots.jpg`, `revision-03-released.jpg`, and
`revision-03-review-narrow.jpg`.

## Files

`index.html` loads `styles.css`, `revision-02.css`, `revision-03.css`, `icons.js`,
`prototype.js` and `release-workflow.js`. The last script replaces the previous
review screens while reusing their editor/environment controls. `iteration-02.html`
loads only the previous scripts and styles, preserving the prior proposal.

Serve locally with `node preview-server.cjs` from this directory; the existing
loopback server uses port 18821. No application build or restart is required.
