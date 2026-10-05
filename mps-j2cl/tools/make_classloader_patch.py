#!/usr/bin/env python3
"""Generate the browser ClassLoader patch against its composed JRE preimage."""

from __future__ import annotations

import argparse
import difflib
import hashlib
import json
from pathlib import Path
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parents[1]
DIRECTORY = ROOT / "third_party/j2cl/mps-support"
TARGET = "jre/java/java/lang/ClassLoader.java"


def sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def preimage() -> str:
    patch = (ROOT / "third_party/j2cl/composed.patch").read_text(encoding="utf-8")
    marker = "diff --git a/" + TARGET + " b/" + TARGET + "\n"
    if patch.count(marker) != 1:
        raise SystemExit("Expected one ClassLoader provider in composed.patch")
    section = patch.split(marker, 1)[1].split("diff --git ", 1)[0]
    if "new file mode 100644\n" not in section or "--- /dev/null\n" not in section:
        raise SystemExit("Composed ClassLoader preimage changed; review its ownership")
    lines = section.splitlines(keepends=True)
    hunk = next(i for i, line in enumerate(lines) if line.startswith("@@ "))
    if any(not line.startswith("+") for line in lines[hunk + 1:]):
        raise SystemExit("Unexpected composed ClassLoader patch structure")
    before = "".join(line[1:] for line in lines[hunk + 1:])
    manifest = json.loads((ROOT / "third_party/j2cl/composition.json").read_text(encoding="utf-8"))
    owner = next(entry for entry in manifest["ownership"] if entry["path"] == TARGET)
    if sha256(before.encode("utf-8")) != owner["emitted_sha256"]:
        raise SystemExit("Composed ClassLoader source does not match its ownership hash")
    return before


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    before = preimage()
    source = DIRECTORY / TARGET
    after = source.read_text(encoding="utf-8")
    if not after.endswith("\n"):
        raise SystemExit("ClassLoader source must end with a newline")
    patch = "diff --git a/" + TARGET + " b/" + TARGET + "\n" + "".join(difflib.unified_diff(
        before.splitlines(keepends=True), after.splitlines(keepends=True),
        fromfile="a/" + TARGET, tofile="b/" + TARGET))
    manifest = {
        "schema_version": 1,
        "j2cl_version": "20260402",
        "patch_order": ["third_party/j2cl/composed.patch", "third_party/j2cl/type-support/class-type-support.patch",
                        "third_party/j2cl/mps-support/classloader.patch"],
        "source": {"path": source.relative_to(ROOT).as_posix(), "sha256": sha256(source.read_bytes())},
        "generator": {"path": "tools/make_classloader_patch.py", "sha256": sha256(Path(__file__).read_bytes())},
        "preimage": {"provider": "java-nio-j2cl via composed.patch", "path": TARGET,
                     "sha256": sha256(before.encode("utf-8"))},
        "output_sha256": sha256(after.encode("utf-8")),
        "patch_sha256": sha256(patch.encode("utf-8")),
        "contract": ["Explicit binary-name to Class registration; normal misses throw ClassNotFoundException.",
                     "Classes are already compiled/linked by J2CL; JVM defineClass fails explicitly.",
                     "Resources are registered byte copies opened as independent buffered streams.",
                     "Resource URLs are opaque registry identifiers; URL.openStream/network/filesystem loading are not provided.",
                     "Only ClassLoader.java changes; Class and existing exception types retain their other providers."],
    }
    outputs = {DIRECTORY / "classloader.patch": patch,
               DIRECTORY / "classloader-provenance.json": json.dumps(manifest, indent=2) + "\n"}
    for path, expected in outputs.items():
        if args.check:
            if not path.exists() or path.read_bytes() != expected.encode("utf-8"):
                raise SystemExit("ClassLoader patch is stale; run python3 tools/make_classloader_patch.py")
        else:
            path.write_text(expected, encoding="utf-8", newline="\n")
    with tempfile.TemporaryDirectory(prefix="mps-classloader-patch-") as temporary:
        baseline = Path(temporary) / TARGET
        baseline.parent.mkdir(parents=True, exist_ok=True)
        baseline.write_text(before, encoding="utf-8", newline="\n")
        subprocess.run(["git", "apply", "--check", str((DIRECTORY / "classloader.patch").resolve())],
                       cwd=temporary, check=True)
    print("ClassLoader patch matches its canonical source and applies to the composed JRE preimage")


if __name__ == "__main__":
    main()
