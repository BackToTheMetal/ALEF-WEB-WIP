#!/usr/bin/env python3
"""Serve the compiled, handwritten-language MPS runtime demo on localhost."""
import argparse
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--port', type=int, default=8799)
    parser.add_argument('--development', action='store_true')
    args = parser.parse_args()
    if not 0 <= args.port <= 65535:
        parser.error('--port must be between 0 and 65535 (0 chooses an available port)')
    bundle = ROOT / 'bazel-bin' / ('editor_live_browser_dev.js' if args.development else 'editor_live_browser.js')
    if not bundle.is_file():
        parser.error('Build //:editor_live_browser' + ('_dev' if args.development else '') + ' first')
    assets = {'/': ('text/html; charset=utf-8', (ROOT / 'browser/editor-demo.html').read_bytes()),
              '/editor.js': ('application/javascript; charset=utf-8', bundle.read_bytes())}

    class Handler(BaseHTTPRequestHandler):
        def do_GET(self):
            asset = assets.get(self.path.split('?', 1)[0])
            if asset is None:
                self.send_error(404)
                return
            content_type, content = asset
            self.send_response(200)
            self.send_header('Content-Type', content_type)
            self.send_header('Cache-Control', 'no-store')
            self.send_header('Content-Length', str(len(content)))
            self.end_headers()
            self.wfile.write(content)

    try:
        server = ThreadingHTTPServer(('127.0.0.1', args.port), Handler)
    except OSError as error:
        parser.error(f'Cannot start the local demo server: {error}')
    print(f'MPS runtime demo: http://localhost:{server.server_port}/', flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()


if __name__ == '__main__':
    main()
