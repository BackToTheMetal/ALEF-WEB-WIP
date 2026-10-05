#!/usr/bin/env python3
"""Read Java/Kotlin sources and class-file references; never read serialized MPS models."""
import argparse
import collections
import hashlib
import json
import re
import subprocess
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def main():
    p = argparse.ArgumentParser()
    p.add_argument("source", type=Path)
    p.add_argument("--git", default="git")
    args = p.parse_args()
    revision = subprocess.check_output([args.git, "-C", str(args.source), "rev-parse", "HEAD"], text=True).strip()
    tracked = subprocess.check_output([args.git, "-C", str(args.source), "ls-files", "-z", "--", "*.java", "*.kt", "*.jar"], text=True).split('\0')
    references, binaries, counts = [], [], collections.Counter()
    pattern = re.compile(r"\bjava\.nio(?:\.[A-Za-z_$][\w$]*)+")
    sources = 0
    for relative in tracked:
        if not relative: continue
        path = args.source / relative
        if not path.is_file(): continue
        if path.suffix == '.jar':
            try:
                with zipfile.ZipFile(path) as jar:
                    classes = {}
                    for entry in jar.infolist():
                        if entry.filename.endswith('.class'):
                            hits = sorted({s.decode('ascii').replace('/', '.') for s in re.findall(rb"java/nio/(?:[A-Za-z_$][\w$]*/)*[A-Za-z_$][\w$]*", jar.read(entry))})
                            if hits: classes[entry.filename] = hits
                    binaries.append({"file":relative,"sha256":hashlib.sha256(path.read_bytes()).hexdigest(),"classes":classes})
            except zipfile.BadZipFile:
                binaries.append({"file":relative,"error":"invalid archive"})
            continue
        sources += 1
        lines = []
        for number, line in enumerate(path.read_text(encoding='utf-8', errors='replace').splitlines(), 1):
            hits = sorted(set(pattern.findall(line)))
            if hits:
                lines.append({"line":number,"references":hits,"source":line.strip()})
                counts.update(hits)
        if lines: references.append({"file":relative,"sha256":hashlib.sha256(path.read_bytes()).hexdigest(),"lines":lines})
    result = {"revision":revision,"source_root":str(args.source),"method":"Tracked Java/Kotlin qualified-reference scan and tracked JAR class-byte reference scan; not full symbol resolution", "sources_scanned":sources,"reference_counts":dict(counts.most_common()),"source_references":references,"tracked_jars":binaries,"limitations":["Selected browser MPS modules have not been defined", "Untracked/downloaded dependencies are not a complete resolved classpath", "Import and qualified-reference matches do not establish member-level coverage", "Serialized MPS models were not read"]}
    (ROOT / "compatibility/mps-nio-usage.json").write_text(json.dumps(result, indent=2)+"\n")
    print(f"MPS {revision}: {sources} sources; {len(references)} with NIO references; {len(binaries)} tracked JARs")

if __name__ == '__main__': main()
