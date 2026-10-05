package java.text;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_text_Annotation {
    static void call0()  { java.text.Annotation value = new java.text.Annotation((java.lang.Object) null); }
    static void call1(java.text.Annotation receiver)  { java.lang.Object value = receiver.getValue(); }
    static void call2(java.text.Annotation receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_text_Annotation extends java.text.Annotation {
    Override_java_text_Annotation()  { super((java.lang.Object) null); }
    @Override
    public java.lang.Object getValue()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
