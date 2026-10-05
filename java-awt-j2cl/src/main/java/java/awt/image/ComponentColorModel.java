package java.awt.image;

import java.awt.color.ColorSpace;
import java.util.Arrays;

public class ComponentColorModel extends ColorModel {
    private final int[] componentSizes;

    public ComponentColorModel(ColorSpace colorSpace, int[] componentSizes, boolean hasAlpha, boolean isAlphaPremultiplied, int transparency, int transferType) {
        super(totalBits(componentSizes), componentSizes, colorSpace, hasAlpha, hasAlpha && isAlphaPremultiplied, transparency, transferType);
        validateTransferType(transferType);
        this.componentSizes = Arrays.copyOf(componentSizes, componentSizes.length);
        validateComponentSizes(transferType);
    }

    public ComponentColorModel(ColorSpace colorSpace, boolean hasAlpha, boolean isAlphaPremultiplied, int transparency, int transferType) {
        this(colorSpace, defaultComponentSizes(colorSpace, hasAlpha, transferType), hasAlpha, isAlphaPremultiplied, transparency, transferType);
    }

    @Override
    public int getRed(int pixel) {
        return getRed(singlePixel(pixel));
    }

    @Override
    public int getGreen(int pixel) {
        return getGreen(singlePixel(pixel));
    }

    @Override
    public int getBlue(int pixel) {
        return getBlue(singlePixel(pixel));
    }

    @Override
    public int getAlpha(int pixel) {
        return getAlpha(singlePixel(pixel));
    }

    @Override
    public int getRGB(int pixel) {
        return getRGB(singlePixel(pixel));
    }

    @Override
    public int getRed(Object pixel) {
        return rgb(pixel)[0];
    }

    @Override
    public int getGreen(Object pixel) {
        return rgb(pixel)[1];
    }

    @Override
    public int getBlue(Object pixel) {
        return rgb(pixel)[2];
    }

    @Override
    public int getAlpha(Object pixel) {
        if (!hasAlpha()) {
            return 255;
        }
        return (int) (normalizedComponent(pixel, getNumColorComponents()) * 255.0 + 0.5);
    }

