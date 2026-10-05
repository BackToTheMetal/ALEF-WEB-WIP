package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;

public class Raster {
    protected SampleModel sampleModel;
    protected DataBuffer dataBuffer;
    protected int minX;
    protected int minY;
    protected int width;
    protected int height;
    protected int sampleModelTranslateX;
    protected int sampleModelTranslateY;
    protected int numBands;
    protected int numDataElements;
    protected Raster parent;

    public static WritableRaster createInterleavedRaster(int dataType, int width, int height, int bands, Point location) {
        int[] bandOffsets = new int[bands];
        for (int band = 0; band < bands; band++) {
            bandOffsets[band] = band;
        }
        return createInterleavedRaster(dataType, width, height, width * bands, bands, bandOffsets, location);
    }

    public static WritableRaster createInterleavedRaster(int dataType, int width, int height, int scanlineStride, int pixelStride, int[] bandOffsets, Point location) {
        requireInterleavedType(dataType);
        PixelInterleavedSampleModel model = new PixelInterleavedSampleModel(dataType, width, height, pixelStride, scanlineStride, bandOffsets);
        return createWritableRaster(model, model.createDataBuffer(), location);
    }

    public static WritableRaster createBandedRaster(int dataType, int width, int height, int bands, Point location) {
        int[] bankIndices = new int[bands];
        int[] bandOffsets = new int[bands];
        for (int band = 0; band < bands; band++) {
            bankIndices[band] = band;
        }
        return createBandedRaster(dataType, width, height, width, bankIndices, bandOffsets, location);
    }

    public static WritableRaster createBandedRaster(int dataType, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets, Point location) {
        requireBandedType(dataType);
        BandedSampleModel model = new BandedSampleModel(dataType, width, height, scanlineStride, bankIndices, bandOffsets);
        return createWritableRaster(model, model.createDataBuffer(), location);
    }

    public static WritableRaster createPackedRaster(int dataType, int width, int height, int[] bandMasks, Point location) {
        SinglePixelPackedSampleModel model = new SinglePixelPackedSampleModel(dataType, width, height, bandMasks);
        return createWritableRaster(model, model.createDataBuffer(), location);
    }

    public static WritableRaster createPackedRaster(int dataType, int width, int height, int bands, int bitsPerBand, Point location) {
        if (bands == 1) {
            MultiPixelPackedSampleModel model = new MultiPixelPackedSampleModel(dataType, width, height, bitsPerBand);
            return createWritableRaster(model, model.createDataBuffer(), location);
        }
        int elementBits = DataBuffer.getDataTypeSize(dataType);
        if (bands <= 0 || bitsPerBand <= 0 || (long) bands * bitsPerBand > elementBits) {
            throw new IllegalArgumentException("Packed bands exceed the data element size");
        }
        int[] masks = new int[bands];
        int baseMask = bitsPerBand == 32 ? -1 : (1 << bitsPerBand) - 1;
        for (int band = 0; band < bands; band++) {
            masks[band] = baseMask << ((bands - band - 1) * bitsPerBand);
        }
        SinglePixelPackedSampleModel model = new SinglePixelPackedSampleModel(dataType, width, height, masks);
        return createWritableRaster(model, model.createDataBuffer(), location);
    }

    public static WritableRaster createInterleavedRaster(DataBuffer buffer, int width, int height, int scanlineStride, int pixelStride, int[] bandOffsets, Point location) {
        requireInterleavedType(buffer.getDataType());
        PixelInterleavedSampleModel model = new PixelInterleavedSampleModel(buffer.getDataType(), width, height, pixelStride, scanlineStride, bandOffsets);
        return createWritableRaster(model, buffer, location);
    }

    public static WritableRaster createBandedRaster(DataBuffer buffer, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets, Point location) {
        requireBandedType(buffer.getDataType());
        BandedSampleModel model = new BandedSampleModel(buffer.getDataType(), width, height, scanlineStride, bankIndices, bandOffsets);
        return createWritableRaster(model, buffer, location);
    }

    public static WritableRaster createPackedRaster(DataBuffer buffer, int width, int height, int scanlineStride, int[] bandMasks, Point location) {
        SinglePixelPackedSampleModel model = new SinglePixelPackedSampleModel(buffer.getDataType(), width, height, scanlineStride, bandMasks);
        return createWritableRaster(model, buffer, location);
    }

