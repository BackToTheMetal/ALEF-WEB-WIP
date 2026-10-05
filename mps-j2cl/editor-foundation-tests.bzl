"""Root-owned editor foundation contracts and real type-engine fixture targets."""
load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def editor_foundation_tests():
    j2cl_library(name = "foundation_jre_test", srcs = ["tests/typechecking/FoundationJreTest.java"])
    closure_js_library(name = "foundation_jre_entry", srcs = ["tests/typechecking/jre-entry.js"], deps = [":foundation_jre_test"])
    j2cl_application(name = "foundation_jre_browser", entry_points = ["mpsj2cl.typechecking.jreEntry"], deps = [":foundation_jre_entry"], jre_class_metadata = "SIMPLE")

    j2cl_library(
        name = "typechecking_foundation_test",
        srcs = ["tests/typechecking/TypecheckingFoundationTest.java"],
        deps = [":mps_typesystemengine", ":mps_typechecking", ":mps_kernel", ":mps_openapi"],
    )
    closure_js_library(name = "typechecking_foundation_entry", srcs = ["tests/typechecking/entry.js"], deps = [":typechecking_foundation_test"])
    j2cl_application(name = "typechecking_foundation_browser", entry_points = ["mpsj2cl.typechecking.entry"], deps = [":typechecking_foundation_entry"], jre_class_metadata = "SIMPLE")
    j2cl_application(name = "typechecking_foundation_stripped", entry_points = ["mpsj2cl.typechecking.entry"], deps = [":typechecking_foundation_entry"], jre_class_metadata = "STRIPPED")

    j2cl_library(
        name = "browser_platform_test",
        srcs = ["tests/typechecking/BrowserPlatformTest.java"],
        deps = [":browser_platform", ":mps_typesystemengine", ":mps_typechecking", ":mps_kernel", ":mps_openapi", ":mps_project"],
    )
    closure_js_library(name = "browser_platform_entry", srcs = ["tests/typechecking/platform-entry.js"], deps = [":browser_platform_test"])
    j2cl_application(name = "browser_platform_browser", entry_points = ["mpsj2cl.typechecking.platformEntry"], deps = [":browser_platform_entry"], jre_class_metadata = "SIMPLE")
    j2cl_application(name = "browser_platform_stripped", entry_points = ["mpsj2cl.typechecking.platformEntry"], deps = [":browser_platform_entry"], jre_class_metadata = "STRIPPED")
