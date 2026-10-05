package java.awt.image;

import java.awt.color.ColorSpace;
import java.util.Arrays;

public abstract class PackedColorModel extends ColorModel {
    private final int[] masks;
    final int[] maskOffsets;
    final int[] maskBits;

    public PackedColorModel(ColorSpace colorSpace, int bits, int[] colorMasks, int alphaMask, boolean isAlphaPremultiplied, int transparency, int transferType) {
        super(bits, componentSizes(colorMasks, alphaMask), colorSpace, alphaMask != 0, isAlphaPremultiplied, transparency, transferType);
        masks = new int[colorMasks.length + (alphaMask == 0 ? 0 : 1)];
        System.arraycopy(colorMasks, 0, masks, 0, colorMasks.length);
        if (alphaMask != 0) {
            masks[masks.length - 1] = alphaMask;
        }
        maskOffsets = new int[masks.length];
        maskBits = new int[masks.length];
        validateMasks(bits);
    }

    public PackedColorModel(ColorSpace colorSpace, int bits, int redMask, int greenMask, int blueMask, int alphaMask, boolean isAlphaPremultiplied, int transparency, int transferType) {
        this(colorSpace, bits, new int[] {redMask, greenMask, blueMask}, alphaMask, isAlphaPremultiplied, transparency, transferType);
    }

    public final int getMask(int component) {
        return masks[component];
    }

    public final int[] getMasks() {
        return Arrays.copyOf(masks, masks.length);
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        return new SinglePixelPackedSampleModel(transferType, width, height, masks);
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel model) {
        if (!(model instanceof SinglePixelPackedSampleModel) || model.getTransferType() != transferType || model.getNumBands() != masks.length) {
            return false;
        }
        return Arrays.equals(masks, ((SinglePixelPackedSampleModel) model).getBitMasks());
    }

    @Override
    public WritableRaster getAlphaRaster(WritableRaster raster) {
        if (!hasAlpha()) {
            return null;
        }
        return raster.createWritableChild(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), raster.getMinX(), raster.getMinY(), new int[] {masks.length - 1});
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PackedColorModel && super.equals(object) && Arrays.equals(masks, ((PackedColorModel) object).masks);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Arrays.hashCode(masks);
    }

    private void validateMasks(int bits) {
        int occupied = 0;
        for (int index = 0; index < masks.length; index++) {
            int mask = masks[index];
            int offset = trailingZeros(mask);
            int count = bitCount(mask >>> offset);
            int expected = count == 32 ? -1 : (1 << count) - 1;
            if ((mask >>> offset) != expected) {
                throw new IllegalArgumentException("Mask must be contiguous");
            }
            if ((occupied & mask) != 0) {
                throw new IllegalArgumentException("Masks must not overlap");
            }
            if (bits < 32 && (mask >>> bits) != 0) {
                throw new IllegalArgumentException("Mask exceeds pixel size");
            }
            occupied |= mask;
            maskOffsets[index] = offset;
            maskBits[index] = count;
        }
    }

    private static int[] componentSizes(int[] colorMasks, int alphaMask) {
        int[] sizes = new int[colorMasks.length + (alphaMask == 0 ? 0 : 1)];
        for (int index = 0; index < colorMasks.length; index++) {
            sizes[index] = bitCount(colorMasks[index] >>> trailingZeros(colorMasks[index]));
        }
        if (alphaMask != 0) {
            sizes[sizes.length - 1] = bitCount(alphaMask >>> trailingZeros(alphaMask));
        }
        return sizes;
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

    private static int bitCount(int value) {
        int count = 0;
        int remaining = value;
        while (remaining != 0) {
            count += remaining & 1;
            remaining >>>= 1;
        }
        return count;
    }
}
