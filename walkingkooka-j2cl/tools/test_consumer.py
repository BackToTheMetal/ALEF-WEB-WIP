#!/usr/bin/env python3
"""Build and execute a separate component using the exact packaged library."""
from pathlib import Path
import os, shutil, subprocess, tarfile

ROOT = Path(__file__).resolve().parents[1]

def main():
    consumer = ROOT / 'out/consumer'
    if consumer.is_symlink() or consumer.resolve() != ROOT.resolve() / 'out/consumer':
        raise ValueError('Consumer output must stay inside this workspace')
    if consumer.exists():
        shutil.rmtree(consumer)
    (ROOT / 'out/consumer-results.json').unlink(missing_ok=True)
    consumer.mkdir(parents=True, exist_ok=True)
    with tarfile.open(ROOT / 'release/walkingkooka-java-time-0.1.0.tar.gz') as tf:
        tf.extractall(consumer, filter='data')
    library = consumer / 'walkingkooka-java-time-0.1.0'
    (consumer / 'patches').mkdir(exist_ok=True)
    shutil.copyfile(library / 'patches/j2cl-java-time.patch', consumer / 'patches/j2cl-java-time.patch')
    (consumer / 'MODULE.bazel').write_text('''module(name = "java_time_consumer")
bazel_dep(name = "walkingkooka_java_time", version = "0.1.0")
local_path_override(module_name = "walkingkooka_java_time", path = "walkingkooka-java-time-0.1.0")
bazel_dep(name = "j2cl", version = "20260402")
bazel_dep(name = "rules_closure", version = "0.16.0")
single_version_override(module_name = "j2cl", version = "20260402", patches = ["//:patches/j2cl-java-time.patch"], patch_strip = 1)
''')
    (consumer / 'BUILD.bazel').write_text('exports_files(["patches/j2cl-java-time.patch"])\n')
    for name in ['.bazelrc', '.bazelversion']:
        shutil.copyfile(library / name, consumer / name)
    (consumer / 'tests').mkdir(exist_ok=True)
    for name in ['TimeContracts.java', 'BrowserEntry.java', 'app.js', 'BUILD.bazel']:
        text = (library / 'tests' / name).read_text()
        text = text.replace('//:java-time-j2cl', '@walkingkooka_java_time//:java-time-j2cl')
        (consumer / 'tests' / name).write_text(text)
    # Reuse only this workspace's toolchain cache, sequentially. The consumer
    # still resolves a separate root module and the exact extracted archive.
    output_base = subprocess.check_output(['bazelisk', 'info', 'output_base'], cwd=ROOT, text=True).strip()
    subprocess.run(['bazelisk', 'shutdown'], cwd=ROOT, check=True)
    bazel = ['bazelisk', '--output_base=' + output_base]
    try:
        subprocess.run([*bazel, 'build', '//tests:browser'], cwd=consumer, check=True)
        env = dict(os.environ, TIME_WORKSPACE=str(consumer), TIME_RESULT='consumer-results.json', TIME_ENGINE='chromium', TIME_MODE='optimized')
        subprocess.run(['node', str(ROOT / 'tools/test_browser.cjs')], cwd=ROOT, env=env, check=True)
    finally:
        subprocess.run([*bazel, 'shutdown'], cwd=consumer, check=False)

if __name__ == '__main__':
    main()
