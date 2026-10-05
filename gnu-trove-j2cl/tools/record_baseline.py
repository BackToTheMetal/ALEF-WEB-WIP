#!/usr/bin/env python3
"""Record vendored artifact provenance and the explicit MPS fixture boundary."""
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    revision = (ROOT / 'third_party/mps-revision.txt').read_text().strip()
    artifacts = []
    for filename in ['trove4j-1.0.20200330.jar', 'trove4j-1.0.20200330-sources.jar']:
        path = ROOT / 'third_party' / filename
        artifacts.append({'file': 'third_party/' + filename, 'sha256': sha(path),
                          'url': 'https://repo.maven.apache.org/maven2/org/jetbrains/intellij/deps/trove4j/1.0.20200330/' + filename})
    baseline = {'schemaVersion': 1, 'trove': 'org.jetbrains.intellij.deps:trove4j:1.0.20200330',
                'artifacts': artifacts, 'j2cl': '20260402', 'bazel': '8.3.1', 'jdk': 21,
                'playwright': '1.62.0', 'mps': {'repository': 'https://github.com/JetBrains/MPS', 'revision': revision,
                'scope': 'UniversalOptimizedNodeIdMap, StringBasedNodeIdMap, INodeIdToNodeMap, ObjectIdentityHashingStrategy',
                'platformArtifactStatus': 'Selected JetBrains compatibility baseline; MPS platform lib/trove.jar is not stored in this source checkout and its binary identity is not asserted.',
                'additionalDependency': {'path': 'languages/baseLanguage/collections/languages/trove/runtime/lib/trove-2.1.0.jar',
                 'status': 'Separate incompatible baseline, not supported by this artifact',
                 'sha256': sha(ROOT / 'third_party/mps-language-trove-2.1.0.jar')}},
                'productionDependencies': ['J2CL emulated JRE'], 'fixtureFiles': []}
    usage = []
    for path in sorted((ROOT / 'tests/mps/source').rglob('*.java')):
        relative = path.relative_to(ROOT / 'tests/mps/source').as_posix()
        source_path = ('core/util/source/' if '/util/' in relative else 'core/smodel/source/') + relative
        baseline['fixtureFiles'].append({'file': path.relative_to(ROOT).as_posix(), 'sha256': sha(path),
                                        'url': f'https://raw.githubusercontent.com/JetBrains/MPS/{revision}/{source_path}'})
        for line, text in enumerate(path.read_text().splitlines(), 1):
            if re.search(r'gnu\.trove\.|my(?:RegularMap|StringBasedIdMap|StringBasedIds|OtherMap)\.', text):
                usage.append({'file': path.relative_to(ROOT).as_posix(), 'line': line, 'source': text.strip(),
                              'validation': 'Compiled and executed by shared MpsScenarios against both implementations'})
    (ROOT / 'compatibility/baseline.json').write_text(json.dumps(baseline, indent=2) + '\n')
    (ROOT / 'compatibility/mps-usage.json').write_text(json.dumps({'scope': baseline['mps']['scope'],
        'completeMpsInventory': False, 'testBoundary': 'Test-only model tokens; actual map and hashing strategy sources are unchanged',
        'references': usage}, indent=2) + '\n')


if __name__ == '__main__': main()
