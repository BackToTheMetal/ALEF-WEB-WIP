#!/usr/bin/env python3
"""Audit complete initial MPS source ownership and sparse mirrored replacements."""

from __future__ import annotations

import argparse
import ast
import json
import re
import sys
from collections import defaultdict
from pathlib import Path, PurePosixPath

from inventory_mps import ROOT, dependency_closure, project_sources, read_projects
from inventory_browser import browser_projects

JAVA_TOKEN = re.compile(r"(?:[^\W\d]|[$_])(?:\w|[$])*|[{}()\[\].;]", re.UNICODE)
JAVA_NAME = re.compile(r"(?:[^\W\d]|[$_])(?:\w|[$])*\Z", re.UNICODE)
BUILD_BOUNDARIES = ("BUILD", "BUILD.bazel", "WORKSPACE", "WORKSPACE.bazel", "MODULE.bazel", "REPO.bazel")


def java_code(text: str) -> str:
    """Mask comments and literals, preserving braces only from executable syntax."""
    pattern = re.compile(r'//[^\n]*|/\*[\s\S]*?\*/|"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'')
    return pattern.sub(lambda match: "".join("\n" if c == "\n" else " " for c in match[0]), text)


def declared_types(path: Path) -> tuple[set[str], set[str]]:
    """Return top-level and named member binary names; javac checks local/anonymous types."""
    text = java_code(path.read_text(encoding="utf-8-sig"))
    package = re.search(r"\bpackage\s+([\w$.\s]+?)\s*;", text)
    prefix = re.sub(r"\s+", "", package[1]) + "." if package else ""
    tokens = JAVA_TOKEN.findall(text)
    depth, parentheses, brackets = 0, 0, 0
    top_level, names, enclosing, pending = set(), set(), [], None
    for index, token in enumerate(tokens):
        if token == "{":
            depth += 1
            if pending and (parentheses, brackets) == pending[1:]:
                enclosing.append((pending[0], depth))
                pending = None
        elif token == "}":
            depth -= 1
            while enclosing and enclosing[-1][1] > depth:
                enclosing.pop()
        elif token == "(":
            parentheses += 1
        elif token == ")":
            parentheses -= 1
        elif token == "[":
            brackets += 1
        elif token == "]":
            brackets -= 1
        elif (depth == 0 or (enclosing and depth == enclosing[-1][1])) and token in ("class", "interface", "enum", "record"):
            if index and tokens[index - 1] == ".":
                continue
            if index + 1 < len(tokens) and JAVA_NAME.fullmatch(tokens[index + 1]):
                name = (enclosing[-1][0] + "$" if enclosing else prefix) + tokens[index + 1]
                names.add(name)
                if not enclosing:
                    top_level.add(name)
                pending = (name, parentheses, brackets)
    return top_level, names


def top_level_types(path: Path) -> set[str]:
    return declared_types(path)[0]


def literal_assignments(path: Path) -> dict:
    if not path.exists():
        raise ValueError(f"Missing override manifest: {path}")
    result = {}
    for node in ast.parse(path.read_text(encoding="utf-8"), filename=str(path)).body:
        if isinstance(node, ast.Assign):
            for target in node.targets:
                if isinstance(target, ast.Name) and target.id in ("MPS_OVERRIDES", "MPS_OVERRIDE_REASONS"):
                    if target.id in result:
                        raise ValueError(f"Repeated assignment: {target.id}")
                    result[target.id] = ast.literal_eval(node.value)
    for key in ("MPS_OVERRIDES", "MPS_OVERRIDE_REASONS"):
        if not isinstance(result.get(key), dict):
            raise ValueError(f"{path}: {key} must be a literal dictionary")
    return result


def valid_relative_java(path) -> bool:
    if not isinstance(path, str) or "\\" in path or ":" in path:
        return False
    parsed = PurePosixPath(path)
    return (not parsed.is_absolute() and parsed.suffix == ".java" and
            ".." not in parsed.parts and parsed.as_posix() == path)


def audit(mps: Path, overlay: Path, overrides_file: Path) -> dict:
    projects, roots = browser_projects(mps)
    names = sorted(projects)
    sources = {name: projects[name]["srcs"] for name in names}
    errors = []
    upstream_owners = defaultdict(list)
    for name, paths in sources.items():
        if not paths and not projects[name]["resources"] and not projects[name]["deps"]:
            errors.append(f"Browser module has no sources, resources or dependencies: {name}")
        for path in paths:
            upstream_owners[path].append(name)
    for path, owners in upstream_owners.items():
        if len(owners) > 1:
            errors.append(f"Source belongs to multiple projects: {path}: {owners}")

    values = literal_assignments(overrides_file)
    overrides, reasons = values["MPS_OVERRIDES"], values["MPS_OVERRIDE_REASONS"]
    for path, replacement in overrides.items():
        if not valid_relative_java(path) or not valid_relative_java(replacement):
            errors.append(f"Replacement paths must be safe relative Java paths: {path!r} -> {replacement!r}")
            continue
        if path != replacement:
            errors.append(f"Replacement must mirror the upstream path: {path} -> {replacement}")
        if path not in upstream_owners:
            errors.append(f"Replacement does not select an initial upstream source: {path}")
        if not (mps / path).is_file():
            errors.append(f"Replacement lacks original upstream file: {path}")
        if not (overlay / replacement).is_file():
            errors.append(f"Replacement file is missing: {replacement}")
        elif (mps / path).is_file():
            original = top_level_types(mps / path)
            changed = top_level_types(overlay / replacement)
            if original != changed:
                errors.append(f"Replacement changes top-level Java types: {path}: "
                              f"original={sorted(original)}, replacement={sorted(changed)}")
        if not isinstance(reasons.get(path), str) or not reasons[path].strip():
            errors.append(f"Replacement needs a nonempty reason: {path}")
    for path in reasons.keys() - overrides.keys():
        errors.append(f"Replacement reason has no replacement: {path}")
    if overlay.exists():
        for path in sorted(overlay.rglob("*")):
            relative = path.relative_to(overlay).as_posix()
            if path.is_dir() and not any(path.iterdir()):
                errors.append(f"Empty replacement directory: {relative}")
            elif path.is_file() and path.suffix == ".iml":
                # Local IntelliJ module descriptors are metadata, never Java
                # replacements or Bazel source inputs. Preserve IDE customization.
                continue
            elif path.is_file() and (path.suffix != ".java" or relative not in overrides.values()):
                errors.append(f"Unregistered replacement file: {relative}")

    boundaries = set()
    visited = set()
    for relative in upstream_owners:
        parent = (mps / relative).parent
        while parent == mps or mps in parent.parents:
            if parent in visited:
                break
            visited.add(parent)
            boundaries.update((parent / filename).relative_to(mps).as_posix()
                              for filename in BUILD_BOUNDARIES if (parent / filename).is_file())
            if parent == mps:
                break
            parent = parent.parent
    errors.extend(f"Upstream Bazel boundary would interrupt root source selection: {path}"
                  for path in sorted(boundaries))

    emitted, top_level_count = defaultdict(list), 0
    for relative in sorted(upstream_owners):
        replacement = overrides.get(relative)
        if replacement is not None and not valid_relative_java(replacement):
            continue
        path = overlay / replacement if replacement is not None else mps / relative
        if not path.is_file():
            continue
        source = ("mps-web/" + replacement) if replacement is not None else "mps/" + relative
        top_level, declared_names = declared_types(path)
        top_level_count += len(top_level)
        for name in declared_names:
            emitted[name].append(source)
    duplicates = {name: paths for name, paths in sorted(emitted.items()) if len(paths) > 1}
    errors.extend(f"Duplicate emitted Java type: {name}: {paths}" for name, paths in duplicates.items())
    return {
        "schema_version": 1,
        "projects_checked": names,
        "roots": roots,
        "absent_declared_source_roots": sorted({root for info in projects.values() for root in info["src_roots"] if not (mps / root).is_dir()}),
        "source_selections": {name: {"mode": projects[name]["selection"], "reason": projects[name]["selection_reason"]} for name in names},
        "java_source_counts": {name: len(paths) for name, paths in sources.items()},
        "java_sources": len(upstream_owners),
        "top_level_types": top_level_count,
        "named_types": len(emitted),
        "replacements": len(overrides),
        "duplicate_types": duplicates,
        "errors": errors,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--mps", type=Path, default=ROOT / "mps")
    parser.add_argument("--overlay", type=Path, default=ROOT / "mps-web")
    parser.add_argument("--overrides", type=Path, default=ROOT / "mps-overrides.bzl")
    parser.add_argument("--check", action="store_true", help="Run the read-only audit (the default)")
    parser.add_argument("--report", type=Path, help="Optional output JSON report")
    args = parser.parse_args()
    try:
        result = audit(args.mps.resolve(), args.overlay.resolve(), args.overrides)
        if args.report:
            args.report.parent.mkdir(parents=True, exist_ok=True)
            args.report.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8", newline="\n")
        for error in result["errors"]:
            print(error, file=sys.stderr)
        print(f"MPS source audit: {len(result['projects_checked'])} projects; "
              f"{result['java_sources']} Java files; {result['named_types']} named types; "
              f"{result['replacements']} replacements; {len(result['errors'])} errors")
        return bool(result["errors"])
    except (OSError, ValueError, SyntaxError) as error:
        print(f"MPS source audit failed: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
