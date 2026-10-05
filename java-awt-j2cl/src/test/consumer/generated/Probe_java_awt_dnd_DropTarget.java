package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DropTarget {
    static void call0() throws java.awt.HeadlessException { try { java.awt.dnd.DropTarget value = new java.awt.dnd.DropTarget((java.awt.Component) null, (int) 0, (java.awt.dnd.DropTargetListener) null, false, (java.awt.datatransfer.FlavorMap) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.dnd.DropTarget value = new java.awt.dnd.DropTarget((java.awt.Component) null, (int) 0, (java.awt.dnd.DropTargetListener) null, false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.dnd.DropTarget value = new java.awt.dnd.DropTarget(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3() throws java.awt.HeadlessException { try { java.awt.dnd.DropTarget value = new java.awt.dnd.DropTarget((java.awt.Component) null, (java.awt.dnd.DropTargetListener) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4() throws java.awt.HeadlessException { try { java.awt.dnd.DropTarget value = new java.awt.dnd.DropTarget((java.awt.Component) null, (int) 0, (java.awt.dnd.DropTargetListener) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5(java.awt.dnd.DropTarget receiver)  { receiver.setComponent((java.awt.Component) null); }
    static void call6(java.awt.dnd.DropTarget receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call7(java.awt.dnd.DropTarget receiver)  { receiver.setDefaultActions((int) 0); }
    static void call8(java.awt.dnd.DropTarget receiver)  { int value = receiver.getDefaultActions(); }
    static void call9(java.awt.dnd.DropTarget receiver)  { receiver.setActive(false); }
    static void call10(java.awt.dnd.DropTarget receiver)  { boolean value = receiver.isActive(); }
    static void call11(java.awt.dnd.DropTarget receiver) throws java.util.TooManyListenersException { try { receiver.addDropTargetListener((java.awt.dnd.DropTargetListener) null); } catch (java.util.TooManyListenersException expected) { throw expected; } }
    static void call12(java.awt.dnd.DropTarget receiver)  { receiver.removeDropTargetListener((java.awt.dnd.DropTargetListener) null); }
    static void call13(java.awt.dnd.DropTarget receiver)  { receiver.dragEnter((java.awt.dnd.DropTargetDragEvent) null); }
    static void call14(java.awt.dnd.DropTarget receiver)  { receiver.dragOver((java.awt.dnd.DropTargetDragEvent) null); }
    static void call15(java.awt.dnd.DropTarget receiver)  { receiver.dropActionChanged((java.awt.dnd.DropTargetDragEvent) null); }
    static void call16(java.awt.dnd.DropTarget receiver)  { receiver.dragExit((java.awt.dnd.DropTargetEvent) null); }
    static void call17(java.awt.dnd.DropTarget receiver)  { receiver.drop((java.awt.dnd.DropTargetDropEvent) null); }
    static void call18(java.awt.dnd.DropTarget receiver)  { java.awt.datatransfer.FlavorMap value = receiver.getFlavorMap(); }
    static void call19(java.awt.dnd.DropTarget receiver)  { receiver.setFlavorMap((java.awt.datatransfer.FlavorMap) null); }
    static void call20(java.awt.dnd.DropTarget receiver)  { receiver.addNotify(); }
    static void call21(java.awt.dnd.DropTarget receiver)  { receiver.removeNotify(); }
    static void call22(java.awt.dnd.DropTarget receiver)  { java.awt.dnd.DropTargetContext value = receiver.getDropTargetContext(); }
    static void call23(java.awt.dnd.DropTarget receiver)  { java.awt.dnd.DropTargetContext value = receiver.createDropTargetContext(); }
}

abstract class Override_java_awt_dnd_DropTarget extends java.awt.dnd.DropTarget {
    Override_java_awt_dnd_DropTarget() throws java.awt.HeadlessException { super((java.awt.Component) null, (int) 0, (java.awt.dnd.DropTargetListener) null, false, (java.awt.datatransfer.FlavorMap) null); }
    @Override
    public void setComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public void setDefaultActions(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getDefaultActions()  { throw new UnsupportedOperationException(); }
    @Override
    public void setActive(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isActive()  { throw new UnsupportedOperationException(); }
    @Override
    public void addDropTargetListener(java.awt.dnd.DropTargetListener arg0) throws java.util.TooManyListenersException { throw new UnsupportedOperationException(); }
    @Override
    public void removeDropTargetListener(java.awt.dnd.DropTargetListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragEnter(java.awt.dnd.DropTargetDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragOver(java.awt.dnd.DropTargetDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dropActionChanged(java.awt.dnd.DropTargetDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragExit(java.awt.dnd.DropTargetEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void drop(java.awt.dnd.DropTargetDropEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.FlavorMap getFlavorMap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFlavorMap(java.awt.datatransfer.FlavorMap arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DropTargetContext getDropTargetContext()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.dnd.DropTargetContext createDropTargetContext()  { throw new UnsupportedOperationException(); }
}
