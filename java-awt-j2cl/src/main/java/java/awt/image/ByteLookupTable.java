package java.awt.image;

public class ByteLookupTable extends LookupTable {
    private final byte[][] data;

    public ByteLookupTable(int offset, byte[][] data) {
        super(offset, data.length);
        numEntries = data[0].length;
        this.data = new byte[numComponents][];
        System.arraycopy(data, 0, this.data, 0, numComponents);
    }

    public ByteLookupTable(int offset, byte[] data) {
        super(offset, data.length);
        numComponents = 1;
        numEntries = data.length;
        this.data = new byte[][] {data};
    }

    public final byte[][] getTable() {
        return data;
    }

    @Override
    public int[] lookupPixel(int[] source, int[] destination) {
        int[] result = destination == null ? new int[source.length] : destination;
        validateLengths(source.length, result.length);
        for (int index = 0; index < source.length; index++) {
            int tableIndex = source[index] - offset;
            validateTableIndex(index, tableIndex);
            result[index] = data[numComponents == 1 ? 0 : index][tableIndex];
        }
        return result;
    }

    public byte[] lookupPixel(byte[] source, byte[] destination) {
        byte[] result = destination == null ? new byte[source.length] : destination;
        validateLengths(source.length, result.length);
        for (int index = 0; index < source.length; index++) {
            int tableIndex = (source[index] & 0xff) - offset;
            validateTableIndex(index, tableIndex);
            result[index] = data[numComponents == 1 ? 0 : index][tableIndex];
        }
        return result;
    }

    private void validateLengths(int sourceLength, int destinationLength) {
        if (destinationLength < sourceLength || (numComponents != 1 && numComponents < sourceLength)) {
            throw new ArrayIndexOutOfBoundsException("Lookup array is too small");
        }
    }

    private void validateTableIndex(int sourceIndex, int tableIndex) {
        byte[] table = data[numComponents == 1 ? 0 : sourceIndex];
        if (tableIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("src[" + sourceIndex + "]-offset is less than zero");
        }
        if (tableIndex >= table.length) {
            throw new ArrayIndexOutOfBoundsException(tableIndex);
        }
    }
}
