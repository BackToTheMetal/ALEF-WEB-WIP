#!/usr/bin/env python3
"""Reproduce the reviewed J2CL port from the pinned JetBrains source JAR.

The input is retained separately for reference compilation. No generated stub
is used: hash algorithms, views, callbacks and storage layout remain upstream.
"""
import hashlib
import json
from pathlib import Path
import re
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'third_party/trove4j-1.0.20200330-sources.jar'


def mask(text):
    return re.sub(r'//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'',
                  lambda m: ''.join('\n' if c == '\n' else ' ' for c in m[0]), text)


def replace_methods(text, pattern, replacement):
    # Match headers in source with comments/strings hidden, then balance braces.
    hidden = mask(text)
    edits = []
    for match in re.finditer(pattern, hidden):
        start = match.start()
        brace = hidden.find('{', match.end() - 1)
        depth, end = 1, brace + 1
        while depth:
            depth += (hidden[end] == '{') - (hidden[end] == '}')
            end += 1
        # Remove an associated @Override as well when removing/replacing a method.
        prefix = re.search(r'@Override\s*$', text[:start])
        if prefix:
            start = prefix.start()
        edits.append((start, end, replacement(match, text[match.start():end])))
    for start, end, value in reversed(edits):
        text = text[:start] + value + text[end:]
    return text


def port(name, text):
    text = text.replace('\r\n', '\n')
    text = re.sub(r'import java\.io\.(IOException|ObjectInputStream|ObjectOutputStream);\n', '', text)
    text = replace_methods(text, r'private\s+void\s+(?:readObject|writeObject)\s*\([^)]*\)[^{]*\{', lambda *_: '')
    text = text.replace('import java.lang.reflect.Array;', 'import java.util.Arrays;')
    text = re.sub(r'\(T\[\]\)\s*Array\.newInstance\(a\.getClass\(\)\.getComponentType\(\),\s*size\)',
                  'Arrays.copyOf(a, size)', text)
    text = text.replace('System.arraycopy(', 'ArraySupport.copy(')
    # J2CL lacks Object.clone(); array copies are rewritten to Arrays.copyOf below.
    # Preserve storage,
    # including dead slots/capacity, with explicit copies in concrete classes.
    if name in ('THash', 'TPrimitiveHash', 'TByteHash', 'TIntHash', 'TLongHash', 'TFloatHash', 'TDoubleHash', 'TObjectHash'):
        return_type = 'TObjectHash<T>' if name == 'TObjectHash' else 'Object'
        text = replace_methods(text, r'public\s+[\w<>?, ]+\s+clone\(\)\s*\{',
                               lambda *_: 'public abstract ' + return_type + ' clone();')
    elif re.fullmatch(r'T(?:Byte|Int|Long|Float|Double)ArrayList', name):
        body = (f'public Object clone() {{\n        {name} copy = new {name}(0);\n'
                '        copy._data = _data == null ? null : _data.clone();\n'
                '        copy._pos = _pos;\n        return copy;\n    }')
        text = replace_methods(text, r'public\s+Object\s+clone\(\)\s*\{', lambda *_: body)
    elif name.endswith(('HashMap', 'HashSet')) and '/decorator/' not in name:
        cls = re.search(r'public class (\w+)(<[^>{}]+>)? extends (\w+)', text)
        if cls and cls[1] == name:
            generic = cls[2] or ''
            parent = cls[3]
            obj = parent == 'TObjectHash'
            body = (f'public {name}{generic} clone() {{\n'
                    f'        {name}{generic} copy = new {name}{generic}(0, _loadFactor, _hashingStrategy);\n'
                    '        copy._size = _size;\n        copy._free = _free;\n'
                    '        copy._deadkeys = _deadkeys;\n        copy._maxSize = _maxSize;\n'
                    '        copy._set = _set == null ? null : _set.clone();\n')
            if parent in ('TByteHash', 'TIntHash', 'TLongHash', 'TFloatHash', 'TDoubleHash'):
                body += '        copy._states = _states == null ? null : _states.clone();\n'
            if name.endswith('HashMap'):
                body += '        copy._values = _values == null ? null : _values.clone();\n'
            body += '        return copy;\n    }'
            if re.search(r'public\s+[\w<>?, ]+\s+clone\(\)', mask(text)):
                text = replace_methods(text, r'public\s+[\w<>?, ]+\s+clone\(\)\s*\{', lambda *_: body)
            else:
                pos = text.find('{', cls.end()) + 1
                text = text[:pos] + '\n    ' + body + '\n' + text[pos:]
    # Methods with explicit upper-bound checks still need lower-bound checks;
    # Java's implicit primitive-array bounds checks are absent in J2CL.
    if name.endswith('ArrayList'):
        text = text.replace('ArraySupport.copy(_data, offset, values, valOffset, length);',
                            'ArraySupport.copy(values, valOffset, _data, offset, length);')
        text = text.replace('swap(i, rand.nextInt(i));', 'swap(i, rand.nextInt(i + 1));')
        text = text.replace('if (offset >= _pos)', 'if (offset < 0 || offset >= _pos)')
        text = text.replace('if (index >= _pos)', 'if (index < 0 || index >= _pos)')
        # Upstream extrema accidentally read _data[_pos] on every iteration.
        text = replace_methods(text, r'public\s+\w+\s+(?:min|max)\(\)\s*\{',
                               lambda m, body: body.replace('_data[_pos]', '_data[i]'))
    def retain(m, body):
        if 'stopCompactingOnRemove' in body or re.search(r'\w+\.remove\(\)', body):
            return body
        brace = body.index('{')
        return (body[:brace + 1] + '\n        stopCompactingOnRemove();\n        try {' + body[brace + 1:-1]
                + '\n        } finally { startCompactingOnRemove(true); }\n    }')
    text = replace_methods(text, r'public\s+boolean\s+(?:retainEntries|retainAll)\([^)]*\)\s*\{', retain) if name.endswith(('HashMap', 'HashSet')) else text
    if name == 'THashMap':
        # Snapshot inputs because separate views can share the same backing map.
        text = text.replace('for (Object element : collection) {',
                            'for (Object element : collection.toArray()) {')
        text = text.replace('public boolean removeAll(Collection<?> collection) {',
                            'public boolean removeAll(Collection<?> collection) {\n'
                            '            if (collection == this) {\n'
                            '                boolean changed = !isEmpty();\n'
                            '                clear();\n'
                            '                return changed;\n'
                            '            }')
        text = text.replace('return null != THashMap.this.remove(key);',
                            'int index = THashMap.this.index(key);\n'
                            '            if (index < 0) return false;\n'
                            '            THashMap.this.removeAt(index);\n'
                            '            return true;')
    # Preserve float32 at library boundaries and after arithmetic/callbacks.
    # All operations use stock J2CL's Float bit conversion implementation.
    if 'float ' in mask(text):
        insertions = []
        for m in re.finditer(r'(?:public|protected|private)\s+(?:static\s+|final\s+)*[\w<>?, ]+\s+\w+\([^)]*\)\s*\{', mask(text)):
            header = text[m.start():m.end()]
            params = re.findall(r'\bfloat\s+(\w+)\b', header[header.index('('):])
            if params and not re.search(r'\b' + re.escape(name) + r'\s*\(', header):
                insertions.append((m.end(), '\n' + ''.join(f'        {p} = FloatSupport.round({p});\n' for p in params)))
        for pos, insertion in reversed(insertions):
            text = text[:pos] + insertion + text[pos:]
    if name == 'THash':
        text = text.replace('_loadFactor = loadFactor;', 'loadFactor = FloatSupport.round(loadFactor);\n        _loadFactor = loadFactor;')
        for expression in ['initialCapacity / loadFactor', '(desiredCapacity + size()) / _loadFactor', 'size() / _loadFactor', 'capacity * _loadFactor']:
            text = text.replace(expression, 'FloatSupport.round(' + expression + ')')
    if name == 'TFloatArrayList' or name.endswith('FloatHashMap'):
        text = re.sub(r'((?:_data|_values|values)\[[^\n;]+?\])\s*=\s*function.execute\(([^;]+)\);',
                      r'\1 = FloatSupport.round(function.execute(\2));', text)
        text = re.sub(r'(_values\[[^\]]+\])\s*\+=\s*amount;', r'\1 = FloatSupport.round(\1 + amount);', text)
    if name.endswith('Decorator'):
        text = re.sub(r'FloatSupport.round\((\w+)\)', r'Float.intBitsToFloat(Float.floatToIntBits(\1))', text)
    text = re.sub(r'(_(?:set|values|states|data))\.clone\(\)', r'java.util.Arrays.copyOf(\1, \1.length)', text)
    return text


