package java.awt.image;

public final class DataBufferUShort extends DataBuffer {
    private final short[] data;
    private final short[][] bankData;

    public DataBufferUShort(int size) {
        super(TYPE_USHORT, size);
        data = new short[size];
        bankData = new short[][] {data};
    }

    public DataBufferUShort(int size, int banks) {
        super(TYPE_USHORT, size, banks);
        bankData = new short[banks][size];
        data = bankData[0];
    }

    public DataBufferUShort(short[] dataArray, int size) {
        super(TYPE_USHORT, size);
        data = dataArray;
        bankData = new short[][] {data};
    }

    public DataBufferUShort(short[] dataArray, int size, int offset) {
        super(TYPE_USHORT, size, 1, offset);
        data = dataArray;
        bankData = new short[][] {data};
    }

    public DataBufferUShort(short[][] dataArray, int size) {
        super(TYPE_USHORT, size, dataArray.length);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public DataBufferUShort(short[][] dataArray, int size, int[] offsets) {
        super(TYPE_USHORT, size, dataArray.length, offsets);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public short[] getData() {
        return data;
    }

    public short[] getData(int bank) {
        return bankData[bank];
    }

    public short[][] getBankData() {
        return java.util.Arrays.copyOf(bankData, bankData.length);
    }

    @Override
    public int getElem(int index) {
        return data[index + offset] & 0xffff;
    }

    @Override
    public int getElem(int bank, int index) {
        return bankData[bank][index + offsets[bank]] & 0xffff;
    }

    @Override
    public void setElem(int index, int value) {
        data[index + offset] = (short) value;
    }

    @Override
    public void setElem(int bank, int index, int value) {
        bankData[bank][index + offsets[bank]] = (short) value;
    }
}
