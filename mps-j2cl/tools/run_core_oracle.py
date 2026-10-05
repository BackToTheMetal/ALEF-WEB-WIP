#!/usr/bin/env python3
"""Run the shared graph/model smoke test on untouched upstream MPS JVM sources."""
import argparse
import json
import os
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--classpath", type=Path, action="append", required=True,
                        help="Repeat for actual JVM JetBrains annotations, Trove and upstream JDOM classes/JAR")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--java", default="java")
    args = parser.parse_args()
    catalog = json.loads((ROOT / "compatibility/mps-projects.json").read_text())
    source_roots = sorted({root for name in catalog["initial_projects"]
                           for root in catalog["projects"][name]["src_roots"]})
    classpath = os.pathsep.join(str(path.resolve(strict=True)) for path in args.classpath)
    with tempfile.TemporaryDirectory(prefix="mps-core-oracle-") as output:
        result = subprocess.run([args.javac, "-J-Xmx384m", "--release", "21", "-encoding", "UTF-8",
                                 "-d", output, "-cp", classpath, "-sourcepath",
                                 os.pathsep.join(str(ROOT / "mps" / root) for root in source_roots),
                                 str(ROOT / "tests/core/KernelSmokeTest.java")], text=True, capture_output=True)
        if result.returncode:
            raise SystemExit(result.stdout + result.stderr)
        classpath = os.pathsep.join([output, classpath, str(ROOT / "mps/core/resources_en/source")])
        result = subprocess.run([args.java, "-ea", "-cp", classpath, "mpsj2cl.core.KernelSmokeTest"],
                                text=True, capture_output=True)
        if result.returncode or result.stdout != "PASS":
            raise SystemExit(result.stdout + result.stderr)
    print("Upstream MPS core graph/model/events JVM oracle: PASS")


if __name__ == "__main__":
    main()
