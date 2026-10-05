package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Scrollbar_AccessibleAWTScrollBar {
    static void call0(java.awt.Scrollbar outer)  { java.awt.Scrollbar.AccessibleAWTScrollBar value = outer.new AccessibleAWTScrollBar(); }
    static void call1(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
    static void call2(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call3(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { javax.accessibility.AccessibleValue value = receiver.getAccessibleValue(); }
    static void call4(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { java.lang.Number value = receiver.getCurrentAccessibleValue(); }
    static void call5(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { boolean value = receiver.setCurrentAccessibleValue((java.lang.Number) null); }
    static void call6(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { java.lang.Number value = receiver.getMinimumAccessibleValue(); }
    static void call7(java.awt.Scrollbar.AccessibleAWTScrollBar receiver)  { java.lang.Number value = receiver.getMaximumAccessibleValue(); }
}

abstract class Override_java_awt_Scrollbar_AccessibleAWTScrollBar extends java.awt.Scrollbar.AccessibleAWTScrollBar {
    Override_java_awt_Scrollbar_AccessibleAWTScrollBar(java.awt.Scrollbar outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleValue getAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getCurrentAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean setCurrentAccessibleValue(java.lang.Number arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getMinimumAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getMaximumAccessibleValue()  { throw new UnsupportedOperationException(); }
}
