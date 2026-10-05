package java.awt.image;

import java.awt.Image;

public class PixelGrabber implements ImageConsumer {
    private static final int GRABBED_BITS = ImageObserver.FRAMEBITS | ImageObserver.ALLBITS;
    private static final int DONE_BITS = GRABBED_BITS | ImageObserver.ERROR;
    private byte[] bytePixels;
    private int destinationHeight;
    private int destinationOffset;
    private int destinationScanlineStride;
    private int destinationWidth;
    private int destinationX;
    private int destinationY;
    private int flags;
    private boolean grabbing;
    private ColorModel imageModel;
    private int[] intPixels;
    private final ImageProducer producer;

    public PixelGrabber(Image image, int x, int y, int width, int height, int[] pixels, int offset, int scanlineStride) {
        this(image.getSource(), x, y, width, height, pixels, offset, scanlineStride);
    }

    public PixelGrabber(ImageProducer producer, int x, int y, int width, int height, int[] pixels, int offset, int scanlineStride) {
        this.producer = producer;
        destinationX = x;
        destinationY = y;
        destinationWidth = width;
        destinationHeight = height;
        destinationOffset = offset;
        destinationScanlineStride = scanlineStride;
        intPixels = pixels;
        imageModel = ColorModel.getRGBdefault();
    }

    public PixelGrabber(Image image, int x, int y, int width, int height, boolean forceRgb) {
        producer = image.getSource();
        destinationX = x;
        destinationY = y;
        destinationWidth = width;
        destinationHeight = height;
        if (forceRgb) {
            imageModel = ColorModel.getRGBdefault();
        }
    }

    public synchronized void startGrabbing() {
        if ((flags & DONE_BITS) != 0 || grabbing) {
            return;
        }
        grabbing = true;
        flags &= ~ImageObserver.ABORT;
        producer.startProduction(this);
    }

    public synchronized void abortGrabbing() {
        imageComplete(IMAGEABORTED);
    }

    public boolean grabPixels() throws InterruptedException {
        return grabPixels(0);
    }

    public synchronized boolean grabPixels(long milliseconds) throws InterruptedException {
        if ((flags & DONE_BITS) == 0 && !grabbing) {
            startGrabbing();
        }
        return (flags & GRABBED_BITS) != 0;
    }

    public synchronized int getStatus() {
        return flags;
    }

    public synchronized int getWidth() {
        return destinationWidth < 0 ? -1 : destinationWidth;
    }

    public synchronized int getHeight() {
        return destinationHeight < 0 ? -1 : destinationHeight;
    }

    public synchronized Object getPixels() {
        return bytePixels == null ? intPixels : bytePixels;
    }

    public synchronized ColorModel getColorModel() {
        return imageModel;
    }

    @Override
    public void setDimensions(int width, int height) {
        if (destinationWidth < 0) {
            destinationWidth = width - destinationX;
        }
        if (destinationHeight < 0) {
            destinationHeight = height - destinationY;
        }
        if (destinationWidth <= 0 || destinationHeight <= 0) {
            imageComplete(STATICIMAGEDONE);
        } else if (intPixels == null && imageModel == ColorModel.getRGBdefault()) {
            intPixels = new int[destinationWidth * destinationHeight];
            destinationScanlineStride = destinationWidth;
            destinationOffset = 0;
        }
        flags |= ImageObserver.WIDTH | ImageObserver.HEIGHT;
    }

    @Override
    public void setHints(int hints) {
    }

