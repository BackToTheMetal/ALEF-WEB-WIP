package java.awt.color;

public class ICC_ProfileGray extends ICC_Profile {
    private static final float[] D50_WHITE_POINT = {0.9642f, 1.0f, 0.8249f};

    ICC_ProfileGray(byte[] data, int standardColorSpace) {
        super(data, standardColorSpace);
    }

    public float[] getMediaWhitePoint() {
        byte[] value = getData(icSigMediaWhitePointTag);
        if (value == null || value.length < 20) {
            return new float[] {D50_WHITE_POINT[0], D50_WHITE_POINT[1], D50_WHITE_POINT[2]};
        }
        return new float[] {readFixed(value, icXYZNumberX), readFixed(value, icXYZNumberX + 4), readFixed(value, icXYZNumberX + 8)};
    }

    public float getGamma() {
        byte[] curve = getData(icSigGrayTRCTag);
        if (curve == null) {
            return 2.2f;
        }
        if (readInt(curve, icCurveCount) != 1) {
            throw new ProfileDataException("TRC is not represented by a single gamma value");
        }
        return readUnsignedShort(curve, icCurveData) / 256.0f;
    }

    public short[] getTRC() {
        byte[] curve = getData(icSigGrayTRCTag);
        if (curve == null) {
            throw new ProfileDataException("Gray profile uses a gamma value");
        }
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
}
