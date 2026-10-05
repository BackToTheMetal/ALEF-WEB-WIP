#!/usr/bin/env python3
"""Refresh the external dependency graph and all generated browser adaptations."""
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]


def run(script):
    subprocess.run([sys.executable, str(ROOT / 'tools' / script)], cwd=ROOT, check=True)


def main():
    # Discover reflective translator descendants before generating direct bindings.
    run('inventory.py')
    run('adapt_tables.py')
    run('adapt_helpers.py')
    run('adapt_service_test.py')
    run('adapt_grammar_menus.py')
    run('editor_metadata.py')
    run('adapt_translators.py')
    run('inventory.py')
    run('inventory_extensions.py')
    run('adapt_mps_languages.py')
    run('inventory_mps.py')
    params = ROOT / 'bazel-out/k8-fastbuild/bin/alef_languages.js-0.params'
    if params.exists():
        run('class_metadata.py')
    elif not (ROOT / 'browser-src/alefj2cl/ClassMetadata.java').exists():
        raise RuntimeError('The committed ClassMetadata.java bootstrap inventory is missing')
    run('register_languages.py')
    run('builtin_models.py')
    run('prepare.py')
    run('check_sources.py')


if __name__ == '__main__': main()
