# java-io-j2cl

`java-io-j2cl` is a source overlay for J2CL `20260402`. It adds the missing
`java.io` declarations needed by the current MPS source tree without pretending
that a browser has a JVM filesystem, blocking pipes, or Java object
serialization.

The canonical sources live in `overlay/jre/java`. `tools/generate_patch.py`
turns them into `third_party/patches/j2cl-java-io.patch`, which Bazel applies to
J2CL's `java.base` implementation. This root-owned patch is necessary because
ordinary external libraries cannot safely introduce additional classes in
`java.*` packages.

## Behavior profile

- Byte-array, character-array, buffered, data, and charset-backed stream
  operations have deterministic in-memory behavior.
- `File` is a lexical path value. Filesystem queries report absence and
  mutations fail explicitly or return the JDK-shaped failure value.
- File streams/readers/writers fail on construction. Object serialization and
  blocking piped streams fail explicitly when invoked.
- There is deliberately no `localStorage`, IndexedDB, native filesystem, or
  other persistence implementation.

The machine-readable contract is in `compatibility/support-manifest.json`. The
checked-in MPS inventory records the exact checkout revision and every source
file that refers to a `java.io` name.

## Use from a Bazel root module

Copy `third_party/patches/j2cl-java-io.patch` into the consuming repository and
apply it from the root `MODULE.bazel`:

```starlark
bazel_dep(name = "j2cl", version = "20260402")

single_version_override(
    module_name = "j2cl",
    patch_strip = 1,
    patches = ["//third_party/patches:j2cl-java-io.patch"],
    version = "20260402",
)
```

The patch is pinned to that J2CL release and must not be silently applied to a
different version. A future combined IO/NIO profile should compose the two
canonical patches at the consumer root; it should not copy or fork either
project's source files.

## Validate and package

From PowerShell:

```powershell
.\tools\validate.ps1
wsl -d Ubuntu --exec sh -lc "cd /mnt/c/Codex/java-io-j2cl && python3 tools/package_release.py"
```

Validation runs in WSL with one Bazel job and automatically acquires the shared
`/tmp/j2cl-build.lock`. See `docs/WSL_COORDINATION.md` before running a cold
build beside another J2CL project.
