package java.awt.image;

import java.util.Arrays;

public abstract class DataBuffer {
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_USHORT = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_FLOAT = 4;
    public static final int TYPE_DOUBLE = 5;
    public static final int TYPE_UNDEFINED = 32;
    protected int dataType;
    protected int banks;
    protected int offset;
    protected int size;
    protected int[] offsets;

    public static int getDataTypeSize(int type) {
        if (type == TYPE_BYTE) {
            return 8;
        }
        if (type == TYPE_USHORT || type == TYPE_SHORT) {
            return 16;
        }
        if (type == TYPE_INT || type == TYPE_FLOAT) {
            return 32;
        }
        if (type == TYPE_DOUBLE) {
            return 64;
        }
        throw new IllegalArgumentException("Unknown data type " + type);
    }

    protected DataBuffer(int dataType, int size) {
        this(dataType, size, 1, 0);
    }

    protected DataBuffer(int dataType, int size, int banks) {
        this(dataType, size, banks, 0);
    }

    protected DataBuffer(int dataType, int size, int banks, int offset) {
        this.dataType = dataType;
        this.size = size;
        this.banks = banks;
        this.offset = offset;
        offsets = new int[banks];
        Arrays.fill(offsets, offset);
    }

    protected DataBuffer(int dataType, int size, int banks, int[] offsets) {
        if (banks != offsets.length) {
            throw new ArrayIndexOutOfBoundsException("Number of banks does not match number of bank offsets");
        }
        this.dataType = dataType;
        this.size = size;
        this.banks = banks;
        this.offsets = Arrays.copyOf(offsets, offsets.length);
        offset = offsets[0];
    }

    public int getDataType() {
        return dataType;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    public int[] getOffsets() {
        return Arrays.copyOf(offsets, offsets.length);
    }

    public int getNumBanks() {
        return banks;
    }

    public int getElem(int index) {
        return getElem(0, index);
    }

    public abstract int getElem(int bank, int index);

    public void setElem(int index, int value) {
        setElem(0, index, value);
    }

    public abstract void setElem(int bank, int index, int value);

    public float getElemFloat(int index) {
        return getElem(index);
    }

    public float getElemFloat(int bank, int index) {
        return getElem(bank, index);
    }

    public void setElemFloat(int index, float value) {
        setElem(index, (int) value);
    }

    public void setElemFloat(int bank, int index, float value) {
        setElem(bank, index, (int) value);
    }

    public double getElemDouble(int index) {
        return getElem(index);
    }

    public double getElemDouble(int bank, int index) {
        return getElem(bank, index);
    }

    public void setElemDouble(int index, double value) {
        setElem(index, (int) value);
    }

    public void setElemDouble(int bank, int index, double value) {
        setElem(bank, index, (int) value);
    }
}
