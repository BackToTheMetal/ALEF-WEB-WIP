#!/usr/bin/env python3
"""Generate throwing java.nio API stubs from JAVA_HOME, or the JDK on PATH.

Python standard library only. javap reads signatures; javac checks the result.
This is a small NIO-specific trial, not a general Java source generator.
"""
import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import tempfile
import zipfile


THROW = 'throw new UnsupportedOperationException("Stub!");'


def run(*command):
    return subprocess.check_output(command, text=True, encoding="utf-8")


def find_jdk(home):
    if home:
        return Path(home).resolve()
    java = shutil.which("java")
    if not java:
        raise ValueError("Set JAVA_HOME to a JDK, or put java on PATH.")
    # Ask Java itself: PATH may contain a launcher or a symlink.
    result = subprocess.run([java, "-XshowSettings:properties", "-version"],
                            capture_output=True, text=True, check=True)
    return Path(re.search(r"^\s*java.home = (.+)$", result.stderr, re.M)[1].strip())


def write_jdk_api(jmod, destination):
    """Save compile-time references for Bazel; none of these classes are transpiled."""
    with zipfile.ZipFile(jmod) as source, zipfile.ZipFile(destination, "w") as target:
        for entry in sorted(source.namelist()):
            name = entry.removeprefix("classes/")
            if (entry.startswith("classes/") and name.endswith(".class")
                    and name != "module-info.class" and not name.startswith("java/nio/")):
                target.writestr(zipfile.ZipInfo(name), source.read(entry), compress_type=zipfile.ZIP_DEFLATED)


def read_api(javap, names):
    """Keep declarations and flags; ignore bytecode, annotations and debug data."""
    classes = {}
    for start in range(0, len(names), 40):
        output = run(str(javap), "-J-Duser.language=en", "-protected", "-constants",
                     "-verbose", "--module", "java.base", *names[start:start + 40])
        for block in re.split(r"(?m)^Classfile ", output)[1:]:
            header = re.search(r"^(?:public |protected )?(?:abstract |final )*"
                               r"(class|interface) ([\w.$]+).*", block, re.M)
            if not header:
                raise ValueError("Unrecognized javap class declaration")
            kind, name = header.group(1, 2)
            flags = re.search(r"^  flags: (.+)$", block, re.M)[1]
            visible = "ACC_PUBLIC" in flags
            static = False
            if "$" in name:
                inner = re.search(r"^  ([\w ]*)#.*// \w+=class "
                                  + re.escape(name.replace(".", "/"))
                                  + r" of class ", block, re.M)
                visible = bool(inner and re.search(r"\b(public|protected)\b", inner[1]))
                static = bool(inner and "static" in inner[1].split())
            members = re.findall(r"^  ((?:public|protected) .+;)\r?\n"
                                 r"    descriptor: [^\n]+\n    flags: ([^\n]+)", block, re.M)
            classes[name] = {"header": header[0], "kind": kind, "visible": visible,
                             "static": static, "enum": "ACC_ENUM" in flags,
                             "members": [(decl[:-1], flags) for decl, flags in members
                                         if "ACC_SYNTHETIC" not in flags and "ACC_BRIDGE" not in flags]}
    # A public nested type is accessible only when its enclosing types are too.
    def visible(name):
        return classes[name]["visible"] and ("$" not in name or visible(name.rsplit("$", 1)[0]))
    return {name: cls for name, cls in classes.items() if visible(name)}


def parameters(declaration):
    """Split parameter types on commas outside generic arguments."""
    contents = declaration.split("(", 1)[1].split(")", 1)[0]
    result, depth, start = [], 0, 0
    for pos, char in enumerate(contents):
        depth += (char == "<") - (char == ">")
        if char == "," and depth == 0:
            result.append(contents[start:pos].strip())
            start = pos + 1
    if contents:
        result.append(contents[start:].strip())
    return result


def constructors(name, cls):
    return [decl for decl, _ in cls["members"] if name + "(" in decl]


def super_call(cls, classes):
    parent = re.search(r"\bextends ([\w.$]+)", cls["header"])
    if not parent:
        return ""
    parent = parent[1]
    if parent == "java.security.BasicPermission":
        return 'super("stub"); '  # LinkPermission: BasicPermission has no no-arg constructor.
    if parent not in classes:
        return ""  # The other external NIO superclasses have accessible no-arg constructors.
    choices = constructors(parent, classes[parent])
    if not choices or any(not parameters(ctor) for ctor in choices):
        return ""
    arguments = []
    for type_name in parameters(choices[0]):
        type_name = type_name.replace("$", ".").replace("...", "[]")
        value = "false" if type_name == "boolean" else (
            "0" if type_name in {"byte", "short", "int", "long", "char", "float", "double"} else "null")
        arguments.append(f"({type_name}) {value}")
    return "super(" + ", ".join(arguments) + "); "


