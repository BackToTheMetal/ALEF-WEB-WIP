package java.awt.im;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_im_InputContext {
    static void call0()  { java.awt.im.InputContext value = new java.awt.im.InputContext(); }
    static void call1()  { java.awt.im.InputContext value = java.awt.im.InputContext.getInstance(); }
    static void call2(java.awt.im.InputContext receiver)  { boolean value = receiver.selectInputMethod((java.util.Locale) null); }
    static void call3(java.awt.im.InputContext receiver)  { java.util.Locale value = receiver.getLocale(); }
    static void call4(java.awt.im.InputContext receiver)  { receiver.setCompositionEnabled(false); }
    static void call5(java.awt.im.InputContext receiver)  { boolean value = receiver.isCompositionEnabled(); }
    static void call6(java.awt.im.InputContext receiver)  { receiver.reconvert(); }
    static void call7(java.awt.im.InputContext receiver)  { receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call8(java.awt.im.InputContext receiver)  { receiver.removeNotify((java.awt.Component) null); }
    static void call9(java.awt.im.InputContext receiver)  { receiver.endComposition(); }
    static void call10(java.awt.im.InputContext receiver)  { receiver.dispose(); }
    static void call11(java.awt.im.InputContext receiver)  { java.lang.Object value = receiver.getInputMethodControlObject(); }
}

abstract class Override_java_awt_im_InputContext extends java.awt.im.InputContext {
    Override_java_awt_im_InputContext()  { super(); }
    @Override
    public boolean selectInputMethod(java.util.Locale arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Locale getLocale()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCompositionEnabled(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompositionEnabled()  { throw new UnsupportedOperationException(); }
    @Override
    public void reconvert()  { throw new UnsupportedOperationException(); }
    @Override
    public void dispatchEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void endComposition()  { throw new UnsupportedOperationException(); }
    @Override
    public void dispose()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getInputMethodControlObject()  { throw new UnsupportedOperationException(); }
}
