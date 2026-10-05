// Signature dependency only; see compatibility/external-support.json.
package java.net;

public class ServerSocket implements java.io.Closeable { public ServerSocket() { throw unsupported(); } public void close() throws java.io.IOException { throw unsupported(); } private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.net.ServerSocket"); } }
