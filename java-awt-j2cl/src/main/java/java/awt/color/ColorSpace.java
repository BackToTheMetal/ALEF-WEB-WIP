package java.awt.color;

import java.io.Serializable;

/**
 * Describes a color space and conversions to standard RGB and CIE XYZ.
 *
 * <p>The predefined spaces use deterministic mathematical conversions. They do not load or interpret ICC profiles, because the browser and the J2CL runtime do not provide the desktop JRE color management module. The {@link #CS_PYCC} implementation uses a normalized YCbCr-compatible approximation.</p>
 */
public abstract class ColorSpace implements Serializable {
    public static final int TYPE_XYZ = 0;
    public static final int TYPE_Lab = 1;
    public static final int TYPE_Luv = 2;
    public static final int TYPE_YCbCr = 3;
    public static final int TYPE_Yxy = 4;
    public static final int TYPE_RGB = 5;
    public static final int TYPE_GRAY = 6;
    public static final int TYPE_HSV = 7;
    public static final int TYPE_HLS = 8;
    public static final int TYPE_CMYK = 9;
    public static final int TYPE_CMY = 11;
    public static final int TYPE_2CLR = 12;
    public static final int TYPE_3CLR = 13;
    public static final int TYPE_4CLR = 14;
    public static final int TYPE_5CLR = 15;
    public static final int TYPE_6CLR = 16;
    public static final int TYPE_7CLR = 17;
    public static final int TYPE_8CLR = 18;
    public static final int TYPE_9CLR = 19;
    public static final int TYPE_ACLR = 20;
    public static final int TYPE_BCLR = 21;
    public static final int TYPE_CCLR = 22;
    public static final int TYPE_DCLR = 23;
    public static final int TYPE_ECLR = 24;
    public static final int TYPE_FCLR = 25;
    public static final int CS_sRGB = 1000;
    public static final int CS_CIEXYZ = 1001;
    public static final int CS_PYCC = 1002;
    public static final int CS_GRAY = 1003;
    public static final int CS_LINEAR_RGB = 1004;
    private static final long serialVersionUID = -409452704308689724L;
    private static final float MAX_XYZ = 1.9999695f;
    private static final ColorSpace SRGB = new RgbColorSpace(false);
    private static final ColorSpace LINEAR_RGB = new RgbColorSpace(true);
    private static final ColorSpace CIEXYZ = new XyzColorSpace();
    private static final ColorSpace PYCC = new PyccColorSpace();
    private static final ColorSpace GRAY = new GrayColorSpace();
    private final int type;
    private final int numComponents;

    protected ColorSpace(int type, int numComponents) {
        if (numComponents < 1) {
            throw new IllegalArgumentException("Number of components must be positive: " + numComponents);
        }
        this.type = type;
        this.numComponents = numComponents;
    }

    public static ColorSpace getInstance(int colorSpace) {
        switch (colorSpace) {
            case CS_sRGB:
                return SRGB;
            case CS_CIEXYZ:
                return CIEXYZ;
            case CS_PYCC:
                return PYCC;
            case CS_GRAY:
                return GRAY;
            case CS_LINEAR_RGB:
                return LINEAR_RGB;
            default:
                throw new IllegalArgumentException("Unknown color space: " + colorSpace);
        }
    }

    public boolean isCS_sRGB() {
        return this == SRGB;
    }

    public abstract float[] toRGB(float[] colorValue);

    public abstract float[] fromRGB(float[] rgbValue);

    public abstract float[] toCIEXYZ(float[] colorValue);

    public abstract float[] fromCIEXYZ(float[] colorValue);