    @Override
    public int getRGB(Object pixel) {
        int[] rgb = rgb(pixel);
        return getAlpha(pixel) << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2];
    }

    @Override
    public Object getDataElements(int rgb, Object pixel) {
        float[] standardRgb = new float[] {(rgb >>> 16 & 0xff) / 255.0f, (rgb >>> 8 & 0xff) / 255.0f, (rgb & 0xff) / 255.0f};
        float[] color = getColorSpace().fromRGB(standardRgb);
        float alpha = (rgb >>> 24) / 255.0f;
        float[] normalized = new float[getNumComponents()];
        for (int component = 0; component < getNumColorComponents(); component++) {
            normalized[component] = isAlphaPremultiplied() ? color[component] * alpha : color[component];
        }
        if (hasAlpha()) {
            normalized[getNumColorComponents()] = alpha;
        }
        return encodeNormalized(normalized, pixel);
    }

    @Override
    public int[] getComponents(int pixel, int[] components, int offset) {
        return getComponents(singlePixel(pixel), components, offset);
    }

    @Override
    public int[] getComponents(Object pixel, int[] components, int offset) {
        requireIntegralTransfer();
        int[] values = components == null ? new int[offset + getNumComponents()] : components;
        for (int component = 0; component < getNumComponents(); component++) {
            values[offset + component] = integralComponent(pixel, component);
        }
        return values;
    }

    @Override
    public int[] getUnnormalizedComponents(float[] normalized, int normalizedOffset, int[] components, int offset) {
        requireIntegralTransfer();
        int[] values = components == null ? new int[offset + getNumComponents()] : components;
        for (int component = 0; component < getNumComponents(); component++) {
            values[offset + component] = (int) (normalized[normalizedOffset + component] * maximum(component) + 0.5);
        }
        return values;
    }

    @Override
    public float[] getNormalizedComponents(int[] components, int offset, float[] normalized, int normalizedOffset) {
        requireIntegralTransfer();
        float[] values = normalized == null ? new float[normalizedOffset + getNumComponents()] : normalized;
        for (int component = 0; component < getNumComponents(); component++) {
            values[normalizedOffset + component] = (float) (components[offset + component] / maximum(component));
        }
        unpremultiply(values, normalizedOffset);
        return values;
    }

    @Override
    public int getDataElement(int[] components, int offset) {
        requireSingleComponent();
        return components[offset];
    }

    @Override
    public Object getDataElements(int[] components, int offset, Object pixel) {
        requireIntegralTransfer();
        Object values = pixel == null ? createTransferArray() : pixel;
        for (int component = 0; component < getNumComponents(); component++) {
            setIntegralComponent(values, component, components[offset + component]);
        }
        return values;
    }

    @Override
    public int getDataElement(float[] normalized, int offset) {
        requireSingleComponent();
        return (int) (normalized[offset] * maximum(0) + 0.5);
    }

    @Override
    public Object getDataElements(float[] normalized, int offset, Object pixel) {
        float[] values = new float[getNumComponents()];
        for (int component = 0; component < getNumComponents(); component++) {
            values[component] = normalized[offset + component];
        }
        if (isAlphaPremultiplied() && hasAlpha()) {
            float alpha = values[getNumColorComponents()];
            for (int component = 0; component < getNumColorComponents(); component++) {
                values[component] *= alpha;
            }
        }
        return encodeNormalized(values, pixel);
    }

    @Override
    public float[] getNormalizedComponents(Object pixel, float[] normalized, int normalizedOffset) {
        float[] values = normalized == null ? new float[normalizedOffset + getNumComponents()] : normalized;
        for (int component = 0; component < getNumComponents(); component++) {
            values[normalizedOffset + component] = (float) normalizedComponent(pixel, component);
        }
        unpremultiply(values, normalizedOffset);
        return values;
    }

    @Override
    public ColorModel coerceData(WritableRaster raster, boolean isAlphaPremultiplied) {
        if (!hasAlpha() || isAlphaPremultiplied() == isAlphaPremultiplied) {
            return this;
        }
        int alphaBand = getNumColorComponents();
        double alphaMaximum = maximum(alphaBand);
        for (int y = raster.getMinY(); y < raster.getMinY() + raster.getHeight(); y++) {
            for (int x = raster.getMinX(); x < raster.getMinX() + raster.getWidth(); x++) {
                double alpha = sample(raster, x, y, alphaBand);
                for (int component = 0; component < getNumColorComponents(); component++) {
                    double color = sample(raster, x, y, component);
                    double adjusted = isAlphaPremultiplied ? color * alpha / alphaMaximum : alpha == 0.0 ? 0.0 : Math.min(maximum(component), color * alphaMaximum / alpha);
                    setSample(raster, x, y, component, adjusted);
                }
            }
        }
        return new ComponentColorModel(getColorSpace(), componentSizes, true, isAlphaPremultiplied, getTransparency(), transferType);
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return raster.getNumBands() == getNumComponents() && raster.getTransferType() == transferType && isCompatibleSampleModel(raster.getSampleModel());
    }

    @Override
    public WritableRaster createCompatibleWritableRaster(int width, int height) {
        return Raster.createWritableRaster(createCompatibleSampleModel(width, height), null);
    }

    @Override
    public SampleModel createCompatibleSampleModel(int width, int height) {
        int[] bandOffsets = new int[getNumComponents()];
        for (int component = 0; component < bandOffsets.length; component++) {
            bandOffsets[component] = component;
        }
        return new PixelInterleavedSampleModel(transferType, width, height, getNumComponents(), width * getNumComponents(), bandOffsets);
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel model) {
        if (!(model instanceof ComponentSampleModel) || model.getTransferType() != transferType || model.getNumBands() != getNumComponents()) {
            return false;
        }
        int[] sampleSizes = model.getSampleSize();
        for (int component = 0; component < sampleSizes.length; component++) {
            if (sampleSizes[component] < componentSizes[component]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public WritableRaster getAlphaRaster(WritableRaster raster) {
        if (!hasAlpha()) {
            return null;
        }
        return raster.createWritableChild(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), raster.getMinX(), raster.getMinY(), new int[] {getNumColorComponents()});
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ComponentColorModel && super.equals(object) && Arrays.equals(componentSizes, ((ComponentColorModel) object).componentSizes);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Arrays.hashCode(componentSizes);
    }

    private int[] rgb(Object pixel) {
        float[] color = new float[getNumColorComponents()];
        double alpha = hasAlpha() ? normalizedComponent(pixel, getNumColorComponents()) : 1.0;
        for (int component = 0; component < color.length; component++) {
            double value = normalizedComponent(pixel, component);
            color[component] = (float) (isAlphaPremultiplied() && hasAlpha() ? alpha == 0.0 ? 0.0 : value / alpha : value);
        }
        float[] converted = getColorSpace().toRGB(color);
        return new int[] {toByte(converted[0]), toByte(converted[1]), toByte(converted[2])};
    }

    private Object singlePixel(int pixel) {
        requireSingleComponent();
        Object values = createTransferArray();
        setIntegralComponent(values, 0, pixel);
        return values;
    }

    private Object encodeNormalized(float[] normalized, Object output) {
        Object values = output == null ? createTransferArray() : output;
        for (int component = 0; component < getNumComponents(); component++) {
            setNormalizedComponent(values, component, normalized[component]);
        }
        return values;
    }

    private Object createTransferArray() {
        if (transferType == DataBuffer.TYPE_BYTE) {
            return new byte[getNumComponents()];
        }
        if (transferType == DataBuffer.TYPE_USHORT || transferType == DataBuffer.TYPE_SHORT) {
            return new short[getNumComponents()];
        }
        if (transferType == DataBuffer.TYPE_INT) {
            return new int[getNumComponents()];
        }
        if (transferType == DataBuffer.TYPE_FLOAT) {
            return new float[getNumComponents()];
        }
        return new double[getNumComponents()];
    }

    private double normalizedComponent(Object pixel, int component) {
        if (transferType == DataBuffer.TYPE_FLOAT) {
            return clamp(((float[]) pixel)[component]);
        }
        if (transferType == DataBuffer.TYPE_DOUBLE) {
            return clamp(((double[]) pixel)[component]);
        }
        return integralComponent(pixel, component) / maximum(component);
    }

    private int integralComponent(Object pixel, int component) {
        if (transferType == DataBuffer.TYPE_BYTE) {
            return ((byte[]) pixel)[component] & 0xff;
        }
        if (transferType == DataBuffer.TYPE_USHORT) {
            return ((short[]) pixel)[component] & 0xffff;
        }
        if (transferType == DataBuffer.TYPE_SHORT) {
            return ((short[]) pixel)[component];
        }
        return ((int[]) pixel)[component];
    }

    private void setIntegralComponent(Object pixel, int component, int value) {
        if (transferType == DataBuffer.TYPE_BYTE) {
            ((byte[]) pixel)[component] = (byte) value;
        } else if (transferType == DataBuffer.TYPE_USHORT || transferType == DataBuffer.TYPE_SHORT) {
            ((short[]) pixel)[component] = (short) value;
        } else {
            ((int[]) pixel)[component] = value;
        }
    }

    private void setNormalizedComponent(Object pixel, int component, double value) {
        double bounded = clamp(value);
        if (transferType == DataBuffer.TYPE_FLOAT) {
            ((float[]) pixel)[component] = (float) bounded;
        } else if (transferType == DataBuffer.TYPE_DOUBLE) {
            ((double[]) pixel)[component] = bounded;
        } else {
            setIntegralComponent(pixel, component, (int) (bounded * maximum(component) + 0.5));
        }
    }

    private double sample(Raster raster, int x, int y, int component) {
        if (transferType == DataBuffer.TYPE_FLOAT) {
            return raster.getSampleFloat(x, y, component);
        }
        if (transferType == DataBuffer.TYPE_DOUBLE) {
            return raster.getSampleDouble(x, y, component);
        }
        return raster.getSample(x, y, component);
    }

    private void setSample(WritableRaster raster, int x, int y, int component, double value) {
        if (transferType == DataBuffer.TYPE_FLOAT) {
            raster.setSample(x, y, component, (float) value);
        } else if (transferType == DataBuffer.TYPE_DOUBLE) {
            raster.setSample(x, y, component, value);
        } else {
            raster.setSample(x, y, component, (int) (value + 0.5));
        }
    }

    private double maximum(int component) {
        return maximumForBits(componentSizes[component]);
    }

    private void unpremultiply(float[] values, int offset) {
        if (!isAlphaPremultiplied() || !hasAlpha()) {
            return;
        }
        float alpha = values[offset + getNumColorComponents()];
        for (int component = 0; component < getNumColorComponents(); component++) {
            values[offset + component] = alpha == 0.0f ? 0.0f : values[offset + component] / alpha;
        }
    }

    private void requireSingleComponent() {
        if (getNumComponents() != 1) {
            throw new IllegalArgumentException("This method requires a single-component color model");
        }
    }

    private void requireIntegralTransfer() {
        if (transferType == DataBuffer.TYPE_FLOAT || transferType == DataBuffer.TYPE_DOUBLE) {
            throw new UnsupportedOperationException("Unnormalized components are not supported for floating-point transfer types");
        }
    }

    private void validateComponentSizes(int transferType) {
        int elementBits = DataBuffer.getDataTypeSize(transferType);
        for (int size : componentSizes) {
            if (size <= 0 || size > elementBits) {
                throw new IllegalArgumentException("Invalid component size");
            }
        }
    }

    private static int totalBits(int[] componentSizes) {
        int total = 0;
        for (int size : componentSizes) {
            total += size;
        }
        return total;
    }

    private static int[] defaultComponentSizes(ColorSpace colorSpace, boolean hasAlpha, int transferType) {
        int components = colorSpace.getNumComponents() + (hasAlpha ? 1 : 0);
        int[] sizes = new int[components];
        Arrays.fill(sizes, DataBuffer.getDataTypeSize(transferType));
        return sizes;
    }

    private static void validateTransferType(int transferType) {
        if (transferType < DataBuffer.TYPE_BYTE || transferType > DataBuffer.TYPE_DOUBLE) {
            throw new IllegalArgumentException("Unsupported transfer type");
        }
    }

    private static int toByte(float value) {
        return (int) (clamp(value) * 255.0f + 0.5f);
    }

    private static double clamp(double value) {
        return value <= 0.0 ? 0.0 : value >= 1.0 ? 1.0 : value;
    }
}
