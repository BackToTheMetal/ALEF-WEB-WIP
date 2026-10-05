package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.math.BigInteger;
import java.util.Arrays;

public class IndexColorModel extends ColorModel {
    private final int mapSize;
    private final int[] palette;
    private final int transparentPixel;
    private final BigInteger validPixels;

    public IndexColorModel(int bits, int size, byte[] reds, byte[] greens, byte[] blues) {
        this(bits, size, createPalette(size, reds, greens, blues, null, -1), false, -1, transferType(bits), null);
    }

    public IndexColorModel(int bits, int size, byte[] reds, byte[] greens, byte[] blues, int transparent) {
        this(bits, size, createPalette(size, reds, greens, blues, null, transparent), true, transparent, transferType(bits), null);
    }

    public IndexColorModel(int bits, int size, byte[] reds, byte[] greens, byte[] blues, byte[] alphas) {
        this(bits, size, createPalette(size, reds, greens, blues, alphas, -1), true, findTransparent(createPalette(size, reds, greens, blues, alphas, -1)), transferType(bits), null);
    }

    public IndexColorModel(int bits, int size, byte[] colorMap, int start, boolean hasAlpha) {
        this(bits, size, colorMap, start, hasAlpha, -1);
    }

    public IndexColorModel(int bits, int size, byte[] colorMap, int start, boolean hasAlpha, int transparent) {
        this(bits, size, unpackPalette(size, colorMap, start, hasAlpha, transparent), hasAlpha || transparent >= 0, transparent, transferType(bits), null);
    }

    public IndexColorModel(int bits, int size, int[] colorMap, int start, boolean hasAlpha, int transparent, int transferType) {
        this(bits, size, copyPalette(size, colorMap, start, hasAlpha, transparent), hasAlpha || transparent >= 0, transparent, transferType, null);
    }

    public IndexColorModel(int bits, int size, int[] colorMap, int start, int transferType, BigInteger validBits) {
        this(bits, size, copyPalette(size, colorMap, start, true, -1), hasPaletteAlpha(colorMap, start, size), findTransparent(copyPalette(size, colorMap, start, true, -1)), transferType, validBits);
    }

    private IndexColorModel(int bits, int size, int[] palette, boolean hasAlpha, int transparent, int transferType, BigInteger validPixels) {
        super(bits, hasAlpha ? new int[] {8, 8, 8, 8} : new int[] {8, 8, 8}, ColorSpace.getInstance(ColorSpace.CS_sRGB), hasAlpha, false, paletteTransparency(palette, hasAlpha), transferType);
        if (size < 1 || size > 65536 || bits < 1 || bits > 16 || size > (1 << bits)) {
            throw new IllegalArgumentException("Invalid palette size or pixel size");
        }
        if (transferType != DataBuffer.TYPE_BYTE && transferType != DataBuffer.TYPE_USHORT) {
            throw new IllegalArgumentException("Transfer type must be byte or unsigned short");
        }
        mapSize = size;
        this.palette = palette;
        transparentPixel = transparent >= 0 && transparent < size ? transparent : findTransparent(palette);
        this.validPixels = validPixels;
    }

    @Override
    public int getTransparency() {
        return super.getTransparency();
    }

    @Override
    public int[] getComponentSize() {
        return hasAlpha() ? new int[] {8, 8, 8, 8} : new int[] {8, 8, 8};
    }

    public final int getMapSize() {
        return mapSize;
    }

    public final int getTransparentPixel() {
        return transparentPixel;
    }

    public final void getReds(byte[] output) {
        for (int index = 0; index < mapSize; index++) {
            output[index] = (byte) (palette[index] >>> 16);
        }
    }

    public final void getGreens(byte[] output) {
        for (int index = 0; index < mapSize; index++) {
            output[index] = (byte) (palette[index] >>> 8);
        }
    }

    public final void getBlues(byte[] output) {
        for (int index = 0; index < mapSize; index++) {
            output[index] = (byte) palette[index];
        }
    }

    public final void getAlphas(byte[] output) {
        for (int index = 0; index < mapSize; index++) {
            output[index] = (byte) (palette[index] >>> 24);
        }
    }

