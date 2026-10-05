import copy
import json
import pathlib
import subprocess
import sys
import tempfile
import unittest

from compare_webawt_api import api_gate_failures, compare
from build_completeness_worklist import build_worklist


class ApiGateTest(unittest.TestCase):
    def setUp(self):
        self.signature = "public int getX();"
        self.reference = {"types": [{"name": "java.awt.Component", "package": "java.awt", "declaration": "public class java.awt.Component {", "members": [self.signature]}]}
        self.actual = copy.deepcopy(self.reference)
        self.manifest = {"entries": [{"type": "java.awt.Component", "supportStatus": "web-mapped", "implementationState": "partial", "members": [{"signature": self.signature, "supportStatus": "web-mapped", "implementationState": "partial"}]}]}
        self.evidence = {"implementedTypes": [], "partialTypes": ["java.awt.Component"]}

    def worklist(self, report):
        return build_worklist(pathlib.Path("."), self.reference, report, self.manifest, {"dependencies": []}, self.evidence)

    def test_partial_member_removal_fails_both_gates(self):
        self.actual["types"][0]["members"].clear()
        report = compare(self.reference, self.actual, self.manifest)
        self.assertEqual(0, report["summary"]["falseImplementedClaimMemberCount"])
        self.assertIn("1 missing in-profile members", api_gate_failures(report))
        worklist = self.worklist(report)
        self.assertEqual(1, worklist["summary"]["missingTargetMemberCount"])
        self.assertTrue(any("Missing or unresolved in-profile member" in issue for issue in worklist["violations"]))

    def test_partial_type_extension_restriction_fails_both_gates(self):
        self.actual["types"][0]["declaration"] = "public final class java.awt.Component {"
        report = compare(self.reference, self.actual, self.manifest)
        self.assertEqual(0, report["summary"]["inScopeMissingMemberCount"])
        self.assertIn("1 incompatible supported type declarations", api_gate_failures(report))
        self.assertTrue(any("Incompatible in-profile type" in issue for issue in self.worklist(report)["violations"]))

    def test_removed_parent_fails_gate(self):
        self.reference["types"][0]["declaration"] = "public class java.awt.Component implements java.io.Serializable {"
        report = compare(self.reference, self.actual, self.manifest)
        self.assertIn("1 incompatible supported type declarations", api_gate_failures(report))

    def test_missing_type_cannot_hide_behind_not_started_state(self):
        self.actual["types"].clear()
        self.manifest["entries"][0]["implementationState"] = "not-started"
        self.manifest["entries"][0]["members"][0]["implementationState"] = "not-started"
        report = compare(self.reference, self.actual, self.manifest)
        self.assertIn("1 missing in-profile types", api_gate_failures(report))

    def test_unclassified_existing_member_fails_gate(self):
        self.manifest["entries"][0]["members"].clear()
        report = compare(self.reference, self.actual, self.manifest)
        self.assertEqual(0, report["summary"]["inScopeMissingMemberCount"])
        self.assertIn("1 unclassified or deferred contracts", api_gate_failures(report))

    def test_worklist_rejects_truncated_validation(self):
        report = compare(self.reference, self.actual, self.manifest)
        report["types"][0]["members"].clear()
        self.assertTrue(any("member inventories disagree" in issue for issue in self.worklist(report)["violations"]))

    def test_worklist_rejects_stale_disposition(self):
        report = compare(self.reference, self.actual, self.manifest)
        report["types"][0]["members"][0]["supportStatus"] = "unavailable"
        self.assertTrue(any("disposition disagree" in issue for issue in self.worklist(report)["violations"]))

    def test_compatible_partial_api_passes_both_gates(self):
        report = compare(self.reference, self.actual, self.manifest)
        self.assertEqual([], api_gate_failures(report))
        self.assertEqual([], self.worklist(report)["violations"])

    def test_current_and_legacy_cli_options_fail_and_preserve_reports(self):
        self.actual["types"][0]["members"].clear()
        with tempfile.TemporaryDirectory() as directory:
            root = pathlib.Path(directory)
            for name, data in (("reference", self.reference), ("actual", self.actual), ("manifest", self.manifest)):
                (root / f"{name}.json").write_text(json.dumps(data), encoding="utf-8")
            for option in ("--fail-on-incomplete-api", "--fail-on-false-claims"):
                with self.subTest(option=option):
                    result = subprocess.run([sys.executable, str(pathlib.Path(__file__).with_name("compare_webawt_api.py")), "--reference", str(root / "reference.json"), "--actual", str(root / "actual.json"), "--manifest", str(root / "manifest.json"), "--json-output", str(root / "report.json"), "--markdown-output", str(root / "report.md"), option], capture_output=True, text=True)
                    self.assertEqual(1, result.returncode, result.stdout + result.stderr)
                    self.assertIn("missing in-profile members", result.stdout)
                    self.assertEqual(1, json.loads((root / "report.json").read_text())["summary"]["inScopeMissingMemberCount"])
                    self.assertIn("Partial API work queue", (root / "report.md").read_text())


if __name__ == "__main__":
    unittest.main()