def main():
    from mps_requirements import check_profile
    retained = set(check_profile(check_files=False)['retained'])
    out = ROOT / 'src/main/java'
    changes = []
    check = '--check' in sys.argv
    errors = []
    baseline = json.loads((ROOT / 'compatibility/baseline.json').read_text())
    expected = next(x['sha256'] for x in baseline['artifacts'] if x['file'].endswith('-sources.jar'))
    if hashlib.sha256(SOURCE.read_bytes()).hexdigest() != expected:
        raise SystemExit('Upstream source checksum mismatch')
    with zipfile.ZipFile(SOURCE) as archive:
        for entry in sorted(archive.namelist()):
            if not entry.endswith('.java'):
                continue
            if entry.endswith('/SerializationProcedure.java'):
                changes.append({'file': entry, 'status': 'excluded', 'reason': 'JVM object streams'})
                continue
            if entry[:-5].replace('/', '.') not in retained:
                changes.append({'file': entry, 'status': 'excluded', 'reason': 'Outside pinned MPS dependency closure'})
                continue
            original = archive.read(entry).decode('utf-8')
            text = port(Path(entry).stem, original)
            text = text.replace('package gnu.trove;', '// Modified for J2CL; see tools/port_sources.py and THIRD_PARTY_NOTICES.md.\npackage gnu.trove;', 1)
            target = out / entry
            if check:
                if not target.exists() or target.read_text() != text: errors.append(str(target))
            else:
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_text(text, encoding='utf-8')
            changes.append({'file': entry, 'status': 'ported', 'upstreamSha256': hashlib.sha256(archive.read(entry)).hexdigest()})
    manifest = ROOT / 'compatibility/source-manifest.json'
    serialized = json.dumps(changes, indent=2) + '\n'
    actual = {p.relative_to(out).as_posix()[:-5].replace('/', '.') for p in out.rglob('*.java')}
    if actual != retained:
        errors.append('Unexpected/missing production sources: ' + ', '.join(sorted(actual ^ retained)))
    if errors:
        raise SystemExit('Generated source drift:\n' + '\n'.join(errors))
    if check:
        if manifest.read_text() != serialized: errors.append(str(manifest))
        if errors: raise SystemExit('Generated source drift:\n' + '\n'.join(errors))
    else:
        manifest.write_text(serialized)
    print(f'{"Verified" if check else "Ported"} {sum(c["status"] == "ported" for c in changes)} upstream source files')


if __name__ == '__main__':
    main()
