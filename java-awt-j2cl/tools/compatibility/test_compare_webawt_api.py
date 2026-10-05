#!/usr/bin/env python3

import pathlib
import sys
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).parent))
from compare_webawt_api import ancestor_names, compare, effective_members, normalize_member, parent_names, type_compatibility_issues, type_shape


class CompareWebAwtApiTest(unittest.TestCase):
    def test_normalize_member_ignores_non_callability_modifiers(self):
        self.assertEqual("public void reset();", normalize_member("public final synchronized void reset();"))
    def test_normalize_member_ignores_throws_clause(self):
        self.assertEqual("public void waitForResult();", normalize_member("public void waitForResult() throws java.lang.InterruptedException;"))
    def test_parent_names_reads_class_and_interfaces(self):
        declaration = "public class java.awt.Child extends java.awt.Parent implements java.awt.First,java.awt.Second {"
        self.assertEqual(["java.awt.Parent", "java.awt.First", "java.awt.Second"], parent_names(declaration))
    def test_effective_members_inherits_methods_but_not_constructors(self):
        index = {
            "java.awt.Parent": {
                "name": "java.awt.Parent",
                "declaration": "public class java.awt.Parent {",
                "members": ["public java.awt.Parent();", "public void inherited();"],
            },
            "java.awt.Child": {
                "name": "java.awt.Child",
                "declaration": "public class java.awt.Child extends java.awt.Parent {",
                "members": ["public java.awt.Child();"],
            },
        }
        members = effective_members("java.awt.Child", index, {})
        self.assertIn("public void inherited();", members)
        self.assertNotIn("public java.awt.Parent();", members)
    def test_type_shape_preserves_source_compatibility_modifiers(self):
        shape = type_shape("public abstract class java.awt.Example extends java.lang.Object {")
        self.assertEqual({"abstract": True, "final": False, "kind": "class"}, shape)
    def test_ancestor_names_follows_awt_inheritance(self):
        index = {
            "java.awt.Parent": {"declaration": "public class java.awt.Parent implements java.awt.Marker {"},
            "java.awt.Child": {"declaration": "public class java.awt.Child extends java.awt.Parent {"},
        }
        self.assertEqual({"java.awt.Parent", "java.awt.Marker"}, ancestor_names("java.awt.Child", index, {}))
    def test_type_compatibility_detects_restrictions_and_missing_parent(self):
        reference = {"declaration": "public class java.awt.Example implements java.awt.Marker {"}
        actual = {"declaration": "public final abstract class java.awt.Example {"}
        issues = type_compatibility_issues(reference, actual, set())
        self.assertEqual(3, len(issues))
    def test_compare_counts_callable_and_missing_members_by_state(self):
        reference = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": ["public void present();", "public void absent();"]}]}
        actual = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": ["public void present();"]}]}
        manifest = {"entries": [{"type": "java.awt.Example", "implementationState": "partial", "supportStatus": "limited", "members": [{"signature": "public void present();", "implementationState": "partial", "supportStatus": "limited"}, {"signature": "public void absent();", "implementationState": "partial", "supportStatus": "limited"}]}]}
        report = compare(reference, actual, manifest)
        self.assertEqual({"total": 2, "callable": 1, "missing": 1}, report["implementationStateCoverage"]["partial"])
        self.assertEqual(1, report["summary"]["partialGapTypeCount"])
    def test_partial_work_queue_excludes_unavailable_members(self):
        reference = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": ["public void available();", "public void unavailable();"]}]}
        actual = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": []}]}
        manifest = {"entries": [{"type": "java.awt.Example", "implementationState": "partial", "supportStatus": "limited", "members": [{"signature": "public void available();", "implementationState": "partial", "supportStatus": "limited"}, {"signature": "public void unavailable();", "implementationState": "not-started", "supportStatus": "unavailable"}]}]}
        report = compare(reference, actual, manifest)
        self.assertEqual(["public void available();"], report["partialGapTypes"][0]["members"])


if __name__ == "__main__":
    unittest.main()
