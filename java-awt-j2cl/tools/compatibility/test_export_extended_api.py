import pathlib
import subprocess
import tempfile
import unittest

from api_classfile import accessible_classes, class_metadata
from export_extended_api import inspect_classes


class ExtendedApiExportTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.directory = tempfile.TemporaryDirectory()
        root = pathlib.Path(cls.directory.name)
        source = root / "Example.java"
        source.write_text("""package fixture;
public class Example {
    protected Example() {}
    protected void dispatch() {}
    public static class PublicNested {}
    protected class ProtectedNested {}
    private static class Hidden { public static class PublicInsideHidden {} }
    public Runnable local() { return new Runnable() { public void run() {} }; }
}
""", encoding="utf-8")
        subprocess.run(["javac", "-d", str(root), str(source)], check=True, capture_output=True, text=True)
        cls.classes = {"fixture." + path.stem: path.read_bytes() for path in (root / "fixture").glob("*.class")}

    @classmethod
    def tearDownClass(cls):
        cls.directory.cleanup()

    def test_respects_nested_visibility_and_enclosing_reachability(self):
        accessible = accessible_classes(self.classes)
        self.assertEqual({"fixture.Example", "fixture.Example$PublicNested", "fixture.Example$ProtectedNested"}, set(accessible))
        self.assertEqual("protected", accessible["fixture.Example$ProtectedNested"]["visibility"])
        self.assertFalse(accessible["fixture.Example$ProtectedNested"]["static"])
        self.assertTrue(accessible["fixture.Example$PublicNested"]["static"])

    def test_includes_protected_constructors_and_methods_from_bytecode(self):
        types = {entry["name"]: entry for entry in inspect_classes(self.classes, "javap")}
        self.assertIn("protected fixture.Example();", types["fixture.Example"]["members"])
        self.assertIn("protected void dispatch();", types["fixture.Example"]["members"])
        self.assertIn("protected fixture.Example$ProtectedNested(fixture.Example);", types["fixture.Example$ProtectedNested"]["members"])

    def test_invalid_or_truncated_classes_fail_loudly(self):
        with self.assertRaisesRegex(ValueError, "Not a Java class"):
            class_metadata(b"nope")
        with self.assertRaisesRegex(ValueError, "Truncated"):
            class_metadata(self.classes["fixture.Example"][:30])


if __name__ == "__main__":
    unittest.main()
