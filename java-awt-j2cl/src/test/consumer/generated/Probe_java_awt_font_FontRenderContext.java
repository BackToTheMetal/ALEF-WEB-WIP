package java.awt.font;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_font_FontRenderContext {
    static void call0()  { java.awt.font.FontRenderContext value = new java.awt.font.FontRenderContext(); }
    static void call1()  { java.awt.font.FontRenderContext value = new java.awt.font.FontRenderContext((java.awt.geom.AffineTransform) null, false, false); }
    static void call2()  { java.awt.font.FontRenderContext value = new java.awt.font.FontRenderContext((java.awt.geom.AffineTransform) null, (java.lang.Object) null, (java.lang.Object) null); }
    static void call3(java.awt.font.FontRenderContext receiver)  { boolean value = receiver.isTransformed(); }
    static void call4(java.awt.font.FontRenderContext receiver)  { int value = receiver.getTransformType(); }
    static void call5(java.awt.font.FontRenderContext receiver)  { java.awt.geom.AffineTransform value = receiver.getTransform(); }
    static void call6(java.awt.font.FontRenderContext receiver)  { boolean value = receiver.isAntiAliased(); }
    static void call7(java.awt.font.FontRenderContext receiver)  { boolean value = receiver.usesFractionalMetrics(); }
    static void call8(java.awt.font.FontRenderContext receiver)  { java.lang.Object value = receiver.getAntiAliasingHint(); }
    static void call9(java.awt.font.FontRenderContext receiver)  { java.lang.Object value = receiver.getFractionalMetricsHint(); }
    static void call10(java.awt.font.FontRenderContext receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call11(java.awt.font.FontRenderContext receiver)  { boolean value = receiver.equals((java.awt.font.FontRenderContext) null); }
    static void call12(java.awt.font.FontRenderContext receiver)  { int value = receiver.hashCode(); }
}

abstract class Override_java_awt_font_FontRenderContext extends java.awt.font.FontRenderContext {
    Override_java_awt_font_FontRenderContext()  { super(); }
    @Override
    public boolean isTransformed()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransformType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform getTransform()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAntiAliased()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean usesFractionalMetrics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getAntiAliasingHint()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getFractionalMetricsHint()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.awt.font.FontRenderContext arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
}
