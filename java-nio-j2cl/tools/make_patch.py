#!/usr/bin/env python3
"""Create a deterministic J2CL JRE overlay patch; never modify the upstream tree."""
import argparse
import difflib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--upstream", type=Path, default=ROOT / "third_party/j2cl-base")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    chunks = []
    sources = {}
    for folder in [ROOT / "src/main/super", ROOT / "third_party/java-io-j2cl/overlay", ROOT / "third_party/WebAWT/overlay"]:
        for source in sorted(folder.rglob("*.java")):
            relative = "jre/java/" + source.relative_to(folder).as_posix()
            if relative in sources: raise SystemExit("Duplicate type ownership: " + relative)
            sources[relative] = source
    for relative, source in sorted(sources.items()):
        original = args.upstream / relative
        before = original.read_text().splitlines(keepends=True) if original.exists() else []
        updated = source.read_text()
        if relative == "jre/java/java/io/File.java" and "java.nio.file.Path toPath(" not in updated:
            offset = updated.rfind('}')
            updated = updated[:offset] + (ROOT / "integrations/File.nio-members.java.fragment").read_text() + updated[offset:]
        after = updated.splitlines(keepends=True)
        chunks.append(f"diff --git a/{relative} b/{relative}\n")
        if not original.exists():
            chunks.append("new file mode 100644\n")
        chunks.extend(difflib.unified_diff(before, after, fromfile="a/" + relative if original.exists() else "/dev/null", tofile="b/" + relative))
    result = "".join(chunks)
    output = ROOT / "patches/j2cl-nio.patch"
    if args.check:
        if not output.exists() or output.read_text() != result: raise SystemExit("Overlay patch is stale; run tools/make_patch.py")
        print("Overlay patch matches canonical sources and pinned dependency snapshots")
    else:
        output.write_text(result, newline="\n")
        print("Generated patches/j2cl-nio.patch")

if __name__ == "__main__":
    main()
