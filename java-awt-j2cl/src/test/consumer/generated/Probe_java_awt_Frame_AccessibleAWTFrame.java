package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Frame_AccessibleAWTFrame {
    static void call0(java.awt.Frame outer)  { java.awt.Frame.AccessibleAWTFrame value = outer.new AccessibleAWTFrame(); }
    static void call1(java.awt.Frame.AccessibleAWTFrame receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call2(java.awt.Frame.AccessibleAWTFrame receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
}

abstract class Override_java_awt_Frame_AccessibleAWTFrame extends java.awt.Frame.AccessibleAWTFrame {
    Override_java_awt_Frame_AccessibleAWTFrame(java.awt.Frame outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
}
