package java.beans;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_beans_PropertyChangeEvent {
    static void call0()  { java.beans.PropertyChangeEvent value = new java.beans.PropertyChangeEvent((java.lang.Object) null, (java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); }
    static void call1(java.beans.PropertyChangeEvent receiver)  { java.lang.String value = receiver.getPropertyName(); }
    static void call2(java.beans.PropertyChangeEvent receiver)  { java.lang.Object value = receiver.getNewValue(); }
    static void call3(java.beans.PropertyChangeEvent receiver)  { java.lang.Object value = receiver.getOldValue(); }
    static void call4(java.beans.PropertyChangeEvent receiver)  { receiver.setPropagationId((java.lang.Object) null); }
    static void call5(java.beans.PropertyChangeEvent receiver)  { java.lang.Object value = receiver.getPropagationId(); }
    static void call6(java.beans.PropertyChangeEvent receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_beans_PropertyChangeEvent extends java.beans.PropertyChangeEvent {
    Override_java_beans_PropertyChangeEvent()  { super((java.lang.Object) null, (java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); }
    @Override
    public java.lang.String getPropertyName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getNewValue()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getOldValue()  { throw new UnsupportedOperationException(); }
    @Override
    public void setPropagationId(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getPropagationId()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
