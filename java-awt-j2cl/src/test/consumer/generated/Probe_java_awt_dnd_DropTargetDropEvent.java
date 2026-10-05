package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DropTargetDropEvent {
    static void call0()  { java.awt.dnd.DropTargetDropEvent value = new java.awt.dnd.DropTargetDropEvent((java.awt.dnd.DropTargetContext) null, (java.awt.Point) null, (int) 0, (int) 0); }
    static void call1()  { java.awt.dnd.DropTargetDropEvent value = new java.awt.dnd.DropTargetDropEvent((java.awt.dnd.DropTargetContext) null, (java.awt.Point) null, (int) 0, (int) 0, false); }
    static void call2(java.awt.dnd.DropTargetDropEvent receiver)  { java.awt.Point value = receiver.getLocation(); }
    static void call3(java.awt.dnd.DropTargetDropEvent receiver)  { java.awt.datatransfer.DataFlavor[] value = receiver.getCurrentDataFlavors(); }
    static void call4(java.awt.dnd.DropTargetDropEvent receiver)  { java.util.List value = receiver.getCurrentDataFlavorsAsList(); }
    static void call5(java.awt.dnd.DropTargetDropEvent receiver)  { boolean value = receiver.isDataFlavorSupported((java.awt.datatransfer.DataFlavor) null); }
    static void call6(java.awt.dnd.DropTargetDropEvent receiver)  { int value = receiver.getSourceActions(); }
    static void call7(java.awt.dnd.DropTargetDropEvent receiver)  { int value = receiver.getDropAction(); }
    static void call8(java.awt.dnd.DropTargetDropEvent receiver)  { java.awt.datatransfer.Transferable value = receiver.getTransferable(); }
    static void call9(java.awt.dnd.DropTargetDropEvent receiver)  { receiver.acceptDrop((int) 0); }
    static void call10(java.awt.dnd.DropTargetDropEvent receiver)  { receiver.rejectDrop(); }
    static void call11(java.awt.dnd.DropTargetDropEvent receiver)  { receiver.dropComplete(false); }
    static void call12(java.awt.dnd.DropTargetDropEvent receiver)  { boolean value = receiver.isLocalTransfer(); }
}

abstract class Override_java_awt_dnd_DropTargetDropEvent extends java.awt.dnd.DropTargetDropEvent {
    Override_java_awt_dnd_DropTargetDropEvent()  { super((java.awt.dnd.DropTargetContext) null, (java.awt.Point) null, (int) 0, (int) 0); }
    @Override
    public java.awt.Point getLocation()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.DataFlavor[] getCurrentDataFlavors()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.List<java.awt.datatransfer.DataFlavor> getCurrentDataFlavorsAsList()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getSourceActions()  { throw new UnsupportedOperationException(); }
    @Override
    public int getDropAction()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.Transferable getTransferable()  { throw new UnsupportedOperationException(); }
    @Override
    public void acceptDrop(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void rejectDrop()  { throw new UnsupportedOperationException(); }
    @Override
    public void dropComplete(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isLocalTransfer()  { throw new UnsupportedOperationException(); }
}
