package java.beans;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_beans_PropertyVetoException {
    static void call0()  { java.beans.PropertyVetoException value = new java.beans.PropertyVetoException((java.lang.String) null, (java.beans.PropertyChangeEvent) null); }
    static void call1(java.beans.PropertyVetoException receiver)  { java.beans.PropertyChangeEvent value = receiver.getPropertyChangeEvent(); }
}

abstract class Override_java_beans_PropertyVetoException extends java.beans.PropertyVetoException {
    Override_java_beans_PropertyVetoException()  { super((java.lang.String) null, (java.beans.PropertyChangeEvent) null); }
    @Override
    public java.beans.PropertyChangeEvent getPropertyChangeEvent()  { throw new UnsupportedOperationException(); }
}
