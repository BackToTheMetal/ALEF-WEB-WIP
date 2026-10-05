#!/usr/bin/env python3
"""Build a separate consumer of the exact release archive, using the root-owned JRE patch."""
import argparse
import base64
import hashlib
import json
import os
import shutil
import subprocess
import sys
import tarfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def main():
    if os.environ.get('J2CL_BUILD_LOCK_HELD') != '1':
        os.execvp('flock',['flock','/tmp/j2cl-build.lock','env','J2CL_BUILD_LOCK_HELD=1',sys.executable,__file__,*sys.argv[1:]])
    p=argparse.ArgumentParser(); p.add_argument('--browser',type=Path,required=True); args=p.parse_args()
    archive=ROOT/'out/release/java-nio-j2cl-0.1.0.tar.gz'
    digest=hashlib.sha256(archive.read_bytes()).digest()
    folder=ROOT/'out/archive-consumer'; folder.mkdir(parents=True,exist_ok=True)
    patch_folder=folder/'third_party/java-nio'; patch_folder.mkdir(parents=True,exist_ok=True)
    with tarfile.open(archive,'r:gz') as source:
        patch=source.extractfile('java-nio-j2cl-0.1.0/patches/j2cl-nio.patch').read()
    (patch_folder/'j2cl-nio.patch').write_bytes(patch)
    (patch_folder/'BUILD.bazel').write_text('exports_files(["j2cl-nio.patch"])\n')
    module='''module(name = "nio_archive_consumer")
bazel_dep(name = "java_nio_j2cl", version = "0.1.0")
bazel_dep(name = "j2cl", version = "20260402")
bazel_dep(name = "rules_closure", version = "0.16.0")
archive_override(module_name = "java_nio_j2cl", urls = [%s], integrity = %s, strip_prefix = "java-nio-j2cl-0.1.0")
single_version_override(module_name = "j2cl", version = "20260402", patches = ["//third_party/java-nio:j2cl-nio.patch"], patch_strip = 1)
''' % (json.dumps(archive.as_uri()),json.dumps('sha256-'+base64.b64encode(digest).decode()))
    (folder/'MODULE.bazel').write_text(module)
    for name in ['.bazelversion','.bazelrc']: shutil.copyfile(ROOT/name,folder/name)
    (folder/'BUILD.bazel').write_text('''load("@j2cl//build_defs:rules.bzl", "j2cl_library", "j2cl_application")
load("@rules_closure//closure:defs.bzl", "closure_js_library")
j2cl_library(name="consumer", srcs=["Consumer.java"], deps=["@java_nio_j2cl//:java-nio", "@j2cl//:jsinterop-annotations-j2cl"])
closure_js_library(name="entry", srcs=["entry.js"], deps=[":consumer"])
j2cl_application(name="application", entry_points=["archive.consumer"], deps=[":entry"])
''')
    (folder/'Consumer.java').write_text('''package consumer;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import jsinterop.annotations.JsMethod;
public final class Consumer {
  @JsMethod public static String run() {
    if (!"MPS".equals(new String("MPS".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))) throw new AssertionError();
    try { Files.exists(null); throw new AssertionError("Unexpected filesystem result"); }
    catch (UnsupportedOperationException expected) {
      if (!expected.getMessage().contains("Files.exists")) throw new AssertionError();
      return "PASS: separate archive consumer linked the NIO stub";
    }
  }
}
''')
    (folder/'entry.js').write_text("goog.module('archive.consumer');\nconst Consumer=goog.require('consumer.Consumer');\ngoog.global['nioProbeResult']=Consumer.run();\n")
    subprocess.run(['bazelisk','build','//:application'],cwd=folder,check=True)
    # Keep the library's full runtime test report intact.
    report=ROOT/'compatibility/browser-check.json'; saved=report.read_bytes() if report.exists() else None
    subprocess.run(['python3',str(ROOT/'tools/browser_smoke.py'),str(folder/'bazel-bin/application.js'),'--browser',str(args.browser)],check=True)
    browser=json.loads(report.read_text())
    if saved is not None: report.write_bytes(saved)
    result={'status':'PASS','archive':archive.name,'sha256':digest.hex(),'consumer':'out/archive-consumer','browser':browser}
    (ROOT/'out/release/archive-consumer-check.json').write_text(json.dumps(result,indent=2)+'\n')
    print(json.dumps(result,indent=2))
if __name__=='__main__': main()
