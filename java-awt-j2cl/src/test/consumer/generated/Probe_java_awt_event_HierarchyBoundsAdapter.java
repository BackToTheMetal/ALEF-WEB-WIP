package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_HierarchyBoundsAdapter {
    static void call0(java.awt.event.HierarchyBoundsAdapter receiver)  { receiver.ancestorMoved((java.awt.event.HierarchyEvent) null); }
    static void call1(java.awt.event.HierarchyBoundsAdapter receiver)  { receiver.ancestorResized((java.awt.event.HierarchyEvent) null); }
}

abstract class Override_java_awt_event_HierarchyBoundsAdapter extends java.awt.event.HierarchyBoundsAdapter {
    Override_java_awt_event_HierarchyBoundsAdapter()  { super(); }
    @Override
    public void ancestorMoved(java.awt.event.HierarchyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void ancestorResized(java.awt.event.HierarchyEvent arg0)  { throw new UnsupportedOperationException(); }
}
