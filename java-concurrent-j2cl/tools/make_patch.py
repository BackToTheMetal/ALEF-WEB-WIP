#!/usr/bin/env python3
"""Generate a deterministic JRE patch from canonical sources and pinned originals."""

import argparse
import difflib
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASELINE = ROOT / "third_party/j2cl-base"
OVERLAY = ROOT / "src/main/super"
OUTPUT = ROOT / "patches/j2cl-concurrent.patch"


def check_baseline():
    manifest = json.loads((BASELINE / "provenance.json").read_text(encoding="utf-8"))
    for relative, expected in manifest["sha256"].items():
        source = BASELINE / relative
        actual = hashlib.sha256(source.read_bytes()).hexdigest() if source.is_file() else None
        if actual != expected:
            raise SystemExit("Pinned J2CL baseline changed: " + relative)


def render():
    check_baseline()
    chunks = []
    for source in sorted(OVERLAY.rglob("*.java")):
        relative = "jre/java/" + source.relative_to(OVERLAY).as_posix()
        original = BASELINE / relative
        before = original.read_text(encoding="utf-8").splitlines(keepends=True) if original.exists() else []
        after = source.read_text(encoding="utf-8").splitlines(keepends=True)
        if before == after:
            continue
        if after and not after[-1].endswith("\n"):
            raise SystemExit("Canonical source must end with a newline: " + str(source))
        chunks.append(f"diff --git a/{relative} b/{relative}\n")
        if not original.exists():
            chunks.append("new file mode 100644\n")
        chunks.extend(difflib.unified_diff(
            before, after,
            fromfile="a/" + relative if original.exists() else "/dev/null",
            tofile="b/" + relative,
        ))
    return "".join(chunks)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    result = render()
    if args.check:
        if not OUTPUT.exists() or OUTPUT.read_text(encoding="utf-8") != result:
            raise SystemExit("Overlay patch is stale; run python3 tools/make_patch.py")
        print("Patch matches canonical sources; pinned baseline hashes verified.")
    else:
        OUTPUT.parent.mkdir(parents=True, exist_ok=True)
        OUTPUT.write_text(result, encoding="utf-8", newline="\n")
        print("Generated patches/j2cl-concurrent.patch")


if __name__ == "__main__":
    main()
