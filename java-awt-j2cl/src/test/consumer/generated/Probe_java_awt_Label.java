package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Label {
    static void call0() { int value = java.awt.Label.LEFT; }
    static void call1() { int value = java.awt.Label.CENTER; }
    static void call2() { int value = java.awt.Label.RIGHT; }
    static void call3() throws java.awt.HeadlessException { try { java.awt.Label value = new java.awt.Label(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4() throws java.awt.HeadlessException { try { java.awt.Label value = new java.awt.Label((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5() throws java.awt.HeadlessException { try { java.awt.Label value = new java.awt.Label((java.lang.String) null, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call6(java.awt.Label receiver)  { receiver.addNotify(); }
    static void call7(java.awt.Label receiver)  { int value = receiver.getAlignment(); }
    static void call8(java.awt.Label receiver)  { receiver.setAlignment((int) 0); }
    static void call9(java.awt.Label receiver)  { java.lang.String value = receiver.getText(); }
    static void call10(java.awt.Label receiver)  { receiver.setText((java.lang.String) null); }
    static void call11(java.awt.Label receiver)  { java.lang.String value = receiver.paramString(); }
    static void call12(java.awt.Label receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Label extends java.awt.Label {
    Override_java_awt_Label() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAlignment()  { throw new UnsupportedOperationException(); }
    @Override
    public void setAlignment(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getText()  { throw new UnsupportedOperationException(); }
    @Override
    public void setText(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
