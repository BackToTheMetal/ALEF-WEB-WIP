package org.webawt.internal.peer;

/** Browser text ink bounds and tiled coverage, independent of AWT objects. */
public interface FontRasterizer {
    /** Untransformed ink bounds: left, top, width, height relative to the baseline. */
    double[] bounds(String cssFont, String text);
    /** Row-major alpha coverage (0..255) for a device-space tile. */
    int[] rasterize(String cssFont, String text, double[] transform, int x, int y, int width, int height);
}
