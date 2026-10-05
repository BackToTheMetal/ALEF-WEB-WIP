load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def completion_matching_tests():
    j2cl_library(name = "completion_matching_test", srcs = ["tests/completion_matching/CompletionMatchingTest.java"], deps = [":intellij_stubs"])
    closure_js_library(name = "completion_matching_entry", srcs = ["tests/completion_matching/entry.js"], deps = [":completion_matching_test"])
    j2cl_application(name = "completion_matching_browser", entry_points = ["mpsj2cl.completion.entry"], deps = [":completion_matching_entry"])
