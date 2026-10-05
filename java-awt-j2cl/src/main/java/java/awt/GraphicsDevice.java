package java.awt;

public abstract class GraphicsDevice {
    public enum WindowTranslucency {
        PERPIXEL_TRANSPARENT,
        TRANSLUCENT,
        PERPIXEL_TRANSLUCENT
    }

    public static final int TYPE_RASTER_SCREEN = 0;
    public static final int TYPE_PRINTER = 1;
    public static final int TYPE_IMAGE_BUFFER = 2;
    private Window fullScreenWindow;
    private Rectangle windowedBounds;

    protected GraphicsDevice() {
    }

    public abstract int getType();

    public abstract String getIDstring();

    public abstract GraphicsConfiguration[] getConfigurations();

    public abstract GraphicsConfiguration getDefaultConfiguration();

    public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate template) {
        if (template == null) {
            throw new NullPointerException("template");
        }
        return template.getBestConfiguration(getConfigurations());
    }

    public boolean isFullScreenSupported() {
        return false;
    }

    public void setFullScreenWindow(Window window) {
        if (fullScreenWindow == window) {
            return;
        }
        restoreWindowedBounds();
        fullScreenWindow = window;
        if (window != null) {
            windowedBounds = window.getBounds();
            window.setBounds(getDefaultConfiguration().getBounds());
            window.setVisible(true);
        }
    }

    public Window getFullScreenWindow() {
        return fullScreenWindow;
    }

    public boolean isDisplayChangeSupported() {
        return false;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        if (displayMode == null) {
            throw new IllegalArgumentException("displayMode cannot be null");
        }
        throw new UnsupportedOperationException("browser display modes cannot be changed");
    }

    public DisplayMode getDisplayMode() {
        Rectangle bounds = getDefaultConfiguration().getBounds();
        return new DisplayMode(bounds.width, bounds.height, DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
    }

    public DisplayMode[] getDisplayModes() {
        return new DisplayMode[] {getDisplayMode()};
    }

    public int getAvailableAcceleratedMemory() {
        return -1;
    }

    public boolean isWindowTranslucencySupported(WindowTranslucency translucencyKind) {
        if (translucencyKind == null) {
            throw new NullPointerException("translucencyKind");
        }
        return translucencyKind == WindowTranslucency.TRANSLUCENT || translucencyKind == WindowTranslucency.PERPIXEL_TRANSLUCENT;
    }

    private void restoreWindowedBounds() {
        if (fullScreenWindow != null && windowedBounds != null) {
            fullScreenWindow.setBounds(windowedBounds);
        }
        windowedBounds = null;
    }
}
