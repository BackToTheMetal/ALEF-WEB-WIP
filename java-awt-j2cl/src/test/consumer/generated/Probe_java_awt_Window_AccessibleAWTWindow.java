package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Window_AccessibleAWTWindow {
    static void call0(java.awt.Window outer)  { java.awt.Window.AccessibleAWTWindow value = outer.new AccessibleAWTWindow(); }
    static void call1(java.awt.Window.AccessibleAWTWindow receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call2(java.awt.Window.AccessibleAWTWindow receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
}

abstract class Override_java_awt_Window_AccessibleAWTWindow extends java.awt.Window.AccessibleAWTWindow {
    Override_java_awt_Window_AccessibleAWTWindow(java.awt.Window outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
}
