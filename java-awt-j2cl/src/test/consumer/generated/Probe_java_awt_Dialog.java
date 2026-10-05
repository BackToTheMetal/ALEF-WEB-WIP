package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Dialog {
    static void call0() { java.awt.Dialog.ModalityType value = java.awt.Dialog.DEFAULT_MODALITY_TYPE; }
    static void call1()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Frame) null); }
    static void call2()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Frame) null, false); }
    static void call3()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Frame) null, (java.lang.String) null); }
    static void call4()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Frame) null, (java.lang.String) null, false); }
    static void call5()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Frame) null, (java.lang.String) null, false, (java.awt.GraphicsConfiguration) null); }
    static void call6()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Dialog) null); }
    static void call7()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Dialog) null, (java.lang.String) null); }
    static void call8()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Dialog) null, (java.lang.String) null, false); }
    static void call9()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Dialog) null, (java.lang.String) null, false, (java.awt.GraphicsConfiguration) null); }
    static void call10()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Window) null); }
    static void call11()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Window) null, (java.lang.String) null); }
    static void call12()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Window) null, (java.awt.Dialog.ModalityType) null); }
    static void call13()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null); }
    static void call14()  { java.awt.Dialog value = new java.awt.Dialog((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); }
    static void call15(java.awt.Dialog receiver)  { receiver.addNotify(); }
    static void call16(java.awt.Dialog receiver)  { boolean value = receiver.isModal(); }
    static void call17(java.awt.Dialog receiver)  { receiver.setModal(false); }
    static void call18(java.awt.Dialog receiver)  { java.awt.Dialog.ModalityType value = receiver.getModalityType(); }
    static void call19(java.awt.Dialog receiver)  { receiver.setModalityType((java.awt.Dialog.ModalityType) null); }
    static void call20(java.awt.Dialog receiver)  { java.lang.String value = receiver.getTitle(); }
    static void call21(java.awt.Dialog receiver)  { receiver.setTitle((java.lang.String) null); }
    static void call22(java.awt.Dialog receiver)  { receiver.setVisible(false); }
    static void call23(java.awt.Dialog receiver)  { receiver.show(); }
    static void call24(java.awt.Dialog receiver)  { receiver.hide(); }
    static void call25(java.awt.Dialog receiver)  { receiver.toBack(); }
    static void call26(java.awt.Dialog receiver)  { boolean value = receiver.isResizable(); }
    static void call27(java.awt.Dialog receiver)  { receiver.setResizable(false); }
    static void call28(java.awt.Dialog receiver)  { receiver.setUndecorated(false); }
    static void call29(java.awt.Dialog receiver)  { boolean value = receiver.isUndecorated(); }
    static void call30(java.awt.Dialog receiver)  { receiver.setOpacity((float) 0); }
    static void call31(java.awt.Dialog receiver)  { receiver.setShape((java.awt.Shape) null); }
    static void call32(java.awt.Dialog receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call33(java.awt.Dialog receiver)  { java.lang.String value = receiver.paramString(); }
    static void call34(java.awt.Dialog receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Dialog extends java.awt.Dialog {
    Override_java_awt_Dialog()  { super((java.awt.Frame) null); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isModal()  { throw new UnsupportedOperationException(); }
    @Override
    public void setModal(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dialog.ModalityType getModalityType()  { throw new UnsupportedOperationException(); }
    @Override
    public void setModalityType(java.awt.Dialog.ModalityType arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getTitle()  { throw new UnsupportedOperationException(); }
    @Override
    public void setTitle(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setVisible(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void show()  { throw new UnsupportedOperationException(); }
    @Override
    public void hide()  { throw new UnsupportedOperationException(); }
    @Override
    public void toBack()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isResizable()  { throw new UnsupportedOperationException(); }
    @Override
    public void setResizable(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setUndecorated(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isUndecorated()  { throw new UnsupportedOperationException(); }
    @Override
    public void setOpacity(float arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setShape(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setBackground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
