#!/usr/bin/env python3
"""Index actual validation artifacts without inventing test or provenance results."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import platform
import subprocess


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--candidate", type=Path, required=True)
    parser.add_argument("--consumer", type=Path, required=True)
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[2]
    candidate = args.candidate.resolve()
    def command(argv):
        result = subprocess.run(argv, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        return {"exitCode": result.returncode, "output": result.stdout.strip()}
    evidence = {"schemaVersion": 1, "sourceManifestSha256": hashlib.sha256((candidate / "source-manifest.json").read_bytes()).hexdigest(), "host": platform.platform(), "git": command(["git", "rev-parse", "HEAD"]), "bazel": command(["bazelisk", "version"]), "java": command(["java", "-version"]), "testArtifacts": {}}
    evidence["workflow"] = {name: os.environ[name] for name in ("GITHUB_SHA", "GITHUB_REF", "GITHUB_RUN_ID", "GITHUB_RUN_ATTEMPT") if name in os.environ}
    execution_root = subprocess.check_output(["bazelisk", "info", "execution_root"], cwd=root, text=True).strip()
    build_java = Path(execution_root) / "external/rules_java++toolchains+remotejdk21_linux/bin/java"
    evidence["buildJava"] = command([str(build_java), "-version"]) if build_java.is_file() else {"unavailable": "Inspect resolved Bazel toolchain in MODULE.bazel.lock"}
    for prefix, directory in (("workspace", root / "bazel-testlogs"), ("external-consumer", args.consumer / "bazel-testlogs")):
        if directory.exists():
            for path in sorted(directory.rglob("*")):
                if path.is_file() and (path.name in {"test.log", "test.xml"} or "test.outputs" in path.parts):
                    evidence["testArtifacts"][prefix + "/" + path.relative_to(directory).as_posix()] = hashlib.sha256(path.read_bytes()).hexdigest()
    if not any(name.startswith("external-consumer/") and name.endswith("test.xml") for name in evidence["testArtifacts"]):
        raise ValueError("External consumer test XML is missing")
    output = candidate / "validation-environment.json"
    output.write_text(json.dumps(evidence, indent=2, sort_keys=True) + "\n")
    print(output)


if __name__ == "__main__":
    main()
