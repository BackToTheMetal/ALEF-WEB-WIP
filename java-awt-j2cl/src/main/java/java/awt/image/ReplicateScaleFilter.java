package java.awt.image;

public class ReplicateScaleFilter extends ImageFilter {
    protected int destHeight;
    protected int destWidth;
    protected Object outpixbuf;
    protected int[] srccols;
    protected int srcHeight;
    protected int[] srcrows;
    protected int srcWidth;
    private final int requestedHeight;
    private final int requestedWidth;

    public ReplicateScaleFilter(int width, int height) {
        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("Width and height must be non-zero");
        }
        requestedWidth = width;
        requestedHeight = height;
        destWidth = width;
        destHeight = height;
    }

    @Override
    public ImageFilter getFilterInstance(ImageConsumer consumer) {
        ReplicateScaleFilter filter = new ReplicateScaleFilter(requestedWidth, requestedHeight);
        filter.consumer = consumer;
        return filter;
    }

    @Override
    public void setDimensions(int width, int height) {
        srcWidth = width;
        srcHeight = height;
        if (destWidth < 0) {
            if (destHeight < 0) {
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else {
                destWidth = srcWidth * destHeight / srcHeight;
            }
        } else if (destHeight < 0) {
            destHeight = srcHeight * destWidth / srcWidth;
        }
        consumer.setDimensions(destWidth, destHeight);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        ensureMaps();
        byte[] output = outpixbuf instanceof byte[] ? (byte[]) outpixbuf : new byte[destWidth];
        outpixbuf = output;
        int destinationX = firstDestinationCoordinate(x, destWidth, srcWidth);
        int destinationY = firstDestinationCoordinate(y, destHeight, srcHeight);
        for (int outputY = destinationY; outputY < destHeight && srcrows[outputY] < y + height; outputY++) {
            int sourceOffset = offset + scanlineStride * (srcrows[outputY] - y);
            int outputX = destinationX;
            while (outputX < destWidth && srccols[outputX] < x + width) {
                output[outputX] = pixels[sourceOffset + srccols[outputX] - x];
                outputX++;
            }
            if (outputX > destinationX) {
                consumer.setPixels(destinationX, outputY, outputX - destinationX, 1, model, output, destinationX, destWidth);
            }
        }
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        ensureMaps();
        int[] output = outpixbuf instanceof int[] ? (int[]) outpixbuf : new int[destWidth];
        outpixbuf = output;
        int destinationX = firstDestinationCoordinate(x, destWidth, srcWidth);
        int destinationY = firstDestinationCoordinate(y, destHeight, srcHeight);
        for (int outputY = destinationY; outputY < destHeight && srcrows[outputY] < y + height; outputY++) {
            int sourceOffset = offset + scanlineStride * (srcrows[outputY] - y);
            int outputX = destinationX;
            while (outputX < destWidth && srccols[outputX] < x + width) {
                output[outputX] = pixels[sourceOffset + srccols[outputX] - x];
                outputX++;
            }
            if (outputX > destinationX) {
                consumer.setPixels(destinationX, outputY, outputX - destinationX, 1, model, output, destinationX, destWidth);
            }
        }
    }

    private void ensureMaps() {
        if (srcrows != null && srccols != null) {
            return;
        }
        srcrows = new int[destHeight + 1];
        for (int y = 0; y <= destHeight; y++) {
            srcrows[y] = (2 * y * srcHeight + srcHeight) / (2 * destHeight);
        }
        srccols = new int[destWidth + 1];
        for (int x = 0; x <= destWidth; x++) {
            srccols[x] = (2 * x * srcWidth + srcWidth) / (2 * destWidth);
        }
    }

    private static int firstDestinationCoordinate(int sourceCoordinate, int destinationSize, int sourceSize) {
        return (2 * sourceCoordinate * destinationSize + sourceSize - 1) / (2 * sourceSize);
    }
}
