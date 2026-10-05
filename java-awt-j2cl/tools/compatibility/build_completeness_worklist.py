#!/usr/bin/env python3

import argparse
import json
import pathlib
import re


TARGET_SUPPORT_STATUSES = {"exact", "limited", "web-mapped"}
OUT_OF_PROFILE_SUPPORT_STATUSES = {"excluded", "unavailable"}
KNOWN_AVAILABLE_NESTED_TYPES = {"java.util.Map$Entry"}
QUALIFIED_TYPE_PATTERN = re.compile(r"\b(?:java|javax)\.[A-Za-z0-9_.$]+")


def load_json(path):
    return json.loads(pathlib.Path(path).read_text(encoding="utf-8-sig"))


def referenced_types(signature):
    return sorted(set(QUALIFIED_TYPE_PATTERN.findall(signature)))


def source_type_exists(workspace, type_name):
    outer_type = type_name.split("$")[0]
    return (workspace / "src" / "main" / "java" / pathlib.Path(*outer_type.split("."))).with_suffix(".java").is_file()


def dependency_groups(workspace, signature, dependency_index):
    missing_j2cl = []
    project_prerequisites = []
    for type_name in referenced_types(signature):
        classification = dependency_index.get(type_name)
        if classification == "missing-in-j2cl-owned-package" and type_name not in KNOWN_AVAILABLE_NESTED_TYPES:
            missing_j2cl.append(type_name)
        if classification in {"external-unclaimed-package", "missing-javax-package"} and not source_type_exists(workspace, type_name):
            project_prerequisites.append(type_name)
    return missing_j2cl, project_prerequisites


def member_disposition(workspace, member, dependency_index):
    missing_j2cl, project_prerequisites = dependency_groups(workspace, member["signature"], dependency_index)
    support_status = member["supportStatus"]
    if support_status in OUT_OF_PROFILE_SUPPORT_STATUSES:
        disposition = support_status
    elif support_status == "deferred":
        disposition = "unresolved"
    elif missing_j2cl:
        disposition = "blocked-j2cl-signature"
    elif project_prerequisites:
        disposition = "requires-project-type"
    else:
        disposition = "feasible"
    return {
        "signature": member["signature"],
        "supportStatus": support_status,
        "implementationState": member["implementationState"],
        "resolution": member["resolution"],
        "disposition": disposition,
        "missingJ2clTypes": missing_j2cl,
        "projectTypePrerequisites": project_prerequisites,
    }


