#!/usr/bin/env python3
"""Check runtime behavior against Java 21, then execute the actual UUID source as a JVM patch."""
import argparse
import difflib
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]
TESTS = ROOT / "tests/runtime_support"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    version = subprocess.run([args.java, "-version"], text=True, capture_output=True, check=True).stderr
    if not re.search(r'version "21[.\"]', version):
        raise SystemExit("Runtime oracle requires Java 21: " + version.strip())
    work = ROOT / ".work/runtime-oracle"
    classes = work / "classes"
    patched = work / "patched"
    source = work / "source/java/util/UUID.java"
    source.parent.mkdir(parents=True, exist_ok=True)
    classes.mkdir(parents=True, exist_ok=True)
    patched.mkdir(parents=True, exist_ok=True)
    source.write_text((ROOT / "third_party/j2cl/api-support/jre/java/java/util/UUID.java").read_text(encoding="utf-8"),
                      encoding="utf-8", newline="\n")
    subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-d", str(classes),
                    *map(str, TESTS.glob("*.java")), str(TESTS / "jvm/UuidParseOracle.java"),
                    str(TESTS / "jvm/CharacterDataOracle.java"),
                    str(ROOT / "third_party/j2cl/api-support/jre/java/javaemul/internal/CharacterData.java")], check=True)
    subprocess.run([args.javac, "--patch-module", "java.base=" + str(work / "source"),
                    "-encoding", "UTF-8", "-d", str(patched), str(source)], check=True)

    def run(name, patch=False):
        options = ["--patch-module", "java.base=" + str(patched)] if patch else []
        return subprocess.check_output([args.java, *options, "-cp", str(classes), "mpsj2cl.runtime." + name],
                                       text=True, encoding="utf-8")

    expected = run("RuntimeSupportTest")
    print(run("CharacterDataOracle").strip())
    golden = TESTS / "jvm-expected.txt"
    if args.check:
        if golden.read_text(encoding="utf-8") != expected:
            raise SystemExit("Runtime golden is stale: " + str(golden))
    else:
        golden.write_text(expected, encoding="utf-8", newline="\n")
    for name, oracle in [("RuntimeSupportTest", expected), ("UuidParseOracle", run("UuidParseOracle"))]:
        actual = run(name, patch=True)
        if actual != oracle:
            print("".join(difflib.unified_diff(oracle.splitlines(True), actual.splitlines(True),
                                               fromfile="stock JVM", tofile="UUID replacement")))
            raise SystemExit(name + " differs from the stock JVM")
        print(name + ": PASS" + ("; " + actual.strip() if name == "UuidParseOracle" else ""))


if __name__ == "__main__":
    main()
