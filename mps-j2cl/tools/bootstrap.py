#!/usr/bin/env python3
"""Create the locked MPS checkout, or verify an existing clean one without resetting it."""
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOCK = json.loads((ROOT / "source-lock.json").read_text())["mps"]
CHECKOUT = ROOT / "mps"


def git(*args, cwd=CHECKOUT):
    return subprocess.check_output(["git", *args], cwd=cwd, text=True).strip()


def main():
    if not CHECKOUT.exists():
        git("clone", "--depth", "1", "--filter=blob:none", "--no-checkout",
            LOCK["url"], "mps", cwd=ROOT)
        git("fetch", "--depth", "1", "origin", LOCK["commit"])
        git("-c", "core.autocrlf=false", "checkout", "--detach", LOCK["commit"])
    if not (CHECKOUT / ".git").exists():
        raise SystemExit("mps exists but is not the managed Git checkout")
    if git("rev-parse", "HEAD") != LOCK["commit"]:
        raise SystemExit("MPS revision differs from source-lock.json; no files were reset")
    if git("status", "--porcelain"):
        raise SystemExit("MPS checkout is modified; preserve those changes before continuing")
    print("Verified clean MPS checkout at " + LOCK["commit"])
    subprocess.run([sys.executable, str(ROOT / 'tools/prepare_dependency_views.py')], cwd=ROOT, check=True)
    subprocess.run([sys.executable, str(ROOT / 'tools/fetch_j2cl.py')], cwd=ROOT, check=True)


if __name__ == "__main__":
    main()
