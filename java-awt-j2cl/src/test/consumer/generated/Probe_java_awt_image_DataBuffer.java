package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_DataBuffer {
    static void call0() { int value = java.awt.image.DataBuffer.TYPE_BYTE; }
    static void call1() { int value = java.awt.image.DataBuffer.TYPE_USHORT; }
    static void call2() { int value = java.awt.image.DataBuffer.TYPE_SHORT; }
    static void call3() { int value = java.awt.image.DataBuffer.TYPE_INT; }
    static void call4() { int value = java.awt.image.DataBuffer.TYPE_FLOAT; }
    static void call5() { int value = java.awt.image.DataBuffer.TYPE_DOUBLE; }
    static void call6() { int value = java.awt.image.DataBuffer.TYPE_UNDEFINED; }
    static void call7(java.awt.image.DataBuffer receiver) { int value = receiver.dataType; receiver.dataType = value; }
    static void call8(java.awt.image.DataBuffer receiver) { int value = receiver.banks; receiver.banks = value; }
    static void call9(java.awt.image.DataBuffer receiver) { int value = receiver.offset; receiver.offset = value; }
    static void call10(java.awt.image.DataBuffer receiver) { int value = receiver.size; receiver.size = value; }
    static void call11(java.awt.image.DataBuffer receiver) { int[] value = receiver.offsets; receiver.offsets = value; }
    static void call12()  { int value = java.awt.image.DataBuffer.getDataTypeSize((int) 0); }
    static void call13(java.awt.image.DataBuffer receiver)  { int value = receiver.getDataType(); }
    static void call14(java.awt.image.DataBuffer receiver)  { int value = receiver.getSize(); }
    static void call15(java.awt.image.DataBuffer receiver)  { int value = receiver.getOffset(); }
    static void call16(java.awt.image.DataBuffer receiver)  { int[] value = receiver.getOffsets(); }
    static void call17(java.awt.image.DataBuffer receiver)  { int value = receiver.getNumBanks(); }
    static void call18(java.awt.image.DataBuffer receiver)  { int value = receiver.getElem((int) 0); }
    static void call19(java.awt.image.DataBuffer receiver)  { int value = receiver.getElem((int) 0, (int) 0); }
    static void call20(java.awt.image.DataBuffer receiver)  { receiver.setElem((int) 0, (int) 0); }
    static void call21(java.awt.image.DataBuffer receiver)  { receiver.setElem((int) 0, (int) 0, (int) 0); }
    static void call22(java.awt.image.DataBuffer receiver)  { float value = receiver.getElemFloat((int) 0); }
    static void call23(java.awt.image.DataBuffer receiver)  { float value = receiver.getElemFloat((int) 0, (int) 0); }
    static void call24(java.awt.image.DataBuffer receiver)  { receiver.setElemFloat((int) 0, (float) 0); }
    static void call25(java.awt.image.DataBuffer receiver)  { receiver.setElemFloat((int) 0, (int) 0, (float) 0); }
    static void call26(java.awt.image.DataBuffer receiver)  { double value = receiver.getElemDouble((int) 0); }
    static void call27(java.awt.image.DataBuffer receiver)  { double value = receiver.getElemDouble((int) 0, (int) 0); }
    static void call28(java.awt.image.DataBuffer receiver)  { receiver.setElemDouble((int) 0, (double) 0); }
    static void call29(java.awt.image.DataBuffer receiver)  { receiver.setElemDouble((int) 0, (int) 0, (double) 0); }
}

abstract class Override_java_awt_image_DataBuffer extends java.awt.image.DataBuffer {
    Override_java_awt_image_DataBuffer()  { super((int) 0, (int) 0); }
    @Override
    public int getDataType()  { throw new UnsupportedOperationException(); }
    @Override
    public int getSize()  { throw new UnsupportedOperationException(); }
    @Override
    public int getOffset()  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getOffsets()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumBanks()  { throw new UnsupportedOperationException(); }
    @Override
    public int getElem(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getElem(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setElem(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setElem(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public float getElemFloat(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getElemFloat(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setElemFloat(int arg0, float arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setElemFloat(int arg0, int arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public double getElemDouble(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double getElemDouble(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setElemDouble(int arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setElemDouble(int arg0, int arg1, double arg2)  { throw new UnsupportedOperationException(); }
}
