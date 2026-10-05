#!/usr/bin/env python3
"""Compile the generated source probes against the pinned JDK module image."""
import argparse
import json
from pathlib import Path
import shutil
import subprocess


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-home", type=Path)
    args = parser.parse_args()
    javac = shutil.which("javac")
    java_home = args.java_home or Path(javac).resolve().parent.parent
    release = (java_home / "release").read_text()
    if 'JAVA_VERSION="17.' not in release:
        raise SystemExit("Reference consumer probes require JDK 17")
    source = Path("src/test/consumer/generated").resolve()
    groups = {"java.base": [], "java.datatransfer": [], "java.desktop": []}
    for path in sorted(source.glob("*.java")):
        package = path.read_text().split(";", 1)[0].removeprefix("package ")
        module = "java.base" if package.startswith(("java.net", "java.text")) else "java.datatransfer" if package == "java.awt.datatransfer" else "java.desktop"
        groups[module].append(path)
    for module, paths in groups.items():
        output = Path("out/consumer-probes-reference") / module
        output.mkdir(parents=True, exist_ok=True)
        arguments = output / "sources.args"
        arguments.write_text("\n".join('"' + str(path).replace("\\", "/") + '"' for path in paths))
        subprocess.run([javac, "-source", "17", "-target", "17", "--system", str(java_home), "--patch-module", module + "=" + str(source), "-d", str(output), "@" + str(arguments)], check=True)
        print(f"Reference {module}: {len(paths)} consumer source files compiled")


if __name__ == "__main__":
    main()
