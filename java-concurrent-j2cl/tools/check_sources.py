#!/usr/bin/env python3
"""Check J2CL-only source ownership and implementation boundaries."""

import re
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OVERLAY = ROOT / "src/main/super"


def main():
    fixture = json.loads((ROOT / "compatibility/mps-fixture.json").read_text(encoding="utf-8-sig"))
    if hashlib.sha256((ROOT / fixture["fixture"]).read_bytes()).hexdigest() != fixture["sha256"]:
        raise SystemExit("Pinned unchanged MPS fixture was modified")
    sources = sorted(OVERLAY.rglob("*.java"))
    if not sources:
        raise SystemExit("Missing canonical JRE overlay sources")
    for source in sources:
        text = source.read_text(encoding="utf-8")
        relative = source.relative_to(OVERLAY).as_posix()
        if relative == "module-info.java":
            continue
        expected_package = relative.rsplit("/", 1)[0].replace("/", ".")
        package = re.search(r"(?m)^package\s+([\w.]+)\s*;", text)
        if not package or package[1] != expected_package:
            raise SystemExit("Package/path mismatch: " + relative)
        # JsInterop native declarations may call the browser's main-thread APIs.
        # JVM native libraries, Unsafe, and VarHandle are unavailable in J2CL.
        code = re.sub(r"/\*.*?\*/|//[^\n]*", "", text, flags=re.S)
        for forbidden in [r"sun\.misc", r"jdk\.internal", r"java\.lang\.invoke\.VarHandle", r"System\.load(?:Library)?\s*\("]:
            if re.search(forbidden, code):
                raise SystemExit("Unsupported runtime dependency in " + relative + ": " + forbidden)
    print("Validated package ownership and J2CL-only boundaries for " + str(len(sources)) + " Java sources.")


if __name__ == "__main__":
    main()
