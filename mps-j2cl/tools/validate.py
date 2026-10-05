#!/usr/bin/env python3
"""Build once, then serially verify sources, generators, JVM oracles and every browser suite.

Run from WSL after tools/bootstrap.py. A failed step does not hide later independent
results. A failed build blocks browser execution, preventing stale bundles from
being presented as validation of the current sources. --skip-build explicitly
allows already built bundles; --skip-generated explicitly omits regeneration checks.
"""
import argparse
from datetime import datetime, timezone
import json
import os
from pathlib import Path
import shlex
import shutil
import signal
import subprocess
import sys
import time
import zipfile

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "out"
SUMMARY = OUT / "validation-summary.json"


def timestamp():
    return datetime.now(timezone.utc).isoformat(timespec="seconds")


class Validation:
    def __init__(self, args):
        self.logs = OUT / "validation-logs"
        self.logs.mkdir(parents=True, exist_ok=True)
        self.report = {
            "schema_version": 1, "started_at": timestamp(), "workspace": str(ROOT),
            "status": "running", "requested_skips": {
                "build": args.skip_build, "generated": args.skip_generated,
            },
            "tools": {name: shutil.which(getattr(args, name)) or getattr(args, name)
                      for name in ("java", "javac", "node")},
            "steps": [],
        }
        self.save()

    def save(self):
        temporary = SUMMARY.with_suffix(".json.tmp")
        temporary.write_text(json.dumps(self.report, indent=2) + "\n", encoding="utf-8")
        temporary.replace(SUMMARY)

    def skip(self, name, reason, blocked=False):
        status = "blocked" if blocked else "skipped"
        self.report["steps"].append({"name": name, "status": status, "exit_code": None, "reason": reason})
        self.save()
        print(f"[{status.upper()}] {name}: {reason}", flush=True)

    def run(self, name, command, capture=False):
        command = [str(part) for part in command]
        log_path = self.logs / f"{len(self.report['steps']) + 1:02d}-{name}.log"
        step = {"name": name, "command": command, "started_at": timestamp(), "status": "running",
                "log": str(log_path.relative_to(ROOT))}
        self.report["steps"].append(step)
        self.save()
        print(f"\n[RUN] {name}: {shlex.join(command)}", flush=True)
        started = time.monotonic()
        process = None
        captured = []
        try:
            with log_path.open("w", encoding="utf-8") as log:
                try:
                    process = subprocess.Popen(command, cwd=ROOT, stdout=subprocess.PIPE,
                                               stderr=subprocess.STDOUT, text=True, encoding="utf-8",
                                               errors="replace", bufsize=1, start_new_session=True)
                    for line in process.stdout:
                        print(line, end="", flush=True)
                        log.write(line)
                        log.flush()
                        if capture:
                            captured.append(line)
                    step["exit_code"] = process.wait()
                except OSError as error:
                    step["exit_code"] = 127
                    step["reason"] = str(error)
                    print(str(error), file=log)
                    print(str(error), flush=True)
            step["status"] = "passed" if step["exit_code"] == 0 else "failed"
        except KeyboardInterrupt:
            step.update(status="interrupted", exit_code=130)
            if process is not None and process.poll() is None:
                os.killpg(process.pid, signal.SIGTERM)
                try:
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    os.killpg(process.pid, signal.SIGKILL)
                    process.wait()
            raise
        finally:
            step["finished_at"] = timestamp()
            step["duration_seconds"] = round(time.monotonic() - started, 3)
            self.save()
        print(f"[{step['status'].upper()}] {name} (exit {step['exit_code']})", flush=True)
        return step["exit_code"] == 0, "".join(captured)

    def finish(self, interrupted=False):
        statuses = {step["status"] for step in self.report["steps"]}
        failed = bool(statuses & {"failed", "blocked", "interrupted", "running"})
        self.report.update(finished_at=timestamp(), status=(
            "interrupted" if interrupted else "failed" if failed else
            "passed_with_skips" if "skipped" in statuses else "passed"))
        self.report["counts"] = {status: sum(step["status"] == status for step in self.report["steps"])
                                 for status in sorted(statuses)}
        self.save()
        print(f"\nValidation: {self.report['status']}; {SUMMARY}", flush=True)
        return 130 if interrupted else 1 if failed else 0


def annotations_jar(explicit):
    """Use the current build's JVM annotation artifact, never a stripped/source JAR."""
    candidates = [explicit] if explicit else sorted(
        (ROOT / "bazel-bin/external").glob("*jetbrains_annotations*/libannotations-hjar.jar"))
    for candidate in candidates:
        try:
            with zipfile.ZipFile(candidate) as jar:
                if "org/jetbrains/annotations/NotNull.class" in jar.namelist():
                    return candidate.resolve(strict=True)
        except (OSError, zipfile.BadZipFile):
            pass
    return None


