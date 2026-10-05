package java.awt.image;

public final class DataBufferFloat extends DataBuffer {
    private final float[] data;
    private final float[][] bankData;

    public DataBufferFloat(int size) {
        super(TYPE_FLOAT, size);
        data = new float[size];
        bankData = new float[][] {data};
    }

    public DataBufferFloat(int size, int banks) {
        super(TYPE_FLOAT, size, banks);
        bankData = new float[banks][size];
        data = bankData[0];
    }

    public DataBufferFloat(float[] dataArray, int size) {
        super(TYPE_FLOAT, size);
        data = dataArray;
        bankData = new float[][] {data};
    }

    public DataBufferFloat(float[] dataArray, int size, int offset) {
        super(TYPE_FLOAT, size, 1, offset);
        data = dataArray;
        bankData = new float[][] {data};
    }

    public DataBufferFloat(float[][] dataArray, int size) {
        super(TYPE_FLOAT, size, dataArray.length);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public DataBufferFloat(float[][] dataArray, int size, int[] offsets) {
        super(TYPE_FLOAT, size, dataArray.length, offsets);
        bankData = java.util.Arrays.copyOf(dataArray, dataArray.length);
        data = bankData[0];
    }

    public float[] getData() {
        return data;
    }

    public float[] getData(int bank) {
        return bankData[bank];
    }

    public float[][] getBankData() {
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
        return data[index + offset];
    }

    @Override
    public float getElemFloat(int bank, int index) {
        return bankData[bank][index + offsets[bank]];
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
        data[index + offset] = (float) value;
    }

    @Override
    public void setElemDouble(int bank, int index, double value) {
        bankData[bank][index + offsets[bank]] = (float) value;
    }
}
