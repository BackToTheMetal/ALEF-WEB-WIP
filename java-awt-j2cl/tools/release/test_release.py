import io
import json
from pathlib import Path
import sys
import subprocess
import tarfile
import tempfile
import unittest

sys.path.insert(0, str(Path(__file__).parent))
from package_source import archive_bytes, package, source_files, ROOT, ROOT_FILES, modules_in_graph
from consumer_smoke import prepare


class ReleaseTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.source = Path(self.temp.name) / "source"
        self.source.mkdir()
        for name in ROOT_FILES:
            (self.source / name).write_bytes((ROOT / name).read_bytes())
        for name in ("release/provenance.json", "tools/release/consumer/BUILD.bazel.template", "tools/release/consumer/ConsumerSmokeTest.java", "tools/release/consumer/ConsumerRuntimeProbe.java", "third_party/patches/BUILD.bazel", "third_party/patches/j2cl-type-support.patch", "third_party/patches/j2cl-invocation-support.patch", "third_party/patches/j2cl-reflection-base.patch", "samples/dual_showcase/app.js", "samples/dual_showcase/BrowserLauncher.java", "third_party/patches/elemental2-1.3.2-w3c-css.patch"):
            path = self.source / name
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes((ROOT / name).read_bytes())
        font = self.source / "samples/dual_showcase/fonts/DejaVuSans.ttf"
        font.parent.mkdir(parents=True)
        font.write_bytes(b"fixture font; no font rendering in package unit tests")

    def test_source_archive_reproducible_and_has_required_consumer_inputs(self):
        files = source_files(self.source)
        first = archive_bytes(self.source, files, "webawt-test")
        self.assertEqual(first, archive_bytes(self.source, files, "webawt-test"))
        with tarfile.open(fileobj=io.BytesIO(first)) as archive:
            names = archive.getnames()
            self.assertIn("webawt-test/third_party/patches/elemental2-1.3.2-w3c-css.patch", names)
            self.assertIn("webawt-test/LICENSE", names)
            self.assertIn("webawt-test/samples/dual_showcase/fonts/DejaVuSans.ttf", names)
            self.assertFalse(any("/out/" in name or "__pycache__" in name or "/.idea/" in name for name in names))
            self.assertTrue(all(member.mtime == 0 and member.uid == 0 for member in archive))

    def test_dependency_graph_uses_resolved_keys_not_registry_history(self):
        graph = {"key": "<root>", "dependencies": [{"key": "j2cl@20260402", "dependencies": [{"key": "elemental2@1.3.2"}]}, {"key": "j2cl@20260402", "unexpanded": True}]}
        self.assertEqual({("j2cl", "20260402"), ("elemental2", "1.3.2")}, modules_in_graph(graph))

    def test_candidate_is_immutable_and_external_consumer_uses_archive(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            graph = {"key": "j2cl@20260402"}
            candidate = package(self.source, root / "artifacts", graph)
            self.assertEqual(candidate, package(self.source, root / "artifacts", graph))
            consumer = prepare(candidate, root / "smoke")
            module = (consumer / "MODULE.bazel").read_text()
            self.assertIn("archive_override(", module)
            self.assertIn('"//third_party/patches:', module)
            self.assertEqual((self.source / "third_party/patches/elemental2-1.3.2-w3c-css.patch").read_bytes(), (consumer / "third_party/patches/elemental2-1.3.2-w3c-css.patch").read_bytes())
            for patch in ("j2cl-type-support.patch", "j2cl-invocation-support.patch", "j2cl-reflection-base.patch"):
                self.assertIn(patch, module)
                self.assertEqual((self.source / "third_party/patches" / patch).read_bytes(), (consumer / "third_party/patches" / patch).read_bytes())
            self.assertIn("ConsumerRuntimeProbe", (consumer / "app.js").read_text())
            self.assertNotIn("local_path_override", module)
            self.assertIn('"@webawt//:webawt"', (consumer / "BUILD.bazel").read_text())
            with self.assertRaises(ValueError):
                prepare(candidate, root / "smoke")
            (candidate / "sbom.cdx.json").write_text("tampered")
            with self.assertRaisesRegex(ValueError, "immutable"):
                package(self.source, root / "artifacts", graph)

    def test_modified_archive_is_rejected_before_extraction(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            candidate = package(self.source, root / "artifacts", {"key": "j2cl@20260402"})
            manifest = json.loads((candidate / "source-manifest.json").read_text())
            (candidate / manifest["sourceArchive"]).write_bytes(b"modified")
            with self.assertRaisesRegex(ValueError, "checksum"):
                prepare(candidate, root / "smoke")

    def test_requested_version_cannot_match_a_dependency_version(self):
        result = subprocess.run([sys.executable, str(ROOT / "tools/release/package_source.py"), "--expected-version", "1.3.2"], capture_output=True, text=True)
        self.assertNotEqual(0, result.returncode)
        self.assertIn("Requested version does not match MODULE.bazel", result.stderr)


if __name__ == "__main__":
    unittest.main()