    public static WritableRaster createPackedRaster(DataBuffer buffer, int width, int height, int bitsPerPixel, Point location) {
        int elementBits = DataBuffer.getDataTypeSize(buffer.getDataType());
        int stride = (width * bitsPerPixel + elementBits - 1) / elementBits;
        MultiPixelPackedSampleModel model = new MultiPixelPackedSampleModel(buffer.getDataType(), width, height, bitsPerPixel, stride, 0);
        return createWritableRaster(model, buffer, location);
    }

    public static Raster createRaster(SampleModel model, DataBuffer buffer, Point location) {
        return new Raster(model, buffer, locationOrOrigin(location));
    }

    public static WritableRaster createWritableRaster(SampleModel model, Point location) {
        return new WritableRaster(model, locationOrOrigin(location));
    }

    public static WritableRaster createWritableRaster(SampleModel model, DataBuffer buffer, Point location) {
        return new WritableRaster(model, buffer, locationOrOrigin(location));
    }

    protected Raster(SampleModel model, Point origin) {
        this(model, model.createDataBuffer(), origin);
    }

    protected Raster(SampleModel model, DataBuffer buffer, Point origin) {
        this(model, buffer, new Rectangle(origin.x, origin.y, model.getWidth(), model.getHeight()), origin, null);
    }

    protected Raster(SampleModel model, DataBuffer buffer, Rectangle region, Point sampleModelTranslate, Raster parent) {
        if (model == null || buffer == null || region == null || sampleModelTranslate == null) {
            throw new NullPointerException("Raster arguments must not be null");
        }
        if (region.width <= 0 || region.height <= 0) {
            throw new RasterFormatException("Raster dimensions must be positive");
        }
        long modelX = (long) region.x - sampleModelTranslate.x;
        long modelY = (long) region.y - sampleModelTranslate.y;
        if (modelX < 0L || modelY < 0L || modelX + region.width > model.getWidth() || modelY + region.height > model.getHeight()) {
            throw new RasterFormatException("Raster region lies outside the sample model");
        }
        sampleModel = model;
        dataBuffer = buffer;
        minX = region.x;
        minY = region.y;
        width = region.width;
        height = region.height;
        sampleModelTranslateX = sampleModelTranslate.x;
        sampleModelTranslateY = sampleModelTranslate.y;
        numBands = model.getNumBands();
        numDataElements = model.getNumDataElements();
        this.parent = parent;
    }

    public Raster getParent() {
        return parent;
    }

    public final int getSampleModelTranslateX() {
        return sampleModelTranslateX;
    }

    public final int getSampleModelTranslateY() {
        return sampleModelTranslateY;
    }

    public WritableRaster createCompatibleWritableRaster() {
        return createWritableRaster(sampleModel, new Point(0, 0));
    }

    public WritableRaster createCompatibleWritableRaster(int width, int height) {
        return createCompatibleWritableRaster(0, 0, width, height);
    }

