#!/usr/bin/env python3
"""Compose the sibling JRE profiles without modifying their canonical sources.

Run from any directory with Python 3.10+. Generation needs the sibling source
trees; Bazel consumes only the resulting root-owned composed.patch. --check is
read-only and rejects changes to any recorded input or generated output. Pass
--upstream /path/to/j2cl-20260402 to additionally validate every patch preimage
and run git apply --check against a complete, unmodified J2CL source tree.
"""

from __future__ import annotations

import argparse
import difflib
import hashlib
import json
import re
import subprocess
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
VERSION = "20260402"
ARCHIVE_SHA256 = "d1419aecefec2727d3449f10a772d973ff5d3a7a7470367d1cfc2c782c7d6e66"
OUTPUT = ROOT / "third_party/j2cl"
PREFERRED = {"java/lang/Thread.java", "java/util/concurrent/ThreadFactory.java"}
WEB_NET = {"URI.java", "URISyntaxException.java", "URL.java", "MalformedURLException.java"}


def fail(message: str) -> None:
    raise SystemExit(message)


def digest(content: bytes) -> str:
    return hashlib.sha256(content).hexdigest()


def read_text(path: Path) -> str:
    text = path.read_text(encoding="utf-8")
    if not text.endswith("\n"):
        fail(f"Source must end with a newline: {path}")
    return text


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def describe(path: Path, siblings: Path) -> dict:
    # Logical paths stay identical on Windows and WSL and on other machines.
    return {"source": "../" + path.relative_to(siblings).as_posix(),
            "sha256": digest(path.read_bytes())}


def module_exports(text: str) -> set[str]:
    plain = re.sub(r"/\*.*?\*/|//[^\n]*", "", text, flags=re.S)
    if not re.fullmatch(r"\s*module\s+java\.base\s*\{(?:\s*exports\s+[\w.]+\s*;)*\s*\}\s*", plain):
        fail("Unexpected module-info.java directives; review the composition explicitly")
    return set(re.findall(r"\bexports\s+([\w.]+)\s*;", plain))


