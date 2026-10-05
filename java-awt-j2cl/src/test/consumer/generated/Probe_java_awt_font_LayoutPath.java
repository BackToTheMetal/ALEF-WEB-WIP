package java.awt.font;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_font_LayoutPath {
    static void call0(java.awt.font.LayoutPath receiver)  { boolean value = receiver.pointToPath((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call1(java.awt.font.LayoutPath receiver)  { receiver.pathToPoint((java.awt.geom.Point2D) null, false, (java.awt.geom.Point2D) null); }
}

abstract class Override_java_awt_font_LayoutPath extends java.awt.font.LayoutPath {
    Override_java_awt_font_LayoutPath()  { super(); }
    @Override
    public boolean pointToPath(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void pathToPoint(java.awt.geom.Point2D arg0, boolean arg1, java.awt.geom.Point2D arg2)  { throw new UnsupportedOperationException(); }
}
