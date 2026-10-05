package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;

public class DirectColorModel extends PackedColorModel {
    public DirectColorModel(int bits, int redMask, int greenMask, int blueMask) {
        this(bits, redMask, greenMask, blueMask, 0);
    }

    public DirectColorModel(int bits, int redMask, int greenMask, int blueMask, int alphaMask) {
        this(ColorSpace.getInstance(ColorSpace.CS_sRGB), bits, redMask, greenMask, blueMask, alphaMask, false, defaultTransferType(bits));
    }

    public DirectColorModel(ColorSpace colorSpace, int bits, int redMask, int greenMask, int blueMask, int alphaMask, boolean isAlphaPremultiplied, int transferType) {
        super(colorSpace, bits, redMask, greenMask, blueMask, alphaMask, isAlphaPremultiplied, transparency(alphaMask), transferType);
    }

    public final int getRedMask() {
        return getMask(0);
    }

    public final int getGreenMask() {
        return getMask(1);
    }

    public final int getBlueMask() {
        return getMask(2);
    }

    public final int getAlphaMask() {
        return hasAlpha() ? getMask(3) : 0;
    }

    @Override
    public final int getRed(int pixel) {
        return colorByte(pixel, 0);
    }

    @Override
    public final int getGreen(int pixel) {
        return colorByte(pixel, 1);
    }

    @Override
    public final int getBlue(int pixel) {
        return colorByte(pixel, 2);
    }

    @Override
    public final int getAlpha(int pixel) {
        return hasAlpha() ? scaleToByte(component(pixel, 3), maskBits[3]) : 255;
    }

    @Override
    public final int getRGB(int pixel) {
        return getAlpha(pixel) << 24 | getRed(pixel) << 16 | getGreen(pixel) << 8 | getBlue(pixel);
    }

    @Override
    public int getRed(Object pixel) {
        return getRed(pixelValue(pixel));
    }

    @Override
    public int getGreen(Object pixel) {
        return getGreen(pixelValue(pixel));
    }

    @Override
    public int getBlue(Object pixel) {
        return getBlue(pixelValue(pixel));
    }

    @Override
    public int getAlpha(Object pixel) {
        return getAlpha(pixelValue(pixel));
    }

    @Override
    public int getRGB(Object pixel) {
        return getRGB(pixelValue(pixel));
    }

    @Override
    public Object getDataElements(int rgb, Object pixel) {
        int alpha = rgb >>> 24;
        int value = packByte(rgb >>> 16 & 0xff, 0, alpha) | packByte(rgb >>> 8 & 0xff, 1, alpha) | packByte(rgb & 0xff, 2, alpha);
        if (hasAlpha()) {
            value |= scaleFromByte(alpha, maskBits[3]) << maskOffsets[3] & getAlphaMask();
        }
        return setPixelValue(value, pixel);
    }

    @Override
    public final int[] getComponents(int pixel, int[] components, int offset) {
        int[] values = components == null ? new int[offset + getNumComponents()] : components;
        for (int component = 0; component < getNumComponents(); component++) {
            values[offset + component] = component(pixel, component);
        }
        return values;
    }

    @Override
    public final int[] getComponents(Object pixel, int[] components, int offset) {
        return getComponents(pixelValue(pixel), components, offset);
    }

    @Override
    public final WritableRaster createCompatibleWritableRaster(int width, int height) {
        return Raster.createWritableRaster(createCompatibleSampleModel(width, height), null);
    }

    @Override
    public int getDataElement(int[] components, int offset) {
        int value = 0;
        for (int component = 0; component < getNumComponents(); component++) {
            value |= components[offset + component] << maskOffsets[component] & getMask(component);
        }
        return value;
    }

    @Override
    public Object getDataElements(int[] components, int offset, Object pixel) {
        return setPixelValue(getDataElement(components, offset), pixel);
    }

    @Override
    public final ColorModel coerceData(WritableRaster raster, boolean isAlphaPremultiplied) {
        if (!hasAlpha() || isAlphaPremultiplied() == isAlphaPremultiplied) {
            return this;
        }
        int alphaMaximum = (int) maximumForBits(maskBits[3]);
        for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
            for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
                int pixel = pixelValue(raster.getDataElements(x, y, null));
                int alpha = component(pixel, 3);
                int converted = pixel & getAlphaMask();
                for (int component = 0; component < 3; component++) {
                    int color = component(pixel, component);
                    int adjusted = isAlphaPremultiplied ? (int) (((long) color * alpha + alphaMaximum / 2) / alphaMaximum) : alpha == 0 ? 0 : Math.min((int) maximumForBits(maskBits[component]), (int) (((long) color * alphaMaximum + alpha / 2) / alpha));
                    converted |= adjusted << maskOffsets[component] & getMask(component);
                }
                raster.setDataElements(x, y, setPixelValue(converted, null));
            }
        }
        return new DirectColorModel(getColorSpace(), pixel_bits, getRedMask(), getGreenMask(), getBlueMask(), getAlphaMask(), isAlphaPremultiplied, transferType);
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return raster.getTransferType() == transferType && isCompatibleSampleModel(raster.getSampleModel());
    }

    @Override
    public String toString() {
        return "DirectColorModel: rmask=" + Integer.toHexString(getRedMask()) + " gmask=" + Integer.toHexString(getGreenMask()) + " bmask=" + Integer.toHexString(getBlueMask()) + " amask=" + Integer.toHexString(getAlphaMask());
    }

    private int colorByte(int pixel, int colorComponent) {
        int value = component(pixel, colorComponent);
        if (isAlphaPremultiplied() && hasAlpha()) {
            int alpha = component(pixel, 3);
            if (alpha == 0) {
                return 0;
            }
            double alphaMaximum = maximumForBits(maskBits[3]);
            double colorMaximum = maximumForBits(maskBits[colorComponent]);
            return Math.min(255, (int) (value * alphaMaximum * 255.0 / (alpha * colorMaximum) + 0.5));
        }
        return scaleToByte(value, maskBits[colorComponent]);
    }

    private int component(int pixel, int index) {
        return (pixel & getMask(index)) >>> maskOffsets[index];
    }

    private int packByte(int value, int component, int alpha) {
        int color = isAlphaPremultiplied() ? (value * alpha + 127) / 255 : value;
        return scaleFromByte(color, maskBits[component]) << maskOffsets[component] & getMask(component);
    }

    private static int scaleToByte(int value, int bits) {
        return (int) (value * 255.0 / maximumForBits(bits) + 0.5);
    }

    private static int scaleFromByte(int value, int bits) {
        return (int) (value * maximumForBits(bits) / 255.0 + 0.5);
    }

    private static int transparency(int alphaMask) {
        if (alphaMask == 0) {
            return Transparency.OPAQUE;
        }
        return (alphaMask & (alphaMask - 1)) == 0 ? Transparency.BITMASK : Transparency.TRANSLUCENT;
    }
}
