#!/usr/bin/env python3
"""Keep literal J2CL compile-time property lookups for optional MPS flags."""
import argparse
from pathlib import Path
import re
ROOT = Path(__file__).resolve().parents[1]
PATH = 'core/smodel/source/jetbrains/mps/RuntimeFlags.java'

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    text = (ROOT / 'mps' / PATH).read_text(encoding='utf-8')
    text, count = re.subn(r'Boolean.getBoolean\(("[^"]+")\)', r'Boolean.parseBoolean(System.getProperty(\1, "false"))', text)
    if count != 4: raise SystemExit('RuntimeFlags baseline changed')
    text = re.sub(r'System.getProperty\(("[^"]+")\)', r'System.getProperty(\1, null)', text)
    output = ROOT / 'mps-web' / PATH
    if args.check:
        if output.read_text(encoding='utf-8') != text: raise SystemExit('Stale RuntimeFlags overlay')
    else:
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(text, encoding='utf-8', newline='\n')
    print('RuntimeFlags: optional literal build properties retained')

if __name__ == '__main__': main()
