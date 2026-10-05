#!/usr/bin/env python3
"""Inventory IntelliJ references in the exact selected MPS sources, applying root overlays.

This lexical audit intentionally complements javac/J2CL: it is not an overload,
accessibility, inherited-member, or runtime semantic checker. No raw MPS models
are read. Comments and string literals are excluded from executable references.
"""
from __future__ import annotations
import ast
import argparse
import collections
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parent.parent
IDENT = r"[A-Za-z_$][\w$]*"
IMPORT = re.compile(r"\bimport\s+(static\s+)?([\w$.*]+)\s*;")
FQ = re.compile(r"(?<![\w$])com\.intellij(?:\." + IDENT + r")+(?:\.\*)?")
DOT = re.compile(r"\b(" + IDENT + r")\.((?:" + IDENT + r"\.)*" + IDENT + r")")
NONCODE = re.compile(r'''""".*?"""|"(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*'|/\*.*?\*/|//[^\r\n]*''', re.S)


def stripped(text):
    return NONCODE.sub(lambda match: "".join("\n" if char == "\n" else " " for char in match.group()), text)


def overrides():
    tree = ast.parse((ROOT / "mps-overrides.bzl").read_text(encoding="utf-8-sig"))
    for statement in tree.body:
        if isinstance(statement, ast.Assign) and any(isinstance(target, ast.Name) and target.id == "MPS_OVERRIDES" for target in statement.targets):
            return ast.literal_eval(statement.value)
    raise ValueError("MPS_OVERRIDES assignment missing")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="fail when committed inventory differs, without rewriting files")
    arguments = parser.parse_args()
    profile = json.loads((ROOT / "compatibility/browser-projects.json").read_text(encoding="utf-8-sig"))
    replacements = overrides()
    source_projects = collections.defaultdict(set)
    for project, info in profile["projects"].items():
        for original in info["srcs"]:
            selected = "mps-web/" + replacements[original] if original in replacements else "mps/" + original
            source_projects[selected].add(project)
    providers = {}
    for source in sorted((ROOT / "third_party/intellij-stubs/src").rglob("*.java")):
        relative = source.relative_to(ROOT / "third_party/intellij-stubs/src").as_posix()
        providers[relative[:-5].replace("/", ".")] = source.relative_to(ROOT).as_posix()

    def provider(symbol):
        candidate = symbol.removesuffix(".*")
        while candidate.startswith("com.intellij."):
            if candidate in providers:
                return providers[candidate]
            candidate = candidate.rpartition(".")[0]
        return None

    records, descendants = [], {}
    for source in sorted(source_projects):
        text = (ROOT / source).read_text(encoding="utf-8-sig")
        code = stripped(text)
        imports = list(IMPORT.finditer(code))
        scope = {match.group(2).rpartition(".")[2]: match.group(2) for match in imports if not match.group(1) and not match.group(2).endswith(".*")}
        package = re.search(r"\bpackage\s+([\w.]+)\s*;", code)
        for declaration in re.finditer(r"\b(?:class|interface)\s+(" + IDENT + r")\s+(?:extends|implements)\s+(" + IDENT + r"(?:\." + IDENT + r")*)", code):
            name = (package.group(1) + "." if package else "") + declaration.group(1)
            base = declaration.group(2)
            descendants[name] = scope.get(base, base)
        records.append((source, text, code, imports, scope))

    def intellij_ancestor(symbol):
        seen = set()
        while symbol in descendants and symbol not in seen:
            seen.add(symbol)
            symbol = descendants[symbol]
        return symbol if symbol.startswith("com.intellij.") else None

    references, qualified_strings = [], []
    for source, text, code, imports, scope in records:
        projects = sorted(source_projects[source])

        def add(symbol, kind, offset, via=None):
            entry = {"source": source, "line": code.count("\n", 0, offset) + 1, "projects": projects, "kind": kind, "symbol": symbol, "provider": provider(symbol)}
            if via:
                entry["via_type"] = via
            references.append(entry)

        for imported in imports:
            symbol = imported.group(2)
            if symbol.startswith("com.intellij."):
                add(symbol, "static_import" if imported.group(1) else "type_import", imported.start())
        body = IMPORT.sub(lambda match: " " * len(match.group()), code)
        for match in FQ.finditer(body):
            add(match.group(), "fully_qualified_code", match.start())
        for match in DOT.finditer(body):
            alias, suffix = match.groups()
            imported = scope.get(alias)
            if not imported:
                continue
            ancestor = intellij_ancestor(imported)
            if ancestor:
                add(ancestor + "." + suffix, "qualified_imported_type" if imported == ancestor else "qualified_subclass", match.start(), None if imported == ancestor else imported)
        for literal in NONCODE.finditer(text):
            if literal.group().startswith('"'):
                for match in FQ.finditer(literal.group()):
                    qualified_strings.append({"source": source, "line": text.count("\n", 0, literal.start()) + 1, "value": match.group()})

    references.sort(key=lambda value: (value["source"], value["line"], value["kind"], value["symbol"]))
    kinds = dict(sorted(collections.Counter(reference["kind"] for reference in references).items()))
    unresolved = [reference for reference in references if reference["provider"] is None and not reference["symbol"].endswith(".*")]
    throwing = []
    for source in providers.values():
        text = (ROOT / source).read_text(encoding="utf-8-sig")
        for match in re.finditer(r'new\s+(?:java\.lang\.)?UnsupportedOperationException\s*\(\s*"((?:\\.|[^"\\])*)"', text):
            throwing.append({"source": source, "line": text.count("\n", 0, match.start()) + 1, "message": match.group(1)})
    output = {
        "schema_version": 1,
        "mps_revision": profile["mps_revision"],
        "scope": "Exact compatibility/browser-projects.json source selections with mps-overrides.bzl substitutions; Java only",
        "coverage": ["ordinary and static imports", "fully qualified executable references", "qualified references through imported types", "simple retained subclasses of IntelliJ types", "separate class-name string occurrences", "explicit unsupported-operation throw sites"],
        "limits": ["Lexical inventory, not Java symbol or overload resolution", "Resolved provider means the owning top-level source exists; a nested type or member may still be missing", "Instance-member calls and implicit static imports require javac/J2CL for full validation", "Wildcard imports and complex inheritance are recorded but not expanded", "Subclass-qualified references are a conservative superset and may name members declared by the MPS subclass", "Class-name strings are evidence only, not assumed executable reflective loads"],
        "retained_sources": len(records), "overlay_sources": sum(source.startswith("mps-web/") for source in source_projects),
        "provided_types": len(providers), "reference_counts": kinds,
        "unresolved_provider_references": unresolved,
        "references": references, "qualified_strings": qualified_strings,
        "unsupported_throw_sites": sorted(throwing, key=lambda value: (value["source"], value["line"])),
    }
    usage_path = ROOT / "compatibility/intellij-usage.json"
    manifest_path = ROOT / "compatibility/intellij-api.json"
    manifest = json.loads(manifest_path.read_text(encoding="utf-8-sig"))
    manifest["sources"] = [{"type": name, "source": source} for name, source in sorted(providers.items())]
    manifest["source_count"] = len(providers)
    manifest["usage_inventory"] = {"file": usage_path.relative_to(ROOT).as_posix(), "generator": "tools/inventory_intellij.py", "retained_sources": len(records), "overlay_sources": output["overlay_sources"], "reference_counts": kinds, "unresolved_provider_references": len(unresolved), "limitations": output["limits"]}
    mismatches = []
    for destination, value in ((usage_path, output), (manifest_path, manifest)):
        expected = json.dumps(value, indent=2) + "\n"
        if arguments.check:
            if not destination.exists() or destination.read_text(encoding="utf-8-sig") != expected:
                mismatches.append(destination.relative_to(ROOT).as_posix())
        else:
            destination.write_text(expected, encoding="utf-8")
    print(json.dumps({"retained_sources": len(records), "overlays": output["overlay_sources"], "providers": len(providers), "references": kinds, "unresolved_provider_references": len(unresolved), "unsupported_throw_sites": len(throwing)}))
    if mismatches:
        raise SystemExit("Stale IntelliJ inventory: " + ", ".join(mismatches))


if __name__ == "__main__":
    main()
