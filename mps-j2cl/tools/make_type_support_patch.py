#!/usr/bin/env python3
"""Delegate to the AWT-owned compiler/runtime support generator."""
from pathlib import Path
import runpy

if __name__ == '__main__':
    import sys
    if '--output' not in sys.argv:
        sys.argv += ['--output', 'third_party/j2cl/type-support/class-type-support.patch']
    runpy.run_path(str(Path(__file__).resolve().parents[2] / 'java-awt-j2cl/tools/compatibility/make_type_support_patch.py'), run_name='__main__')
