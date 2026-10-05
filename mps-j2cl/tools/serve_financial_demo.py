#!/usr/bin/env python3
"""Serve a snapshot of the generated-language financial editor on localhost."""
import argparse
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--port', type=int, default=8800)
parser.add_argument('--optimized', action='store_true')
args = parser.parse_args()
bundle = ROOT / 'bazel-bin' / ('financial_browser.js' if args.optimized else 'financial_browser_dev.js')
if not bundle.is_file():
    parser.error('Build //:financial_browser' + ('' if args.optimized else '_dev') + ' first')
assets = {'/': ('text/html; charset=utf-8', (ROOT / 'browser/financial/index.html').read_bytes()),
          '/financial.js': ('application/javascript; charset=utf-8', bundle.read_bytes())}

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        asset = assets.get(self.path.split('?', 1)[0])
        if asset is None:
            self.send_error(404)
            return
        content_type, content = asset
        self.send_response(200)
        self.send_header('Content-Type', content_type)
        self.send_header('Content-Length', str(len(content)))
        self.send_header('Cache-Control', 'no-store')
        self.end_headers()
        self.wfile.write(content)

server = ThreadingHTTPServer(('127.0.0.1', args.port), Handler)
print(f'Financial calculations: http://localhost:{server.server_port}/', flush=True)
try:
    server.serve_forever()
except KeyboardInterrupt:
    pass
finally:
    server.server_close()
