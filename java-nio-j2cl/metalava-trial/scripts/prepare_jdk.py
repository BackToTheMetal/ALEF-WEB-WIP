#!/usr/bin/env python3
"""Read a selected JDK's class declarations without loading or executing them."""
import argparse
import hashlib
import json
import shutil
import subprocess
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def sha256(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def prepare(jdk, source_zip=None):
    jdk = jdk.resolve()
    jmod = jdk / "jmods/java.base.jmod"
    if not jmod.is_file():
        raise SystemExit(f"A full JDK with jmods/java.base.jmod is required: {jdk}")
    version = subprocess.check_output([str(jdk / "bin/java"), "-version"], stderr=subprocess.STDOUT, text=True)
    if 'version "21.' not in version:
        raise SystemExit("This trial's J2CL policy targets JDK 21. Select a JDK 21 installation.")
    output = ROOT / "build/jdk"
    output.mkdir(parents=True, exist_ok=True)
    counts = {}
    with zipfile.ZipFile(jmod) as source:
        for filename, prefix in [("java-base.jar", "classes/"), ("nio-input.jar", "classes/java/nio/")]:
            entries = sorted(name for name in source.namelist()
                             if name.startswith(prefix) and name.endswith(".class")
                             and name != "classes/module-info.class")
            with zipfile.ZipFile(output / filename, "w", compression=zipfile.ZIP_DEFLATED) as target:
                for name in entries:
                    info = zipfile.ZipInfo(name.removeprefix("classes/"), (1980, 1, 1, 0, 0, 0))
                    info.compress_type = zipfile.ZIP_DEFLATED
                    target.writestr(info, source.read(name))
            counts[filename] = len(entries)
    release = dict(line.split("=", 1) for line in (jdk / "release").read_text().splitlines() if "=" in line)
    manifest = {
        "jdk_home": str(jdk), "java_version": version.strip(), "release": release,
        "input": "JDK java.base.jmod class declarations; no reflection or implementation execution",
        "scope": "java.nio and every subpackage; public/protected API selected by Metalava",
        "java_base_jmod_sha256": sha256(jmod), "class_entries": counts,
        "jar_sha256": {name: sha256(output / name) for name in counts},
        "metalava": "com.android.tools.metalava:metalava:1.0.0-alpha15",
    }
    if source_zip:
        source_zip = source_zip.resolve()
        source_root = output / "sources"
        if source_root.exists():
            if not source_root.resolve().is_relative_to(ROOT.resolve()):
                raise ValueError("Unexpected generated source directory")
            shutil.rmtree(source_root)
        files = []
        with zipfile.ZipFile(source_zip) as source:
            for name in sorted(source.namelist()):
                if not name.startswith("java.base/java/nio/") or not name.endswith(".java"):
                    continue
                path = (source_root / name).resolve()
                if not path.is_relative_to(source_root.resolve()):
                    raise ValueError(f"Invalid source archive entry: {name}")
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_bytes(source.read(name))
                files.append(path.relative_to(ROOT).as_posix())
        (output / "nio-sources.list").write_text("\n".join(files) + "\n")
        manifest["source_zip"] = str(source_zip)
        manifest["source_zip_sha256"] = sha256(source_zip)
        manifest["source_files"] = len(files)
    reports = ROOT / "reports"
    reports.mkdir(exist_ok=True)
    (reports / "input.json").write_text(json.dumps(manifest, indent=2) + "\n")
    print(json.dumps({"jdk_home": str(jdk), "classes": counts}, indent=2))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jdk-home", type=Path, required=True)
    parser.add_argument("--source-zip", type=Path)
    args = parser.parse_args()
    prepare(args.jdk_home, args.source_zip)
