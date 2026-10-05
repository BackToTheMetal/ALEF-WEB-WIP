package java.awt.image;

public class CropImageFilter extends ImageFilter {
    private final int cropHeight;
    private final int cropWidth;
    private final int cropX;
    private final int cropY;

    public CropImageFilter(int x, int y, int width, int height) {
        cropX = x;
        cropY = y;
        cropWidth = width;
        cropHeight = height;
    }

    @Override
    public ImageFilter getFilterInstance(ImageConsumer consumer) {
        CropImageFilter filter = new CropImageFilter(cropX, cropY, cropWidth, cropHeight);
        filter.consumer = consumer;
        return filter;
    }

    @Override
    public void setDimensions(int width, int height) {
        consumer.setDimensions(cropWidth, cropHeight);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        Intersection intersection = intersection(x, y, width, height);
        if (intersection != null) {
            consumer.setPixels(intersection.x - cropX, intersection.y - cropY, intersection.width, intersection.height, model, pixels, offset + (intersection.y - y) * scanlineStride + intersection.x - x, scanlineStride);
        }
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        Intersection intersection = intersection(x, y, width, height);
        if (intersection != null) {
            consumer.setPixels(intersection.x - cropX, intersection.y - cropY, intersection.width, intersection.height, model, pixels, offset + (intersection.y - y) * scanlineStride + intersection.x - x, scanlineStride);
        }
    }

    private Intersection intersection(int x, int y, int width, int height) {
        int left = Math.max(x, cropX);
        int top = Math.max(y, cropY);
        int right = Math.min(addWithoutOverflow(x, width), addWithoutOverflow(cropX, cropWidth));
        int bottom = Math.min(addWithoutOverflow(y, height), addWithoutOverflow(cropY, cropHeight));
        return left >= right || top >= bottom ? null : new Intersection(left, top, right - left, bottom - top);
    }

    private static int addWithoutOverflow(int value, int increment) {
        long sum = (long) value + increment;
        return sum > Integer.MAX_VALUE ? Integer.MAX_VALUE : sum < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) sum;
    }

    private static final class Intersection {
        private final int height;
        private final int width;
        private final int x;
        private final int y;

        private Intersection(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
