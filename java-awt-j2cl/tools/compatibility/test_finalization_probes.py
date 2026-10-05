import copy
import json
from pathlib import Path
import tempfile
import unittest

from audit_extended_api import audit
from check_package_ownership import audit as ownership_audit
from compare_webawt_api import type_compatibility_issues
from generate_consumer_probes import generate


class FinalizationProbeTest(unittest.TestCase):
    def test_new_j2cl_package_owner_fails_collision_audit(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            project, jre = root / "project", root / "jre"
            project.mkdir(); jre.mkdir()
            (project / "URI.java").write_text("package java.net; class URI {}")
            (jre / "Object.java").write_text("package java.lang; class Object {}")
            self.assertEqual([], ownership_audit(project, jre)["collisions"])
            (jre / "URL.java").write_text("package java.net; class URL {}")
            self.assertEqual(["java.net"], ownership_audit(project, jre)["collisions"])

    def test_enum_constant_subclasses_do_not_make_consumer_enum_extensible(self):
        reference = {"declaration": "public class a.E extends java.lang.Enum<a.E> {"}
        actual = {"declaration": "public final class a.E extends java.lang.Enum<a.E> {"}
        self.assertEqual([], type_compatibility_issues(reference, actual, {"java.lang.Enum"}))
        reference["declaration"] = "public class a.E {"
        actual["declaration"] = "public final class a.E {"
        self.assertTrue(type_compatibility_issues(reference, actual, set()))

    def test_probes_keep_generics_varargs_mutable_fields_and_protected_overrides(self):
        signatures = ["protected java.awt.Example();", "protected int field;", "protected <T extends java.util.EventListener> T[] listeners(java.lang.Class<T>);", "public void images(java.lang.String...);", "public void excluded();"]
        reference = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": signatures}]}
        report = {"types": [{"type": "java.awt.Example", "supportStatus": "limited", "members": [{"signature": sig, "supportStatus": "unavailable" if "excluded" in sig else "limited"} for sig in signatures]}]}
        files, coverage = generate(reference, report)
        text = next(iter(files.values()))
        self.assertIn("receiver.field = value", text)
        self.assertIn("(java.lang.String[]) null", text)
        self.assertIn("protected <T extends java.util.EventListener> T[] listeners(java.lang.Class<T> arg0)", text)
        self.assertNotIn("excluded", text)
        self.assertEqual(4, coverage["callProbeCount"])

    def test_unavailable_parent_cannot_mask_an_available_or_unrelated_parent(self):
        reference = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example implements java.io.Externalizable {", "members": []}]}
        actual = {"types": [{"name": "java.awt.Example", "declaration": "public class java.awt.Example {", "members": []}]}
        manifest = {"entries": [{"type": "java.awt.Example", "supportStatus": "limited", "members": []}]}
        dispositions = {"unavailableParents": [{"type": "java.awt.Example", "parent": "java.io.Externalizable", "reason": "owned missing type", "evidence": ["proof.md"]}]}
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory); (root / "compatibility").mkdir(); (root / "proof.md").touch()
            report = root / "compatibility/package-ownership-report.json"
            report.write_text(json.dumps({"missingTypesInOwnedPackages": []}))
            with self.assertRaisesRegex(ValueError, "absent"):
                audit(reference, actual, manifest, dispositions, root)
            report.write_text(json.dumps({"missingTypesInOwnedPackages": ["java.io.Externalizable"]}))
            self.assertTrue(audit(reference, actual, manifest, dispositions, root)["summary"]["releaseReady"])
            dispositions["unavailableParents"][0]["parent"] = "java.io.Serializable"
            with self.assertRaisesRegex(ValueError, "Unknown"):
                audit(reference, actual, manifest, dispositions, root)
