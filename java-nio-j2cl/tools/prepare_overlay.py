#!/usr/bin/env python3
"""Preserve and augment pinned J2CL sources; snapshot externally owned signature dependencies."""
import argparse
import hashlib
import json
import re
from pathlib import Path
from generate_stubs import API, ROOT, DEST, signature, fail

def write(relative, text):
    path = DEST / relative
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, newline="\n")

def append(text, declarations):
    offset = text.rfind('}')
    return text[:offset] + "\n  // Additional source compatibility declarations; operations are unsupported.\n" + declarations + "\n" + text[offset:]

def main():
    p = argparse.ArgumentParser()
    p.add_argument("--upstream", type=Path, required=True)
    p.add_argument("--io", type=Path, required=True)
    p.add_argument("--webawt", type=Path, required=True)
    args = p.parse_args()
    jre = args.upstream / "jre/java"
    charset = (jre / "java/nio/charset/Charset.java").read_text()
    charset = charset.replace("private final String name;", "private final String name;\n  private final java.util.Set<String> aliases;")
    charset = charset.replace("this.name = name;", "this.name = name;\n    this.aliases = aliasesIgnored == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(new java.util.HashSet<>(java.util.Arrays.asList(aliasesIgnored)));")
    existing = {"availableCharsets", "defaultCharset", "forName", "name", "compareTo", "hashCode", "equals", "toString"}
    extra = []
    c = next(c for c in API["classes"] if c["name"] == "java.nio.charset.Charset")
    for m in c["methods"]:
        if m["name"] in existing and not (m["name"] == "forName" and "," in m["declaration"]): continue
        decl, _ = signature(m)
        if m["abstract"]: extra.append("  " + decl + ";")
        elif m["name"] == "aliases": extra.append("  " + decl + " { return aliases; }")
        elif m["name"] == "displayName": extra.append("  " + decl + " { return name(); }")
        elif m["name"] == "isRegistered": extra.append("  " + decl + ' { return !name().startsWith("X-") && !name().startsWith("x-"); }')
        elif m["name"] == "isSupported": extra.append("  " + decl + " { try { forName(p0); return true; } catch (UnsupportedCharsetException e) { return false; } }")
        elif m["name"] == "forName": extra.append("  " + decl + " { try { return forName(p0); } catch (UnsupportedCharsetException e) { return p1; } }")
        else: extra.append("  " + decl + " { " + fail(c,m) + " }")
    write("java/nio/charset/Charset.java", append(charset, "\n".join(extra)))
    # Concrete internal charsets must satisfy the newly restored abstract API.
    emulated = (jre / "javaemul/internal/EmulatedCharset.java").read_text()
    methods = []
    for m in c["methods"]:
        if m["abstract"]:
            decl, _ = signature(m)
            methods.append("  @Override " + decl.replace("abstract ", "") + " { " + fail(c,m) + " }")
    write("javaemul/internal/EmulatedCharset.java", append(emulated, "\n".join(methods)))
    standard = (jre / "java/nio/charset/StandardCharsets.java").read_text()
    constants = '\n'.join('  public static final Charset '+name+' = new MetadataCharset("'+label+'");' for name,label in [("US_ASCII","US-ASCII"),("UTF_16","UTF-16"),("UTF_16BE","UTF-16BE"),("UTF_16LE","UTF-16LE")])
    constants += '''
  private static final class MetadataCharset extends EmulatedCharset {
    MetadataCharset(String name) { super(name); }
    public boolean contains(Charset other) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.contains(Charset)"); }
    public CharsetDecoder newDecoder() { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.newDecoder()"); }
    public CharsetEncoder newEncoder() { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.Charset.newEncoder()"); }
    public byte[] getBytes(String text, boolean invalid) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.StandardCharsets encoding " + name()); }
    public byte[] getBytes(char[] text, int offset, int count, boolean invalid) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.StandardCharsets encoding " + name()); }
    public char[] decodeString(byte[] bytes, int offset, int length, boolean invalid) { throw javaemul.internal.nio.UnsupportedNio.operation("java.nio.charset.StandardCharsets decoding " + name()); }
  }
'''
    write("java/nio/charset/StandardCharsets.java", append(standard, constants))
    dependencies = []
    for owner, folder, sources in [
        ("java-io-j2cl", args.io / "overlay/jre/java", list((args.io / "overlay/jre/java").rglob("*.java"))),
        ("WebAWT", args.webawt / "src/main/java", [args.webawt / ("src/main/java/java/net/" + name + ".java") for name in ["URI", "URISyntaxException", "URL", "MalformedURLException"]]),
    ]:
        for source in sorted(sources):
            relative = source.relative_to(folder)
            destination = ROOT / "third_party" / owner / "overlay" / relative
            content = source.read_bytes()
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_bytes(content)
            dependencies.append({"owner":owner,"path":relative.as_posix(),"sha256":hashlib.sha256(content).hexdigest()})
    (ROOT / "compatibility/dependency-snapshots.json").write_text(json.dumps(dependencies, indent=2)+"\n")
    available = {p.relative_to(jre).as_posix()[:-5].replace('/', '.') for p in jre.rglob("*.java")}
    (ROOT / "compatibility/j2cl-types.json").write_text(json.dumps({"version":"20260402","types":sorted(available)},indent=2)+"\n")
    available |= {c["name"] for c in API["classes"]}
    available |= {x["path"][:-5].replace('/', '.') for x in dependencies}
    available |= {p.relative_to(DEST).as_posix()[:-5].replace('/', '.') for p in DEST.rglob("*.java")}
    required = set(re.findall(r"java\.(?:[a-z]+\.)+[A-Z][\w.]*", json.dumps(API)))
    missing = sorted(n.rstrip('.') for n in required if n.rstrip('.') not in available and not n.startswith("java.lang.foreign."))
    print("External signature types still required:", missing)
    exports = sorted({c["package"] for c in API["classes"]} | {"java.net", "java.time"})
    module = (jre / "module-info.java").read_text()
    module = append(module, "\n".join("  exports " + package + ";" for package in exports if "exports " + package + ";" not in module))
    write("module-info.java", module)
    baseline = []
    for source in sorted(DEST.rglob("*.java")):
        relative = source.relative_to(DEST)
        original = jre / relative
        if original.exists():
            path = ROOT / "third_party/j2cl-base/jre/java" / relative
            path.parent.mkdir(parents=True, exist_ok=True)
            content = original.read_bytes(); path.write_bytes(content)
            baseline.append({"path":relative.as_posix(), "sha256":hashlib.sha256(content).hexdigest()})
    (ROOT / "compatibility/j2cl-baseline.json").write_text(json.dumps({"version":"20260402", "archive_sha256":"d1419aecefec2727d3449f10a772d973ff5d3a7a7470367d1cfc2c782c7d6e66", "modified_upstream_sources":baseline},indent=2)+"\n")

if __name__ == "__main__": main()