def compose(siblings: Path, upstream: Path | None) -> tuple[str, str]:
    nio = siblings / "java-nio-j2cl"
    concurrent = siblings / "java-concurrent-j2cl"
    io = siblings / "java-io-j2cl"
    awt = siblings / "java-awt-j2cl"
    inputs: dict[str, dict] = {}

    def record(path: Path) -> dict:
        item = describe(path, siblings)
        inputs[item["source"]] = item
        return item

    for directory in [nio, concurrent, io, awt]:
        module = directory / "MODULE.bazel"
        record(module)
        if not re.search(r'\bversion\s*=\s*"' + VERSION + '"', read_text(module)):
            fail(f"Sibling does not select J2CL {VERSION}: {module}")

    # Pinned originals cover every existing JRE file modified by these profiles.
    # The full upstream inventory catches accidental new ownership of an existing
    # JRE source even when that file has no locally stored original.
    originals: dict[str, str] = {}
    original_records: dict[str, list[dict]] = {}

    def baseline(path: Path, relative: str, expected: str) -> None:
        if digest(path.read_bytes()) != expected:
            fail(f"Pinned J2CL baseline hash changed: {path}")
        item = record(path)
        if not relative.startswith("jre/java/"):
            return
        text = read_text(path)
        if relative in originals and originals[relative] != text:
            fail(f"Sibling J2CL baselines disagree: {relative}")
        originals[relative] = text
        original_records.setdefault(relative, []).append(item)

    nio_manifest_path = nio / "compatibility/j2cl-baseline.json"
    record(nio_manifest_path)
    nio_manifest = load_json(nio_manifest_path)
    if nio_manifest["version"] != VERSION or nio_manifest["archive_sha256"] != ARCHIVE_SHA256:
        fail("NIO J2CL baseline version or archive identity changed")
    for entry in nio_manifest["modified_upstream_sources"]:
        relative = "jre/java/" + entry["path"]
        baseline(nio / "third_party/j2cl-base" / relative, relative, entry["sha256"])

    concurrent_manifest_path = concurrent / "third_party/j2cl-base/provenance.json"
    record(concurrent_manifest_path)
    concurrent_manifest = load_json(concurrent_manifest_path)
    if concurrent_manifest["version"] != VERSION:
        fail("Concurrency J2CL baseline version changed")
    for relative, expected in concurrent_manifest["sha256"].items():
        baseline(concurrent / "third_party/j2cl-base" / relative, relative, expected)

    inventory_path = nio / "compatibility/j2cl-types.json"
    record(inventory_path)
    inventory = load_json(inventory_path)
    if inventory["version"] != VERSION:
        fail("J2CL source inventory version changed")
    upstream_paths = {"jre/java/" + name.replace(".", "/") + ".java"
                      for name in inventory["types"] if name != "module-info"}
    upstream_paths.add("jre/java/module-info.java")

    # Use current owner sources, and verify that NIO's reviewed dependency
    # snapshots still match them. Never add the same IO/URI types a second time.
    snapshots_path = nio / "compatibility/dependency-snapshots.json"
    record(snapshots_path)
    for entry in load_json(snapshots_path):
        owner = entry["owner"]
        if owner == "java-io-j2cl":
            canonical = io / "overlay/jre/java" / entry["path"]
        elif owner == "WebAWT":
            canonical = awt / "src/main/java" / entry["path"]
        else:
            fail(f"Unreviewed NIO dependency source owner: {owner}")
        snapshot = nio / "third_party" / owner / "overlay" / entry["path"]
        for path in [canonical, snapshot]:
            if digest(path.read_bytes()) != entry["sha256"]:
                fail(f"NIO dependency snapshot differs from canonical owner: {path}")
            record(path)

    sources: dict[str, dict] = {}
    modules: list[dict] = []
    overrides: list[dict] = []

    def add(owner: str, folder: Path, files: list[Path]) -> None:
        if not files:
            fail(f"No canonical sources found: {folder}")
        for path in sorted(files):
            relative = path.relative_to(folder).as_posix()
            item = {"owner": owner, **record(path), "text": read_text(path)}
            if relative == "module-info.java":
                modules.append(item)
                continue
            if relative in sources:
                previous = sources[relative]
                if not (relative in PREFERRED and previous["owner"] == "java-nio-j2cl"
                        and owner == "java-concurrent-j2cl"):
                    fail(f"Unreviewed duplicate JRE source: {relative}")
                overrides.append({"path": "jre/java/" + relative,
                                  "selected": {key: value for key, value in item.items() if key != "text"},
                                  "excluded": {key: value for key, value in previous.items() if key != "text"},
                                  "reason": "Use the operational main-thread concurrency implementation instead of NIO's signature-only declaration"})
            sources[relative] = item

    add("java-nio-j2cl", nio / "src/main/super", list((nio / "src/main/super").rglob("*.java")))
    add("java-io-j2cl", io / "overlay/jre/java", list((io / "overlay/jre/java").rglob("*.java")))
    add("java-awt-j2cl", awt / "src/main/java", [awt / "src/main/java/java/net" / name for name in WEB_NET])
    add("java-concurrent-j2cl", concurrent / "src/main/super", list((concurrent / "src/main/super").rglob("*.java")))
    if {entry["path"].removeprefix("jre/java/") for entry in overrides} != PREFERRED:
        fail("The reviewed NIO/concurrency source overlap changed")

    fragment = nio / "integrations/File.nio-members.java.fragment"
    fragment_record = record(fragment)
    file_source = sources["java/io/File.java"]
    if "java.nio.file.Path toPath(" in file_source["text"]:
        fail("Canonical IO File now defines toPath; review the integration fragment")
    offset = file_source["text"].rfind("}")
    if offset < 0:
        fail("Missing closing brace in java.io.File")
    file_source["text"] = file_source["text"][:offset] + read_text(fragment) + file_source["text"][offset:]
    file_source["integration"] = {"operation": "append File.toPath declaration", **fragment_record}

    base_module = originals["jre/java/module-info.java"]
    exports = module_exports(base_module)
    for item in modules:
        exports |= module_exports(item["text"])
    additional = sorted(exports - module_exports(base_module))
    offset = base_module.rfind("}")
    module_text = (base_module[:offset] + "\n  // Additional packages supplied by the composed browser JRE profiles.\n"
                   + "".join("  exports " + package + ";\n" for package in additional) + base_module[offset:])
    sources["module-info.java"] = {
        "owner": "mps-j2cl composition", "text": module_text,
        "sources": [{key: value for key, value in item.items() if key != "text"} for item in modules],
        "integration": {"operation": "union of upstream, NIO and concurrent java.base exports", "added_exports": additional},
    }

    chunks: list[str] = []
    ownership: list[dict] = []
    for relative, item in sorted(sources.items()):
        target = "jre/java/" + relative
        before = originals.get(target, "")
        if target in upstream_paths and target not in originals:
            fail(f"Missing pinned original for existing J2CL source: {target}")
        if upstream is not None:
            original = upstream / target
            actual = read_text(original) if original.exists() else ""
            if actual != before:
                fail(f"Complete upstream tree differs from the pinned preimage: {target}")
        after = item["text"]
        ownership.append({"path": target, "provider": "J2CL java.base",
                          **{key: value for key, value in item.items() if key != "text"},
                          "emitted_sha256": digest(after.encode("utf-8")),
                          "baseline": original_records.get(target, []),
                          "change": "unchanged" if before == after else "modified" if before else "added"})
        if before == after:
            continue
        chunks.append(f"diff --git a/{target} b/{target}\n")
        if not before:
            chunks.append("new file mode 100644\n")
        chunks.extend(difflib.unified_diff(before.splitlines(keepends=True), after.splitlines(keepends=True),
                                         fromfile="a/" + target if before else "/dev/null", tofile="b/" + target))

    patch = "".join(chunks)
    manifest = {
        "schema_version": 1,
        "j2cl": {"version": VERSION, "upstream_archive_sha256": ARCHIVE_SHA256,
                 "source": "https://bcr.bazel.build/modules/j2cl/20260402/source.json"},
        "patch": {"path": "third_party/j2cl/composed.patch", "sha256": digest(patch.encode("utf-8"))},
        "generator": {"path": "tools/compose_jre.py", "sha256": digest(Path(__file__).read_bytes())},
        "policy": ["Canonical sources remain owned by the sibling projects; no source trees are copied into mps or mps-web.",
                   "Every emitted source has one provider in J2CL java.base.",
                   "IO and WebAWT canonical sources match NIO's reviewed dependency snapshots.",
                   "Concurrency replaces NIO's signature-only Thread and ThreadFactory.",
                   "WebAWT's ordinary uri-j2cl target must contain no sources when used with this JRE.",
                   "Apply the separate type-support patch after this composition; it is intentionally not incorporated here."],
        "source_count": len(ownership), "inputs": [inputs[key] for key in sorted(inputs)],
        "overrides": overrides, "ownership": ownership,
    }
    return patch, json.dumps(manifest, indent=2, ensure_ascii=True) + "\n"


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--siblings", type=Path, default=ROOT.parent,
                        help="Parent containing the four canonical compatibility source trees")
    parser.add_argument("--upstream", type=Path, help="Complete clean J2CL 20260402 source tree for preimage and git apply validation")
    parser.add_argument("--check", action="store_true", help="Verify generated files without changing them")
    args = parser.parse_args()
    patch, manifest = compose(args.siblings.resolve(), args.upstream.resolve() if args.upstream else None)
    outputs = {OUTPUT / "composed.patch": patch, OUTPUT / "composition.json": manifest}
    if args.check:
        for path, expected in outputs.items():
            if not path.is_file() or path.read_bytes() != expected.encode("utf-8"):
                fail(f"Composition is stale: {path}; run python3 tools/compose_jre.py")
    else:
        OUTPUT.mkdir(parents=True, exist_ok=True)
        for path, content in outputs.items():
            path.write_text(content, encoding="utf-8", newline="\n")
    if args.upstream:
        # Preserve canonical source formatting, including a sibling's existing
        # blank line at EOF; whitespace policy is not a source-ownership check.
        subprocess.run(["git", "apply", "--check", "--whitespace=nowarn", str((OUTPUT / "composed.patch").resolve())],
                       cwd=args.upstream.resolve(), check=True)
    count = json.loads(manifest)["source_count"]
    print(f"{'Verified' if args.check else 'Generated'} composed.patch and composition.json: {count} unique source providers; pinned baselines and sibling snapshots match")


if __name__ == "__main__":
    main()
