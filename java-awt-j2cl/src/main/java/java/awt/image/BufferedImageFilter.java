package java.awt.image;

public class BufferedImageFilter extends ImageFilter implements Cloneable {
    private final BufferedImageOp bufferedImageOp;
    private int height;
    private int[] pixels;
    private int width;

    public BufferedImageFilter(BufferedImageOp operation) {
        if (operation == null) {
            throw new NullPointerException("Operation cannot be null");
        }
        bufferedImageOp = operation;
    }

    public BufferedImageOp getBufferedImageOp() {
        return bufferedImageOp;
    }

    @Override
    public ImageFilter getFilterInstance(ImageConsumer consumer) {
        BufferedImageFilter filter = new BufferedImageFilter(bufferedImageOp);
        filter.consumer = consumer;
        return filter;
    }

    @Override
    public void setDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            imageComplete(STATICIMAGEDONE);
            return;
        }
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }

    @Override
    public void setColorModel(ColorModel model) {
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] source, int offset, int scanlineStride) {
        validateChunk(width, height);
        PixelRegion region = clip(x, y, width, height, offset, scanlineStride);
        if (region == null) {
            return;
        }
        for (int row = 0; row < region.height; row++) {
            int sourceIndex = region.offset + row * scanlineStride;
            int destinationIndex = (region.y + row) * this.width + region.x;
            for (int column = 0; column < region.width; column++) {
                pixels[destinationIndex + column] = model.getRGB(source[sourceIndex + column] & 0xff);
            }
        }
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] source, int offset, int scanlineStride) {
        validateChunk(width, height);
        PixelRegion region = clip(x, y, width, height, offset, scanlineStride);
        if (region == null) {
            return;
        }
        for (int row = 0; row < region.height; row++) {
            int sourceIndex = region.offset + row * scanlineStride;
            int destinationIndex = (region.y + row) * this.width + region.x;
            for (int column = 0; column < region.width; column++) {
                pixels[destinationIndex + column] = model.getRGB(source[sourceIndex + column]);
            }
        }
    }

    @Override
    public void imageComplete(int status) {
        if (status == IMAGEERROR || status == IMAGEABORTED) {
            reset();
        } else if ((status == SINGLEFRAMEDONE || status == STATICIMAGEDONE) && width > 0 && height > 0 && pixels != null) {
            BufferedImage source = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            source.setRGB(0, 0, width, height, pixels, 0, width);
            BufferedImage result = bufferedImageOp.filter(source, null);
            int resultWidth = result.getWidth();
            int resultHeight = result.getHeight();
            int[] resultPixels = result.getRGB(0, 0, resultWidth, resultHeight, null, 0, resultWidth);
            consumer.setDimensions(resultWidth, resultHeight);
            consumer.setColorModel(ColorModel.getRGBdefault());
            consumer.setPixels(0, 0, resultWidth, resultHeight, ColorModel.getRGBdefault(), resultPixels, 0, resultWidth);
        }
        consumer.imageComplete(status);
    }

    private PixelRegion clip(int x, int y, int width, int height, int offset, int scanlineStride) {
        if (width == 0 || height == 0 || pixels == null) {
            return null;
        }
        if (y < 0) {
            int difference = -y;
            if (difference >= height) {
                return null;
            }
            offset += scanlineStride * difference;
            y = 0;
            height -= difference;
        }
        if (y + height > this.height) {
            height = this.height - y;
        }
        if (height <= 0) {
            return null;
        }
        if (x < 0) {
            int difference = -x;
            if (difference >= width) {
                return null;
            }
            offset += difference;
            x = 0;
            width -= difference;
        }
        if (x + width > this.width) {
            width = this.width - x;
        }
        return width <= 0 ? null : new PixelRegion(x, y, width, height, offset);
    }

    private void reset() {
        width = -1;
        height = -1;
        pixels = null;
    }

    private static void validateChunk(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width (" + width + ") and height (" + height + ") must be > 0");
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
