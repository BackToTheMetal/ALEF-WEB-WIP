package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GradientPaint {
    static void call0()  { java.awt.GradientPaint value = new java.awt.GradientPaint((float) 0, (float) 0, (java.awt.Color) null, (float) 0, (float) 0, (java.awt.Color) null); }
    static void call1()  { java.awt.GradientPaint value = new java.awt.GradientPaint((java.awt.geom.Point2D) null, (java.awt.Color) null, (java.awt.geom.Point2D) null, (java.awt.Color) null); }
    static void call2()  { java.awt.GradientPaint value = new java.awt.GradientPaint((float) 0, (float) 0, (java.awt.Color) null, (float) 0, (float) 0, (java.awt.Color) null, false); }
    static void call3()  { java.awt.GradientPaint value = new java.awt.GradientPaint((java.awt.geom.Point2D) null, (java.awt.Color) null, (java.awt.geom.Point2D) null, (java.awt.Color) null, false); }
    static void call4(java.awt.GradientPaint receiver)  { java.awt.geom.Point2D value = receiver.getPoint1(); }
    static void call5(java.awt.GradientPaint receiver)  { java.awt.Color value = receiver.getColor1(); }
    static void call6(java.awt.GradientPaint receiver)  { java.awt.geom.Point2D value = receiver.getPoint2(); }
    static void call7(java.awt.GradientPaint receiver)  { java.awt.Color value = receiver.getColor2(); }
    static void call8(java.awt.GradientPaint receiver)  { boolean value = receiver.isCyclic(); }
    static void call9(java.awt.GradientPaint receiver)  { java.awt.PaintContext value = receiver.createContext((java.awt.image.ColorModel) null, (java.awt.Rectangle) null, (java.awt.geom.Rectangle2D) null, (java.awt.geom.AffineTransform) null, (java.awt.RenderingHints) null); }
    static void call10(java.awt.GradientPaint receiver)  { int value = receiver.getTransparency(); }
}

abstract class Override_java_awt_GradientPaint extends java.awt.GradientPaint {
    Override_java_awt_GradientPaint()  { super((float) 0, (float) 0, (java.awt.Color) null, (float) 0, (float) 0, (java.awt.Color) null); }
    @Override
    public java.awt.geom.Point2D getPoint1()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getColor1()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getPoint2()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getColor2()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCyclic()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.PaintContext createContext(java.awt.image.ColorModel arg0, java.awt.Rectangle arg1, java.awt.geom.Rectangle2D arg2, java.awt.geom.AffineTransform arg3, java.awt.RenderingHints arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
}
