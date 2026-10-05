#!/usr/bin/env python3
"""Shade checked-out sources and generate a root-consumer J2CL JRE patch."""
from pathlib import Path
import base64, difflib, hashlib, json, re, sys, tarfile, urllib.request

ROOT = Path(__file__).resolve().parents[1]

def code_only(source):
    # Preserve offsets while masking comments and literals for source selection.
    return re.sub(r'//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'',
                  lambda m: ' ' * len(m.group()), source)

def compatible_code(source):
    code = code_only(source)
    while True:
        match = re.search(r'@GwtIncompatible(?:\s*\([^)]*\))?', code)
        if not match:
            return code
        start = match.start()
        declaration = re.search(r'[;{]', code[match.end():])
        if not declaration:
            raise ValueError('Unterminated GwtIncompatible declaration')
        end = match.end() + declaration.start() + 1
        if code[end - 1] == '{':
            depth = 1
            while depth:
                if code[end] == '{': depth += 1
                if code[end] == '}': depth -= 1
                end += 1
        code = code[:start] + ' ' * (end - start) + code[end:]

def select_sources(sources, origins):
    """Transitive source closure, ignoring JVM-only declarations and unused imports."""
    codes = {rel: compatible_code(src) for rel, src in sources.items()}
    index = {}
    packages = {}
    for rel, code in codes.items():
        package = re.search(r'^package ([\w.]+);', code, re.M).group(1)
        packages[rel] = package
        declaration = re.search(r'\b(?:class|interface|enum)\s+(\w+)', code)
        if declaration:
            index[package + '.' + declaration.group(1)] = rel
    selected = set()
    pending = [rel for rel in sources if not origins[rel].startswith(('upstream/walkingkooka/', 'upstream/walkingkooka-text-printer/')) and re.search(r'\b(?:class|interface|enum)\s+\w+', codes[rel])]
    while pending:
        rel = pending.pop()
        if rel in selected:
            continue
        selected.add(rel)
        code = codes[rel]
        imports = re.findall(r'^import (?:static )?([\w.*]+);', code, re.M)
        body = re.sub(r'^(?:import|package) [^;]+;', '', code, flags=re.M)
        tokens = set(re.findall(r'\b[A-Za-z_$][\w$]*\b', body))
        refs = {packages[rel] + '.' + token for token in tokens}
        for imp in imports:
            if imp.endswith('.*'):
                refs.update(imp[:-1] + token for token in tokens)
            elif imp.rsplit('.', 1)[-1] in tokens:
                refs.add(imp)
        refs.update(re.findall(r'\bwalkingkooka(?:\.\w+)+', body))
        for ref in refs:
            while '.' in ref:
                if ref in index:
                    pending.append(index[ref])
                    break
                ref = ref.rsplit('.', 1)[0]
    return {rel: sources[rel] for rel in sorted(selected)}

