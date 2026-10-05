package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_ScrollPane {
    static void call0() { int value = java.awt.ScrollPane.SCROLLBARS_AS_NEEDED; }
    static void call1() { int value = java.awt.ScrollPane.SCROLLBARS_ALWAYS; }
    static void call2() { int value = java.awt.ScrollPane.SCROLLBARS_NEVER; }
    static void call3() throws java.awt.HeadlessException { try { java.awt.ScrollPane value = new java.awt.ScrollPane(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4() throws java.awt.HeadlessException { try { java.awt.ScrollPane value = new java.awt.ScrollPane((int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5(java.awt.ScrollPane receiver)  { receiver.addImpl((java.awt.Component) null, (java.lang.Object) null, (int) 0); }
    static void call6(java.awt.ScrollPane receiver)  { int value = receiver.getScrollbarDisplayPolicy(); }
    static void call7(java.awt.ScrollPane receiver)  { java.awt.Dimension value = receiver.getViewportSize(); }
    static void call8(java.awt.ScrollPane receiver)  { int value = receiver.getHScrollbarHeight(); }
    static void call9(java.awt.ScrollPane receiver)  { int value = receiver.getVScrollbarWidth(); }
    static void call10(java.awt.ScrollPane receiver)  { java.awt.Adjustable value = receiver.getVAdjustable(); }
    static void call11(java.awt.ScrollPane receiver)  { java.awt.Adjustable value = receiver.getHAdjustable(); }
    static void call12(java.awt.ScrollPane receiver)  { receiver.setScrollPosition((int) 0, (int) 0); }
    static void call13(java.awt.ScrollPane receiver)  { receiver.setScrollPosition((java.awt.Point) null); }
    static void call14(java.awt.ScrollPane receiver)  { java.awt.Point value = receiver.getScrollPosition(); }
    static void call15(java.awt.ScrollPane receiver)  { receiver.setLayout((java.awt.LayoutManager) null); }
    static void call16(java.awt.ScrollPane receiver)  { receiver.doLayout(); }
    static void call17(java.awt.ScrollPane receiver)  { receiver.layout(); }
    static void call18(java.awt.ScrollPane receiver)  { receiver.printComponents((java.awt.Graphics) null); }
    static void call19(java.awt.ScrollPane receiver)  { receiver.addNotify(); }
    static void call20(java.awt.ScrollPane receiver)  { java.lang.String value = receiver.paramString(); }
    static void call21(java.awt.ScrollPane receiver)  { receiver.processMouseWheelEvent((java.awt.event.MouseWheelEvent) null); }
    static void call22(java.awt.ScrollPane receiver)  { boolean value = receiver.eventTypeEnabled((int) 0); }
    static void call23(java.awt.ScrollPane receiver)  { receiver.setWheelScrollingEnabled(false); }
    static void call24(java.awt.ScrollPane receiver)  { boolean value = receiver.isWheelScrollingEnabled(); }
    static void call25(java.awt.ScrollPane receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_ScrollPane extends java.awt.ScrollPane {
    Override_java_awt_ScrollPane() throws java.awt.HeadlessException { super(); }
    @Override
    public int getScrollbarDisplayPolicy()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getViewportSize()  { throw new UnsupportedOperationException(); }
    @Override
    public int getHScrollbarHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public int getVScrollbarWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Adjustable getVAdjustable()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Adjustable getHAdjustable()  { throw new UnsupportedOperationException(); }
    @Override
    public void setScrollPosition(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setScrollPosition(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getScrollPosition()  { throw new UnsupportedOperationException(); }
    @Override
    public void doLayout()  { throw new UnsupportedOperationException(); }
    @Override
    public void layout()  { throw new UnsupportedOperationException(); }
    @Override
    public void printComponents(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    protected void processMouseWheelEvent(java.awt.event.MouseWheelEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected boolean eventTypeEnabled(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setWheelScrollingEnabled(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isWheelScrollingEnabled()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
