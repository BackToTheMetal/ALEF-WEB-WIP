package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GraphicsDevice {
    static void call0() { int value = java.awt.GraphicsDevice.TYPE_RASTER_SCREEN; }
    static void call1() { int value = java.awt.GraphicsDevice.TYPE_PRINTER; }
    static void call2() { int value = java.awt.GraphicsDevice.TYPE_IMAGE_BUFFER; }
    static void call3(java.awt.GraphicsDevice receiver)  { int value = receiver.getType(); }
    static void call4(java.awt.GraphicsDevice receiver)  { java.lang.String value = receiver.getIDstring(); }
    static void call5(java.awt.GraphicsDevice receiver)  { java.awt.GraphicsConfiguration[] value = receiver.getConfigurations(); }
    static void call6(java.awt.GraphicsDevice receiver)  { java.awt.GraphicsConfiguration value = receiver.getDefaultConfiguration(); }
    static void call7(java.awt.GraphicsDevice receiver)  { java.awt.GraphicsConfiguration value = receiver.getBestConfiguration((java.awt.GraphicsConfigTemplate) null); }
    static void call8(java.awt.GraphicsDevice receiver)  { boolean value = receiver.isFullScreenSupported(); }
    static void call9(java.awt.GraphicsDevice receiver)  { receiver.setFullScreenWindow((java.awt.Window) null); }
    static void call10(java.awt.GraphicsDevice receiver)  { java.awt.Window value = receiver.getFullScreenWindow(); }
    static void call11(java.awt.GraphicsDevice receiver)  { boolean value = receiver.isDisplayChangeSupported(); }
    static void call12(java.awt.GraphicsDevice receiver)  { receiver.setDisplayMode((java.awt.DisplayMode) null); }
    static void call13(java.awt.GraphicsDevice receiver)  { java.awt.DisplayMode value = receiver.getDisplayMode(); }
    static void call14(java.awt.GraphicsDevice receiver)  { java.awt.DisplayMode[] value = receiver.getDisplayModes(); }
    static void call15(java.awt.GraphicsDevice receiver)  { int value = receiver.getAvailableAcceleratedMemory(); }
    static void call16(java.awt.GraphicsDevice receiver)  { boolean value = receiver.isWindowTranslucencySupported((java.awt.GraphicsDevice.WindowTranslucency) null); }
}

abstract class Override_java_awt_GraphicsDevice extends java.awt.GraphicsDevice {
    Override_java_awt_GraphicsDevice()  { super(); }
    @Override
    public int getType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getIDstring()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsConfiguration[] getConfigurations()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsConfiguration getDefaultConfiguration()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsConfiguration getBestConfiguration(java.awt.GraphicsConfigTemplate arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFullScreenSupported()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFullScreenWindow(java.awt.Window arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window getFullScreenWindow()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDisplayChangeSupported()  { throw new UnsupportedOperationException(); }
    @Override
    public void setDisplayMode(java.awt.DisplayMode arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.DisplayMode getDisplayMode()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.DisplayMode[] getDisplayModes()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAvailableAcceleratedMemory()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isWindowTranslucencySupported(java.awt.GraphicsDevice.WindowTranslucency arg0)  { throw new UnsupportedOperationException(); }
}
