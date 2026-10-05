package java.awt.color;

public class ICC_ProfileRGB extends ICC_Profile {
    public static final int REDCOMPONENT = 0;
    public static final int GREENCOMPONENT = 1;
    public static final int BLUECOMPONENT = 2;
    private static final float[] D65_WHITE_POINT = {0.95015f, 1.0f, 1.08826f};
    private static final float[][] SRGB_MATRIX = {{0.4360747f, 0.3850649f, 0.1430804f}, {0.2225045f, 0.7168786f, 0.0606169f}, {0.0139322f, 0.0971045f, 0.7141733f}};

    ICC_ProfileRGB(byte[] data, int standardColorSpace) {
        super(data, standardColorSpace);
    }

    public float[] getMediaWhitePoint() {
        return xyzTagOrDefault(icSigMediaWhitePointTag, D65_WHITE_POINT);
    }

    public float[][] getMatrix() {
        float[] red = xyzTagOrDefault(icSigRedColorantTag, column(SRGB_MATRIX, REDCOMPONENT));
        float[] green = xyzTagOrDefault(icSigGreenColorantTag, column(SRGB_MATRIX, GREENCOMPONENT));
        float[] blue = xyzTagOrDefault(icSigBlueColorantTag, column(SRGB_MATRIX, BLUECOMPONENT));
        return new float[][] {{red[0], green[0], blue[0]}, {red[1], green[1], blue[1]}, {red[2], green[2], blue[2]}};
    }

    public float getGamma(int component) {
        checkComponent(component);
        if (getStandardColorSpace() == ColorSpace.CS_LINEAR_RGB) {
            return 1.0f;
        }
        byte[] curve = getData(curveTag(component));
        if (curve == null || readInt(curve, icCurveCount) != 1) {
            throw new ProfileDataException("TRC is not represented by a single gamma value");
        }
        return readUnsignedShort(curve, icCurveData) / 256.0f;
    }

    public short[] getTRC(int component) {
        checkComponent(component);
        byte[] curve = getData(curveTag(component));
        if (curve != null) {
            int count = readInt(curve, icCurveCount);
            if (count <= 1) {
                throw new ProfileDataException("TRC is represented by a gamma value");
            }
            short[] result = new short[count];
            for (int index = 0; index < count; index++) {
                result[index] = (short) readUnsignedShort(curve, icCurveData + index * 2);
            }
            return result;
        }
        if (getStandardColorSpace() == ColorSpace.CS_LINEAR_RGB) {
            throw new ProfileDataException("Linear RGB uses a gamma value");
        }
        short[] result = new short[256];
        for (int index = 0; index < result.length; index++) {
            double encoded = index / 255.0;
            double linear = encoded <= 0.04045 ? encoded / 12.92 : Math.pow((encoded + 0.055) / 1.055, 2.4);
            result[index] = (short) Math.round(linear * 65535.0);
        }
        return result;
    }

    private float[] xyzTagOrDefault(int tag, float[] fallback) {
        byte[] value = getData(tag);
        if (value == null || value.length < 20) {
            return copy(fallback);
        }
        return new float[] {readFixed(value, icXYZNumberX), readFixed(value, icXYZNumberX + 4), readFixed(value, icXYZNumberX + 8)};
    }

    private static int curveTag(int component) {
        if (component == REDCOMPONENT) {
            return icSigRedTRCTag;
        }
        return component == GREENCOMPONENT ? icSigGreenTRCTag : icSigBlueTRCTag;
    }

    private static void checkComponent(int component) {
        if (component < REDCOMPONENT || component > BLUECOMPONENT) {
            throw new IllegalArgumentException("Component index out of range: " + component);
        }
    }

    private static float[] column(float[][] matrix, int component) {
        return new float[] {matrix[0][component], matrix[1][component], matrix[2][component]};
    }

    private static float readFixed(byte[] data, int offset) {
        return readInt(data, offset) / 65536.0f;
    }

    private static int readInt(byte[] data, int offset) {
        if (offset < 0 || offset + 4 > data.length) {
            throw new ProfileDataException("ICC tag is truncated");
        }
        return (data[offset] & 0xff) << 24 | (data[offset + 1] & 0xff) << 16 | (data[offset + 2] & 0xff) << 8 | data[offset + 3] & 0xff;
    }

    private static int readUnsignedShort(byte[] data, int offset) {
        if (offset < 0 || offset + 2 > data.length) {
            throw new ProfileDataException("ICC curve is truncated");
        }
        return (data[offset] & 0xff) << 8 | data[offset + 1] & 0xff;
    }

    private static float[] copy(float[] values) {
        float[] result = new float[values.length];
        System.arraycopy(values, 0, result, 0, values.length);
        return result;
    }
}
