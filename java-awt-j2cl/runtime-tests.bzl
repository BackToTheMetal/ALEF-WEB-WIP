"""Library-owned portable and browser runtime contracts."""
load("@j2cl//build_defs:rules.bzl", "j2cl_library", "j2cl_application")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def runtime_tests():

    j2cl_library(name = "class_support_fixtures", srcs = ["src/test/runtime/class_support/FixtureTypes.java"])
    j2cl_library(name = "class_support", srcs = ["src/test/runtime/class_support/ClassSupportTest.java", "src/test/runtime/class_support/InteropBoundaryTest.java"], deps = [":class_support_fixtures", "@j2cl//:jsinterop-annotations-j2cl"])
    closure_js_library(name = "class_support_entry", srcs = ["src/test/runtime/class_support/entry.js"], deps = [":class_support"])
    j2cl_application(name = "class_support_browser", entry_points = ["org.webawt.classsupport.entry"], deps = [":class_support_entry"], jre_class_metadata = "SIMPLE")
    j2cl_application(name = "class_support_stripped", entry_points = ["org.webawt.classsupport.entry"], deps = [":class_support_entry"], jre_class_metadata = "STRIPPED")
