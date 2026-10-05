package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_TexturePaint {
    static void call0()  { java.awt.TexturePaint value = new java.awt.TexturePaint((java.awt.image.BufferedImage) null, (java.awt.geom.Rectangle2D) null); }
    static void call1(java.awt.TexturePaint receiver)  { java.awt.image.BufferedImage value = receiver.getImage(); }
    static void call2(java.awt.TexturePaint receiver)  { java.awt.geom.Rectangle2D value = receiver.getAnchorRect(); }
    static void call3(java.awt.TexturePaint receiver)  { java.awt.PaintContext value = receiver.createContext((java.awt.image.ColorModel) null, (java.awt.Rectangle) null, (java.awt.geom.Rectangle2D) null, (java.awt.geom.AffineTransform) null, (java.awt.RenderingHints) null); }
    static void call4(java.awt.TexturePaint receiver)  { int value = receiver.getTransparency(); }
}

abstract class Override_java_awt_TexturePaint extends java.awt.TexturePaint {
    Override_java_awt_TexturePaint()  { super((java.awt.image.BufferedImage) null, (java.awt.geom.Rectangle2D) null); }
    @Override
    public java.awt.image.BufferedImage getImage()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getAnchorRect()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.PaintContext createContext(java.awt.image.ColorModel arg0, java.awt.Rectangle arg1, java.awt.geom.Rectangle2D arg2, java.awt.geom.AffineTransform arg3, java.awt.RenderingHints arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
}
