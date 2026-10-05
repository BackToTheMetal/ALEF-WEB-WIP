"""Contracts for the browser's actual MPS keymap and selection adapters."""
load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def editor_runtime_tests():
    j2cl_library(
        name = "browser_selection_factories",
        srcs = ["browser/src/mpsj2cl/editor/BrowserSelectionFactories.java"],
        deps = [":mps_editor_runtime", ":mps_kernel"],
    )
    j2cl_library(
        name = "editor_runtime_contracts_test",
        srcs = ["tests/editor_runtime/EditorRuntimeContractsTest.java", "tests/runtime_factories/RuntimeTestLoaders.java"],
        deps = [":mps_editor_runtime", ":mps_kernel", ":browser_selection_factories", "@org_jdom_j2cl//:jdom", "@webawt//:webawt"],
    )
    closure_js_library(
        name = "editor_runtime_contracts_entry",
        srcs = ["tests/editor_runtime/entry.js"],
        deps = [":editor_runtime_contracts_test"],
    )
    j2cl_application(
        name = "editor_runtime_contracts_browser",
        entry_points = ["mpsj2cl.editorcontracts.entry"],
        deps = [":editor_runtime_contracts_entry"],
        jre_class_metadata = "SIMPLE",
    )
