package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_TextArea {
    static void call0() { int value = java.awt.TextArea.SCROLLBARS_BOTH; }
    static void call1() { int value = java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY; }
    static void call2() { int value = java.awt.TextArea.SCROLLBARS_HORIZONTAL_ONLY; }
    static void call3() { int value = java.awt.TextArea.SCROLLBARS_NONE; }
    static void call4() throws java.awt.HeadlessException { try { java.awt.TextArea value = new java.awt.TextArea(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5() throws java.awt.HeadlessException { try { java.awt.TextArea value = new java.awt.TextArea((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call6() throws java.awt.HeadlessException { try { java.awt.TextArea value = new java.awt.TextArea((int) 0, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call7() throws java.awt.HeadlessException { try { java.awt.TextArea value = new java.awt.TextArea((java.lang.String) null, (int) 0, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call8() throws java.awt.HeadlessException { try { java.awt.TextArea value = new java.awt.TextArea((java.lang.String) null, (int) 0, (int) 0, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call9(java.awt.TextArea receiver)  { receiver.addNotify(); }
    static void call10(java.awt.TextArea receiver)  { receiver.insert((java.lang.String) null, (int) 0); }
    static void call11(java.awt.TextArea receiver)  { receiver.insertText((java.lang.String) null, (int) 0); }
    static void call12(java.awt.TextArea receiver)  { receiver.append((java.lang.String) null); }
    static void call13(java.awt.TextArea receiver)  { receiver.appendText((java.lang.String) null); }
    static void call14(java.awt.TextArea receiver)  { receiver.replaceRange((java.lang.String) null, (int) 0, (int) 0); }
    static void call15(java.awt.TextArea receiver)  { receiver.replaceText((java.lang.String) null, (int) 0, (int) 0); }
    static void call16(java.awt.TextArea receiver)  { int value = receiver.getRows(); }
    static void call17(java.awt.TextArea receiver)  { receiver.setRows((int) 0); }
    static void call18(java.awt.TextArea receiver)  { int value = receiver.getColumns(); }
    static void call19(java.awt.TextArea receiver)  { receiver.setColumns((int) 0); }
    static void call20(java.awt.TextArea receiver)  { int value = receiver.getScrollbarVisibility(); }
    static void call21(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.getPreferredSize((int) 0, (int) 0); }
    static void call22(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.preferredSize((int) 0, (int) 0); }
    static void call23(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.getPreferredSize(); }
    static void call24(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.preferredSize(); }
    static void call25(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.getMinimumSize((int) 0, (int) 0); }
    static void call26(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.minimumSize((int) 0, (int) 0); }
    static void call27(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.getMinimumSize(); }
    static void call28(java.awt.TextArea receiver)  { java.awt.Dimension value = receiver.minimumSize(); }
    static void call29(java.awt.TextArea receiver)  { java.lang.String value = receiver.paramString(); }
    static void call30(java.awt.TextArea receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_TextArea extends java.awt.TextArea {
    Override_java_awt_TextArea() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void insert(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void insertText(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void append(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void appendText(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void replaceRange(java.lang.String arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void replaceText(java.lang.String arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int getRows()  { throw new UnsupportedOperationException(); }
    @Override
    public void setRows(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getColumns()  { throw new UnsupportedOperationException(); }
    @Override
    public void setColumns(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getScrollbarVisibility()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
