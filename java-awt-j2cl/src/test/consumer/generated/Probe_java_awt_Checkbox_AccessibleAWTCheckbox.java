package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Checkbox_AccessibleAWTCheckbox {
    static void call0(java.awt.Checkbox outer)  { java.awt.Checkbox.AccessibleAWTCheckbox value = outer.new AccessibleAWTCheckbox(); }
    static void call1(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { receiver.itemStateChanged((java.awt.event.ItemEvent) null); }
    static void call2(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { javax.accessibility.AccessibleAction value = receiver.getAccessibleAction(); }
    static void call3(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { javax.accessibility.AccessibleValue value = receiver.getAccessibleValue(); }
    static void call4(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { int value = receiver.getAccessibleActionCount(); }
    static void call5(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { java.lang.String value = receiver.getAccessibleActionDescription((int) 0); }
    static void call6(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { boolean value = receiver.doAccessibleAction((int) 0); }
    static void call7(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { java.lang.Number value = receiver.getCurrentAccessibleValue(); }
    static void call8(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { boolean value = receiver.setCurrentAccessibleValue((java.lang.Number) null); }
    static void call9(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { java.lang.Number value = receiver.getMinimumAccessibleValue(); }
    static void call10(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { java.lang.Number value = receiver.getMaximumAccessibleValue(); }
    static void call11(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call12(java.awt.Checkbox.AccessibleAWTCheckbox receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
}

abstract class Override_java_awt_Checkbox_AccessibleAWTCheckbox extends java.awt.Checkbox.AccessibleAWTCheckbox {
    Override_java_awt_Checkbox_AccessibleAWTCheckbox(java.awt.Checkbox outer)  { outer.super(); }
    @Override
    public void itemStateChanged(java.awt.event.ItemEvent arg0)  { throw new UnsupportedOperationException(); }
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
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
}
