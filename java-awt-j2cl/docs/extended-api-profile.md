# Stock-J2CL extension profile

This is a reviewed extension of the locked public AWT profile, not a claim of Java SE implementation completeness. Exact signatures and per-member decisions live in `compatibility/extended-api-dispositions.json`. Public baseline dispositions are unchanged. The generated consumer probes check calls, return types, fields, constructors and overridable declarations; family regression tests establish the stated behavior.

## Retained contracts

- Component/event extensions: protected event routing, coalescing, property changes, focus requests, listener fields and mutable event fields. Browser scheduling is asynchronous; blocking and native event loops remain unavailable.
- Accessibility: the Component/Container protected context hierarchy, component geometry delegation, container children, button action/value contracts, list selection and child contexts, and menu metadata interfaces. JDK menu geometry and list-child geometry already return null/false or perform no action; those contracts are preserved. Browser DOM accessibility still requires the later manual assistive-technology acceptance step.
- GridBag extension methods dispatch through both modern and legacy hook names and expose layout information/default constraints. `comptable` is unavailable because the pinned J2CL owns java.util but omits Hashtable; its public Map-independent layout API remains supported.
- Geometry, raster, image, paint, value objects, enums, listener interfaces and beans retain the behavior covered by the existing differential tests. Numerical flattening, browser metrics, RGB color handling and the previously documented image-format limits still apply. Compilation coverage is distinct from behavioral parity.
- Drag/drop hooks expose context creation, transferable proxies, acceptance/rejection, flavor inspection and mutable fields for browser-local transfers. Serialization cloning of transferable objects and native cursor control are not provided.
- URI/URL are the narrow signature prerequisites authorized by ADR 0006: validated literal values for browser navigation, mail and asynchronous image loading. They are not general URI resolution/normalization/parser or synchronous URLConnection libraries. Their absent constructors, decomposition/resolution and transport methods are individually unavailable in this release.
- DataFlavor supports the existing browser string/local MIME profile, equality, cloning and MIME queries. Class-loader resolution, stream/reader flavors, general best-flavor selection, Java serialization and Externalizable subtyping are unavailable. Externalizable is absent from a J2CL-owned package; the audit records that single explicit parent exception and verifies it against the live package inventory.

## Explicit unavailable extension surfaces

- Component.BltBufferStrategy and FlipBufferStrategy expose native AWT implementation internals. Canvas/Window getBufferStrategy and browser buffer copying remain retained through WebAWT's existing strategy.
- Java AccessibleText/AccessibleEditableText and the TextComponent/TextArea/TextField specialized nested text contexts require the excluded Swing AttributeSet prerequisite. Text controls still return supported AccessibleContext metadata and browser-native accessibility. Full Java accessible text geometry and attributes are not claimed.
- DropTargetAutoScroller and its timer-driven native extension hooks are unavailable. Native browser scrolling is not represented as a Java Swing timer implementation.
- Object-stream readResolve/writeExternal/readExternal hooks are unavailable under the existing no-Java-serialization profile.
- Toolkit.getNativeContainer and DragSourceContext.updateCurrentCursor depend on native host-window/drag-cursor control.
- TextLayout.handleJustify is unavailable; the retained immutable getJustifiedLayout currently scales runs to the requested width rather than implementing a full word-spacing/complex-script justification engine.

The explicit unavailable parent exception cannot suppress arbitrary declaration failures: the parent must be a real reference parent and absent from an actual J2CL-owned package. Enum final flags are compared according to Java's enum rules: compiler-generated constant subclasses do not make an enum extensible to consumer source. All other final/abstract/visibility and throws incompatibilities remain gated.
