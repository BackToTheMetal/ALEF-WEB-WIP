#!/usr/bin/env python3
"""Execute the optimized J2CL contracts in Chromium, including asynchronous results."""

import argparse
import hashlib
import html
import json
import os
import re
import shutil
import signal
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def find_browser():
    cache = Path.home() / ".cache/ms-playwright"
    candidates = sorted(cache.glob("chromium_headless_shell-*/chrome-headless-shell-linux*/chrome-headless-shell"))
    candidates += sorted(cache.glob("chromium-*/chrome-linux*/chrome"))
    for executable in candidates:
        if executable.is_file() and os.access(executable, os.X_OK):
            return executable
    for name in ["chromium", "chromium-browser", "google-chrome"]:
        executable = shutil.which(name)
        if executable:
            return Path(executable)
    raise SystemExit("No Chromium found. Pass --browser /path/to/chromium or install Chromium.")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("javascript", type=Path)
    parser.add_argument("--browser", type=Path)
    parser.add_argument("--output", type=Path, default=ROOT / "out/browser")
    args = parser.parse_args()
    browser = args.browser or find_browser()
    if not args.javascript.is_file():
        raise SystemExit("Built JavaScript does not exist: " + str(args.javascript))
    output = args.output
    output.mkdir(parents=True, exist_ok=True)
    destination = output / "application.js"
    if args.javascript.resolve() != destination.resolve():
        shutil.copyfile(args.javascript, destination)
    (output / "index.html").write_text('''<!doctype html>
<meta charset="utf-8"><title>Java concurrent J2CL contracts</title>
<pre id="result">WAITING</pre>
<script>
window.onerror = function(message) {
  window.concurrentProbeError = window.concurrentProbeError || 'FAIL: ' + message;
};
window.onunhandledrejection = function(event) {
  window.concurrentProbeError = window.concurrentProbeError || 'FAIL: ' + event.reason;
};
</script>
<script src="application.js"></script>
<script>
(function collect() {
  var result = window.concurrentProbeError || window.concurrentProbeResult;
  if (result) document.getElementById('result').textContent = result;
  setTimeout(collect, 10);
})();
</script>
''', encoding="utf-8", newline="\n")
    with tempfile.TemporaryDirectory(prefix="concurrent-j2cl-browser-") as temporary:
        # WSL browser profiles and HTML live on Linux's native filesystem.
        page = Path(temporary) / "page"
        page.mkdir()
        shutil.copyfile(output / "index.html", page / "index.html")
        shutil.copyfile(destination, page / "application.js")
        command = [
            str(browser), "--headless", "--no-sandbox", "--disable-dev-shm-usage",
            "--disable-gpu", "--no-first-run", "--disable-background-networking",
            "--disable-extensions", "--user-data-dir=" + str(Path(temporary) / "profile"),
            "--virtual-time-budget=3000", "--dump-dom", (page / "index.html").as_uri(),
        ]
        process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                   text=True, start_new_session=os.name != "nt")
        timed_out = False
        try:
            stdout, stderr = process.communicate(timeout=45)
        except subprocess.TimeoutExpired:
            timed_out = True
            if os.name != "nt":
                os.killpg(process.pid, signal.SIGKILL)
            else:
                process.kill()
            stdout, stderr = process.communicate(timeout=5)
    (output / "dom.html").write_text(stdout, encoding="utf-8")
    (output / "browser.log").write_text(stderr, encoding="utf-8")
    match = re.search(r'<pre id="result">([^<]+)</pre>', stdout)
    result = html.unescape(match[1]) if match else None
    passed = not timed_out and process.returncode == 0 and result is not None and result.startswith("PASS:")
    report = {
        "status": "PASS" if passed else "FAIL",
        "result": result,
        "javascript": str(args.javascript),
        "javascript_sha256": hashlib.sha256(args.javascript.read_bytes()).hexdigest(),
        "browser": str(browser),
        "exit_code": process.returncode,
        "timed_out": timed_out,
    }
    (output / "report.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2))
    if not passed:
        print(stdout[-2000:])
        print(stderr[-2000:])
        raise SystemExit(1)


if __name__ == "__main__":
    main()
