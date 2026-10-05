package java.awt;

public final class DisplayMode {
    public static final int BIT_DEPTH_MULTI = -1;
    public static final int REFRESH_RATE_UNKNOWN = 0;
    private final Dimension size;
    private final int bitDepth;
    private final int refreshRate;

    public DisplayMode(int width, int height, int bitDepth, int refreshRate) {
        size = new Dimension(width, height);
        this.bitDepth = bitDepth;
        this.refreshRate = refreshRate;
    }

    public int getHeight() {
        return size.height;
    }

    public int getWidth() {
        return size.width;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public boolean equals(DisplayMode mode) {
        return mode != null && getWidth() == mode.getWidth() && getHeight() == mode.getHeight() && bitDepth == mode.bitDepth && refreshRate == mode.refreshRate;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof DisplayMode && equals((DisplayMode) object);
    }

    @Override
    public int hashCode() {
        return getWidth() + getHeight() + bitDepth * 7 + refreshRate * 13;
    }

    @Override
    public String toString() {
        String depth = bitDepth == BIT_DEPTH_MULTI ? "Multi" : Integer.toString(bitDepth);
        String refresh = refreshRate == REFRESH_RATE_UNKNOWN ? "Unknown" : Integer.toString(refreshRate);
        return getWidth() + "x" + getHeight() + "x" + depth + "bpp@" + refresh + "Hz";
    }
}
