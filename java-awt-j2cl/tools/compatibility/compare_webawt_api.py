#!/usr/bin/env python3

import argparse
import datetime
import json
import pathlib
import re
import sys


IGNORED_MEMBER_MODIFIERS = ("abstract", "default", "final", "native", "strictfp", "synchronized")


def parse_arguments():
    parser = argparse.ArgumentParser(description="Compare the callable WebAWT API with the OpenJDK java.desktop inventory.")
    parser.add_argument("--reference", default="compatibility/openjdk-awt-api.json")
    parser.add_argument("--actual", default="compatibility/webawt-awt-api.json")
    parser.add_argument("--manifest", default="compatibility/compatibility-manifest.json")
    parser.add_argument("--json-output", default="compatibility/webawt-api-validation.json")
    parser.add_argument("--markdown-output", default="compatibility/webawt-api-validation.md")
    parser.add_argument("--fail-on-incomplete-api", "--fail-on-false-claims", dest="fail_on_incomplete_api", action="store_true", help="Fail for missing in-profile APIs, incompatible supported types, or unclassified contracts (the old option is an alias)")
    return parser.parse_args()


def load_json(path):
    return json.loads(pathlib.Path(path).read_text(encoding="utf-8-sig"))


def normalize_member(member):
    normalized = re.sub(r"\s+", " ", member.strip())
    normalized = re.sub(r" throws .+;$", ";", normalized)
    for modifier in IGNORED_MEMBER_MODIFIERS:
        normalized = re.sub(rf"\b{modifier}\s+", "", normalized)
    return normalized


def parent_names(declaration):
    header = declaration.removesuffix(" {")
    matches = re.findall(r"\b(?:extends|implements)\s+(.+?)(?=\s+implements\s+|$)", header)
    parents = []
    for match in matches:
        parents.extend(re.sub(r"<.*>", "", name).strip() for name in match.split(","))
    return [parent for parent in parents if parent]


def is_constructor(member, type_name):
    without_visibility = re.sub(r"^(?:public|protected)\s+", "", member)
    return without_visibility.startswith(f"{type_name}(")


def type_index(inventory):
    return {entry["name"]: entry for entry in inventory["types"]}


def type_shape(declaration):
    match = re.match(r"^public\s+(?P<prefix>.*?)\b(?P<kind>@interface|class|enum|interface|record)\s+(?P<name>[^\s<{]+)", declaration)
    if match is None:
        raise ValueError(f"Cannot parse public type declaration: {declaration}")
    return {
        "abstract": "abstract" in match.group("prefix").split(),
        "final": "final" in match.group("prefix").split(),
        "kind": match.group("kind"),
    }


def ancestor_names(type_name, index, cache, visiting=None):
    if type_name in cache:
        return cache[type_name]
    if visiting is None:
        visiting = set()
    if type_name in visiting or type_name not in index:
        return set()
    visiting.add(type_name)
    ancestors = set(parent_names(index[type_name]["declaration"]))
    for parent in tuple(ancestors):
        ancestors.update(ancestor_names(parent, index, cache, visiting))
    visiting.remove(type_name)
    cache[type_name] = ancestors
    return ancestors


def effective_members(type_name, index, cache, visiting=None):
    if type_name in cache:
        return cache[type_name]
    if visiting is None:
        visiting = set()
    if type_name in visiting or type_name not in index:
        return set()
    visiting.add(type_name)
    entry = index[type_name]
    members = {normalize_member(member) for member in entry["members"]}
    for parent in parent_names(entry["declaration"]):
        inherited = effective_members(parent, index, cache, visiting)
        members.update(member for member in inherited if not is_constructor(member, parent))
    visiting.remove(type_name)
    cache[type_name] = members
    return members


def manifest_index(manifest):
    return {entry["type"]: entry for entry in manifest["entries"]}


def member_manifest_index(manifest_entry):
    return {normalize_member(member["signature"]): member for member in manifest_entry["members"]}


def type_compatibility_issues(reference_entry, actual_entry, actual_ancestors):
    if actual_entry is None:
        return []
    reference_shape = type_shape(reference_entry["declaration"])
    actual_shape = type_shape(actual_entry["declaration"])
    issues = []
    if reference_shape["kind"] != actual_shape["kind"]:
        issues.append(f"kind is {actual_shape['kind']}, expected {reference_shape['kind']}")
    if not reference_shape["abstract"] and actual_shape["abstract"]:
        issues.append("actual type is abstract but the reference type is instantiable")
    if not reference_shape["final"] and actual_shape["final"] and not ("extends java.lang.Enum<" in reference_entry["declaration"] and "extends java.lang.Enum<" in actual_entry["declaration"]):
        issues.append("actual type is final but the reference type is extensible")
    for parent in parent_names(reference_entry["declaration"]):
        if parent not in actual_ancestors:
            issues.append(f"missing required parent {parent}")
    return issues


