load("@j2cl//build_defs:rules.bzl", "j2cl_library")
load("@mps_j2cl//:mps-browser-projects.bzl", "MPS_BROWSER_NAMES")
load(":runtime-sources.bzl", "RUNTIME_SOURCES")

_MPS_DEPS = ["@mps_j2cl//:mps_" + (p[4:] if p.startswith("mps-") and p != "mps-platform" else p).replace("-", "_").lower() for p in MPS_BROWSER_NAMES] + [
    "@mps_j2cl//:intellij_stubs", "@webawt//:webawt", "@javax-swing-j2cl//src/main:javax-swing-j2cl",
    "@mps_j2cl//:extension_annotations", "@gnu_trove_j2cl//:trove-j2cl",
]

def extension_runtimes():
    dependencies = {
        "reflection": [],
        "hacks": [":reflection"],
        "querylist": [":hacks", ":reflection"],
        "boolean_editor": [],
        "math_editor": [":hacks", ":reflection"],
        "math_symbols": [":math_editor"],
        "grammarcells": [":reflection", ":grammarcells_language"],
        "grammarcells_language": [],
    }
    for name, srcs in RUNTIME_SOURCES.items():
        j2cl_library(name = name, srcs = srcs, deps = _MPS_DEPS + dependencies[name])
    j2cl_library(name = "runtime", exports = [":" + name for name in RUNTIME_SOURCES])
