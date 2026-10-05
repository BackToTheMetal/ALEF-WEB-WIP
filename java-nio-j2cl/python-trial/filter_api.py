#!/usr/bin/env python3
"""Keep missing JDK type signatures as compilation inputs; emit no runtime code."""
from pathlib import Path
import subprocess
import sys
import tempfile
import zipfile


def main():
    ijar, jdk_api, output, *jre_apis = sys.argv[1:]
    provided = set()
    for path in jre_apis:
        with zipfile.ZipFile(path) as archive:
            provided.update(archive.namelist())
    with tempfile.TemporaryDirectory(prefix="nio-api-") as temporary:
        missing = Path(temporary) / "missing.jar"
        with zipfile.ZipFile(jdk_api) as source, zipfile.ZipFile(missing, "w") as target:
            for name in sorted(source.namelist()):
                if name not in provided and name.endswith(".class"):
                    if name == "module-info.class" or name.startswith("java/nio/"):
                        continue
                    target.writestr(zipfile.ZipInfo(name), source.read(name))
        # Bazel's ijar strips method bodies. This jar is only used for type resolution.
        subprocess.run([ijar, str(missing), output], check=True)


if __name__ == "__main__":
    main()
