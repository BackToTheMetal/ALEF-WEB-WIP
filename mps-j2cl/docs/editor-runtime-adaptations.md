# Browser editor host adaptations

All upstream Java replacements remain in `mps-web` at the original MPS path.
The project-sized Bazel targets and exact exclusion mappings remain root-owned.

## Host ownership and disposal

`BrowserEditorHost` reserves the single browser model-access owner before changing
`ApplicationManager`. A rejected overlapping host therefore leaves the running
application, model access, language registry and transfer manager intact. Cleanup
captures the host's own `LanguageRegistry` and passes it to
`DataTransferManager.releaseInstance(owner)`; that overload only releases a
singleton belonging to the exact registry. A partially constructed host cannot
clear another host's transfer caches or remove its registry listener.

An editor's disposal listener removes it from host tracking as well as the
highlighter and data context. Closing individual editors therefore does not retain
their component/node graphs until the whole host closes. Host teardown snapshots
and clears its editor list before invoking callbacks, so those removals cannot
skip another editor during cleanup. Application and platform services remain
available while editor/project listeners detach.

The live regression initializes real transfer caches, attempts a second host,
checks exact service identities and the original model/cell tree, then creates and
disposes an additional real editor. Host tracking must go from two editors back to
one. Existing edit, typechecking, clipboard and undo contracts subsequently run on
the surviving host; this sequence runs in both host cycles.

## Keyboard and selection deployment

`AWTKeymapHandler` uses the sibling AWT constants directly, with no reflection.
`tools/prepare_editor_runtime.py` reproduces that overlay, the selection
overlays and the browser paint bridges. `tools/check_editor_keycodes.py` compares every sibling constant with
Java 21: all 189 names/values match. The handler maps 186 names after excluding
Shift, Control and Alt. Both `VK_SEPARATOR` and the legacy `VK_SEPARATER` alias
remain usable, although the JVM implementation's single-value map loses one.
Character categories, modifiers, event matching and action-menu behavior remain.

`SelectionInfoImpl` and `NodeRangeSelection` restore compiled constructors through
the existing `ModuleClassLoader` factory registry. Their model/cell selection
algorithms and XML/property formats remain. The browser bootstrap calls
`BrowserSelectionFactories.registerCoreSelections(loader)` once for its core
module loader. Generated selection/filter classes register typed factories with
explicit binary names. Module-local classes restore through their module; names
without module IDs must also be visible in the system loader's class registry.
The helper performs that extra registration when `applicationVisible` is true.

Selection serialization uses `ModuleClassLoader.getRegisteredClassName(Class)`
to recover the explicit deployed binary alias. It does not depend on stripped
`Class.getName()` metadata. Name ownership comes from the existing constructor
factory registry and is released when its module loader is disposed.

The shared AWT/Swing keystroke implementations accept ordinary Java shortcut
syntax such as `F2` and `shift F2`, including the optional `pressed` keyword.
AWT rejects invalid specifications; Swing returns null. The actual replacement
parsers match the Java 21 oracle in 750 grammar/modifier observations.

The relevant constructor signatures are `(EditorComponent, Map, CellInfo)` for
selections and `(EditorContext)` for range filters. Missing factories fail rather
than returning synthetic selections. Stale selections signaled by
`SelectionRestoreException` remain an expected unsuccessful restoration.

## Highlighting

The `Highlighter` and `HighlighterUpdateSession` overlays preserve checker
selection, ordering, real typechecking sessions, message updates and quick-fix
decisions. They remove the JVM make-service dependency. A host that performs a
conflicting operation may install `Highlighter.setBusyCondition(...)`.

Browser tasks use `ScheduledExecutorService.schedule(..., 0, MILLISECONDS)`;
the sibling executor's `execute`/`submit` path can run inline and is not a yield.
The update session yields between editors, and closes every model read action
and typechecking context before yielding. Individual checker algorithms remain
synchronous. Cancellation or a temporarily inaccessible model marks the editor
unchecked and returns to the browser; there is no sleep or retry loop.

