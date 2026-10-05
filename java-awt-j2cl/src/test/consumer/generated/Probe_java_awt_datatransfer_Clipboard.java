package java.awt.datatransfer;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_datatransfer_Clipboard {
    static void call0(java.awt.datatransfer.Clipboard receiver) { java.awt.datatransfer.ClipboardOwner value = receiver.owner; receiver.owner = value; }
    static void call1(java.awt.datatransfer.Clipboard receiver) { java.awt.datatransfer.Transferable value = receiver.contents; receiver.contents = value; }
    static void call2()  { java.awt.datatransfer.Clipboard value = new java.awt.datatransfer.Clipboard((java.lang.String) null); }
    static void call3(java.awt.datatransfer.Clipboard receiver)  { java.lang.String value = receiver.getName(); }
    static void call4(java.awt.datatransfer.Clipboard receiver)  { receiver.setContents((java.awt.datatransfer.Transferable) null, (java.awt.datatransfer.ClipboardOwner) null); }
    static void call5(java.awt.datatransfer.Clipboard receiver)  { java.awt.datatransfer.Transferable value = receiver.getContents((java.lang.Object) null); }
    static void call6(java.awt.datatransfer.Clipboard receiver)  { java.awt.datatransfer.DataFlavor[] value = receiver.getAvailableDataFlavors(); }
    static void call7(java.awt.datatransfer.Clipboard receiver)  { boolean value = receiver.isDataFlavorAvailable((java.awt.datatransfer.DataFlavor) null); }
    static void call8(java.awt.datatransfer.Clipboard receiver) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { try { try { java.lang.Object value = receiver.getData((java.awt.datatransfer.DataFlavor) null); } catch (java.awt.datatransfer.UnsupportedFlavorException expected) { throw expected; } } catch (java.io.IOException expected) { throw expected; } }
    static void call9(java.awt.datatransfer.Clipboard receiver)  { receiver.addFlavorListener((java.awt.datatransfer.FlavorListener) null); }
    static void call10(java.awt.datatransfer.Clipboard receiver)  { receiver.removeFlavorListener((java.awt.datatransfer.FlavorListener) null); }
    static void call11(java.awt.datatransfer.Clipboard receiver)  { java.awt.datatransfer.FlavorListener[] value = receiver.getFlavorListeners(); }
}

abstract class Override_java_awt_datatransfer_Clipboard extends java.awt.datatransfer.Clipboard {
    Override_java_awt_datatransfer_Clipboard()  { super((java.lang.String) null); }
    @Override
    public java.lang.String getName()  { throw new UnsupportedOperationException(); }
    @Override
    public void setContents(java.awt.datatransfer.Transferable arg0, java.awt.datatransfer.ClipboardOwner arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.Transferable getContents(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.DataFlavor[] getAvailableDataFlavors()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDataFlavorAvailable(java.awt.datatransfer.DataFlavor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getData(java.awt.datatransfer.DataFlavor arg0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { throw new UnsupportedOperationException(); }
    @Override
    public void addFlavorListener(java.awt.datatransfer.FlavorListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeFlavorListener(java.awt.datatransfer.FlavorListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.FlavorListener[] getFlavorListeners()  { throw new UnsupportedOperationException(); }
}
