#!/usr/bin/env python3
"""Run the shared dynamic-component lifecycle suite on untouched upstream JVM MPS."""
import argparse
import os
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--annotations-jar", type=Path, required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    args = parser.parse_args()
    roots = ["core/kernel/source", "core/components/source", "core/logging/source"]
    with tempfile.TemporaryDirectory(prefix="mps-dynamic-oracle-") as output:
        result = subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-d", output,
                                 "-cp", str(args.annotations_jar.resolve(strict=True)), "-sourcepath",
                                 os.pathsep.join(str(ROOT / "mps" / path) for path in roots),
                                 str(ROOT / "tests/dynamic_components/DynamicComponentLifecycleTest.java")],
                                text=True, capture_output=True)
        if result.returncode:
            raise SystemExit(result.stdout + result.stderr)
        result = subprocess.run([args.java, "-ea", "-cp", output,
                                  "mpsj2cl.dynamiccomponents.DynamicComponentLifecycleTest"],
                                 text=True, capture_output=True)
        if result.returncode or result.stdout != "PASS":
            raise SystemExit(result.stdout + result.stderr)
    print("Dynamic component upstream JVM lifecycle: PASS")


if __name__ == "__main__":
    main()