def compare_type(type_name, reference_entry, actual_entry, reference_effective, actual_effective, actual_ancestors, manifest_entry):
    actual_declared = {normalize_member(member): member for member in actual_entry["members"]} if actual_entry else {}
    reference_declared = {normalize_member(member): member for member in reference_entry["members"]}
    reference_effective_members = reference_effective.get(type_name, set())
    actual_effective_members = actual_effective.get(type_name, set())
    manifest_members = member_manifest_index(manifest_entry) if manifest_entry else {}
    member_results = []
    false_claims = []
    for normalized, reference_member in reference_declared.items():
        manifest_member = manifest_members.get(normalized)
        if actual_entry is None or normalized not in actual_effective_members:
            resolution = "missing"
        elif normalized not in actual_declared:
            resolution = "inherited"
        elif actual_declared[normalized] == reference_member:
            resolution = "exact-declaration"
        else:
            resolution = "compatible-declaration"
        result = {
            "signature": reference_member,
            "normalizedSignature": normalized,
            "resolution": resolution,
            "implementationState": manifest_member["implementationState"] if manifest_member else "unclassified",
            "supportStatus": manifest_member["supportStatus"] if manifest_member else "unclassified",
        }
        member_results.append(result)
        if resolution == "missing" and result["implementationState"] == "implemented" and result["supportStatus"] not in ("excluded", "unavailable"):
            false_claims.append(reference_member)
    actual_extras = []
    actual_redeclarations = []
    if actual_entry:
        for normalized, actual_member in actual_declared.items():
            if normalized in reference_declared:
                continue
            if normalized in reference_effective_members and not is_constructor(actual_member, type_name):
                actual_redeclarations.append(actual_member)
            else:
                actual_extras.append(actual_member)
    return {
        "type": type_name,
        "referenceDeclaration": reference_entry["declaration"],
        "actualDeclaration": actual_entry["declaration"] if actual_entry else None,
        "implementationState": manifest_entry["implementationState"] if manifest_entry else "unclassified",
        "supportStatus": manifest_entry["supportStatus"] if manifest_entry else "unclassified",
        "typeCompatibilityIssues": type_compatibility_issues(reference_entry, actual_entry, actual_ancestors.get(type_name, set())),
        "referenceDeclaredMemberCount": len(reference_declared),
        "actualDeclaredMemberCount": len(actual_declared),
        "callableReferenceMemberCount": len(reference_effective_members),
        "callableActualMemberCount": len(actual_effective_members),
        "members": member_results,
        "missingCallableMembers": [result["signature"] for result in member_results if result["resolution"] == "missing"],
        "falseImplementedClaims": false_claims,
        "compatibleDeclarationDifferences": [result["signature"] for result in member_results if result["resolution"] == "compatible-declaration"],
        "inheritedImplementations": [result["signature"] for result in member_results if result["resolution"] == "inherited"],
        "actualRedeclarations": actual_redeclarations,
        "unexpectedDeclaredMembers": actual_extras,
    }


