# AWT/Swing dependencies

MPS consumes `../java-awt-j2cl` and `../javax-swing-j2cl` directly through
MODULE.bazel. The 0.2.0 libraries own the former one AWT replacement and 44 Swing
replacement/addition files. No Java source is replaced in a dependency view.

The stable labels remain `@webawt//:webawt` and
`@javax-swing-j2cl//src/main:javax-swing-j2cl`. Install the browser peers with
`WebAwt.install(rootElementId)` before mounting components.

## JRE prerequisites and single ownership

The composed MPS JRE supplies URI, URL, URISyntaxException and
MalformedURLException. `.bazelrc` selects AWT's supported
`--@webawt//src/main/java/java/net:external_jre=true` profile. Normal standalone
AWT/Swing consumers leave this false and receive the library's four sources.

AWT owns the J2CL type-support generator and callback exception patch. MPS keeps
root-local copies because Bazel override patch labels must belong to the root.
The composed IO patch already supplies ReflectiveOperationException, so MPS does
not apply the standalone reflection-base patch. Each class is emitted once.

`dependency-views.json` is now schema version 2: reviewed direct library module
hashes and prerequisite patch copies. `tools/prepare_dependency_views.py` retains
its historical CLI for bootstrap compatibility but creates no source views. It
verifies the modules and four net source hashes and copies only shared patches.
Its `--check` mode performs no writes.

## Validation ownership

Reusable keystroke, component, scrolling, controls, native painting and input
method fixtures live in javax-swing-j2cl/src/test/runtime. Its own validation
runs JVM oracles plus Chromium, Firefox and WebKit development/optimized bundles.
MPS consumes the same fixture sources and retains real EditorComponent/model
integration tests. The AWT library owns its parser regression and compiler type
support matrix, including optimized STRIPPED output.

See [the migration report](ui-library-migration.md) for the source inventory,
archive identities and completed verification.
