package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_PathIterator {
    static void call0() { int value = java.awt.geom.PathIterator.WIND_EVEN_ODD; }
    static void call1() { int value = java.awt.geom.PathIterator.WIND_NON_ZERO; }
    static void call2() { int value = java.awt.geom.PathIterator.SEG_MOVETO; }
    static void call3() { int value = java.awt.geom.PathIterator.SEG_LINETO; }
    static void call4() { int value = java.awt.geom.PathIterator.SEG_QUADTO; }
    static void call5() { int value = java.awt.geom.PathIterator.SEG_CUBICTO; }
    static void call6() { int value = java.awt.geom.PathIterator.SEG_CLOSE; }
    static void call7(java.awt.geom.PathIterator receiver)  { int value = receiver.getWindingRule(); }
    static void call8(java.awt.geom.PathIterator receiver)  { boolean value = receiver.isDone(); }
    static void call9(java.awt.geom.PathIterator receiver)  { receiver.next(); }
    static void call10(java.awt.geom.PathIterator receiver)  { int value = receiver.currentSegment((float[]) null); }
    static void call11(java.awt.geom.PathIterator receiver)  { int value = receiver.currentSegment((double[]) null); }
}
