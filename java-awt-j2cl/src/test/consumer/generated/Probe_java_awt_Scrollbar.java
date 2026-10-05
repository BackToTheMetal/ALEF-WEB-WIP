package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Scrollbar {
    static void call0() { int value = java.awt.Scrollbar.HORIZONTAL; }
    static void call1() { int value = java.awt.Scrollbar.VERTICAL; }
    static void call2() throws java.awt.HeadlessException { try { java.awt.Scrollbar value = new java.awt.Scrollbar(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3() throws java.awt.HeadlessException { try { java.awt.Scrollbar value = new java.awt.Scrollbar((int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4() throws java.awt.HeadlessException { try { java.awt.Scrollbar value = new java.awt.Scrollbar((int) 0, (int) 0, (int) 0, (int) 0, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5(java.awt.Scrollbar receiver)  { receiver.addNotify(); }
    static void call6(java.awt.Scrollbar receiver)  { int value = receiver.getOrientation(); }
    static void call7(java.awt.Scrollbar receiver)  { receiver.setOrientation((int) 0); }
    static void call8(java.awt.Scrollbar receiver)  { int value = receiver.getValue(); }
    static void call9(java.awt.Scrollbar receiver)  { receiver.setValue((int) 0); }
    static void call10(java.awt.Scrollbar receiver)  { int value = receiver.getMinimum(); }
    static void call11(java.awt.Scrollbar receiver)  { receiver.setMinimum((int) 0); }
    static void call12(java.awt.Scrollbar receiver)  { int value = receiver.getMaximum(); }
    static void call13(java.awt.Scrollbar receiver)  { receiver.setMaximum((int) 0); }
    static void call14(java.awt.Scrollbar receiver)  { int value = receiver.getVisibleAmount(); }
    static void call15(java.awt.Scrollbar receiver)  { int value = receiver.getVisible(); }
    static void call16(java.awt.Scrollbar receiver)  { receiver.setVisibleAmount((int) 0); }
    static void call17(java.awt.Scrollbar receiver)  { receiver.setUnitIncrement((int) 0); }
    static void call18(java.awt.Scrollbar receiver)  { receiver.setLineIncrement((int) 0); }
    static void call19(java.awt.Scrollbar receiver)  { int value = receiver.getUnitIncrement(); }
    static void call20(java.awt.Scrollbar receiver)  { int value = receiver.getLineIncrement(); }
    static void call21(java.awt.Scrollbar receiver)  { receiver.setBlockIncrement((int) 0); }
    static void call22(java.awt.Scrollbar receiver)  { receiver.setPageIncrement((int) 0); }
    static void call23(java.awt.Scrollbar receiver)  { int value = receiver.getBlockIncrement(); }
    static void call24(java.awt.Scrollbar receiver)  { int value = receiver.getPageIncrement(); }
    static void call25(java.awt.Scrollbar receiver)  { receiver.setValues((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call26(java.awt.Scrollbar receiver)  { boolean value = receiver.getValueIsAdjusting(); }
    static void call27(java.awt.Scrollbar receiver)  { receiver.setValueIsAdjusting(false); }
    static void call28(java.awt.Scrollbar receiver)  { receiver.addAdjustmentListener((java.awt.event.AdjustmentListener) null); }
    static void call29(java.awt.Scrollbar receiver)  { receiver.removeAdjustmentListener((java.awt.event.AdjustmentListener) null); }
    static void call30(java.awt.Scrollbar receiver)  { java.awt.event.AdjustmentListener[] value = receiver.getAdjustmentListeners(); }
    static void call31(java.awt.Scrollbar receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call32(java.awt.Scrollbar receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call33(java.awt.Scrollbar receiver)  { receiver.processAdjustmentEvent((java.awt.event.AdjustmentEvent) null); }
    static void call34(java.awt.Scrollbar receiver)  { java.lang.String value = receiver.paramString(); }
    static void call35(java.awt.Scrollbar receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Scrollbar extends java.awt.Scrollbar {
    Override_java_awt_Scrollbar() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public int getOrientation()  { throw new UnsupportedOperationException(); }
    @Override
    public void setOrientation(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getValue()  { throw new UnsupportedOperationException(); }
    @Override
    public void setValue(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getMinimum()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMinimum(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getMaximum()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMaximum(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getVisibleAmount()  { throw new UnsupportedOperationException(); }
    @Override
    public int getVisible()  { throw new UnsupportedOperationException(); }
    @Override
    public void setVisibleAmount(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setUnitIncrement(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLineIncrement(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getUnitIncrement()  { throw new UnsupportedOperationException(); }
    @Override
    public int getLineIncrement()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBlockIncrement(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPageIncrement(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getBlockIncrement()  { throw new UnsupportedOperationException(); }
    @Override
    public int getPageIncrement()  { throw new UnsupportedOperationException(); }
    @Override
    public void setValues(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getValueIsAdjusting()  { throw new UnsupportedOperationException(); }
    @Override
    public void setValueIsAdjusting(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addAdjustmentListener(java.awt.event.AdjustmentListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAdjustmentListener(java.awt.event.AdjustmentListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.AdjustmentListener[] getAdjustmentListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processAdjustmentEvent(java.awt.event.AdjustmentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