    public WritableRaster createCompatibleWritableRaster(Rectangle rectangle) {
        return createCompatibleWritableRaster(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public WritableRaster createCompatibleWritableRaster(int x, int y, int width, int height) {
        SampleModel compatible = sampleModel.createCompatibleSampleModel(width, height);
        return createWritableRaster(compatible, new Point(x, y));
    }

    public Raster createTranslatedChild(int childMinX, int childMinY) {
        return createChild(minX, minY, width, height, childMinX, childMinY, null);
    }

    public Raster createChild(int parentX, int parentY, int width, int height, int childMinX, int childMinY, int[] bands) {
        checkRegion(parentX, parentY, width, height);
        SampleModel childModel = bands == null ? sampleModel : sampleModel.createSubsetSampleModel(bands);
        int deltaX = childMinX - parentX;
        int deltaY = childMinY - parentY;
        Point translate = new Point(sampleModelTranslateX + deltaX, sampleModelTranslateY + deltaY);
        return new Raster(childModel, dataBuffer, new Rectangle(childMinX, childMinY, width, height), translate, this);
    }

    public Rectangle getBounds() {
        return new Rectangle(minX, minY, width, height);
    }

    public final int getMinX() {
        return minX;
    }

    public final int getMinY() {
        return minY;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final int getNumBands() {
        return numBands;
    }

    public final int getNumDataElements() {
        return numDataElements;
    }

    public final int getTransferType() {
        return sampleModel.getTransferType();
    }

    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }

    public SampleModel getSampleModel() {
        return sampleModel;
    }

    public Object getDataElements(int x, int y, Object output) {
        checkPoint(x, y);
        return sampleModel.getDataElements(modelX(x), modelY(y), output, dataBuffer);
    }

    public Object getDataElements(int x, int y, int width, int height, Object output) {
        checkRegion(x, y, width, height);
        return sampleModel.getDataElements(modelX(x), modelY(y), width, height, output, dataBuffer);
    }

    public int[] getPixel(int x, int y, int[] output) {
        checkPoint(x, y);
        return sampleModel.getPixel(modelX(x), modelY(y), output, dataBuffer);
    }

    public float[] getPixel(int x, int y, float[] output) {
        checkPoint(x, y);
        return sampleModel.getPixel(modelX(x), modelY(y), output, dataBuffer);
    }

    public double[] getPixel(int x, int y, double[] output) {
        checkPoint(x, y);
        return sampleModel.getPixel(modelX(x), modelY(y), output, dataBuffer);
    }

    public int[] getPixels(int x, int y, int width, int height, int[] output) {
        checkRegion(x, y, width, height);
        return sampleModel.getPixels(modelX(x), modelY(y), width, height, output, dataBuffer);
    }

    public float[] getPixels(int x, int y, int width, int height, float[] output) {
        checkRegion(x, y, width, height);
        return sampleModel.getPixels(modelX(x), modelY(y), width, height, output, dataBuffer);
    }

    public double[] getPixels(int x, int y, int width, int height, double[] output) {
        checkRegion(x, y, width, height);
        return sampleModel.getPixels(modelX(x), modelY(y), width, height, output, dataBuffer);
    }

    public int getSample(int x, int y, int band) {
        checkPoint(x, y);
        return sampleModel.getSample(modelX(x), modelY(y), band, dataBuffer);
    }

    public float getSampleFloat(int x, int y, int band) {
        checkPoint(x, y);
        return sampleModel.getSampleFloat(modelX(x), modelY(y), band, dataBuffer);
    }

    public double getSampleDouble(int x, int y, int band) {
        checkPoint(x, y);
        return sampleModel.getSampleDouble(modelX(x), modelY(y), band, dataBuffer);
    }

    public int[] getSamples(int x, int y, int width, int height, int band, int[] output) {
        checkRegion(x, y, width, height);
        return sampleModel.getSamples(modelX(x), modelY(y), width, height, band, output, dataBuffer);
    }

    public float[] getSamples(int x, int y, int width, int height, int band, float[] output) {
        checkRegion(x, y, width, height);
        return sampleModel.getSamples(modelX(x), modelY(y), width, height, band, output, dataBuffer);
    }

    public double[] getSamples(int x, int y, int width, int height, int band, double[] output) {
        checkRegion(x, y, width, height);
        return sampleModel.getSamples(modelX(x), modelY(y), width, height, band, output, dataBuffer);
    }

    final void checkPoint(int x, int y) {
        if (x < minX || y < minY || x >= (long) minX + width || y >= (long) minY + height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds");
        }
    }

    final void checkRegion(int x, int y, int width, int height) {
        if (width < 0 || height < 0 || x < minX || y < minY || (long) x + width > (long) minX + this.width || (long) y + height > (long) minY + this.height) {
            throw new ArrayIndexOutOfBoundsException("Region out of bounds");
        }
    }

    final int modelX(int x) {
        return x - sampleModelTranslateX;
    }

    final int modelY(int y) {
        return y - sampleModelTranslateY;
    }

    private static Point locationOrOrigin(Point location) {
        return location == null ? new Point(0, 0) : location;
    }

    private static void requireInterleavedType(int dataType) {
        if (dataType != DataBuffer.TYPE_BYTE && dataType != DataBuffer.TYPE_USHORT) {
            throw new IllegalArgumentException("Interleaved rasters support byte and unsigned short data");
        }
    }

    private static void requireBandedType(int dataType) {
        if (dataType != DataBuffer.TYPE_BYTE && dataType != DataBuffer.TYPE_USHORT && dataType != DataBuffer.TYPE_INT) {
            throw new IllegalArgumentException("Banded rasters support byte, unsigned short, and int data");
        }
    }
}
