package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ByteLookupTable {
    static void call0()  { java.awt.image.ByteLookupTable value = new java.awt.image.ByteLookupTable((int) 0, (byte[][]) null); }
    static void call1()  { java.awt.image.ByteLookupTable value = new java.awt.image.ByteLookupTable((int) 0, (byte[]) null); }
    static void call2(java.awt.image.ByteLookupTable receiver)  { byte[][] value = receiver.getTable(); }
    static void call3(java.awt.image.ByteLookupTable receiver)  { int[] value = receiver.lookupPixel((int[]) null, (int[]) null); }
    static void call4(java.awt.image.ByteLookupTable receiver)  { byte[] value = receiver.lookupPixel((byte[]) null, (byte[]) null); }
}

abstract class Override_java_awt_image_ByteLookupTable extends java.awt.image.ByteLookupTable {
    Override_java_awt_image_ByteLookupTable()  { super((int) 0, (byte[][]) null); }
    @Override
    public int[] lookupPixel(int[] arg0, int[] arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public byte[] lookupPixel(byte[] arg0, byte[] arg1)  { throw new UnsupportedOperationException(); }
}
