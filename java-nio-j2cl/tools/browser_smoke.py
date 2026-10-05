#!/usr/bin/env python3
"""Run an optimized artifact in an isolated headless Chromium process, with no test dependencies."""
import argparse
import json
import os
import re
import signal
import shutil
import subprocess
import tempfile
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]

def main():
    p = argparse.ArgumentParser()
    p.add_argument("javascript", type=Path)
    p.add_argument("--browser", type=Path, required=True)
    args = p.parse_args()
    output = ROOT / "out/browser"
    output.mkdir(parents=True, exist_ok=True)
    if args.javascript.resolve() != (output / "application.js").resolve():
        shutil.copyfile(args.javascript, output / "application.js")
    (output / "index.html").write_text('''<!doctype html><meta charset="utf-8"><title>NIO smoke test</title>
<pre id="result">WAITING</pre>
<script>window.onerror=function(message){document.getElementById('result').textContent='FAIL: '+message;};</script>
<script src="application.js"></script>
<script>document.getElementById('result').textContent=window.nioProbeResult||'FAIL: result missing';</script>
''')
    with tempfile.TemporaryDirectory(prefix="nio-chromium-") as profile:
        # Keep the browser page on the native filesystem (WSL /tmp, not the Windows mount).
        page = Path(profile) / 'page'
        page.mkdir()
        shutil.copyfile(output/'index.html', page/'index.html')
        shutil.copyfile(output/'application.js', page/'application.js')
        command = [str(args.browser), '--headless', '--no-sandbox', '--disable-dev-shm-usage', '--disable-gpu', '--no-first-run', '--disable-background-networking', '--disable-extensions', '--user-data-dir='+str(Path(profile)/'profile'), '--dump-dom', (page/'index.html').as_uri()]
        process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, start_new_session=os.name!='nt')
        timed_out = False
        try:
            stdout, stderr = process.communicate(timeout=45)
        except subprocess.TimeoutExpired:
            timed_out = True
            if os.name != 'nt': os.killpg(process.pid, signal.SIGKILL)
            else: process.kill()
            stdout, stderr = process.communicate(timeout=5)
    (output/'dom.html').write_text(stdout)
    (output/'browser.log').write_text(stderr)
    passed = not timed_out and process.returncode == 0 and '<pre id="result">PASS:' in stdout
    report = {"status":"PASS" if passed else "FAIL", "javascript":str(args.javascript), "browser":str(args.browser), "exit_code":process.returncode, "timed_out":timed_out}
    match = re.search(r'<pre id="result">([^<]+)</pre>', stdout)
    report['result'] = match[1] if match else None
    (ROOT/'compatibility/browser-check.json').write_text(json.dumps(report,indent=2)+'\n')
    print(json.dumps(report,indent=2))
    if not passed:
        print(stdout[-2000:]); print(stderr[-2000:]); raise SystemExit(1)

if __name__ == '__main__': main()
