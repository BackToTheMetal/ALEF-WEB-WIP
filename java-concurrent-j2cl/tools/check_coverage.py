#!/usr/bin/env python3
"""Require inventoried MPS class names in the emitted J2CL JRE header jar."""

import argparse
import hashlib
import json
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("header_jar", type=Path)
    args = parser.parse_args()
    if not args.header_jar.is_file():
        raise SystemExit("Compiled JRE header jar does not exist: " + str(args.header_jar))
    inventory_path = ROOT / "compatibility/mps-concurrent-usage.json"
    inventory = json.loads(inventory_path.read_text(encoding="utf-8-sig"))
    with zipfile.ZipFile(args.header_jar) as jar:
        names = set(jar.namelist())
    classes = []
    for item in inventory["classes"]:
        name = item["name"]
        source = name.replace(".", "/") + ".java"
        entry = name.replace(".", "/") + ".class"
        classes.append({
            "name": name,
            "source_owner": "overlay" if (ROOT / "src/main/super" / source).is_file() else "upstream_j2cl",
            "compiled_class": entry,
            "emitted": entry in names,
            "referencing_files": item["file_count"],
        })
    missing = [item["name"] for item in classes if not item["emitted"]]
    report = {
        "schema_version": 1,
        "status": "FAIL" if missing else "PASS",
        "j2cl_version": "20260402",
        "mps_commit": inventory["commit"],
        "inventory_sha256": hashlib.sha256(inventory_path.read_bytes()).hexdigest(),
        "header_jar_sha256": hashlib.sha256(args.header_jar.read_bytes()).hexdigest(),
        "scope": "Compiled class membership only; this is not proof of method parity or complete MPS compatibility.",
        "summary": {
            "inventoried_classes": len(classes),
            "inventoried_concurrent_classes": sum(item["name"].startswith("java.util.concurrent.") for item in classes),
            "emitted_classes": sum(item["emitted"] for item in classes),
            "overlay_classes": sum(item["source_owner"] == "overlay" for item in classes),
            "upstream_classes": sum(item["source_owner"] == "upstream_j2cl" for item in classes),
        },
        "missing_classes": missing,
        "classes": classes,
    }
    destination = ROOT / "compatibility/api-coverage.json"
    destination.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"status": report["status"], **report["summary"], "missing_classes": missing}, indent=2))
    if missing:
        raise SystemExit("MPS concurrent classes missing from compiled JRE: " + ", ".join(missing))


if __name__ == "__main__":
    main()
