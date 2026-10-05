#!/usr/bin/env python3
"""Compare lexical VFS operations with untouched upstream JVM source."""
import argparse
import difflib
import json
import os
import re
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TESTS = ROOT / "tests/vfs"
SOURCE_ROOTS = ["core/vfs/source", "core/vfs/deprecated", "core/openapi/source",
                "core/util/source", "core/logging/source", "core/components/source",
                "core/mps-annotations/source"]


def command(args):
    result = subprocess.run(args, text=True, encoding="utf-8", capture_output=True)
    if result.returncode:
        raise SystemExit(result.stdout + result.stderr)
    return result.stdout


def variant(args, name):
    work = ROOT / ".work/vfs-oracle"
    work.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix=name + "-", dir=work) as scratch:
        sources = []
        for directory in ["core/vfs/source", "core/vfs/deprecated"]:
            for original in sorted((ROOT / "mps" / directory).rglob("*.java")):
                relative = original.relative_to(ROOT / "mps")
                if str(relative).replace("\\", "/").startswith("core/vfs/source/jetbrains/mps/vfs/impl/"):
                    continue
                overlay = ROOT / "mps-web" / relative
                sources.append(overlay if name == "overlay" and overlay.exists() else original)
        sources.append(TESTS / "VfsLexicalTest.java")
        if name == "overlay":
            sources.append(TESTS / "ArchiveBoundaryTest.java")
        classpath = os.pathsep.join(map(str, [args.annotations_jar, args.trove_jar]))
        command([args.javac, "--release", "21", "-encoding", "UTF-8", "-Xlint:-removal",
                 "-d", scratch, "-cp", classpath, "-sourcepath",
                 os.pathsep.join(str(ROOT / "mps" / path) for path in SOURCE_ROOTS),
                 *map(str, sources)])
        runtime = [args.java, "-ea", "-Duser.language=en", "-Duser.country=US", "-cp",
                   os.pathsep.join([scratch, classpath])]
        output = command(runtime + ["mpsj2cl.vfs.VfsLexicalTest"])
        if name == "overlay":
            if command(runtime + ["mpsj2cl.vfs.ArchiveBoundaryTest"]) != "PASS":
                raise SystemExit("Archive boundary check failed")
        return output


def save_or_check(path, text, check):
    if check:
        if not path.is_file() or path.read_text(encoding="utf-8") != text:
            raise SystemExit("Stale VFS oracle artifact: " + str(path))
    else:
        path.write_text(text, encoding="utf-8", newline="\n")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--annotations-jar", type=Path, required=True)
    parser.add_argument("--trove-jar", type=Path,
                        default=ROOT.parent / "gnu-trove-j2cl/third_party/trove4j-1.0.20200330.jar")
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    args.annotations_jar = args.annotations_jar.resolve(strict=True)
    args.trove_jar = args.trove_jar.resolve(strict=True)
    version_text = subprocess.run([args.java, "-version"], text=True, capture_output=True, check=True).stderr
    java_version = re.search(r'version "([^"]+)"', version_text)[1]
    expected = variant(args, "upstream")
    actual = variant(args, "overlay")
    if expected != actual:
        print("".join(difflib.unified_diff(expected.splitlines(True), actual.splitlines(True),
                                          fromfile="upstream JVM", tofile="overlay JVM")))
        raise SystemExit("VFS overlay differs from upstream JVM lexical behavior")
    save_or_check(TESTS / "jvm-expected.txt", expected, args.check)
    baseline = {
        "mps_revision": command(["git", "-C", str(ROOT / "mps"), "rev-parse", "HEAD"]).strip(),
        "java_version": java_version,
        "oracle_source_roots": ["mps/core/vfs/source", "mps/core/vfs/deprecated"],
        "coverage": "Lexical Path/FileUtil operations, 5461 exhaustive short macro inputs, PathSpec, QualifiedPath, file/JAR URL encoding",
        "browser_only": "ArchiveBoundaryTest verifies explicit failure for ZIP creation and archive content access",
        "boundary": "JAR URL ASCII escaping preserves exact Unicode sequences; unlike JDK URI it does not NFC-normalize decomposed text",
    }
    save_or_check(TESTS / "jvm-baseline.json", json.dumps(baseline, indent=2) + "\n", args.check)
    print(f"VFS oracle: {len(expected.splitlines())} result rows, 5461 macro inputs; overlay matches; archive boundaries PASS")


if __name__ == "__main__":
    main()
