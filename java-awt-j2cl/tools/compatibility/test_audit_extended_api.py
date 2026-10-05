import copy
import pathlib
import tempfile
import unittest

from audit_extended_api import audit, declaration_issues, has_regressions


class ExtendedAuditTest(unittest.TestCase):
    def setUp(self):
        self.reference = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": ["protected void dispatch();"]}]}
        self.actual = copy.deepcopy(self.reference)
        self.manifest = {"entries": [{"type": "java.awt.Example", "supportStatus": "limited", "members": []}]}

    def test_present_protected_api_requires_review(self):
        report = audit(self.reference, self.actual, self.manifest, {})
        self.assertEqual(1, report["summary"]["unreviewedMemberCount"])
        self.assertEqual(0, report["summary"]["missingCandidateMemberCount"])
        self.assertFalse(report["summary"]["releaseReady"])
        self.assertFalse(has_regressions(report))

    def test_missing_protected_api_is_visible(self):
        self.actual["types"][0]["members"].clear()
        report = audit(self.reference, self.actual, self.manifest, {})
        self.assertEqual(1, report["summary"]["missingCandidateMemberCount"])

    def test_reviewed_supported_hook_requires_behavior_evidence(self):
        dispositions = {"members": [{"type": "java.awt.Example", "signature": "protected void dispatch();", "supportStatus": "web-mapped", "reason": "Subclass hook", "evidence": ["test.java"]}]}
        with tempfile.TemporaryDirectory() as directory:
            root = pathlib.Path(directory)
            with self.assertRaisesRegex(ValueError, "evidence"):
                audit(self.reference, self.actual, self.manifest, dispositions, root)
            (root / "test.java").touch()
            report = audit(self.reference, self.actual, self.manifest, dispositions, root)
            self.assertTrue(report["summary"]["releaseReady"])
            self.actual["types"][0]["members"].clear()
            self.assertTrue(has_regressions(audit(self.reference, self.actual, self.manifest, dispositions, root)))

    def test_source_extension_modifiers_are_not_silently_normalized(self):
        self.assertTrue(declaration_issues("public void run();", "public final void run();", True))
        self.assertTrue(declaration_issues("public void run();", "public abstract void run();", True))
        self.assertTrue(declaration_issues("public void run() throws java.io.IOException;", "public void run();", True))
        self.assertTrue(declaration_issues("public int count;", "public final int count;", False))
        self.assertEqual([], declaration_issues("public synchronized void run();", "public void run();", True))
        self.assertTrue(declaration_issues("protected void run();", "public void run();", True))

    def test_final_on_a_supported_method_is_a_development_regression(self):
        self.manifest["entries"][0]["members"] = [{"signature": "protected void dispatch();", "supportStatus": "limited"}]
        self.actual["types"][0]["members"] = ["protected final void dispatch();"]
        report = audit(self.reference, self.actual, self.manifest, {})
        self.assertEqual(0, report["summary"]["missingSupportedMemberCount"])
        self.assertTrue(has_regressions(report))

    def test_nested_types_of_omitted_platform_types_stay_omitted(self):
        self.reference["types"].append({"name": "java.awt.Example$Nested", "declaration": "public class java.awt.Example$Nested {", "members": ["protected void dispatch();"]})
        self.manifest["entries"][0]["supportStatus"] = "excluded"
        report = audit(self.reference, {"types": []}, self.manifest, {})
        self.assertTrue(report["summary"]["releaseReady"])


if __name__ == "__main__":
    unittest.main()