Stopping cancels the timer and queued tasks without `awaitTermination`.
Disposal is idempotent, stops checker wrappers, clears checked state and removes
listeners. Nested write/command pause scopes are counted so an inner completion
cannot prematurely unpause an outer operation.

`IntentionsSupport` keeps the original one-second availability delay through an
owned Swing-thread `Alarm`. It cancels pending lookup on focus loss, selection
replacement and disposal, and checks selected-node/root identity before and after
the real intention lookup. No thread sleep or model read lock spans the delay.

`HintPopupController` cancels both its move and hover alarms on typing, clicking
and listener removal. Editing therefore cannot leave a queued callback that later
shows an obsolete diagnostic. The strict live runner checks that the resolved
"Expected an integer literal" message is absent from visible tooltip/pop-up content.

The browser host installs MPS's actual `FindUsagesManager` before constructing
editors and disposes it after editors and project services have stopped. Eight
upstream implementation classes supply reference, concept-instance and model-usage
searches, including real model traversal and registered persistence participants.
`HighlightUsagesSupport` therefore calls the real facade when its selection timer
fires. Desktop `FindersManager`, finder-plugin discovery and the desktop search UI
remain excluded. The live fixture verifies results after reference retargeting,
root removal and import changes, with explicit model-scope boundaries.

## Undo

`WorkbenchUndoHandler` collects nested commands into one outer action. The
replacement collector retains every actual MPS `UndoItem`, including model
renames and root/child/property operations; it omits only desktop VirtualFile and
text Document cache bookkeeping. Upstream `SNodeIdeaUndoableAction` remains:
undo visits actions in reverse order, redo in forward order, inside the model
access undo-transparent command.

The functional IntelliJ `UndoManager` owns project history, command grouping and
suppression of recording during playback. The browser currently uses one model
history per project. `OnReloadingUndoCleaner` synchronously invalidates that
whole history on model replacement/removal; this is deliberately conservative
because there are no per-text-document stacks. The project calls
`WorkbenchUndoHandler.dispose()` before closing its repository.

`BrowserEditorHost` installs `WHEN_FOCUSED` Swing bindings for Ctrl/Meta+Z undo
and Ctrl/Meta+Y or Ctrl/Meta+Shift+Z redo. The actions invoke the project's real
`UndoManager`; they do not write text or emulate history in the input proxy.
They require the live editor to be the AWT focus owner and consume the recognized
shortcut even when model history is empty, preventing the native textarea from
replaying a separate text-history operation over the MPS model.

The host adds child `InputMap`/`ActionMap` instances whose parents are the existing
maps, so other editor bindings remain available. Disposal restores the prior maps
only if the installed maps still belong to the host; later map replacements are
preserved. The live runner clicks the actual painted property cell, types through
the browser, checks each undo/redo shortcut against the real model value, and
checks that moving focus outside the editor prevents history activation.

## Swing utilities

The shared libraries' `AWTKeyStroke` and Swing `KeyStroke` implementations accept the
standard optional `pressed` token, including the real editor's `F2` and
`shift F2` bindings. Invalid AWT specifications throw IllegalArgumentException;
the Swing string factory returns null. `tools/run_keystroke_oracle.py --check`
compares the actual replacement parsers with Java 21 across 750 observations,
including modifiers, typed/released keys, whitespace and malformed input.
`//:keystroke_browser{,_dev}` passes the same observations in Chromium, Firefox
and WebKit in both development and optimized builds (six runs).

The shared `javax-swing-j2cl` library provides scheduling, component ancestry,
JLayer unwrapping, coordinate conversion, hit testing and mouse/wheel conversion.
MPS consumes the implementation directly; there is no Swing source override.

