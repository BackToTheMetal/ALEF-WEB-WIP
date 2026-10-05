#!/usr/bin/env python3
"""Generate compile-only calls, field assignments and subclass overrides from retained reference signatures."""
import argparse
import json
from pathlib import Path
import re

SUPPORTED = {"exact", "limited", "web-mapped"}
ROOT = Path(__file__).resolve().parents[2]
DEST = ROOT / "src/test/consumer/generated"


def split_args(text):
    parts, depth, start = [], 0, 0
    for index, char in enumerate(text):
        depth += (char == "<") - (char == ">")
        if char == "," and depth == 0:
            parts.append(text[start:index].strip())
            start = index + 1
    if text[start:].strip():
        parts.append(text[start:].strip())
    return parts


def erased(text):
    while "<" in text:
        text = re.sub(r"<[^<>]*>", "", text)
    return text.replace("$", ".").replace("...", "[]")


def default(type_name):
    if type_name == "boolean":
        return "false"
    if type_name in {"int", "byte", "short", "char", "float", "double", "long"}:
        return "(" + type_name + ") 0"
    return "(" + type_name + ") null"


def parse(signature):
    signature = signature.rstrip(";").replace("$", ".")
    header, rest = signature.split("(", 1)
    parameters, tail = rest.split(")", 1)
    name = header.split()[-1]
    modifiers = set(header.split()) & {"public", "protected", "static", "abstract", "final", "synchronized", "native"}
    body = re.sub(r"^(?:(?:public|protected|static|abstract|final|synchronized|native)\s+)+", "", header)
    generics = ""
    bounds = {}
    if body.startswith("<"):
        depth = 0
        for index, char in enumerate(body):
            depth += (char == "<") - (char == ">")
            if depth == 0:
                generics, body = body[:index + 1], body[index + 1:].strip()
                break
        for param in split_args(generics[1:-1]):
            pieces = param.split(" extends ", 1)
            bounds[pieces[0]] = erased(pieces[1].split(" & ")[0]) if len(pieces) > 1 else "java.lang.Object"
    return name, body, split_args(parameters), tail.strip(), modifiers, generics, bounds


