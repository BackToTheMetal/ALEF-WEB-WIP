#!/usr/bin/env python3
"""Compose an isolated J2CL patch from Metalava output and existing dependency providers."""
import difflib
import hashlib
import json
import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PARENT = ROOT.parent


def prepare(jdk):
    policy = json.loads((ROOT / "policy.json").read_text())
    raw = ROOT / "stubs/raw"
    adapted = ROOT / "stubs/j2cl"
    # This tree contains generated trial output only.
    if adapted.exists():
        if not adapted.resolve().is_relative_to(ROOT.resolve()):
            raise ValueError("Generated adaptation directory is outside the trial")
        shutil.rmtree(adapted)
    classes = ROOT / "build/tools"
    classes.mkdir(parents=True, exist_ok=True)
    subprocess.run([str(jdk / "bin/javac"), "-d", str(classes), str(ROOT / "scripts/AdaptStubs.java")], check=True)
    subprocess.run([str(jdk / "bin/java"), "-cp", str(classes), "AdaptStubs", str(raw), str(adapted),
                    *policy["excluded_j2cl_members"]], check=True)
    sources = {}
    reused = []
    for folder in [PARENT / "src/main/super", PARENT / "third_party/java-io-j2cl/overlay", PARENT / "third_party/WebAWT/overlay"]:
        for source in sorted(folder.rglob("*.java")):
            relative = source.relative_to(folder).as_posix()
            if relative.startswith("java/nio/") and relative[:-5].replace("/", ".") not in policy["preserve_existing_j2cl_types"]:
                continue
            if relative in sources:
                raise ValueError("Duplicate provider: " + relative)
            sources[relative] = source.read_text()
            reused.append({"source": source.relative_to(PARENT).as_posix(),
                           "sha256": hashlib.sha256(source.read_bytes()).hexdigest()})
    generated = []
    for source in sorted(adapted.rglob("*.java")):
        if source.name == "package-info.java":
            continue
        relative = source.relative_to(adapted).as_posix()
        name = relative[:-5].replace("/", ".")
        if name in policy["preserve_existing_j2cl_types"]:
            # Keep the integration directory honest: preserved types have no Metalava replacement here.
            source.unlink()
            continue
        if relative in sources:
            raise ValueError("Duplicate provider: " + relative)
        sources[relative] = source.read_text()
        generated.append(name)
    file_path = "java/io/File.java"
    if "java.nio.file.Path toPath(" not in sources[file_path]:
        pos = sources[file_path].rfind("}")
        sources[file_path] = sources[file_path][:pos] + (PARENT / "integrations/File.nio-members.java.fragment").read_text() + sources[file_path][pos:]
    chunks = []
    for name, content in sorted(sources.items()):
        relative = "jre/java/" + name
        original = PARENT / "third_party/j2cl-base" / relative
        before = original.read_text().splitlines(keepends=True) if original.exists() else []
        after = content.splitlines(keepends=True)
        if before == after:
            continue
        chunks.append(f"diff --git a/{relative} b/{relative}\n")
        if not original.exists():
            chunks.append("new file mode 100644\n")
        chunks.extend(difflib.unified_diff(before, after, fromfile="a/" + relative if original.exists() else "/dev/null", tofile="b/" + relative))
    patch = ROOT / "j2cl-workspace/patches/metalava-nio.patch"
    patch.parent.mkdir(parents=True, exist_ok=True)
    patch.write_text("".join(chunks), newline="\n")
    for name in [".bazelrc", ".bazelversion"]:
        shutil.copyfile(PARENT / name, ROOT / "j2cl-workspace" / name)
    report = {"generated_nio_types": generated, "generated_top_level_count": len(generated),
              "preserved": policy["preserve_existing_j2cl_types"],
              "excluded": policy["excluded_j2cl_members"], "reused_sources": reused,
              "patch_sha256": hashlib.sha256(patch.read_bytes()).hexdigest(),
              "stub_bodies": 'Unmodified Metalava RuntimeException("Stub!") bodies'}
    (ROOT / "reports/j2cl-adaptation.json").write_text(json.dumps(report, indent=2) + "\n")
    print(f"Prepared {len(generated)} Metalava NIO types for the isolated J2CL JRE")


if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jdk-home", type=Path, required=True)
    prepare(parser.parse_args().jdk_home)
