package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Graphics2D {
    static void call0(java.awt.Graphics2D receiver)  { receiver.draw3DRect((int) 0, (int) 0, (int) 0, (int) 0, false); }
    static void call1(java.awt.Graphics2D receiver)  { receiver.fill3DRect((int) 0, (int) 0, (int) 0, (int) 0, false); }
    static void call2(java.awt.Graphics2D receiver)  { receiver.draw((java.awt.Shape) null); }
    static void call3(java.awt.Graphics2D receiver)  { boolean value = receiver.drawImage((java.awt.Image) null, (java.awt.geom.AffineTransform) null, (java.awt.image.ImageObserver) null); }
    static void call4(java.awt.Graphics2D receiver)  { receiver.drawImage((java.awt.image.BufferedImage) null, (java.awt.image.BufferedImageOp) null, (int) 0, (int) 0); }
    static void call5(java.awt.Graphics2D receiver)  { receiver.drawRenderedImage((java.awt.image.RenderedImage) null, (java.awt.geom.AffineTransform) null); }
    static void call6(java.awt.Graphics2D receiver)  { receiver.drawRenderableImage((java.awt.image.renderable.RenderableImage) null, (java.awt.geom.AffineTransform) null); }
    static void call7(java.awt.Graphics2D receiver)  { receiver.drawString((java.lang.String) null, (int) 0, (int) 0); }
    static void call8(java.awt.Graphics2D receiver)  { receiver.drawString((java.lang.String) null, (float) 0, (float) 0); }
    static void call9(java.awt.Graphics2D receiver)  { receiver.drawString((java.text.AttributedCharacterIterator) null, (int) 0, (int) 0); }
    static void call10(java.awt.Graphics2D receiver)  { receiver.drawString((java.text.AttributedCharacterIterator) null, (float) 0, (float) 0); }
    static void call11(java.awt.Graphics2D receiver)  { receiver.drawGlyphVector((java.awt.font.GlyphVector) null, (float) 0, (float) 0); }
    static void call12(java.awt.Graphics2D receiver)  { receiver.fill((java.awt.Shape) null); }
    static void call13(java.awt.Graphics2D receiver)  { boolean value = receiver.hit((java.awt.Rectangle) null, (java.awt.Shape) null, false); }
    static void call14(java.awt.Graphics2D receiver)  { java.awt.GraphicsConfiguration value = receiver.getDeviceConfiguration(); }
    static void call15(java.awt.Graphics2D receiver)  { receiver.setComposite((java.awt.Composite) null); }
    static void call16(java.awt.Graphics2D receiver)  { receiver.setPaint((java.awt.Paint) null); }
    static void call17(java.awt.Graphics2D receiver)  { receiver.setStroke((java.awt.Stroke) null); }
    static void call18(java.awt.Graphics2D receiver)  { receiver.setRenderingHint((java.awt.RenderingHints.Key) null, (java.lang.Object) null); }
    static void call19(java.awt.Graphics2D receiver)  { java.lang.Object value = receiver.getRenderingHint((java.awt.RenderingHints.Key) null); }
    static void call20(java.awt.Graphics2D receiver)  { receiver.setRenderingHints((java.util.Map) null); }
    static void call21(java.awt.Graphics2D receiver)  { receiver.addRenderingHints((java.util.Map) null); }
    static void call22(java.awt.Graphics2D receiver)  { java.awt.RenderingHints value = receiver.getRenderingHints(); }
    static void call23(java.awt.Graphics2D receiver)  { receiver.translate((int) 0, (int) 0); }
    static void call24(java.awt.Graphics2D receiver)  { receiver.translate((double) 0, (double) 0); }
    static void call25(java.awt.Graphics2D receiver)  { receiver.rotate((double) 0); }
    static void call26(java.awt.Graphics2D receiver)  { receiver.rotate((double) 0, (double) 0, (double) 0); }
    static void call27(java.awt.Graphics2D receiver)  { receiver.scale((double) 0, (double) 0); }
    static void call28(java.awt.Graphics2D receiver)  { receiver.shear((double) 0, (double) 0); }
    static void call29(java.awt.Graphics2D receiver)  { receiver.transform((java.awt.geom.AffineTransform) null); }
    static void call30(java.awt.Graphics2D receiver)  { receiver.setTransform((java.awt.geom.AffineTransform) null); }
    static void call31(java.awt.Graphics2D receiver)  { java.awt.geom.AffineTransform value = receiver.getTransform(); }
    static void call32(java.awt.Graphics2D receiver)  { java.awt.Paint value = receiver.getPaint(); }
    static void call33(java.awt.Graphics2D receiver)  { java.awt.Composite value = receiver.getComposite(); }
    static void call34(java.awt.Graphics2D receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call35(java.awt.Graphics2D receiver)  { java.awt.Color value = receiver.getBackground(); }
    static void call36(java.awt.Graphics2D receiver)  { java.awt.Stroke value = receiver.getStroke(); }
    static void call37(java.awt.Graphics2D receiver)  { receiver.clip((java.awt.Shape) null); }
    static void call38(java.awt.Graphics2D receiver)  { java.awt.font.FontRenderContext value = receiver.getFontRenderContext(); }
}

abstract class Override_java_awt_Graphics2D extends java.awt.Graphics2D {
    Override_java_awt_Graphics2D()  { super(); }
    @Override
    public void draw3DRect(int arg0, int arg1, int arg2, int arg3, boolean arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void fill3DRect(int arg0, int arg1, int arg2, int arg3, boolean arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void draw(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean drawImage(java.awt.Image arg0, java.awt.geom.AffineTransform arg1, java.awt.image.ImageObserver arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawImage(java.awt.image.BufferedImage arg0, java.awt.image.BufferedImageOp arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawRenderedImage(java.awt.image.RenderedImage arg0, java.awt.geom.AffineTransform arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawRenderableImage(java.awt.image.renderable.RenderableImage arg0, java.awt.geom.AffineTransform arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawString(java.lang.String arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawString(java.lang.String arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawString(java.text.AttributedCharacterIterator arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawString(java.text.AttributedCharacterIterator arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void drawGlyphVector(java.awt.font.GlyphVector arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void fill(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hit(java.awt.Rectangle arg0, java.awt.Shape arg1, boolean arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsConfiguration getDeviceConfiguration()  { throw new UnsupportedOperationException(); }
    @Override
    public void setComposite(java.awt.Composite arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPaint(java.awt.Paint arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setStroke(java.awt.Stroke arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRenderingHint(java.awt.RenderingHints.Key arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getRenderingHint(java.awt.RenderingHints.Key arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRenderingHints(java.util.Map<?, ?> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addRenderingHints(java.util.Map<?, ?> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.RenderingHints getRenderingHints()  { throw new UnsupportedOperationException(); }
    @Override
    public void translate(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void translate(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void rotate(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void rotate(double arg0, double arg1, double arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void scale(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void shear(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform getTransform()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Paint getPaint()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Composite getComposite()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBackground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getBackground()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Stroke getStroke()  { throw new UnsupportedOperationException(); }
    @Override
    public void clip(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.FontRenderContext getFontRenderContext()  { throw new UnsupportedOperationException(); }
}
