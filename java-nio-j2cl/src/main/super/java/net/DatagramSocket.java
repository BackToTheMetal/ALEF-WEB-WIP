// Signature dependency only; see compatibility/external-support.json.
package java.net;

public class DatagramSocket implements java.io.Closeable { public DatagramSocket() { throw unsupported(); } public void close() throws java.io.IOException { throw unsupported(); } private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.net.DatagramSocket"); } }
