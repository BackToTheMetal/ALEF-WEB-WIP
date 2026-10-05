#!/usr/bin/env python3

import argparse
import datetime
import glob
import json
import pathlib
import re
import shutil
import subprocess
import tempfile
import zipfile


def parse_arguments():
    parser = argparse.ArgumentParser(description="Export the public API emitted by WebAWT J2CL library jars.")
    parser.add_argument("--output", default="compatibility/webawt-awt-api.json")
    parser.add_argument("--jar-pattern", action="append", dest="jar_patterns")
    return parser.parse_args()


def find_jars(patterns, ignored_names=()):
    jars = sorted({path for pattern in patterns for path in glob.glob(pattern) if pathlib.Path(path).name not in ignored_names})
    if not jars:
        raise RuntimeError("No WebAWT J2CL jars were found. Run bazelisk build //src/main/java/... first.")
    return jars


def declared_jars():
    bazel = shutil.which("bazelisk") or shutil.which("bazel")
    if bazel is None:
        raise RuntimeError("Bazel is required to identify current production output jars")
    query = "filter('-j2cl$', //src/main/java/...)"
    labels = subprocess.run([bazel, "query", query, "--output=label"], check=True, capture_output=True, text=True).stdout.splitlines()
    if not labels:
        raise RuntimeError("Bazel declared no production J2CL targets")
    result = subprocess.run([bazel, "cquery", "set(" + " ".join(labels) + ")", "--output=files"], capture_output=True, text=True)
    if result.returncode:
        raise RuntimeError(f"Could not resolve declared production jars:\n{result.stderr}")
    jars = sorted({line.strip() for line in result.stdout.splitlines() if line.strip().endswith("-j2cl.jar")})
    if not jars:
        raise RuntimeError("Bazel declared no production J2CL jars")
    missing = [path for path in jars if not pathlib.Path(path).is_file()]
    if missing:
        raise RuntimeError(f"Build //... before exporting APIs; missing outputs: {missing}")
    return jars


def read_classes(jars):
    classes = {}
    origins = {}
    for jar_path in jars:
        with zipfile.ZipFile(jar_path) as jar:
            for entry in jar.namelist():
                if not entry.startswith("java/awt/") or not entry.endswith(".class") or "$" in entry:
                    continue
                class_name = entry[:-6].replace("/", ".")
                class_bytes = jar.read(entry)
                if class_name in classes and classes[class_name] != class_bytes:
                    raise RuntimeError(f"Conflicting definitions for {class_name}: {origins[class_name]} and {jar_path}")
                classes[class_name] = class_bytes
                origins[class_name] = jar_path
    return classes, origins


def inspect_class(class_name, class_bytes, origin, directory):
    class_file = pathlib.Path(directory, *class_name.split(".")).with_suffix(".class")
    class_file.parent.mkdir(parents=True, exist_ok=True)
    class_file.write_bytes(class_bytes)
    result = subprocess.run(["javap", "-public", str(class_file)], check=True, capture_output=True, text=True)
    lines = result.stdout.splitlines()
    declaration = next((line.strip() for line in lines if re.match(r"^public\s+.*(?:class|interface|enum|record|@interface)\s+", line)), None)
    if declaration is None:
        return None
    members = [line.strip() for line in lines if re.match(r"^  (?:public|protected) ", line)]
    return {
        "name": class_name,
        "package": class_name.rsplit(".", 1)[0],
        "declaration": declaration,
        "members": members,
        "origin": origin,
    }


def main():
    arguments = parse_arguments()
    jars = find_jars(arguments.jar_patterns) if arguments.jar_patterns else [path for path in declared_jars() if pathlib.PurePosixPath(path).parent.as_posix().endswith(("/src/main/java/java/awt", "/src/main/java/java/awt/color"))]
    classes, origins = read_classes(jars)
    with tempfile.TemporaryDirectory(prefix="webawt-api-") as directory:
        inspected_types = [inspect_class(name, classes[name], origins[name], directory) for name in sorted(classes)]
        types = [type_entry for type_entry in inspected_types if type_entry is not None]
    inventory = {
        "schemaVersion": 1,
        "generatedAtUtc": datetime.datetime.now(datetime.timezone.utc).isoformat(),
        "sourceJars": jars,
        "publicTopLevelTypeCount": len(types),
        "types": types,
    }
    output_path = pathlib.Path(arguments.output)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(inventory, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote {len(types)} emitted WebAWT types to {output_path}")


if __name__ == "__main__":
    main()
