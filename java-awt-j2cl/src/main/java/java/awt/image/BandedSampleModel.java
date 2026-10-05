package java.awt.image;

public final class BandedSampleModel extends ComponentSampleModel {
    public BandedSampleModel(int dataType, int width, int height, int numBands) {
        super(dataType, width, height, 1, width, sequentialBanks(numBands), new int[numBands]);
    }

    public BandedSampleModel(int dataType, int width, int height, int scanlineStride, int[] bankIndices, int[] bandOffsets) {
        super(dataType, width, height, 1, scanlineStride, bankIndices, bandOffsets);
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        return new BandedSampleModel(dataType, width, height, width, bankIndices, normalizedOffsets());
    }

    @Override
    public SampleModel createSubsetSampleModel(int[] bands) {
        int[] subsetBanks = new int[bands.length];
        int[] subsetOffsets = new int[bands.length];
        for (int index = 0; index < bands.length; index++) {
            subsetBanks[index] = bankIndices[bands[index]];
            subsetOffsets[index] = bandOffsets[bands[index]];
        }
        return new BandedSampleModel(dataType, width, height, scanlineStride, subsetBanks, subsetOffsets);
    }

    @Override
    public DataBuffer createDataBuffer() {
        return super.createDataBuffer();
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ 2;
    }

    private int[] normalizedOffsets() {
        int minimum = bandOffsets[0];
        for (int value : bandOffsets) {
            minimum = Math.min(minimum, value);
        }
        int[] offsets = new int[bandOffsets.length];
        for (int index = 0; index < offsets.length; index++) {
            offsets[index] = bandOffsets[index] - minimum;
        }
        return offsets;
    }

    private static int[] sequentialBanks(int count) {
        int[] banks = new int[count];
        for (int index = 0; index < count; index++) {
            banks[index] = index;
        }
        return banks;
    }
}
