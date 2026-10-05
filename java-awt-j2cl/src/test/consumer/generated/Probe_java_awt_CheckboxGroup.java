package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_CheckboxGroup {
    static void call0()  { java.awt.CheckboxGroup value = new java.awt.CheckboxGroup(); }
    static void call1(java.awt.CheckboxGroup receiver)  { java.awt.Checkbox value = receiver.getSelectedCheckbox(); }
    static void call2(java.awt.CheckboxGroup receiver)  { java.awt.Checkbox value = receiver.getCurrent(); }
    static void call3(java.awt.CheckboxGroup receiver)  { receiver.setSelectedCheckbox((java.awt.Checkbox) null); }
    static void call4(java.awt.CheckboxGroup receiver)  { receiver.setCurrent((java.awt.Checkbox) null); }
    static void call5(java.awt.CheckboxGroup receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_CheckboxGroup extends java.awt.CheckboxGroup {
    Override_java_awt_CheckboxGroup()  { super(); }
    @Override
    public java.awt.Checkbox getSelectedCheckbox()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Checkbox getCurrent()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSelectedCheckbox(java.awt.Checkbox arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setCurrent(java.awt.Checkbox arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
