import contextlib
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import build_support
import check_api
import test_consumer
import validate


class ApiTests(unittest.TestCase):
    def test_only_exact_reviewed_adaptations_pass(self):
        adaptations = check_api.load_adaptations()
        for key, entry in adaptations.items():
            with self.subTest(key=key):
                reference, actual = entry['reference'], entry['replacement']
                self.assertEqual('adapted', check_api.classify(key, reference, actual, adaptations)[0])
                for changed in [None, actual.replace('public ', 'protected ', 1),
                                actual.replace('public ', 'public static ', 1),
                                actual + ' throws java.lang.Exception',
                                actual.replace(' gnu.trove.', ' java.lang.String ', 1)]:
                    self.assertEqual('mismatch', check_api.classify(key, reference, changed, adaptations)[0])
                self.assertEqual('mismatch', check_api.classify(key, reference + ' throws X', actual, adaptations)[0])

    def test_unlisted_clone_is_compared_normally(self):
        key = ('gnu.trove.TIntArrayList', 'METHOD', 'clone()')
        signature = 'public java.lang.Object gnu.trove.TIntArrayList.clone()'
        adaptations = check_api.load_adaptations()
        self.assertEqual('compatible', check_api.classify(key, signature, signature, adaptations)[0])
        self.assertEqual('mismatch', check_api.classify(key, signature, signature.replace('Object', 'String'), adaptations)[0])


class CleanupTests(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        (self.root / 'out').mkdir()
        for name in ['.bazelrc', '.bazelversion']:
            (self.root / name).write_text('')
        (self.root / 'out/source-release.json').write_text(json.dumps({'archive': 'test.tar.gz', 'sha256': 'a' * 64}))

    def test_consumer_failure_cleans_up_and_removes_stale_success(self):
        for stage in ['build', 'node', 'shutdown']:
            with self.subTest(stage=stage):
                calls = []
                report = self.root / 'out/consumer-result.json'
                report.write_text('{"status":"passed"}')
                def run(command, **kwargs):
                    calls.append(command)
                    fails = stage in command
                    if fails and kwargs.get('check'):
                        raise subprocess.CalledProcessError(1, command)
                    return subprocess.CompletedProcess(command, 1 if fails else 0)
                with patch.object(test_consumer, 'ROOT', self.root), patch('subprocess.run', side_effect=run):
                    with self.assertRaises(subprocess.CalledProcessError):
                        test_consumer.run_consumer()
                self.assertIn('shutdown', calls[-1])
                self.assertFalse(report.exists())

    def test_original_failure_survives_failed_cleanup(self):
        with patch('subprocess.run', return_value=subprocess.CompletedProcess(['bazelisk'], 1)):
            with self.assertRaisesRegex(ValueError, 'original'):
                with build_support.bazel_session(self.root):
                    raise ValueError('original')

    def test_interrupt_also_shuts_down(self):
        with patch('subprocess.run', return_value=subprocess.CompletedProcess(['bazelisk'], 0)) as run:
            with self.assertRaises(KeyboardInterrupt):
                with build_support.bazel_session(self.root):
                    raise KeyboardInterrupt()
            self.assertEqual('shutdown', run.call_args.args[0][-1])

    def test_failed_full_build_cleans_up_and_never_runs_consumer(self):
        report = self.root / 'out/validation-results.json'
        report.write_text('{"status":"passed"}')
        def stage(*command):
            if command[0] == 'bazelisk':
                raise subprocess.CalledProcessError(1, command)
        with patch.object(validate, 'ROOT', self.root), patch.object(validate, 'build_lock', return_value=contextlib.nullcontext()), \
             patch.object(validate, 'run', side_effect=stage), patch.object(validate, 'run_consumer') as consumer, \
             patch('subprocess.run', return_value=subprocess.CompletedProcess(['bazelisk'], 0)) as cleanup:
            with self.assertRaises(subprocess.CalledProcessError):
                validate.main()
            consumer.assert_not_called()
            self.assertEqual('shutdown', cleanup.call_args.args[0][-1])
        self.assertFalse(report.exists())


@unittest.skipUnless(os.name == 'posix', 'Linux/WSL flock integration')
class LockTests(unittest.TestCase):
    def test_competing_process_waits_and_lock_releases_after_exception(self):
        import fcntl
        with tempfile.TemporaryDirectory() as directory:
            lock = Path(directory) / 'build.lock'
            script = ('import fcntl,sys; f=open(sys.argv[1], "a"); '
                      'print("ready", flush=True); fcntl.flock(f, fcntl.LOCK_EX); '
                      'print("acquired", flush=True)')
            with self.assertRaisesRegex(ValueError, 'release'):
                with build_support.build_lock(lock):
                    # A separate descriptor proves interoperability with flock.
                    with lock.open('a') as competitor:
                        with self.assertRaises(BlockingIOError):
                            fcntl.flock(competitor, fcntl.LOCK_EX | fcntl.LOCK_NB)
                    child = subprocess.Popen([sys.executable, '-u', '-c', script, str(lock)], stdout=subprocess.PIPE, text=True)
                    try:
                        self.assertEqual('ready', child.stdout.readline().strip())
                        self.assertIsNone(child.poll())
                        raise ValueError('release')
                    finally:
                        self.addCleanup(lambda: child.poll() is None and child.kill())
            output, _ = child.communicate(timeout=10)
            self.assertEqual(0, child.returncode)
            self.assertIn('acquired', output)


if __name__ == '__main__':
    unittest.main()
