#!/usr/bin/env python3
"""Invoke J2CL on NIO sources, keeping Bazel symlinks inside one Java module."""
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile


def main():
    compiler, system, core_api, jdk_api, output, *sources = sys.argv[1:]
    # javac resolves Bazel's source symlinks before checking --patch-module roots.
    # A physical staging tree keeps the sources and the module root consistent.
    with tempfile.TemporaryDirectory(prefix="nio-sources-") as temporary:
        staged = []
        for source in sources:
            parts = Path(source).parts
            relative = Path(*parts[parts.index("java"):])
            if relative.parts[:2] != ("java", "nio"):
                raise ValueError("Only java.nio source inputs are allowed: " + source)
            target = Path(temporary) / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(source, target)
            staged.append(str(target))
        subprocess.run([
            compiler, "-frontend", "JAVAC", "-system", system,
            "-classpath", core_api + ":" + jdk_api,
            "-javacOptions", "--patch-module=java.base=" + temporary + ":" + jdk_api,
            "-javacOptions", "--add-reads=java.base=ALL-UNNAMED",
            "-d", output, *staged,
        ], check=True)


if __name__ == "__main__":
    main()
