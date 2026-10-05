import pathlib
import tempfile
import unittest
from unittest.mock import patch
import subprocess

from export_webawt_api import declared_jars, find_jars


class ExportWebAwtApiTest(unittest.TestCase):
    def test_find_jars_ignores_retired_alias_outputs(self):
        with tempfile.TemporaryDirectory() as directory:
            current = pathlib.Path(directory, "libgraphics-api-j2cl.jar")
            retired = pathlib.Path(directory, "libimage-objects-j2cl.jar")
            current.touch()
            retired.touch()
            jars = find_jars([str(pathlib.Path(directory, "lib*-j2cl.jar"))], {retired.name})
            self.assertEqual([str(current)], jars)

    def test_default_export_uses_declared_outputs_instead_of_stale_files(self):
        with tempfile.TemporaryDirectory() as directory:
            current = pathlib.Path(directory, "libcurrent-j2cl.jar")
            stale = pathlib.Path(directory, "libstale-j2cl.jar")
            current.touch()
            stale.touch()
            responses = [subprocess.CompletedProcess([], 0, "//src/main/java/example:current-j2cl\n", ""), subprocess.CompletedProcess([], 0, f"{current}\n{current}\nignored.js\n", "")]
            with patch("export_webawt_api.shutil.which", return_value="bazel"), patch("export_webawt_api.subprocess.run", side_effect=responses) as run:
                self.assertEqual([str(current)], declared_jars())
                self.assertEqual("query", run.call_args_list[0].args[0][1])
                self.assertEqual("set(//src/main/java/example:current-j2cl)", run.call_args_list[1].args[0][2])

    def test_missing_declared_output_requires_a_build(self):
        with tempfile.TemporaryDirectory() as directory:
            missing = pathlib.Path(directory, "libmissing-j2cl.jar")
            responses = [subprocess.CompletedProcess([], 0, "//src/main/java/example:missing-j2cl\n", ""), subprocess.CompletedProcess([], 0, f"{missing}\n", "")]
            with patch("export_webawt_api.shutil.which", return_value="bazel"), patch("export_webawt_api.subprocess.run", side_effect=responses):
                with self.assertRaisesRegex(RuntimeError, "Build //..."):
                    declared_jars()


if __name__ == "__main__":
    unittest.main()