Passive hints preserve editor focus. Swing's `requestFocusEnabled` flag controls
automatic focus requests independently of whether a component is focusable;
`JPopupMenu` honors it on show and restores the invoker only when focus was inside
the closing popup. IntelliJ popup/hint adapters likewise keep `requestFocus`
separate from `focusable`, so showing or dismissing a passive hint does not steal
keyboard input from the editor.

`tools/run_swing_utilities_oracle.py --check --windows` compares the actual
implementation, rebased only into a test package, with Java 21 Swing on real
component trees. All 224 geometry/input observations and undisplayed window
ancestry/cross-window tests match. Windows are tested under Xvfb. Applet and
MenuDragMouseEvent support are not added to this browser profile.

Both development and optimized Swing utility fixtures pass Chromium, Firefox,
and WebKit. The shared Swing library adds `Scrollable`, window/root
panes, visual focus traversal, caret and hyperlink notifications, keyboard action
registration, menu separators and accelerators, and scrollbar UI contracts.
`JViewport` honors the view's tracking flags and exposes actual clipped view
coordinates. `JScrollPane` synchronizes scrollbar models and row/column headers;
its viewport retains native browser wheel scrolling. Java view coordinates and
DOM scrolling account for the displacement exactly once.

`BasicScrollBarUI` computes track/thumb geometry from the range model, supports
thumb dragging, block and unit movement, and removes its listeners on replacement.
The scrollbar hosts a canvas painting layer so MPS's gutter drawing hooks can run.
The existing `JEditorPane` HTML profile remains normalized plain text; hyperlink
events may be dispatched by a host, but this change does not add an HTML renderer
or synthesize clickable anchors from the normalized text.

`tools/run_swing_editor_oracle.py --check` records 28 JVM component observations
and checks visible-window root/content routing plus visual focus order under Xvfb.
It runs Swing on the JVM event dispatch thread. The corresponding browser fixture
tests the actual browser replacements, including model-driven scrollbar dragging.

The main editor, left gutter, and completion pattern panel keep their original
paint methods. A shared `BrowserPaintSurface` places a canvas behind native
embedded controls without adding an AWT child; rebuilding the cell component tree
therefore preserves its painting surface. Disposal detaches that canvas.
Custom completion list renderers are painted into independent row snapshots,
including icons and colored fragments. Accessible names remain DOM text; normal
JLabel renderers retain their native text presentation. The separate native
fixture checks pixels, scrolling, embedded input, renderer reuse and disposal.

`tools/run_swing_controls_oracle.py --check` records 18 additional Java 21
observations for button change/action events, pressed/released actions, text
alignment and RTL insets, compound/matte border pixels, layered-pane ordering,
and completion list sizing/colors. These exact observations pass Chromium,
Firefox and WebKit in both development and optimized builds. The native paint
fixture also passes all six runs with browser peers installed: it checks canvas
pixels, accessible completion text and colored icons, real list clicks, wheel
and keyboard scrolling, embedded text input, rebuilding/remounting and disposal.
This fixture exercises the shared Swing rendering path with representative
components; acceptance of the real MPS EditorComponent is tracked separately.
It also verifies repaint propagation from the external scroll pane through its
native viewport to the nested editor canvas. Only intersecting damage is passed
down, and traversal stops at the first painting surface. In all six browser runs,
parent-only repaint paints that surface once and preserves pixels outside a
subsequent clipped repaint.
The shared RepaintManager also discards queued invalid components that have
lost their peers, validates each live root once per batch, and completes valid
work before rethrowing an unrelated layout/paint failure. Its scheduling state
is restored even after a failure. Native regressions pass in all six browser
runs for a detached editor queued before disposal and for later repaint recovery
after an explicitly observed layout exception.

The real editor lifecycle follows the same rule: queued work must not re-enter an
editor's layout after its Swing peers, model context or painting surface are gone.
The live fixture creates and disposes two hosts in sequence, leaving queued browser
ticks free to run between the native-input and final-disposal checks.

## Input methods

