package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

final class ProducerImage extends Image implements ImageConsumer {
    private final ImageProducer producer;
    private final List<ImageObserver> observers = new ArrayList<>();
    private BufferedImage image;
    private ColorModel colorModel = ColorModel.getRGBdefault();
    private int width = -1;
    private int height = -1;
    private int completionStatus;

    ProducerImage(ImageProducer producer) {
        if (producer == null) {
            throw new NullPointerException("producer");
        }
        this.producer = producer;
        producer.startProduction(this);
    }

    void release() {
        producer.removeConsumer(this);
        observers.clear();
        image = null;
    }

    @Override
    public int getWidth(ImageObserver observer) {
        addObserver(observer);
        return width;
    }

    @Override
    public int getHeight(ImageObserver observer) {
        addObserver(observer);
        return height;
    }

    @Override
    public ImageProducer getSource() {
        return producer;
    }

    @Override
    public Graphics getGraphics() {
        return image == null ? null : image.getGraphics();
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        addObserver(observer);
        return UndefinedProperty;
    }

    @Override
    public void setDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            imageComplete(IMAGEERROR);
            return;
        }
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        notifyObservers(ImageObserver.WIDTH | ImageObserver.HEIGHT, 0, 0, width, height);
    }

    @Override
    public void setColorModel(ColorModel model) {
        if (model != null) {
            colorModel = model;
        }
    }

    @Override
    public void setHints(int hints) {
        // The raster accepts every producer ordering, so no alternate buffering strategy is required.
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        if (image == null) {
            return;
        }
        ColorModel effectiveModel = currentColorModel(model);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                image.setRGB(x + column, y + row, effectiveModel.getRGB(pixels[offset + row * scanlineStride + column] & 0xff));
            }
        }
        notifyObservers(ImageObserver.SOMEBITS, x, y, width, height);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        if (image == null) {
            return;
        }
        ColorModel effectiveModel = currentColorModel(model);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                image.setRGB(x + column, y + row, effectiveModel.getRGB(pixels[offset + row * scanlineStride + column]));
            }
        }
        notifyObservers(ImageObserver.SOMEBITS, x, y, width, height);
    }

    @Override
    public void imageComplete(int status) {
        if (status == IMAGEERROR) {
            completionStatus = ImageObserver.ERROR;
        } else if (status == IMAGEABORTED) {
            completionStatus = ImageObserver.ABORT;
        } else if (status == SINGLEFRAMEDONE) {
            completionStatus = ImageObserver.FRAMEBITS;
        } else if (status == STATICIMAGEDONE) {
            completionStatus = ImageObserver.ALLBITS;
        }
        notifyObservers(completionStatus, 0, 0, width, height);
        if (completionStatus == ImageObserver.ALLBITS || completionStatus == ImageObserver.ERROR || completionStatus == ImageObserver.ABORT) {
            observers.clear();
        }
    }

    int getCompletionStatus() {
        return completionStatus;
    }

    private void addObserver(ImageObserver observer) {
        if (observer == null || completionStatus != 0 || observers.contains(observer)) {
            return;
        }
        observers.add(observer);
    }

    private void notifyObservers(int flags, int x, int y, int width, int height) {
        List<ImageObserver> snapshot = new ArrayList<>(observers);
        for (ImageObserver observer : snapshot) {
            if (!observer.imageUpdate(this, flags, x, y, width, height)) {
                observers.remove(observer);
            }
        }
    }

    private ColorModel currentColorModel(ColorModel model) {
        return model == null ? colorModel : model;
    }
}
