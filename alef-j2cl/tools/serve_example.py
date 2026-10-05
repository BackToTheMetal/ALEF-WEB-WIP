#!/usr/bin/env python3
"""Serve the ALEF editor example on localhost, without exposing workspace files."""
import argparse
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
import shutil

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--port', type=int, default=8810)
    parser.add_argument('--optimized', action='store_true', help='Serve the optimized Closure build')
    args = parser.parse_args()
    target = 'editor_example_browser' + ('' if args.optimized else '_dev')
    bundle = ROOT / 'bazel-bin' / (target + '.js')
    if not bundle.is_file():
        parser.error('Build //:' + target + ' first')
    vue = ROOT / 'node_modules/vue/dist/vue.runtime.esm-browser.prod.js'
    if not vue.is_file():
        parser.error('Run npm ci --ignore-scripts first to install the project-view runtime')
    # Open the current artifact on each request, so a rebuild needs only a page reload.
    routes = {
        '/': ('text/html; charset=utf-8', ROOT / 'example/index.html'),
        '/interface.css': ('text/css; charset=utf-8', ROOT / 'example/interface.css'),
        '/interface.js': ('application/javascript; charset=utf-8', ROOT / 'example/interface.js'),
        '/interface-ui.js': ('application/javascript; charset=utf-8', ROOT / 'example/interface-ui.js'),
        '/package-checks.js': ('application/javascript; charset=utf-8', ROOT / 'example/package-checks.js'),
        '/model-diff.js': ('application/javascript; charset=utf-8', ROOT / 'example/model-diff.js'),
        '/mps-diff.js': ('application/javascript; charset=utf-8', ROOT / 'example/mps-diff.js'),
        '/style.css': ('text/css; charset=utf-8', ROOT / 'example/style.css'),
        '/page.js': ('application/javascript; charset=utf-8', ROOT / 'example/page.js'),
        '/backend.js': ('application/javascript; charset=utf-8', ROOT / 'example/backend.js'),
        '/saved-workspace.js': ('application/javascript; charset=utf-8', ROOT / 'example/saved-workspace.js'),
        '/backend-workflows.js': ('application/javascript; charset=utf-8', ROOT / 'example/backend-workflows.js'),
        '/backend-mode.js': ('application/javascript; charset=utf-8', ROOT / 'example/backend-mode.js'),
        '/project-view.js': ('application/javascript; charset=utf-8', ROOT / 'example/project-view.js'),
        '/project-tree.js': ('application/javascript; charset=utf-8', ROOT / 'example/project-tree.js'),
        '/vendor/vue.runtime.esm-browser.prod.js': ('application/javascript; charset=utf-8', vue),
        '/app.js': ('application/javascript; charset=utf-8', bundle),
        '/favicon.ico': ('image/x-icon', None),
    }

    class Handler(BaseHTTPRequestHandler):
        def do_GET(self):
            route = routes.get(self.path.split('?', 1)[0])
            if route is None:
                self.send_error(404)
                return
            content_type, path = route
            if path is None:
                self.send_response(204)
                self.end_headers()
                return
            with path.open('rb') as content:
                self.send_response(200)
                self.send_header('Content-Type', content_type)
                self.send_header('Content-Length', str(path.stat().st_size))
                self.send_header('Cache-Control', 'no-store')
                self.end_headers()
                try:
                    shutil.copyfileobj(content, self.wfile)
                except (BrokenPipeError, ConnectionResetError):
                    pass  # Page reload while the development bundle is downloading.

    server = ThreadingHTTPServer(('127.0.0.1', args.port), Handler)
    print(f'ALEF editor example ({target}): http://localhost:{server.server_port}/', flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()


if __name__ == '__main__': main()
