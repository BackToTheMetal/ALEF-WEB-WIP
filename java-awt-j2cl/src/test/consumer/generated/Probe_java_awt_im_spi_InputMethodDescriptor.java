package java.awt.im.spi;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_im_spi_InputMethodDescriptor {
    static void call0(java.awt.im.spi.InputMethodDescriptor receiver) throws java.awt.AWTException { try { java.util.Locale[] value = receiver.getAvailableLocales(); } catch (java.awt.AWTException expected) { throw expected; } }
    static void call1(java.awt.im.spi.InputMethodDescriptor receiver)  { boolean value = receiver.hasDynamicLocaleList(); }
    static void call2(java.awt.im.spi.InputMethodDescriptor receiver)  { java.lang.String value = receiver.getInputMethodDisplayName((java.util.Locale) null, (java.util.Locale) null); }
    static void call3(java.awt.im.spi.InputMethodDescriptor receiver)  { java.awt.Image value = receiver.getInputMethodIcon((java.util.Locale) null); }
    static void call4(java.awt.im.spi.InputMethodDescriptor receiver) throws java.lang.Exception { try { java.awt.im.spi.InputMethod value = receiver.createInputMethod(); } catch (java.lang.Exception expected) { throw expected; } }
}
