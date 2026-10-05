package java.awt.image;

public final class DataBufferByte extends DataBuffer {
    private final byte[] data;
    private final byte[][] bankData;

    public DataBufferByte(int size) {
        super(TYPE_BYTE, size);
        data = new byte[size];
        bankData = new byte[][] {data};
    }

    public DataBufferByte(int size, int banks) {
        super(TYPE_BYTE, size, banks);
        bankData = new byte[banks][size];
        data = bankData[0];
    }

    public DataBufferByte(byte[] dataArray, int size) {
        super(TYPE_BYTE, size);
        data = dataArray;
        bankData = new byte[][] {data};
    }

    public DataBufferByte(byte[] dataArray, int size, int offset) {
        super(TYPE_BYTE, size, 1, offset);
        data = dataArray;
        bankData = new byte[][] {data};
    }

    public DataBufferByte(byte[][] dataArray, int size) {
        super(TYPE_BYTE, size, dataArray.length);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public DataBufferByte(byte[][] dataArray, int size, int[] offsets) {
        super(TYPE_BYTE, size, dataArray.length, offsets);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getData(int bank) {
        return bankData[bank];
    }

    public byte[][] getBankData() {
        return java.util.Arrays.copyOf(bankData, bankData.length);
    }

    @Override
    public int getElem(int index) {
        return data[index + offset] & 0xff;
    }

    @Override
    public int getElem(int bank, int index) {
        return bankData[bank][index + offsets[bank]] & 0xff;
    }

    @Override
    public void setElem(int index, int value) {
        data[index + offset] = (byte) value;
    }

    @Override
    public void setElem(int bank, int index, int value) {
        bankData[bank][index + offsets[bank]] = (byte) value;
    }
}
