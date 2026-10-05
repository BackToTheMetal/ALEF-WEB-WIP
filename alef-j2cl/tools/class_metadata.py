#!/usr/bin/env python3
"""Record abstract modifiers from compiler headers and selected source declarations."""
import ast
import json
from pathlib import Path
import re
import struct
import zipfile
from inventory import code_only
from adapt_translators import end_at

ROOT = Path(__file__).resolve().parents[1]


def class_info(data):
    count = struct.unpack_from('>H', data, 8)[0]
    offset, item, strings = 10, 1, {}
    while item < count:
        tag = data[offset]
        offset += 1
        if tag == 1:
            size = struct.unpack_from('>H', data, offset)[0]
            strings[item] = data[offset + 2:offset + 2 + size].decode('utf-8', errors='replace')
            offset += 2 + size
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18): offset += 4
        elif tag in (5, 6): offset += 8; item += 1
        elif tag in (7, 8, 16, 19, 20): offset += 2
        elif tag == 15: offset += 3
        else: raise ValueError('Unknown classfile constant: ' + str(tag))
        item += 1
    flags = struct.unpack_from('>H', data, offset)[0]
    offset += 6
    interfaces = struct.unpack_from('>H', data, offset)[0]
    offset += 2 + 2 * interfaces
    methods = set()
    for kind in ('fields', 'methods'):
        count = struct.unpack_from('>H', data, offset)[0]
        offset += 2
        for _ in range(count):
            _, name, _, attributes = struct.unpack_from('>4H', data, offset)
            offset += 8
            if kind == 'methods': methods.add(strings[name])
            for _ in range(attributes):
                size = struct.unpack_from('>I', data, offset + 2)[0]
                offset += 6 + size
    return flags, methods


def main():
    abstracts, layouts = set(), set()
    params = ROOT / 'bazel-out/k8-fastbuild/bin/alef_languages.js-0.params'
    classpath = params.read_text().splitlines()[1]
    for jar in classpath.split(':'):
        if not (ROOT / jar).is_file(): continue
        with zipfile.ZipFile(ROOT / jar) as archive:
            for entry in archive.namelist():
                if entry.endswith('.class'):
                    flags, methods = class_info(archive.read(entry))
                    name = entry[:-6].replace('/', '.')
                    if flags & 0x400: abstracts.add(name)
                    if 'relayoutImpl' in methods: layouts.add(name)
    sources = json.loads((ROOT / 'out/source-inventory.json').read_text())['sources']
    sources += json.loads((ROOT / 'out/mps-inventory.json').read_text())['sources']
    sources += json.loads((ROOT / 'out/extension-inventory.json').read_text())['sources']
    for source in sources:
        text = code_only((ROOT / source).read_text())
        package = re.search(r'^package\s+([\w.]+);', text, re.M)
        if not package: continue
        declarations = []
        for match in re.finditer(r'((?:(?:public|private|protected|abstract|static|final)\s+)*)(?:class|interface)\s+(\w+)[^{;]*\{', text):
            start = match.end() - 1
            end = end_at(text, start)
            parents = [c[2] for c in declarations if c[0] < start < c[1]]
            if 'abstract' in match[1] or 'interface' in match[0]:
                abstracts.add(package[1] + '.' + '$'.join(parents + [match[2]]))
            declarations.append((start, end, match[2]))
        for match in re.finditer(r'\b(?:public|protected)\s+void\s+relayoutImpl\s*\(', text):
            parents = [c[2] for c in declarations if c[0] < match.start() < c[1]]
            if parents: layouts.add(package[1] + '.' + '$'.join(parents))
    output = '''package alefj2cl;
/** Abstract modifiers from the exact compiler inputs; regenerated with the source inventory. */
public final class ClassMetadata {
  private static final java.util.Set<String> ABSTRACT = new java.util.HashSet<>(java.util.Arrays.asList(
'''
    output += ',\n'.join('    ' + json.dumps(name) for name in sorted(abstracts))
    output += '''));
  public static boolean isAbstract(Class<?> type) { return type.isArray() || type.isInterface() || ABSTRACT.contains(type.getName()); }
}
'''
    (ROOT / 'browser-src/alefj2cl/ClassMetadata.java').write_text(output)
    layout_output = '''package alefj2cl.extensions;
/** Method declarations from the exact source and dependency-header inventory. */
public final class LayoutMethods {
  private static final java.util.Set<String> DECLARATIONS = new java.util.HashSet<>(java.util.Arrays.asList(
'''
    layout_output += ',\n'.join('    ' + json.dumps(name) for name in sorted(layouts))
    layout_output += '''));
  public static boolean declaresRelayout(Class<?> type) { return DECLARATIONS.contains(type.getName()); }
}
'''
    (ROOT / 'extension-support/alefj2cl/extensions/LayoutMethods.java').write_text(layout_output)
    print('Abstract class metadata:', len(abstracts), 'types')
    print('Layout method metadata:', len(layouts), 'types')


if __name__ == '__main__': main()
