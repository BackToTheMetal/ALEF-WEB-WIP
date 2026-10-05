#!/usr/bin/env python3
"""Inventory Graphics/Graphics2D overloads; validate explicitly reviewed showcase evidence."""
import argparse
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[2]
BASE = "src/main/java/java/awt/"
BACKENDS = {
    "Canvas": "src/main/java/org/webawt/internal/graphics/CanvasGraphics2D.java",
    "BufferedImage": BASE + "image/BufferedImageGraphics2D.java",
}


def key(signature):
    signature = re.sub(r"<[^>]*>", "", signature)
    match = re.search(r"(\w+)\(([^)]*)\)", signature)
    if not match:
        return None
    args = re.sub(r"(?:[\w$]+\.)+", "", match[2].replace("$", ".")).replace(" ", "")
    return match[1] + "(" + args + ")"


def source_methods(path):
    result = {}
    for match in re.finditer(r"^\s+(?:public|protected)\s+(?:(?:static|final|abstract|synchronized)\s+)*[\w<>?,.\[\] ]+\s+(\w+)\(([^)]*)\)", path.read_text(), re.M):
        parameters = re.sub(r"<[^>]*>", "", match[2])
        types = [parameter.strip().rsplit(" ", 1)[0] for parameter in parameters.split(",") if parameter.strip()]
        result[key(match[1] + "(" + ",".join(types) + ")")] = path.relative_to(ROOT).as_posix()
    return result


def inventory(reference):
    methods = {}
    for name in ("java.awt.Graphics", "java.awt.Graphics2D"):
        entry = next(entry for entry in reference["types"] if entry["name"] == name)
        for signature in entry["members"]:
            method = key(signature)
            if method and method not in ("Graphics()", "Graphics2D()"):
                methods[method] = {"signature": re.sub(r"\babstract ", "", signature), "declaredBy": name}
    return methods


def validate_reviews(reviews, methods, root):
    seen = set()
    for review in reviews:
        method = review["method"]
        if method not in methods or method in seen:
            raise ValueError("Unknown or duplicate reviewed signature: " + method)
        seen.add(method)
        if not review.get("scenarios") or not review.get("observable"):
            raise ValueError("Scenario and observable result required: " + method)
        for backend in BACKENDS:
            evidence = review.get("evidence", {}).get(backend, [])
            if not evidence or any(not (root / file).is_file() for file in evidence):
                raise ValueError("Existing backend evidence required: " + method + " / " + backend)


def build_report(reference, reviews):
    methods = inventory(reference)
    validate_reviews(reviews, methods, ROOT)
    reviewed = {entry["method"]: entry for entry in reviews}
    common = source_methods(ROOT / (BASE + "Graphics.java"))
    common.update(source_methods(ROOT / (BASE + "Graphics2D.java")))
    implementations = {backend: dict(common, **source_methods(ROOT / file)) for backend, file in BACKENDS.items()}
    rows = []
    for method, declaration in sorted(methods.items()):
        review = reviewed.get(method)
        backends = {}
        for backend in BACKENDS:
            source = implementations[backend].get(method)
            if source is None:
                raise ValueError("Cannot resolve implementation: " + backend + " / " + method)
            unavailable = backend == "Canvas" and method == "setXORMode(Color)"
            backends[backend] = {
                "implementation": source,
                "coverage": "unavailable" if unavailable else "verified" if review else "pending",
                "evidence": review["evidence"][backend] if review else [],
            }
        rows.append(dict(method=method, **declaration, backends=backends,
                         page="Rendering lab / Graphics state" if review else "Graphics2D / Rendering lab: overload review pending",
                         scenarios=review["scenarios"] if review else [],
                         observable=review["observable"] if review else "No reviewed signature-level assertion yet."))
    return {"schemaVersion": 1, "methodCount": len(rows), "verifiedOnBothBackends": sum(all(b["coverage"] == "verified" for b in row["backends"].values()) for row in rows),
            "complete": all(backend["coverage"] != "pending" for row in rows for backend in row["backends"].values()),
            "methods": rows}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--require-complete", action="store_true")
    args = parser.parse_args()
    reference = json.loads((ROOT / "compatibility/openjdk-awt-api.json").read_text())
    reviews = json.loads((ROOT / "compatibility/graphics2d-showcase-reviews.json").read_text())["reviews"]
    report = build_report(reference, reviews)
    (ROOT / "compatibility/graphics2d-showcase-coverage.json").write_text(json.dumps(report, indent=2) + "\n")
    lines = ["# Graphics2D showcase method coverage", "",
             "All public Graphics2D methods, including inherited Graphics overloads, are listed individually. Constructors of the abstract API classes are not drawing-context operations. Backend implementation paths include inherited default methods. WebAWT-only internal helpers are outside this JDK consumer signature inventory.", "",
             "Verified means manually reviewed scenario code plus state/pixel assertions on both backends; the generator checks signatures and evidence references, not the semantic quality of tests. Existing gallery calls without reviewed overload-specific assertions remain pending. Canvas XOR is explicitly unavailable; its rejection and BufferedImage XOR are demonstrated and asserted. Completion is enforced with --require-complete.", "",
             f"**{report['verifiedOnBothBackends']} / {report['methodCount']} signatures verified on both backends.**", "",
             "Scenario sources: samples/dual_showcase/GraphicsStateScenarios.java, StrokeScenarios.java, PaintScenarios.java, FontScenarios.java, TextScenarios.java, AttributedScenarios.java, CoreGraphicsScenarios.java, RenderedImageScenarios.java and PerformanceScenarios.java. JSON contains per-backend implementation and test evidence paths. Desktop direct Canvas checks cover opaque backgrounds; transparent native desktop surfaces are platform-dependent. Transparent BufferedImage and browser Canvas output are asserted separately.", "",
             "| Signature | Canvas implementation | BufferedImage implementation | Coverage (Canvas / image) | Page / scenario | Observable assertion |",
             "| --- | --- | --- | --- | --- | --- |"]
    for row in report["methods"]:
        canvas, image = row["backends"]["Canvas"], row["backends"]["BufferedImage"]
        lines.append(f"| {row['method']} | {canvas['implementation']} | {image['implementation']} | {canvas['coverage']} / {image['coverage']} | {row['page']}: {', '.join(row['scenarios'])} | {row['observable']} |")
    (ROOT / "compatibility/graphics2d-showcase-coverage.md").write_text("\n".join(lines) + "\n")
    print(f"Graphics2D showcase: {report['verifiedOnBothBackends']} / {report['methodCount']} signatures verified on both backends; complete={report['complete']}")
    return 1 if args.require_complete and not report["complete"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