    @Override
    public void setColorModel(ColorModel model) {
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] source, int offset, int scanlineStride) {
        PixelRegion region = clip(x, y, width, height, offset, scanlineStride);
        if (region == null) {
            return;
        }
        int destinationIndex = destinationOffset + (region.y - destinationY) * destinationScanlineStride + region.x - destinationX;
        if (intPixels == null) {
            if (bytePixels == null) {
                bytePixels = new byte[destinationWidth * destinationHeight];
                destinationScanlineStride = destinationWidth;
                destinationOffset = 0;
                destinationIndex = (region.y - destinationY) * destinationWidth + region.x - destinationX;
                imageModel = model;
            } else if (imageModel != model) {
                convertToRgb();
                destinationIndex = (region.y - destinationY) * destinationWidth + region.x - destinationX;
            }
            if (bytePixels != null) {
                copyBytes(source, region, scanlineStride, destinationIndex);
            }
        }
        if (intPixels != null) {
            copyConvertedBytes(source, model, region, scanlineStride, destinationIndex);
        }
        flags |= ImageObserver.SOMEBITS;
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] source, int offset, int scanlineStride) {
        PixelRegion region = clip(x, y, width, height, offset, scanlineStride);
        if (region == null) {
            return;
        }
        if (intPixels == null) {
            if (bytePixels == null) {
                intPixels = new int[destinationWidth * destinationHeight];
                destinationScanlineStride = destinationWidth;
                destinationOffset = 0;
                imageModel = model;
            } else {
                convertToRgb();
            }
        }
        int destinationIndex = destinationOffset + (region.y - destinationY) * destinationScanlineStride + region.x - destinationX;
        if (imageModel == model) {
            for (int row = 0; row < region.height; row++) {
                System.arraycopy(source, region.offset + row * scanlineStride, intPixels, destinationIndex + row * destinationScanlineStride, region.width);
            }
        } else {
            if (imageModel != ColorModel.getRGBdefault()) {
                convertToRgb();
                destinationIndex = (region.y - destinationY) * destinationWidth + region.x - destinationX;
            }
            for (int row = 0; row < region.height; row++) {
                int sourceIndex = region.offset + row * scanlineStride;
                int rowDestination = destinationIndex + row * destinationScanlineStride;
                for (int column = 0; column < region.width; column++) {
                    intPixels[rowDestination + column] = model.getRGB(source[sourceIndex + column]);
                }
            }
        }
        flags |= ImageObserver.SOMEBITS;
    }

    @Override
    public synchronized void imageComplete(int status) {
        grabbing = false;
        if (status == IMAGEABORTED) {
            flags |= ImageObserver.ABORT;
        } else if (status == STATICIMAGEDONE) {
            flags |= ImageObserver.ALLBITS;
        } else if (status == SINGLEFRAMEDONE) {
            flags |= ImageObserver.FRAMEBITS;
        } else {
            flags |= ImageObserver.ERROR | ImageObserver.ABORT;
        }
        producer.removeConsumer(this);
    }

    public synchronized int status() {
        return flags;
    }

    private PixelRegion clip(int x, int y, int width, int height, int offset, int scanlineStride) {
        if (y < destinationY) {
            int difference = destinationY - y;
            if (difference >= height) {
                return null;
            }
            offset += scanlineStride * difference;
            y += difference;
            height -= difference;
        }
        if (y + height > destinationY + destinationHeight) {
            height = destinationY + destinationHeight - y;
        }
        if (height <= 0) {
            return null;
        }
        if (x < destinationX) {
            int difference = destinationX - x;
            if (difference >= width) {
                return null;
            }
            offset += difference;
            x += difference;
            width -= difference;
        }
        if (x + width > destinationX + destinationWidth) {
            width = destinationX + destinationWidth - x;
        }
        return width <= 0 ? null : new PixelRegion(x, y, width, height, offset);
    }

    private void convertToRgb() {
        int[] converted = new int[destinationWidth * destinationHeight];
        for (int row = 0; row < destinationHeight; row++) {
            int sourceIndex = destinationOffset + row * destinationScanlineStride;
            int destinationIndex = row * destinationWidth;
            for (int column = 0; column < destinationWidth; column++) {
                converted[destinationIndex + column] = bytePixels == null ? imageModel.getRGB(intPixels[sourceIndex + column]) : imageModel.getRGB(bytePixels[sourceIndex + column] & 0xff);
            }
        }
        bytePixels = null;
        intPixels = converted;
        destinationScanlineStride = destinationWidth;
        destinationOffset = 0;
        imageModel = ColorModel.getRGBdefault();
    }

    private void copyBytes(byte[] source, PixelRegion region, int scanlineStride, int destinationIndex) {
        for (int row = 0; row < region.height; row++) {
            System.arraycopy(source, region.offset + row * scanlineStride, bytePixels, destinationIndex + row * destinationScanlineStride, region.width);
        }
    }

    private void copyConvertedBytes(byte[] source, ColorModel model, PixelRegion region, int scanlineStride, int destinationIndex) {
        for (int row = 0; row < region.height; row++) {
            int sourceIndex = region.offset + row * scanlineStride;
            int rowDestination = destinationIndex + row * destinationScanlineStride;
            for (int column = 0; column < region.width; column++) {
                intPixels[rowDestination + column] = model.getRGB(source[sourceIndex + column] & 0xff);
            }
        }
    }

    private static final class PixelRegion {
        private final int height;
        private final int offset;
        private final int width;
        private final int x;
        private final int y;

        private PixelRegion(int x, int y, int width, int height, int offset) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.offset = offset;
        }
    }
}
