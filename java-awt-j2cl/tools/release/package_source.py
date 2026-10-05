#!/usr/bin/env python3
"""Create a deterministic source candidate and a resolved-dependency SBOM."""
import argparse
import base64
import gzip
import hashlib
import io
import json
from pathlib import Path
import re
import subprocess
import tarfile

ROOT = Path(__file__).resolve().parents[2]
DIRECTORIES = ("src", "samples", "tools", "docs", "compatibility", "config", "third_party", "release", ".github")
ROOT_FILES = ("package.json", "package-lock.json", "runtime-tests.bzl", "BUILD.bazel", "MODULE.bazel", "MODULE.bazel.lock", ".bazelrc", ".bazelversion", ".bazelignore", ".editorconfig", ".gitignore", "rules_jvm_external++maven+webawt_test_maven_install.json", "LICENSE", "NOTICE", "THIRD_PARTY_NOTICES.md", "README.md", "CONTRIBUTING.md", "CHANGELOG.md", "FINALIZATION.md", "PLAN.md", "API_COMPLETENESS_PLAN.md", "PHASE5_VALIDATION.md", "PHASE0_STATUS.md", "PHASE1_STATUS.md", "PHASE2_TO_4_STATUS.md")


def sha(data):
    return hashlib.sha256(data).hexdigest()


def source_files(root):
    paths = [root / name for name in ROOT_FILES]
    for directory in DIRECTORIES:
        paths.extend(p for p in (root / directory).rglob("*") if p.is_file() and "__pycache__" not in p.parts and p.suffix not in {".pyc", ".class"})
    for path in paths:
        if path.is_symlink() or not path.resolve().is_relative_to(root.resolve()):
            raise ValueError(f"Source must be a regular workspace file: {path}")
        if not path.is_file():
            raise ValueError(f"Required source file missing: {path}")
    return sorted(set(paths), key=lambda p: p.relative_to(root).as_posix())


def archive_bytes(root, files, prefix, contents=None):
    output = io.BytesIO()
    with gzip.GzipFile(fileobj=output, mode="wb", filename="", mtime=0) as compressed:
        with tarfile.open(fileobj=compressed, mode="w", format=tarfile.PAX_FORMAT) as archive:
            for path in files:
                data = contents[path.relative_to(root).as_posix()] if contents is not None else path.read_bytes()
                member = tarfile.TarInfo(prefix + "/" + path.relative_to(root).as_posix())
                member.size = len(data)
                member.mode = 0o644
                member.mtime = 0
                archive.addfile(member, io.BytesIO(data))
    return output.getvalue()


def json_bytes(value):
    return (json.dumps(value, indent=2, sort_keys=True) + "\n").encode()


def modules_in_graph(node):
    found = set()
    if isinstance(node, dict):
        key = node.get("key", "")
        if "@" in key and not key.startswith("<root>"):
            found.add(tuple(key.rsplit("@", 1)))
        for value in node.values():
            found.update(modules_in_graph(value))
    elif isinstance(node, list):
        for value in node:
            found.update(modules_in_graph(value))
    return found


