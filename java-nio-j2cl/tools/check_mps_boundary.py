#!/usr/bin/env python3
"""Capture the remaining non-NIO blocker in an unchanged production MPS source fixture."""
import json
import re
import subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def main():
    result=subprocess.run(['bazelisk','build','//src/test/mps/blockers:mps-file-position'],cwd=ROOT,capture_output=True,text=True)
    output=result.stdout+result.stderr
    (ROOT/'out').mkdir(exist_ok=True)
    (ROOT/'out/mps-file-position.log').write_text(output)
    methods=re.findall(r'symbol:\s+method ([^\n]+)',output)
    error_count=len(re.findall(r'(?:^Error:|: error:)',output,re.MULTILINE))
    known=result.returncode!=0 and 'FileWithPosition.java' in output and bool(methods) and all(m.split('(')[0] in {'format','since','forRemoval'} for m in methods) and error_count==len(methods)
    report={"fixture":"FileWithPosition.java (unchanged)","status":"PASS" if result.returncode==0 else "EXPECTED_EXTERNAL_BLOCKER" if known else "UNEXPECTED_FAILURE","nio_signature_errors":False if known or result.returncode==0 else "unknown","missing_external_methods":sorted(set(methods)),"remaining_blockers":["J2CL String.format is unavailable", "J2CL Deprecated lacks since and forRemoval attributes"] if known else [],"log":"out/mps-file-position.log"}
    (ROOT/'compatibility/mps-boundary.json').write_text(json.dumps(report,indent=2)+'\n')
    print(json.dumps(report,indent=2))
    if result.returncode and not known:
        print(output[-5000:]); raise SystemExit(1)
if __name__=='__main__': main()
