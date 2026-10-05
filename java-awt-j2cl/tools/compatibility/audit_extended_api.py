#!/usr/bin/env python3

"""Expose the extension/prerequisite backlog separately from the locked public-only baseline."""

import argparse
import json
import pathlib
import re

from compare_webawt_api import ancestor_names, is_constructor, load_json, normalize_member, type_compatibility_issues


SUPPORTED = {"exact", "limited", "web-mapped"}
OMITTED = {"excluded", "unavailable"}


def member_key(signature):
    return re.sub(r"^protected\b", "public", normalize_member(signature))


def effective_declarations(name, types, cache, visiting=None):
    if name in cache:
        return cache[name]
    visiting = set() if visiting is None else visiting
    if name not in types or name in visiting:
        return {}
    from compare_webawt_api import parent_names
    members = {}
    for parent in parent_names(types[name]["declaration"]):
        members.update({key: value for key, value in effective_declarations(parent, types, cache, visiting | {name}).items() if not is_constructor(value, parent)})
    members.update({member_key(member): member for member in types[name]["members"]})
    cache[name] = members
    return members


def declaration_issues(reference, actual, extensible):
    issues = []
    ref_modifiers = reference.split("(", 1)[0].split()
    actual_modifiers = actual.split("(", 1)[0].split()
    if "final" not in ref_modifiers and "final" in actual_modifiers and (extensible or "(" not in reference):
        issues.append("Added final restricts overriding or field assignment")
    if "abstract" not in ref_modifiers and "abstract" in actual_modifiers:
        issues.append("Concrete member became abstract")
    if reference.startswith("public ") and actual.startswith("protected "):
        issues.append("Public member became protected")
    if extensible and reference.startswith("protected ") and actual.startswith("public ") and "(" in reference and len(ref_modifiers) > 2:
        issues.append("Protected method became public, restricting existing protected overrides")
    def exceptions(signature):
        match = re.search(r" throws (.+);$", signature)
        return set(match.group(1).split(",")) if match else set()
    if exceptions(reference) != exceptions(actual):
        issues.append("Throws declarations differ; check caller catch clauses and subclass overrides with compile probes")
    return issues


def review_overrides(dispositions, reference, workspace):
    reference_types = {entry["name"]: entry for entry in reference["types"]}
    types = {}
    members = {}
    for entry in dispositions.get("types", []):
        name = entry["type"]
        if name not in reference_types or name in types:
            raise ValueError(f"Unknown or duplicate extended type disposition: {name}")
        types[name] = entry
    for entry in dispositions.get("members", []):
        name = entry["type"]
        key = (name, member_key(entry["signature"]))
        if name not in reference_types or entry["signature"] not in reference_types[name]["members"] or key in members:
            raise ValueError(f"Unknown or duplicate extended member disposition: {key}")
        members[key] = entry
    for entry in list(types.values()) + list(members.values()):
        if entry.get("supportStatus") not in SUPPORTED | OMITTED or not entry.get("reason", "").strip():
            raise ValueError(f"Extended disposition requires a resolved status and reason: {entry['type']}")
        if entry["supportStatus"] in SUPPORTED:
            if not entry.get("evidence") or any(not (workspace / path).is_file() for path in entry["evidence"]):
                raise ValueError(f"Supported extended disposition requires existing evidence files: {entry['type']}")
    return types, members


