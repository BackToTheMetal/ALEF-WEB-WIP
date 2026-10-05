package java.awt.im.spi;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_im_spi_InputMethod {
    static void call0(java.awt.im.spi.InputMethod receiver)  { receiver.setInputMethodContext((java.awt.im.spi.InputMethodContext) null); }
    static void call1(java.awt.im.spi.InputMethod receiver)  { boolean value = receiver.setLocale((java.util.Locale) null); }
    static void call2(java.awt.im.spi.InputMethod receiver)  { java.util.Locale value = receiver.getLocale(); }
    static void call3(java.awt.im.spi.InputMethod receiver)  { receiver.setCompositionEnabled(false); }
    static void call4(java.awt.im.spi.InputMethod receiver)  { boolean value = receiver.isCompositionEnabled(); }
    static void call5(java.awt.im.spi.InputMethod receiver)  { receiver.reconvert(); }
    static void call6(java.awt.im.spi.InputMethod receiver)  { receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call7(java.awt.im.spi.InputMethod receiver)  { receiver.notifyClientWindowChange((java.awt.Rectangle) null); }
    static void call8(java.awt.im.spi.InputMethod receiver)  { receiver.activate(); }
    static void call9(java.awt.im.spi.InputMethod receiver)  { receiver.deactivate(false); }
    static void call10(java.awt.im.spi.InputMethod receiver)  { receiver.hideWindows(); }
    static void call11(java.awt.im.spi.InputMethod receiver)  { receiver.removeNotify(); }
    static void call12(java.awt.im.spi.InputMethod receiver)  { receiver.endComposition(); }
    static void call13(java.awt.im.spi.InputMethod receiver)  { receiver.dispose(); }
    static void call14(java.awt.im.spi.InputMethod receiver)  { java.lang.Object value = receiver.getControlObject(); }
}
