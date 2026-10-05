package java.net;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_net_URL {
    static void call0() throws java.net.MalformedURLException { try { java.net.URL value = new java.net.URL((java.lang.String) null); } catch (java.net.MalformedURLException expected) { throw expected; } }
    static void call1(java.net.URL receiver)  { java.lang.String value = receiver.getProtocol(); }
    static void call2(java.net.URL receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call3(java.net.URL receiver)  { int value = receiver.hashCode(); }
    static void call4(java.net.URL receiver)  { java.lang.String value = receiver.toString(); }
    static void call5(java.net.URL receiver)  { java.lang.String value = receiver.toExternalForm(); }
}
