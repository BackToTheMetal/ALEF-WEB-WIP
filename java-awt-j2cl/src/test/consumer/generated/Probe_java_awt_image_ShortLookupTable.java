package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ShortLookupTable {
    static void call0()  { java.awt.image.ShortLookupTable value = new java.awt.image.ShortLookupTable((int) 0, (short[][]) null); }
    static void call1()  { java.awt.image.ShortLookupTable value = new java.awt.image.ShortLookupTable((int) 0, (short[]) null); }
    static void call2(java.awt.image.ShortLookupTable receiver)  { short[][] value = receiver.getTable(); }
    static void call3(java.awt.image.ShortLookupTable receiver)  { int[] value = receiver.lookupPixel((int[]) null, (int[]) null); }
    static void call4(java.awt.image.ShortLookupTable receiver)  { short[] value = receiver.lookupPixel((short[]) null, (short[]) null); }
}

abstract class Override_java_awt_image_ShortLookupTable extends java.awt.image.ShortLookupTable {
    Override_java_awt_image_ShortLookupTable()  { super((int) 0, (short[][]) null); }
    @Override
    public int[] lookupPixel(int[] arg0, int[] arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public short[] lookupPixel(short[] arg0, short[] arg1)  { throw new UnsupportedOperationException(); }
}
