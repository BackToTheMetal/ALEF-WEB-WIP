"""Browser IntelliJ compatibility APIs, implemented without IntelliJ binaries."""

load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def intellij_stubs():
    j2cl_library(
        name = "intellij_stubs",
        srcs = native.glob(["third_party/intellij-stubs/src/**/*.java"]),
        deps = [
            "@jetbrains_annotations//:annotations",
            "@webawt//:webawt",
            "@javax-swing-j2cl//src/main:javax-swing-j2cl",
        ],
        visibility = ["//visibility:public"],
    )
    j2cl_library(
        name = "intellij_contracts_test",
        srcs = ["tests/intellij_stubs/IntelliJContractsTest.java", "tests/intellij_stubs/IntelliJBrowserTest.java"],
        deps = [":intellij_stubs", "@webawt//:webawt", "@javax-swing-j2cl//src/main:javax-swing-j2cl"],
    )
    closure_js_library(
        name = "intellij_contracts_entry",
        srcs = ["tests/intellij_stubs/entry.js"],
        deps = [":intellij_contracts_test"],
    )
    j2cl_application(
        name = "intellij_contracts_browser",
        entry_points = ["mpsj2cl.intellij.entry"],
        deps = [":intellij_contracts_entry"],
        jre_class_metadata = "SIMPLE",
    )