    public final void getRGBs(int[] output) {
        System.arraycopy(palette, 0, output, 0, mapSize);
    }

    @Override
    public final int getRed(int pixel) {
        return validIndex(pixel) ? palette[pixel] >>> 16 & 0xff : 0;
    }

    @Override
    public final int getGreen(int pixel) {
        return validIndex(pixel) ? palette[pixel] >>> 8 & 0xff : 0;
    }

    @Override
    public final int getBlue(int pixel) {
        return validIndex(pixel) ? palette[pixel] & 0xff : 0;
    }

    @Override
    public final int getAlpha(int pixel) {
        return validIndex(pixel) ? palette[pixel] >>> 24 : 0;
    }

    @Override
    public final int getRGB(int pixel) {
        return validIndex(pixel) ? palette[pixel] : 0;
    }

    @Override
    public Object getDataElements(int rgb, Object pixel) {
        return setPixelValue(nearestIndex(rgb), pixel);
    }

    @Override
    public int[] getComponents(int pixel, int[] components, int offset) {
        int[] values = components == null ? new int[offset + getNumComponents()] : components;
        values[offset] = getRed(pixel);
        values[offset + 1] = getGreen(pixel);
        values[offset + 2] = getBlue(pixel);
        if (hasAlpha()) {
            values[offset + 3] = getAlpha(pixel);
        }
        return values;
    }

    @Override
    public int[] getComponents(Object pixel, int[] components, int offset) {
        return getComponents(pixelValue(pixel), components, offset);
    }

    @Override
    public int getDataElement(int[] components, int offset) {
        int alpha = hasAlpha() ? components[offset + 3] : 255;
        int rgb = alpha << 24 | components[offset] << 16 | components[offset + 1] << 8 | components[offset + 2];
        return nearestIndex(rgb);
    }

    @Override
    public Object getDataElements(int[] components, int offset, Object pixel) {
        return setPixelValue(getDataElement(components, offset), pixel);
    }

    @Override
    public WritableRaster createCompatibleWritableRaster(int width, int height) {
        return Raster.createWritableRaster(createCompatibleSampleModel(width, height), null);
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return isCompatibleSampleModel(raster.getSampleModel()) && raster.getTransferType() == transferType;
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        if (pixel_bits == 1 || pixel_bits == 2 || pixel_bits == 4) {
            return new MultiPixelPackedSampleModel(transferType, width, height, pixel_bits);
        }
        return new PixelInterleavedSampleModel(transferType, width, height, 1, width, new int[] {0});
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel model) {
        if (model.getTransferType() != transferType || model.getNumBands() != 1) {
            return false;
        }
        int sampleSize = model.getSampleSize(0);
        return sampleSize >= pixel_bits && sampleSize <= DataBuffer.getDataTypeSize(transferType);
    }

    public BufferedImage convertToIntDiscrete(Raster raster, boolean forceArgb) {
        if (!isCompatibleRaster(raster)) {
            throw new IllegalArgumentException("This raster is not compatible with this IndexColorModel");
        }
        int imageType = forceArgb || getTransparency() != Transparency.OPAQUE ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage image = new BufferedImage(raster.getWidth(), raster.getHeight(), imageType);
        int pixelMask = pixel_bits == 32 ? -1 : (1 << pixel_bits) - 1;
        for (int row = 0; row < raster.getHeight(); row++) {
            for (int column = 0; column < raster.getWidth(); column++) {
                int index = raster.getSample(raster.getMinX() + column, raster.getMinY() + row, 0) & pixelMask;
                image.setRGB(column, row, getRGB(index));
            }
        }
        return image;
    }

    public boolean isValid(int pixel) {
        return validIndex(pixel);
    }

    public boolean isValid() {
        return validPixels == null;
    }

    public BigInteger getValidPixels() {
        return validPixels == null ? BigInteger.ONE.shiftLeft(mapSize).subtract(BigInteger.ONE) : validPixels;
    }

    @Override
    public void finalize() {
    }

