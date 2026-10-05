package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GraphicsEnvironment {
    static void call0()  { java.awt.GraphicsEnvironment value = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment(); }
    static void call1()  { boolean value = java.awt.GraphicsEnvironment.isHeadless(); }
    static void call2(java.awt.GraphicsEnvironment receiver)  { boolean value = receiver.isHeadlessInstance(); }
    static void call3(java.awt.GraphicsEnvironment receiver) throws java.awt.HeadlessException { try { java.awt.GraphicsDevice[] value = receiver.getScreenDevices(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4(java.awt.GraphicsEnvironment receiver) throws java.awt.HeadlessException { try { java.awt.GraphicsDevice value = receiver.getDefaultScreenDevice(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5(java.awt.GraphicsEnvironment receiver)  { java.awt.Graphics2D value = receiver.createGraphics((java.awt.image.BufferedImage) null); }
    static void call6(java.awt.GraphicsEnvironment receiver)  { java.awt.Font[] value = receiver.getAllFonts(); }
    static void call7(java.awt.GraphicsEnvironment receiver)  { java.lang.String[] value = receiver.getAvailableFontFamilyNames(); }
    static void call8(java.awt.GraphicsEnvironment receiver)  { java.lang.String[] value = receiver.getAvailableFontFamilyNames((java.util.Locale) null); }
    static void call9(java.awt.GraphicsEnvironment receiver)  { boolean value = receiver.registerFont((java.awt.Font) null); }
    static void call10(java.awt.GraphicsEnvironment receiver)  { receiver.preferLocaleFonts(); }
    static void call11(java.awt.GraphicsEnvironment receiver)  { receiver.preferProportionalFonts(); }
    static void call12(java.awt.GraphicsEnvironment receiver) throws java.awt.HeadlessException { try { java.awt.Point value = receiver.getCenterPoint(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call13(java.awt.GraphicsEnvironment receiver) throws java.awt.HeadlessException { try { java.awt.Rectangle value = receiver.getMaximumWindowBounds(); } catch (java.awt.HeadlessException expected) { throw expected; } }
}

abstract class Override_java_awt_GraphicsEnvironment extends java.awt.GraphicsEnvironment {
    Override_java_awt_GraphicsEnvironment()  { super(); }
    @Override
    public boolean isHeadlessInstance()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsDevice[] getScreenDevices() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsDevice getDefaultScreenDevice() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics2D createGraphics(java.awt.image.BufferedImage arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font[] getAllFonts()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getAvailableFontFamilyNames()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getAvailableFontFamilyNames(java.util.Locale arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean registerFont(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void preferLocaleFonts()  { throw new UnsupportedOperationException(); }
    @Override
    public void preferProportionalFonts()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getCenterPoint() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getMaximumWindowBounds() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
}
