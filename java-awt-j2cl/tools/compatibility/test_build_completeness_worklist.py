#!/usr/bin/env python3

import pathlib
import sys
import tempfile
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).parent))
from build_completeness_worklist import build_worklist, member_disposition


class CompletenessWorklistTest(unittest.TestCase):
    def test_supported_missing_j2cl_signature_is_a_violation(self):
        with tempfile.TemporaryDirectory() as directory:
            report = build_worklist(pathlib.Path(directory), self.reference(), self.validation("limited"), self.manifest("limited"), self.dependencies("missing-in-j2cl-owned-package"), self.implementation_status())
            self.assertTrue(any("missing J2CL-owned signature type" in issue for issue in report["violations"]))
    def test_unavailable_missing_j2cl_signature_is_resolved(self):
        with tempfile.TemporaryDirectory() as directory:
            report = build_worklist(pathlib.Path(directory), self.reference(), self.validation("unavailable"), self.manifest("unavailable"), self.dependencies("missing-in-j2cl-owned-package"), self.implementation_status())
            self.assertEqual(0, report["summary"]["violationCount"])
            self.assertEqual(1, report["summary"]["unavailableMemberCount"])
    def test_missing_unclaimed_type_becomes_project_prerequisite(self):
        with tempfile.TemporaryDirectory() as directory:
            member = self.validation("limited")["types"][0]["members"][0]
            disposition = member_disposition(pathlib.Path(directory), member, {"java.example.Missing": "external-unclaimed-package"})
            self.assertEqual("requires-project-type", disposition["disposition"])
    def test_emitted_type_requires_explicit_evidence(self):
        with tempfile.TemporaryDirectory() as directory:
            validation = self.validation("limited")
            validation["types"][0]["actualDeclaration"] = "public class java.awt.Example {"
            report = build_worklist(pathlib.Path(directory), self.reference(), validation, self.manifest("limited"), self.dependencies("stock-j2cl"), self.implementation_status())
            self.assertTrue(any("lacks explicit implementation evidence" in issue for issue in report["violations"]))
    @staticmethod
    def reference():
        return {"types": [{"name": "java.awt.Example", "package": "java.awt", "declaration": "public class java.awt.Example {", "members": ["public void use(java.example.Missing);"]}]}
    @staticmethod
    def validation(support_status):
        member = {"signature": "public void use(java.example.Missing);", "supportStatus": support_status, "implementationState": "not-started", "resolution": "missing"}
        return {"types": [{"type": "java.awt.Example", "actualDeclaration": None, "implementationState": "not-started", "supportStatus": support_status, "members": [member]}]}
    @staticmethod
    def manifest(support_status):
        member = {"signature": "public void use(java.example.Missing);", "supportStatus": support_status, "implementationState": "not-started"}
        return {"entries": [{"type": "java.awt.Example", "supportStatus": support_status, "implementationState": "not-started", "members": [member]}]}
    @staticmethod
    def dependencies(classification):
        return {"dependencies": [{"type": "java.example.Missing", "classification": classification}]}
    @staticmethod
    def implementation_status():
        return {"implementedTypes": [], "partialTypes": []}


if __name__ == "__main__":
    unittest.main()
