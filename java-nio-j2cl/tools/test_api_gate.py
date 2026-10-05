#!/usr/bin/env python3
"""Negative controls: an incomplete emitted API must never pass the compatibility gate."""
import json
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def main():
    original=Path(sys.argv[1])
    with tempfile.TemporaryDirectory(prefix='nio-gate-') as temporary:
        folder=Path(temporary); broken=folder/'broken.jar'
        with zipfile.ZipFile(original) as source,zipfile.ZipFile(broken,'w') as target:
            for entry in source.infolist():
                if entry.filename!='java/nio/ByteOrder.class': target.writestr(entry,source.read(entry))
        report=folder/'missing-type.json'
        result=subprocess.run([sys.executable,str(ROOT/'tools/check_api.py'),str(broken),'--report',str(report)],capture_output=True,text=True)
        data=json.loads(report.read_text())
        assert result.returncode!=0 and any(e['type']=='java.nio.ByteOrder' and e['missing']=='class' for e in data['missing'])
        reference=json.loads((ROOT/'compatibility/jdk-nio-api.json').read_text())
        item=next(c for c in reference['classes'] if c['name']=='java.nio.file.Files')
        item['methods'].append({'name':'missingControl','declaration':'public static void missingControl()','abstract':False,'value':None})
        changed=folder/'reference.json'; changed.write_text(json.dumps(reference))
        report=folder/'missing-member.json'
        result=subprocess.run([sys.executable,str(ROOT/'tools/check_api.py'),str(original),'--reference',str(changed),'--report',str(report)],capture_output=True,text=True)
        data=json.loads(report.read_text())
        assert result.returncode!=0 and any('missingControl' in e['missing'] for e in data['missing'])
    print('PASS: API gate rejects missing emitted types and members')
if __name__=='__main__': main()
