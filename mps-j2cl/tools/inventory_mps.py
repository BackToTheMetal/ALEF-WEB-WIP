#!/usr/bin/env python3
"""Generate the root Bazel project catalog from the unmodified MPS descriptors."""

from __future__ import annotations

import argparse
import io
import json
import posixpath
import pprint
import subprocess
import sys
import tarfile
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
INITIAL_ROOTS = ("openapi", "kernel")


def relative_url(url: str, module_dir: str) -> str:
    """Resolve IDEA's filesystem URL without allowing paths outside MPS."""
    value = url.removeprefix("file://")
    value = value.replace("$MODULE_DIR$", module_dir).replace("$PROJECT_DIR$", ".")
    value = posixpath.normpath(value.replace("\\", "/"))
    if "$" in value or value.startswith(("/", "../")) or value == ".." or ":" in value:
        raise ValueError(f"Unsupported or escaping source path: {url!r}")
    return value


def read_projects(mps: Path) -> dict[str, dict]:
    # IDEA may customize the working descriptors for this composite workspace.
    # The Bazel catalog describes the pinned upstream revision, not those local
    # IDE modules. Read its descriptors without changing the user's checkout.
    revision = json.loads((ROOT / "source-lock.json").read_text())["mps"]["commit"]
    if source_revision(mps) != revision:
        raise ValueError("MPS HEAD differs from source-lock.json")
    inventory = ET.fromstring(subprocess.check_output(
        ["git", "-C", str(mps), "show", revision + ":.idea/modules.xml"]))
    descriptors = [relative_url(entry.attrib["filepath"], ".") for entry in
                   inventory.findall("./component[@name='ProjectModuleManager']/modules/module")]
    archive = subprocess.check_output(
        ["git", "-C", str(mps), "archive", "--format=tar", revision, "--", *descriptors])
    with tarfile.open(fileobj=io.BytesIO(archive)) as contents:
        descriptor_xml = {name: contents.extractfile(name).read() for name in descriptors}
    projects = {}
    for entry in inventory.findall("./component[@name='ProjectModuleManager']/modules/module"):
        descriptor = relative_url(entry.attrib["filepath"], ".")
        name = Path(descriptor).stem
        if name in projects:
            raise ValueError(f"Duplicate IDEA module name: {name}")
        module_dir = posixpath.dirname(descriptor) or "."
        module = ET.fromstring(descriptor_xml[descriptor])
        manager = module.find("./component[@name='NewModuleRootManager']")
        if manager is None:
            raise ValueError(f"Missing NewModuleRootManager: {descriptor}")
        sources, resources, tests, excludes = set(), set(), set(), set()
        for content in manager.findall("content"):
            for folder in content.findall("sourceFolder"):
                path = relative_url(folder.attrib["url"], module_dir)
                kind = folder.attrib.get("type", "")
                if folder.attrib.get("isTestSource") == "true" or "test" in kind:
                    tests.add(path)
                elif kind == "java-resource":
                    resources.add(path)
                else:
                    sources.add(path)
            excludes.update(relative_url(e.attrib["url"], module_dir)
                            for e in content.findall("excludeFolder"))
        deps, libraries = set(), set()
        exported_deps, exported_libraries = set(), set()
        for dependency in manager.findall("orderEntry"):
            if dependency.attrib.get("scope", "COMPILE") in ("TEST", "RUNTIME"):
                continue
            kind = dependency.attrib["type"]
            exported = "exported" in dependency.attrib
            if kind == "module":
                name_of_dependency = dependency.attrib["module-name"]
                deps.add(name_of_dependency)
                if exported:
                    exported_deps.add(name_of_dependency)
            elif kind == "library":
                name_of_library = dependency.attrib["name"]
                libraries.add(name_of_library)
                if exported:
                    exported_libraries.add(name_of_library)
            elif kind == "module-library":
                library = dependency.find("library")
                if library is not None:
                    name_of_library = (library.attrib.get("name") or
                                       "module-library:" + descriptor + ":" +
                                       ",".join(e.attrib["url"] for e in library.findall("CLASSES/root")))
                    libraries.add(name_of_library)
                    if exported:
                        exported_libraries.add(name_of_library)
        projects[name] = {
            "path": module_dir,
            "descriptor": descriptor,
            "src_roots": sorted(sources),
            "resource_roots": sorted(resources),
            "test_src_roots": sorted(tests),
            "excludes": sorted(excludes),
            "deps": sorted(deps),
            "exported_deps": sorted(exported_deps),
            "libraries": sorted(libraries),
            "exported_libraries": sorted(exported_libraries),
        }
    for name, project in projects.items():
        missing = set(project["deps"]) - projects.keys()
        if missing:
            raise ValueError(f"{name}: unresolved module dependencies: {sorted(missing)}")
    for name, project in projects.items():
        # IDEA exposes all direct dependencies to a module, then follows only
        # exported edges from those dependencies. Do not mistake this for the
        # full dependency_closure, which also includes implementation details.
        accessible, pending = set(), list(project["deps"])
        while pending:
            dependency = pending.pop()
            if dependency != name and dependency not in accessible:
                accessible.add(dependency)
                pending.extend(projects[dependency]["exported_deps"])
        project["compile_deps"] = sorted(accessible)
        compile_libraries = set(project["libraries"])
        for dependency in accessible:
            compile_libraries.update(projects[dependency]["exported_libraries"])
        project["compile_libraries"] = sorted(compile_libraries)
    return dict(sorted(projects.items()))


