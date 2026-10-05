package java.awt.datatransfer;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_datatransfer_Transferable {
    static void call0(java.awt.datatransfer.Transferable receiver)  { java.awt.datatransfer.DataFlavor[] value = receiver.getTransferDataFlavors(); }
    static void call1(java.awt.datatransfer.Transferable receiver)  { boolean value = receiver.isDataFlavorSupported((java.awt.datatransfer.DataFlavor) null); }
    static void call2(java.awt.datatransfer.Transferable receiver) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { try { try { java.lang.Object value = receiver.getTransferData((java.awt.datatransfer.DataFlavor) null); } catch (java.awt.datatransfer.UnsupportedFlavorException expected) { throw expected; } } catch (java.io.IOException expected) { throw expected; } }
}
