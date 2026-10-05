#!/usr/bin/env python3
"""Create a byte-for-byte reproducible source release ZIP."""

from __future__ import annotations

import argparse
import hashlib
import io
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "out" / "java-io-j2cl-source.zip"
CHECKSUM = ROOT / "out" / "java-io-j2cl-source.sha256"
ROOT_FILES = (
    ".bazelrc",
    ".bazelversion",
    ".gitignore",
    "BUILD.bazel",
    "IMPLEMENTATION_PLAN.md",
    "IMPLEMENTATION_STATUS.md",
    "LICENSE",
    "MODULE.bazel",
    "MODULE.bazel.lock",
    "README.md",
    "THIRD_PARTY_NOTICES.md",
)
SOURCE_DIRECTORIES = ("compatibility", "docs", "overlay", "tests", "third_party", "tools")
FIXED_TIMESTAMP = (1980, 1, 1, 0, 0, 0)


def release_files() -> list[Path]:
    files = [ROOT / name for name in ROOT_FILES]
    for directory in SOURCE_DIRECTORIES:
        files.extend(path for path in (ROOT / directory).rglob("*") if path.is_file())
    return sorted(set(files), key=lambda path: path.relative_to(ROOT).as_posix())


def render() -> bytes:
    buffer = io.BytesIO()
    with zipfile.ZipFile(
        buffer, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9, strict_timestamps=True
    ) as archive:
        for source in release_files():
            relative = source.relative_to(ROOT).as_posix()
            entry = zipfile.ZipInfo(f"java-io-j2cl/{relative}", FIXED_TIMESTAMP)
            entry.compress_type = zipfile.ZIP_DEFLATED
            entry.external_attr = 0o100644 << 16
            archive.writestr(entry, source.read_bytes())
    return buffer.getvalue()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()

    content = render()
    digest = hashlib.sha256(content).hexdigest()
    checksum = f"{digest}  {OUTPUT.name}\n"
    if args.check:
        if not OUTPUT.exists() or OUTPUT.read_bytes() != content:
            raise SystemExit("release archive is stale; run tools/package_release.py")
        if not CHECKSUM.exists() or CHECKSUM.read_text(encoding="utf-8") != checksum:
            raise SystemExit("release checksum is stale; run tools/package_release.py")
        print(f"Release archive is reproducible: {digest}")
        return 0

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_bytes(content)
    CHECKSUM.write_text(checksum, encoding="utf-8", newline="\n")
    print(f"Wrote {OUTPUT} ({len(content)} bytes, sha256 {digest})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
