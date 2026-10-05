#!/usr/bin/env python3
"""Expose the compiled language regression result for manual browser verification."""
import argparse
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
import shutil

ROOT = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--port', type=int, default=8813)
parser.add_argument('--optimized', action='store_true')
args = parser.parse_args()
bundle = ROOT / 'bazel-bin' / ('language_contracts_browser' + ('' if args.optimized else '_dev') + '.js')
html = b'''<!doctype html><meta charset="utf-8"><title>ALEF language regression contracts</title>
<h1>ALEF language regression contracts</h1><pre id="result">Running...</pre>
<script src="/contracts.js"></script><script>
const timer = setInterval(() => {
  const result = window.languageResult?.();
  if (result) { document.getElementById('result').textContent = result; clearInterval(timer); }
}, 100);
</script>'''


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            self.send_response(200)
            self.send_header('Content-Type', 'text/html; charset=utf-8')
            self.end_headers()
            self.wfile.write(html)
        elif self.path == '/contracts.js':
            self.send_response(200)
            self.send_header('Content-Type', 'application/javascript')
            self.send_header('Cache-Control', 'no-store')
            self.end_headers()
            with bundle.open('rb') as source:
                shutil.copyfileobj(source, self.wfile)
        else:
            self.send_error(404)


print(f'Language contracts: http://localhost:{args.port}/', flush=True)
ThreadingHTTPServer(('127.0.0.1', args.port), Handler).serve_forever()
