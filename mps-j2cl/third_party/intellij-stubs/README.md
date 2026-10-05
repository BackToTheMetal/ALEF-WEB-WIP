# Browser IntelliJ API compatibility

These are local implementations of the IntelliJ API surface used by the selected
MPS browser profile. They are not a complete IntelliJ implementation or its binaries.
MPS services are injected by the browser bootstrap so this library never depends
on MPS. Unsupported host operations fail explicitly.

The local MPS 2025.3 installation is used to check public signatures with `javap`;
the pinned source checkout's call sites determine the required API surface. No
code or binaries from that installation are copied or linked.

Implemented foundations include identity keys, user data, text ranges, a disposal
tree, synchronous message delivery, service registration, application read/write
contexts and queued browser actions. The supported surface grows from compiler
errors and behavior tests; this directory is not a complete IntelliJ platform.

`BrowserMessageBus.registerPublisher` binds a topic to a typed publisher factory.
For example, a `Changed` listener with `void changed(String value)` can be bound as
`bus.registerPublisher(TOPIC, dispatch -> value -> dispatch.accept(l -> l.changed(value)))`.
This keeps ordinary `syncPublisher(TOPIC)` callers working without Java dynamic
proxies. The host must register factories before requesting their publishers.

`BrowserApplication` maps pooled execution to the existing cooperative browser
scheduler. It does not provide parallel JVM threads, native modal queues, host
settings persistence or arbitrary process lifecycle control.

The service/disposal/message-bus, command/undo, data-context, action, clipboard,
timer, and presentation implementations retain state and perform the operations
used by MPS. Important contracts include:

- Disposable services remain readable during child cleanup. Disposal attempts all
  remaining children if one throws.
- Undo reverses action order, redo preserves it, nested commands form one frame,
  and adjacent non-null command groups merge. Playback failure or model replacement
  clears history rather than retaining an unsafe partial frame.
- Lazy action data evaluates on first lookup, caches per snapshot, inherits parent
  values, distinguishes an absent key from explicit null, and rejects cycles.
- Clipboard history retains Transferable identity and all custom node flavors.
  Text equality never substitutes a different node-transfer object. The default
  history limit is 50. Its clipboard is WebAWT's application clipboard; OS clipboard
  import/export remains a browser host responsibility.
- Timer expiry queues onto Swing's event thread. Cancellation remains effective
  after an action was posted. Periodic failure completes its Future exceptionally.
  Cooperative scheduling does not create parallel JVM threads in the browser.
- Completion rendering measures and paints fragments, icons, and decorations and
  exposes the combined text to accessibility. Popups dispose registered children
  when the underlying Swing popup closes.

The host mounts the documentation component returned by
`ToolWindowManager.getInstance(project).getToolWindow("Documentation").getComponent()`
and the logical status component returned by `WindowManager`. It supplies external
link, notification, console, file-system, action, and icon integrations explicitly.
IntelliJ symbolic icon names use visible placeholders until the host supplies an
asset. MPS icons are registered separately by the browser bootstrap.

Desktop persistence, process control, blocking dialogs, reflective bean copying,
two-stroke action shortcuts, and native window modality are unsupported. Popup
builder flags for desktop geometry/decoration are compatibility hints; they do not
provide a native IDE window manager. Documentation uses the selected WebSwing
plain-text HTML profile. None of these declarations implies a full IDE port.

`compatibility/intellij-api.json` inventories the source surface and its supported
contracts. `tests/intellij_stubs/IntelliJContractsTest.java` includes synchronous
contracts and callback-based scheduler contracts reusable by the browser harness.
JVM contracts complement, but do not replace, the root J2CL and browser tests.
The current 147 synchronous assertions and asynchronous scheduler contracts pass
on the JVM and in development/optimized bundles across Chromium, Firefox, and
WebKit. Run `tools/run_intellij_contracts.py` for the JVM and
`node tools/test_browser.mjs intellij_contracts` for the built browser bundles.
