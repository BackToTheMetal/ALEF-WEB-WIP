"""Project-sized MPS builds and sparse, explicitly reviewed source overlays."""

load("@j2cl//build_defs:rules.bzl", "j2cl_library")
load(":mps-overrides.bzl", "MPS_OVERRIDES")
load(":mps-browser-projects.bzl", MPS_COMPILE_GROUPS = "MPS_BROWSER_COMPILE_GROUPS", MPS_INITIAL_PROJECTS = "MPS_BROWSER_NAMES", MPS_PROJECTS = "MPS_BROWSER_PROJECTS")

_LIBRARIES = {
    "Annotations": "@jetbrains_annotations//:annotations",
    "gnu.trove": "@gnu_trove_j2cl//:trove-j2cl",
    "org.jdom": "@org_jdom_j2cl//:jdom",
    "Idea-platform": ":intellij_stubs",
}

def _name(project):
    # Both core/platform and workbench/mps-platform are upstream projects.
    if project == "mps-platform":
        return "mps_mps_platform"
    suffix = project[4:] if project.startswith("mps-") else project
    return "mps_" + suffix.replace("-", "_").lower()

def _group_name(group):
    return "_mps_" + group

def _sources(project):
    info = MPS_PROJECTS[project]
    return [
        "mps-web/" + MPS_OVERRIDES[path] if path in MPS_OVERRIDES else "mps/" + path
        for path in info["srcs"]
    ]

def _compile(name, members):
    sources = []
    dependencies = {"@jetbrains_annotations//:annotations": True}
    exports = {}
    for member in members:
        info = MPS_PROJECTS[member]
        sources += _sources(member)
        for dependency in info.get("bazel_deps", []):
            dependencies[dependency] = True
        if member in ["util-core", "kernel"]:
            dependencies["@j2cl//:jsinterop-annotations-j2cl"] = True
        if member == "kernel":
            dependencies["@org_jdom_j2cl//:sax-api"] = True
            dependencies["@webawt//:webawt"] = True
            dependencies["@javax-swing-j2cl//src/main:javax-swing-j2cl"] = True
        for dependency in info["compile_deps"]:
            if dependency not in members:
                dependencies[":" + _name(dependency)] = True
        for library in info["compile_libraries"]:
            if library not in _LIBRARIES:
                fail("Unresolved MPS library %s required by %s" % (library, member))
            dependencies[_LIBRARIES[library]] = True
        for dependency in info["exported_deps"]:
            if dependency not in members:
                exports[":" + _name(dependency)] = True
        for library in info["exported_libraries"]:
            exports[_LIBRARIES[library]] = True
    j2cl_library(
        name = name,
        srcs = sorted(sources),
        deps = sorted(dependencies.keys()) if sources else [],
        exports = sorted(exports.keys()),
        visibility = ["//visibility:public"] if len(members) == 1 else ["//visibility:private"],
    )

def mps_projects():
    """Declare reviewed browser projects, retaining one public target per project."""
    grouped = {}
    for group, members in MPS_COMPILE_GROUPS.items():
        _compile(_group_name(group), members)
        for member in members:
            grouped[member] = group
    for project in MPS_INITIAL_PROJECTS:
        if project in grouped:
            native.alias(
                name = _name(project),
                actual = ":" + _group_name(grouped[project]),
                visibility = ["//visibility:public"],
            )
        else:
            _compile(_name(project), [project])
        native.filegroup(
            name = _name(project) + "_sources",
            srcs = _sources(project),
            visibility = ["//visibility:public"],
        )
        native.filegroup(
            name = _name(project) + "_resources",
            srcs = ["mps/" + path for path in MPS_PROJECTS[project]["resources"]],
            visibility = ["//visibility:public"],
        )
