package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Dialog_AccessibleAWTDialog {
    static void call0(java.awt.Dialog outer)  { java.awt.Dialog.AccessibleAWTDialog value = outer.new AccessibleAWTDialog(); }
    static void call1(java.awt.Dialog.AccessibleAWTDialog receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call2(java.awt.Dialog.AccessibleAWTDialog receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
}

abstract class Override_java_awt_Dialog_AccessibleAWTDialog extends java.awt.Dialog.AccessibleAWTDialog {
    Override_java_awt_Dialog_AccessibleAWTDialog(java.awt.Dialog outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
}
