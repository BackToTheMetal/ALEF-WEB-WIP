#!/usr/bin/env python3
"""Check the shared Class, core API and Modifier fixtures against a stock Java 21 JVM."""
import argparse
import difflib
import re
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--check", action="store_true", help="Verify committed goldens (the default; this tool never updates them)")
    args = parser.parse_args()
    version = subprocess.run([args.java, "-version"], text=True, capture_output=True, check=True).stderr
    if not re.search(r'version "21[.\"]', version):
        raise SystemExit("Core oracles require Java 21: " + version.strip())
    suites = [
        ("class_support", "mpsj2cl.classsupport.ClassSupportTest", ["FixtureTypes.java", "ClassSupportTest.java"]),
        ("api_basics", "mpsj2cl.apibasics.CoreApiTest", ["CoreApiTest.java"]),
        ("modifiers", "mpsj2cl.modifiers.ModifierTest", ["ModifierTest.java"]),
    ]
    failures = []
    with tempfile.TemporaryDirectory(prefix="mps-core-oracles-") as directory:
        for suite, entrypoint, filenames in suites:
            classes = Path(directory) / suite
            classes.mkdir()
            try:
                subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-d", str(classes),
                                *[str(ROOT / "tests" / suite / name) for name in filenames]], check=True)
                actual = subprocess.check_output([args.java, "-ea", "-cp", str(classes), entrypoint],
                                                 text=True, encoding="utf-8")
                if suite == "modifiers":
                    # The shared fixture itself checks the independently recorded JVM fingerprint.
                    if not actual.strip():
                        raise ValueError("Modifier assertions completed without a result")
                else:
                    golden = ROOT / "tests" / suite / "jvm-expected.txt"
                    expected = golden.read_text(encoding="utf-8")
                    if actual != expected:
                        print("".join(difflib.unified_diff(expected.splitlines(True), actual.splitlines(True),
                                                          fromfile=str(golden), tofile="stock JVM")))
                        raise ValueError("Committed golden differs from the stock JVM")
                print(f"{suite} stock JVM oracle: PASS", flush=True)
            except (OSError, subprocess.CalledProcessError, ValueError) as error:
                failures.append(suite)
                print(f"{suite} stock JVM oracle: FAIL: {error}", flush=True)
    if failures:
        raise SystemExit("Core oracle failures: " + ", ".join(failures))


if __name__ == "__main__":
    main()
