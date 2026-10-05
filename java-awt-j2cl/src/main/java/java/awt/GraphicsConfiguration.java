package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.VolatileImage;

public abstract class GraphicsConfiguration {
    protected GraphicsConfiguration() {
    }

    public abstract GraphicsDevice getDevice();

    public BufferedImage createCompatibleImage(int width, int height) {
        return createCompatibleImage(width, height, Transparency.OPAQUE);
    }

    public BufferedImage createCompatibleImage(int width, int height, int transparency) {
        validateTransparency(transparency);
        return new BufferedImage(width, height, transparency == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
    }

    public VolatileImage createCompatibleVolatileImage(int width, int height) {
        return new BufferedVolatileImage(width, height, Transparency.OPAQUE, getImageCapabilities());
    }

    public VolatileImage createCompatibleVolatileImage(int width, int height, int transparency) {
        validateTransparency(transparency);
        return new BufferedVolatileImage(width, height, transparency, getImageCapabilities());
    }

    public VolatileImage createCompatibleVolatileImage(int width, int height, ImageCapabilities capabilities) throws AWTException {
        return createCompatibleVolatileImage(width, height, capabilities, Transparency.OPAQUE);
    }

    public VolatileImage createCompatibleVolatileImage(int width, int height, ImageCapabilities capabilities, int transparency) throws AWTException {
        if (capabilities == null) {
            throw new IllegalArgumentException("capabilities cannot be null");
        }
        if (capabilities.isTrueVolatile()) {
            throw new AWTException("true volatile images are unavailable in the browser profile");
        }
        validateTransparency(transparency);
        return new BufferedVolatileImage(width, height, transparency, capabilities);
    }

    public abstract ColorModel getColorModel();

    public abstract ColorModel getColorModel(int transparency);

    public abstract AffineTransform getDefaultTransform();

    public abstract AffineTransform getNormalizingTransform();

    public abstract Rectangle getBounds();

    public BufferCapabilities getBufferCapabilities() {
        ImageCapabilities capabilities = getImageCapabilities();
        return new BufferCapabilities(capabilities, capabilities, null);
    }

    public ImageCapabilities getImageCapabilities() {
        return new ImageCapabilities(false);
    }

    public boolean isTranslucencyCapable() {
        ColorModel colorModel = getColorModel(Transparency.TRANSLUCENT);
        return colorModel != null && colorModel.getTransparency() == Transparency.TRANSLUCENT;
    }

    static ColorModel colorModel(int transparency) {
        if (transparency == Transparency.OPAQUE) {
            return new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);
        }
        if (transparency == Transparency.BITMASK || transparency == Transparency.TRANSLUCENT) {
            return ColorModel.getRGBdefault();
        }
        return null;
    }

    private static void validateTransparency(int transparency) {
        if (transparency < Transparency.OPAQUE || transparency > Transparency.TRANSLUCENT) {
            throw new IllegalArgumentException("invalid transparency");
        }
    }
}