def generate(reference, report):
    refs = {t["name"]: t for t in reference["types"]}
    files, records, skipped = {}, [], []
    for type in report["types"]:
        if type["supportStatus"] not in SUPPORTED:
            continue
        name, ref = type["type"], refs[type["type"]]
        java_name = name.replace("$", ".")
        package = name.rsplit(".", 1)[0]
        ident = name.replace(".", "_").replace("$", "_")
        members = [m for m in type["members"] if m["supportStatus"] in SUPPORTED]
        calls, overrides = [], []
        constructors = []
        enum = "extends java.lang.Enum<" in ref["declaration"]
        abstract = "abstract" in ref["declaration"].split("class", 1)[0] or " interface " in ref["declaration"]
        extensible = not enum and "final" not in ref["declaration"].split("class", 1)[0]
        enclosing = ref.get("enclosingType") if not ref.get("static") else None
        for member in members:
            sig = member["signature"]
            if "(" in sig and sig.split("(")[0].split()[-1] == name:
                constructors.append(member)
        super_call = None
        for member in constructors:
            _, _, args, tail, _, _, _ = parse(member["signature"])
            if enclosing and args and args[0] == enclosing.replace("$", "."):
                args = args[1:]
            super_call = (args, tail)
            break
        for member in members:
            sig = member["signature"]
            number = len(calls)
            if "(" not in sig:
                tokens = erased(sig.rstrip(";")).split()
                field, field_type = tokens[-1], tokens[-2]
                receiver = java_name if "static" in tokens else "receiver"
                params = "" if "static" in tokens else java_name + " receiver"
                body = field_type + " value = " + receiver + "." + field + ";"
                if "final" not in tokens:
                    body += " " + receiver + "." + field + " = value;"
                calls.append("    static void call" + str(number) + "(" + params + ") { " + body + " }")
                records.append({"type": name, "signature": sig, "probe": "field-read" if "final" in tokens else "field-read-write"})
                continue
            method, body_header, args, tail, modifiers, generics, bounds = parse(sig)
            constructor = method == java_name
            def erase_type(value):
                value = erased(value)
                for variable, bound in bounds.items():
                    value = re.sub(r"\b" + variable + r"\b", bound, value)
                return value
            call_args = list(args)
            if constructor and enclosing and call_args and call_args[0] == enclosing.replace("$", "."):
                call_args = call_args[1:]
            invocation_args = ", ".join(default(erase_type(arg)) for arg in call_args)
            if constructor:
                if abstract or enum:
                    skipped.append({"type": name, "signature": sig, "reason": "abstract/enum constructor; subclass declaration probes cover accessible abstract super constructors"})
                    continue
                params = enclosing.replace("$", ".") + " outer" if enclosing else ""
                invocation = "outer.new " + java_name.rsplit(".", 1)[1] if enclosing else "new " + java_name
                return_type = java_name
            else:
                params = "" if "static" in modifiers else java_name + " receiver"
                invocation = (java_name if "static" in modifiers else "receiver") + "." + method
                if method == "compareTo" and args == ["java.lang.Object"] and "java.lang.Comparable<" in ref["declaration"]:
                    invocation = "((java.lang.Comparable) receiver).compareTo"
                return_type = erase_type(body_header.rsplit(" ", 1)[0])
            statement = invocation + "(" + invocation_args + ");"
            if return_type != "void":
                statement = return_type + " value = " + statement
            # A checked catch is source-sensitive when an implementation silently removes throws.
            exceptions = split_args(tail.removeprefix("throws ")) if tail else []
            for exception in exceptions:
                statement = "try { " + statement + " } catch (" + exception + " expected) { throw expected; }"
            calls.append("    static void call" + str(number) + "(" + params + ") " + tail + " { " + statement + " }")
            records.append({"type": name, "signature": sig, "probe": "constructor" if constructor else "call-return-throws"})
            if not constructor and extensible and super_call is not None and not ({"final", "static"} & modifiers):
                visibility = "protected" if "protected" in modifiers else "public"
                params = ", ".join(arg + " arg" + str(index) for index, arg in enumerate(args))
                overrides.append("    @Override\n    " + visibility + " " + (generics + " " if generics else "") + body_header + "(" + params + ") " + tail + " { throw new UnsupportedOperationException(); }")
        content = "package " + package + ";\n\n// Generated from retained OpenJDK signatures. Compile only; never execute.\nfinal class Probe_" + ident + " {\n" + "\n".join(calls) + "\n}\n"
        if extensible and super_call is not None:
            args, tail = super_call
            parameters = enclosing.replace("$", ".") + " outer" if enclosing else ""
            invocation = "outer.super" if enclosing else "super"
            content += "\nabstract class Override_" + ident + " extends " + java_name + " {\n    Override_" + ident + "(" + parameters + ") " + tail + " { " + invocation + "(" + ", ".join(default(erased(arg)) for arg in args) + "); }\n" + "\n".join(overrides) + "\n}\n"
        files["Probe_" + ident + ".java"] = content
    return files, {"schemaVersion": 1, "callProbeCount": len(records), "fileCount": len(files), "probes": records, "constructorLimitations": skipped}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    reference = json.loads((ROOT / "compatibility/openjdk-extended-api.json").read_text())
    report = json.loads((ROOT / "compatibility/extended-api-worklist.json").read_text())
    files, coverage = generate(reference, report)
    DEST.mkdir(parents=True, exist_ok=True)
    expected = set(files)
    actual = {p.name for p in DEST.glob("*.java")}
    differences = sorted(expected ^ actual)
    for name, content in files.items():
        path = DEST / name
        if args.check:
            if not path.exists() or path.read_text() != content:
                differences.append(name)
        else:
            path.write_text(content)
    if not args.check:
        for stale in actual - expected:
            (DEST / stale).unlink()
        (ROOT / "compatibility/consumer-probe-coverage.json").write_text(json.dumps(coverage, indent=2) + "\n")
    print(f"Consumer probes: {coverage['callProbeCount']} calls/fields/constructors in {len(files)} reference types")
    if args.check and differences:
        raise SystemExit("Stale generated consumer probes: " + str(differences))


if __name__ == "__main__":
    main()
