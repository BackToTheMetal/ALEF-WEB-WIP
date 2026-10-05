#!/usr/bin/env python3
"""Generate real Metalava NIO stubs, validate them, and optionally transpile with J2CL."""
import argparse
from datetime import datetime, timezone
import fcntl
import hashlib
import json
import os
import re
import shutil
import signal
import subprocess
import sys
import tempfile
import time
import urllib.request
from pathlib import Path

from prepare_jdk import prepare as prepare_jdk
from prepare_j2cl import prepare as prepare_j2cl
from check_api import check as check_api

ROOT = Path(__file__).resolve().parents[1]
REPORTS = ROOT / "reports"


def run(command, log, cwd=ROOT):
    print("Running:", " ".join(map(str, command[:7])), "...", flush=True)
    started = time.monotonic()
    with log.open("w") as output:
        result = subprocess.run(list(map(str, command)), cwd=cwd, stdout=output, stderr=subprocess.STDOUT)
    if result.returncode:
        print(log.read_text()[-10000:])
        raise RuntimeError(f"Command failed ({result.returncode}); see {log.relative_to(ROOT)}")
    return round(time.monotonic() - started, 2)


def reset_output(folder):
    folder = folder.resolve()
    if not folder.is_relative_to(ROOT) or folder == ROOT:
        raise ValueError("Refusing to reset output outside the trial")
    allowed = [ROOT / "build/raw-classes", ROOT / "build/raw-stage", ROOT / "stubs/raw", ROOT / "stubs/full-input"]
    if folder not in [p.resolve() for p in allowed]:
        raise ValueError("Not a managed output directory: " + str(folder))
    if folder.exists():
        shutil.rmtree(folder)
    folder.mkdir(parents=True)


def source_archive(jdk, supplied):
    if supplied:
        return supplied.resolve()
    local = jdk / "lib/src.zip"
    if local.is_file():
        return local.resolve()
    pin = json.loads((ROOT / "reference.json").read_text())
    if hashlib.sha256((jdk / "jmods/java.base.jmod").read_bytes()).hexdigest() != pin["java_base_jmod_sha256"]:
        raise RuntimeError("The selected JDK differs from the pinned reference. Supply its matching --source-zip explicitly.")
    cache = ROOT / ".cache/jdk-source"
    archive = cache / "extracted/usr/lib/jvm/openjdk-21/lib/src.zip"
    if archive.exists() and hashlib.sha256(archive.read_bytes()).hexdigest() == pin["source_zip_sha256"]:
        return archive
    cache.mkdir(parents=True, exist_ok=True)
    package = cache / "openjdk-source.deb"
    urllib.request.urlretrieve(pin["source_package_url"], package)
    if hashlib.sha256(package.read_bytes()).hexdigest() != pin["source_package_sha256"]:
        raise RuntimeError("JDK source package checksum mismatch")
    run(["dpkg-deb", "--extract", package, cache / "extracted"], REPORTS / "source-extraction.log")
    if hashlib.sha256(archive.read_bytes()).hexdigest() != pin["source_zip_sha256"]:
        raise RuntimeError("JDK source archive checksum mismatch")
    return archive


