#!/usr/bin/env python3

import json
import pathlib
import sys
import tempfile
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).parent))
from check_bundle_budgets import evaluate_bundles, measure_bundle
from check_production_dependencies import EXPECTED_BAZEL_DEPENDENCIES, EXPECTED_BAZEL_VERSION, EXPECTED_TEST_ARTIFACTS, validate


class ProductionDependencyPolicyTest(unittest.TestCase):
    def setUp(self):
        self.temporary_directory = tempfile.TemporaryDirectory()
        self.workspace = pathlib.Path(self.temporary_directory.name)
        self.write_valid_workspace()
    def tearDown(self):
        self.temporary_directory.cleanup()
    def write_valid_workspace(self):
        bazel_dependencies = "\n".join(f'bazel_dep(name = "{name}", version = "{version}")' for name, version in EXPECTED_BAZEL_DEPENDENCIES.items())
        test_artifacts = "\n".join(f'        "{name}:{version}",' for name, version in EXPECTED_TEST_ARTIFACTS.items())
        module_text = f'{bazel_dependencies}\nmaven.install(\n    artifacts = [\n{test_artifacts}\n    ],\n)\n'
        (self.workspace / "MODULE.bazel").write_text(module_text, encoding="utf-8")
        (self.workspace / ".bazelversion").write_text(EXPECTED_BAZEL_VERSION + "\n", encoding="utf-8")
        (self.workspace / "MODULE.bazel.lock").write_text('{"lockFileVersion": 18}\n', encoding="utf-8")
        artifacts = {name: {"version": version, "shasums": {"jar": "a" * 64}} for name, version in EXPECTED_TEST_ARTIFACTS.items()}
        (self.workspace / "rules_jvm_external++maven+webawt_test_maven_install.json").write_text(json.dumps({"artifacts": artifacts}), encoding="utf-8")
        source_directory = self.workspace / "src" / "main" / "java" / "org" / "webawt"
        source_directory.mkdir(parents=True)
        (source_directory / "Example.java").write_text("package org.webawt;\nimport java.util.List;\n", encoding="utf-8")
        (source_directory / "BUILD.bazel").write_text('load("@j2cl//build_defs:rules.bzl", "j2cl_library")\n', encoding="utf-8")
        (self.workspace / "samples").mkdir()
    def test_accepts_pinned_production_and_test_dependencies(self):
        self.assertEqual([], validate(self.workspace))
    def test_rejects_unapproved_production_import(self):
        source_file = self.workspace / "src" / "main" / "java" / "org" / "webawt" / "Example.java"
        source_file.write_text("package org.webawt;\nimport com.example.Library;\n", encoding="utf-8")
        self.assertTrue(any("unapproved production type" in violation for violation in validate(self.workspace)))
    def test_rejects_test_maven_repository_in_production_build(self):
        build_file = self.workspace / "src" / "main" / "java" / "org" / "webawt" / "BUILD.bazel"
        build_file.write_text('java_library(name = "example", deps = ["@webawt_test_maven//:junit_junit"])\n', encoding="utf-8")
        self.assertTrue(any("unapproved production repository" in violation for violation in validate(self.workspace)))


class BundleBudgetTest(unittest.TestCase):
    def test_measurement_is_deterministic(self):
        with tempfile.TemporaryDirectory() as directory:
            bundle_path = pathlib.Path(directory) / "application.js"
            bundle_path.write_bytes(b"const value = 42;\n" * 100)
            self.assertEqual(measure_bundle(bundle_path), measure_bundle(bundle_path))
    def test_reports_budget_violation(self):
        with tempfile.TemporaryDirectory() as directory:
            workspace = pathlib.Path(directory)
            bundle_path = workspace / "bundle.js"
            bundle_path.write_bytes(b"x" * 100)
            configuration = {"bundles": [{"name": "test", "path": "bundle.js", "maxRawBytes": 99, "maxGzipBytes": 100}]}
            report, violations = evaluate_bundles(workspace, configuration)
            self.assertEqual(100, report["bundles"][0]["rawBytes"])
            self.assertEqual(1, len(violations))


if __name__ == "__main__":
    unittest.main()
