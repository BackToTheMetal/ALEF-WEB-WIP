package java.awt.image;

public class MultiPixelPackedSampleModel extends SampleModel {
    private final int pixelBitStride;
    private final int bitMask;
    private final int dataElementSize;
    private final int dataBitOffset;
    private final int scanlineStride;

    public MultiPixelPackedSampleModel(int dataType, int width, int height, int numberOfBits) {
        this(dataType, width, height, numberOfBits, defaultScanlineStride(dataType, width, numberOfBits), 0);
    }

    public MultiPixelPackedSampleModel(int dataType, int width, int height, int numberOfBits, int scanlineStride, int dataBitOffset) {
        super(dataType, width, height, 1);
        if (dataType != DataBuffer.TYPE_BYTE && dataType != DataBuffer.TYPE_USHORT && dataType != DataBuffer.TYPE_INT) {
            throw new IllegalArgumentException("Data type must be TYPE_BYTE, TYPE_USHORT, or TYPE_INT");
        }
        dataElementSize = DataBuffer.getDataTypeSize(dataType);
        if (numberOfBits <= 0 || numberOfBits > dataElementSize || (numberOfBits & (numberOfBits - 1)) != 0 || dataElementSize % numberOfBits != 0) {
            throw new IllegalArgumentException("MultiPixelPackedSampleModel does not allow pixels to span data element boundaries");
        }
        if (dataBitOffset < 0) {
            throw new IllegalArgumentException("Data bit offset must be non-negative");
        }
        pixelBitStride = numberOfBits;
        bitMask = numberOfBits == 32 ? -1 : (1 << numberOfBits) - 1;
        this.scanlineStride = scanlineStride;
        this.dataBitOffset = dataBitOffset;
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        return new MultiPixelPackedSampleModel(dataType, width, height, pixelBitStride);
    }

    @Override
    public DataBuffer createDataBuffer() {
        long lastBit = (long) dataBitOffset + (long) (height - 1) * scanlineStride * dataElementSize + (long) width * pixelBitStride;
        int size = (int) ((lastBit + dataElementSize - 1L) / dataElementSize);
        return SampleModelSupport.createBuffer(dataType, size, 1);
    }

    @Override
    public int getNumDataElements() {
        return 1;
    }

    @Override
    public int[] getSampleSize() {
        return new int[] {pixelBitStride};
    }

    @Override
    public int getSampleSize(int band) {
        return pixelBitStride;
    }

    public int getOffset(int x, int y) {
        return y * scanlineStride + (x * pixelBitStride + dataBitOffset) / dataElementSize;
    }

    public int getBitOffset(int x) {
        return (x * pixelBitStride + dataBitOffset) % dataElementSize;
    }

    public int getScanlineStride() {
        return scanlineStride;
    }

    public int getPixelBitStride() {
        return pixelBitStride;
    }

    public int getDataBitOffset() {
        return dataBitOffset;
    }

    @Override
    public int getTransferType() {
        return dataType;
    }

    @Override
    public SampleModel createSubsetSampleModel(int[] bands) {
        if (bands.length != 1 || bands[0] != 0) {
            throw new IllegalArgumentException("MultiPixelPackedSampleModel has only one band");
        }
        return createCompatibleSampleModel(width, height);
    }

    @Override
    public int getSample(int x, int y, int band, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        int bitOffset = getBitOffset(x);
        int shift = dataElementSize - bitOffset - pixelBitStride;
        return buffer.getElem(getOffset(x, y)) >>> shift & bitMask;
    }

    @Override
    public void setSample(int x, int y, int band, int value, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        int offset = getOffset(x, y);
        int shift = dataElementSize - getBitOffset(x) - pixelBitStride;
        int element = buffer.getElem(offset);
        element = element & ~(bitMask << shift) | (value & bitMask) << shift;
        buffer.setElem(offset, element);
    }

    @Override
    public Object getDataElements(int x, int y, Object output, DataBuffer buffer) {
        Object values = output == null ? SampleModelSupport.createArray(dataType, 1) : output;
        SampleModelSupport.set(values, dataType, 0, getSample(x, y, 0, buffer));
        return values;
    }

    @Override
    public int[] getPixel(int x, int y, int[] output, DataBuffer buffer) {
        int[] values = output == null ? new int[1] : output;
        values[0] = getSample(x, y, 0, buffer);
        return values;
    }

    @Override
    public void setDataElements(int x, int y, Object values, DataBuffer buffer) {
        setSample(x, y, 0, (int) SampleModelSupport.get(values, dataType, 0), buffer);
    }

    @Override
    public void setPixel(int x, int y, int[] values, DataBuffer buffer) {
        setSample(x, y, 0, values[0], buffer);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MultiPixelPackedSampleModel)) {
            return false;
        }
        MultiPixelPackedSampleModel model = (MultiPixelPackedSampleModel) object;
        return dataType == model.dataType && width == model.width && height == model.height && pixelBitStride == model.pixelBitStride && scanlineStride == model.scanlineStride && dataBitOffset == model.dataBitOffset;
    }

    @Override
    public int hashCode() {
        int result = dataType;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + pixelBitStride;
        result = 31 * result + scanlineStride;
        return 31 * result + dataBitOffset;
    }

    private static int defaultScanlineStride(int dataType, int width, int numberOfBits) {
        int elementSize = DataBuffer.getDataTypeSize(dataType);
        return (width * numberOfBits + elementSize - 1) / elementSize;
    }
}
