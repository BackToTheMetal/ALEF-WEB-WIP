package java.awt.image;

import java.util.ArrayList;
import java.util.List;

public class MemoryImageSource implements ImageProducer {
    private final int width;
    private final int height;
    private ColorModel model;
    private byte[] bytePixels;
    private int[] intPixels;
    private int pixelOffset;
    private int scanlineStride;
    private final List<ImageConsumer> consumers = new ArrayList<ImageConsumer>();
    private boolean animated;
    private boolean fullBufferUpdates;

    public MemoryImageSource(int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        validateDimensions(width, height);
        this.width = width;
        this.height = height;
        this.model = requireModel(model);
        bytePixels = pixels;
        pixelOffset = offset;
        this.scanlineStride = scanlineStride;
    }

    public MemoryImageSource(int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        validateDimensions(width, height);
        this.width = width;
        this.height = height;
        this.model = requireModel(model);
        intPixels = pixels;
        pixelOffset = offset;
        this.scanlineStride = scanlineStride;
    }

    public MemoryImageSource(int width, int height, int[] pixels, int offset, int scanlineStride) {
        this(width, height, ColorModel.getRGBdefault(), pixels, offset, scanlineStride);
    }

    @Override
    public void addConsumer(ImageConsumer consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer must not be null");
        }
        if (isConsumer(consumer)) {
            return;
        }
        consumers.add(consumer);
        initializeAndSend(consumer);
    }

    @Override
    public boolean isConsumer(ImageConsumer consumer) {
        return consumers.contains(consumer);
    }

    @Override
    public void removeConsumer(ImageConsumer consumer) {
        consumers.remove(consumer);
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        if (isConsumer(consumer)) {
            initializeAndSend(consumer);
        } else {
            addConsumer(consumer);
        }
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
        if (!isConsumer(consumer)) {
            return;
        }
        consumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES);
        sendPixels(consumer, 0, 0, width, height);
        consumer.imageComplete(animated ? ImageConsumer.SINGLEFRAMEDONE : ImageConsumer.STATICIMAGEDONE);
        if (!animated) {
            removeConsumer(consumer);
        }
    }

    public void setAnimated(boolean animated) {
        if (this.animated && !animated) {
            List<ImageConsumer> snapshot = new ArrayList<ImageConsumer>(consumers);
            for (ImageConsumer consumer : snapshot) {
                consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
            }
            consumers.clear();
        }
        this.animated = animated;
    }

    public void setFullBufferUpdates(boolean fullBufferUpdates) {
        if (this.fullBufferUpdates == fullBufferUpdates) {
            return;
        }
        this.fullBufferUpdates = fullBufferUpdates;
        int hints = currentHints();
        List<ImageConsumer> snapshot = new ArrayList<ImageConsumer>(consumers);
        for (ImageConsumer consumer : snapshot) {
            consumer.setHints(hints);
        }
    }

    public void newPixels() {
        newPixels(0, 0, width, height, true);
    }

    public void newPixels(int x, int y, int width, int height) {
        newPixels(x, y, width, height, true);
    }

    public void newPixels(int x, int y, int width, int height, boolean frameNotify) {
        if (!animated) {
            return;
        }
        int left = Math.max(0, x);
        int top = Math.max(0, y);
        int right = Math.min(this.width, x + width);
        int bottom = Math.min(this.height, y + height);
        if (fullBufferUpdates) {
            left = 0;
            top = 0;
            right = this.width;
            bottom = this.height;
        }
        List<ImageConsumer> snapshot = new ArrayList<ImageConsumer>(consumers);
        for (ImageConsumer consumer : snapshot) {
            if (right > left && bottom > top) {
                sendPixels(consumer, left, top, right - left, bottom - top);
            }
            if (frameNotify) {
                consumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
            }
        }
    }

    public void newPixels(byte[] pixels, ColorModel model, int offset, int scanlineStride) {
        bytePixels = pixels;
        intPixels = null;
        this.model = requireModel(model);
        pixelOffset = offset;
        this.scanlineStride = scanlineStride;
        newPixels();
    }

    public void newPixels(int[] pixels, ColorModel model, int offset, int scanlineStride) {
        intPixels = pixels;
        bytePixels = null;
        this.model = requireModel(model);
        pixelOffset = offset;
        this.scanlineStride = scanlineStride;
        newPixels();
    }

    private void initializeAndSend(ImageConsumer consumer) {
        consumer.setDimensions(width, height);
        consumer.setColorModel(model);
        consumer.setHints(currentHints());
        sendPixels(consumer, 0, 0, width, height);
        consumer.imageComplete(animated ? ImageConsumer.SINGLEFRAMEDONE : ImageConsumer.STATICIMAGEDONE);
        if (!animated) {
            removeConsumer(consumer);
        }
    }

    private void sendPixels(ImageConsumer consumer, int x, int y, int width, int height) {
        int offset = pixelOffset + y * scanlineStride + x;
        if (bytePixels != null) {
            consumer.setPixels(x, y, width, height, model, bytePixels, offset, scanlineStride);
        } else {
            consumer.setPixels(x, y, width, height, model, intPixels, offset, scanlineStride);
        }
    }

    private int currentHints() {
        if (!animated) {
            return ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS | ImageConsumer.SINGLEFRAME;
        }
        return fullBufferUpdates ? ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES : ImageConsumer.RANDOMPIXELORDER;
    }

    private static void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero");
        }
    }

    private static ColorModel requireModel(ColorModel model) {
        if (model == null) {
            throw new NullPointerException("color model must not be null");
        }
        return model;
    }
}
