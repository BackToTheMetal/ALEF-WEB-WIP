# Dual-runtime WebAWT showcase plan

## Goal

Create one capability gallery whose shared source imports only standard `java.awt`, `java.awt.*`, and J2CL-JRE APIs. Compile that source unchanged against the desktop JDK and against WebAWT with J2CL so visual and behavioral differences are directly observable.

## Architecture

- `DualShowcase.java` owns the complete shared UI and contains no WebAWT, Elemental2, JsInterop, Swing, filesystem, or printing references.
- `DesktopLauncher.java` starts the shared UI on the JDK AWT event queue and links only to the JDK.
- `BrowserLauncher.java` installs the Elemental2 WebAWT peers and starts the same shared UI.
- `app.js` is the Closure entry point and exports a small browser mount/unmount/test surface.
- `index.html` is a repository-served browser entry page.

## Capability pages

| Page | Functionality exercised |
| --- | --- |
| Controls and events | Button, Label, Checkbox, CheckboxGroup, Choice, List, TextField, TextArea, Scrollbar, action/item/text/focus/key/mouse/adjustment events |
| Layouts | BorderLayout, FlowLayout, GridLayout, GridBagLayout, CardLayout, preferred sizes, validation and orientation |
| Graphics2D | Canvas, colors, fonts, strokes, alpha composite, transforms, paths, rectangles, ellipses, arcs and gradients |
| Images and color | BufferedImage, raster access, RescaleOp, ConvolveOp, AffineTransformOp, ColorConvertOp and standard ICC color spaces |
| Text and focus | Editable text, selection, caret, font changes, component orientation and focus traversal |
| Menus and scrolling | MenuBar, Menu, MenuItem, CheckboxMenuItem, PopupMenu, ScrollPane and adjustable state |
| DnD and clipboard | DragSource, DropTarget, StringSelection, DataFlavor and Toolkit clipboard behavior |
| Windows and Desktop | Frame, Dialog, ownership/disposal, Toolkit metrics, cursors, beep, Desktop browse/mail support reporting |
| Runtime and limits | Runtime identity, API completeness figures, browser limitations and deterministic self-checks |

## Validation

1. Compile the shared source as an ordinary JVM target without any WebAWT production dependency.
2. Run a JDK desktop smoke test under a virtual display.
3. Compile and run the same shared source as a J2CL test with model peers.
4. Build the optimized browser application.
5. Use Playwright to navigate all pages and exercise controls, Graphics2D, image operations, DnD, menus, scrolling and dialogs in a real browser.
6. Add the tests to the existing validation suites and record a separate optimized bundle budget for this intentionally comprehensive application.
7. Run the complete WebAWT validation gate.

## Quality constraints

- Production code remains dependency-free beyond pinned J2CL and Elemental2.
- Test-only JUnit and Playwright remain isolated from production targets.
- Shared methods contain no extra blank lines and source is not mechanically word-wrapped.
- UI construction is split by capability page, mutable state is narrowly scoped, errors are reported in the gallery, and resources/windows are disposed explicitly.
