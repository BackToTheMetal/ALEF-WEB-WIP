"""Fetch pinned inputs without generating or modifying anything in mps-extensions/."""
from pathlib import Path
import hashlib
import json
import subprocess
import sys
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
lock = json.loads((ROOT / 'source-lock.json').read_text())
ext = lock['extensions']
checkout = ROOT / 'mps-extensions'
if not checkout.exists():
    subprocess.run(['git', 'init', str(checkout)], check=True)
    subprocess.run(['git', '-C', str(checkout), 'remote', 'add', 'origin', ext['url']], check=True)
    subprocess.run(['git', '-C', str(checkout), 'fetch', '--depth', '1', 'origin', ext['commit']], check=True)
    subprocess.run(['git', '-C', str(checkout), 'checkout', '--detach', ext['commit']], check=True)
head = subprocess.check_output(['git', '-C', str(checkout), 'rev-parse', 'HEAD'], text=True).strip()
assert head == ext['commit'], 'Existing checkout differs from source-lock.json; refusing to reset it'
assert not subprocess.check_output(['git', '-C', str(checkout), 'status', '--porcelain'], text=True), 'Upstream checkout is modified'
archive = ROOT / '.work/extensions.zip'
archive.parent.mkdir(parents=True, exist_ok=True)
if not archive.exists():
    urllib.request.urlretrieve(ext['source_archive_url'], archive)
assert hashlib.sha256(archive.read_bytes()).hexdigest() == ext['source_archive_sha256'], 'Archive hash mismatch'
for script in ('extract_sources.py', 'make_overlays.py', 'prepare.py', 'inventory.py', 'check_sources.py'):
    subprocess.run([sys.executable, str(ROOT / 'tools' / script)], check=True)
