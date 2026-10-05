#!/usr/bin/env python3
"""Snapshot unchanged Java fixtures only; no edits to MPS sources or models."""
import argparse
import hashlib
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
FILES = {
"languages/baseLanguage/baseLanguage/sandbox/source_gen/jetbrains/mps/baseLanguage/sandbox/internal/InternalAnonymousClassUsage.java":"supported/InternalAnonymousClassUsage.java",
"core/make-runtime/source/jetbrains/mps/make/FileWithPosition.java":"blockers/FileWithPosition.java",
"core/openapi/source/org/jetbrains/mps/annotations/Immutable.java":"blockers/Immutable.java",
}
def main():
    p=argparse.ArgumentParser(); p.add_argument("source",type=Path); args=p.parse_args()
    items=[]
    for relative, target in FILES.items():
        content=(args.source/relative).read_bytes()
        destination=ROOT/'src/test/mps'/target
        destination.parent.mkdir(parents=True,exist_ok=True); destination.write_bytes(content)
        items.append({"source":relative,"fixture":target,"sha256":hashlib.sha256(content).hexdigest()})
    revision=json.loads((ROOT/'compatibility/mps-nio-usage.json').read_text())['revision']
    (ROOT/'compatibility/mps-fixtures.json').write_text(json.dumps({"revision":revision,"files":items},indent=2)+'\n')
    print("Snapshotted",len(items),"unchanged Java source files")
if __name__=='__main__': main()
