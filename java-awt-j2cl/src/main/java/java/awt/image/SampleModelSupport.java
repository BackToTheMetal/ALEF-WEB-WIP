package java.awt.image;

final class SampleModelSupport {
    private SampleModelSupport() {
    }

    static Object createArray(int type, int length) {
        if (type == DataBuffer.TYPE_BYTE) {
            return new byte[length];
        }
        if (type == DataBuffer.TYPE_USHORT || type == DataBuffer.TYPE_SHORT) {
            return new short[length];
        }
        if (type == DataBuffer.TYPE_INT) {
            return new int[length];
        }
        if (type == DataBuffer.TYPE_FLOAT) {
            return new float[length];
        }
        if (type == DataBuffer.TYPE_DOUBLE) {
            return new double[length];
        }
        throw new IllegalArgumentException("Unsupported data type " + type);
    }

    static int length(Object values, int type) {
        if (type == DataBuffer.TYPE_BYTE) {
            return ((byte[]) values).length;
        }
        if (type == DataBuffer.TYPE_USHORT || type == DataBuffer.TYPE_SHORT) {
            return ((short[]) values).length;
        }
        if (type == DataBuffer.TYPE_INT) {
            return ((int[]) values).length;
        }
        if (type == DataBuffer.TYPE_FLOAT) {
            return ((float[]) values).length;
        }
        return ((double[]) values).length;
    }

    static double get(Object values, int type, int index) {
        if (type == DataBuffer.TYPE_BYTE) {
            return ((byte[]) values)[index] & 0xff;
        }
        if (type == DataBuffer.TYPE_USHORT) {
            return ((short[]) values)[index] & 0xffff;
        }
        if (type == DataBuffer.TYPE_SHORT) {
            return ((short[]) values)[index];
        }
        if (type == DataBuffer.TYPE_INT) {
            return ((int[]) values)[index];
        }
        if (type == DataBuffer.TYPE_FLOAT) {
            return ((float[]) values)[index];
        }
        return ((double[]) values)[index];
    }

    static void set(Object values, int type, int index, double value) {
        if (type == DataBuffer.TYPE_BYTE) {
            ((byte[]) values)[index] = (byte) value;
        } else if (type == DataBuffer.TYPE_USHORT || type == DataBuffer.TYPE_SHORT) {
            ((short[]) values)[index] = (short) value;
        } else if (type == DataBuffer.TYPE_INT) {
            ((int[]) values)[index] = (int) value;
        } else if (type == DataBuffer.TYPE_FLOAT) {
            ((float[]) values)[index] = (float) value;
        } else {
            ((double[]) values)[index] = value;
        }
    }

    static DataBuffer createBuffer(int type, int size, int banks) {
        if (type == DataBuffer.TYPE_BYTE) {
            return new DataBufferByte(size, banks);
        }
        if (type == DataBuffer.TYPE_USHORT) {
            return new DataBufferUShort(size, banks);
        }
        if (type == DataBuffer.TYPE_SHORT) {
            return new DataBufferShort(size, banks);
        }
        if (type == DataBuffer.TYPE_INT) {
            return new DataBufferInt(size, banks);
        }
        if (type == DataBuffer.TYPE_FLOAT) {
            return new DataBufferFloat(size, banks);
        }
        if (type == DataBuffer.TYPE_DOUBLE) {
            return new DataBufferDouble(size, banks);
        }
        throw new IllegalArgumentException("Unsupported data type " + type);
    }
}
