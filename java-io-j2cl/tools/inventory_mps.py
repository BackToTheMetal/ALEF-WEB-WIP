#!/usr/bin/env python3
"""Create a deterministic inventory of java.io references in an MPS checkout."""

from __future__ import annotations

import argparse
import json
import re
import subprocess
from collections import defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_OUTPUT = ROOT / "compatibility" / "mps-java-io-usage.json"
IMPORT = re.compile(r"(?m)^\s*import\s+java\.io\.([A-Za-z_$][\w$]*|\*)")
QUALIFIED = re.compile(r"\bjava\.io\.([A-Z][\w$]*)")


def tracked_sources(checkout: Path) -> list[Path]:
    try:
        result = subprocess.run(
            ["git", "-C", str(checkout), "ls-files", "-z", "--", "*.java", "*.kt"],
            check=True,
            capture_output=True,
        )
        return [checkout / name.decode("utf-8") for name in result.stdout.split(b"\0") if name]
    except (FileNotFoundError, subprocess.CalledProcessError):
        ignored = {".git", ".gradle", ".idea", "build", "out", "target"}
        return sorted(
            path
            for suffix in ("*.java", "*.kt")
            for path in checkout.rglob(suffix)
            if not ignored.intersection(path.relative_to(checkout).parts)
        )


def matching_lines(checkout: Path, sources: list[Path]):
    """Yield (path, line) pairs, using Git's index instead of opening every source."""
    try:
        result = subprocess.run(
            [
                "git",
                "-C",
                str(checkout),
                "grep",
                "-I",
                "-n",
                "-E",
                r"(^[[:space:]]*import[[:space:]]+java[.]io[.]|java[.]io[.][A-Z])",
                "--",
                "*.java",
                "*.kt",
            ],
            capture_output=True,
            text=True,
        )
        if result.returncode not in (0, 1):
            result.check_returncode()
        for raw_line in result.stdout.splitlines():
            relative, _, line = raw_line.split(":", 2)
            yield relative, line
        return
    except (FileNotFoundError, subprocess.CalledProcessError, ValueError):
        pass

    for source in sources:
        if not source.is_file():
            continue
        relative = source.relative_to(checkout).as_posix()
        for line in source.read_text(encoding="utf-8", errors="replace").splitlines():
            if "java.io." in line:
                yield relative, line


def git_revision(checkout: Path) -> str | None:
    try:
        return subprocess.run(
            ["git", "-C", str(checkout), "rev-parse", "HEAD"],
            check=True,
            capture_output=True,
            text=True,
        ).stdout.strip()
    except (FileNotFoundError, subprocess.CalledProcessError):
        return None


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--mps-root", type=Path, required=True)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    args = parser.parse_args()

    checkout = args.mps_root.resolve()
    if not checkout.is_dir():
        raise SystemExit(f"MPS checkout does not exist: {checkout}")

    imports: dict[str, int] = defaultdict(int)
    qualified: dict[str, int] = defaultdict(int)
    files: dict[str, set[str]] = defaultdict(set)
    sources = tracked_sources(checkout)
    for relative, line in matching_lines(checkout, sources):
        imported = False
        for match in IMPORT.finditer(line):
            imported = True
            name = match.group(1)
            imports[name] += 1
            files[name].add(relative)
        if imported:
            continue
        for match in QUALIFIED.finditer(line):
            name = match.group(1)
            qualified[name] += 1
            files[name].add(relative)

    names = sorted(set(imports) | set(qualified))
    result = {
        "schemaVersion": 1,
        "source": {
            "checkout": str(checkout),
            "gitRevision": git_revision(checkout),
            "scannedJavaAndKotlinFiles": len(sources),
        },
        "types": {
            name: {
                "importCount": imports[name],
                "qualifiedReferenceCount": qualified[name],
                "files": sorted(files[name]),
            }
            for name in names
        },
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {len(names)} java.io names from {len(sources)} source files to {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
