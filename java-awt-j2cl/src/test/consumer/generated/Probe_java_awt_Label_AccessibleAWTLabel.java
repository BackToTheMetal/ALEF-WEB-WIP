package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Label_AccessibleAWTLabel {
    static void call0(java.awt.Label outer)  { java.awt.Label.AccessibleAWTLabel value = outer.new AccessibleAWTLabel(); }
    static void call1(java.awt.Label.AccessibleAWTLabel receiver)  { java.lang.String value = receiver.getAccessibleName(); }
    static void call2(java.awt.Label.AccessibleAWTLabel receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_Label_AccessibleAWTLabel extends java.awt.Label.AccessibleAWTLabel {
    Override_java_awt_Label_AccessibleAWTLabel(java.awt.Label outer)  { outer.super(); }
    @Override
    public java.lang.String getAccessibleName()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
