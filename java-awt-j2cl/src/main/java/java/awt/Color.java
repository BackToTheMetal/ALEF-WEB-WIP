package java.awt;

import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.Serializable;

public class Color implements Paint, Serializable {
    public static final Color white = new Color(255, 255, 255);
    public static final Color WHITE = white;
    public static final Color lightGray = new Color(192, 192, 192);
    public static final Color LIGHT_GRAY = lightGray;
    public static final Color gray = new Color(128, 128, 128);
    public static final Color GRAY = gray;
    public static final Color darkGray = new Color(64, 64, 64);
    public static final Color DARK_GRAY = darkGray;
    public static final Color black = new Color(0, 0, 0);
    public static final Color BLACK = black;
    public static final Color red = new Color(255, 0, 0);
    public static final Color RED = red;
    public static final Color pink = new Color(255, 175, 175);
    public static final Color PINK = pink;
    public static final Color orange = new Color(255, 200, 0);
    public static final Color ORANGE = orange;
    public static final Color yellow = new Color(255, 255, 0);
    public static final Color YELLOW = yellow;
    public static final Color green = new Color(0, 255, 0);
    public static final Color GREEN = green;
    public static final Color magenta = new Color(255, 0, 255);
    public static final Color MAGENTA = magenta;
    public static final Color cyan = new Color(0, 255, 255);
    public static final Color CYAN = cyan;
    public static final Color blue = new Color(0, 0, 255);
    public static final Color BLUE = blue;
    private static final long serialVersionUID = 118526816881161077L;
    private static final double FACTOR = 0.7;
    private static final ColorSpace SRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    int value;
    private float[] frgbvalue;
    private float[] fvalue;
    private float falpha;
    private ColorSpace cs;

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int red, int green, int blue, int alpha) {
        checkByte(red, "Red");
        checkByte(green, "Green");
        checkByte(blue, "Blue");
        checkByte(alpha, "Alpha");
        value = alpha << 24 | red << 16 | green << 8 | blue;
    }

    public Color(int rgb) {
        value = 0xff000000 | rgb;
    }

    public Color(int rgba, boolean hasAlpha) {
        value = hasAlpha ? rgba : 0xff000000 | rgba;
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1.0f);
    }

    public Color(float red, float green, float blue, float alpha) {
        checkUnit(red, "Red");
        checkUnit(green, "Green");
        checkUnit(blue, "Blue");
        checkUnit(alpha, "Alpha");
        value = toByte(alpha) << 24 | toByte(red) << 16 | toByte(green) << 8 | toByte(blue);
        frgbvalue = new float[] {red, green, blue};
        falpha = alpha;
    }

    public Color(ColorSpace colorSpace, float[] components, float alpha) {
        if (colorSpace == null) {
            throw new NullPointerException("colorSpace");
        }
        if (components == null) {
            throw new NullPointerException("components");
        }
        if (components.length < colorSpace.getNumComponents()) {
            throw new IllegalArgumentException("Components do not match the color space");
        }
        checkUnit(alpha, "Alpha");
        fvalue = new float[colorSpace.getNumComponents()];
        for (int component = 0; component < fvalue.length; component++) {
            float item = components[component];
            if (item < colorSpace.getMinValue(component) || item > colorSpace.getMaxValue(component)) {
                throw new IllegalArgumentException("Color component outside color space range: " + item);
            }
            fvalue[component] = item;
        }
        cs = colorSpace;
        falpha = alpha;
        frgbvalue = colorSpace.toRGB(fvalue);
        value = toByte(alpha) << 24 | toByte(frgbvalue[0]) << 16 | toByte(frgbvalue[1]) << 8 | toByte(frgbvalue[2]);
    }

    public int getRed() {
        return value >> 16 & 0xff;
    }

    public int getGreen() {
        return value >> 8 & 0xff;
    }

    public int getBlue() {
        return value & 0xff;
    }

    public int getAlpha() {
        return value >>> 24;
    }

    public int getRGB() {
        return value;
    }

    public Color brighter() {
        int redValue = getRed();
        int greenValue = getGreen();
        int blueValue = getBlue();
        int threshold = (int) (1.0 / (1.0 - FACTOR));
        if (redValue == 0 && greenValue == 0 && blueValue == 0) {
            return new Color(threshold, threshold, threshold, getAlpha());
        }
        redValue = redValue > 0 && redValue < threshold ? threshold : redValue;
        greenValue = greenValue > 0 && greenValue < threshold ? threshold : greenValue;
        blueValue = blueValue > 0 && blueValue < threshold ? threshold : blueValue;
        return new Color(Math.min((int) (redValue / FACTOR), 255), Math.min((int) (greenValue / FACTOR), 255), Math.min((int) (blueValue / FACTOR), 255), getAlpha());
    }

    public Color darker() {
        return new Color(Math.max((int) (getRed() * FACTOR), 0), Math.max((int) (getGreen() * FACTOR), 0), Math.max((int) (getBlue() * FACTOR), 0), getAlpha());
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Color && ((Color) object).getRGB() == value;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
    }

    public static Color decode(String value) throws NumberFormatException {
        return new Color(Integer.decode(value));
    }

    public static Color getColor(String propertyName) {
        return getColor(propertyName, (Color) null);
    }

    public static Color getColor(String propertyName, Color defaultColor) {
        return defaultColor;
    }

    public static Color getColor(String propertyName, int defaultRgb) {
        Color color = getColor(propertyName);
        return color == null ? new Color(defaultRgb) : color;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        if (saturation == 0.0f) {
            int gray = toByte(brightness);
            return 0xff000000 | gray << 16 | gray << 8 | gray;
        }
        float normalizedHue = hue - (float) Math.floor(hue);
        float sectorPosition = normalizedHue * 6.0f;
        int sector = (int) sectorPosition;
        float fraction = sectorPosition - sector;
        float first = brightness * (1.0f - saturation);
        float second = brightness * (1.0f - saturation * fraction);
        float third = brightness * (1.0f - saturation * (1.0f - fraction));
        float redValue;
        float greenValue;
        float blueValue;
        switch (sector) {
            case 0:
                redValue = brightness;
                greenValue = third;
                blueValue = first;
                break;
            case 1:
                redValue = second;
                greenValue = brightness;
                blueValue = first;
                break;
            case 2:
                redValue = first;
                greenValue = brightness;
                blueValue = third;
                break;
            case 3:
                redValue = first;
                greenValue = second;
                blueValue = brightness;
                break;
            case 4:
                redValue = third;
                greenValue = first;
                blueValue = brightness;
                break;
            default:
                redValue = brightness;
                greenValue = first;
                blueValue = second;
                break;
        }
        return 0xff000000 | toByte(redValue) << 16 | toByte(greenValue) << 8 | toByte(blueValue);
    }

    public static float[] RGBtoHSB(int red, int green, int blue, float[] hsbValues) {
        float[] result = hsbValues == null ? new float[3] : hsbValues;
        int maximum = Math.max(red, Math.max(green, blue));
        int minimum = Math.min(red, Math.min(green, blue));
        result[2] = maximum / 255.0f;
        result[1] = maximum == 0 ? 0.0f : (maximum - minimum) / (float) maximum;
        if (result[1] == 0.0f) {
            result[0] = 0.0f;
            return result;
        }
        float redDistance = (maximum - red) / (float) (maximum - minimum);
        float greenDistance = (maximum - green) / (float) (maximum - minimum);
        float blueDistance = (maximum - blue) / (float) (maximum - minimum);
        float hue = red == maximum ? blueDistance - greenDistance : green == maximum ? 2.0f + redDistance - blueDistance : 4.0f + greenDistance - redDistance;
        hue /= 6.0f;
        result[0] = hue < 0.0f ? hue + 1.0f : hue;
        return result;
    }

    public static Color getHSBColor(float hue, float saturation, float brightness) {
        return new Color(HSBtoRGB(hue, saturation, brightness));
    }

    public float[] getRGBComponents(float[] components) {
        float[] result = prepare(components, 4);
        float[] rgb = standardRgbComponents();
        result[0] = rgb[0];
        result[1] = rgb[1];
        result[2] = rgb[2];
        result[3] = alphaComponent();
        return result;
    }

    public float[] getRGBColorComponents(float[] components) {
        float[] result = prepare(components, 3);
        float[] rgb = standardRgbComponents();
        result[0] = rgb[0];
        result[1] = rgb[1];
        result[2] = rgb[2];
        return result;
    }

    public float[] getComponents(float[] components) {
        float[] colors = nativeComponents();
        float[] result = prepare(components, colors.length + 1);
        System.arraycopy(colors, 0, result, 0, colors.length);
        result[colors.length] = alphaComponent();
        return result;
    }

    public float[] getColorComponents(float[] components) {
        float[] colors = nativeComponents();
        float[] result = prepare(components, colors.length);
        System.arraycopy(colors, 0, result, 0, colors.length);
        return result;
    }

    public float[] getComponents(ColorSpace colorSpace, float[] components) {
        float[] colors = convertedComponents(colorSpace);
        float[] result = prepare(components, colors.length + 1);
        System.arraycopy(colors, 0, result, 0, colors.length);
        result[colors.length] = alphaComponent();
        return result;
    }

    public float[] getColorComponents(ColorSpace colorSpace, float[] components) {
        float[] colors = convertedComponents(colorSpace);
        float[] result = prepare(components, colors.length);
        System.arraycopy(colors, 0, result, 0, colors.length);
        return result;
    }

    public ColorSpace getColorSpace() {
        return cs == null ? SRGB : cs;
    }

    @Override
    public synchronized PaintContext createContext(ColorModel colorModel, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        return new SolidColorPaintContext(value);
    }

    @Override
    public int getTransparency() {
        int alpha = getAlpha();
        return alpha == 255 ? OPAQUE : alpha == 0 ? BITMASK : TRANSLUCENT;
    }

    private float[] nativeComponents() {
        return fvalue == null ? standardRgbComponents() : fvalue;
    }

    private float[] standardRgbComponents() {
        if (frgbvalue != null) {
            return frgbvalue;
        }
        return new float[] {getRed() / 255.0f, getGreen() / 255.0f, getBlue() / 255.0f};
    }

    private float alphaComponent() {
        return frgbvalue == null && fvalue == null ? getAlpha() / 255.0f : falpha;
    }

    private float[] convertedComponents(ColorSpace colorSpace) {
        if (colorSpace == null) {
            throw new NullPointerException("colorSpace");
        }
        return colorSpace.fromCIEXYZ(getColorSpace().toCIEXYZ(nativeComponents()));
    }

    private static float[] prepare(float[] components, int required) {
        if (components == null) {
            return new float[required];
        }
        if (components.length < required) {
            throw new IllegalArgumentException("Component array must contain at least " + required + " values");
        }
        return components;
    }

    private static int toByte(float value) {
        return (int) (value * 255.0f + 0.5f);
    }

    private static void checkByte(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " component outside expected range: " + value);
        }
    }

    private static void checkUnit(float value, String name) {
        if (value < 0.0f || value > 1.0f) {
            throw new IllegalArgumentException(name + " component outside expected range: " + value);
        }
    }

    private static final class SolidColorPaintContext implements PaintContext {
        private final int rgb;
        private final ColorModel colorModel = ColorModel.getRGBdefault();

        private SolidColorPaintContext(int rgb) {
            this.rgb = rgb;
        }

        @Override
        public void dispose() {
        }

        @Override
        public ColorModel getColorModel() {
            return colorModel;
        }

        @Override
        public Raster getRaster(int x, int y, int width, int height) {
            WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
            Object pixel = colorModel.getDataElements(rgb, null);
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    raster.setDataElements(column, row, pixel);
                }
            }
            return raster;
        }
    }
}
