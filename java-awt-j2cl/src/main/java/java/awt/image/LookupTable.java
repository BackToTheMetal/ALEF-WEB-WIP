package java.awt.image;

public abstract class LookupTable {
    int numComponents;
    int numEntries;
    int offset;

    protected LookupTable(int offset, int numComponents) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than 0");
        }
        if (numComponents < 1) {
            throw new IllegalArgumentException("Number of components must be at least 1");
        }
        this.numComponents = numComponents;
        this.offset = offset;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public int getOffset() {
        return offset;
    }

    public abstract int[] lookupPixel(int[] source, int[] destination);
}
