#!/usr/bin/env python3
"""Minimal external type closure. These are explicitly NOT full non-NIO API emulations."""
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
SOURCES = {
"java.lang.Readable": "public interface Readable { int read(java.nio.CharBuffer target) throws java.io.IOException; }",
"java.lang.ClassLoader": "public abstract class ClassLoader { protected ClassLoader() {} }",
"java.lang.Thread": '''public class Thread implements Runnable {
  public Thread() { throw unsupported(); }
  public Thread(Runnable runnable) { throw unsupported(); }
  public void start() { throw unsupported(); }
  public void run() { throw unsupported(); }
  private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.lang.Thread"); }
}''',
"java.net.SocketAddress": "public abstract class SocketAddress implements java.io.Serializable { public SocketAddress() {} }",
"java.net.ProtocolFamily": "public interface ProtocolFamily { String name(); }",
"java.net.SocketOption": "public interface SocketOption<T> { String name(); Class<T> type(); }",
"java.net.InetAddress": "public class InetAddress implements java.io.Serializable { InetAddress() {} }",
"java.net.NetworkInterface": "public final class NetworkInterface { private NetworkInterface() {} }",
"java.security.Principal": "public interface Principal { String getName(); boolean equals(Object other); int hashCode(); String toString(); }",
"java.security.Permission": '''public abstract class Permission implements java.io.Serializable {
  private final String name;
  public Permission(String name) { this.name = name; }
  public final String getName() { return name; }
  public abstract boolean implies(Permission permission);
  public abstract boolean equals(Object other);
  public abstract int hashCode();
  public abstract String getActions();
}''',
"java.security.BasicPermission": '''public abstract class BasicPermission extends Permission implements java.io.Serializable {
  public BasicPermission(String name) { super(name); }
  public BasicPermission(String name, String actions) { super(name); }
  public boolean implies(Permission permission) { throw unsupported(); }
  public boolean equals(Object other) { throw unsupported(); }
  public int hashCode() { throw unsupported(); }
  public String getActions() { throw unsupported(); }
  private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.security.BasicPermission"); }
}''',
"java.time.Instant": '''public final class Instant implements Comparable<Instant>, java.io.Serializable {
  private Instant() {}
  public int compareTo(Instant other) { throw javaemul.internal.nio.UnsupportedNio.operation("java.time.Instant.compareTo(Instant)"); }
}''',
"java.util.concurrent.ThreadFactory": "public interface ThreadFactory { Thread newThread(Runnable runnable); }",
}
for simple in ["Socket", "ServerSocket", "DatagramSocket"]:
    SOURCES["java.net." + simple] = 'public class ' + simple + ' implements java.io.Closeable { public ' + simple + '() { throw unsupported(); } public void close() throws java.io.IOException { throw unsupported(); } private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.net.' + simple + '"); } }'

def main():
    for name, source in SOURCES.items():
        path = ROOT / "src/main/super" / (name.replace('.', '/') + ".java")
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text("// Signature dependency only; see compatibility/external-support.json.\npackage " + name.rsplit('.',1)[0] + ";\n\n" + source + "\n", newline="\n")
    (ROOT / "compatibility/external-support.json").write_text(json.dumps({"status":"Signature closure only; not complete external APIs", "types":sorted(SOURCES)}, indent=2)+"\n")
    print("Generated", len(SOURCES), "external signature support types")

if __name__ == "__main__": main()
