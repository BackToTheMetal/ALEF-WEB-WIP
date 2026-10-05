#!/usr/bin/env python3
"""Generate fail-fast declarations from ExtractApi's checked-in JDK signature inventory."""
import argparse
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DEST = ROOT / "src/main/super"
API = json.loads((ROOT / "compatibility/jdk-nio-api.json").read_text())
TYPES = {c["name"]: c for c in API["classes"]}
UPSTREAM = {"java.nio.charset.Charset", "java.nio.charset.StandardCharsets", "java.nio.charset.IllegalCharsetNameException", "java.nio.charset.UnsupportedCharsetException"}
HANDWRITTEN = {"java.nio.ByteOrder", "java.nio.charset.CodingErrorAction", "java.nio.charset.CoderResult", "java.nio.file.StandardWatchEventKinds", "java.nio.file.InvalidPathException", "java.nio.file.FileSystemException", "java.nio.file.attribute.FileTime"}

def split_types(text):
    result, depth, start = [], 0, 0
    for i, char in enumerate(text):
        depth += (char == '<') - (char == '>')
        if char == ',' and depth == 0:
            result.append(text[start:i].strip()); start = i + 1
    if text[start:].strip(): result.append(text[start:].strip())
    return result

def signature(member):
    declaration = member["declaration"]
    begin, end = declaration.index('('), declaration.index(')')
    types = split_types(declaration[begin+1:end])
    named = ", ".join(t + " p" + str(i) for i, t in enumerate(types))
    return declaration[:begin+1] + named + declaration[end:], types

def fail(c, member):
    declaration = member["declaration"]
    return 'throw javaemul.internal.nio.UnsupportedNio.operation("' + c["name"] + '.' + declaration[declaration.index(member["name"] + '('):] + '");'

def ctor_body(c, member, types):
    parent = TYPES.get(c["superclass"])
    call = ""
    if parent and parent["constructors"]:
        choices = []
        for ctor in parent["constructors"]:
            _, needed = signature(ctor)
            available = list(enumerate(types)); arguments = []
            for t in needed:
                match = next(((i, v) for i, v in available if v == t), None)
                if match:
                    arguments.append("p" + str(match[0])); available.remove(match)
                else: arguments.append("false" if t == "boolean" else "0" if t in {"int","long","byte","short","float","double","char"} else "null")
            choices.append((sum(a in {"null","0","false"} for a in arguments), -len(needed), arguments))
        arguments = min(choices)[2]
        call = "super(" + ", ".join(arguments) + "); "
    elif c["name"].endswith(("Exception", "Error")):
        if types == ["java.lang.String"]: call = "super(p0); "
        elif len(types) == 1 and types[0] in {"java.lang.Throwable", "java.lang.Exception", "java.lang.Error", "java.io.IOException"}: call = "super(p0); "
    if c["name"] in {"java.nio.charset.MalformedInputException", "java.nio.charset.UnmappableCharacterException"}: call += "this.inputLength = p0; "
    if c["name"] == "java.nio.file.attribute.UserPrincipalNotFoundException": call += "this.name = p0; "
    if c["name"] == "java.nio.file.DirectoryIteratorException": call = "super(java.util.Objects.requireNonNull(p0)); "
    if c["name"] == "java.nio.file.LinkPermission": call = "super(" + ", ".join("p"+str(i) for i in range(len(types))) + "); "
    # Constructors on abstract types support user subclasses. Concrete exceptions carry data.
    if " abstract class " in (" " + c["declaration"] + " ") or c["name"].endswith(("Exception", "Error")):
        return call
    return call + fail(c, member)

def render(c, indent=""):
    lines = [indent + c["declaration"] + " {"]
    inner = indent + "  "
    if c["name"] in {"java.nio.charset.MalformedInputException", "java.nio.charset.UnmappableCharacterException"}: lines.append(inner + "private final int inputLength;")
    if c["name"] == "java.nio.file.attribute.UserPrincipalNotFoundException": lines.append(inner + "private final String name;")
    if c["enumConstants"]:
        lines.append(inner + ", ".join(c["enumConstants"]) + ";")
    if c["name"] == "java.nio.channels.FileChannel.MapMode":
        lines += [inner + 'private final String name;', inner + 'private MapMode(String name) { this.name = name; }']
    elif not c["constructors"] and " interface " not in (" " + c["declaration"] + " ") and not c["enumConstants"]:
        # Package-private construction is required by the buffer hierarchy; utilities stay private.
        access = "" if c["simpleName"].endswith("Buffer") else "private "
        lines.append(inner + access + c["simpleName"] + "() {}")
    for field in c["fields"]:
        if field["name"] in c["enumConstants"]: continue
        value = field["value"]
        if c["name"] == "java.nio.file.attribute.AclEntryPermission":
            value = {"ADD_FILE":"WRITE_DATA", "ADD_SUBDIRECTORY":"APPEND_DATA", "LIST_DIRECTORY":"READ_DATA"}.get(field["name"], value)
        if c["name"] == "java.nio.channels.FileChannel.MapMode": value = 'new MapMode("' + field["name"] + '")'
        if value is None:
            raise ValueError("Field needs explicit metadata implementation: " + c["name"] + "." + field["name"])
        lines.append(inner + field["declaration"] + " = " + value + ";")
    for ctor in c["constructors"]:
        declaration, types = signature(ctor)
        lines.append(inner + declaration + " { " + ctor_body(c, ctor, types) + " }")
    for method in c["methods"]:
        if "java.lang.foreign." in method["declaration"]: continue  # Java 21 preview API, outside the profile.
        declaration, _ = signature(method)
        declaration = re.sub(r"\b(native|synchronized) ", "", declaration)
        metadata = None
        if c["name"] in {"java.nio.charset.MalformedInputException", "java.nio.charset.UnmappableCharacterException"}:
            metadata = "return inputLength;" if method["name"] == "getInputLength" else 'return "Input length = " + inputLength;'
        if c["name"] == "java.nio.file.attribute.UserPrincipalNotFoundException" and method["name"] == "getName": metadata = "return name;"
        if c["name"] == "java.nio.file.DirectoryIteratorException" and method["name"] == "getCause": metadata = "return (java.io.IOException) super.getCause();"
        if c["name"] == "java.nio.file.DirectoryIteratorException" and method["name"] == "getCause": lines.append(inner + '@SuppressWarnings("unusable-by-js")')
        if metadata: lines.append(inner + declaration + " { " + metadata + " }")
        elif method["abstract"]: lines.append(inner + declaration + ";")
        elif c["name"] == "java.nio.channels.FileChannel.MapMode" and method["name"] == "toString": lines.append(inner + declaration + " { return name; }")
        else: lines.append(inner + declaration + " { " + fail(c, method) + " }")
    for nested in API["classes"]:
        if nested["enclosing"] == c["name"]: lines.extend(render(nested, inner))
    lines.append(indent + "}")
    return lines

def main():
    count = 0
    for c in API["classes"]:
        if c["enclosing"] or c["name"] in UPSTREAM | HANDWRITTEN: continue
        path = DEST / (c["name"].replace('.', '/') + ".java")
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text("// Generated by tools/generate_stubs.py from JDK signatures; no JDK implementation copied.\npackage " + c["package"] + ";\n\n" + "\n".join(render(c)) + "\n", newline="\n")
        count += 1
    print("Generated", count, "top-level NIO stub files")

if __name__ == "__main__": main()
