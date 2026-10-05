package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_CheckboxMenuItem_AccessibleAWTCheckboxMenuItem {
    static void call0(java.awt.CheckboxMenuItem outer)  { java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem value = outer.new AccessibleAWTCheckboxMenuItem(); }
    static void call1(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { javax.accessibility.AccessibleAction value = receiver.getAccessibleAction(); }
    static void call2(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { javax.accessibility.AccessibleValue value = receiver.getAccessibleValue(); }
    static void call3(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { int value = receiver.getAccessibleActionCount(); }
    static void call4(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { java.lang.String value = receiver.getAccessibleActionDescription((int) 0); }
    static void call5(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { boolean value = receiver.doAccessibleAction((int) 0); }
    static void call6(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { java.lang.Number value = receiver.getCurrentAccessibleValue(); }
    static void call7(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { boolean value = receiver.setCurrentAccessibleValue((java.lang.Number) null); }
    static void call8(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { java.lang.Number value = receiver.getMinimumAccessibleValue(); }
    static void call9(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { java.lang.Number value = receiver.getMaximumAccessibleValue(); }
    static void call10(java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_CheckboxMenuItem_AccessibleAWTCheckboxMenuItem extends java.awt.CheckboxMenuItem.AccessibleAWTCheckboxMenuItem {
    Override_java_awt_CheckboxMenuItem_AccessibleAWTCheckboxMenuItem(java.awt.CheckboxMenuItem outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleAction getAccessibleAction()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleValue getAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleActionCount()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getAccessibleActionDescription(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean doAccessibleAction(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getCurrentAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean setCurrentAccessibleValue(java.lang.Number arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getMinimumAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getMaximumAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
