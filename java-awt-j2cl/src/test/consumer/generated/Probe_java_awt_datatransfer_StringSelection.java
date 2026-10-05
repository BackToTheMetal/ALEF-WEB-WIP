package java.awt.datatransfer;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_datatransfer_StringSelection {
    static void call0()  { java.awt.datatransfer.StringSelection value = new java.awt.datatransfer.StringSelection((java.lang.String) null); }
    static void call1(java.awt.datatransfer.StringSelection receiver)  { java.awt.datatransfer.DataFlavor[] value = receiver.getTransferDataFlavors(); }
    static void call2(java.awt.datatransfer.StringSelection receiver)  { boolean value = receiver.isDataFlavorSupported((java.awt.datatransfer.DataFlavor) null); }
    static void call3(java.awt.datatransfer.StringSelection receiver) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { try { try { java.lang.Object value = receiver.getTransferData((java.awt.datatransfer.DataFlavor) null); } catch (java.awt.datatransfer.UnsupportedFlavorException expected) { throw expected; } } catch (java.io.IOException expected) { throw expected; } }
    static void call4(java.awt.datatransfer.StringSelection receiver)  { receiver.lostOwnership((java.awt.datatransfer.Clipboard) null, (java.awt.datatransfer.Transferable) null); }
}

abstract class Override_java_awt_datatransfer_StringSelection extends java.awt.datatransfer.StringSelection {
    Override_java_awt_datatransfer_StringSelection()  { super((java.lang.String) null); }
    @Override
    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getTransferData(java.awt.datatransfer.DataFlavor arg0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { throw new UnsupportedOperationException(); }
    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard arg0, java.awt.datatransfer.Transferable arg1)  { throw new UnsupportedOperationException(); }
}
