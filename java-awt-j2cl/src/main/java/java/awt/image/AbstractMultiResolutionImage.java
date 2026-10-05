package java.awt.image;

import java.awt.Graphics;
import java.awt.Image;

public abstract class AbstractMultiResolutionImage extends Image implements MultiResolutionImage {
    protected AbstractMultiResolutionImage() {
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return getBaseImage().getWidth(observer);
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return getBaseImage().getHeight(observer);
    }

    @Override
    public ImageProducer getSource() {
        return getBaseImage().getSource();
    }

    @Override
    public Graphics getGraphics() {
        throw new UnsupportedOperationException("getGraphics() not supported on Multi-Resolution Images");
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return getBaseImage().getProperty(name, observer);
    }

    protected abstract Image getBaseImage();
}
