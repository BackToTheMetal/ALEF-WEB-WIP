"""Shared Linux/WSL build coordination and exception-safe Bazel lifetime."""
from contextlib import contextmanager
from pathlib import Path
import signal
import subprocess
import sys

LOCK_PATH = Path('/tmp/j2cl-build.lock')


@contextmanager
def build_lock(path=LOCK_PATH):
    import fcntl  # Validation runs on Linux/WSL, never in the browser library.
    print('Waiting for shared J2CL build lock:', path, flush=True)
    with Path(path).open('a') as lock:
        fcntl.flock(lock, fcntl.LOCK_EX)
        print('Acquired shared J2CL build lock.', flush=True)
        previous = signal.getsignal(signal.SIGTERM)
        def terminate(signum, frame):
            raise SystemExit(128 + signum)
        signal.signal(signal.SIGTERM, terminate)
        try:
            yield
        finally:
            signal.signal(signal.SIGTERM, previous)
            fcntl.flock(lock, fcntl.LOCK_UN)


@contextmanager
def bazel_session(workspace, startup=()):
    try:
        yield
    finally:
        failed = sys.exc_info()[0] is not None
        try:
            result = subprocess.run(['bazelisk', *startup, 'shutdown'], cwd=workspace, check=False)
            if result.returncode:
                if not failed:
                    raise subprocess.CalledProcessError(result.returncode, result.args)
                print('Bazel shutdown failed during error cleanup.', file=sys.stderr)
        except OSError:
            if not failed:
                raise
            print('Unable to invoke Bazel shutdown during error cleanup.', file=sys.stderr)


def invalidate_results(root, names):
    for name in names:
        (Path(root) / 'out' / name).unlink(missing_ok=True)