def build_worklist(workspace, reference, validation, manifest, dependency_report, implementation_status):
    workspace = pathlib.Path(workspace)
    validation_index = {entry["type"]: entry for entry in validation["types"]}
    manifest_index = {entry["type"]: entry for entry in manifest["entries"]}
    dependency_index = {entry["type"]: entry["classification"] for entry in dependency_report["dependencies"]}
    implemented_types = set(implementation_status["implementedTypes"])
    partial_types = set(implementation_status["partialTypes"])
    evidence_types = implemented_types | partial_types
    violations = []
    if implemented_types & partial_types:
        violations.append("Implementation evidence contains types in both implementedTypes and partialTypes")
    type_results = []
    target_member_count = 0
    callable_target_member_count = 0
    unavailable_member_count = 0
    excluded_member_count = 0
    project_prerequisite_member_count = 0
    for reference_entry in reference["types"]:
        type_name = reference_entry["name"]
        manifest_entry = manifest_index.get(type_name)
        validation_entry = validation_index.get(type_name)
        if manifest_entry is None or validation_entry is None:
            violations.append(f"Missing manifest or validation entry for {type_name}")
            continue
        support_status = manifest_entry["supportStatus"]
        implementation_state = manifest_entry["implementationState"]
        emitted = validation_entry["actualDeclaration"] is not None
        if support_status not in TARGET_SUPPORT_STATUSES | OUT_OF_PROFILE_SUPPORT_STATUSES:
            violations.append(f"Type has no resolved support disposition: {type_name}")
        if support_status in TARGET_SUPPORT_STATUSES and not emitted:
            violations.append(f"Missing in-profile type: {type_name}")
        if support_status in TARGET_SUPPORT_STATUSES:
            for issue in validation_entry.get("typeCompatibilityIssues", []):
                violations.append(f"Incompatible in-profile type: {type_name}: {issue}")
        if support_status == "deferred":
            violations.append(f"Type remains deferred: {type_name}")
        if emitted and type_name not in evidence_types:
            violations.append(f"Emitted type lacks explicit implementation evidence: {type_name}")
        if type_name in evidence_types and not emitted:
            violations.append(f"Implementation evidence names a type that is not emitted: {type_name}")
        expected_state = "implemented" if type_name in implemented_types else "partial" if type_name in partial_types else "not-started"
        if implementation_state != expected_state:
            violations.append(f"Implementation state for {type_name} is {implementation_state}, expected {expected_state}")
        member_results = []
        reference_members = set(reference_entry["members"])
        validation_members = {member["signature"] for member in validation_entry["members"]}
        manifest_members = {member["signature"]: member for member in manifest_entry["members"]}
        if reference_members != validation_members or reference_members != set(manifest_members):
            violations.append(f"Reference, manifest, and validation member inventories disagree: {type_name}")
        for member in validation_entry["members"]:
            result = member_disposition(workspace, member, dependency_index)
            member_results.append(result)
            classified = manifest_members.get(member["signature"])
            if classified is not None and (classified["supportStatus"] != member["supportStatus"] or classified["implementationState"] != member["implementationState"]):
                violations.append(f"Manifest and validation disposition disagree: {type_name} {member['signature']}")
            if result["supportStatus"] not in TARGET_SUPPORT_STATUSES | OUT_OF_PROFILE_SUPPORT_STATUSES:
                violations.append(f"Member has no resolved support disposition: {type_name} {result['signature']}")
            if result["disposition"] == "blocked-j2cl-signature" and result["supportStatus"] in TARGET_SUPPORT_STATUSES:
                violations.append(f"Supported member requires a missing J2CL-owned signature type: {type_name} {result['signature']}")
            if result["disposition"] == "unresolved":
                violations.append(f"Member remains deferred: {type_name} {result['signature']}")
            if result["supportStatus"] in TARGET_SUPPORT_STATUSES:
                target_member_count += 1
                if result["resolution"] in {"exact-declaration", "compatible-declaration", "inherited"}:
                    callable_target_member_count += 1
                else:
                    violations.append(f"Missing or unresolved in-profile member: {type_name} {result['signature']}")
                if result["disposition"] == "requires-project-type":
                    project_prerequisite_member_count += 1
            elif result["supportStatus"] == "unavailable":
                unavailable_member_count += 1
            elif result["supportStatus"] == "excluded":
                excluded_member_count += 1
        type_results.append({
            "type": type_name,
            "package": reference_entry["package"],
            "supportStatus": support_status,
            "implementationState": implementation_state,
            "emitted": emitted,
            "targetType": support_status in TARGET_SUPPORT_STATUSES,
            "referenceMemberCount": len(reference_entry["members"]),
            "members": member_results,
        })
    target_types = [entry for entry in type_results if entry["targetType"]]
    emitted_target_types = [entry for entry in target_types if entry["emitted"]]
    summary = {
        "referenceTypeCount": len(reference["types"]),
        "targetTypeCount": len(target_types),
        "emittedTargetTypeCount": len(emitted_target_types),
        "implementedTypeCount": sum(entry["implementationState"] == "implemented" for entry in type_results),
        "partialTypeCount": sum(entry["implementationState"] == "partial" for entry in type_results),
        "notStartedTypeCount": sum(entry["implementationState"] == "not-started" for entry in type_results),
        "deferredTypeCount": sum(entry["supportStatus"] == "deferred" for entry in type_results),
        "referenceMemberCount": sum(len(entry["members"]) for entry in reference["types"]),
        "targetMemberCount": target_member_count,
        "callableTargetMemberCount": callable_target_member_count,
        "missingTargetMemberCount": target_member_count - callable_target_member_count,
        "projectPrerequisiteMemberCount": project_prerequisite_member_count,
        "unavailableMemberCount": unavailable_member_count,
        "excludedMemberCount": excluded_member_count,
        "violationCount": len(violations),
    }
    return {"schemaVersion": 1, "summary": summary, "violations": violations, "types": type_results}


def main():
    parser = argparse.ArgumentParser(description="Build and validate the stock-J2CL WebAWT API completeness worklist")
    parser.add_argument("--workspace", default=".")
    parser.add_argument("--reference", default="compatibility/openjdk-awt-api.json")
    parser.add_argument("--validation", default="compatibility/webawt-api-validation.json")
    parser.add_argument("--manifest", default="compatibility/compatibility-manifest.json")
    parser.add_argument("--dependencies", default="compatibility/j2cl-dependency-report.json")
    parser.add_argument("--implementation-status", default="compatibility/implementation-status.json")
    parser.add_argument("--output", default="compatibility/api-completeness-worklist.json")
    arguments = parser.parse_args()
    workspace = pathlib.Path(arguments.workspace).resolve()
    report = build_worklist(workspace, load_json(workspace / arguments.reference), load_json(workspace / arguments.validation), load_json(workspace / arguments.manifest), load_json(workspace / arguments.dependencies), load_json(workspace / arguments.implementation_status))
    output_path = workspace / arguments.output
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    summary = report["summary"]
    print(f"Completeness target: {summary['emittedTargetTypeCount']} / {summary['targetTypeCount']} types, {summary['callableTargetMemberCount']} / {summary['targetMemberCount']} members callable")
    print(f"Deferred types: {summary['deferredTypeCount']}; project-prerequisite members: {summary['projectPrerequisiteMemberCount']}; invariant violations: {summary['violationCount']}")
    for violation in report["violations"]:
        print(f"ERROR: {violation}")
    return 1 if report["violations"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
