#!/usr/bin/env python3

"""Keep the supported API denominator stable until an explicit profile update."""

import argparse
import hashlib
import json
import pathlib

from compare_webawt_api import load_json


RESOLVED_STATUSES = {"exact", "web-mapped", "limited", "unavailable", "excluded"}


def profile_snapshot(manifest, extended=None, prerequisites=None):
    types = {}
    for entry in manifest["entries"]:
        name = entry["type"]
        if name in types:
            raise ValueError(f"Duplicate profile type: {name}")
        if entry["supportStatus"] not in RESOLVED_STATUSES:
            raise ValueError(f"Unresolved profile type: {name}")
        members = {}
        counts = {}
        for member in entry["members"]:
            signature = member["signature"]
            status = member["supportStatus"]
            if signature in members:
                raise ValueError(f"Duplicate profile member: {name} {signature}")
            if status not in RESOLVED_STATUSES:
                raise ValueError(f"Unresolved profile member: {name} {signature}")
            members[signature] = status
            counts[status] = counts.get(status, 0) + 1
        encoded = json.dumps(members, sort_keys=True, separators=(",", ":"), ensure_ascii=True).encode("utf-8")
        types[name] = {
            "supportStatus": entry["supportStatus"],
            "memberCounts": dict(sorted(counts.items())),
            "memberDispositionsSha256": hashlib.sha256(encoded).hexdigest(),
        }
    snapshot = {"schemaVersion": 1, "types": dict(sorted(types.items()))}
    if extended is not None:
        extension_types = {}
        extension_members = {}
        for entry in extended.get("types", []):
            if entry["type"] in extension_types or entry["supportStatus"] not in RESOLVED_STATUSES:
                raise ValueError(f"Duplicate or unresolved extended type: {entry['type']}")
            extension_types[entry["type"]] = entry["supportStatus"]
        for entry in extended.get("members", []):
            key = entry["type"] + ": " + entry["signature"]
            if key in extension_members or entry["supportStatus"] not in RESOLVED_STATUSES:
                raise ValueError(f"Duplicate or unresolved extended member: {key}")
            extension_members[key] = entry["supportStatus"]
        snapshot["extensions"] = {"types": dict(sorted(extension_types.items())), "members": dict(sorted(extension_members.items()))}
        if extended.get("unavailableParents"):
            snapshot["extensions"]["unavailableParents"] = sorted([entry["type"], entry["parent"]] for entry in extended["unavailableParents"])
    if prerequisites is not None:
        roots = prerequisites["types"]
        if len(roots) != len(set(roots)):
            raise ValueError("Duplicate prerequisite root types")
        snapshot["prerequisiteRootTypes"] = sorted(roots)
    return snapshot


def profile_changes(locked, current):
    if locked.get("schemaVersion") != 1:
        return ["Unsupported profile lock schema"]
    before = locked["types"]
    after = current["types"]
    changes = [f"Removed reference type: {name}" for name in sorted(before.keys() - after.keys())]
    changes.extend(f"Added reference type: {name}" for name in sorted(after.keys() - before.keys()))
    changes.extend(f"Changed support disposition or member inventory: {name}" for name in sorted(before.keys() & after.keys()) if before[name] != after[name])
    if locked.get("extensions") != current.get("extensions"):
        changes.append("Changed reviewed extended API dispositions")
    if locked.get("prerequisiteRootTypes") != current.get("prerequisiteRootTypes"):
        changes.append("Changed prerequisite reference type families")
    return changes


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--manifest", default="compatibility/compatibility-manifest.json")
    parser.add_argument("--lock", default="compatibility/stock-profile-lock.json")
    parser.add_argument("--extended-dispositions", default="compatibility/extended-api-dispositions.json")
    parser.add_argument("--prerequisites", default="compatibility/prerequisite-types.json")
    parser.add_argument("--write-lock", action="store_true", help="Explicitly accept the current profile; review the manifest and lock changes together. Never used by CI.")
    arguments = parser.parse_args()
    current = profile_snapshot(load_json(arguments.manifest), load_json(arguments.extended_dispositions), load_json(arguments.prerequisites))
    lock_path = pathlib.Path(arguments.lock)
    if arguments.write_lock:
        lock_path.parent.mkdir(parents=True, exist_ok=True)
        lock_path.write_text(json.dumps(current, indent=2) + "\n", encoding="utf-8")
        print(f"Recorded profile dispositions for {len(current['types'])} types in {lock_path}")
        return 0
    if not lock_path.is_file():
        print(f"ERROR: Missing profile lock: {lock_path}")
        return 1
    changes = profile_changes(load_json(lock_path), current)
    for change in changes:
        print(f"ERROR: {change}")
    if changes:
        print("Review the compatibility manifest changes and explicitly update the profile lock. API presence alone does not justify dropping support.")
        return 1
    print(f"Stock profile unchanged: {len(current['types'])} type dispositions and their exact member inventories")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
