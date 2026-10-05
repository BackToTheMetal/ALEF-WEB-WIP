package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_EventQueue {
    static void call0()  { java.awt.EventQueue value = new java.awt.EventQueue(); }
    static void call1(java.awt.EventQueue receiver)  { receiver.postEvent((java.awt.AWTEvent) null); }
    static void call2(java.awt.EventQueue receiver) throws java.lang.InterruptedException { try { java.awt.AWTEvent value = receiver.getNextEvent(); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call3(java.awt.EventQueue receiver)  { java.awt.AWTEvent value = receiver.peekEvent(); }
    static void call4(java.awt.EventQueue receiver)  { java.awt.AWTEvent value = receiver.peekEvent((int) 0); }
    static void call5(java.awt.EventQueue receiver)  { receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call6()  { long value = java.awt.EventQueue.getMostRecentEventTime(); }
    static void call7()  { java.awt.AWTEvent value = java.awt.EventQueue.getCurrentEvent(); }
    static void call8(java.awt.EventQueue receiver)  { receiver.push((java.awt.EventQueue) null); }
    static void call9(java.awt.EventQueue receiver) throws java.util.EmptyStackException { try { receiver.pop(); } catch (java.util.EmptyStackException expected) { throw expected; } }
    static void call10()  { boolean value = java.awt.EventQueue.isDispatchThread(); }
    static void call11()  { java.awt.EventQueue.invokeLater((java.lang.Runnable) null); }
}

abstract class Override_java_awt_EventQueue extends java.awt.EventQueue {
    Override_java_awt_EventQueue()  { super(); }
    @Override
    public void postEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.AWTEvent getNextEvent() throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.AWTEvent peekEvent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.AWTEvent peekEvent(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void dispatchEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void push(java.awt.EventQueue arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void pop() throws java.util.EmptyStackException { throw new UnsupportedOperationException(); }
}
