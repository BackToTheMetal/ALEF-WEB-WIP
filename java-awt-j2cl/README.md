# java-awt-j2cl

java-awt-j2cl implements a defined `java.awt` browser profile for J2CL applications, using Elemental2 for DOM controls and Canvas rendering. The module version is **0.2.0 (unreleased)**. File dialogs, printing, native desktop integration and blocking browser event loops are outside the profile.

The [dual-runtime showcase](samples/dual_showcase/README.md) runs the same application against the JDK and java-awt-j2cl. Its 37 graphics scenarios compare Direct Canvas and BufferedImage. Choose **Rendering lab > Graphics state**, then select a scenario at the bottom of the page.

## 0.2.0 migration

This candidate includes the shared editor support migrated from MPS. Run
`npm ci --ignore-scripts` before the expanded browser runtime validation.
The consumer root must apply the supplied J2CL prerequisite patches; see [runtime support](docs/swing-runtime-support.md).

## Build and run

Use Linux or WSL2 with Bazelisk. `.bazelversion` pins Bazel 8.3.1; Bazel obtains its JDK 21 build toolchain. OpenJDK/Temurin 17.0.16+8 is the compatibility reference. The pinned dependencies are J2CL 20260402, Elemental2 1.3.2 with the included build patch, and rules_closure 0.16.0. Browser validation uses the Chromium, Firefox and WebKit revisions installed by Playwright 1.62.0. Other versions are not yet validated.

```sh
bazelisk build //samples/dual_showcase:browser_application
python3 -m http.server 8000 --bind 127.0.0.1
```

Open [the local showcase](http://localhost:8000/samples/dual_showcase/). Desktop comparison requires a graphical session:

```sh
bazelisk run //samples/dual_showcase:desktop
```

## Consume from another Bazel project

The stable dependency label is **`@webawt//:webawt`**. Internal package targets are implementation details. java-awt-j2cl is supplied as a source module; an ordinary JVM jar is not a replacement for the JDK's AWT.

Until a public release exists, obtain the locally prepared source candidate described in [release engineering](docs/releasing.md), verify `SHA256SUMS`, and extract it to `vendor/webawt`. Copy `vendor/webawt/third_party/patches` to your own `third_party/patches` directory, retaining its BUILD.bazel and patch. In your root `MODULE.bazel`:

```starlark
module(name = "my_application")
bazel_dep(name = "webawt", version = "0.2.0")
local_path_override(module_name = "webawt", path = "vendor/webawt")
bazel_dep(name = "j2cl", version = "20260402")
bazel_dep(name = "elemental2", version = "1.3.2")
bazel_dep(name = "rules_closure", version = "0.16.0")
single_version_override(
    module_name = "elemental2",
    version = "1.3.2",
    patch_strip = 1,
    patches = ["//third_party/patches:elemental2-1.3.2-w3c-css.patch"],
)
```

Copy the candidate's `.bazelversion` and `.bazelrc`, then add `@webawt//:webawt` to your `j2cl_library` dependencies. Declare Elemental2/JsInterop dependencies directly when your application imports those types. For an immutable downloaded release, replace `local_path_override` with `archive_override` using the archive URL, integrity and strip-prefix recorded in `source-manifest.json`. No public download URL or registry publication is claimed yet.

The Elemental2 override must be repeated in the **consuming root**: dependency modules cannot impose their overrides on it, and Bazel requires patch labels to belong to the root repository. See [Bazel's root-module override rules](https://bazel.build/external/module#overrides). The packaging smoke test compiles the existing showcase from an archive in a separate root and tests it in all three browsers.

## Runtime lifecycle

Call `WebAwt.install("root-id")` after the root DOM element exists, then create/show your AWT component tree. `WebAwt.uninstall()` disposes the installed application's windows and listeners, cancels scheduled callbacks and image decoding, and releases graphics/image resources. It is idempotent. Only one root is installed at a time; installing another replaces the previous runtime. Release application-owned references when unmounting.

The browser showcase's [BrowserLauncher.java](samples/dual_showcase/BrowserLauncher.java) is the runnable mounting example. It exposes mount/unmount to JavaScript in [app.js](samples/dual_showcase/app.js). J2CL entry points must be explicitly exported; Java `main` is not the browser entry point.

`WebAwt.loadFont(family, url)`, `readClipboardText()` and `writeClipboardText(text)` return asynchronous promises. Handle rejection and avoid repainting disposed components after completion. Loaded font faces remain document-scoped. Browser system clipboard access requires a secure context, applicable permission and usually a user gesture. Synchronous `Toolkit.getSystemClipboard()` is application-local. Images report readiness/failure through ImageObserver; do not block the browser event loop waiting for them.

## Compatibility and failure behavior

The public gate covers 299 retained top-level types and 4714 callable members. Protected/nested and prerequisite contracts have separate reviews and 6101 compilation probes. These are source-contract inventories, not a claim of complete Java SE behavioral parity.

- [Graphics2D coverage](compatibility/graphics2d-showcase-coverage.md): all 84 signatures reviewed, 83 supported on both backends; Canvas XOR is explicitly unavailable.
- [Text/rendering profile](docs/text-rendering-profile.md): text shaping, outlines, rendering hints, composite restrictions, image caching and resource limits.
- [Extended API profile](docs/extended-api-profile.md): protected hooks, accessibility and retained prerequisite subsets.

Unavailable operations fail explicitly where specified, commonly with UnsupportedOperationException; browser capability operations may report failure asynchronously. Custom Composite, arbitrary Canvas text Paint and several destructive Canvas text composite rules are unsupported. Custom device ICC transforms remain an approximation. Inspect the specific method's reviewed disposition before relying on native behavior.

## Validation and release status

[FINALIZATION.md](FINALIZATION.md) records completed implementation and release work. The owner accepted the current showcase on 2026-09-08; additional manual acceptance and SonarQube/source-attributed percentage coverage are deferred. Existing behavior, three-browser, API, bundle and packaging gates remain mandatory. See [coverage policy](docs/testing/coverage-policy.md) and [contributing](CONTRIBUTING.md).

Original project code uses [Apache License 2.0](LICENSE); the font and other materials retain their notices in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). The source-policy and prepublication review status are recorded in [release/provenance.json](release/provenance.json). A prepared candidate is not an already published release.
