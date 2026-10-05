#!/usr/bin/env python3
"""Prepare external dependency views without editing sibling repositories."""
from pathlib import Path
import json
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'
EXT = ROOT.parent / 'mps-extensions-j2cl'


def write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.is_symlink():
        raise RuntimeError('Refusing to edit through a dependency symlink: ' + str(path))
    path.write_text(text)


def link(source, target):
    if target.is_symlink():
        if target.resolve() != source.resolve():
            raise RuntimeError('Dependency link changed: ' + str(target))
    elif target.exists():
        raise RuntimeError('Expected a dependency link: ' + str(target))
    else:
        target.parent.mkdir(parents=True, exist_ok=True)
        target.symlink_to(source.resolve(), target_is_directory=source.is_dir())


def awt_view(source, target, replacements, relative=''):
    # Expand only branches with local overrides; retain links everywhere else.
    # Unlinking a directory symlink never modifies the sibling it points to.
    if target.is_symlink():
        target.unlink()
    target.mkdir(parents=True, exist_ok=True)
    if relative == 'src/main/java/java/text':
        write(target / 'BUILD.bazel', '''load("@j2cl//build_defs:rules.bzl", "j2cl_library")
j2cl_library(name = "text-j2cl", visibility = ["//visibility:public"])
''')
        return
    # Include new helper files as well as replacements of existing sources.
    prefix = relative + '/' if relative else ''
    names = {child.name for child in source.iterdir()} if source.exists() else set()
    names.update(path[len(prefix):].split('/')[0] for path in replacements if path.startswith(prefix))
    for child_name in sorted(names):
        child = source / child_name
        if child.name.startswith(('bazel-', '.')):
            continue
        name = (relative + '/' if relative else '') + child.name
        if name in replacements:
            entry = target / child.name
            # An override may be added after this branch was already expanded.
            # Replace only the generated link to the original dependency file.
            if entry.is_symlink() and entry.resolve() == child.resolve():
                entry.unlink()
            link(replacements[name], entry)
        elif any(path.startswith(name + '/') for path in replacements):
            awt_view(child, target / child.name, replacements, name)
        else:
            link(child, target / child.name)