def audit(reference, actual, manifest, dispositions, workspace=pathlib.Path(".")):
    reference_types = {entry["name"]: entry for entry in reference["types"]}
    actual_types = {entry["name"]: entry for entry in actual["types"]}
    baseline = {entry["type"]: entry for entry in manifest["entries"]}
    type_overrides, member_overrides = review_overrides(dispositions, reference, workspace)
    unavailable_parents = {}
    for entry in dispositions.get("unavailableParents", []):
        name, parent = entry["type"], entry["parent"]
        key = (name, parent)
        from compare_webawt_api import parent_names
        if key in unavailable_parents or name not in reference_types or parent not in parent_names(reference_types[name]["declaration"]):
            raise ValueError("Unknown or duplicate unavailable parent: " + str(key))
        if not entry.get("reason") or not entry.get("evidence") or any(not (workspace / file).is_file() for file in entry["evidence"]):
            raise ValueError("Unavailable parent requires rationale and existing evidence")
        ownership = load_json(workspace / "compatibility/package-ownership-report.json")
        if parent not in ownership["missingTypesInOwnedPackages"]:
            raise ValueError("Unavailable parent must be absent from a J2CL-owned package: " + parent)
        unavailable_parents[key] = entry
    actual_effective = {}
    ancestors = {}
    results = []
    for name, expected in sorted(reference_types.items()):
        actual_type = actual_types.get(name)
        base = baseline.get(name, {})
        root_status = baseline.get(name.split("$")[0], {}).get("supportStatus")
        type_status = base.get("supportStatus", root_status if root_status in OMITTED else "review-required")
        override = type_overrides.get(name)
        if override:
            if base:
                raise ValueError(f"Use the locked public manifest to change baseline type {name}")
            type_status = override["supportStatus"]
        issues = type_compatibility_issues(expected, actual_type, ancestor_names(name, actual_types, ancestors)) if actual_type else []
        omitted_parents = [parent for owner, parent in unavailable_parents if owner == name]
        issues = [issue for issue in issues if issue not in {"missing required parent " + parent for parent in omitted_parents}]
        if actual_type and expected.get("enclosingType"):
            if expected.get("static") != actual_type.get("static"):
                issues.append("Nested type static declaration differs")
            if expected.get("visibility") == "public" and actual_type.get("visibility") != "public":
                issues.append("Public nested type is no longer public")
        expected_members = {member_key(entry["signature"]): entry for entry in base.get("members", [])}
        actual_members = effective_declarations(name, actual_types, actual_effective)
        members = []
        extensible = "final" not in expected["declaration"].split("class", 1)[0].split()
        for signature in expected["members"]:
            key = member_key(signature)
            known = expected_members.get(key)
            status = known["supportStatus"] if known else type_status if type_status in OMITTED else "review-required"
            override = member_overrides.get((name, key))
            if override:
                if known:
                    raise ValueError(f"Use the locked public manifest to change baseline member {name}: {signature}")
                status = override["supportStatus"]
            declaration = actual_members.get(key)
            source_issues = declaration_issues(signature, declaration, extensible) if declaration else []
            members.append({"signature": signature, "supportStatus": status, "present": declaration is not None, "actualSignature": declaration, "declarationIssues": source_issues, "explicitlyReviewed": override is not None})
        results.append({"type": name, "supportStatus": type_status, "emitted": actual_type is not None, "declarationIssues": issues, "unavailableParents": omitted_parents, "members": members})
    candidates = [entry for entry in results if entry["supportStatus"] not in OMITTED]
    candidate_members = [member for entry in candidates for member in entry["members"] if member["supportStatus"] not in OMITTED]
    summary = {
        "referenceTypeCount": len(reference_types),
        "emittedTypeCount": len(actual_types),
        "unreviewedTypeCount": sum(entry["supportStatus"] == "review-required" for entry in candidates),
        "unreviewedMemberCount": sum(member["supportStatus"] == "review-required" for member in candidate_members),
        "missingCandidateTypeCount": sum(not entry["emitted"] for entry in candidates),
        "missingCandidateMemberCount": sum(not member["present"] for member in candidate_members),
        "typeDeclarationReviewCount": sum(bool(entry["declarationIssues"]) for entry in candidates),
        "memberDeclarationReviewCount": sum(bool(member["declarationIssues"]) for member in candidate_members),
        "missingSupportedTypeCount": sum(not entry["emitted"] and entry["supportStatus"] in SUPPORTED for entry in candidates),
        "missingSupportedMemberCount": sum(not member["present"] and member["supportStatus"] in SUPPORTED for member in candidate_members),
        "supportedTypeDeclarationFailureCount": sum(bool(entry["declarationIssues"]) and entry["supportStatus"] in SUPPORTED for entry in candidates),
        "supportedMemberDeclarationFailureCount": sum(any(not issue.startswith("Throws declarations differ") for issue in member["declarationIssues"]) and member["supportStatus"] in SUPPORTED for member in candidate_members),
        "reviewedMemberDeclarationFailureCount": sum(bool(member["declarationIssues"]) and member["supportStatus"] in SUPPORTED and member["explicitlyReviewed"] for member in candidate_members),
    }
    summary["releaseReady"] = not any(value for key, value in summary.items() if key not in {"referenceTypeCount", "emittedTypeCount"})
    return {"schemaVersion": 1, "summary": summary, "types": results}


