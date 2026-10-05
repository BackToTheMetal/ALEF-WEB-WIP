#!/usr/bin/env python3
"""Replace the fixed module-reference regex with its equivalent scanner."""
import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PATH = 'core/smodel/source/jetbrains/mps/project/structure/modules/ModuleReference.java'

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    text = (ROOT / 'mps' / PATH).read_text(encoding='utf-8')
    text = text.replace('import java.util.regex.Matcher;\n', '').replace('import java.util.regex.Pattern;\n', '')
    lines = text.splitlines(keepends=True)
    text = ''.join(line for line in lines if 'private static final Pattern MODULE_REFERENCE' not in line)
    before = '    Matcher m = MODULE_REFERENCE.matcher(text);\n    if (m.matches()) {'
    after = '''    int opening = text.indexOf('(');
    boolean noLineBreaks = true;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '\\n' || c == '\\r' || c == '\\u0085' || c == '\\u2028' || c == '\\u2029') {
        noLineBreaks = false;
        break;
      }
    }
    if (noLineBreaks && opening >= 0 && text.endsWith(")")) {
      String idText = text.substring(0, opening);
      String nameText = text.substring(opening + 1, text.length() - 1);'''
    if text.count(before) != 1: raise SystemExit('ModuleReference parser preimage changed')
    text = text.replace(before, after).replace('m.group(1)', 'idText').replace('m.group(2)', 'nameText')
    output = ROOT / 'mps-web' / PATH
    if args.check:
      if output.read_text(encoding='utf-8') != text: raise SystemExit('Stale ModuleReference replacement')
    else:
      output.parent.mkdir(parents=True, exist_ok=True)
      output.write_text(text, encoding='utf-8', newline='\n')
    print('ModuleReference parser replacement verified')

if __name__ == '__main__':
    main()
