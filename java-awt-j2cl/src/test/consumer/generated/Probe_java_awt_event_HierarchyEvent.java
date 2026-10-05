package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_HierarchyEvent {
    static void call0() { int value = java.awt.event.HierarchyEvent.HIERARCHY_FIRST; }
    static void call1() { int value = java.awt.event.HierarchyEvent.HIERARCHY_CHANGED; }
    static void call2() { int value = java.awt.event.HierarchyEvent.ANCESTOR_MOVED; }
    static void call3() { int value = java.awt.event.HierarchyEvent.ANCESTOR_RESIZED; }
    static void call4() { int value = java.awt.event.HierarchyEvent.HIERARCHY_LAST; }
    static void call5() { int value = java.awt.event.HierarchyEvent.PARENT_CHANGED; }
    static void call6() { int value = java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED; }
    static void call7() { int value = java.awt.event.HierarchyEvent.SHOWING_CHANGED; }
    static void call8()  { java.awt.event.HierarchyEvent value = new java.awt.event.HierarchyEvent((java.awt.Component) null, (int) 0, (java.awt.Component) null, (java.awt.Container) null); }
    static void call9()  { java.awt.event.HierarchyEvent value = new java.awt.event.HierarchyEvent((java.awt.Component) null, (int) 0, (java.awt.Component) null, (java.awt.Container) null, (long) 0); }
    static void call10(java.awt.event.HierarchyEvent receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call11(java.awt.event.HierarchyEvent receiver)  { java.awt.Component value = receiver.getChanged(); }
    static void call12(java.awt.event.HierarchyEvent receiver)  { java.awt.Container value = receiver.getChangedParent(); }
    static void call13(java.awt.event.HierarchyEvent receiver)  { long value = receiver.getChangeFlags(); }
    static void call14(java.awt.event.HierarchyEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_HierarchyEvent extends java.awt.event.HierarchyEvent {
    Override_java_awt_event_HierarchyEvent()  { super((java.awt.Component) null, (int) 0, (java.awt.Component) null, (java.awt.Container) null); }
    @Override
    public java.awt.Component getComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getChanged()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Container getChangedParent()  { throw new UnsupportedOperationException(); }
    @Override
    public long getChangeFlags()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
