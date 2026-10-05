import io
from contextlib import redirect_stdout
import json
from pathlib import Path
import sys
import tarfile
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import mps_requirements as inventory


class RequirementsTests(unittest.TestCase):
    def test_dependencies_include_signatures_and_decorator_package(self):
        sources = {
            'gnu.trove.TMap': 'package gnu.trove; class TMap extends TBase { TCallback callback; }',
            'gnu.trove.TBase': 'package gnu.trove; class TBase {}',
            'gnu.trove.TCallback': 'package gnu.trove; interface TCallback {}',
            'gnu.trove.TUnused': 'package gnu.trove; class TUnused {}',
            'gnu.trove.decorator.TDecorator': 'package gnu.trove.decorator; import gnu.trove.*; class TDecorator { TMap map; /* TUnused */ }',
        }
        kept, deps, _ = inventory.closure(sources, ['gnu.trove.decorator.TDecorator'])
        self.assertEqual(set(sources) - {'gnu.trove.TUnused'}, set(kept))
        self.assertEqual(['gnu.trove.TBase', 'gnu.trove.TCallback'], deps['gnu.trove.TMap'])

    def test_model_aliases_generator_family_and_auxiliary_scope(self):
        with tempfile.TemporaryDirectory() as directory:
            archive = Path(directory) / 'mps.tar.gz'
            revision = (inventory.ROOT / 'third_party/mps-revision.txt').read_text().strip()
            files = {
                'languages/containers.mps': '<model><imports><import index="lib" ref="id/java:gnu.trove.decorator(runtime/)" /></imports>\n<ref to="lib:~TByteIntHashMapDecorator" /></model>',
                'languages/Queries.java': 'class Queries { String name = "gnu.trove.T" + primitive + "ArrayList"; }',
                'testbench/Test.java': 'import gnu.trove.TIntStack; class Test { TIntStack stack; }',
            }
            with tarfile.open(archive, 'w:gz') as tar:
                for path, content in files.items():
                    data = content.encode()
                    info = tarfile.TarInfo('MPS-' + revision + '/' + path)
                    info.size = len(data)
                    tar.addfile(info, io.BytesIO(data))
            profile = Path(directory) / 'profile.json'
            with patch.object(inventory, 'PROFILE', profile), redirect_stdout(io.StringIO()):
                inventory.inspect_archive(archive)
            data = json.loads(profile.read_text())
            self.assertIn('gnu.trove.decorator.TByteIntHashMapDecorator', data['roots'])
            self.assertIn('gnu.trove.TByteIntHashMap', data['retained'])
            self.assertIn('gnu.trove.TShortArrayList', data['unsupportedRoots'])
            self.assertNotIn('gnu.trove.TIntStack', data['retained'])
            self.assertEqual('auxiliary', data['evidence']['gnu.trove.TIntStack'][0]['scope'])

    def test_reintroduced_source_is_rejected(self):
        original = inventory.Path.rglob
        extra = inventory.ROOT / 'src/main/java/gnu/trove/TIntStack.java'
        def paths(path, pattern):
            return iter([*original(path, pattern), extra])
        with patch.object(inventory.Path, 'rglob', paths):
            with self.assertRaisesRegex(ValueError, 'source set differs'):
                inventory.check_profile()

    def test_removed_dependency_is_rejected(self):
        data = json.loads(inventory.PROFILE.read_text())
        data['retained'].remove('gnu.trove.THash')
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / 'profile.json'
            path.write_text(json.dumps(data))
            with patch.object(inventory, 'PROFILE', path):
                with self.assertRaisesRegex(ValueError, 'profile drift: retained'):
                    inventory.check_profile()
