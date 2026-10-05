#!/usr/bin/env python3
"""Generate the pinned J2CL new-file patch from the canonical overlay."""

from __future__ import annotations

import argparse
import difflib
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
OVERLAY = ROOT / "overlay"
OUTPUT = ROOT / "third_party" / "patches" / "j2cl-java-io.patch"


def render() -> str:
    chunks: list[str] = []
    for source in sorted(OVERLAY.rglob("*.java")):
        relative = source.relative_to(OVERLAY).as_posix()
        content = source.read_text(encoding="utf-8").splitlines(keepends=True)
        chunks.append(f"diff --git a/{relative} b/{relative}\n")
        chunks.append("new file mode 100644\n")
        chunks.extend(
            difflib.unified_diff(
                [],
                content,
                fromfile="/dev/null",
                tofile=f"b/{relative}",
                n=3,
            )
        )
    return "".join(chunks)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    expected = render()
    if args.check:
        actual = OUTPUT.read_text(encoding="utf-8") if OUTPUT.exists() else ""
        if actual != expected:
            raise SystemExit("J2CL patch is stale; run tools/generate_patch.py")
        return 0
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(expected, encoding="utf-8", newline="\n")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
