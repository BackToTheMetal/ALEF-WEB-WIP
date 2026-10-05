#!/usr/bin/env python3

import argparse
import json
import pathlib
import re


EXPECTED_BAZEL_DEPENDENCIES = {
    "elemental2": "1.3.2",
    "j2cl": "20260402",
    "rules_closure": "0.16.0",
    "rules_jvm_external": "6.7",
}
EXPECTED_BAZEL_VERSION = "8.3.1"
EXPECTED_TEST_ARTIFACTS = {
    "com.microsoft.playwright:playwright": "1.62.0",
    "junit:junit": "4.13.2",
}
ALLOWED_PRODUCTION_IMPORT_PREFIXES = ("elemental2.", "java.", "javax.", "jsinterop.annotations.", "org.webawt.")
ALLOWED_PRODUCTION_REPOSITORIES = {"elemental2", "j2cl", "rules_closure"}
SHA256_PATTERN = re.compile(r"[0-9a-f]{64}")


def parse_bazel_dependencies(module_text):
    dependencies = {}
    for block in re.findall(r"bazel_dep\s*\((.*?)\)", module_text, re.DOTALL):
        name_match = re.search(r"\bname\s*=\s*\"([^\"]+)\"", block)
        version_match = re.search(r"\bversion\s*=\s*\"([^\"]+)\"", block)
        if name_match and version_match:
            dependencies[name_match.group(1)] = version_match.group(1)
    return dependencies


def parse_test_artifacts(module_text):
    install_match = re.search(r"maven\.install\s*\((.*?)\n\)", module_text, re.DOTALL)
    if not install_match:
        return {}
    artifacts_match = re.search(r"\bartifacts\s*=\s*\[(.*?)\]", install_match.group(1), re.DOTALL)
    if not artifacts_match:
        return {}
    artifacts = {}
    for coordinate in re.findall(r"\"([^\"]+)\"", artifacts_match.group(1)):
        parts = coordinate.split(":")
        if len(parts) == 3:
            artifacts[":".join(parts[:2])] = parts[2]
    return artifacts


def validate_imports(workspace):
    violations = []
    source_root = workspace / "src" / "main" / "java"
    import_pattern = re.compile(r"^\s*import\s+(?:static\s+)?([A-Za-z0-9_.]+)(?:\.\*)?;", re.MULTILINE)
    for source_file in sorted(source_root.rglob("*.java")):
        source_text = source_file.read_text(encoding="utf-8")
        for imported_type in import_pattern.findall(source_text):
            if not imported_type.startswith(ALLOWED_PRODUCTION_IMPORT_PREFIXES):
                violations.append(f"{source_file.relative_to(workspace)} imports unapproved production type {imported_type}")
    return violations


def validate_build_labels(workspace):
    violations = []
    repository_pattern = re.compile(r"@([A-Za-z0-9_+.-]+)//")
    for root_name in ("src/main/java", "samples"):
        root = workspace / root_name
        for build_file in sorted(path for path in root.rglob("*") if path.name in {"BUILD", "BUILD.bazel"}):
            repositories = set(repository_pattern.findall(build_file.read_text(encoding="utf-8")))
            for repository in sorted(repositories - ALLOWED_PRODUCTION_REPOSITORIES):
                violations.append(f"{build_file.relative_to(workspace)} uses unapproved production repository @{repository}")
    return violations


def validate_maven_lock(workspace):
    violations = []
    lock_path = workspace / "rules_jvm_external++maven+webawt_test_maven_install.json"
    if not lock_path.is_file():
        return [f"Missing Maven lock file {lock_path.name}"]
    try:
        lock = json.loads(lock_path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as error:
        return [f"Invalid Maven lock file {lock_path.name}: {error}"]
    artifacts = lock.get("artifacts", {})
    for artifact_name, artifact in sorted(artifacts.items()):
        jar_sha = artifact.get("shasums", {}).get("jar", "")
        if not SHA256_PATTERN.fullmatch(jar_sha):
            violations.append(f"Maven lock artifact {artifact_name} has no valid SHA-256 jar checksum")
    for artifact_name, expected_version in EXPECTED_TEST_ARTIFACTS.items():
        actual_version = artifacts.get(artifact_name, {}).get("version")
        if actual_version != expected_version:
            violations.append(f"Maven lock artifact {artifact_name} expected {expected_version} but found {actual_version}")
    return violations


def validate(workspace):
    workspace = pathlib.Path(workspace)
    violations = []
    module_path = workspace / "MODULE.bazel"
    if not module_path.is_file():
        return ["Missing MODULE.bazel"]
    module_text = module_path.read_text(encoding="utf-8")
    actual_dependencies = parse_bazel_dependencies(module_text)
    if actual_dependencies != EXPECTED_BAZEL_DEPENDENCIES:
        violations.append(f"MODULE.bazel dependencies must be exactly {EXPECTED_BAZEL_DEPENDENCIES}, found {actual_dependencies}")
    actual_artifacts = parse_test_artifacts(module_text)
    if actual_artifacts != EXPECTED_TEST_ARTIFACTS:
        violations.append(f"Test Maven artifacts must be exactly {EXPECTED_TEST_ARTIFACTS}, found {actual_artifacts}")
    bazel_version_path = workspace / ".bazelversion"
    actual_bazel_version = bazel_version_path.read_text(encoding="utf-8").strip() if bazel_version_path.is_file() else None
    if actual_bazel_version != EXPECTED_BAZEL_VERSION:
        violations.append(f".bazelversion expected {EXPECTED_BAZEL_VERSION} but found {actual_bazel_version}")
    module_lock_path = workspace / "MODULE.bazel.lock"
    if not module_lock_path.is_file():
        violations.append("Missing MODULE.bazel.lock")
    else:
        try:
            module_lock = json.loads(module_lock_path.read_text(encoding="utf-8"))
            if not isinstance(module_lock.get("lockFileVersion"), int):
                violations.append("MODULE.bazel.lock has no numeric lockFileVersion")
        except json.JSONDecodeError as error:
            violations.append(f"Invalid MODULE.bazel.lock: {error}")
    violations.extend(validate_maven_lock(workspace))
    violations.extend(validate_imports(workspace))
    violations.extend(validate_build_labels(workspace))
    return violations


def main():
    parser = argparse.ArgumentParser(description="Enforce WebAWT production dependency and lock-file policy")
    parser.add_argument("--workspace", default=".", help="WebAWT workspace root")
    arguments = parser.parse_args()
    violations = validate(pathlib.Path(arguments.workspace).resolve())
    if violations:
        for violation in violations:
            print(f"ERROR: {violation}")
        return 1
    print("Production dependency policy passed: pinned J2CL and Elemental2 only; JUnit and Playwright remain test-only.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
