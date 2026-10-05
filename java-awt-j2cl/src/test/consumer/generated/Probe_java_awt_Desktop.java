package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Desktop {
    static void call0()  { java.awt.Desktop value = java.awt.Desktop.getDesktop(); }
    static void call1()  { boolean value = java.awt.Desktop.isDesktopSupported(); }
    static void call2(java.awt.Desktop receiver)  { boolean value = receiver.isSupported((java.awt.Desktop.Action) null); }
    static void call3(java.awt.Desktop receiver) throws java.io.IOException { try { receiver.browse((java.net.URI) null); } catch (java.io.IOException expected) { throw expected; } }
    static void call4(java.awt.Desktop receiver) throws java.io.IOException { try { receiver.mail(); } catch (java.io.IOException expected) { throw expected; } }
    static void call5(java.awt.Desktop receiver) throws java.io.IOException { try { receiver.mail((java.net.URI) null); } catch (java.io.IOException expected) { throw expected; } }
    static void call6(java.awt.Desktop receiver)  { receiver.addAppEventListener((java.awt.desktop.SystemEventListener) null); }
    static void call7(java.awt.Desktop receiver)  { receiver.removeAppEventListener((java.awt.desktop.SystemEventListener) null); }
    static void call8(java.awt.Desktop receiver)  { receiver.setQuitHandler((java.awt.desktop.QuitHandler) null); }
    static void call9(java.awt.Desktop receiver)  { receiver.enableSuddenTermination(); }
    static void call10(java.awt.Desktop receiver)  { receiver.disableSuddenTermination(); }
    static void call11(java.awt.Desktop receiver)  { receiver.requestForeground(false); }
}
