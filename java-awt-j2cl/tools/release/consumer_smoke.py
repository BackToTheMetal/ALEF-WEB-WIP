#!/usr/bin/env python3
"""Consume only a candidate archive in a separate Bazel root and run the showcase."""
import argparse
import hashlib
import json
from pathlib import Path
import shutil
import subprocess
import tarfile


def prepare(candidate, destination):
    manifest = json.loads((candidate / "source-manifest.json").read_text())
    archive = candidate / manifest["sourceArchive"]
    if archive.parent != candidate or hashlib.sha256(archive.read_bytes()).hexdigest() != manifest["sourceSha256"]:
        raise ValueError("Candidate source checksum mismatch")
    if destination.exists() and any(destination.iterdir()):
        raise ValueError("Consumer directory must be empty; use a fresh directory")
    destination.mkdir(parents=True, exist_ok=True)
    extracted = destination / "source"
    with tarfile.open(archive) as source:
        for member in source.getmembers():
            if not member.isfile() or not member.name.startswith(manifest["stripPrefix"] + "/") or not (extracted / member.name).resolve().is_relative_to(extracted.resolve()):
                raise ValueError("Unsafe source archive member")
        source.extractall(extracted, filter="data")
    library = extracted / manifest["stripPrefix"]
    for relative, digest in manifest["files"].items():
        path = library / relative
        if not path.resolve().is_relative_to(library.resolve()) or hashlib.sha256(path.read_bytes()).hexdigest() != digest:
            raise ValueError(f"Source manifest mismatch: {relative}")
    consumer = destination / "consumer"
    consumer.mkdir()
    # Only the published archive supplies source, configuration, patch and locks.
    module = (library / "MODULE.bazel").read_text().replace('name = "webawt",', 'name = "webawt_consumer",', 1)
    module += f'\nbazel_dep(name = "webawt", version = "{manifest["version"]}")\narchive_override(\n    module_name = "webawt",\n    urls = [{json.dumps(archive.as_uri())}],\n    integrity = "{manifest["integrity"]}",\n    strip_prefix = "{manifest["stripPrefix"]}",\n)\n'
    (consumer / "MODULE.bazel").write_text(module)
    for name in (".bazelrc", ".bazelversion", ".bazelignore", "rules_jvm_external++maven+webawt_test_maven_install.json"):
        shutil.copyfile(library / name, consumer / name)
    shutil.copytree(library / "third_party/patches", consumer / "third_party/patches")
    for path in (library / "samples/dual_showcase").glob("*.java"):
        if path.name not in {"DesktopLauncher.java", "DesktopFonts.java"}:
            shutil.copyfile(path, consumer / path.name)
    shutil.copyfile(library / "samples/dual_showcase/app.js", consumer / "app.js")
    shutil.copytree(library / "samples/dual_showcase/fonts", consumer / "fonts")
    with (consumer / "app.js").open("a") as entry:
        entry.write("\nconst RuntimeProbe = goog.require('org.webawt.consumer.ConsumerRuntimeProbe');\nglobalThis['consumerRuntimeResult'] = RuntimeProbe.m_run__java_lang_String();\n")
    for name in ("BUILD.bazel", "ConsumerSmokeTest.java", "ConsumerRuntimeProbe.java"):
        source_name = name + ".template" if name == "BUILD.bazel" else name
        shutil.copyfile(library / "tools/release/consumer" / source_name, consumer / name)
    (consumer / "README.md").write_text("Generated external consumer. WebAWT source is resolved exclusively from the checksummed source archive via archive_override. The Elemental2 and J2CL prerequisite overrides are deliberately applied in this root module.\n")
    return consumer


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--candidate", type=Path, required=True)
    parser.add_argument("--workdir", type=Path, required=True)
    parser.add_argument("--prepare-only", action="store_true")
    args = parser.parse_args()
    consumer = prepare(args.candidate.resolve(), args.workdir.resolve())
    if not args.prepare_only:
        subprocess.run(["bazelisk", "test", "//:consumer_smoke", "--nocache_test_results", "--test_output=errors"], cwd=consumer, check=True)
    print(consumer)


if __name__ == "__main__":
    main()
