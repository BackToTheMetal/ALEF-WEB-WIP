#!/usr/bin/env python3
"""Enforce the source-ownership and browser-only runtime policy."""

from __future__ import annotations

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
OVERLAY = ROOT / "overlay" / "jre" / "java"
BASELINE = ROOT / "compatibility" / "j2cl-java-io-baseline.json"
MPS_INVENTORY = ROOT / "compatibility" / "mps-java-io-usage.json"
MPS_NON_API = ROOT / "compatibility" / "mps-java-io-non-api-tokens.json"

FORBIDDEN = (
    "localStorage",
    "indexedDB",
    "elemental2",
    "jsinterop",
    "FileSystemAccess",
    "native ",
)


def declared_type(path: Path) -> str:
    relative = path.relative_to(OVERLAY)
    return ".".join(relative.with_suffix("").parts)


def main() -> int:
    baseline = json.loads(BASELINE.read_text(encoding="utf-8"))
    upstream = set(baseline["upstreamTypes"])
    sources = sorted(OVERLAY.rglob("*.java"))
    additions = {declared_type(path) for path in sources}
    duplicates = sorted(upstream & additions)
    if duplicates:
        raise SystemExit("overlay duplicates stock J2CL types: " + ", ".join(duplicates))
    for path in sources:
        text = path.read_text(encoding="utf-8")
        for token in FORBIDDEN:
            if token in text:
                raise SystemExit(f"forbidden runtime dependency {token!r} in {path}")
    print(f"Source policy passed for {len(sources)} added JRE types.")

    if MPS_INVENTORY.exists():
        inventory = json.loads(MPS_INVENTORY.read_text(encoding="utf-8"))
        ignored = set()
        if MPS_NON_API.exists():
            ignored = set(json.loads(MPS_NON_API.read_text(encoding="utf-8"))["tokens"])
        required = {
            f"java.io.{name}"
            for name, usage in inventory["types"].items()
            if name != "*"
            and name not in ignored
            and (usage["importCount"] > 0 or usage["qualifiedReferenceCount"] > 0)
        }
        missing = sorted(required - upstream - additions)
        if missing:
            raise SystemExit("MPS java.io names without J2CL coverage: " + ", ".join(missing))
        print(f"MPS type coverage passed for {len(required)} named java.io references.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
