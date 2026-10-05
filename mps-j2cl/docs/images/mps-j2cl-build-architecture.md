> Historical diagram note: AWT/Swing dependency views were replaced by direct
> 0.2.0 library dependencies; see [UI dependencies](../ui-dependencies.md) for the current boundary.

# mps-j2cl build architecture image

Created from the workspace configuration on 2026-09-09 using the built-in image generation tool.

## Repository evidence

- [README](../../README.md): current browser profile and runtime scope.
- [Source lock](../../source-lock.json): pinned MPS, Bazel, Java toolchain context and J2CL baseline (Java version also in .bazelrc).
- [Bazel modules](../../MODULE.bazel): sibling dependencies, dependency views and ordered J2CL patches.
- [Project rules](../../mps-rules.bzl): source selection, public targets and shared compilation groups.
- [Browser project inventory](../../mps-browser-projects.bzl): selected sources and compilation groups.
- [Root targets](../../BUILD.bazel) and [live editor targets](../../editor-live-tests.bzl): browser host and application entry points.
- [Dependency views](../../dependency-views.json): root-owned WebAWT and WebSwing views.
- [JRE composition](../../tools/compose_jre.py): canonical sibling sources and JRE ownership.
- [Bootstrap](../../tools/bootstrap.py), [Bazel wrapper](../../tools/bazel.sh) and [validation](../../tools/validate.py): setup, serialized WSL builds and verification pipeline.

The image describes the current build architecture. No build or validation suite was run to produce this documentation image.

## Generation prompt

Use case: infographic-diagram
Asset type: a high-resolution architecture image for the mps-j2cl repository documentation.
Primary request: Create a technically accurate, beautifully typeset architecture diagram of the CURRENT mps-j2cl build, based exclusively on the facts below.
Style: precise professional software architecture infographic; warm white background, dark navy text, blue source cards, violet build cards, teal browser cards, restrained amber validation strip. Flat vector-like raster illustration, small purposeful folder/code/browser icons, sharp thin orthogonal connectors with unambiguous arrowheads, excellent generous spacing. No 3D, no mascots, no decorative circuit boards, no fake logos, no watermark. Wide landscape, approximately 3:2, ideally 3072 x 2048. All text crisp and readable. Make the main flow understandable at a glance.
Title at top left, exact: "mps-j2cl build architecture"
Subtitle exact: "From pinned MPS Java sources to a browser editor runtime"
Small toolchain line: "Bazel 8.3.1  |  Java 21  |  J2CL 20260402"

LAYOUT: Four major vertical columns with short headings "01 INPUTS", "02 PREPARATION", "03 BAZEL + J2CL", "04 BROWSER". Under the columns, a full-width validation strip. Prefer concise card text; do not print these instructions. Route arrows only through gutters and never through cards or labels. Use solid arrows for source/artifact flow and dashed connectors for configuration. Keep the lower preparation support cards distinct from the MPS selection path.

COLUMN 1 INPUTS has three cards:
A. "Pinned MPS checkout" with "mps/" and "source-lock.json", "Java sources + .iml descriptors", small commit label "d2589ed55b".
B. "Browser adaptations" with "mps-web/  •  99 explicit replacements" and "browser/  •  host + assets + adapters". Keep these as two distinct lines.
C. "Sibling libraries" with compact grouped names: "IO • NIO • concurrency", "WebAWT • WebSwing", "Trove • JDOM".
These sibling names refer to existing local Java source repositories.

COLUMN 2 PREPARATION has three cards:
D, top, "Select + overlay MPS sources" with "107 catalogued projects → 36 selected", "2,052 Java sources", "browser-profile.json + mps-overrides.bzl", "mps-browser-projects.bzl". A feeds D; only the mps-web replacements line of B feeds D. This is source selection and replacement, not a model code generator.
E, middle, "Browser dependency graph" with "WebAWT / WebSwing dependency views", "Trove + JDOM + annotations", "IntelliJ adapters + Elemental2". C feeds E. Tiny path ".work/dependency-views/".
F, bottom, "Compose JRE + compiler patches" with "IO / NIO / concurrency / java.net", then four short ordered lines: "1  JRE composition", "2  Class type support", "3  Registered ClassLoader", "4  Additional MPS APIs". Below these: "JRE additions compiled once in java.base". C feeds F. These patches are applied in order to pinned J2CL through MODULE.bazel, and the patched compiler is built from source.