def compare(reference, actual, manifest):
    reference_types = type_index(reference)
    actual_types = type_index(actual)
    manifest_types = manifest_index(manifest)
    reference_effective = {}
    actual_effective = {}
    actual_ancestors = {}
    for type_name in reference_types:
        effective_members(type_name, reference_types, reference_effective)
    for type_name in actual_types:
        effective_members(type_name, actual_types, actual_effective)
        ancestor_names(type_name, actual_types, actual_ancestors)
    type_results = [compare_type(type_name, reference_types[type_name], actual_types.get(type_name), reference_effective, actual_effective, actual_ancestors, manifest_types.get(type_name)) for type_name in sorted(reference_types)]
    emitted_reference_types = sorted(set(actual_types).intersection(reference_types))
    unexpected_types = sorted(set(actual_types).difference(reference_types))
    false_claims = [{"type": result["type"], "members": result["falseImplementedClaims"]} for result in type_results if result["falseImplementedClaims"]]
    false_type_claims = [{"type": result["type"], "issues": result["typeCompatibilityIssues"]} for result in type_results if result["typeCompatibilityIssues"] and result["implementationState"] == "implemented" and result["supportStatus"] not in ("excluded", "unavailable")]
    supported_type_issues = [{"type": result["type"], "issues": result["typeCompatibilityIssues"]} for result in type_results if result["typeCompatibilityIssues"] and result["supportStatus"] not in ("excluded", "unavailable")]
    missing_supported_types = [result["type"] for result in type_results if result["actualDeclaration"] is None and result["supportStatus"] not in ("excluded", "unavailable")]
    resolved_statuses = {"exact", "limited", "web-mapped", "excluded", "unavailable"}
    unclassified_contracts = [result["type"] for result in type_results if result["supportStatus"] not in resolved_statuses]
    unclassified_contracts.extend(f"{result['type']}: {member['signature']}" for result in type_results for member in result["members"] if member["supportStatus"] not in resolved_statuses)
    resolution_counts = {}
    implementation_state_coverage = {}
    support_status_coverage = {}
    for result in type_results:
        for member in result["members"]:
            resolution_counts[member["resolution"]] = resolution_counts.get(member["resolution"], 0) + 1
            update_coverage(implementation_state_coverage, member["implementationState"], member["resolution"])
            update_coverage(support_status_coverage, member["supportStatus"], member["resolution"])
    partial_gap_types = []
    for result in type_results:
        actionable_members = [member["signature"] for member in result["members"] if member["resolution"] == "missing" and member["supportStatus"] not in ("excluded", "unavailable")]
        if result["implementationState"] == "partial" and actionable_members:
            partial_gap_types.append({"type": result["type"], "missingMemberCount": len(actionable_members), "members": actionable_members})
    partial_gap_types.sort(key=lambda entry: (-entry["missingMemberCount"], entry["type"]))
    in_scope_statuses = set(support_status_coverage).difference(("excluded", "unavailable"))
    in_scope_total = sum(support_status_coverage[status]["total"] for status in in_scope_statuses)
    in_scope_callable = sum(support_status_coverage[status]["callable"] for status in in_scope_statuses)
    return {
        "schemaVersion": 1,
        "generatedAtUtc": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "summary": {
            "referenceTypeCount": len(reference_types),
            "emittedTypeCount": len(actual_types),
            "emittedReferenceTypeCount": len(emitted_reference_types),
            "unexpectedTypeCount": len(unexpected_types),
            "referenceDeclaredMemberCount": sum(len(entry["members"]) for entry in reference_types.values()),
            "actualDeclaredMemberCount": sum(len(entry["members"]) for entry in actual_types.values()),
            "resolutionCounts": resolution_counts,
            "missingSupportedTypeCount": len(missing_supported_types),
            "falseImplementedClaimTypeCount": len(false_claims),
            "falseImplementedClaimMemberCount": sum(len(claim["members"]) for claim in false_claims),
            "falseImplementedTypeClaimCount": len(false_type_claims),
            "supportedTypeCompatibilityIssueCount": len(supported_type_issues),
            "inScopeReferenceMemberCount": in_scope_total,
            "inScopeCallableMemberCount": in_scope_callable,
            "inScopeMissingMemberCount": in_scope_total - in_scope_callable,
            "partialGapTypeCount": len(partial_gap_types),
            "unclassifiedContractCount": len(unclassified_contracts),
        },
        "implementationStateCoverage": implementation_state_coverage,
        "supportStatusCoverage": support_status_coverage,
        "missingSupportedTypes": missing_supported_types,
        "unexpectedTypes": unexpected_types,
        "falseImplementedClaims": false_claims,
        "falseImplementedTypeClaims": false_type_claims,
        "supportedTypeCompatibilityIssues": supported_type_issues,
        "partialGapTypes": partial_gap_types,
        "unclassifiedContracts": unclassified_contracts,
        "types": type_results,
    }


def update_coverage(coverage, category, resolution):
    counts = coverage.setdefault(category, {"total": 0, "callable": 0, "missing": 0})
    counts["total"] += 1
    if resolution == "missing":
        counts["missing"] += 1
    else:
        counts["callable"] += 1


def api_gate_failures(report):
    metrics = {
        "missingSupportedTypeCount": "missing in-profile types",
        "inScopeMissingMemberCount": "missing in-profile members",
        "supportedTypeCompatibilityIssueCount": "incompatible supported type declarations",
        "falseImplementedClaimMemberCount": "false implemented member claims",
        "falseImplementedTypeClaimCount": "false implemented type claims",
        "unclassifiedContractCount": "unclassified or deferred contracts",
    }
    return [f"{report['summary'].get(key, 0)} {description}" for key, description in metrics.items() if report["summary"].get(key, 0)]