def main():
    out = ROOT / 'out'
    out.mkdir(exist_ok=True)
    archive = out / 'j2cl-v20260402.tar.gz'
    if not archive.exists():
        urllib.request.urlretrieve('https://github.com/google/j2cl/releases/download/v20260402/j2cl-v20260402.tar.gz', archive)
    assert base64.b64encode(hashlib.sha256(archive.read_bytes()).digest()).decode() == '0UGa7O/sJyfTRJ8Qp3LZc/9dOnp0cDZ9HPwseCx9bmY='
    with tarfile.open(archive) as tf:
        baseline = {m.name.removeprefix('j2cl-20260402/'): tf.extractfile(m).read().decode() for m in tf.getmembers() if m.isfile() and (m.name.endswith('.java') or m.name.endswith('/BUILD')) and ('/jre/java/' in m.name or '/tools/java/com/google/j2cl/tools/gwtincompatible/BUILD' in m.name)}
    sources = {}
    origins = {}
    for repo in sorted((ROOT / 'upstream').iterdir()):
        if 'annotation-processor' in repo.name or repo.name in ('j2cl-uber', 'j2cl-java-util-timezone-ZoneRulesReader'):
            continue
        for p in sorted((repo / 'src/main/java').rglob('*.java')):
            rel = p.relative_to(repo / 'src/main/java').as_posix()
            if 'Testing' in p.name or ('/test/' in rel and p.name != 'Fake.java') or '/annotationprocessor/' in rel:
                continue
            sources[rel] = p.read_text()
            origins[rel] = p.relative_to(ROOT).as_posix()
    for p in sorted((ROOT / 'generated').rglob('*.java')):
        rel = p.relative_to(ROOT / 'generated').as_posix()
        sources[rel] = p.read_text()
        origins[rel] = p.relative_to(ROOT).as_posix()
    sources = select_sources(sources, origins)
    # The upstream Maven plugin performs these relocations before transpilation.
    relocations = {'org.threeten.bp': 'java.time'}
    for rel, source in sources.items():
        package = re.search(r'^package ([\w.]+);', source, re.M).group(1)
        if package in ('walkingkooka.j2cl.java.io', 'walkingkooka.j2cl.java.text'):
            relocations[package + '.' + Path(rel).stem] = package.removeprefix('walkingkooka.j2cl.') + '.' + Path(rel).stem
        if package in ('walkingkooka.j2cl.java.util.locale', 'walkingkooka.j2cl.java.util.timezone', 'walkingkooka.j2cl.java.util.calendar', 'walkingkooka.j2cl.java.util.currency'):
            relocations[package + '.' + Path(rel).stem] = 'java.util.' + Path(rel).stem
    replacements = {}
    manifest = {}
    for rel, source in sources.items():
        package = re.search(r'^package ([\w.]+);', source, re.M).group(1)
        oldname = package + '.' + Path(rel).stem
        newname = relocations.get(oldname, oldname.replace('org.threeten.bp', 'java.time'))
        newpackage = newname.rsplit('.', 1)[0]
        source = source.replace('package ' + package + ';', 'package ' + newpackage + ';')
        # Fully qualified class references only; support/generated subpackages stay intact.
        for old, new in sorted(relocations.items(), key=lambda x: -len(x[0])):
            source = re.sub(r'(?<![\w.])' + re.escape(old) + r'(?!\w)', new, source)
        if newname == 'java.util.Locale':
            # Preserve the two constants supplied by stock J2CL.
            source = source.replace('public final class Locale {', 'public final class Locale {\n\n    public static final Locale ENGLISH = new Locale("en");\n    public static final Locale US = new Locale("en", "US");')
        if newname == 'walkingkooka.collect.list.Lists':
            # This factory is unrelated to java.time; its upstream concurrency
            # adapter deliberately uses Lists.array(). Keep it JVM-only.
            source = source.replace('import java.util.ArrayList;', 'import javaemul.internal.annotations.GwtIncompatible;\nimport java.util.ArrayList;')
            source = source.replace('public static <T> CopyOnWriteArrayList<T> copyOnWrite()', '@GwtIncompatible\n    public static <T> CopyOnWriteArrayList<T> copyOnWrite()')
        target = 'jre/java/' + newname.replace('.', '/') + '.java'
        if target in replacements:
            raise ValueError('Duplicate provider: ' + target)
        replacements[target] = source.rstrip() + '\n'
        manifest[target] = origins[rel]
    for path in sorted((ROOT / 'overlay').rglob('*.java')):
        target = 'jre/java/' + path.relative_to(ROOT / 'overlay').as_posix()
        if target in replacements:
            raise ValueError('Duplicate local provider: ' + target)
        replacements[target] = path.read_text()
        manifest[target] = path.relative_to(ROOT).as_posix()
    build = baseline['jre/java/BUILD']
    build = build.replace('["java*/**/*.java"]', '["java*/**/*.java", "walkingkooka/**/*.java"]')
    build = build.replace('srcs = [":jre_java_files"],', 'srcs = [":walkingkooka_jre_stripped"],')
    build += '''
# The bootclasspath java_library does not run the normal J2CL source stripper.
# Use the official parser-based stripper before compiling these upstream sources.
genrule(
    name = "walkingkooka_jre_stripped",
    srcs = [":jre_java_files"],
    outs = ["walkingkooka_jre_stripped.srcjar"],
    tools = ["//tools/java/com/google/j2cl/tools/gwtincompatible:GwtIncompatibleStripper"],
    cmd = "$(location //tools/java/com/google/j2cl/tools/gwtincompatible:GwtIncompatibleStripper) -d $@.zip $(location :jre_java_files) && mv $@.zip $@",
)
'''
    replacements['jre/java/BUILD'] = build
    stripper_build = 'tools/java/com/google/j2cl/tools/gwtincompatible/BUILD'
    replacements[stripper_build] = baseline[stripper_build].replace('name = "GwtIncompatibleStripper",', 'name = "GwtIncompatibleStripper",\n    visibility = ["//jre/java:__pkg__"],')
    chunks = []
    for path, content in sorted(replacements.items()):
        before = baseline.get(path, '')
        if before == content:
            continue
        chunks.append(f'diff --git a/{path} b/{path}\n')
        if path not in baseline:
            chunks.append('new file mode 100644\n')
        chunks.extend(difflib.unified_diff(before.splitlines(True), content.splitlines(True), fromfile='a/' + path if before else '/dev/null', tofile='b/' + path))
    outputs = {'patches/j2cl-java-time.patch': ''.join(chunks),
               'release/source-manifest.json': json.dumps(manifest, indent=2) + '\n'}
    for name, text in outputs.items():
        path = ROOT / name
        if '--check' in sys.argv:
            if not path.exists() or path.read_text() != text:
                raise SystemExit(name + ' is stale; run tools/overlay.py')
        else:
            path.parent.mkdir(exist_ok=True)
            path.write_text(text)
    print('Overlay:', len(manifest), 'Java sources')

if __name__ == '__main__':
    main()
