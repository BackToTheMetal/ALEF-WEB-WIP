"""Prepare root-owned build metadata and a read-only view of ../mps-j2cl (WSL)."""
from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'

def write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.is_symlink():
        raise RuntimeError(f'Refusing to write through {path}')
    path.write_text(text)

def main():
    view = ROOT / '.work/mps-j2cl'
    view.mkdir(parents=True, exist_ok=True)
    for source in MPS.iterdir():
        if source.name in ('mps-rules.bzl', 'BUILD.bazel') or source.name.startswith(('bazel-', '.')) or source.name == 'out':
            continue
        target = view / source.name
        if not target.exists():
            target.symlink_to(source, target_is_directory=source.is_dir())
    overlays = json.loads((ROOT / 'mps-access.json').read_text())
    rules = (MPS / 'mps-rules.bzl').read_text()
    assert rules.count('def _sources(project):') == 1
    assert rules.count('"mps-web/" + MPS_OVERRIDES[path] if') == 1
    rules = rules.replace('def _sources(project):', 'EXTENSION_OVERRIDES = ' + repr({r['original']: r['replacement'] for r in overlays}) + '\n\ndef _sources(project):')
    rules = rules.replace('"mps-web/" + MPS_OVERRIDES[path] if', '"extensions-src/" + EXTENSION_OVERRIDES[path] if path in EXTENSION_OVERRIDES else "mps-web/" + MPS_OVERRIDES[path] if')
    write(view / 'mps-rules.bzl', rules)
    build = (MPS / 'BUILD.bazel').read_text()
    for target in ('browser_editor_host', 'browser_platform'):
        marker = 'name = "' + target + '",'
        assert build.count(marker) == 1
        build = build.replace(marker, marker + '\n    visibility = ["//visibility:public"],')
    build += '\nexports_files(["tests/editor_live/ExpressionLanguage.java"], visibility = ["//visibility:public"])\n'
    build += '\nalias(name = "extension_annotations", actual = "@jetbrains_annotations//:annotations", visibility = ["//visibility:public"])\n'
    write(view / 'BUILD.bazel', build)
    if not (view / 'extensions-src').exists():
        (view / 'extensions-src').symlink_to(ROOT / 'j2cl-src', target_is_directory=True)
    for relative in ('third_party/j2cl/composed.patch', 'third_party/j2cl/type-support/class-type-support.patch', 'third_party/j2cl/mps-support/classloader.patch', 'third_party/j2cl/api-support/api-support.patch', 'third_party/webawt/elemental2-1.3.2-w3c-css.patch'):
        write(ROOT / relative, (MPS / relative).read_text())
    module = (MPS / 'MODULE.bazel').read_text().replace('module(name = "mps_j2cl"', 'module(name = "mps_extensions_j2cl"')
    module = module.replace('path = ".work/dependency-views/', 'path = "../mps-j2cl/.work/dependency-views/')
    # Use the MPS module's annotation provider, rather than compiling another copy.
    module = module.split('http_archive = use_repo_rule(', 1)[0]
    module += '\nbazel_dep(name = "mps_j2cl", version = "0.1.0")\nlocal_path_override(module_name = "mps_j2cl", path = ".work/mps-j2cl")\n'
    write(ROOT / 'MODULE.bazel', module)
    for name in ('.bazelversion', '.bazelrc'):
        write(ROOT / name, (MPS / name).read_text())
    print('Prepared MPS dependency view and composed J2CL toolchain; sibling sources unchanged')

if __name__ == '__main__':
    main()
