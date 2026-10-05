package java.awt.image;

public abstract class RGBImageFilter extends ImageFilter {
    protected ColorModel origmodel;
    protected ColorModel newmodel;
    protected boolean canFilterIndexColorModel;

    protected RGBImageFilter() {
    }

    @Override
    public void setColorModel(ColorModel model) {
        if (model instanceof IndexColorModel && canFilterIndexColorModel) {
            IndexColorModel filtered = filterIndexColorModel((IndexColorModel) model);
            substituteColorModel(model, filtered);
            consumer.setColorModel(filtered);
            return;
        }
        origmodel = null;
        newmodel = null;
        consumer.setColorModel(ColorModel.getRGBdefault());
    }

    public void substituteColorModel(ColorModel oldModel, ColorModel replacement) {
        origmodel = oldModel;
        newmodel = replacement;
    }

    public IndexColorModel filterIndexColorModel(IndexColorModel model) {
        int size = model.getMapSize();
        int[] colors = new int[size];
        model.getRGBs(colors);
        for (int index = 0; index < size; index++) {
            colors[index] = filterRGB(-1, -1, colors[index]);
        }
        return new IndexColorModel(model.getPixelSize(), size, colors, 0, model.hasAlpha(), model.getTransparentPixel(), model.getTransferType());
    }

    public void filterRGBPixels(int x, int y, int width, int height, int[] pixels, int offset, int scanlineStride) {
        for (int row = 0; row < height; row++) {
            int rowOffset = offset + row * scanlineStride;
            for (int column = 0; column < width; column++) {
                pixels[rowOffset + column] = filterRGB(x + column, y + row, pixels[rowOffset + column]);
            }
        }
        consumer.setPixels(x, y, width, height, ColorModel.getRGBdefault(), pixels, offset, scanlineStride);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        if (model == origmodel && newmodel != null) {
            consumer.setPixels(x, y, width, height, newmodel, pixels, offset, scanlineStride);
            return;
        }
        int[] filtered = new int[width * height];
        for (int row = 0; row < height; row++) {
            int sourceOffset = offset + row * scanlineStride;
            int destinationOffset = row * width;
            for (int column = 0; column < width; column++) {
                filtered[destinationOffset + column] = model.getRGB(pixels[sourceOffset + column] & 0xff);
            }
        }
        filterRGBPixels(x, y, width, height, filtered, 0, width);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        if (model == origmodel && newmodel != null) {
            consumer.setPixels(x, y, width, height, newmodel, pixels, offset, scanlineStride);
            return;
        }
        int[] filtered = new int[width * height];
        for (int row = 0; row < height; row++) {
            int sourceOffset = offset + row * scanlineStride;
            int destinationOffset = row * width;
            for (int column = 0; column < width; column++) {
                filtered[destinationOffset + column] = model.getRGB(pixels[sourceOffset + column]);
            }
        }
        filterRGBPixels(x, y, width, height, filtered, 0, width);
    }

    public abstract int filterRGB(int x, int y, int rgb);

    @Override
    public Object clone() {
        return new DelegatingRGBImageFilter(this);
    }

    private static final class DelegatingRGBImageFilter extends RGBImageFilter {
        private final RGBImageFilter delegate;

        private DelegatingRGBImageFilter(RGBImageFilter delegate) {
            this.delegate = delegate;
            canFilterIndexColorModel = delegate.canFilterIndexColorModel;
            origmodel = delegate.origmodel;
            newmodel = delegate.newmodel;
        }

        @Override
        public int filterRGB(int x, int y, int rgb) {
            return delegate.filterRGB(x, y, rgb);
        }
    }
}
