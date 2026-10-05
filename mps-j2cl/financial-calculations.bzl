"""Generated financialCalculations language application, separate from the runtime profile."""
load("@j2cl//build_defs:rules.bzl", "j2cl_library", "j2cl_application")
load("@rules_closure//closure:defs.bzl", "closure_js_library")
load(":financial-sources.bzl", "FINANCIAL_SOURCES")
load(":mps-browser-projects.bzl", "MPS_BROWSER_NAMES")

def financial_calculations():
    j2cl_library(
        name = "financial_languages",
        srcs = FINANCIAL_SOURCES,
        # Pinned generated code contains redundant rounding and duplicate boolean
        # terms. Preserve those upstream sources and keep these diagnostics visible.
        javacopts = [
            "-Xep:MathRoundIntLong:WARN",
            "-Xep:IdentityBinaryExpression:WARN",
        ],
        deps = [":mps_" + (p[4:] if p.startswith("mps-") and p != "mps-platform" else p).replace("-", "_").lower() for p in MPS_BROWSER_NAMES] + [
            ":intellij_stubs", "@webawt//:webawt", "@javax-swing-j2cl//src/main:javax-swing-j2cl",
            "@jetbrains_annotations//:annotations", "@gnu_trove_j2cl//:trove-j2cl", "@org_jdom_j2cl//:jdom", "@org_jdom_j2cl//:sax-api",
        ],
    )
    j2cl_library(
        name = "financial_demo",
        srcs = ["browser/financial/FinancialDemo.java", "browser/financial/FinancialLanguages.java", "browser/financial/FinancialClassLoader.java", "browser/financial/RuntimeTypesLanguage.java", "browser/financial/PrimitiveModelResource.java", "browser/financial/BrowserModelReader.java"],
        deps = [":financial_languages", ":browser_editor_host", ":browser_platform", ":mps_kernel", ":mps_editor_runtime", ":mps_openapi", ":mps_typechecking",
                "@webawt//:webawt", "@javax-swing-j2cl//src/main:javax-swing-j2cl", "@org_jdom_j2cl//:jdom", "@org_jdom_j2cl//:sax-api"],
    )
    closure_js_library(name = "financial_entry", srcs = ["browser/financial/entry.js"], deps = [":financial_demo"])
    j2cl_application(name = "financial_browser", entry_points = ["mpsj2cl.financial.entry"], deps = [":financial_entry"], jre_class_metadata = "SIMPLE")
