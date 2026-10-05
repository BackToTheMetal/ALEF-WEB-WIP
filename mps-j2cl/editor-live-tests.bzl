"""Real editor integration with a deliberately handwritten runtime language."""
load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def editor_live_tests():
    j2cl_library(
        name = "editor_live_test",
        srcs = ["tests/editor_live/ExpressionLanguage.java", "tests/editor_live/EditorLiveTest.java", "tests/editor_live/EditorResourceContracts.java", "tests/editor_live/FindUsagesContracts.java"],
        deps = [":browser_editor_host", ":browser_model_access", ":intellij_stubs", ":mps_editor_runtime", ":mps_actions_runtime", ":mps_intentions_runtime",
                ":mps_mps_platform", ":mps_icons", ":mps_kernel", ":mps_typesystemengine", ":mps_findusages_runtime",
                ":mps_typechecking", ":mps_typesystemintegration", ":mps_editor_api", "@webawt//:webawt", "@javax-swing-j2cl//src/main:javax-swing-j2cl"],
    )
    closure_js_library(name = "editor_live_entry", srcs = ["tests/editor_live/entry.js"], deps = [":editor_live_test"])
    j2cl_application(name = "editor_live_browser", entry_points = ["mpsj2cl.editorlive.entry"], deps = [":editor_live_entry"], jre_class_metadata = "SIMPLE")
    j2cl_application(name = "editor_live_stripped", entry_points = ["mpsj2cl.editorlive.entry"], deps = [":editor_live_entry"], jre_class_metadata = "STRIPPED")
