# Pure java.nio stub trial

This trial generates and transpiles **only `java.nio` and its subpackages**.
All 156 generated Java files are used, including the charset classes. Concrete
methods and explicit constructors throw:

```java
throw new UnsupportedOperationException("Stub!");
```

There are no WebAWT or IO project dependencies, no reused NIO implementations,
and no runtime overlay assembly. `CharArrayReader` is not an input source or an
output type. The trial is independent of its parent repository.

## Generate

Use Python 3.10+ and a full JDK. From this folder:

```powershell
python generate.py
```

The script uses `JAVA_HOME`, falling back to Java on PATH. To select a JDK:

```powershell
python generate.py --java-home "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
```

On this machine Python is not on PATH. The available bundled executable is:

```powershell
& "$env:USERPROFILE\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe" generate.py
```

The script reads NIO API signatures with `javap`, emits sources under
`generated/src`, and compiles them with the selected JDK into `generated/classes`.
The tested source API is **Temurin JDK 17.0.16+8**: 156 source files and 163
public/protected types. It also caches JDK class files in `.cache/jdk-reference.jar`
as compiler inputs. These reference classes are never transpiled into the library.

Each successful generation replaces the script-owned `generated` folder.
Do not put manually maintained sources there.

## Build with Bazel / J2CL

From this folder in WSL Ubuntu:

```bash
bash build.sh
```

Or from PowerShell:

```powershell
wsl -d Ubuntu --cd /mnt/c/Codex/java-nio-j2cl/python-trial --exec bash build.sh
```

The script runs `bazelisk build //:nio-stubs` with Bazel **8.3.1**, J2CL
**20260402**, and a Java 21 compiler toolchain. The NIO source API stays JDK 17.
The build uses the shared WSL lock and one worker.

The result is **`reports/nio-stubs.js.zip`** (also in `bazel-bin`). It contains
NIO JavaScript modules, source maps, and copies of the exact generated NIO sources.
`check_output.py` verifies that every source is unchanged, every compiled NIO type
has a JavaScript module, and no output file belongs to another package.

## Compile-time dependencies

NIO signatures refer to types such as `java.net.URI`, `java.io.File`, and
`java.security.BasicPermission`. They must be known to the compiler, even though
stub methods do not perform those operations.

The build uses two sets of **compile-time declarations**:

- Stock J2CL core headers, including the special core methods its transpiler needs.
- Missing JDK types from the selected JDK. `filter_api.py` removes types already
  supplied by J2CL, then Bazel's `ijar` strips method bodies to produce an API jar.

These jars are type-resolution inputs. They are not source inputs to the
transpiler, and their implementations are not emitted into the NIO artifact.
`transpile.py` stages the NIO sources to handle Bazel symlinks correctly and calls
J2CL. `compile_api.bzl` exposes the core header provider.

`j2cl-api-visibility.patch` changes visibility for two upstream **Bazel targets**
so the compiler and core headers can be used. It changes no Java source files.

## Scope and limitations

- This is a transpiled **library**, not a linked browser application. A consumer
  must provide a compatible J2CL runtime and resolve external dependencies and
  overlapping NIO types when linking. This pure library has no browser-runtime
  validation; the earlier mixed-runtime browser build has been removed.
- Abstract methods remain abstract. Java enums retain compiler-generated enum
  behavior. J2CL also emits constructor and type-metadata scaffolding; that is not
  an implementation of the NIO operations.
- Object fields, including `StandardCharsets.UTF_8`, are `null` placeholders.
- Annotations, documentation, parameter names, and sealed/permits metadata are
  not reproduced. Package-private constructor scaffolding permits stub inheritance.
- The generator is tested against JDK 17. Other JDK versions, particularly preview
  APIs, may require adjustments.
- J2CL reports one non-blocking `unusable-by-js` warning for
  `DirectoryIteratorException.getCause()`.

## What to commit

Commit the Python scripts, `build.sh`, `compile_api.bzl`, `BUILD.bazel`,
`MODULE.bazel`, `MODULE.bazel.lock`, `.bazelrc`, `.bazelversion`,
`j2cl-api-visibility.patch`, `.gitignore`, and this README.

Keep `generated/`, `.cache/`, `reports/`, `bazel-*`, Python caches, and local IDE
metadata out of version control. Run generation before building a fresh checkout.

