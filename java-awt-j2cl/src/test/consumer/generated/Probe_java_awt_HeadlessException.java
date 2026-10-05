package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_HeadlessException {
    static void call0()  { java.awt.HeadlessException value = new java.awt.HeadlessException(); }
    static void call1()  { java.awt.HeadlessException value = new java.awt.HeadlessException((java.lang.String) null); }
    static void call2(java.awt.HeadlessException receiver)  { java.lang.String value = receiver.getMessage(); }
}

abstract class Override_java_awt_HeadlessException extends java.awt.HeadlessException {
    Override_java_awt_HeadlessException()  { super(); }
    @Override
    public java.lang.String getMessage()  { throw new UnsupportedOperationException(); }
}