def markdown_report(report):
    summary = report["summary"]
    lines = [
        "# WebAWT API validation",
        "",
        f"Generated: {report['generatedAtUtc']}",
        "",
        "## Summary",
        "",
        "| Metric | Count |",
        "| --- | ---: |",
        f"| OpenJDK reference types | {summary['referenceTypeCount']} |",
        f"| Emitted WebAWT types | {summary['emittedTypeCount']} |",
        f"| OpenJDK declared members | {summary['referenceDeclaredMemberCount']} |",
        f"| WebAWT declared members | {summary['actualDeclaredMemberCount']} |",
        f"| Missing supported types | {summary['missingSupportedTypeCount']} |",
        f"| False implemented member claims | {summary['falseImplementedClaimMemberCount']} |",
        f"| False implemented type claims | {summary['falseImplementedTypeClaimCount']} |",
        f"| Supported types with declaration issues | {summary['supportedTypeCompatibilityIssueCount']} |",
        f"| In-scope callable members | {summary['inScopeCallableMemberCount']} / {summary['inScopeReferenceMemberCount']} |",
        f"| In-scope missing members | {summary['inScopeMissingMemberCount']} |",
        f"| Partial types with API gaps | {summary['partialGapTypeCount']} |",
        f"| Unclassified or deferred contracts | {summary['unclassifiedContractCount']} |",
        "",
        "This is the locked public top-level AWT inventory. Inherited means WebAWT exposes a member through a parent. Compatible declaration here uses callability normalization; it does not prove subclass or checked-exception compatibility. The extended API worklist separately audits protected members, accessible nested types, prerequisite packages, and declaration differences. Public-inventory completeness is not release readiness.",
        "",
    ]
    lines.extend(["## Coverage by implementation state", "", "| State | Callable | Missing | Total |", "| --- | ---: | ---: | ---: |"])
    for state in sorted(report["implementationStateCoverage"]):
        coverage = report["implementationStateCoverage"][state]
        lines.append(f"| {state} | {coverage['callable']} | {coverage['missing']} | {coverage['total']} |")
    lines.append("")
    if report["missingSupportedTypes"]:
        lines.extend(["## Missing supported types", ""])
        lines.extend(f"- `{type_name}`" for type_name in report["missingSupportedTypes"])
        lines.append("")
    if report["unclassifiedContracts"]:
        lines.extend(["## Unclassified or deferred contracts", ""])
        lines.extend(f"- `{contract}`" for contract in report["unclassifiedContracts"])
        lines.append("")
    if report["falseImplementedClaims"]:
        lines.extend(["## False implemented claims", ""])
        for claim in report["falseImplementedClaims"]:
            lines.append(f"### `{claim['type']}`")
            lines.append("")
            lines.extend(f"- `{member}`" for member in claim["members"])
            lines.append("")
    if report["supportedTypeCompatibilityIssues"]:
        lines.extend(["## Supported type declaration issues", ""])
        for issue in report["supportedTypeCompatibilityIssues"]:
            lines.append(f"- `{issue['type']}`: {'; '.join(issue['issues'])}")
        lines.append("")
    if report["partialGapTypes"]:
        lines.extend(["## Partial API work queue", "", "| Type | Missing members |", "| --- | ---: |"])
        for gap in report["partialGapTypes"]:
            lines.append(f"| `{gap['type']}` | {gap['missingMemberCount']} |")
        lines.append("")
    lines.extend(["## Declaration and extension warnings", ""])
    warning_found = False
    for result in report["types"]:
        warnings = []
        if result["compatibleDeclarationDifferences"]:
            warnings.append(f"compatible declarations: {len(result['compatibleDeclarationDifferences'])}")
        if result["inheritedImplementations"]:
            warnings.append(f"inherited implementations: {len(result['inheritedImplementations'])}")
        if result["actualRedeclarations"]:
            warnings.append(f"compatible redeclarations: {len(result['actualRedeclarations'])}")
        if result["unexpectedDeclaredMembers"]:
            warnings.append(f"unexpected declarations: {len(result['unexpectedDeclaredMembers'])}")
        if warnings:
            warning_found = True
            lines.append(f"- `{result['type']}`: {', '.join(warnings)}")
    if not warning_found:
        lines.append("None.")
    lines.append("")
    return "\n".join(lines)


def main():
    arguments = parse_arguments()
    report = compare(load_json(arguments.reference), load_json(arguments.actual), load_json(arguments.manifest))
    json_output = pathlib.Path(arguments.json_output)
    markdown_output = pathlib.Path(arguments.markdown_output)
    json_output.parent.mkdir(parents=True, exist_ok=True)
    markdown_output.parent.mkdir(parents=True, exist_ok=True)
    json_output.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    markdown_output.write_text(markdown_report(report), encoding="utf-8")
    summary = report["summary"]
    print(f"Compared {summary['emittedTypeCount']} emitted types with {summary['referenceTypeCount']} OpenJDK types")
    print(f"False implemented claims: {summary['falseImplementedClaimMemberCount']} members across {summary['falseImplementedClaimTypeCount']} types")
    failures = api_gate_failures(report)
    if arguments.fail_on_incomplete_api and failures:
        for failure in failures:
            print(f"ERROR: {failure}")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
