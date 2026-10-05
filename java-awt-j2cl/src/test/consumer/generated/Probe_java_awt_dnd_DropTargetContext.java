package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DropTargetContext {
    static void call0(java.awt.dnd.DropTargetContext receiver)  { java.awt.dnd.DropTarget value = receiver.getDropTarget(); }
    static void call1(java.awt.dnd.DropTargetContext receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call2(java.awt.dnd.DropTargetContext receiver)  { receiver.setTargetActions((int) 0); }
    static void call3(java.awt.dnd.DropTargetContext receiver)  { int value = receiver.getTargetActions(); }
    static void call4(java.awt.dnd.DropTargetContext receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.dropComplete(false); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call5(java.awt.dnd.DropTargetContext receiver)  { receiver.acceptDrag((int) 0); }
    static void call6(java.awt.dnd.DropTargetContext receiver)  { receiver.rejectDrag(); }
    static void call7(java.awt.dnd.DropTargetContext receiver)  { receiver.acceptDrop((int) 0); }
    static void call8(java.awt.dnd.DropTargetContext receiver)  { receiver.rejectDrop(); }
    static void call9(java.awt.dnd.DropTargetContext receiver)  { java.awt.datatransfer.DataFlavor[] value = receiver.getCurrentDataFlavors(); }
    static void call10(java.awt.dnd.DropTargetContext receiver)  { java.util.List value = receiver.getCurrentDataFlavorsAsList(); }
    static void call11(java.awt.dnd.DropTargetContext receiver)  { boolean value = receiver.isDataFlavorSupported((java.awt.datatransfer.DataFlavor) null); }
    static void call12(java.awt.dnd.DropTargetContext receiver) throws java.awt.dnd.InvalidDnDOperationException { try { java.awt.datatransfer.Transferable value = receiver.getTransferable(); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call13(java.awt.dnd.DropTargetContext receiver)  { java.awt.datatransfer.Transferable value = receiver.createTransferableProxy((java.awt.datatransfer.Transferable) null, false); }
}
