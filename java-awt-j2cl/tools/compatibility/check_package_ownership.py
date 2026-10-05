#!/usr/bin/env python3
"""Audit project namespaces against the actual pinned J2CL JRE source tree."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess

MISSING_PREREQUISITES = ("java.io.Externalizable", "java.util.Hashtable", "java.lang.ClassLoader")


def inventory(root):
    packages, types = set(), set()
    for path in root.rglob("*.java"):
        match = re.search(r"^package\s+([\w.]+)\s*;", path.read_text(encoding="utf-8"), re.M)
        if match and match[1].startswith(("java.", "javax.")):
            packages.add(match[1])
            if path.stem != "package-info":
                types.add(match[1] + "." + path.stem)
    return packages, types


def audit(project, jre):
    owned, types = inventory(jre)
    project_packages, _ = inventory(project)
    if not owned or "java.lang" not in owned:
        raise ValueError("Not a J2CL JRE source tree")
    missing = [name for name in MISSING_PREREQUISITES if name.rsplit(".", 1)[0] in owned and name not in types]
    return {"schemaVersion": 1, "projectPackages": sorted(project_packages), "j2clPackages": sorted(owned),
            "collisions": sorted(project_packages & owned), "missingTypesInOwnedPackages": missing,
            "j2clTypeInventorySha256": hashlib.sha256("\n".join(sorted(types)).encode()).hexdigest()}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--j2cl-root", type=Path)
    args = parser.parse_args()
    root = args.j2cl_root
    if root is None:
        bazel = shutil.which("bazelisk") or shutil.which("bazel")
        output = subprocess.run([bazel, "info", "output_base"], capture_output=True, text=True, check=True).stdout.strip()
        root = Path(output) / "external/j2cl+/jre/java"
    report = audit(Path("src/main/java"), root)
    Path("compatibility/package-ownership-report.json").write_text(json.dumps(report, indent=2) + "\n")
    print("Package ownership: " + str(len(report["projectPackages"])) + " project packages; collisions=" + str(report["collisions"]))
    return int(bool(report["collisions"]))


if __name__ == "__main__":
    raise SystemExit(main())
