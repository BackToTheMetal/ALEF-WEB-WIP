#!/usr/bin/env python3
"""Verify that the transpiled library contains exactly our NIO sources and types."""
import hashlib
import json
from pathlib import Path
import zipfile

ROOT = Path(__file__).resolve().parent


def main():
    source_root = ROOT / "generated/src"
    expected = {path.relative_to(source_root).as_posix(): path.read_text(encoding="utf-8")
                for path in source_root.rglob("*.java")}
    artifact = ROOT / "reports/nio-stubs.js.zip"
    with zipfile.ZipFile(artifact) as archive:
        files = {entry.filename for entry in archive.infolist() if not entry.is_dir()}
        outside = [name for name in files if not name.startswith("java/nio/")]
        if outside:
            raise SystemExit("Unexpected non-NIO output: " + str(sorted(outside)))
        actual_sources = {name for name in files if name.endswith(".java")}
        if actual_sources != expected.keys():
            raise SystemExit("The output Java source inventory differs from the generated NIO sources")
        for name, text in expected.items():
            if archive.read(name).decode("utf-8").replace("\r\n", "\n") != text:
                raise SystemExit("A generated source was replaced or edited: " + name)
        # Every javac-emitted NIO type must have a corresponding J2CL implementation module.
        classes = ROOT / "generated/classes"
        for path in classes.rglob("*.class"):
            module = path.relative_to(classes).as_posix().removesuffix(".class") + ".impl.java.js"
            if module not in files:
                raise SystemExit("Missing transpiled type: " + module)
        modules = sum(name.endswith(".impl.java.js") for name in files)
    result = {"status": "PASS", "source_files": len(expected), "javascript_modules": modules,
              "non_nio_output_files": 0, "replaced_source_files": 0,
              "artifact_sha256": hashlib.sha256(artifact.read_bytes()).hexdigest()}
    (ROOT / "reports/output-check.json").write_text(json.dumps(result, indent=2) + "\n")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
