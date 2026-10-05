package java.awt.image;

public class ShortLookupTable extends LookupTable {
    private final short[][] data;

    public ShortLookupTable(int offset, short[][] data) {
        super(offset, data.length);
        numEntries = data[0].length;
        this.data = new short[numComponents][];
        System.arraycopy(data, 0, this.data, 0, numComponents);
    }

    public ShortLookupTable(int offset, short[] data) {
        super(offset, data.length);
        numComponents = 1;
        numEntries = data.length;
        this.data = new short[][] {data};
    }

    public final short[][] getTable() {
        return data;
    }

    @Override
    public int[] lookupPixel(int[] source, int[] destination) {
        int[] result = destination == null ? new int[source.length] : destination;
        validateLengths(source.length, result.length);
        for (int index = 0; index < source.length; index++) {
            int tableIndex = (source[index] & 0xffff) - offset;
            validateTableIndex(index, tableIndex);
            result[index] = data[numComponents == 1 ? 0 : index][tableIndex];
        }
        return result;
    }

    public short[] lookupPixel(short[] source, short[] destination) {
        short[] result = destination == null ? new short[source.length] : destination;
        validateLengths(source.length, result.length);
        for (int index = 0; index < source.length; index++) {
            int tableIndex = (source[index] & 0xffff) - offset;
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
        short[] table = data[numComponents == 1 ? 0 : sourceIndex];
        if (tableIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("src[" + sourceIndex + "]-offset is less than zero");
        }
        if (tableIndex >= table.length) {
            throw new ArrayIndexOutOfBoundsException(tableIndex);
        }
    }
}
