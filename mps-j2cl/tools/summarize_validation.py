#!/usr/bin/env python3
"""Write a human-readable report only from a complete, passing validation run."""
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
report = json.loads((ROOT / 'out/validation-summary.json').read_text())
if report['status'] != 'passed':
    raise SystemExit('A complete passing validation run is required before writing the report')
audit = json.loads((ROOT / 'out/source-audit.json').read_text())
lock = json.loads((ROOT / 'source-lock.json').read_text())
build_log = (ROOT / 'out/validation-logs/01-build.log').read_text()
targets = re.search(r'INFO: Found (\d+) targets', build_log)[1]
files = [(name, name + '-browser-results.json') for name in report['browser_modes']]
files += [('WeakSet GC', 'weakset-gc-results.json'), ('PNG decoding', 'icon-png-browser-results.json'),
          ('Foundation GC', 'foundation-gc-results.json'),
          ('Dynamic components', 'dynamic-components-results.json'),
          ('Live MPS editor', 'editor-live-browser-results.json')]
rows = []
total = 0
for name, filename in files:
    results = json.loads((ROOT / 'out' / filename).read_text())
    if not results or not all(result['passed'] for result in results):
        raise SystemExit('Non-passing browser artifact: ' + filename)
    for result in results:
        binary = ROOT / 'bazel-bin' / (result['build'] + '.js')
        if hashlib.sha256(binary.read_bytes()).hexdigest() != result['sha256']:
            raise SystemExit('Browser result is stale for ' + result['build'])
    total += len(results)
    rows.append(f'| {name} | {len(results)} | PASS |')
content = f'''# Validation results

Completed: {report['finished_at']}. All {len(report['steps'])} validation steps passed; no steps were skipped.

- Bazel `build //:all`: **{targets} targets passed**.
- MPS closure: **{len(audit['projects_checked'])} projects**, **{audit['java_sources']} Java files**.
- Sparse MPS replacements: **{audit['replacements']}**, with **zero source-audit errors**.
- Browser verification: **{total} passing browser/build runs**; recorded hashes match the current bundles.
- Source pin: `{lock['mps']['commit']}`; Bazel `{lock['bazel']}`, J2CL `{lock['j2cl']['version']}`.

The validation command checks source ownership, reproducible generated metadata and patches,
stock-JVM golden results, browser behavior and optimized output. Ordinary browser suites run
on Chromium, Firefox and WebKit with development and ADVANCED JavaScript. Class, runtime,
typechecking, platform and live editor fixtures also run with STRIPPED class metadata. Explicit garbage collection uses Chromium test
instrumentation; dynamic-component lifecycle behavior runs on all three engines.

| Suite | Browser/build runs | Result |
| --- | ---: | --- |
''' + '\n'.join(rows) + '''

The core smoke test exercises actual MPS nodes and models, containment, properties, references,
indexing, events, imports, detach/reattach, collections and tuples. The live editor fixture uses
a handwritten test language with real MPS model repositories, cell construction, incremental
typechecking, commands, intentions, completion and browser input. These tests establish the
documented browser profile, not a complete desktop MPS IDE port. Host files/JAR extraction,
dynamic JVM bytecode loading, unrestricted reflection, the full Java regex language and desktop
threading are outside that profile. See [the README](../README.md) for exact contracts.

Reproduce with `python3 tools/validate.py` in WSL (or `tools/validate.ps1` from PowerShell), then
`python3 tools/summarize_validation.py`. Detailed machine-readable results and per-step logs are
under `out/validation-summary.json` and `out/validation-logs/`. The summary refuses skipped,
failed or stale browser results.
'''
(ROOT / 'docs/validation.md').write_text(content, encoding='utf-8', newline='\n')
print(f'Validation report: {targets} targets; {total} passing browser/build runs')
