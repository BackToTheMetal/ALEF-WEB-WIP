#!/usr/bin/env python3
"""Inventory a committed MPS Java/Kotlin tree without changing that checkout.

This is a lexical inventory, not Java/Kotlin symbol resolution. Git reads the
selected commit, so generated tracked sources count and working edits do not.
"""

import argparse
from collections import Counter, defaultdict
import json
from pathlib import Path
import re
import subprocess


IMPORT = re.compile(r"(?m)^\s*import\s+(?:static\s+)?(java\.util\.concurrent(?:\.[\w*$]+)+)(?:\s+as\s+\w+)?\s*;?")
QUALIFIED = re.compile(r"\bjava\.util\.concurrent(?:\.[a-z][\w$]*)*\.[A-Z][\w$]*")
THREAD = re.compile(r"\b(?:java\.lang\.)?(Thread|ThreadLocal|InheritableThreadLocal)\b")
COMMENTS_STRINGS = re.compile(r'//[^\n]*|/\*[\s\S]*?\*/|"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'')


def strip_comments_strings(source):
    return COMMENTS_STRINGS.sub(lambda match: "\n" * match.group().count("\n") or " ", source)


def class_name(qualified):
    parts = qualified.split(".")
    for index, part in enumerate(parts):
        if part and part[0].isupper():
            return ".".join(parts[:index + 1])
    return None


def inspect_file(path, source):
    clean = strip_comments_strings(source)
    imports = sorted(set(IMPORT.findall(clean)))
    classes = set(filter(None, (class_name(item) for item in imports)))
    classes.update(QUALIFIED.findall(clean))
    classes.update("java.lang." + match.group(1) for match in THREAD.finditer(clean))
    if not classes and not imports:
        return None
    body = re.sub(r"(?m)^\s*import\s+[^\n]*", "", clean)
    methods = {}
    for qualified in sorted(classes):
        short = qualified.rsplit(".", 1)[-1]
        escaped = re.escape(short)
        receiver_names = set(re.findall(r"(?<![\w$.<])" + escaped + r"\s*(?:<[^;{}=()]*>)?\s+(\w+)\b", body))
        uses = Counter(re.findall(r"\b" + escaped + r"\s*\.\s*(\w+)\s*\(", body))
        constructors = len(re.findall(r"\bnew\s+(?:[\w.]+\.)?" + escaped + r"\s*(?:<[^;{}=()]*>)?\s*\(", body))
        if constructors:
            uses["<init>"] = constructors
        for receiver in receiver_names:
            uses.update(re.findall(r"\b" + re.escape(receiver) + r"\s*\.\s*(\w+)\s*\(", body))
            uses.update(re.findall(r"\b" + re.escape(receiver) + r"\s*::\s*(\w+)", body))
        if uses:
            methods[qualified] = dict(sorted(uses.items()))
    thread_sites = []
    for lineno, line in enumerate(clean.splitlines(), 1):
        operations = sorted(set(re.findall(r"\bThread\s*\.\s*(\w+)\s*\(", line)))
        operations += ["constructor"] if re.search(r"\bnew\s+Thread\s*\(", line) else []
        operations += ["subclass"] if re.search(r"\bextends\s+Thread\b", line) else []
        if operations:
            thread_sites.append({"line": lineno, "operations": operations})
    return {
        "path": path,
        "generated": "source_gen/" in path,
        "test_path": bool(re.search(r"(?:^|/)(?:test|tests|testbench)(?:/|$)", path)),
        "classes": sorted(classes),
        "imports": imports,
        "method_patterns": methods,
        "thread_sites": thread_sites,
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("checkout", type=Path)
    parser.add_argument("--revision", default="HEAD")
    parser.add_argument("--output", type=Path, default=Path("compatibility/mps-concurrent-usage.json"))
    args = parser.parse_args()
    checkout = args.checkout.resolve()
    git = ["git", "-c", "safe.directory=" + checkout.as_posix(), "-C", str(checkout)]

    def run(*arguments):
        return subprocess.check_output(git + list(arguments))

    commit = run("rev-parse", "--verify", args.revision + "^{commit}").decode().strip()
    tree = run("ls-tree", "-r", "--name-only", "-z", commit).decode().split("\0")
    source_paths = [path for path in tree if path.endswith((".java", ".kt"))]
    search = subprocess.run(git + ["grep", "-I", "-l", "-z", "-E", r"java\.util\.concurrent|\b(Thread|ThreadLocal|InheritableThreadLocal)\b", commit, "--", "*.java", "*.kt"], stdout=subprocess.PIPE, check=False)
    if search.returncode not in (0, 1):
        raise SystemExit(search.returncode)
    blob_names = sorted(name for name in search.stdout.decode().split("\0") if name)
    batch = subprocess.run(git + ["cat-file", "--batch"], input=("\n".join(blob_names) + "\n").encode(), stdout=subprocess.PIPE, check=True).stdout
    files = []
    offset = 0
    for blob_name in blob_names:
        header_end = batch.index(b"\n", offset)
        _, kind, size = batch[offset:header_end].split()
        if kind != b"blob":
            raise ValueError("Expected blob: " + blob_name)
        start = header_end + 1
        end = start + int(size)
        record = inspect_file(blob_name.split(":", 1)[1], batch[start:end].decode("utf-8", errors="replace"))
        if record:
            files.append(record)
        offset = end + 1

    class_files = defaultdict(list)
    class_methods = defaultdict(Counter)
    for record in files:
        for qualified in record["classes"]:
            class_files[qualified].append(record["path"])
        for qualified, methods in record["method_patterns"].items():
            class_methods[qualified].update(methods)
    concurrent = sorted(name for name in class_files if name.startswith("java.util.concurrent."))
    payload = {
        "schema_version": 1,
        "commit": commit,
        "source": "Git committed tree; tracked .java and .kt only; generated sources included",
        "limitations": [
            "Lexical scan strips comments and string literals; it does not resolve symbols or overloads.",
            "Class imports count even if unused. Wildcard imports are recorded but not expanded.",
            "Instance method patterns use same-file declarations: they may misattribute shadowed names and miss chained calls, inherited fields, Kotlin inference, or fully qualified declarations.",
            "java.lang.Thread and thread-local token matches may include unrelated classes with those names.",
            "MPS model files and bundled IntelliJ/dependency binaries are outside this inventory.",
            "An import inventory is not proof that MPS compiles or runs with this library.",
        ],
        "summary": {
            "tracked_java_kotlin_files": len(source_paths),
            "files_with_concurrent_or_thread_references": len(files),
            "files_with_concurrent_references": sum(any(name.startswith("java.util.concurrent.") for name in record["classes"]) or bool(record["imports"]) for record in files),
            "unique_concurrent_classes": len(concurrent),
            "files_with_thread_or_threadlocal_references": sum(any(name.startswith("java.lang.") for name in record["classes"]) for record in files),
            "files_with_wildcard_imports": sum(any("*" in name for name in record["imports"]) for record in files),
            "by_top_level_directory": dict(sorted(Counter(record["path"].split("/", 1)[0] for record in files).items())),
        },
        "classes": [
            {"name": name, "file_count": len(paths), "files": paths, "method_patterns": dict(sorted(class_methods[name].items()))}
            for name, paths in sorted(class_files.items())
        ],
        "files": files,
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, indent=2, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")
    print(json.dumps(payload["summary"], indent=2))


if __name__ == "__main__":
    main()
