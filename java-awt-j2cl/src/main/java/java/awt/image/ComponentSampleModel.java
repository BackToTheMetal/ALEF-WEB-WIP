package java.awt.image;

import java.util.Arrays;

public class ComponentSampleModel extends SampleModel {
    protected int[] bandOffsets;
    protected int[] bankIndices;
    protected int numBands;
    protected int numBanks;
    protected int scanlineStride;
    protected int pixelStride;

    public ComponentSampleModel(int dataType, int width, int height, int pixelStride, int scanlineStride, int[] bandOffsets) {
        this(dataType, width, height, pixelStride, scanlineStride, new int[bandOffsets.length], bandOffsets);
    }

    public ComponentSampleModel(int dataType, int width, int height, int pixelStride, int scanlineStride, int[] bankIndices, int[] bandOffsets) {
        super(dataType, width, height, bandOffsets.length);
        if (bankIndices.length != bandOffsets.length) {
            throw new IllegalArgumentException("bankIndices.length must equal bandOffsets.length");
        }
        if (pixelStride < 0 || scanlineStride < 0) {
            throw new IllegalArgumentException("Pixel and scanline strides must be non-negative");
        }
        this.pixelStride = pixelStride;
        this.scanlineStride = scanlineStride;
        this.bankIndices = Arrays.copyOf(bankIndices, bankIndices.length);
        this.bandOffsets = Arrays.copyOf(bandOffsets, bandOffsets.length);
        numBands = bandOffsets.length;
        int maximumBank = 0;
        for (int bank : bankIndices) {
            if (bank < 0) {
                throw new IllegalArgumentException("Bank index must be non-negative");
            }
            maximumBank = Math.max(maximumBank, bank);
        }
        numBanks = maximumBank + 1;
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        int[] compatibleOffsets = normalizedBandOffsets();
        return new ComponentSampleModel(dataType, width, height, pixelStride, pixelStride * width, bankIndices, compatibleOffsets);
    }

    @Override
    public SampleModel createSubsetSampleModel(int[] bands) {
        if (bands.length > numBands) {
            throw new IllegalArgumentException("There are only " + numBands + " bands");
        }
        int[] subsetBanks = new int[bands.length];
        int[] subsetOffsets = new int[bands.length];
        for (int index = 0; index < bands.length; index++) {
            subsetBanks[index] = bankIndices[bands[index]];
            subsetOffsets[index] = bandOffsets[bands[index]];
        }
        return new ComponentSampleModel(dataType, width, height, pixelStride, scanlineStride, subsetBanks, subsetOffsets);
    }

    @Override
    public DataBuffer createDataBuffer() {
        return SampleModelSupport.createBuffer(dataType, bufferSize(), numBanks);
    }

    public int getOffset(int x, int y) {
        return y * scanlineStride + x * pixelStride + bandOffsets[0];
    }

    public int getOffset(int x, int y, int band) {
        return y * scanlineStride + x * pixelStride + bandOffsets[band];
    }

    @Override
    public final int[] getSampleSize() {
        int[] sizes = new int[numBands];
        Arrays.fill(sizes, DataBuffer.getDataTypeSize(dataType));
        return sizes;
    }

    @Override
    public final int getSampleSize(int band) {
        return DataBuffer.getDataTypeSize(dataType);
    }

    public final int[] getBankIndices() {
        return Arrays.copyOf(bankIndices, bankIndices.length);
    }

    public final int[] getBandOffsets() {
        return Arrays.copyOf(bandOffsets, bandOffsets.length);
    }

    public final int getScanlineStride() {
        return scanlineStride;
    }

    public final int getPixelStride() {
        return pixelStride;
    }

    @Override
    public final int getNumDataElements() {
        return numBands;
    }

