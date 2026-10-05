#!/usr/bin/env python3
"""Compare real upstream/overlay StringsIO, Message, and source-root diagnostics."""
import argparse
import difflib
import json
import os
import re
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCES = [
    "core/kernel/kernelSolution/source_gen/jetbrains/mps/util/StringsIO.java",
    "core/kernel/source/jetbrains/mps/messages/Message.java",
    "core/kernel/source/jetbrains/mps/extapi/persistence/SourcePaths.java",
    "core/kernel/source/jetbrains/mps/extapi/persistence/FileKindIsNotAllowedException.java",
]
SOURCE_ROOTS = [
    "core/kernel/source", "core/kernel/kernelSolution/source_gen", "core/mps-messaging/source",
    "core/openapi/source", "core/util/source", "core/logging/source", "core/mps-annotations/source",
    "core/vfs/source", "core/vfs/deprecated",
    "languages/baseLanguage/references/runtime/source", "languages/baseLanguage/closures/runtime/source_gen",
    "languages/baseLanguage/collections/runtime/source_gen",
]


def variant(args, name):
    work = ROOT / ".work/kernel-utilities-oracle"
    work.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(dir=work, prefix=name + "-") as output:
        sources = [ROOT / ("mps" if name == "upstream" else "mps-web") / source for source in SOURCES]
        sources.append(ROOT / "tests/kernel_utilities/KernelUtilitiesTest.java")
        result = subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-d", output,
                                 "-cp", str(args.annotations_jar), "-sourcepath",
                                 os.pathsep.join(str(ROOT / "mps" / path) for path in SOURCE_ROOTS),
                                 *map(str, sources)], text=True, capture_output=True)
        if result.returncode:
            raise SystemExit(result.stdout + result.stderr)
        return subprocess.check_output([args.java, "-ea", "-Dfile.encoding=UTF-8", "-Duser.language=en",
                                          "-Duser.country=US", "-cp", output,
                                          "jetbrains.mps.extapi.persistence.KernelUtilitiesTest"],
                                         text=True, encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--annotations-jar", type=Path, required=True)
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--java", default="java")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    args.annotations_jar = args.annotations_jar.resolve(strict=True)
    expected = variant(args, "upstream")
    actual = variant(args, "overlay")
    if expected != actual:
        print("".join(difflib.unified_diff(expected.splitlines(True), actual.splitlines(True),
                                          fromfile="upstream JVM", tofile="overlay JVM")))
        raise SystemExit("Kernel utility parity failure")
    version = subprocess.run([args.java, "-version"], text=True, capture_output=True, check=True).stderr
    baseline = {"java_version": re.search(r'version "([^"]+)"', version)[1],
                "encoding": "UTF-8", "locale": "en-US", "oracle_sources": SOURCES,
                "coverage": "Scanner line delimiters, partial UTF-8/read/close failures, output closure, message timestamps, diagnostic text and toString ordering",
                "result_rows": len(expected.splitlines())}
    for path, value in [(ROOT / "tests/kernel_utilities/jvm-expected.txt", expected),
                         (ROOT / "tests/kernel_utilities/jvm-baseline.json", json.dumps(baseline, indent=2) + "\n")]:
        if args.check:
            if not path.is_file() or path.read_text(encoding="utf-8") != value:
                raise SystemExit("Stale utility oracle artifact: " + str(path))
        else:
            path.write_text(value, encoding="utf-8", newline="\n")
    print(f"Kernel utilities: {baseline['result_rows']} JVM/overlay rows match")


if __name__ == "__main__":
    main()
