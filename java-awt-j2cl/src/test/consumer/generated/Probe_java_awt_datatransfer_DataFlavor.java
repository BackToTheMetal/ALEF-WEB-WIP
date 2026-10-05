package java.awt.datatransfer;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_datatransfer_DataFlavor {
    static void call0() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.stringFlavor; }
    static void call1() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.imageFlavor; }
    static void call2() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.plainTextFlavor; }
    static void call3() { java.lang.String value = java.awt.datatransfer.DataFlavor.javaSerializedObjectMimeType; }
    static void call4() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.javaFileListFlavor; }
    static void call5() { java.lang.String value = java.awt.datatransfer.DataFlavor.javaJVMLocalObjectMimeType; }
    static void call6() { java.lang.String value = java.awt.datatransfer.DataFlavor.javaRemoteObjectMimeType; }
    static void call7() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.selectionHtmlFlavor; }
    static void call8() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.fragmentHtmlFlavor; }
    static void call9() { java.awt.datatransfer.DataFlavor value = java.awt.datatransfer.DataFlavor.allHtmlFlavor; }
    static void call10()  { java.awt.datatransfer.DataFlavor value = new java.awt.datatransfer.DataFlavor(); }
    static void call11()  { java.awt.datatransfer.DataFlavor value = new java.awt.datatransfer.DataFlavor((java.lang.Class) null, (java.lang.String) null); }
    static void call12()  { java.awt.datatransfer.DataFlavor value = new java.awt.datatransfer.DataFlavor((java.lang.String) null, (java.lang.String) null); }
    static void call13(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.toString(); }
    static void call14(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.getMimeType(); }
    static void call15(java.awt.datatransfer.DataFlavor receiver)  { java.lang.Class value = receiver.getRepresentationClass(); }
    static void call16(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.getHumanPresentableName(); }
    static void call17(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.getPrimaryType(); }
    static void call18(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.getSubType(); }
    static void call19(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.getParameter((java.lang.String) null); }
    static void call20(java.awt.datatransfer.DataFlavor receiver)  { receiver.setHumanPresentableName((java.lang.String) null); }
    static void call21(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call22(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.equals((java.awt.datatransfer.DataFlavor) null); }
    static void call23(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.equals((java.lang.String) null); }
    static void call24(java.awt.datatransfer.DataFlavor receiver)  { int value = receiver.hashCode(); }
    static void call25(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.match((java.awt.datatransfer.DataFlavor) null); }
    static void call26(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isMimeTypeEqual((java.lang.String) null); }
    static void call27(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isMimeTypeEqual((java.awt.datatransfer.DataFlavor) null); }
    static void call28(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isMimeTypeSerializedObject(); }
    static void call29(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isRepresentationClassInputStream(); }
    static void call30(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isRepresentationClassReader(); }
    static void call31(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isRepresentationClassCharBuffer(); }
    static void call32(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isRepresentationClassByteBuffer(); }
    static void call33(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isRepresentationClassSerializable(); }
    static void call34(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isRepresentationClassRemote(); }
    static void call35(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isFlavorSerializedObjectType(); }
    static void call36(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isFlavorRemoteObjectType(); }
    static void call37(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isFlavorJavaFileListType(); }
    static void call38(java.awt.datatransfer.DataFlavor receiver)  { boolean value = receiver.isFlavorTextType(); }
    static void call39(java.awt.datatransfer.DataFlavor receiver) throws java.lang.CloneNotSupportedException { try { java.lang.Object value = receiver.clone(); } catch (java.lang.CloneNotSupportedException expected) { throw expected; } }
    static void call40(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.normalizeMimeTypeParameter((java.lang.String) null, (java.lang.String) null); }
    static void call41(java.awt.datatransfer.DataFlavor receiver)  { java.lang.String value = receiver.normalizeMimeType((java.lang.String) null); }
}

abstract class Override_java_awt_datatransfer_DataFlavor extends java.awt.datatransfer.DataFlavor {
    Override_java_awt_datatransfer_DataFlavor()  { super(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getMimeType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Class<?> getRepresentationClass()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getHumanPresentableName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getPrimaryType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getSubType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getParameter(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setHumanPresentableName(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.awt.datatransfer.DataFlavor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean match(java.awt.datatransfer.DataFlavor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isMimeTypeEqual(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isMimeTypeSerializedObject()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRepresentationClassInputStream()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRepresentationClassReader()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRepresentationClassCharBuffer()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRepresentationClassByteBuffer()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRepresentationClassSerializable()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRepresentationClassRemote()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFlavorSerializedObjectType()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFlavorRemoteObjectType()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFlavorJavaFileListType()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFlavorTextType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String normalizeMimeTypeParameter(java.lang.String arg0, java.lang.String arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String normalizeMimeType(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
}
