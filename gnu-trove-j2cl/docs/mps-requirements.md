# Retained MPS Trove requirements

MPS revision: `fb6524e4726c73081d1e0934b54da5a0f78e7d05` (the project pin).
The source archive scan covers 29,557 Java files, 127 Kotlin files, 3,481 MPS
models and 1,688 XML/module/devkit descriptors. Auxiliary tests and samples
are recorded separately and do not add production roots.

**203 of the previous 207 production Java files remain:** 89 directly named
types provided by this port plus 114 implementation/signature dependencies.
These are 201 upstream source files and two portability helpers. There are
118 main-MPS requirements in total; the other 29 are absent from the original
JetBrains baseline, not removed by this change.

| Removed source | Reason |
| --- | --- |
| `gnu.trove.TIntStack` | No main-MPS reference or dependency from retained sources |
| `gnu.trove.TLinkedList` | No main-MPS reference or dependency from retained sources |
| `gnu.trove.TLinkable` | Only needed by the removed intrusive-list implementation |
| `gnu.trove.TLinkableAdaptor` | No main-MPS reference or dependency from retained sources |

The reduction is small because MPS uses more than its handwritten Java imports.
`languages/baseLanguage/collections/languages/trove/languageAccessories/containers.mps`
references the primitive map/set families and their GNU decorators through MPS
model import indices. The collections generator constructs primitive list names
as strings. Removing those families would break generated collections code.
All 40 current GNU decorator sources therefore remain. MPS's own primitive-list
decorators belong to a different package and are not GNU implementation files.

The exact roots, source paths, line evidence, source hashes, all retained names,
dependency edges and reasons are in
[`compatibility/mps-required.json`](../compatibility/mps-required.json).
`dependencyParents` traces each support type back toward a named root.
`qualifiedMentions` preserves textual mentions for audit, including strings.
The existing `mps-usage.json` continues to describe only the executable fixtures.

The 29 unsupported requirements are the short list/set, maps with short keys
or values, and their decorators from MPS's separately bundled Trove 2.1.0.
They are listed individually in `unsupportedRoots`. Keeping similarly named
JetBrains types does not establish Trove 2.1.0 compatibility; that remains a
separate migration task. The externally assembled platform `lib/trove.jar`
has not been identified byte-for-byte.

## Reproduce and enforce

Download the pinned archive from `archiveUrl` in the JSON, then run:

```sh
python3 tools/mps_requirements.py --archive out/research/mps-pinned.tar.gz
python3 tools/mps_requirements.py
python3 tools/port_sources.py --check
bash tools/validate.sh
```

The archive is scanned without extraction; its checksum and per-evidence-file
hashes are recorded. Ordinary offline validation recomputes the source dependency
closure from the vendored upstream source JAR and reviewed port transformations.
It rejects added or missing production sources. Regeneration emits only retained
sources, and compiled API comparison checks every retained public/protected API
against the original binary. JVM output directories are cleaned before compiling
so stale removed classes cannot hide missing dependencies. Original third-party
JARs remain as provenance and differential-test inputs; they are not runtime
dependencies or compiled production sources.

This is a conservative source-unit closure for the inspected revision, not a
minimum JavaScript bundle or a whole-MPS integration test. It includes full class
APIs and method bodies. It does not discover requirements in external IntelliJ
binaries, plugins, user languages or arbitrary reflective names. Review the
profile when changing MPS revision or adding consumers.
