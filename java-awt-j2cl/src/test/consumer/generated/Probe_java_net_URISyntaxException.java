package java.net;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_net_URISyntaxException {
    static void call0()  { java.net.URISyntaxException value = new java.net.URISyntaxException((java.lang.String) null, (java.lang.String) null, (int) 0); }
    static void call1()  { java.net.URISyntaxException value = new java.net.URISyntaxException((java.lang.String) null, (java.lang.String) null); }
    static void call2(java.net.URISyntaxException receiver)  { java.lang.String value = receiver.getInput(); }
    static void call3(java.net.URISyntaxException receiver)  { java.lang.String value = receiver.getReason(); }
    static void call4(java.net.URISyntaxException receiver)  { int value = receiver.getIndex(); }
    static void call5(java.net.URISyntaxException receiver)  { java.lang.String value = receiver.getMessage(); }
}

abstract class Override_java_net_URISyntaxException extends java.net.URISyntaxException {
    Override_java_net_URISyntaxException()  { super((java.lang.String) null, (java.lang.String) null, (int) 0); }
    @Override
    public java.lang.String getInput()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getReason()  { throw new UnsupportedOperationException(); }
    @Override
    public int getIndex()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getMessage()  { throw new UnsupportedOperationException(); }
}