`EditorComponent` mounts `BrowserInputMethodSupport` alongside its painting
surface and installs its existing `InputMethodListenerImpl` through
`getInputMethodRequests()`. The helper creates a native textarea outside the
AWT child list, so cell rebuilds retain the input target and embedded controls
retain their own focus. The textarea is removed with its listeners and pending
timers when the editor unmounts or is disposed.

Focusing the editor redirects native browser focus to this textarea while the
AWT focus owner remains the real editor. The proxy has no extra Tab stop. Native
keyboard events are forwarded through the existing WebAWT DOM key bridge,
including release events and modifiers; handled keys suppress native insertion.
Its position follows the selected cell's caret from MPS InputMethodRequests,
so the browser can place its candidate window beside the edited text.

`BrowserEditorHost.createEditor` explicitly sets the editor focusable. The mounted
host must request focus when activating an editor; creating a generic Swing
container alone does not create a usable browser keyboard target. The live fixture
checks both `requestFocusInWindow()` and the real AWT focus owner, then waits for
the native textarea to own DOM focus before composing text.

Composition updates and commits reach the actual MPS input-method listener with
UTF-16 committed counts. An empty composition start, or cancellation before any
preedit, sends no text change. Cancellation after preedit replaces the listener's
tracked uncommitted suffix. The textarea's transient text is cleared after commit
to avoid inserting a second copy through the following native input event.

The live runner drives Chromium's native browser composition pipeline with CDP
`Input.imeSetComposition` and `Input.insertText`. It verifies preedit, a single
commit and subsequent cancellation against the actual model value. Firefox and
WebKit use synthetic DOM `CompositionEvent` sequences to verify the same bridge
contract; those events do not establish native IME coverage in those engines.
Neither route tests a physical operating-system IME, candidate selection or a
language-specific keyboard installation.

Use the [current live browser report](../out/editor-live-browser-results.json)
for engine/build status, bundle hashes, native mouse/history checks, automatic
diagnostic pixels, composition coverage and lifecycle results. The
[full validation report](../out/validation-summary.json) records build, generator,
oracle and browser steps separately. Results from an earlier bundle do not establish
acceptance of newly added bindings or regression checks; verify the current reports.

## Validation targets

- `//:editor_utilities_browser{,_dev}`: 520 exact tokenizer JVM observation rows.
- `//:swing_utilities_browser{,_dev}`: actual sibling component trees, hit testing,
  keyboard-button masks, mouse/wheel conversions and window ancestry.
- `//:swing_editor_browser{,_dev}`: editor-facing scrolling, listeners, menus,
  keyboard actions, window structure and focus traversal.
- `//:swing_editor_controls_browser{,_dev}`: button events, alignment, border
  pixels, layered-pane order and completion list sizing/colors.
- `//:swing_editor_native_browser{,_dev}`: real browser canvas, completion
  renderer snapshots, native input/scrolling and lifecycle.
- `//:editor_runtime_contracts_browser{,_dev}`: actual MPS keymap matching and
  selection XML/constructor-factory contracts. This focused fixture does not
  substitute for the full model/cell selection restoration scenario.
- `//:editor_live_browser{,_dev}` and `//:editor_live_stripped`: real MPS
  editor/model/typechecking, structural edits, usage search, automatic diagnostic
  pixels, native mouse/keyboard input, focused undo/redo shortcuts, composition and
  repeated host disposal.

The corresponding `tools/test_browser.mjs` modes are `editor_utilities`,
`swing_utilities`, `swing_editor`, `swing_editor_controls`, `swing_editor_native`
and `editor_runtime_contracts`. Browser reports record execution
separately from JVM oracle success; an oracle result alone is not an editor
runtime acceptance result.

The live targets use `tools/test_editor_live.mjs`; its per-run `imeCoverage` field
distinguishes Chromium's native composition pipeline from the other engines'
DOM composition contracts. Unexpected page errors or Java/browser console errors
fail the live run even when its Java assertions have passed.
