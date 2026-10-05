"""Shared Java utility contracts needed by the browser editor profile."""
load("@j2cl//build_defs:rules.bzl", "j2cl_application", "j2cl_library")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

def editor_jre_tests():
    j2cl_library(
        name = "keystroke_test",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/keystroke/KeyStrokeTest.java"],
        deps = [
            "@javax-swing-j2cl//src/main:javax-swing-j2cl",
            "@webawt//src/main/java/java/awt:graphics-api-j2cl",
        ],
    )
    closure_js_library(
        name = "keystroke_entry",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/keystroke/entry.js"],
        deps = [":keystroke_test"],
    )
    j2cl_application(
        name = "keystroke_browser",
        entry_points = ["dev.webswing.keystroke.entry"],
        deps = [":keystroke_entry"],
        jre_class_metadata = "SIMPLE",
    )
    j2cl_library(
        name = "editor_utilities_test",
        srcs = ["tests/editor_utilities/EditorUtilitiesTest.java"],
    )
    closure_js_library(
        name = "editor_utilities_entry",
        srcs = ["tests/editor_utilities/entry.js"],
        deps = [":editor_utilities_test"],
    )
    j2cl_application(
        name = "editor_utilities_browser",
        entry_points = ["mpsj2cl.editorutilities.entry"],
        deps = [":editor_utilities_entry"],
        jre_class_metadata = "SIMPLE",
    )
    j2cl_library(
        name = "swing_utilities_test",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_utilities/SwingUtilitiesTest.java"],
        deps = [
            "@javax-swing-j2cl//src/main:javax-swing-j2cl",
            "@webawt//src/main/java/java/awt:awt-j2cl",
            "@webawt//src/main/java/java/awt:graphics-api-j2cl",
        ],
    )
    closure_js_library(
        name = "swing_utilities_entry",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_utilities/entry.js"],
        deps = [":swing_utilities_test"],
    )
    j2cl_application(
        name = "swing_utilities_browser",
        entry_points = ["dev.webswing.swingutilities.entry"],
        deps = [":swing_utilities_entry"],
        jre_class_metadata = "SIMPLE",
    )
    j2cl_library(
        name = "swing_editor_test",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_editor/SwingEditorTest.java"],
        deps = [
            "@javax-swing-j2cl//src/main:javax-swing-j2cl",
            "@webawt//src/main/java/java/awt:awt-j2cl",
            "@webawt//src/main/java/java/awt:graphics-api-j2cl",
        ],
    )
    closure_js_library(
        name = "swing_editor_entry",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_editor/entry.js"],
        deps = [":swing_editor_test"],
    )
    j2cl_application(
        name = "swing_editor_browser",
        entry_points = ["dev.webswing.swingeditor.entry"],
        deps = [":swing_editor_entry"],
        jre_class_metadata = "SIMPLE",
    )
    j2cl_library(
        name = "swing_editor_native_test",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_editor/BrowserSwingEditorTest.java"],
        deps = [
            "@javax-swing-j2cl//src/main:javax-swing-j2cl",
            "@webawt//:webawt",
            "@elemental2//:elemental2-dom-j2cl",
        ],
    )
    closure_js_library(
        name = "swing_editor_native_entry",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_editor/native-entry.js"],
        deps = [":swing_editor_native_test"],
    )
    j2cl_application(
        name = "swing_editor_native_browser",
        entry_points = ["dev.webswing.swingeditor.nativeentry"],
        deps = [":swing_editor_native_entry"],
        jre_class_metadata = "SIMPLE",
    )
    j2cl_library(
        name = "swing_editor_controls_test",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_editor/SwingEditorControlsTest.java"],
        deps = [
            "@javax-swing-j2cl//src/main:javax-swing-j2cl",
            "@webawt//src/main/java/java/awt:awt-j2cl",
            "@webawt//src/main/java/java/awt:graphics-api-j2cl",
            "@webawt//src/main/java/java/awt:painting-j2cl",
        ],
    )
    closure_js_library(
        name = "swing_editor_controls_entry",
        srcs = ["@javax-swing-j2cl//:src/test/runtime/swing_editor/controls-entry.js"],
        deps = [":swing_editor_controls_test"],
    )
    j2cl_application(
        name = "swing_editor_controls_browser",
        entry_points = ["dev.webswing.swingeditor.controlsentry"],
        deps = [":swing_editor_controls_entry"],
        jre_class_metadata = "SIMPLE",
    )
