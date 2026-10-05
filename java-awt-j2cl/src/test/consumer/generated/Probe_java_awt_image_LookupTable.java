package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_LookupTable {
    static void call0(java.awt.image.LookupTable receiver)  { int value = receiver.getNumComponents(); }
    static void call1(java.awt.image.LookupTable receiver)  { int value = receiver.getOffset(); }
    static void call2(java.awt.image.LookupTable receiver)  { int[] value = receiver.lookupPixel((int[]) null, (int[]) null); }
}

abstract class Override_java_awt_image_LookupTable extends java.awt.image.LookupTable {
    Override_java_awt_image_LookupTable()  { super((int) 0, (int) 0); }
    @Override
    public int getNumComponents()  { throw new UnsupportedOperationException(); }
    @Override
    public int getOffset()  { throw new UnsupportedOperationException(); }
    @Override
    public int[] lookupPixel(int[] arg0, int[] arg1)  { throw new UnsupportedOperationException(); }
}
