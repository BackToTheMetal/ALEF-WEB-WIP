#!/usr/bin/env python3
"""Reuse the independent class-file gate, allowing only interface-list reordering."""
import argparse
import json
import re
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def header_key(text):
    # Only an unordered implements list is normalized. Generic argument order stays significant.
    prefix, marker, interfaces = text.partition(" implements ")
    items, depth, start = [], 0, 0
    for i, char in enumerate(interfaces):
        depth += (char == "<") - (char == ">")
        if char == "," and depth == 0:
            items.append(interfaces[start:i])
            start = i + 1
    if interfaces:
        items.append(interfaces[start:])
    clean = lambda s: re.sub(r"\s+", "", s)
    return clean(prefix), bool(marker), tuple(sorted(clean(s) for s in items))


def check(jar, report):
    intermediate = ROOT / "build" / (report.stem + "-text-comparison.json")
    intermediate.unlink(missing_ok=True)
    result = subprocess.run([
        sys.executable, str(ROOT.parent / "tools/check_api.py"), str(jar),
        "--reference", str(ROOT / "reports/jdk-api.json"), "--report", str(intermediate),
    ], capture_output=True, text=True)
    if not intermediate.exists():
        raise RuntimeError(result.stdout + result.stderr)
    data = json.loads(intermediate.read_text())
    reordered = []
    missing = []
    for item in data["missing"]:
        if (item["missing"] == "declaration mismatch"
                and header_key(item["expected"]) == header_key(item["actual"])):
            reordered.append(item["type"])
        else:
            missing.append(item)
    data["missing"] = missing
    data["interface_order_normalized"] = reordered
    data["scope"] = "Public/protected non-preview declarations; excludes sealed restrictions, annotations, constant values and runtime semantics."
    data["status"] = "PASS" if not missing else "FAIL"
    report.write_text(json.dumps(data, indent=2) + "\n")
    print(json.dumps({k: data[k] for k in ["status", "types", "members_checked", "missing", "interface_order_normalized"]}, indent=2))
    if missing:
        raise SystemExit(1)
    return data


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("jar", type=Path)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    check(args.jar, args.report)