    @Override
    public String toString() {
        return "IndexColorModel: #pixelBits = " + pixel_bits + " numComponents = " + getNumComponents() + " color space = " + getColorSpace() + " transparency = " + getTransparency() + " transIndex = " + transparentPixel + " has alpha = " + hasAlpha() + " isAlphaPre = " + isAlphaPremultiplied();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IndexColorModel)) {
            return false;
        }
        IndexColorModel model = (IndexColorModel) object;
        return super.equals(object) && mapSize == model.mapSize && transparentPixel == model.transparentPixel && Arrays.equals(palette, model.palette) && getValidPixels().equals(model.getValidPixels());
    }

    @Override
    public int hashCode() {
        return 31 * (31 * super.hashCode() + Arrays.hashCode(palette)) + transparentPixel;
    }

    private boolean validIndex(int pixel) {
        return pixel >= 0 && pixel < mapSize && (validPixels == null || validPixels.testBit(pixel));
    }

    private int nearestIndex(int rgb) {
        if ((rgb >>> 24) == 0 && transparentPixel >= 0) {
            return transparentPixel;
        }
        int bestIndex = 0;
        long bestDistance = Long.MAX_VALUE;
        for (int index = 0; index < mapSize; index++) {
            if (!validIndex(index)) {
                continue;
            }
            if (palette[index] == rgb) {
                return index;
            }
            int alpha = (palette[index] >>> 24) - (rgb >>> 24);
            int red = (palette[index] >>> 16 & 0xff) - (rgb >>> 16 & 0xff);
            int green = (palette[index] >>> 8 & 0xff) - (rgb >>> 8 & 0xff);
            int blue = (palette[index] & 0xff) - (rgb & 0xff);
            long distance = alpha * alpha + red * red + green * green + blue * blue;
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = index;
            }
        }
        return bestIndex;
    }

    private static int[] createPalette(int size, byte[] reds, byte[] greens, byte[] blues, byte[] alphas, int transparent) {
        int[] colors = new int[size];
        for (int index = 0; index < size; index++) {
            int alpha = alphas == null ? 255 : alphas[index] & 0xff;
            if (index == transparent) {
                alpha = 0;
            }
            colors[index] = alpha << 24 | (reds[index] & 0xff) << 16 | (greens[index] & 0xff) << 8 | blues[index] & 0xff;
        }
        return colors;
    }

    private static int[] unpackPalette(int size, byte[] values, int start, boolean hasAlpha, int transparent) {
        int[] colors = new int[size];
        int components = hasAlpha ? 4 : 3;
        for (int index = 0; index < size; index++) {
            int position = start + index * components;
            int alpha = hasAlpha ? values[position + 3] & 0xff : 255;
            if (index == transparent) {
                alpha = 0;
            }
            colors[index] = alpha << 24 | (values[position] & 0xff) << 16 | (values[position + 1] & 0xff) << 8 | values[position + 2] & 0xff;
        }
        return colors;
    }

    private static int[] copyPalette(int size, int[] values, int start, boolean hasAlpha, int transparent) {
        int[] colors = new int[size];
        for (int index = 0; index < size; index++) {
            int color = values[start + index];
            if (!hasAlpha) {
                color |= 0xff000000;
            }
            if (index == transparent) {
                color &= 0x00ffffff;
            }
            colors[index] = color;
        }
        return colors;
    }

    private static boolean hasPaletteAlpha(int[] values, int start, int size) {
        for (int index = 0; index < size; index++) {
            if ((values[start + index] >>> 24) != 255) {
                return true;
            }
        }
        return false;
    }

    private static int findTransparent(int[] colors) {
        for (int index = 0; index < colors.length; index++) {
            if ((colors[index] >>> 24) == 0) {
                return index;
            }
        }
        return -1;
    }

    private static int paletteTransparency(int[] colors, boolean hasAlpha) {
        if (!hasAlpha) {
            return Transparency.OPAQUE;
        }
        boolean translucent = false;
        for (int color : colors) {
            int alpha = color >>> 24;
            if (alpha != 0 && alpha != 255) {
                translucent = true;
            }
        }
        return translucent ? Transparency.TRANSLUCENT : Transparency.BITMASK;
    }

    private static int transferType(int bits) {
        return bits <= 8 ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_USHORT;
    }
}
