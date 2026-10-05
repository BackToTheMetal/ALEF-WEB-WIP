package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Canvas {
    static void call0()  { java.awt.Canvas value = new java.awt.Canvas(); }
    static void call1()  { java.awt.Canvas value = new java.awt.Canvas((java.awt.GraphicsConfiguration) null); }
    static void call2(java.awt.Canvas receiver)  { receiver.addNotify(); }
    static void call3(java.awt.Canvas receiver)  { receiver.paint((java.awt.Graphics) null); }
    static void call4(java.awt.Canvas receiver)  { receiver.update((java.awt.Graphics) null); }
    static void call5(java.awt.Canvas receiver)  { receiver.createBufferStrategy((int) 0); }
    static void call6(java.awt.Canvas receiver) throws java.awt.AWTException { try { receiver.createBufferStrategy((int) 0, (java.awt.BufferCapabilities) null); } catch (java.awt.AWTException expected) { throw expected; } }
    static void call7(java.awt.Canvas receiver)  { java.awt.image.BufferStrategy value = receiver.getBufferStrategy(); }
    static void call8(java.awt.Canvas receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Canvas extends java.awt.Canvas {
    Override_java_awt_Canvas()  { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void paint(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void update(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void createBufferStrategy(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void createBufferStrategy(int arg0, java.awt.BufferCapabilities arg1) throws java.awt.AWTException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.BufferStrategy getBufferStrategy()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
