package java.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Transparency;

public abstract class VolatileImage extends Image implements Transparency {
    public static final int IMAGE_OK = 0;
    public static final int IMAGE_RESTORED = 1;
    public static final int IMAGE_INCOMPATIBLE = 2;
    protected int transparency = TRANSLUCENT;

    protected VolatileImage() {
    }

    public abstract BufferedImage getSnapshot();

    public abstract int getWidth();

    public abstract int getHeight();

    @Override
    public ImageProducer getSource() {
        return getSnapshot().getSource();
    }

    @Override
    public Graphics getGraphics() {
        return createGraphics();
    }

    public abstract Graphics2D createGraphics();

    public abstract int validate(GraphicsConfiguration configuration);

    public abstract boolean contentsLost();

    public abstract ImageCapabilities getCapabilities();

    @Override
    public int getTransparency() {
        return transparency;
    }
}
