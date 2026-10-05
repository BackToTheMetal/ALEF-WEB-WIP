package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Panel {
    static void call0()  { java.awt.Panel value = new java.awt.Panel(); }
    static void call1()  { java.awt.Panel value = new java.awt.Panel((java.awt.LayoutManager) null); }
    static void call2(java.awt.Panel receiver)  { receiver.addNotify(); }
    static void call3(java.awt.Panel receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Panel extends java.awt.Panel {
    Override_java_awt_Panel()  { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