def generate(args):
    (REPORTS / "generation.json").write_text(json.dumps({"status": "RUNNING"}) + "\n")
    jdk = args.jdk_home.resolve()
    java = jdk / "bin/java"
    javac = jdk / "bin/javac"
    source = source_archive(jdk, args.source_zip)
    prepare_jdk(jdk, source)
    if not args.skip_tool_setup:
        launcher = os.environ.get("METALAVA_GRADLE", "")
        command = [launcher] if launcher else ["bash", str(ROOT / "gradlew")]
        run(command + ["--no-daemon", "--max-workers=1", "--console=plain",
                       "--gradle-user-home", ROOT / ".cache/gradle", "installMetalava"], REPORTS / "tool-setup.log")
    libs = ROOT / "build/metalava/lib"
    if not (libs / "metalava-1.0.0-alpha15.jar").exists():
        raise RuntimeError("Pinned Metalava installation is missing")
    command = [java, "-Xmx1536m", "-cp", str(libs / "*"), "com.android.tools.metalava.Driver", "--no-color"]
    run(command + ["--version"], REPORTS / "metalava-version.txt")
    stage = ROOT / "build/raw-stage"
    reset_output(stage)
    inputs = (ROOT / "build/jdk/nio-sources.list").read_text().splitlines()
    metalava_args = ["main", "--source-path", "build/jdk/sources/java.base", "--classpath", "build/jdk/java-base.jar",
                    "--java-source", "21", "--api", "reports/nio-api.txt", "--stubs", "build/raw-stage",
                    "--exclude-documentation-from-stubs", "--warning", "AddedSealed", *inputs]
    (REPORTS / "metalava-arguments.json").write_text(json.dumps(metalava_args, indent=2) + "\n")
    duration = run(command + metalava_args, REPORTS / "metalava.log")
    raw = ROOT / "stubs/raw"
    reset_output(raw)
    shutil.copytree(stage, raw, dirs_exist_ok=True)
    sources = sorted(raw.rglob("*.java"))
    if not sources:
        raise RuntimeError("Metalava emitted no source files")
    policy = json.loads((ROOT / "policy.json").read_text())
    for path in sources:
        package = path.parent.relative_to(raw).as_posix().replace("/", ".")
        if package not in policy["packages"]:
            raise RuntimeError("Unexpected output package: " + package)
    # Keep copied OpenJDK copyright notices and the matching distribution license.
    licenses = ROOT / "licenses"
    licenses.mkdir(exist_ok=True)
    system_license = Path("/usr/share/doc/openjdk-21-jre-headless/copyright")
    if system_license.exists():
        shutil.copyfile(system_license, licenses / "openjdk-copyright.txt")
    assembly = jdk / "legal/java.base/ASSEMBLY_EXCEPTION"
    if assembly.exists():
        shutil.copyfile(assembly, licenses / "ASSEMBLY_EXCEPTION")
    classes = ROOT / "build/raw-classes"
    reset_output(classes)
    argfile = ROOT / "build/raw-sources.list"
    argfile.write_text("\n".join('"' + str(p) + '"' for p in sources) + "\n")
    run([javac, "-encoding", "UTF-8", "--enable-preview", "--source", "21", "--patch-module",
         "java.base=" + str(raw), "-d", classes, "@" + str(argfile)], REPORTS / "javac-raw.log")
    tools = ROOT / "build/reference-tools"
    tools.mkdir(exist_ok=True)
    run([javac, "-d", tools, ROOT.parent / "tools/ExtractApi.java"], REPORTS / "reference-compile.log")
    run([java, "-cp", tools, "ExtractApi"], REPORTS / "jdk-api.json")
    jar = ROOT / "build/raw-stubs.jar"
    run([jdk / "bin/jar", "--create", "--file", jar, "-C", classes, "."], REPORTS / "jar.log")
    api = check_api(jar, REPORTS / "raw-api-check.json")
    run([sys.executable, ROOT / "scripts/test_api_gate.py"], REPORTS / "api-negative-control.log")
    run([javac, "-d", tools, ROOT / "scripts/InspectMetadata.java"], REPORTS / "metadata-compile.log")
    run([java, "-cp", tools, "InspectMetadata", ROOT / "build/jdk/sources/java.base"], REPORTS / "reference-metadata.tsv")
    run([java, "-cp", tools, "InspectMetadata", raw], REPORTS / "raw-metadata.tsv")
    def metadata(path):
        values = {}
        for line in path.read_text().splitlines():
            parts = line.split("\t")
            values[(parts[0], parts[1])] = parts[2:] or [True]
        return values
    expected = metadata(REPORTS / "reference-metadata.tsv")
    actual = metadata(REPORTS / "raw-metadata.tsv")
    visible = {c["name"] for c in json.loads((REPORTS / "jdk-api.json").read_text())["classes"]}
    differences = [{"kind": kind, "type": name, "reference": value, "generated": actual.get((kind, name))}
                   for (kind, name), value in expected.items()
                   if name in visible and value != actual.get((kind, name))]
    (REPORTS / "metadata-differences.json").write_text(json.dumps({
        "status": "DIFFERENCES_RECORDED" if differences else "MATCH",
        "scope": "Sealed modifiers and enum declaration order, compared via javac syntax trees",
        "differences": differences}, indent=2) + "\n")
    prepare_j2cl(jdk)
    data = {"status": "PASS", "input_mode": "matching JDK sources with complete java.base classpath",
            "java_files": len(sources), "top_level_type_files": sum(p.name != "package-info.java" for p in sources),
            "packages": policy["packages"], "generation_seconds": duration,
            "raw_javac": "PASS", "api_members_checked": api["members_checked"],
            "raw_tree_sha256": hashlib.sha256(b"".join(p.relative_to(raw).as_posix().encode() + b"\0" + p.read_bytes() for p in sources)).hexdigest(),
            "limitations": ["Metalava emits unsealed buffer classes (AddedSealed warnings).",
                            "Object-valued constants can be null; enum order can change.",
                            'Concrete stub bodies throw RuntimeException("Stub!"); abstract declarations remain abstract.',
                            "Constructor bodies also throw, including exception types and subclass scaffolding."]}
    (REPORTS / "generation.json").write_text(json.dumps(data, indent=2) + "\n")
    print(json.dumps(data, indent=2), flush=True)


