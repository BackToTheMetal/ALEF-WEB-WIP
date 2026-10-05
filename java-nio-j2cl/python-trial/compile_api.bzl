"""Expose stock J2CL's compile headers without including its JavaScript output."""

load("@rules_java//java:defs.bzl", "JavaInfo")

def _jre_headers_impl(ctx):
    java_info = ctx.attr.jre[JavaInfo]
    return [java_info, DefaultInfo(files = java_info.compile_jars)]

jre_headers = rule(
    implementation = _jre_headers_impl,
    attrs = {"jre": attr.label(default = "@j2cl//jre/java:jre_bootclasspath")},
)
