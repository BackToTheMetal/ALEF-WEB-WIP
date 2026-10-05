#!/usr/bin/env python3
"""Build an independent Bzlmod consumer of the checksummed source archive."""
import json
from pathlib import Path
import shutil
import subprocess
from build_support import build_lock, bazel_session, invalidate_results

ROOT = Path(__file__).resolve().parents[1]


def run_consumer():
    """Execute with the shared build lock already held by the caller."""
    invalidate_results(ROOT, ['consumer-result.json'])
    release = json.loads((ROOT / 'out/source-release.json').read_text())
    archive = ROOT / 'out' / release['archive']
    consumer = ROOT / 'out/consumer'
    consumer.mkdir(parents=True, exist_ok=True)
    (consumer / 'MODULE.bazel').write_text(f'''module(name = "trove_consumer")
bazel_dep(name = "gnu_trove_j2cl", version = "0.1.0")
bazel_dep(name = "j2cl", version = "20260402")
bazel_dep(name = "rules_closure", version = "0.16.0")
bazel_dep(name = "rules_java", version = "8.6.1")
archive_override(
    module_name = "gnu_trove_j2cl",
    urls = ["{archive.as_uri()}"],
    sha256 = "{release['sha256']}",
    strip_prefix = "gnu-trove-j2cl-0.1.0",
)
''')
    for name in ['.bazelversion', '.bazelrc']:
        shutil.copyfile(ROOT / name, consumer / name)
    (consumer / 'BUILD.bazel').write_text('''load("@j2cl//build_defs:rules.bzl", "j2cl_library", "j2cl_application")
load("@rules_closure//closure:defs.bzl", "closure_js_library")
j2cl_library(name = "consumer", srcs = ["Consumer.java"], deps = ["@gnu_trove_j2cl//:trove-j2cl", "@j2cl//:jsinterop-annotations-j2cl"])
closure_js_library(name = "app", srcs = ["app.js"], deps = [":consumer"])
j2cl_application(name = "application", entry_points = ["trove.consumer"], deps = [":app"])
''')
    (consumer / 'Consumer.java').write_text('''package consumer;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TObjectIntHashMap;
import jsinterop.annotations.JsMethod;
public final class Consumer {
  @JsMethod public static String run() {
    TLongObjectHashMap<String> ids = new TLongObjectHashMap<>();
    ids.put(9007199254740992L, "a"); ids.put(9007199254740993L, "b");
    TObjectIntHashMap<String> counts = new TObjectIntHashMap<>();
    counts.put("x", 1); counts.increment("x");
    if (ids.size() != 2 || !ids.get(9007199254740993L).equals("b") || counts.get("x") != 2) throw new AssertionError();
    return "PASS independent archive consumer";
  }
}
''')
    (consumer / 'app.js').write_text("goog.module('trove.consumer');\nconst Consumer = goog.require('consumer.Consumer');\ngoog.exportSymbol('runConsumer', () => Consumer.run());\n")
    # Use Bazel's validated action cache across these sequential workspaces.
    # Only cache/output state is shared: the consumer resolves the archive via
    # its own MODULE.bazel, with no source checkout override. Switching a server
    # between workspaces requires shutdown; no simultaneous builds are allowed.
    output_link = ROOT / 'bazel-out'
    startup = ['--host_jvm_args=-Xmx1024m']
    if output_link.is_symlink():
        output_base = output_link.resolve().parents[2]
        subprocess.run(['bazelisk', 'shutdown'], cwd=ROOT, check=True)
        startup.append('--output_base=' + str(output_base))
    with bazel_session(consumer, startup):
        with (ROOT / 'out/consumer-build.log').open('w') as log:
            subprocess.run(['bazelisk', *startup, 'build', '//:application', '--jobs=1'], cwd=consumer,
                           stdout=log, stderr=subprocess.STDOUT, check=True)
        subprocess.run(['node', str(ROOT / 'tools/test_consumer.cjs')], cwd=ROOT, check=True)
    (ROOT / 'out/consumer-result.json').write_text(json.dumps({'status': 'passed', 'archiveSha256': release['sha256'],
        'dependency': '@gnu_trove_j2cl//:trove-j2cl', 'localPathOverride': False,
        'sharedBazelActionCache': output_link.is_symlink()}, indent=2) + '\n')


def main():
    with build_lock():
        run_consumer()


if __name__ == '__main__': main()
