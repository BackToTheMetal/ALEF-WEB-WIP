#!/usr/bin/env python3

"""Export public/protected AWT and shipped prerequisite APIs, including accessible nested types."""

import argparse
import datetime
import json
import pathlib
import re
import shutil
import subprocess
import tempfile
import zipfile

from api_classfile import accessible_classes
from compare_webawt_api import load_json
from export_webawt_api import declared_jars


DECLARATION = re.compile(r"^(?:public|protected)\s+.*?\b(?:class|interface|enum|record|@interface)\s+([^\s<{]+)")


def read_archives(archives, prefixes, jmod=False):
    classes = {}
    for archive in archives:
        with zipfile.ZipFile(archive) as content:
            for entry in content.namelist():
                relative = entry.removeprefix("classes/") if jmod else entry
                if not relative.endswith(".class") or not relative.startswith(prefixes):
                    continue
                name = relative[:-6].replace("/", ".")
                data = content.read(entry)
                if name in classes and classes[name] != data:
                    raise ValueError(f"Conflicting emitted definitions for {name}: {archive}")
                classes[name] = data
    return classes


def parse_javap(output, metadata):
    types = []
    current = None
    for line in output.splitlines():
        match = DECLARATION.match(line)
        if match:
            name = match.group(1)
            if name not in metadata:
                raise ValueError(f"Unexpected javap declaration: {name}")
            current = {"name": name, "package": name.rsplit(".", 1)[0], "declaration": line.strip(), "members": [], **metadata[name]}
            types.append(current)
        elif current is not None and re.match(r"^  (?:public|protected) ", line):
            current["members"].append(line.strip())
    return types


def inspect_classes(classes, javap):
    metadata = accessible_classes(classes)
    with tempfile.TemporaryDirectory(prefix="webawt-extended-api-") as directory:
        paths = []
        for name in sorted(metadata):
            path = pathlib.Path(directory, *name.split(".")).with_suffix(".class")
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(classes[name])
            paths.append(str(path))
        types = []
        for start in range(0, len(paths), 40):
            output = subprocess.run([str(javap), "-protected", *paths[start:start + 40]], check=True, capture_output=True, text=True).stdout
            types.extend(parse_javap(output, metadata))
    names = [entry["name"] for entry in types]
    if len(names) != len(set(names)) or set(names) != set(metadata):
        raise ValueError("javap did not emit each accessible class exactly once")
    return sorted(types, key=lambda entry: entry["name"])


def inventory(types, identity):
    return {"schemaVersion": 1, "generatedAtUtc": datetime.datetime.now(datetime.timezone.utc).isoformat(), "scope": "Public/protected AWT, accessible nested types, and the shipped prerequisite type families", "source": identity, "typeCount": len(types), "nestedTypeCount": sum(entry["enclosingType"] is not None for entry in types), "memberCount": sum(len(entry["members"]) for entry in types), "types": types}


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-home", type=pathlib.Path, help="Reference JDK 17 installation; defaults to the JDK containing javap on PATH. Its jmods may also reside on a mounted Windows drive.")
    parser.add_argument("--output-directory", type=pathlib.Path, default=pathlib.Path("compatibility"))
    arguments = parser.parse_args()
    javap = shutil.which("javap")
    if arguments.java_home is None and javap is None:
        raise RuntimeError("JDK 17 javap is required")
    java_home = arguments.java_home or pathlib.Path(javap).resolve().parent.parent
    if javap is None:
        raise RuntimeError("A local javap executable is required to inspect the class files")
    release = (java_home / "release").read_text(encoding="utf-8")
    match = re.search(r'^JAVA_VERSION="([^"]+)"', release, re.MULTILINE)
    if match is None:
        raise RuntimeError(f"Cannot identify the reference JDK in {java_home}")
    version = match.group(1)
    if not version.startswith("17."):
        raise RuntimeError(f"Reference API requires JDK 17, found {version}; use --java-home")
    archives = declared_jars()
    actual_classes = read_archives(archives, ("java/", "javax/"))
    actual = inspect_classes(actual_classes, javap)
    baseline = load_json("compatibility/openjdk-awt-api.json")
    awt_packages = set(baseline["exportedPackages"])
    prerequisite_roots = set(load_json("compatibility/prerequisite-types.json")["types"])
    reference_classes = read_archives([java_home / "jmods/java.desktop.jmod", java_home / "jmods/java.base.jmod", java_home / "jmods/java.datatransfer.jmod"], ("java/awt/", "java/beans/", "java/net/", "java/text/", "javax/accessibility/"), jmod=True)
    reference_classes = {name: data for name, data in reference_classes.items() if name.rsplit(".", 1)[0] in awt_packages or name.split("$")[0] in prerequisite_roots}
    reference = inspect_classes(reference_classes, javap)
    write_json(arguments.output_directory / "openjdk-extended-api.json", inventory(reference, f"OpenJDK {version} java.desktop/java.base/java.datatransfer"))
    write_json(arguments.output_directory / "webawt-extended-api.json", inventory(actual, "Bazel-emitted project J2CL jars"))
    print(f"Extended inventory: {len(actual)} emitted / {len(reference)} reference types, including accessible nested types and protected members")


if __name__ == "__main__":
    main()