    @Override
    public Object getDataElements(int x, int y, Object output, DataBuffer buffer) {
        checkPixel(x, y);
        Object values = output == null ? SampleModelSupport.createArray(dataType, numBands) : output;
        int pixelOffset = y * scanlineStride + x * pixelStride;
        for (int band = 0; band < numBands; band++) {
            int index = pixelOffset + bandOffsets[band];
            double value = dataType == DataBuffer.TYPE_FLOAT ? buffer.getElemFloat(bankIndices[band], index) : dataType == DataBuffer.TYPE_DOUBLE ? buffer.getElemDouble(bankIndices[band], index) : buffer.getElem(bankIndices[band], index);
            SampleModelSupport.set(values, dataType, band, value);
        }
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
        return buffer.getElem(bankIndices[band], getOffset(x, y, band));
    }

    @Override
    public float getSampleFloat(int x, int y, int band, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        return buffer.getElemFloat(bankIndices[band], getOffset(x, y, band));
    }

    @Override
    public double getSampleDouble(int x, int y, int band, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        return buffer.getElemDouble(bankIndices[band], getOffset(x, y, band));
    }

    @Override
    public int[] getSamples(int x, int y, int width, int height, int band, int[] output, DataBuffer buffer) {
        return super.getSamples(x, y, width, height, band, output, buffer);
    }

    @Override
    public void setDataElements(int x, int y, Object values, DataBuffer buffer) {
        checkPixel(x, y);
        int pixelOffset = y * scanlineStride + x * pixelStride;
        for (int band = 0; band < numBands; band++) {
            int index = pixelOffset + bandOffsets[band];
            double value = SampleModelSupport.get(values, dataType, band);
            if (dataType == DataBuffer.TYPE_FLOAT) {
                buffer.setElemFloat(bankIndices[band], index, (float) value);
            } else if (dataType == DataBuffer.TYPE_DOUBLE) {
                buffer.setElemDouble(bankIndices[band], index, value);
            } else {
                buffer.setElem(bankIndices[band], index, (int) value);
            }
        }
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
        buffer.setElem(bankIndices[band], getOffset(x, y, band), value);
    }

    @Override
    public void setSample(int x, int y, int band, float value, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        buffer.setElemFloat(bankIndices[band], getOffset(x, y, band), value);
    }

    @Override
    public void setSample(int x, int y, int band, double value, DataBuffer buffer) {
        checkPixel(x, y);
        checkBand(band);
        buffer.setElemDouble(bankIndices[band], getOffset(x, y, band), value);
    }

    @Override
    public void setSamples(int x, int y, int width, int height, int band, int[] values, DataBuffer buffer) {
        super.setSamples(x, y, width, height, band, values, buffer);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComponentSampleModel)) {
            return false;
        }
        ComponentSampleModel model = (ComponentSampleModel) object;
        return getClass() == model.getClass() && dataType == model.dataType && width == model.width && height == model.height && numBands == model.numBands && pixelStride == model.pixelStride && scanlineStride == model.scanlineStride && Arrays.equals(bankIndices, model.bankIndices) && Arrays.equals(bandOffsets, model.bandOffsets);
    }

    @Override
    public int hashCode() {
        int result = dataType;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + pixelStride;
        result = 31 * result + scanlineStride;
        result = 31 * result + Arrays.hashCode(bankIndices);
        return 31 * result + Arrays.hashCode(bandOffsets);
    }

    private int bufferSize() {
        int maximumOffset = 0;
        for (int bandOffset : bandOffsets) {
            maximumOffset = Math.max(maximumOffset, bandOffset);
        }
        long required = (long) maximumOffset + (long) (width - 1) * pixelStride + (long) (height - 1) * scanlineStride + 1L;
        if (required > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Required buffer size exceeds Integer.MAX_VALUE");
        }
        return (int) required;
    }

    private int[] normalizedBandOffsets() {
        int minimum = bandOffsets[0];
        for (int bandOffset : bandOffsets) {
            minimum = Math.min(minimum, bandOffset);
        }
        int[] normalized = new int[numBands];
        for (int band = 0; band < numBands; band++) {
            normalized[band] = bandOffsets[band] - minimum;
        }
        return normalized;
    }
}
