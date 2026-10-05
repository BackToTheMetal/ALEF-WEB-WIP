# WebAWT API validation

Generated: 2026-10-01T14:54:27.123082+00:00

## Summary

| Metric | Count |
| --- | ---: |
| OpenJDK reference types | 325 |
| Emitted WebAWT types | 299 |
| OpenJDK declared members | 5014 |
| WebAWT declared members | 4683 |
| Missing supported types | 0 |
| False implemented member claims | 0 |
| False implemented type claims | 0 |
| Supported types with declaration issues | 0 |
| In-scope callable members | 4714 / 4714 |
| In-scope missing members | 0 |
| Partial types with API gaps | 0 |
| Unclassified or deferred contracts | 0 |

This is the locked public top-level AWT inventory. Inherited means WebAWT exposes a member through a parent. Compatible declaration here uses callability normalization; it does not prove subclass or checked-exception compatibility. The extended API worklist separately audits protected members, accessible nested types, prerequisite packages, and declaration differences. Public-inventory completeness is not release readiness.

## Coverage by implementation state

| State | Callable | Missing | Total |
| --- | ---: | ---: | ---: |
| implemented | 3588 | 4 | 3592 |
| not-started | 13 | 285 | 298 |
| partial | 1122 | 2 | 1124 |

## Declaration and extension warnings

- `java.awt.Button`: compatible redeclarations: 2
- `java.awt.Canvas`: compatible redeclarations: 2
- `java.awt.Checkbox`: compatible redeclarations: 2
- `java.awt.Choice`: compatible declarations: 4, compatible redeclarations: 2
- `java.awt.Container`: inherited implementations: 12
- `java.awt.Dialog`: inherited implementations: 7, compatible redeclarations: 1
- `java.awt.Dimension`: compatible redeclarations: 1
- `java.awt.Frame`: inherited implementations: 3, compatible redeclarations: 3
- `java.awt.Graphics`: compatible declarations: 6
- `java.awt.Graphics2D`: compatible declarations: 1, inherited implementations: 2
- `java.awt.Label`: compatible redeclarations: 2
- `java.awt.List`: compatible declarations: 12, inherited implementations: 3
- `java.awt.MediaTracker`: compatible declarations: 4
- `java.awt.MenuItem`: compatible redeclarations: 1
- `java.awt.Panel`: inherited implementations: 1
- `java.awt.Point`: compatible redeclarations: 1
- `java.awt.PopupMenu`: compatible redeclarations: 1
- `java.awt.Rectangle`: compatible redeclarations: 1
- `java.awt.Scrollbar`: compatible redeclarations: 2
- `java.awt.TextArea`: inherited implementations: 4
- `java.awt.TextComponent`: inherited implementations: 3
- `java.awt.TextField`: inherited implementations: 4
- `java.awt.Window`: inherited implementations: 22
- `java.awt.desktop.QuitStrategy`: unexpected declarations: 1
- `java.awt.dnd.DragSourceAdapter`: unexpected declarations: 1
- `java.awt.dnd.DropTarget`: unexpected declarations: 1
- `java.awt.dnd.DropTargetAdapter`: unexpected declarations: 1
- `java.awt.dnd.MouseDragGestureRecognizer`: unexpected declarations: 1
- `java.awt.font.GlyphVector`: unexpected declarations: 1
- `java.awt.font.LayoutPath`: unexpected declarations: 1
- `java.awt.font.LineMetrics`: unexpected declarations: 1
- `java.awt.geom.Line2D`: compatible redeclarations: 1
- `java.awt.geom.Path2D`: compatible declarations: 5
- `java.awt.image.AreaAveragingScaleFilter`: compatible redeclarations: 1
- `java.awt.image.BandedSampleModel`: inherited implementations: 14
- `java.awt.image.BufferedImageFilter`: compatible redeclarations: 1
- `java.awt.image.CropImageFilter`: compatible redeclarations: 1
- `java.awt.image.FilteredImageSource`: compatible declarations: 5
- `java.awt.image.IndexColorModel`: compatible declarations: 1
- `java.awt.image.MemoryImageSource`: compatible declarations: 9
- `java.awt.image.RGBImageFilter`: compatible redeclarations: 1
- `java.awt.image.ReplicateScaleFilter`: compatible redeclarations: 1
