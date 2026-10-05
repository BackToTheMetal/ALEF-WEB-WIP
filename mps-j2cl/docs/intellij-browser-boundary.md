# IntelliJ compatibility boundary

The compatibility library covers the IntelliJ declarations used by the retained
MPS Java sources. It implements the stateful contracts needed by editors and the
typesystem host without linking IntelliJ implementation jars. This does not amount
to a complete IntelliJ platform or a finished IDE shell.

`tools/inventory_intellij.py` reads the exact selections in
`compatibility/browser-projects.json` and applies `mps-overrides.bzl` before scanning.
It reads Java sources only. The generated `compatibility/intellij-usage.json`
includes ordinary/static imports, executable fully qualified references, imported
type qualifiers, and simple MPS subclasses of IntelliJ types. It keeps class-name
strings separate. `--check` verifies the inventory without rewriting it.

The current audit covers 2,052 retained sources, including 99 replacements, and
275 compatibility source files. It records 564 imports, 16 fully qualified code
references, 490 imported-type qualifiers, and 82 subclass-qualified references.
Every reference has an owning compatibility source. A resolved owning source
does not prove that every nested type, member, overload, or access modifier exists;
javac and J2CL remain the authoritative checks. Subclass references are a
conservative superset and can refer to fields defined by MPS itself.

The remaining host requirements and limitations are concrete:

| Surface | Current behavior | Host work or limit |
| --- | --- | --- |
| External links | `BrowserUtil` calls an installed opener. | The default throws until a host opener is supplied. |
| Notifications and stack traces | Notification and console callbacks default to `System.err`. | The host supplies visible notification/console presentation. Status notifications also update the logical status label. |
| Project/file persistence | The browser project is in memory; VFS protocols are registered explicitly. | File byte/content writes, rename/move/delete/copy, and save operations require an actual backend; the base methods fail explicitly. No local storage is added. |
| Clipboard | WebAWT application clipboard; 50-entry newest-first history preserving complete `Transferable` identity and MPS node flavors. | OS clipboard read/write and asynchronous import/export belong to the host. Equal text never substitutes a different node payload. |
| Blocking dialogs and modal progress | Synchronous confirmation/native modal methods throw. | Import and confirmation workflows need browser callbacks; no synthetic affirmative answer is returned. |
| Services and extensions | Host-injected service instances and explicitly registered extensions/publishers. | No plugin descriptor discovery, reflective service construction, or dynamic Java proxy publishers. |
| Scheduling | Cancellable cooperative tasks queue onto the real Swing event thread. | There are no parallel JVM worker threads, blocking waits for indexing, or native modal event queues. |
| Undo/redo | Actual model actions are grouped, reversed/replayed, and invalidated on failure or model replacement. | No IntelliJ document snapshot/VFS undo integration. |
| Keymaps | Escape, Up, Down, and Ctrl+Q defaults plus mutable host keymaps; one-stroke action bindings execute actions. | MPS-specific action registration and platform-specific bindings remain host work; two-stroke binding registration currently throws. |
| Action UI | Action update/perform callbacks and popup selection preserve context and place. | IntelliJ popup speed-search, mnemonic/numbering modes, and full secondary-toolbar layout behavior are not reproduced. |
| Popups | Real WebSwing popups, measured content, selection callbacks, and disposal on close. Passive documentation and hints honor `requestFocus(false)` while remaining explicitly focusable; ordinary menus retain automatic focus. Closing restores invoker focus only when the popup held focus. | Desktop resize/move/shadow/global-stack/dimension-service options are compatibility hints. Native window modality and persistent non-dismissable help balloons are unavailable. |
| Documentation | Readable text using the selected WebSwing HTML profile and real font metrics. | Full Swing HTML/CSS/document-view behavior is excluded by MPS overlays. |
| Tool windows/status | Stateful, host-mountable Swing documentation and status components. | The host places these components in its UI. `WindowManager.getFrame` returns an attached frame or null; no OS frame is fabricated. |
| Icons | MPS assets are registered by the root browser bundle; IntelliJ symbolic names have visible placeholders. | IntelliJ artwork requires an explicit host asset registration. |
| Metadata/settings | Narrow declaration annotations and mutable in-memory settings. | Annotations do not activate persistence/telemetry. Reflective XML bean copying throws; selected MPS settings copy fields explicitly. |

Popup `setFocusable(false)` governs the popup component and automatic focus on
opening; descendant controls retain their own focus policies. It does not enforce
desktop popup-window focus restrictions across all descendants. The retained
completion chooser, `NodeSubstituteChooserUi`, explicitly disables focus on its
list and both scrollbars.

`compatibility/intellij-usage.json` records each explicit unsupported-operation
construction with source and line. Some entries are API guards such as executing
a separator; they are not additional missing host features.

`tools/run_intellij_contracts.py` passes 147 synchronous JVM assertions and
callback-based scheduler contracts. The runner accepts `--java`/`--javac`, uses
temporary output under `.work`, and never links installed IntelliJ jars or updates
golden results.

The `intellij_contracts` browser mode runs the same contracts in development and
optimized bundles in Chromium, Firefox, and WebKit, plus browser-only popup focus
checks. Those checks cover passive documentation, lightweight hints and IDE
tooltips; explicit focus into a passive popup; active popup focus; focus on close;
and ordinary menu/list-popup defaults. Focus-loss listeners also detect transient
focus theft. All six runs pass against the rebuilt bundles, including these
additional focus checks. `out/intellij_contracts-browser-results.json`
records each run's results and bundle hashes. Real MPS editor interaction is
validated separately by the live editor fixture; these compatibility contracts do
not establish full IDE parity.
