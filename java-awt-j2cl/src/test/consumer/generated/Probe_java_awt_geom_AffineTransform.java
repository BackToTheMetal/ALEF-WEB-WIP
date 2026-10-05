package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_AffineTransform {
    static void call0() { int value = java.awt.geom.AffineTransform.TYPE_IDENTITY; }
    static void call1() { int value = java.awt.geom.AffineTransform.TYPE_TRANSLATION; }
    static void call2() { int value = java.awt.geom.AffineTransform.TYPE_UNIFORM_SCALE; }
    static void call3() { int value = java.awt.geom.AffineTransform.TYPE_GENERAL_SCALE; }
    static void call4() { int value = java.awt.geom.AffineTransform.TYPE_MASK_SCALE; }
    static void call5() { int value = java.awt.geom.AffineTransform.TYPE_FLIP; }
    static void call6() { int value = java.awt.geom.AffineTransform.TYPE_QUADRANT_ROTATION; }
    static void call7() { int value = java.awt.geom.AffineTransform.TYPE_GENERAL_ROTATION; }
    static void call8() { int value = java.awt.geom.AffineTransform.TYPE_MASK_ROTATION; }
    static void call9() { int value = java.awt.geom.AffineTransform.TYPE_GENERAL_TRANSFORM; }
    static void call10()  { java.awt.geom.AffineTransform value = new java.awt.geom.AffineTransform(); }
    static void call11()  { java.awt.geom.AffineTransform value = new java.awt.geom.AffineTransform((java.awt.geom.AffineTransform) null); }
    static void call12()  { java.awt.geom.AffineTransform value = new java.awt.geom.AffineTransform((float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0); }
    static void call13()  { java.awt.geom.AffineTransform value = new java.awt.geom.AffineTransform((float[]) null); }
    static void call14()  { java.awt.geom.AffineTransform value = new java.awt.geom.AffineTransform((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call15()  { java.awt.geom.AffineTransform value = new java.awt.geom.AffineTransform((double[]) null); }
    static void call16()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getTranslateInstance((double) 0, (double) 0); }
    static void call17()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getRotateInstance((double) 0); }
    static void call18()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getRotateInstance((double) 0, (double) 0, (double) 0); }
    static void call19()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getRotateInstance((double) 0, (double) 0); }
    static void call20()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getRotateInstance((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call21()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getQuadrantRotateInstance((int) 0); }
    static void call22()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getQuadrantRotateInstance((int) 0, (double) 0, (double) 0); }
    static void call23()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getScaleInstance((double) 0, (double) 0); }
    static void call24()  { java.awt.geom.AffineTransform value = java.awt.geom.AffineTransform.getShearInstance((double) 0, (double) 0); }
    static void call25(java.awt.geom.AffineTransform receiver)  { int value = receiver.getType(); }
    static void call26(java.awt.geom.AffineTransform receiver)  { double value = receiver.getDeterminant(); }
    static void call27(java.awt.geom.AffineTransform receiver)  { receiver.getMatrix((double[]) null); }
    static void call28(java.awt.geom.AffineTransform receiver)  { double value = receiver.getScaleX(); }
    static void call29(java.awt.geom.AffineTransform receiver)  { double value = receiver.getScaleY(); }
    static void call30(java.awt.geom.AffineTransform receiver)  { double value = receiver.getShearX(); }
    static void call31(java.awt.geom.AffineTransform receiver)  { double value = receiver.getShearY(); }
    static void call32(java.awt.geom.AffineTransform receiver)  { double value = receiver.getTranslateX(); }
    static void call33(java.awt.geom.AffineTransform receiver)  { double value = receiver.getTranslateY(); }
    static void call34(java.awt.geom.AffineTransform receiver)  { receiver.translate((double) 0, (double) 0); }
    static void call35(java.awt.geom.AffineTransform receiver)  { receiver.rotate((double) 0); }
    static void call36(java.awt.geom.AffineTransform receiver)  { receiver.rotate((double) 0, (double) 0, (double) 0); }
    static void call37(java.awt.geom.AffineTransform receiver)  { receiver.rotate((double) 0, (double) 0); }
    static void call38(java.awt.geom.AffineTransform receiver)  { receiver.rotate((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call39(java.awt.geom.AffineTransform receiver)  { receiver.quadrantRotate((int) 0); }
    static void call40(java.awt.geom.AffineTransform receiver)  { receiver.quadrantRotate((int) 0, (double) 0, (double) 0); }
    static void call41(java.awt.geom.AffineTransform receiver)  { receiver.scale((double) 0, (double) 0); }
    static void call42(java.awt.geom.AffineTransform receiver)  { receiver.shear((double) 0, (double) 0); }
    static void call43(java.awt.geom.AffineTransform receiver)  { receiver.setToIdentity(); }
    static void call44(java.awt.geom.AffineTransform receiver)  { receiver.setToTranslation((double) 0, (double) 0); }
    static void call45(java.awt.geom.AffineTransform receiver)  { receiver.setToRotation((double) 0); }
    static void call46(java.awt.geom.AffineTransform receiver)  { receiver.setToRotation((double) 0, (double) 0, (double) 0); }
    static void call47(java.awt.geom.AffineTransform receiver)  { receiver.setToRotation((double) 0, (double) 0); }
    static void call48(java.awt.geom.AffineTransform receiver)  { receiver.setToRotation((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call49(java.awt.geom.AffineTransform receiver)  { receiver.setToQuadrantRotation((int) 0); }
    static void call50(java.awt.geom.AffineTransform receiver)  { receiver.setToQuadrantRotation((int) 0, (double) 0, (double) 0); }
    static void call51(java.awt.geom.AffineTransform receiver)  { receiver.setToScale((double) 0, (double) 0); }
    static void call52(java.awt.geom.AffineTransform receiver)  { receiver.setToShear((double) 0, (double) 0); }
    static void call53(java.awt.geom.AffineTransform receiver)  { receiver.setTransform((java.awt.geom.AffineTransform) null); }
    static void call54(java.awt.geom.AffineTransform receiver)  { receiver.setTransform((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call55(java.awt.geom.AffineTransform receiver)  { receiver.concatenate((java.awt.geom.AffineTransform) null); }
    static void call56(java.awt.geom.AffineTransform receiver)  { receiver.preConcatenate((java.awt.geom.AffineTransform) null); }
    static void call57(java.awt.geom.AffineTransform receiver) throws java.awt.geom.NoninvertibleTransformException { try { java.awt.geom.AffineTransform value = receiver.createInverse(); } catch (java.awt.geom.NoninvertibleTransformException expected) { throw expected; } }
    static void call58(java.awt.geom.AffineTransform receiver) throws java.awt.geom.NoninvertibleTransformException { try { receiver.invert(); } catch (java.awt.geom.NoninvertibleTransformException expected) { throw expected; } }
    static void call59(java.awt.geom.AffineTransform receiver)  { java.awt.geom.Point2D value = receiver.transform((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call60(java.awt.geom.AffineTransform receiver)  { receiver.transform((java.awt.geom.Point2D[]) null, (int) 0, (java.awt.geom.Point2D[]) null, (int) 0, (int) 0); }
    static void call61(java.awt.geom.AffineTransform receiver)  { receiver.transform((float[]) null, (int) 0, (float[]) null, (int) 0, (int) 0); }
    static void call62(java.awt.geom.AffineTransform receiver)  { receiver.transform((double[]) null, (int) 0, (double[]) null, (int) 0, (int) 0); }
    static void call63(java.awt.geom.AffineTransform receiver)  { receiver.transform((float[]) null, (int) 0, (double[]) null, (int) 0, (int) 0); }
    static void call64(java.awt.geom.AffineTransform receiver)  { receiver.transform((double[]) null, (int) 0, (float[]) null, (int) 0, (int) 0); }
    static void call65(java.awt.geom.AffineTransform receiver) throws java.awt.geom.NoninvertibleTransformException { try { java.awt.geom.Point2D value = receiver.inverseTransform((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); } catch (java.awt.geom.NoninvertibleTransformException expected) { throw expected; } }
    static void call66(java.awt.geom.AffineTransform receiver) throws java.awt.geom.NoninvertibleTransformException { try { receiver.inverseTransform((double[]) null, (int) 0, (double[]) null, (int) 0, (int) 0); } catch (java.awt.geom.NoninvertibleTransformException expected) { throw expected; } }
    static void call67(java.awt.geom.AffineTransform receiver)  { java.awt.geom.Point2D value = receiver.deltaTransform((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call68(java.awt.geom.AffineTransform receiver)  { receiver.deltaTransform((double[]) null, (int) 0, (double[]) null, (int) 0, (int) 0); }
    static void call69(java.awt.geom.AffineTransform receiver)  { java.awt.Shape value = receiver.createTransformedShape((java.awt.Shape) null); }
    static void call70(java.awt.geom.AffineTransform receiver)  { java.lang.String value = receiver.toString(); }
    static void call71(java.awt.geom.AffineTransform receiver)  { boolean value = receiver.isIdentity(); }
    static void call72(java.awt.geom.AffineTransform receiver)  { java.lang.Object value = receiver.clone(); }
    static void call73(java.awt.geom.AffineTransform receiver)  { int value = receiver.hashCode(); }
    static void call74(java.awt.geom.AffineTransform receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_geom_AffineTransform extends java.awt.geom.AffineTransform {
    Override_java_awt_geom_AffineTransform()  { super(); }
    @Override
    public int getType()  { throw new UnsupportedOperationException(); }
    @Override
    public double getDeterminant()  { throw new UnsupportedOperationException(); }
    @Override
    public void getMatrix(double[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double getScaleX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getScaleY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getShearX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getShearY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getTranslateX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getTranslateY()  { throw new UnsupportedOperationException(); }
    @Override
    public void translate(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void rotate(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void rotate(double arg0, double arg1, double arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void rotate(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void rotate(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void quadrantRotate(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void quadrantRotate(int arg0, double arg1, double arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void scale(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void shear(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToIdentity()  { throw new UnsupportedOperationException(); }
    @Override
    public void setToTranslation(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToRotation(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToRotation(double arg0, double arg1, double arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToRotation(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToRotation(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToQuadrantRotation(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToQuadrantRotation(int arg0, double arg1, double arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToScale(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setToShear(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setTransform(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public void concatenate(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void preConcatenate(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform createInverse() throws java.awt.geom.NoninvertibleTransformException { throw new UnsupportedOperationException(); }
    @Override
    public void invert() throws java.awt.geom.NoninvertibleTransformException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D transform(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(java.awt.geom.Point2D[] arg0, int arg1, java.awt.geom.Point2D[] arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(float[] arg0, int arg1, float[] arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(double[] arg0, int arg1, double[] arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(float[] arg0, int arg1, double[] arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(double[] arg0, int arg1, float[] arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D inverseTransform(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1) throws java.awt.geom.NoninvertibleTransformException { throw new UnsupportedOperationException(); }
    @Override
    public void inverseTransform(double[] arg0, int arg1, double[] arg2, int arg3, int arg4) throws java.awt.geom.NoninvertibleTransformException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D deltaTransform(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void deltaTransform(double[] arg0, int arg1, double[] arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape createTransformedShape(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isIdentity()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
}
