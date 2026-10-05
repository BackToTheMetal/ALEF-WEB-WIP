package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DropTargetContext_TransferableProxy {
    static void call0(java.awt.dnd.DropTargetContext.TransferableProxy receiver) { java.awt.datatransfer.Transferable value = receiver.transferable; receiver.transferable = value; }
    static void call1(java.awt.dnd.DropTargetContext.TransferableProxy receiver) { boolean value = receiver.isLocal; receiver.isLocal = value; }
    static void call2(java.awt.dnd.DropTargetContext.TransferableProxy receiver)  { java.awt.datatransfer.DataFlavor[] value = receiver.getTransferDataFlavors(); }
    static void call3(java.awt.dnd.DropTargetContext.TransferableProxy receiver)  { boolean value = receiver.isDataFlavorSupported((java.awt.datatransfer.DataFlavor) null); }
    static void call4(java.awt.dnd.DropTargetContext.TransferableProxy receiver) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { try { try { java.lang.Object value = receiver.getTransferData((java.awt.datatransfer.DataFlavor) null); } catch (java.awt.datatransfer.UnsupportedFlavorException expected) { throw expected; } } catch (java.io.IOException expected) { throw expected; } }
}
