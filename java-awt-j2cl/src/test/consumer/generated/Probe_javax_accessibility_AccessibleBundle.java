package javax.accessibility;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_javax_accessibility_AccessibleBundle {
    static void call0(javax.accessibility.AccessibleBundle receiver) { java.lang.String value = receiver.key; receiver.key = value; }
    static void call1(javax.accessibility.AccessibleBundle receiver)  { java.lang.String value = receiver.toDisplayString((java.lang.String) null, (java.util.Locale) null); }
    static void call2(javax.accessibility.AccessibleBundle receiver)  { java.lang.String value = receiver.toDisplayString((java.util.Locale) null); }
    static void call3(javax.accessibility.AccessibleBundle receiver)  { java.lang.String value = receiver.toDisplayString(); }
    static void call4(javax.accessibility.AccessibleBundle receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_javax_accessibility_AccessibleBundle extends javax.accessibility.AccessibleBundle {
    Override_javax_accessibility_AccessibleBundle()  { super(); }
    @Override
    protected java.lang.String toDisplayString(java.lang.String arg0, java.util.Locale arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toDisplayString(java.util.Locale arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toDisplayString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
