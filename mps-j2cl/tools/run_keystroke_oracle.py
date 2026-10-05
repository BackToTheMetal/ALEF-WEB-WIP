#!/usr/bin/env python3
"""Run the shared regression from its owning Swing library."""
from pathlib import Path
import subprocess
import sys

root = Path(__file__).resolve().parents[2] / 'javax-swing-j2cl'
subprocess.run([sys.executable, str(root / 'tools/run_keystroke_oracle.py'), *sys.argv[1:]], cwd=root, check=True)