def browser_test(javascript, supplied):
    candidates = sorted(Path.home().glob(".cache/ms-playwright/chromium_headless_shell-*/chrome-headless-shell-linux*/chrome-headless-shell"))
    browser = supplied or (candidates[-1] if candidates else None)
    if not browser:
        raise RuntimeError("Pass --browser with a Chromium executable")
    with tempfile.TemporaryDirectory(prefix="metalava-trial-") as directory:
        page = Path(directory)
        shutil.copyfile(javascript, page / "application.js")
        (page / "index.html").write_text('''<!doctype html><meta charset="utf-8"><pre id="result">WAITING</pre>
<script>window.onerror=function(m){document.getElementById('result').textContent='FAIL: '+m;};</script>
<script src="application.js"></script>
<script>document.getElementById('result').textContent=window.metalavaTrialResult||'FAIL: missing result';</script>''')
        process = subprocess.Popen([str(browser), "--headless", "--no-sandbox", "--disable-dev-shm-usage",
                                    "--disable-gpu", "--no-first-run", "--disable-background-networking",
                                    "--user-data-dir=" + str(page / "profile"), "--dump-dom", (page / "index.html").as_uri()],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, start_new_session=True)
        try:
            stdout, stderr = process.communicate(timeout=45)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGKILL)
            stdout, stderr = process.communicate()
    (REPORTS / "browser-dom.html").write_text(stdout)
    (REPORTS / "browser.log").write_text(stderr)
    match = re.search(r'<pre id="result">([^<]+)</pre>', stdout)
    message = match[1] if match else "Missing result"
    data = {"status": "PASS" if process.returncode == 0 and message.startswith("PASS:") else "FAIL",
            "result": message, "browser": str(browser), "javascript_sha256": hashlib.sha256(javascript.read_bytes()).hexdigest()}
    (REPORTS / "browser-check.json").write_text(json.dumps(data, indent=2) + "\n")
    if data["status"] != "PASS":
        raise RuntimeError("Browser check failed: " + message)
    return data


def j2cl(args):
    (REPORTS / "j2cl-check.json").write_text(json.dumps({"status": "RUNNING"}) + "\n")
    workspace = ROOT / "j2cl-workspace"
    print("Waiting for shared WSL build lock /tmp/j2cl-build.lock", flush=True)
    with open("/tmp/j2cl-build.lock", "a") as lock:
        fcntl.flock(lock, fcntl.LOCK_EX)
        duration = run(["bazelisk", "build", "//:application"], REPORTS / "j2cl-build.log", workspace)
        binary = Path(subprocess.check_output(["bazelisk", "info", "bazel-bin"], cwd=workspace, text=True).strip())
        api = check_api(binary / "external/j2cl+/jre/java/libjre_bootclasspath-hjar.jar", REPORTS / "j2cl-api-check.json")
        browser = browser_test(binary / "application.js", args.browser)
    data = {"status": "PASS", "build_seconds": duration, "api_members_checked": api["members_checked"],
            "browser": browser, "j2cl_version": "20260402", "scope": "152 Metalava top-level NIO replacements in the JRE; 4 existing charset types preserved"}
    (REPORTS / "j2cl-check.json").write_text(json.dumps(data, indent=2) + "\n")
    print(json.dumps(data, indent=2), flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jdk-home", type=Path, default=Path("/usr/lib/jvm/java-21-openjdk-amd64"))
    parser.add_argument("--source-zip", type=Path)
    parser.add_argument("--browser", type=Path)
    parser.add_argument("--phase", choices=["generate", "j2cl", "all"], default="all")
    parser.add_argument("--skip-tool-setup", action="store_true", help="Use the already-installed pinned Metalava jars")
    args = parser.parse_args()
    # Use one consistent tool environment for all phases. Changing PATH only during
    # generation invalidates Bazel actions when switching between 'all' and 'j2cl'.
    java_bin = str(args.jdk_home.resolve() / "bin")
    os.environ["PATH"] = os.pathsep.join([java_bin] + [p for p in os.environ["PATH"].split(os.pathsep) if p != java_bin])
    REPORTS.mkdir(exist_ok=True)
    state = {"status": "RUNNING", "phase": args.phase,
             "started_utc": datetime.now(timezone.utc).isoformat()}
    state_file = REPORTS / "run.json"
    state_file.write_text(json.dumps(state, indent=2) + "\n")
    try:
        if args.phase in ["generate", "all"]:
            generate(args)
        if args.phase in ["j2cl", "all"]:
            j2cl(args)
        state["status"] = "PASS"
    except Exception as error:
        state.update(status="FAIL", error=str(error))
        for name in ["generation.json", "j2cl-check.json"]:
            report = REPORTS / name
            if report.exists() and json.loads(report.read_text()).get("status") == "RUNNING":
                report.write_text(json.dumps({"status": "FAIL", "error": str(error)}, indent=2) + "\n")
        raise
    finally:
        state["finished_utc"] = datetime.now(timezone.utc).isoformat()
        state_file.write_text(json.dumps(state, indent=2) + "\n")


if __name__ == "__main__":
    main()
