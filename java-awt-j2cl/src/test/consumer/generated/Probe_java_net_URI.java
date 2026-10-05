package java.net;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_net_URI {
    static void call0() throws java.net.URISyntaxException { try { java.net.URI value = new java.net.URI((java.lang.String) null); } catch (java.net.URISyntaxException expected) { throw expected; } }
    static void call1()  { java.net.URI value = java.net.URI.create((java.lang.String) null); }
    static void call2(java.net.URI receiver)  { java.lang.String value = receiver.getScheme(); }
    static void call3(java.net.URI receiver)  { boolean value = receiver.isAbsolute(); }
    static void call4(java.net.URI receiver)  { boolean value = receiver.isOpaque(); }
    static void call5(java.net.URI receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call6(java.net.URI receiver)  { int value = receiver.hashCode(); }
    static void call7(java.net.URI receiver)  { int value = receiver.compareTo((java.net.URI) null); }
    static void call8(java.net.URI receiver)  { java.lang.String value = receiver.toString(); }
    static void call9(java.net.URI receiver)  { java.lang.String value = receiver.toASCIIString(); }
    static void call10(java.net.URI receiver)  { int value = ((java.lang.Comparable) receiver).compareTo((java.lang.Object) null); }
}
