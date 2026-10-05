#!/usr/bin/env python3
"""Emit regex literals supported by both Java and J2CL's JavaScript regex engine."""
import argparse
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
PATH = 'core/kernel/source/jetbrains/mps/util/PatternUtil.java'

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    text = (ROOT / 'mps' / PATH).read_text(encoding='utf-8')
    text = text.replace('    if (state == State.QUOTING) {\n      b.append("\\\\E");\n    }\n', '')
    start = text.index("    if (c == '*' || c == '?' || c == '.' || c == '@' || Character.isLetterOrDigit(c) || c == '_') {")
    end = text.index("    if (c == '*') {", start)
    text = text[:start] + text[end:]
    old = '      b.append(c);\n      return State.QUOTING;'
    new = r'''      // Escape literal metacharacters; JavaScript does not support Java's quote blocks.
      if ("\\^$.*+?()[]{}|".indexOf(c) >= 0) {
        b.append('\\');
      }
      b.append(c);
      return State.QUOTING;'''
    if text.count(old) != 1: raise SystemExit('PatternUtil baseline changed')
    text = text.replace(old, new)
    # Java's default regex whitespace class is ASCII; JS's is Unicode.
    text = text.replace(r'((\\s)+[', r'(([ \\t\\n\\x0B\\f\\r])+[')
    output = ROOT / 'mps-web' / PATH
    if args.check:
        if output.read_text(encoding='utf-8') != text: raise SystemExit('Stale PatternUtil')
    else:
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(text, encoding='utf-8', newline='\n')
    print('PatternUtil: portable literal escaping and ASCII whitespace')

if __name__ == '__main__': main()
