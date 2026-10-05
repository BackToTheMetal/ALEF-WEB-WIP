#!/usr/bin/env python3
"""Record which exact distributable passed the full validation entrypoint."""
from datetime import datetime, timezone
import hashlib, json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
archive = ROOT / 'release/walkingkooka-java-time-0.1.0.tar.gz'
browsers = json.loads((ROOT / 'out/browser-results.json').read_text())
consumer = json.loads((ROOT / 'out/consumer-results.json').read_text())
expected = {(engine, mode) for engine in ['chromium', 'firefox', 'webkit'] for mode in ['development', 'optimized']}
if {(r['engine'], r['mode']) for r in browsers} != expected:
    raise SystemExit('Full browser matrix has not passed')
if len(consumer) != 1 or consumer[0]['engine'] != 'chromium' or consumer[0]['mode'] != 'optimized':
    raise SystemExit('Packaged consumer has not passed')
report = {
    'validatedAt': datetime.now(timezone.utc).isoformat(),
    'archive': archive.name,
    'sha256': hashlib.sha256(archive.read_bytes()).hexdigest(),
    'jdkReference': (ROOT / 'out/jdk-reference.txt').read_text().splitlines()[0],
    'browsers': browsers, 'archiveConsumer': consumer,
}
(ROOT / 'release/validation.json').write_text(json.dumps(report, indent=2) + '\n')
print('java.time validation passed:', report['jdkReference'])
