package java.text;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_text_AttributedCharacterIterator_Attribute {
    static void call0() { java.text.AttributedCharacterIterator.Attribute value = java.text.AttributedCharacterIterator.Attribute.LANGUAGE; }
    static void call1() { java.text.AttributedCharacterIterator.Attribute value = java.text.AttributedCharacterIterator.Attribute.READING; }
    static void call2() { java.text.AttributedCharacterIterator.Attribute value = java.text.AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT; }
    static void call3()  { java.text.AttributedCharacterIterator.Attribute value = new java.text.AttributedCharacterIterator.Attribute((java.lang.String) null); }
    static void call4(java.text.AttributedCharacterIterator.Attribute receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call5(java.text.AttributedCharacterIterator.Attribute receiver)  { int value = receiver.hashCode(); }
    static void call6(java.text.AttributedCharacterIterator.Attribute receiver)  { java.lang.String value = receiver.toString(); }
    static void call7(java.text.AttributedCharacterIterator.Attribute receiver)  { java.lang.String value = receiver.getName(); }
}

abstract class Override_java_text_AttributedCharacterIterator_Attribute extends java.text.AttributedCharacterIterator.Attribute {
    Override_java_text_AttributedCharacterIterator_Attribute()  { super((java.lang.String) null); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String getName()  { throw new UnsupportedOperationException(); }
}