    public int getType() {
        return type;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public String getName(int component) {
        checkComponent(component);
        String[] names = componentNames(type);
        return component < names.length ? names[component] : "Unnamed color component(" + component + ")";
    }

    public float getMinValue(int component) {
        checkComponent(component);
        return 0.0f;
    }

    public float getMaxValue(int component) {
        checkComponent(component);
        return 1.0f;
    }

    private void checkComponent(int component) {
        if (component < 0 || component >= numComponents) {
            throw new IllegalArgumentException("Component index out of range: " + component);
        }
    }

    private static String[] componentNames(int colorType) {
        switch (colorType) {
            case TYPE_XYZ:
                return new String[] {"X", "Y", "Z"};
            case TYPE_Lab:
                return new String[] {"L", "a", "b"};
            case TYPE_Luv:
                return new String[] {"L", "u", "v"};
            case TYPE_YCbCr:
                return new String[] {"Y", "Cb", "Cr"};
            case TYPE_Yxy:
                return new String[] {"Y", "x", "y"};
            case TYPE_RGB:
                return new String[] {"Red", "Green", "Blue"};
            case TYPE_GRAY:
                return new String[] {"Gray"};
            case TYPE_HSV:
                return new String[] {"Hue", "Saturation", "Value"};
            case TYPE_HLS:
                return new String[] {"Hue", "Lightness", "Saturation"};
            case TYPE_CMYK:
                return new String[] {"Cyan", "Magenta", "Yellow", "Black"};
            case TYPE_CMY:
                return new String[] {"Cyan", "Magenta", "Yellow"};
            default:
                return new String[0];
        }
    }

    private static void requireComponents(float[] values, int required, String name) {
        if (values == null) {
            throw new NullPointerException(name);
        }
        if (values.length < required) {
            throw new IllegalArgumentException(name + " must contain at least " + required + " components");
        }
    }

    private static float clamp(float value) {
        if (value <= 0.0f) {
            return 0.0f;
        }
        return value >= 1.0f ? 1.0f : value;
    }

    private static float clampXyz(float value) {
        if (value <= 0.0f) {
            return 0.0f;
        }
        return value >= MAX_XYZ ? MAX_XYZ : value;
    }

    private static double decodeSrgb(double value) {
        double bounded = clamp((float) value);
        return bounded <= 0.04045 ? bounded / 12.92 : Math.pow((bounded + 0.055) / 1.055, 2.4);
    }

    private static float encodeSrgb(double value) {
        double bounded = Math.max(0.0, Math.min(1.0, value));
        double encoded = bounded <= 0.0031308 ? 12.92 * bounded : 1.055 * Math.pow(bounded, 1.0 / 2.4) - 0.055;
        return clamp((float) encoded);
    }

    private static float[] linearRgbToXyz(double red, double green, double blue) {
        double d65X = 0.4124564 * red + 0.3575761 * green + 0.1804375 * blue;
        double d65Y = 0.2126729 * red + 0.7151522 * green + 0.0721750 * blue;
        double d65Z = 0.0193339 * red + 0.1191920 * green + 0.9503041 * blue;
        float x = clampXyz((float) (1.0478112 * d65X + 0.0228866 * d65Y - 0.0501270 * d65Z));
        float y = clampXyz((float) (0.0295424 * d65X + 0.9904844 * d65Y - 0.0170491 * d65Z));
        float z = clampXyz((float) (-0.0092345 * d65X + 0.0150436 * d65Y + 0.7521316 * d65Z));
        return new float[] {x, y, z};
    }

    private static float[] xyzToLinearRgb(float[] xyzValue) {
        requireComponents(xyzValue, 3, "xyzValue");
        double x = xyzValue[0];
        double y = xyzValue[1];
        double z = xyzValue[2];
        double d65X = 0.9555766 * x - 0.0230393 * y + 0.0631636 * z;
        double d65Y = -0.0282895 * x + 1.0099416 * y + 0.0210077 * z;
        double d65Z = 0.0122982 * x - 0.0204830 * y + 1.3299098 * z;
        float red = clamp((float) (3.2404542 * d65X - 1.5371385 * d65Y - 0.4985314 * d65Z));
        float green = clamp((float) (-0.9692660 * d65X + 1.8760108 * d65Y + 0.0415560 * d65Z));
        float blue = clamp((float) (0.0556434 * d65X - 0.2040259 * d65Y + 1.0572252 * d65Z));
        return new float[] {red, green, blue};
    }

    private static final class RgbColorSpace extends ColorSpace {
        private static final long serialVersionUID = 1L;
        private final boolean linear;

        private RgbColorSpace(boolean linear) {
            super(TYPE_RGB, 3);
            this.linear = linear;
        }

        @Override
        public float[] toRGB(float[] colorValue) {
            requireComponents(colorValue, 3, "colorValue");
            if (!linear) {
                return new float[] {clamp(colorValue[0]), clamp(colorValue[1]), clamp(colorValue[2])};
            }
            return new float[] {encodeSrgb(colorValue[0]), encodeSrgb(colorValue[1]), encodeSrgb(colorValue[2])};
        }

        @Override
        public float[] fromRGB(float[] rgbValue) {
            requireComponents(rgbValue, 3, "rgbValue");
            if (!linear) {
                return new float[] {clamp(rgbValue[0]), clamp(rgbValue[1]), clamp(rgbValue[2])};
            }
            return new float[] {(float) decodeSrgb(rgbValue[0]), (float) decodeSrgb(rgbValue[1]), (float) decodeSrgb(rgbValue[2])};
        }

        @Override
        public float[] toCIEXYZ(float[] colorValue) {
            requireComponents(colorValue, 3, "colorValue");
            if (linear) {
                return linearRgbToXyz(clamp(colorValue[0]), clamp(colorValue[1]), clamp(colorValue[2]));
            }
            return linearRgbToXyz(decodeSrgb(colorValue[0]), decodeSrgb(colorValue[1]), decodeSrgb(colorValue[2]));
        }

        @Override
        public float[] fromCIEXYZ(float[] colorValue) {
            float[] rgb = xyzToLinearRgb(colorValue);
            if (linear) {
                return rgb;
            }
            return new float[] {encodeSrgb(rgb[0]), encodeSrgb(rgb[1]), encodeSrgb(rgb[2])};
        }
    }

    private static final class XyzColorSpace extends ColorSpace {
        private static final long serialVersionUID = 1L;

        private XyzColorSpace() {
            super(TYPE_XYZ, 3);
        }

        @Override
        public float[] toRGB(float[] colorValue) {
            float[] rgb = xyzToLinearRgb(colorValue);
            return new float[] {encodeSrgb(rgb[0]), encodeSrgb(rgb[1]), encodeSrgb(rgb[2])};
        }

        @Override
        public float[] fromRGB(float[] rgbValue) {
            requireComponents(rgbValue, 3, "rgbValue");
            return linearRgbToXyz(decodeSrgb(rgbValue[0]), decodeSrgb(rgbValue[1]), decodeSrgb(rgbValue[2]));
        }

        @Override
        public float[] toCIEXYZ(float[] colorValue) {
            requireComponents(colorValue, 3, "colorValue");
            return new float[] {clampXyz(colorValue[0]), clampXyz(colorValue[1]), clampXyz(colorValue[2])};
        }

        @Override
        public float[] fromCIEXYZ(float[] colorValue) {
            return toCIEXYZ(colorValue);
        }

        @Override
        public float getMaxValue(int component) {
            super.getMaxValue(component);
            return MAX_XYZ;
        }
    }

    private static final class GrayColorSpace extends ColorSpace {
        private static final long serialVersionUID = 1L;

        private GrayColorSpace() {
            super(TYPE_GRAY, 1);
        }

        @Override
        public float[] toRGB(float[] colorValue) {
            requireComponents(colorValue, 1, "colorValue");
            float gray = clamp(colorValue[0]);
            return new float[] {gray, gray, gray};
        }

        @Override
        public float[] fromRGB(float[] rgbValue) {
            requireComponents(rgbValue, 3, "rgbValue");
            double luminance = 0.2126729 * decodeSrgb(rgbValue[0]) + 0.7151522 * decodeSrgb(rgbValue[1]) + 0.0721750 * decodeSrgb(rgbValue[2]);
            return new float[] {encodeSrgb(luminance)};
        }

        @Override
        public float[] toCIEXYZ(float[] colorValue) {
            float[] rgb = toRGB(colorValue);
            return linearRgbToXyz(decodeSrgb(rgb[0]), decodeSrgb(rgb[1]), decodeSrgb(rgb[2]));
        }

        @Override
        public float[] fromCIEXYZ(float[] colorValue) {
            return fromRGB(CIEXYZ.toRGB(colorValue));
        }
    }

    private static final class PyccColorSpace extends ColorSpace {
        private static final long serialVersionUID = 1L;

        private PyccColorSpace() {
            super(TYPE_3CLR, 3);
        }

        @Override
        public float[] toRGB(float[] colorValue) {
            requireComponents(colorValue, 3, "colorValue");
            double luminance = clamp(colorValue[0]);
            double blueDifference = clamp(colorValue[1]) - 0.5;
            double redDifference = clamp(colorValue[2]) - 0.5;
            float red = clamp((float) (luminance + 1.402 * redDifference));
            float green = clamp((float) (luminance - 0.344136 * blueDifference - 0.714136 * redDifference));
            float blue = clamp((float) (luminance + 1.772 * blueDifference));
            return new float[] {red, green, blue};
        }

        @Override
        public float[] fromRGB(float[] rgbValue) {
            requireComponents(rgbValue, 3, "rgbValue");
            double red = clamp(rgbValue[0]);
            double green = clamp(rgbValue[1]);
            double blue = clamp(rgbValue[2]);
            double luminance = 0.299 * red + 0.587 * green + 0.114 * blue;
            float blueDifference = clamp((float) ((blue - luminance) / 1.772 + 0.5));
            float redDifference = clamp((float) ((red - luminance) / 1.402 + 0.5));
            return new float[] {clamp((float) luminance), blueDifference, redDifference};
        }

        @Override
        public float[] toCIEXYZ(float[] colorValue) {
            return SRGB.toCIEXYZ(toRGB(colorValue));
        }

        @Override
        public float[] fromCIEXYZ(float[] colorValue) {
            return fromRGB(CIEXYZ.toRGB(colorValue));
        }
    }
}