COLUMN 3 BAZEL + J2CL:
A slender configuration ribbon above the central card: "BUILD.bazel + MODULE.bazel + mps-rules.bzl".
Central large card heading "Project target graph". Exact body:
"//:mps_<project>"
"OpenAPI • Kernel • editor • typechecking"
"Two shared compilation groups:"
"Kernel / Project / Constraints"
"Actions / Editor / Intentions"
"Public project aliases for cycle members"
D and E feed this card. The browser/ host line from B also feeds this card using a clearly labeled separate connector "host + assets".
Immediately below it, connected downwards, a card "J2CL transpilation" with "j2cl_library → JavaScript libraries". F feeds this transpilation card with a short label "patched JRE + compiler".
Below that, another connected card "Closure compilation" with "j2cl_application + JS entry points".
The config ribbon uses dashed connectors, not data arrows.
Keep the two compilation cycles in one logical box; no circular arrows are needed.

COLUMN 4 BROWSER:
Top card "JavaScript bundles" with "Development", "ADVANCED optimized", "STRIPPED metadata variants". Closure compilation feeds this card.
Below, connected downward, a larger browser-window-shaped card "Browser editor runtime". Inside are simple structured labels:
"MPS models + editor cells"
"Commands • undo/redo • completion"
"Incremental typechecking + diagnostics"
"WebSwing / WebAWT browser UI"
A small pill below the window: "Handwritten expression demo language"
A clear small scope line: "Ahead-of-time, in-memory runtime".
Do not imply a full desktop IDE, dynamic JVM bytecode execution, or generated-language deployment.

BOTTOM STRIP:
Heading "VALIDATION  •  tools/validate.py"
Four neatly separated linked stages:
"Source + provenance audits" → "Java 21 oracle checks" → "Browser behavior suites" → "Reports + bundle hashes"
Under browser suites: "Chromium • Firefox • WebKit"
Under reports: "out/validation-summary.json"
Small footer label: "WSL build orchestration • shared J2CL lock • one job / worker".
This strip describes validation architecture; do not add PASS badges, success counts, or claim that a validation run was performed for this image.

ACCURACY: Do not invent extra systems such as Docker, Maven build stages, servers, cloud deployment, databases, JVM bytecode loading or Kotlin compilation. It is an ahead-of-time Java-to-JavaScript build. Keep all file names and tool versions exact. Avoid repeating the title or long prose.

## Arrow correction prompt

Use case: infographic-diagram. Edit this existing mps-j2cl architecture diagram. Preserve ALL text, typography, colors, cards, layout, dimensions and styling exactly. Change ONLY the blue connector arrows specified below; the facts and words are already correct.
1. The arrow labeled "patched JRE + compiler" currently incorrectly points from "Compose JRE + compiler patches" into "Closure compilation". Remove that connector and reroute it from the right edge of "Compose JRE + compiler patches" upward within the gutter between columns 2 and 3, ending with an arrowhead on the LEFT EDGE of the "J2CL transpilation" card at its vertical center. Put the same "patched JRE + compiler" label by that new route. It must NOT enter Closure compilation.
2. The horizontal arrow from the "Browser adaptations" input card into "Browser dependency graph" is incorrect. Remove that connector. Instead create a connector from the RIGHT EDGE of the "Sibling libraries" input card (near its top) that runs right, then UP within the gutter between columns 1 and 2, then right into the LEFT EDGE of "Browser dependency graph". Label it "libraries". It must be clear that the starting card is Sibling libraries and the destination is Browser dependency graph.
3. There are currently two arrows from "Sibling libraries" into "Compose JRE + compiler patches". Retain just ONE direct arrow between these two cards. The second sibling arrow should be the one rerouted into Browser dependency graph in item 2. No duplicate arrow to JRE composition.
Preserve the existing correct mps-web replacements arrow into "Select + overlay MPS sources", the correct host + assets connector into "Project target graph", all source/bundle flow, and the entire validation strip. Lines must avoid card interiors and labels. Do not change any other content.
