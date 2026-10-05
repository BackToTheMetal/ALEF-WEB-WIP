package java.awt.image;

public final class DataBufferDouble extends DataBuffer {
    private final double[] data;
    private final double[][] bankData;

    public DataBufferDouble(int size) {
        super(TYPE_DOUBLE, size);
        data = new double[size];
        bankData = new double[][] {data};
    }

    public DataBufferDouble(int size, int banks) {
        super(TYPE_DOUBLE, size, banks);
        bankData = new double[banks][size];
        data = bankData[0];
    }

    public DataBufferDouble(double[] dataArray, int size) {
        super(TYPE_DOUBLE, size);
        data = dataArray;
        bankData = new double[][] {data};
    }

    public DataBufferDouble(double[] dataArray, int size, int offset) {
        super(TYPE_DOUBLE, size, 1, offset);
        data = dataArray;
        bankData = new double[][] {data};
    }

    public DataBufferDouble(double[][] dataArray, int size) {
        super(TYPE_DOUBLE, size, dataArray.length);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public DataBufferDouble(double[][] dataArray, int size, int[] offsets) {
        super(TYPE_DOUBLE, size, dataArray.length, offsets);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public double[] getData() {
        return data;
    }

    public double[] getData(int bank) {
        return bankData[bank];
    }

    public double[][] getBankData() {
        return java.util.Arrays.copyOf(bankData, bankData.length);
    }

    @Override
    public int getElem(int index) {
        return (int) data[index + offset];
    }

    @Override
    public int getElem(int bank, int index) {
        return (int) bankData[bank][index + offsets[bank]];
    }

    @Override
    public void setElem(int index, int value) {
        data[index + offset] = value;
    }

    @Override
    public void setElem(int bank, int index, int value) {
        bankData[bank][index + offsets[bank]] = value;
    }

    @Override
    public float getElemFloat(int index) {
        return (float) data[index + offset];
    }

    @Override
    public float getElemFloat(int bank, int index) {
        return (float) bankData[bank][index + offsets[bank]];
    }

    @Override
    public void setElemFloat(int index, float value) {
        data[index + offset] = value;
    }

    @Override
    public void setElemFloat(int bank, int index, float value) {
        bankData[bank][index + offsets[bank]] = value;
    }

    @Override
    public double getElemDouble(int index) {
        return data[index + offset];
    }

    @Override
    public double getElemDouble(int bank, int index) {
        return bankData[bank][index + offsets[bank]];
    }

    @Override
    public void setElemDouble(int index, double value) {
        data[index + offset] = value;
    }

    @Override
    public void setElemDouble(int bank, int index, double value) {
        bankData[bank][index + offsets[bank]] = value;
    }
}
