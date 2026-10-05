package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.util.Arrays;

public abstract class ColorModel implements Transparency {
    protected int pixel_bits;
    protected int transferType;
    private static final ColorModel RGB_DEFAULT = new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000);
    private final int[] componentSizes;
    private final ColorSpace colorSpace;
    private final boolean alpha;
    private final boolean alphaPremultiplied;
    private final int transparency;
    private final int numColorComponents;
    private final int numComponents;

    public static ColorModel getRGBdefault() {
        return RGB_DEFAULT;
    }

    public ColorModel(int bits) {
        this(bits, new int[] {8, 8, 8, 8}, ColorSpace.getInstance(ColorSpace.CS_sRGB), true, false, TRANSLUCENT, defaultTransferType(bits));
    }

    protected ColorModel(int bits, int[] componentSizes, ColorSpace colorSpace, boolean hasAlpha, boolean isAlphaPremultiplied, int transparency, int transferType) {
        if (bits <= 0) {
            throw new IllegalArgumentException("Number of pixel bits must be positive");
        }
        if (colorSpace == null) {
            throw new NullPointerException("colorSpace must not be null");
        }
        int colorComponents = colorSpace.getNumComponents();
        int expectedComponents = colorComponents + (hasAlpha ? 1 : 0);
        if (componentSizes != null && componentSizes.length != expectedComponents) {
            throw new IllegalArgumentException("Component size array does not match the color space");
        }
        DataBuffer.getDataTypeSize(transferType);
        pixel_bits = bits;
        this.componentSizes = componentSizes == null ? null : Arrays.copyOf(componentSizes, componentSizes.length);
        this.colorSpace = colorSpace;
        alpha = hasAlpha;
        alphaPremultiplied = isAlphaPremultiplied;
        this.transparency = transparency;
        this.transferType = transferType;
        numColorComponents = colorComponents;
        numComponents = expectedComponents;
    }

    public final boolean hasAlpha() {
        return alpha;
    }

    public final boolean isAlphaPremultiplied() {
        return alphaPremultiplied;
    }

    public final int getTransferType() {
        return transferType;
    }

    public int getPixelSize() {
        return pixel_bits;
    }

    public int getComponentSize(int component) {
        if (componentSizes == null) {
            throw new NullPointerException("Component sizes are unavailable");
        }
        return componentSizes[component];
    }

    public int[] getComponentSize() {
        return componentSizes == null ? null : Arrays.copyOf(componentSizes, componentSizes.length);
    }

    @Override
    public int getTransparency() {
        return transparency;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public int getNumColorComponents() {
        return numColorComponents;
    }

    public abstract int getRed(int pixel);

    public abstract int getGreen(int pixel);

    public abstract int getBlue(int pixel);

    public abstract int getAlpha(int pixel);

    public int getRGB(int pixel) {
        return getAlpha(pixel) << 24 | getRed(pixel) << 16 | getGreen(pixel) << 8 | getBlue(pixel);
    }

    public int getRed(Object pixel) {
        return getRed(pixelValue(pixel));
    }

    public int getGreen(Object pixel) {
        return getGreen(pixelValue(pixel));
    }

    public int getBlue(Object pixel) {
        return getBlue(pixelValue(pixel));
    }

    public int getAlpha(Object pixel) {
        return getAlpha(pixelValue(pixel));
    }

    public int getRGB(Object pixel) {
        return getRGB(pixelValue(pixel));
    }

    public Object getDataElements(int rgb, Object pixel) {
        throw new UnsupportedOperationException("This color model does not support packed data elements");
    }

    public int[] getComponents(int pixel, int[] components, int offset) {
        int[] values = components == null ? new int[offset + numComponents] : components;
        values[offset] = getRed(pixel);
        if (numColorComponents > 1) {
            values[offset + 1] = getGreen(pixel);
        }
        if (numColorComponents > 2) {
            values[offset + 2] = getBlue(pixel);
        }
        if (alpha) {
            values[offset + numColorComponents] = getAlpha(pixel);
        }
        return values;
    }

    public int[] getComponents(Object pixel, int[] components, int offset) {
        return getComponents(pixelValue(pixel), components, offset);
    }

    public int[] getUnnormalizedComponents(float[] normalized, int normalizedOffset, int[] components, int offset) {
        if (componentSizes == null) {
            throw new UnsupportedOperationException("Component sizes are unavailable");
        }
        int[] values = components == null ? new int[offset + numComponents] : components;
        for (int component = 0; component < numComponents; component++) {
            double maximum = maximumForBits(componentSizes[component]);
            values[offset + component] = (int) (normalized[normalizedOffset + component] * maximum + 0.5);
        }
        return values;
    }

    public float[] getNormalizedComponents(int[] components, int offset, float[] normalized, int normalizedOffset) {
        if (componentSizes == null) {
            throw new UnsupportedOperationException("Component sizes are unavailable");
        }
        float[] values = normalized == null ? new float[normalizedOffset + numComponents] : normalized;
        for (int component = 0; component < numComponents; component++) {
            values[normalizedOffset + component] = (float) (components[offset + component] / maximumForBits(componentSizes[component]));
        }
        return values;
    }

    public int getDataElement(int[] components, int offset) {
        throw new UnsupportedOperationException("This color model does not support a single data element");
    }

    public Object getDataElements(int[] components, int offset, Object pixel) {
        return getDataElements(componentsToRgb(components, offset), pixel);
    }

    public int getDataElement(float[] normalized, int offset) {
        int[] components = getUnnormalizedComponents(normalized, offset, null, 0);
        return getDataElement(components, 0);
    }

    public Object getDataElements(float[] normalized, int offset, Object pixel) {
        int[] components = getUnnormalizedComponents(normalized, offset, null, 0);
        return getDataElements(components, 0, pixel);
    }

    public float[] getNormalizedComponents(Object pixel, float[] normalized, int normalizedOffset) {
        int[] components = getComponents(pixel, null, 0);
        return getNormalizedComponents(components, 0, normalized, normalizedOffset);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ColorModel)) {
            return false;
        }
        ColorModel model = (ColorModel) object;
        return pixel_bits == model.pixel_bits && transferType == model.transferType && alpha == model.alpha && alphaPremultiplied == model.alphaPremultiplied && transparency == model.transparency && colorSpace.equals(model.colorSpace) && Arrays.equals(componentSizes, model.componentSizes);
    }

    @Override
    public int hashCode() {
        int result = pixel_bits;
        result = 31 * result + transferType;
        result = 31 * result + (alpha ? 1 : 0);
        result = 31 * result + (alphaPremultiplied ? 1 : 0);
        return 31 * result + Arrays.hashCode(componentSizes);
    }

    public final ColorSpace getColorSpace() {
        return colorSpace;
    }

    public ColorModel coerceData(WritableRaster raster, boolean isAlphaPremultiplied) {
        if (alphaPremultiplied == isAlphaPremultiplied) {
            return this;
        }
        throw new UnsupportedOperationException("Alpha coercion is not supported by this color model");
    }

    public boolean isCompatibleRaster(Raster raster) {
        return isCompatibleSampleModel(raster.getSampleModel());
    }

    public WritableRaster createCompatibleWritableRaster(int width, int height) {
        return Raster.createWritableRaster(createCompatibleSampleModel(width, height), null);
    }

    public SampleModel createCompatibleSampleModel(int width, int height) {
        throw new UnsupportedOperationException("Compatible sample model is not defined");
    }

    public boolean isCompatibleSampleModel(SampleModel model) {
        return false;
    }

    public void finalize() {
    }

    public WritableRaster getAlphaRaster(WritableRaster raster) {
        return null;
    }

    @Override
    public String toString() {
        return "ColorModel: #pixelBits = " + pixel_bits + " numComponents = " + numComponents + " color space = " + colorSpace + " transparency = " + transparency + " has alpha = " + alpha + " isAlphaPre = " + alphaPremultiplied;
    }

    final int pixelValue(Object pixel) {
        if (transferType == DataBuffer.TYPE_BYTE) {
            return ((byte[]) pixel)[0] & 0xff;
        }
        if (transferType == DataBuffer.TYPE_USHORT) {
            return ((short[]) pixel)[0] & 0xffff;
        }
        if (transferType == DataBuffer.TYPE_SHORT) {
            return ((short[]) pixel)[0];
        }
        return ((int[]) pixel)[0];
    }

    final Object setPixelValue(int value, Object pixel) {
        if (transferType == DataBuffer.TYPE_BYTE) {
            byte[] values = pixel == null ? new byte[1] : (byte[]) pixel;
            values[0] = (byte) value;
            return values;
        }
        if (transferType == DataBuffer.TYPE_USHORT || transferType == DataBuffer.TYPE_SHORT) {
            short[] values = pixel == null ? new short[1] : (short[]) pixel;
            values[0] = (short) value;
            return values;
        }
        int[] values = pixel == null ? new int[1] : (int[]) pixel;
        values[0] = value;
        return values;
    }

    static int defaultTransferType(int bits) {
        if (bits <= 8) {
            return DataBuffer.TYPE_BYTE;
        }
        if (bits <= 16) {
            return DataBuffer.TYPE_USHORT;
        }
        return DataBuffer.TYPE_INT;
    }

    static double maximumForBits(int bits) {
        return bits >= 32 ? 4294967295.0 : (1L << bits) - 1L;
    }

    private int componentsToRgb(int[] components, int offset) {
        int red = scaleToByte(components[offset], componentSizes[0]);
        int green = numColorComponents > 1 ? scaleToByte(components[offset + 1], componentSizes[1]) : red;
        int blue = numColorComponents > 2 ? scaleToByte(components[offset + 2], componentSizes[2]) : red;
        int alphaValue = alpha ? scaleToByte(components[offset + numColorComponents], componentSizes[numColorComponents]) : 255;
        return alphaValue << 24 | red << 16 | green << 8 | blue;
    }

    private static int scaleToByte(int value, int bits) {
        return (int) (value * 255.0 / maximumForBits(bits) + 0.5);
    }
}