def make_sbom(root, graph, version, contents):
    components = []
    for name, release in sorted(modules_in_graph(graph)):
        if name == "webawt":
            continue
        component = {"type": "library", "bom-ref": f"bazel:{name}@{release}", "name": name, "version": release, "properties": [{"name": "webawt:scope", "value": "resolved-build-graph"}]}
        if name in {"j2cl", "elemental2", "rules_closure", "rules_jvm_external"}:
            component["licenses"] = [{"license": {"id": "Apache-2.0"}}]
        components.append(component)
    lock = json.loads(contents["rules_jvm_external++maven+webawt_test_maven_install.json"])
    for coordinate, artifact in sorted(lock["artifacts"].items()):
        group, name = coordinate.split(":")
        purl = f"pkg:maven/{group}/{name}@{artifact['version']}"
        components.append({"type": "library", "bom-ref": purl, "group": group, "name": name, "version": artifact["version"], "purl": purl, "hashes": [{"alg": "SHA-256", "content": artifact["shasums"]["jar"]}], "properties": [{"name": "webawt:scope", "value": "test-only-maven-lock"}]})
    components.append({"type": "file", "bom-ref": "font:DejaVuSans", "name": "DejaVuSans.ttf", "hashes": [{"alg": "SHA-256", "content": sha(contents["samples/dual_showcase/fonts/DejaVuSans.ttf"])}], "licenses": [{"license": {"name": "Bitstream Vera and DejaVu font terms", "url": "https://dejavu-fonts.github.io/License.html"}}]})
    return {"bomFormat": "CycloneDX", "specVersion": "1.6", "version": 1, "metadata": {"component": {"type": "library", "bom-ref": f"webawt@{version}", "name": "webawt", "version": version, "licenses": [{"license": {"id": "Apache-2.0"}}]}, "properties": [{"name": "webawt:inventory-scope", "value": "Source candidate, resolved Bzlmod modules, root locked Maven test artifacts, bundled font. Not a complete SBOM of downloaded toolchain binaries or their embedded dependencies. Unknown dependency licenses are unasserted."}]}, "components": components}


def package(root, output, graph):
    files = source_files(root)
    contents = {p.relative_to(root).as_posix(): p.read_bytes() for p in files}
    version = re.search(r'module\(\s*name\s*=\s*"webawt",\s*version\s*=\s*"([^"]+)"', contents["MODULE.bazel"].decode())[1]
    if not re.fullmatch(r"\d+\.\d+\.\d+(?:-[A-Za-z0-9.-]+)?", version):
        raise ValueError("Invalid release version")
    prefix = "webawt-" + version
    data = archive_bytes(root, files, prefix, contents)
    digest = sha(data)
    destination = output / (prefix + "-" + digest[:12])
    destination.mkdir(parents=True, exist_ok=True)
    archive_name = prefix + "-source.tar.gz"
    sbom = make_sbom(root, graph, version, contents)
    manifest = {"schemaVersion": 1, "version": version, "sourceArchive": archive_name, "sourceSha256": digest, "integrity": "sha256-" + base64.b64encode(bytes.fromhex(digest)).decode(), "stripPrefix": prefix, "publicationReview": json.loads(contents["release/provenance.json"])["publicationReview"], "files": {name: sha(data) for name, data in contents.items()}, "toolchain": {"bazel": contents[".bazelversion"].decode().strip(), "referenceJdk": "17.0.16+8", "buildJdk": "Bazel remotejdk_21", "playwright": "1.62.0"}}
    artifacts = {archive_name: data, "sbom.cdx.json": json_bytes(sbom), "source-manifest.json": json_bytes(manifest), "resolved-modules.json": json_bytes(graph)}
    artifacts["SHA256SUMS"] = "".join(f"{sha(content)}  {name}\n" for name, content in sorted(artifacts.items())).encode()
    for name, content in artifacts.items():
        path = destination / name
        if path.exists() and path.read_bytes() != content:
            raise ValueError(f"Refusing to replace an immutable candidate artifact: {path}")
        path.write_bytes(content)
    return destination


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / "out/releases")
    parser.add_argument("--graph", type=Path)
    parser.add_argument("--expected-version")
    args = parser.parse_args()
    module_version = re.search(r'module\(\s*name\s*=\s*"webawt",\s*version\s*=\s*"([^"]+)"', (ROOT / "MODULE.bazel").read_text())[1]
    if args.expected_version and args.expected_version != module_version:
        raise ValueError("Requested version does not match MODULE.bazel")
    graph = json.loads(args.graph.read_text() if args.graph else subprocess.check_output(["bazelisk", "mod", "graph", "--output=json"], cwd=ROOT, text=True))
    if not modules_in_graph(graph):
        raise ValueError("Resolved module graph is empty")
    print(package(ROOT, args.output.resolve(), graph))


if __name__ == "__main__":
    main()
