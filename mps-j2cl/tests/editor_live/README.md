# Live editor acceptance fixture

`ExpressionLanguage.java` is a handwritten Java runtime language. It is not generated
MPS output, a generated sample, or a distribution of `jetbrains.mps.lang.core`. Its
small accompanying bootstrap runtimes supply BaseConcept, INamedConcept/name and
RuntimeTypeVariable/name/pullUp contracts required by this isolated fixture. They
do not deploy the complete core/typesystem languages or BaseLanguage Type hierarchy.
The named-variable inheritance is essential: the real equation engine keys runtime
variables by `SNode.getName()`, which returns null without INamedConcept metadata.
The fixture asserts distinct variable names before testing dependent equations.

The fixture uses the real MPS structure descriptor builder, concept registry,
behavior descriptor defaults, property constraints, editor descriptor/cell factory,
property accessor, incremental updater, model events, inference/equation solver,
typesystem editor checker, highlight messages, completion matching, and intention
lookup/execution. A non-typesystem rule emits a separate warning, and actual MPS
undo actions are exercised through the browser UndoManager after completion and
intention edits. Both model/cell values and diagnostics must follow undo/redo. The
fixture closes and recreates the editor/project once before native keyboard input.
Each cycle also rejects a second live host after initializing transfer caches,
verifies that the original application, model access, registry and transfer manager
keep their exact identities and original model/cells, and runs the edit,
typechecking, clipboard and undo contracts afterward. An additional real editor
increases host tracking from one to two; disposing only that editor must restore
the count to one while the original editor remains usable. These assertions cover
the ownership failure and retained-editor regression without relying on garbage
collection timing. The host reserves model access before replacing application
globals, limits transfer cleanup to its own registry, and snapshots its editor
list before disposal callbacks can remove entries.
It also exercises original icon assets, explicit resource origins with stripped
Class metadata, unload/reload invalidation, and MPS's unchanged search entrypoints.

`FindUsagesContracts` creates two additional attached memory models and tests real
reference traversal, exact/nonexact concept-instance queries, model-scope limits,
consumer results, retargeting, root removal and explicit/implicit model imports.
The host installs the actual `FindUsagesManager` before editor startup and releases
it during teardown. This includes the eight basic search implementation classes;
desktop finder-plugin discovery, `FindersManager` and search-tool windows remain
outside the selected profile.

The Group/Reference extension inserts and removes real child nodes, changes a
reference to another model root, and edits that target's property. Real updater
contexts build the child/reference cells; the MPS equation engine propagates the
last child's type to its group and the referenced literal's type to its use.
Commands, undo/redo, cells, diagnostics and inferred types must agree without
recreating the incremental session. These are model-command tests; they do not
claim keyboard-driven structural editing or a production reference-scope aspect.
The in-memory model extends `EditableModelDescriptor`; loading,
root edits and event dispatch remain inherited. Saving/reloading fail explicitly.
Its `MemorySolution` explicitly reports that it is not packaged. A default fileless
MPS `Solution` is read-only, which correctly prevents the highlighter from attaching
model listeners. The fixture verifies writable module metadata before registration.

Synchronous contracts schedule their diagnostic checks explicitly to test each
undo/redo state. The native keyboard phase clears those fixture-owned messages,
changes the model to an invalid literal, and waits for the browser host's real
`Highlighter` and `TypesEditorChecker` to publish a diagnostic automatically.
Typing a valid integer must then remove that diagnostic without a manual checker
update. Message conversion, targeting and painting use `NodeHighlightManager`.
The runner also rejects visible stale "Expected an integer literal" tooltip text
after the edit. The hint controller cancels both move and hover alarms on typing,
clicking and uninstall, preventing a delayed hover from recreating that message.
The fixture also saves/restores an actual text selection with stripped Class
metadata, copies selected text through MPS's label action and the WebAWT clipboard,
pastes it through the real label action, and checks undo/redo afterward.
The browser host installs focused Swing bindings for Ctrl/Meta+Z undo and
Ctrl/Meta+Y or Ctrl/Meta+Shift+Z redo. They call the real project UndoManager and
consume the shortcut before MPS's text handlers; they never modify model text
directly. Empty history still consumes a recognized shortcut to prevent the native
textarea from applying unrelated text history. Parent input/action maps preserve
other editor bindings; disposal restores the prior maps only while the host's own
maps remain installed. The fixture exposes the property's actual screen rectangle
for native mouse acceptance without fixed click coordinates or a Java helper that
selects the cell.
Intention availability uses the original one-second delay through a cancellable
Swing-thread Alarm. Focus loss, selection replacement and editor disposal cancel
pending work. The adapter checks the captured selected node and edited root before
and after the real intention lookup; no model lock spans the delay. Browser hooks
count actual intention-factory queries and showing LightBulbMenu children so the
runner can verify delayed availability and cancellation without test-only UI.

The host explicitly makes the real editor focusable. After mounting it, the native
input hooks request focus and assert the AWT focus owner; the runner also checks
that the editor's native textarea receives DOM focus. The input proxy retains the
MPS editor as the AWT event target and is removed during disposal. Queued Swing
validation/repaint work discards detached peers, so a stale browser tick cannot
re-enter a disposed editor's layout after its model context has been released.
Passive hints do not request focus on show or move it back on dismissal unless
the popup actually held focus. Swing `requestFocusEnabled` and IntelliJ
`requestFocus` remain separate from focusability, preserving editor input while
allowing explicitly activated popup controls to receive focus.

`tools/test_editor_live.mjs` runs each development/optimized/stripped bundle in
Chromium, Firefox and WebKit. After the Java contracts, it clicks the painted
property cell, types a new character, invokes every Ctrl/Meta undo/redo variant,
and checks the real model value. It also verifies that history shortcuts do not
activate after focus moves outside the editor. The runner then requests a
visible diagnostic state, checks actual diagnostic pixels, captures the editor,
types through the WebAWT DOM keyboard bridge, and observes the model property
change and automatic diagnostic/pixel removal. It verifies delayed intention
availability, blur cancellation, and disposal with an intention request still
pending. Its reports and screenshots are written to `out/editor-live*`.
Optional arguments select an engine
and one bundle, for example `chromium editor_live_browser_dev`.

Composition coverage is deliberately recorded per engine. Chromium uses CDP
`Input.imeSetComposition` and `Input.insertText` against the actual focused native
textarea: preedit changes `7` to `78`, commit preserves `78` without duplication,
another preedit produces `789`, and cancellation restores `78`. Firefox and WebKit
dispatch DOM `CompositionEvent` sequences for the same model/bridge contract;
this is synthetic-event coverage, not native IME input in those engines. The
Chromium driver exercises the browser's native input pipeline, not a physical
operating-system IME or its language-specific candidate-selection UI.

Read the [current live browser report](../../out/editor-live-browser-results.json)
for each engine/build result and bundle SHA-256. Its `historyInput` records native
mouse typing, keyboard undo/redo and focus isolation; `imeCoverage` distinguishes
native Chromium composition from DOM-event contracts. Unexpected page errors or
Java/browser console errors fail the run even when Java assertions have passed.
The [full validation report](../../out/validation-summary.json) records the build
and other required gates. Check the current results after changing source or
adding regressions; an earlier successful matrix, compilation, or the test source
alone does not establish acceptance of the latest bundle.