def validate(args, validation):
    def python_step(name, *options):
        return validation.run(name, [sys.executable, ROOT / "tools" / (name + ".py"), *options])

    if args.skip_build:
        validation.skip("build", "--skip-build: using existing bazel-bin outputs")
        build_ok = True
    else:
        build_ok, _ = validation.run("build", ["bash", "tools/bazel.sh", "build", "--keep_going", "//:all"])

    python_step("check_sources", "--check", "--report", OUT / "source-audit.json")
    if args.skip_generated:
        validation.skip("check_generated", "--skip-generated requested")
    else:
        python_step("check_generated")

    validation.run("java_version", [args.java, "-version"])
    validation.run("javac_version", [args.javac, "-version"])
    validation.run("node_version", [args.node, "--version"])
    jvm = ["--java", args.java, "--javac", args.javac]
    annotation_path = annotations_jar(args.annotations_jar)
    validation.report["annotations_jar"] = str(annotation_path) if annotation_path else None
    validation.save()
    python_step("run_core_oracles", *jvm, "--check")
    for name, needs_annotations, options in [
        ("run_openapi_oracle", True, ["--check", "--verify-overlay"]),
        ("run_vfs_oracle", True, ["--check"] + (["--trove-jar", args.trove_jar] if args.trove_jar else [])),
        ("run_runtime_oracle", False, ["--check"]),
        ("run_regex_oracle", False, ["--check"]),
        ("run_foundation_time_oracle", False, ["--check"]),
        ("run_intellij_contracts", False, []),
        ("run_kernel_utilities_oracle", True, ["--check"]),
        ("check_formatting", False, []),
        ("run_dynamic_components_oracle", True, []),
    ]:
        if needs_annotations and annotation_path is None:
            validation.skip(name, "No compiled JetBrains annotations JAR; build first or supply --annotations-jar", blocked=True)
        else:
            annotation_options = ["--annotations-jar", annotation_path] if needs_annotations else []
            python_step(name, *jvm, *annotation_options, *options)

    for name in ["run_editor_utilities_oracle", "run_swing_utilities_oracle", "run_swing_editor_oracle", "run_swing_controls_oracle", "run_keystroke_oracle"]:
        python_step(name, "--check")

    listed, output = validation.run("browser_modes", [args.node, "tools/test_browser.mjs", "--list-modes"], capture=True)
    modes = []
    if listed:
        try:
            modes = json.loads(output)
            if (not isinstance(modes, list) or not modes or
                    any(not isinstance(mode, str) or not mode or not mode.replace("_", "").isalnum() for mode in modes) or
                    len(set(modes)) != len(modes)):
                raise ValueError("Expected a nonempty JSON array of unique browser mode names")
            validation.report["browser_modes"] = modes
            validation.save()
        except (ValueError, TypeError) as error:
            modes = []
            validation.skip("browser_mode_validation", str(error), blocked=True)
    if not modes:
        validation.skip("browser_suites", "Could not discover browser modes", blocked=True)
    for mode in modes:
        if build_ok:
            validation.run("browser_" + mode, [args.node, "tools/test_browser.mjs", mode])
        else:
            validation.skip("browser_" + mode, "Build failed; existing bundles may be stale", blocked=True)
    for name in ["test_weakset_gc", "test_foundation_gc", "test_icon_png", "test_dynamic_components", "test_editor_live"]:
        if build_ok:
            validation.run(name, [args.node, "tools/" + name + ".mjs"])
        else:
            validation.skip(name, "Build failed; existing bundles may be stale", blocked=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--skip-build", action="store_true", help="Use already built bundles; report the build as skipped")
    parser.add_argument("--skip-generated", action="store_true", help="Skip the clean-baseline generator check; report it as skipped")
    parser.add_argument("--annotations-jar", type=Path, help="Existing JVM annotations JAR; otherwise discover the current Bazel output")
    parser.add_argument("--trove-jar", type=Path, help="Override the VFS oracle's sibling Trove JAR")
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--node", default="node")
    args = parser.parse_args()
    if os.name != "posix":
        parser.error("Run in WSL/Linux; Windows users can use tools/validate.ps1")
    # All relative input paths are workspace-relative, regardless of the invoking directory.
    os.chdir(ROOT)
    validation = Validation(args)
    try:
        validate(args, validation)
    except KeyboardInterrupt:
        return validation.finish(interrupted=True)
    except Exception as error:
        validation.skip("orchestration", f"{type(error).__name__}: {error}", blocked=True)
    return validation.finish()


if __name__ == "__main__":
    sys.exit(main())