def main():
    subprocess.run([sys.executable, str(ROOT / 'tools/adapt_editor_updates.py')], check=True)
    # All java.text classes must be in java.base once java.time exports it.
    awt_source = MPS / '.work/dependency-views/webawt'
    text_sources = awt_source / 'src/main/java/java/text'
    for source in text_sources.glob('*.java'):
        target = ROOT / 'j2cl-src/jre/java/text' / source.name
        if not target.exists():
            write(target, source.read_text())
    subprocess.run([sys.executable, str(ROOT / 'tools/compose_jre.py')], check=True)
    mps_source = EXT / '.work/mps-j2cl'
    view = ROOT / '.work/mps-j2cl'
    view.mkdir(parents=True, exist_ok=True)
    intellij_overrides = {
        'intellij-stubs/src/' + source.relative_to(ROOT / 'j2cl-src/intellij').as_posix(): source
        for source in (ROOT / 'j2cl-src/intellij').rglob('*.java')
    }
    for source in mps_source.iterdir():
        if source.name.startswith(('bazel-', '.')) or source.name in ('out', 'BUILD.bazel', 'mps-rules.bzl'):
            continue
        if source.name == 'third_party' and intellij_overrides:
            awt_view(source, view / source.name, intellij_overrides)
        else:
            link(source, view / source.name)
    build = (mps_source / 'BUILD.bazel').read_text()
    build += '''
load(":financial-sources.bzl", "FINANCIAL_SOURCES")
load(":mps-browser-projects.bzl", "MPS_BROWSER_NAMES")
j2cl_library(
    name = "alef_language_support",
    srcs = [s for s in FINANCIAL_SOURCES if "/samples/" not in s],
    deps = [":mps_" + (p[4:] if p.startswith("mps-") and p != "mps-platform" else p).replace("-", "_").lower() for p in MPS_BROWSER_NAMES] + [
        ":intellij_stubs", "@webawt//:webawt", "@javax-swing-j2cl//src/main:javax-swing-j2cl",
        "@jetbrains_annotations//:annotations", "@gnu_trove_j2cl//:trove-j2cl", "@org_jdom_j2cl//:jdom", "@org_jdom_j2cl//:sax-api",
    ],
    visibility = ["//visibility:public"],
)
'''
    write(view / 'BUILD.bazel', build)
    rules = (mps_source / 'mps-rules.bzl').read_text()
    access = ROOT / 'mps-access.json'
    if access.exists():
        overrides = {row['original']: row['replacement'] for row in json.loads(access.read_text())}
        rules = rules.replace('def _sources(project):', 'ALEF_OVERRIDES = ' + repr(overrides) + '\n\ndef _sources(project):')
        rules = rules.replace('"extensions-src/" + EXTENSION_OVERRIDES[path] if',
            '"alef-src/" + ALEF_OVERRIDES[path] if path in ALEF_OVERRIDES else "extensions-src/" + EXTENSION_OVERRIDES[path] if')
        link(ROOT / 'j2cl-src', view / 'alef-src/j2cl-src')
    rule_target = view / 'mps-rules.bzl'
    if rule_target.is_symlink():
        rule_target.unlink()
    write(rule_target, rules)
    # WalkingKooka owns ParseException in the composed java.base bootclasspath.
    swing_source = MPS / '.work/dependency-views/webswing'
    swing = ROOT / '.work/webswing'
    for source in swing_source.iterdir():
        if source.name != 'src' and not source.name.startswith(('bazel-', '.')):
            link(source, swing / source.name)
    main = swing / 'src/main'
    main.mkdir(parents=True, exist_ok=True)
    link(swing_source / 'src/main/java', main / 'java')
    # Sparse local replacements keep the shared sibling dependency read-only.
    swing_overrides = ROOT / 'j2cl-src/swing'
    link(swing_overrides, main / 'alef-java')
    link(swing_source / 'src/test', swing / 'src/test')
    build = (swing_source / 'src/main/BUILD.bazel').read_text()
    assert 'srcs = glob(["java/**/*.java"]),' in build
    excluded = ['java/java/text/ParseException.java'] + [
        'java/' + source.relative_to(swing_overrides).as_posix()
        for source in sorted(swing_overrides.rglob('*.java'))
    ]
    write(main / 'BUILD.bazel', build.replace('srcs = glob(["java/**/*.java"]),',
        'srcs = glob(["java/**/*.java"], exclude = ' + repr(excluded)
        + ') + glob(["alef-java/**/*.java"]),'))
    awt = ROOT / '.work/webawt'
    awt_replacements = {'src/main/java/java/text/BUILD.bazel': None}
    for source in (ROOT / 'j2cl-src/awt').rglob('*.java'):
        awt_replacements['src/main/java/' + source.relative_to(ROOT / 'j2cl-src/awt').as_posix()] = source
    awt_view(awt_source, awt, awt_replacements)
    relative = 'third_party/webawt/elemental2-1.3.2-w3c-css.patch'
    write(ROOT / relative, (MPS / relative).read_text())
    module = (EXT / 'MODULE.bazel').read_text().replace('module(name = "mps_extensions_j2cl"', 'module(name = "alef_j2cl"')
    module = module.replace('path = "../mps-j2cl/.work/dependency-views/webswing"', 'path = ".work/webswing"')
    module = module.replace('path = "../mps-j2cl/.work/dependency-views/webawt"', 'path = ".work/webawt"')
    module = module.replace('"//:third_party/j2cl/api-support/api-support.patch",',
        '"//:third_party/j2cl/api-support/api-support.patch",\n        "//:third_party/j2cl/java-time.patch",')
    module += '''
bazel_dep(name = "mps_extensions_j2cl", version = "0.1.0")
local_path_override(module_name = "mps_extensions_j2cl", path = "../mps-extensions-j2cl")
bazel_dep(name = "walkingkooka_java_time", version = "0.1.0")
local_path_override(module_name = "walkingkooka_java_time", path = "../walkingkooka-j2cl")
'''
    write(ROOT / 'MODULE.bazel', module)
    write(ROOT / '.bazelversion', '8.3.1\n')
    write(ROOT / '.bazelrc', (EXT / '.bazelrc').read_text() + '''
build --strategy=Javac=local
build --strategy=J2cl=local
build --strategy=Closure=local
build --local_resources=memory=4096
''')
    print('Prepared MPS, extensions, java.time, and Swing dependency views')


if __name__ == '__main__':
    subprocess.run([sys.executable, str(ROOT / 'tools/adapt_backend_guard.py')], check=True)
    subprocess.run([sys.executable, str(ROOT / 'tools/adapt_backend_commands.py')], check=True)
    main()
