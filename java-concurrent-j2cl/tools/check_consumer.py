#!/usr/bin/env python3
"""Verify a separate root module consumes a root-owned copy of the pinned JRE patch."""

import argparse
import hashlib
import json
import os
import shutil
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WORKSPACE = ROOT / "out/consumer"


def write(relative, content):
    destination = WORKSPACE / relative
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_text(content, encoding="utf-8", newline="\n")


def verify_patch_copy():
    canonical = hashlib.sha256((ROOT / "patches/j2cl-concurrent.patch").read_bytes()).hexdigest()
    copied = hashlib.sha256((WORKSPACE / "third_party/java-concurrent/j2cl-concurrent.patch").read_bytes()).hexdigest()
    if copied != canonical:
        raise SystemExit("Consumer patch differs from the canonical generated patch")
    write("patch-provenance.json", json.dumps({
        "source_module": "java_concurrent_j2cl",
        "source_version": "0.1.0",
        "j2cl_version": "20260402",
        "root_owned_patch_label": "//third_party/java-concurrent:j2cl-concurrent.patch",
        "canonical_patch_sha256": canonical,
        "copied_patch_sha256": copied,
        "matches": True,
    }, indent=2) + "\n")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--prepare-only", action="store_true")
    parser.add_argument("--browser", type=Path)
    args = parser.parse_args()
    WORKSPACE.mkdir(parents=True, exist_ok=True)
    write("MODULE.bazel", '''module(name = "concurrent_consumer_smoke", version = "0.1.0")
bazel_dep(name = "java_concurrent_j2cl", version = "0.1.0")
local_path_override(module_name = "java_concurrent_j2cl", path = %s)
bazel_dep(name = "j2cl", version = "20260402")
bazel_dep(name = "rules_closure", version = "0.16.0")
single_version_override(
    module_name = "j2cl",
    version = "20260402",
    patch_strip = 1,
    patches = ["//third_party/java-concurrent:j2cl-concurrent.patch"],
)
''' % json.dumps(ROOT.as_posix()))
    write("third_party/java-concurrent/BUILD.bazel", 'exports_files(["j2cl-concurrent.patch"])\n')
    shutil.copyfile(ROOT / "patches/j2cl-concurrent.patch",
                    WORKSPACE / "third_party/java-concurrent/j2cl-concurrent.patch")
    verify_patch_copy()
    for name in [".bazelrc", ".bazelversion"]:
        shutil.copyfile(ROOT / name, WORKSPACE / name)
    write("BUILD.bazel", '''load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")
j2cl_library(
    name = "probe",
    srcs = ["ConsumerProbe.java"],
    deps = ["@j2cl//:jsinterop-annotations-j2cl"],
)
closure_js_library(name = "entry", srcs = ["entry.js"], deps = [":probe"])
j2cl_application(name = "application", entry_points = ["concurrent.consumer"], deps = [":entry"])
''')
    write("ConsumerProbe.java", '''package consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import jsinterop.annotations.JsMethod;

public final class ConsumerProbe {
  @JsMethod public static String run() throws Exception {
    AtomicInteger count = new AtomicInteger();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<Integer> result = executor.submit(() -> count.incrementAndGet());
    if (count.get() != 1 || !result.isDone() || result.get() != 1) {
      throw new AssertionError("Tasks must execute on submission");
    }
    ReentrantLock lock = new ReentrantLock();
    lock.lock();
    try {
      if (!lock.isHeldByCurrentThread()) throw new AssertionError("Lock ownership");
      count.incrementAndGet();
    } finally { lock.unlock(); }
    executor.shutdown();
    if (!executor.awaitTermination(0, TimeUnit.MILLISECONDS) || count.get() != 2) {
      throw new AssertionError("Executor lifecycle");
    }
    ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
    map.put(1, 1); map.put(2, 2); map.put(3, 3);
    map.forEach((key, value) -> map.remove(key));
    if (!map.isEmpty()) throw new AssertionError("Concurrent map callback traversal");
    CompletableFuture<Integer> source = new CompletableFuture<>();
    CompletionStage<Integer> minimal = source.minimalCompletionStage();
    minimal.toCompletableFuture().complete(99);
    source.complete(7);
    if (minimal.toCompletableFuture().join() != 7) throw new AssertionError("Minimal stage isolation");
    CompletableFuture<Integer> loser = new CompletableFuture<>();
    CompletableFuture.anyOf(loser, source).join();
    if (loser.getNumberOfDependents() != 0) throw new AssertionError("Race listener cleanup");
    ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    pool.getQueue().add(() -> { throw new AssertionError("Queued task must be returned unexecuted"); });
    if (pool.shutdownNow().size() != 1 || !pool.getQueue().isEmpty() || !pool.isTerminated()) {
      throw new AssertionError("Queued shutdown recovery");
    }
    return "PASS: independent root module consumed the JRE overlay";
  }
}
''')
    write("entry.js", '''goog.module('concurrent.consumer');
const ConsumerProbe = goog.require('consumer.ConsumerProbe');
goog.global['concurrentProbeResult'] = ConsumerProbe.run();
''')
    if args.prepare_only:
        print("Prepared independent consumer workspace: " + str(WORKSPACE))
        return
    # A direct invocation participates in the same host-wide lock as validate.sh.
    lock_file = None
    if os.name != "nt" and os.environ.get("J2CL_BUILD_LOCK_HELD") != "1":
        import fcntl
        lock_file = open("/tmp/j2cl-build.lock", "a")
        print("Waiting for shared WSL build lock /tmp/j2cl-build.lock", flush=True)
        fcntl.flock(lock_file, fcntl.LOCK_EX)
    subprocess.run(["bazelisk", "build", "//:application"], cwd=WORKSPACE, check=True)
    binary = subprocess.check_output(["bazelisk", "info", "bazel-bin"], cwd=WORKSPACE, text=True).strip()
    verify_patch_copy()
    command = [sys.executable, str(ROOT / "tools/browser_smoke.py"), str(Path(binary) / "application.js"),
               "--output", str(WORKSPACE / "browser")]
    if args.browser:
        command.extend(["--browser", str(args.browser)])
    subprocess.run(command, cwd=WORKSPACE, check=True)
    subprocess.run(["bazelisk", "shutdown"], cwd=WORKSPACE, check=True)
    if lock_file:
        lock_file.close()
    print("Independent consumer compilation and browser execution passed.")


if __name__ == "__main__":
    main()