def render(name, classes, indent=""):
    cls = classes[name]
    simple = name.rsplit(".", 1)[1].rsplit("$", 1)[-1]
    header = cls["header"].replace(name, simple, 1).replace("$", ".")
    if cls["enum"]:
        header = re.sub(r"\bfinal class\b", "enum", header)
        header = re.sub(r" extends java.lang.Enum<[^>]+>", "", header)
    elif cls["static"]:
        header = header.replace("public ", "public static ", 1)
    lines = [indent + header + " {"]
    body = indent + "    "
    if cls["enum"]:
        constants = [decl.rsplit(" ", 1)[1] for decl, flags in cls["members"] if "ACC_ENUM" in flags]
        lines.append(body + ", ".join(constants) + ";")
    elif cls["kind"] == "class" and not constructors(name, cls):
        # Prevent an accidental public default constructor; also lets sibling stubs extend buffers.
        lines.append(body + simple + "() { " + super_call(cls, classes) + THROW + " }")
    for declaration, flags in cls["members"]:
        if "ACC_ENUM" in flags:
            continue
        if cls["enum"] and re.search(r"\b(values|valueOf)\(", declaration):
            continue  # javac generates these two enum methods itself.
        constructor = name + "(" in declaration
        declaration = declaration.replace(name + "(", simple + "(").replace("$", ".")
        if "(" not in declaration:
            if " = " not in declaration:
                type_name = declaration.rsplit(" ", 2)[-2]
                value = "false" if type_name == "boolean" else (
                    "0" if type_name in {"byte", "short", "int", "long", "char", "float", "double"} else "null")
                declaration += " = " + value
            lines.append(body + declaration + ";")
            continue
        args = ", ".join(f"{type_name} p{i}" for i, type_name in enumerate(parameters(declaration)))
        declaration = re.sub(r"\([^)]*\)", lambda _: "(" + args + ")", declaration, count=1)
        if "ACC_ABSTRACT" in flags:
            lines.append(body + declaration + ";")
        else:
            declaration = re.sub(r"\b(native|synchronized) ", "", declaration)
            prefix = super_call(cls, classes) if constructor else ""
            lines.append(body + declaration + " { " + prefix + THROW + " }")
    for nested in classes:
        if "$" in nested and nested.rsplit("$", 1)[0] == name:
            lines.extend(render(nested, classes, body))
    lines.append(indent + "}")
    return lines


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-home", default=os.environ.get("JAVA_HOME"), help="Override JAVA_HOME")
    args = parser.parse_args()
    jdk = find_jdk(args.java_home)
    suffix = ".exe" if os.name == "nt" else ""
    javap, javac = (jdk / "bin" / (tool + suffix) for tool in ("javap", "javac"))
    jmod = jdk / "jmods/java.base.jmod"
    if not all(path.is_file() for path in (javap, javac, jmod)):
        raise ValueError(f"A full JDK 9+ with javap, javac and jmods is required: {jdk}")
    version = run(str(javap), "-version").strip()
    print(f"Reading java.nio APIs from JDK {version}: {jdk}", flush=True)
    with zipfile.ZipFile(jmod) as archive:
        names = sorted(entry[len("classes/"):-len(".class")].replace("/", ".")
                       for entry in archive.namelist()
                       if entry.startswith("classes/java/nio/") and entry.endswith(".class"))
    classes = read_api(javap, names)
    if not classes:
        raise ValueError("No public NIO types found")
    base = Path(__file__).resolve().parent
    output = base / "generated"
    # Only replace this script's fixed output folder, never a symlink or junction target.
    if output.resolve().parent != base or output.is_symlink():
        raise ValueError(f"Refusing to replace linked output directory: {output}")
    with tempfile.TemporaryDirectory(prefix="nio-stubs-", dir=base) as temporary:
        stage = Path(temporary)
        sources = []
        for name in classes:
            if "$" in name:
                continue
            path = stage / "src" / (name.replace(".", "/") + ".java")
            path.parent.mkdir(parents=True, exist_ok=True)
            package = name.rsplit(".", 1)[0]
            path.write_text(f"// Generated from JDK {version}. Do not edit.\npackage {package};\n\n"
                            + "\n".join(render(name, classes)) + "\n", encoding="utf-8")
            sources.append(path)
        source_list = stage / "sources.txt"
        source_list.write_text("\n".join('"' + str(path).replace("\\", "/") + '"' for path in sources), encoding="utf-8")
        print(f"Checking {len(classes)} types in {len(sources)} Java files with javac...", flush=True)
        subprocess.run([str(javac), "-encoding", "UTF-8", "--patch-module", f"java.base={stage / 'src'}",
                        "-d", str(stage / "classes"), "@" + str(source_list)], check=True)
        cache = base / ".cache"
        cache.mkdir(exist_ok=True)
        write_jdk_api(jmod, cache / "jdk-reference.jar")
        (stage / "jdk.txt").write_text(f"JDK {version}\n{jdk}\n{len(classes)} public/protected types\n"
                                        f"{len(sources)} source files\n", encoding="utf-8")
        source_list.unlink()  # Its absolute paths are temporary.
        if output.exists():
            shutil.rmtree(output)
        shutil.move(str(stage), str(output))
    print(f"Done. Sources: {output / 'src'}\nCompiled classes: {output / 'classes'}")


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError, subprocess.CalledProcessError) as error:
        raise SystemExit(str(error)) from error
