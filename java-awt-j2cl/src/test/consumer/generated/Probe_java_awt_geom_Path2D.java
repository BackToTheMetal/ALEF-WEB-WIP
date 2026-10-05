package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Path2D {
    static void call0() { int value = java.awt.geom.Path2D.WIND_EVEN_ODD; }
    static void call1() { int value = java.awt.geom.Path2D.WIND_NON_ZERO; }
    static void call2(java.awt.geom.Path2D receiver)  { receiver.moveTo((double) 0, (double) 0); }
    static void call3(java.awt.geom.Path2D receiver)  { receiver.lineTo((double) 0, (double) 0); }
    static void call4(java.awt.geom.Path2D receiver)  { receiver.quadTo((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call5(java.awt.geom.Path2D receiver)  { receiver.curveTo((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call6(java.awt.geom.Path2D receiver)  { receiver.closePath(); }
    static void call7(java.awt.geom.Path2D receiver)  { receiver.append((java.awt.Shape) null, false); }
    static void call8(java.awt.geom.Path2D receiver)  { receiver.append((java.awt.geom.PathIterator) null, false); }
    static void call9(java.awt.geom.Path2D receiver)  { int value = receiver.getWindingRule(); }
    static void call10(java.awt.geom.Path2D receiver)  { receiver.setWindingRule((int) 0); }
    static void call11(java.awt.geom.Path2D receiver)  { java.awt.geom.Point2D value = receiver.getCurrentPoint(); }
    static void call12(java.awt.geom.Path2D receiver)  { receiver.reset(); }
    static void call13(java.awt.geom.Path2D receiver)  { receiver.transform((java.awt.geom.AffineTransform) null); }
    static void call14(java.awt.geom.Path2D receiver)  { java.awt.Shape value = receiver.createTransformedShape((java.awt.geom.AffineTransform) null); }
    static void call15(java.awt.geom.Path2D receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call16()  { boolean value = java.awt.geom.Path2D.contains((java.awt.geom.PathIterator) null, (double) 0, (double) 0); }
    static void call17()  { boolean value = java.awt.geom.Path2D.contains((java.awt.geom.PathIterator) null, (java.awt.geom.Point2D) null); }
    static void call18(java.awt.geom.Path2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call19(java.awt.geom.Path2D receiver)  { boolean value = receiver.contains((java.awt.geom.Point2D) null); }
    static void call20()  { boolean value = java.awt.geom.Path2D.contains((java.awt.geom.PathIterator) null, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call21()  { boolean value = java.awt.geom.Path2D.contains((java.awt.geom.PathIterator) null, (java.awt.geom.Rectangle2D) null); }
    static void call22(java.awt.geom.Path2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call23(java.awt.geom.Path2D receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call24()  { boolean value = java.awt.geom.Path2D.intersects((java.awt.geom.PathIterator) null, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call25()  { boolean value = java.awt.geom.Path2D.intersects((java.awt.geom.PathIterator) null, (java.awt.geom.Rectangle2D) null); }
    static void call26(java.awt.geom.Path2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call27(java.awt.geom.Path2D receiver)  { boolean value = receiver.intersects((java.awt.geom.Rectangle2D) null); }
    static void call28(java.awt.geom.Path2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
    static void call29(java.awt.geom.Path2D receiver)  { java.lang.Object value = receiver.clone(); }
    static void call30(java.awt.geom.Path2D receiver)  { receiver.trimToSize(); }
}
