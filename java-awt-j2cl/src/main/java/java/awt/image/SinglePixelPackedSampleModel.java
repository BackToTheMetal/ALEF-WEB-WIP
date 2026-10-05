package java.awt.image;

import java.util.Arrays;

public class SinglePixelPackedSampleModel extends SampleModel {
    private final int[] bitMasks;
    private final int[] bitOffsets;
    private final int[] bitSizes;
    private final int scanlineStride;

    public SinglePixelPackedSampleModel(int dataType, int width, int height, int[] bitMasks) {
        this(dataType, width, height, width, bitMasks);
    }

    public SinglePixelPackedSampleModel(int dataType, int width, int height, int scanlineStride, int[] bitMasks) {
        super(dataType, width, height, bitMasks.length);
        if (dataType != DataBuffer.TYPE_BYTE && dataType != DataBuffer.TYPE_USHORT && dataType != DataBuffer.TYPE_INT) {
            throw new IllegalArgumentException("Data type must be TYPE_BYTE, TYPE_USHORT, or TYPE_INT");
        }
        int elementBits = DataBuffer.getDataTypeSize(dataType);
        this.bitMasks = Arrays.copyOf(bitMasks, bitMasks.length);
        bitOffsets = new int[bitMasks.length];
        bitSizes = new int[bitMasks.length];
        for (int band = 0; band < bitMasks.length; band++) {
            int mask = bitMasks[band];
            if (dataType != DataBuffer.TYPE_INT && (mask >>> elementBits) != 0) {
                throw new IllegalArgumentException("Mask exceeds data type size");
            }
            bitOffsets[band] = trailingZeros(mask);
            bitSizes[band] = contiguousBitCount(mask >>> bitOffsets[band]);
            int expected = bitSizes[band] == 32 ? -1 : (1 << bitSizes[band]) - 1;
            if ((mask >>> bitOffsets[band]) != expected) {
                throw new IllegalArgumentException("Mask must be contiguous");
            }
        }
        this.scanlineStride = scanlineStride;
    }

    @Override
    public int getNumDataElements() {
        return 1;
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        return new SinglePixelPackedSampleModel(dataType, width, height, bitMasks);
    }

    @Override
    public DataBuffer createDataBuffer() {
        long size = (long) scanlineStride * (height - 1) + width;
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Required buffer size exceeds Integer.MAX_VALUE");
        }
        return SampleModelSupport.createBuffer(dataType, (int) size, 1);
    }

    @Override
    public int[] getSampleSize() {
        return Arrays.copyOf(bitSizes, bitSizes.length);
    }

    @Override
    public int getSampleSize(int band) {
        return bitSizes[band];
    }

    public int getOffset(int x, int y) {
        return y * scanlineStride + x;
    }

    public int[] getBitOffsets() {
        return Arrays.copyOf(bitOffsets, bitOffsets.length);
    }

    public int[] getBitMasks() {
        return Arrays.copyOf(bitMasks, bitMasks.length);
    }

    public int getScanlineStride() {
        return scanlineStride;
    }

    @Override
    public SampleModel createSubsetSampleModel(int[] bands) {
        int[] masks = new int[bands.length];
        for (int index = 0; index < bands.length; index++) {
            masks[index] = bitMasks[bands[index]];
        }
        return new SinglePixelPackedSampleModel(dataType, width, height, scanlineStride, masks);
    }

    @Override
    public Object getDataElements(int x, int y, Object output, DataBuffer buffer) {
        checkPixel(x, y);
        Object values = output == null ? SampleModelSupport.createArray(dataType, 1) : output;
        SampleModelSupport.set(values, dataType, 0, buffer.getElem(getOffset(x, y)));
        return values;
    }

    @Override
    public int[] getPixel(int x, int y, int[] output, DataBuffer buffer) {
        return super.getPixel(x, y, output, buffer);
    }

    @Override
    public int[] getPixels(int x, int y, int width, int height, int[] output, DataBuffer buffer) {
        return super.getPixels(x, y, width, height, output, buffer);
    }

    @Override
    public int getSample(int x, int y, int band, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        return (buffer.getElem(getOffset(x, y)) & bitMasks[band]) >>> bitOffsets[band];
    }

    @Override
    public int[] getSamples(int x, int y, int width, int height, int band, int[] output, DataBuffer buffer) {
        return super.getSamples(x, y, width, height, band, output, buffer);
    }

    @Override
    public void setDataElements(int x, int y, Object values, DataBuffer buffer) {
        checkPixel(x, y);
        buffer.setElem(getOffset(x, y), (int) SampleModelSupport.get(values, dataType, 0));
    }

    @Override
    public void setPixel(int x, int y, int[] values, DataBuffer buffer) {
        super.setPixel(x, y, values, buffer);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, int[] values, DataBuffer buffer) {
        super.setPixels(x, y, width, height, values, buffer);
    }

    @Override
    public void setSample(int x, int y, int band, int value, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        int offset = getOffset(x, y);
        int element = buffer.getElem(offset);
        element = element & ~bitMasks[band] | value << bitOffsets[band] & bitMasks[band];
        buffer.setElem(offset, element);
    }

    @Override
    public void setSamples(int x, int y, int width, int height, int band, int[] values, DataBuffer buffer) {
        super.setSamples(x, y, width, height, band, values, buffer);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        SinglePixelPackedSampleModel model = (SinglePixelPackedSampleModel) object;
        return dataType == model.dataType && width == model.width && height == model.height && scanlineStride == model.scanlineStride && Arrays.equals(bitMasks, model.bitMasks);
    }

    @Override
    public int hashCode() {
        int result = dataType;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + scanlineStride;
        return 31 * result + Arrays.hashCode(bitMasks);
    }

    private static int trailingZeros(int value) {
        if (value == 0) {
            return 0;
        }
        int count = 0;
        int remaining = value;
        while ((remaining & 1) == 0) {
            remaining >>>= 1;
            count++;
        }
        return count;
    }

    private static int contiguousBitCount(int value) {
        int count = 0;
        int remaining = value;
        while ((remaining & 1) != 0) {
            remaining >>>= 1;
            count++;
        }
        return count;
    }
}
