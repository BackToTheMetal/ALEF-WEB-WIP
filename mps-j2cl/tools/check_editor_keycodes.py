#!/usr/bin/env python3
"""Verify every generated browser key name/value against the selected JVM AWT API."""
import argparse
import json
import re
import subprocess
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--java', default='java')
    parser.add_argument('--javac', default='javac')
    args = parser.parse_args()
    with tempfile.TemporaryDirectory(prefix='mps-keycode-oracle-') as temporary:
        source = Path(temporary) / 'KeyCodeOracle.java'
        source.write_text('''import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.TreeMap;
public class KeyCodeOracle {
  public static void main(String[] args) throws Exception {
    TreeMap<String, Integer> values = new TreeMap<>();
    for (Field field : KeyEvent.class.getDeclaredFields()) {
      if (field.getName().startsWith("VK_")) values.put(field.getName(), field.getInt(null));
    }
    values.forEach((name, value) -> System.out.println(name + "=" + value));
  }
}
''', encoding='utf-8')
        subprocess.run([args.javac, '-d', temporary, str(source)], check=True)
        expected = subprocess.check_output([args.java, '-Djava.awt.headless=true', '-cp', temporary,
                                             'KeyCodeOracle'], encoding='utf-8')
    values = {name: int(value) for name, value in (line.split('=') for line in expected.splitlines())}
    awt_source = (ROOT.parent / 'java-awt-j2cl/src/main/java/java/awt/event/KeyEvent.java').read_text(encoding='utf-8')
    actual = {name: int(value, 0) for name, value in re.findall(r'public static final int (VK_\w+)\s*=\s*(\w+);', awt_source)}
    if values != actual:
        raise SystemExit('Sibling AWT key constants differ from JVM: ' + str({name: (value, actual.get(name))
                         for name, value in values.items() if actual.get(name) != value}))
    overlay = (ROOT / 'mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/keymaps/AWTKeymapHandler.java').read_text(encoding='utf-8')
    listed = re.findall(r'registerKey\(KeyEvent\.(VK_\w+), "(VK_\w+)"\);', overlay)
    expected_names = set(values) - {'VK_CONTROL', 'VK_ALT', 'VK_SHIFT'}
    if set(name for name, text in listed if name == text) != expected_names or len(listed) != len(expected_names):
        raise SystemExit('Missing, duplicated or mismatched key name in browser overlay')
    (ROOT / 'out').mkdir(exist_ok=True)
    (ROOT / 'out/editor-keycodes-oracle.json').write_text(json.dumps({
        'jvm_constants': len(values), 'mapped_names': len(listed), 'aliases': {'VK_SEPARATOR': values['VK_SEPARATOR'],
        'VK_SEPARATER': values['VK_SEPARATER']}, 'status': 'passed'}, indent=2) + '\n', encoding='utf-8')
    print(f'AWT key table: all {len(values)} JVM constants match; {len(listed)} mapped names preserve aliases')


if __name__ == '__main__':
    main()
