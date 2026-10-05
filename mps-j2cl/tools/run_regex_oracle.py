#!/usr/bin/env python3
"""Generate/check exact supported-regex results against the selected JVM runtime."""
import argparse
import json
import re
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--java", default="java")
    args = parser.parse_args()
    directory = ROOT / ".work/regex-oracle"
    directory.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(dir=directory) as output:
        subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-d", output,
                        str(ROOT / "tests/regex/RegexSubsetTest.java")], check=True)
        expected = subprocess.check_output([args.java, "-ea", "-cp", output,
                                             "mpsj2cl.regex.RegexSubsetTest"], text=True, encoding="utf-8")
    version = subprocess.run([args.java, "-version"], text=True, capture_output=True, check=True).stderr
    baseline = {
        "java_version": re.search(r'version "([^"]+)"', version)[1],
        "scope": "Explicitly documented Java 21 regex subset, flags=0 and ASCII CASE_INSENSITIVE, word boundaries; exact result rows",
        "result_rows": len(expected.splitlines()),
        "test_source": "tests/regex/RegexSubsetTest.java",
    }
    for path, text in [(ROOT / "tests/regex/jvm-expected.txt", expected),
                       (ROOT / "tests/regex/jvm-baseline.json", json.dumps(baseline, indent=2) + "\n")]:
        if args.check:
            if not path.is_file() or path.read_text(encoding="utf-8") != text:
                raise SystemExit("Stale regex oracle artifact: " + str(path))
        else:
            path.write_text(text, encoding="utf-8", newline="\n")
    print(f"Regex JVM oracle: {baseline['result_rows']} result rows; JDK {baseline['java_version']}")


if __name__ == "__main__":
    main()
