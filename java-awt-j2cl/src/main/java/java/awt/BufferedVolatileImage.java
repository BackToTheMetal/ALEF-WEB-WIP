package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;

final class BufferedVolatileImage extends VolatileImage {
    private final BufferedImage image;
    private final ImageCapabilities capabilities;

    BufferedVolatileImage(int width, int height, int transparency, ImageCapabilities capabilities) {
        this.transparency = transparency;
        this.capabilities = capabilities;
        image = new BufferedImage(width, height, transparency == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public BufferedImage getSnapshot() {
        BufferedImage snapshot = new BufferedImage(getWidth(), getHeight(), transparency == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        snapshot.setData(image.getRaster());
        return snapshot;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return getHeight();
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return image.getProperty(name, observer);
    }

    @Override
    public Graphics2D createGraphics() {
        return image.createGraphics();
    }

    @Override
    public int validate(GraphicsConfiguration configuration) {
        return IMAGE_OK;
    }

    @Override
    public boolean contentsLost() {
        return false;
    }

    @Override
    public ImageCapabilities getCapabilities() {
        return capabilities;
    }
}
