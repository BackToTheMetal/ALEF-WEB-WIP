package java.awt.image;

public class PixelInterleavedSampleModel extends ComponentSampleModel {
    public PixelInterleavedSampleModel(int dataType, int width, int height, int pixelStride, int scanlineStride, int[] bandOffsets) {
        super(dataType, width, height, pixelStride, scanlineStride, bandOffsets);
        int minimum = bandOffsets[0];
        int maximum = bandOffsets[0];
        for (int offset : bandOffsets) {
            minimum = Math.min(minimum, offset);
            maximum = Math.max(maximum, offset);
        }
        if (maximum - minimum > scanlineStride) {
            throw new IllegalArgumentException("Offsets between bands must be less than the scanline stride");
        }
        if (pixelStride * width > scanlineStride) {
            throw new IllegalArgumentException("Pixel stride times width must not exceed scanline stride");
        }
        if (pixelStride < maximum - minimum) {
            throw new IllegalArgumentException("Pixel stride must not be less than the offset between bands");
        }
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        int minimum = bandOffsets[0];
        for (int offset : bandOffsets) {
            minimum = Math.min(minimum, offset);
        }
        int[] offsets = new int[bandOffsets.length];
        for (int index = 0; index < offsets.length; index++) {
            offsets[index] = bandOffsets[index] - minimum;
        }
        return new PixelInterleavedSampleModel(dataType, width, height, pixelStride, pixelStride * width, offsets);
    }

    @Override
    public SampleModel createSubsetSampleModel(int[] bands) {
        int[] offsets = new int[bands.length];
        for (int index = 0; index < bands.length; index++) {
            offsets[index] = bandOffsets[bands[index]];
        }
        return new PixelInterleavedSampleModel(dataType, width, height, pixelStride, scanlineStride, offsets);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ 1;
    }
}
