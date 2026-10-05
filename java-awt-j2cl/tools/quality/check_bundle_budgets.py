#!/usr/bin/env python3

import argparse
import gzip
import json
import pathlib


def measure_bundle(bundle_path):
    content = bundle_path.read_bytes()
    return {"rawBytes": len(content), "gzipBytes": len(gzip.compress(content, mtime=0))}


def evaluate_bundles(workspace, configuration):
    results = []
    violations = []
    for bundle in configuration.get("bundles", []):
        bundle_path = workspace / bundle["path"]
        if not bundle_path.is_file():
            violations.append(f"Missing optimized bundle {bundle['path']}")
            continue
        measurements = measure_bundle(bundle_path)
        result = {
            "name": bundle["name"],
            "path": bundle["path"],
            "rawBytes": measurements["rawBytes"],
            "maxRawBytes": bundle["maxRawBytes"],
            "gzipBytes": measurements["gzipBytes"],
            "maxGzipBytes": bundle["maxGzipBytes"],
        }
        results.append(result)
        if result["rawBytes"] > result["maxRawBytes"]:
            violations.append(f"{result['name']} raw size {result['rawBytes']} exceeds {result['maxRawBytes']} bytes")
        if result["gzipBytes"] > result["maxGzipBytes"]:
            violations.append(f"{result['name']} gzip size {result['gzipBytes']} exceeds {result['maxGzipBytes']} bytes")
    return {"schemaVersion": 1, "bundles": results}, violations


def main():
    parser = argparse.ArgumentParser(description="Measure optimized WebAWT bundles against committed budgets")
    parser.add_argument("--workspace", default=".", help="WebAWT workspace root")
    parser.add_argument("--config", default="config/bundle-budgets.json", help="Bundle budget configuration")
    parser.add_argument("--output", default="compatibility/bundle-size-report.json", help="Deterministic JSON report path")
    arguments = parser.parse_args()
    workspace = pathlib.Path(arguments.workspace).resolve()
    configuration = json.loads((workspace / arguments.config).read_text(encoding="utf-8"))
    report, violations = evaluate_bundles(workspace, configuration)
    output_path = workspace / arguments.output
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    for bundle in report["bundles"]:
        print(f"{bundle['name']}: {bundle['rawBytes']} raw bytes, {bundle['gzipBytes']} gzip bytes")
    if violations:
        for violation in violations:
            print(f"ERROR: {violation}")
        return 1
    print("Optimized bundle budgets passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
