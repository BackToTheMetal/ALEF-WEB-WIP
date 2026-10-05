package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_TextField {
    static void call0() throws java.awt.HeadlessException { try { java.awt.TextField value = new java.awt.TextField(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.TextField value = new java.awt.TextField((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.TextField value = new java.awt.TextField((int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3() throws java.awt.HeadlessException { try { java.awt.TextField value = new java.awt.TextField((java.lang.String) null, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4(java.awt.TextField receiver)  { receiver.addNotify(); }
    static void call5(java.awt.TextField receiver)  { char value = receiver.getEchoChar(); }
    static void call6(java.awt.TextField receiver)  { receiver.setEchoChar((char) 0); }
    static void call7(java.awt.TextField receiver)  { receiver.setEchoCharacter((char) 0); }
    static void call8(java.awt.TextField receiver)  { receiver.setText((java.lang.String) null); }
    static void call9(java.awt.TextField receiver)  { boolean value = receiver.echoCharIsSet(); }
    static void call10(java.awt.TextField receiver)  { int value = receiver.getColumns(); }
    static void call11(java.awt.TextField receiver)  { receiver.setColumns((int) 0); }
    static void call12(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.getPreferredSize((int) 0); }
    static void call13(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.preferredSize((int) 0); }
    static void call14(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.getPreferredSize(); }
    static void call15(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.preferredSize(); }
    static void call16(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.getMinimumSize((int) 0); }
    static void call17(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.minimumSize((int) 0); }
    static void call18(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.getMinimumSize(); }
    static void call19(java.awt.TextField receiver)  { java.awt.Dimension value = receiver.minimumSize(); }
    static void call20(java.awt.TextField receiver)  { receiver.addActionListener((java.awt.event.ActionListener) null); }
    static void call21(java.awt.TextField receiver)  { receiver.removeActionListener((java.awt.event.ActionListener) null); }
    static void call22(java.awt.TextField receiver)  { java.awt.event.ActionListener[] value = receiver.getActionListeners(); }
    static void call23(java.awt.TextField receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call24(java.awt.TextField receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call25(java.awt.TextField receiver)  { receiver.processActionEvent((java.awt.event.ActionEvent) null); }
    static void call26(java.awt.TextField receiver)  { java.lang.String value = receiver.paramString(); }
    static void call27(java.awt.TextField receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_TextField extends java.awt.TextField {
    Override_java_awt_TextField() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public char getEchoChar()  { throw new UnsupportedOperationException(); }
    @Override
    public void setEchoChar(char arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setEchoCharacter(char arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setText(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean echoCharIsSet()  { throw new UnsupportedOperationException(); }
    @Override
    public int getColumns()  { throw new UnsupportedOperationException(); }
    @Override
    public void setColumns(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void addActionListener(java.awt.event.ActionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeActionListener(java.awt.event.ActionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.ActionListener[] getActionListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processActionEvent(java.awt.event.ActionEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