def has_regressions(report):
    return any(report["summary"][key] for key in ("missingSupportedTypeCount", "missingSupportedMemberCount", "supportedTypeDeclarationFailureCount", "supportedMemberDeclarationFailureCount", "reviewedMemberDeclarationFailureCount"))


def markdown(report):
    summary = report["summary"]
    lines = ["# Extended API finalization worklist", "", "This audit includes protected members, accessible nested types, and the shipped prerequisite type families. It extends the older public-only AWT inventory. Missing candidates need implementation or an explicit reviewed profile disposition; they are not automatically classified as supported or unavailable. Declaration differences require source-compatibility review and consumer compilation probes.", "", "| Metric | Count |", "| --- | ---: |"]
    lines.extend(f"| {key} | {value} |" for key, value in summary.items())
    lines.extend(["", "The API-profile completion gate requires all reviews and retained-contract gaps to be closed. Run `audit_extended_api.py --require-complete` to enforce it. A passing API gate does not complete the manual acceptance and release-engineering work in FINALIZATION.md.", "", "## Work queue", ""])
    for entry in report["types"]:
        if entry["supportStatus"] in OMITTED:
            continue
        pending = [member for member in entry["members"] if member["supportStatus"] not in OMITTED and (not member["present"] or member["supportStatus"] == "review-required" or member["declarationIssues"])]
        if not pending and not entry["declarationIssues"] and entry["supportStatus"] != "review-required":
            continue
        lines.extend([f"### {entry['type']}", "", f"Type disposition: {entry['supportStatus']}; emitted: {entry['emitted']}.", ""])
        lines.extend(f"- Type declaration: {issue}" for issue in entry["declarationIssues"])
        for member in pending:
            state = "present" if member["present"] else "missing"
            details = "; ".join(member["declarationIssues"])
            lines.append(f"- `{member['signature']}` — {state}; {member['supportStatus']}" + (f"; {details}" if details else ""))
        lines.append("")
    return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--reference", default="compatibility/openjdk-extended-api.json")
    parser.add_argument("--actual", default="compatibility/webawt-extended-api.json")
    parser.add_argument("--manifest", default="compatibility/compatibility-manifest.json")
    parser.add_argument("--dispositions", default="compatibility/extended-api-dispositions.json")
    parser.add_argument("--require-complete", action="store_true", help="Release gate: reject outstanding reviews, candidate gaps, and declaration differences")
    arguments = parser.parse_args()
    report = audit(load_json(arguments.reference), load_json(arguments.actual), load_json(arguments.manifest), load_json(arguments.dispositions))
    pathlib.Path("compatibility/extended-api-worklist.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    pathlib.Path("compatibility/extended-api-worklist.md").write_text(markdown(report), encoding="utf-8")
    print("Extended API audit: " + json.dumps(report["summary"], sort_keys=True))
    return 1 if has_regressions(report) or arguments.require_complete and not report["summary"]["releaseReady"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
