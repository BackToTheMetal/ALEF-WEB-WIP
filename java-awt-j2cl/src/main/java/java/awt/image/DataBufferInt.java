package java.awt.image;

public final class DataBufferInt extends DataBuffer {
    private final int[] data;
    private final int[][] bankData;

    public DataBufferInt(int size) {
        super(TYPE_INT, size);
        data = new int[size];
        bankData = new int[][] {data};
    }

    public DataBufferInt(int size, int banks) {
        super(TYPE_INT, size, banks);
        bankData = new int[banks][size];
        data = bankData[0];
    }

    public DataBufferInt(int[] dataArray, int size) {
        super(TYPE_INT, size);
        data = dataArray;
        bankData = new int[][] {data};
    }

    public DataBufferInt(int[] dataArray, int size, int offset) {
        super(TYPE_INT, size, 1, offset);
        data = dataArray;
        bankData = new int[][] {data};
    }

    public DataBufferInt(int[][] dataArray, int size) {
        super(TYPE_INT, size, dataArray.length);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public DataBufferInt(int[][] dataArray, int size, int[] offsets) {
        super(TYPE_INT, size, dataArray.length, offsets);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public int[] getData() {
        return data;
    }

    public int[] getData(int bank) {
        return bankData[bank];
    }

    public int[][] getBankData() {
        return java.util.Arrays.copyOf(bankData, bankData.length);
    }

    @Override
    public int getElem(int index) {
        return data[index + offset];
    }

    @Override
    public int getElem(int bank, int index) {
        return bankData[bank][index + offsets[bank]];
    }

    @Override
    public void setElem(int index, int value) {
        data[index + offset] = value;
    }

    @Override
    public void setElem(int bank, int index, int value) {
        bankData[bank][index + offsets[bank]] = value;
    }
}
