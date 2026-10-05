package java.beans;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_beans_IndexedPropertyChangeEvent {
    static void call0()  { java.beans.IndexedPropertyChangeEvent value = new java.beans.IndexedPropertyChangeEvent((java.lang.Object) null, (java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null, (int) 0); }
    static void call1(java.beans.IndexedPropertyChangeEvent receiver)  { int value = receiver.getIndex(); }
}

abstract class Override_java_beans_IndexedPropertyChangeEvent extends java.beans.IndexedPropertyChangeEvent {
    Override_java_beans_IndexedPropertyChangeEvent()  { super((java.lang.Object) null, (java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null, (int) 0); }
    @Override
    public int getIndex()  { throw new UnsupportedOperationException(); }
}