def dependency_closure(projects: dict, roots=INITIAL_ROOTS) -> list[str]:
    result, pending = set(), list(roots)
    while pending:
        name = pending.pop()
        if name not in projects:
            raise ValueError(f"Unknown initial module: {name}")
        if name not in result:
            result.add(name)
            pending.extend(projects[name]["deps"])
    return sorted(result)


def strongly_connected_components(projects: dict, names: list[str]) -> list[list[str]]:
    selected = set(names)
    index, indexes, lowlinks, stack, stacked, result = 0, {}, {}, [], set(), []

    def visit(name):
        nonlocal index
        indexes[name] = lowlinks[name] = index
        index += 1
        stack.append(name)
        stacked.add(name)
        for dep in projects[name]["deps"]:
            if dep not in selected:
                continue
            if dep not in indexes:
                visit(dep)
                lowlinks[name] = min(lowlinks[name], lowlinks[dep])
            elif dep in stacked:
                lowlinks[name] = min(lowlinks[name], indexes[dep])
        if lowlinks[name] == indexes[name]:
            component = []
            while True:
                dep = stack.pop()
                stacked.remove(dep)
                component.append(dep)
                if dep == name:
                    break
            result.append(sorted(component))

    for name in sorted(selected):
        if name not in indexes:
            visit(name)
    return sorted(result)


def compilation_groups(projects: dict, names: list[str]) -> dict[str, list[str]]:
    groups = {}
    for members in strongly_connected_components(projects, names):
        if len(members) == 1 and members[0] not in projects[members[0]]["deps"]:
            continue
        name = "kernel_cycle" if members == ["constraints-runtime", "kernel", "project"] else (
            "cycle_" + "__".join(n.replace("-", "_") for n in members))
        groups[name] = members
    return groups


def excluded(path: str, exclusions: list[str]) -> bool:
    return any(path == item or path.startswith(item.rstrip("/") + "/") for item in exclusions)


def project_sources(mps: Path, project: dict) -> list[str]:
    result = set()
    for root in project["src_roots"]:
        for path in (mps / root).rglob("*.java"):
            relative = path.relative_to(mps).as_posix()
            if path.is_file() and not excluded(relative, project["excludes"]):
                result.add(relative)
    return sorted(result)


def source_revision(mps: Path) -> str:
    return subprocess.check_output(
        ["git", "-C", str(mps), "rev-parse", "HEAD"], text=True).strip()


def artifacts(mps: Path) -> tuple[str, str]:
    projects = read_projects(mps)
    initial = dependency_closure(projects)
    groups = compilation_groups(projects, initial)
    revision = source_revision(mps)
    header = (
        '# Generated by tools/inventory_mps.py; do not edit.\n'
        '# Source paths are relative to mps/, not to an individual module.\n'
        '# deps/libraries preserve declarations; compile_* include exported dependency visibility.\n'
        f'# Upstream commit: {revision}\n\n')
    bzl = header + "\n\n".join(
        name + " = " + pprint.pformat(value, width=100, sort_dicts=False)
        for name, value in (("MPS_PROJECTS", projects),
                            ("MPS_INITIAL_PROJECTS", initial),
                            ("MPS_COMPILE_GROUPS", groups))) + "\n"
    counts = {name: len(project_sources(mps, projects[name])) for name in initial}
    manifest = {
        "schema_version": 2,
        "mps_revision": revision,
        "initial_roots": list(INITIAL_ROOTS),
        "initial_projects": initial,
        "compile_groups": groups,
        "initial_java_source_counts": counts,
        "projects": projects,
    }
    return bzl, json.dumps(manifest, indent=2, ensure_ascii=False) + "\n"


def write_or_check(path: Path, content: str, check: bool) -> bool:
    if check:
        if not path.exists() or path.read_text(encoding="utf-8") != content:
            print(f"STALE: {path}; run tools/inventory_mps.py", file=sys.stderr)
            return False
    else:
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8", newline="\n")
    return True


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--mps", type=Path, default=ROOT / "mps")
    parser.add_argument("--check", action="store_true", help="Fail when generated metadata differs")
    args = parser.parse_args()
    try:
        bzl, manifest = artifacts(args.mps.resolve())
        checks = [write_or_check(ROOT / "mps-projects.bzl", bzl, args.check),
                  write_or_check(ROOT / "compatibility/mps-projects.json", manifest, args.check)]
        if not all(checks):
            return 1
        data = json.loads(manifest)
        print(f"MPS inventory: {len(data['projects'])} projects; "
              f"{len(data['initial_projects'])} in initial closure; "
              f"{sum(data['initial_java_source_counts'].values())} Java files; "
              f"compile groups: {data['compile_groups']}")
        return 0
    except (OSError, ValueError, ET.ParseError, subprocess.CalledProcessError) as error:
        print(f"MPS inventory failed: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
