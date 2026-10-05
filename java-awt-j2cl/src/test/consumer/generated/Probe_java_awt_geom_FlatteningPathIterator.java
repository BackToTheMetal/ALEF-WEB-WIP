package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_FlatteningPathIterator {
    static void call0()  { java.awt.geom.FlatteningPathIterator value = new java.awt.geom.FlatteningPathIterator((java.awt.geom.PathIterator) null, (double) 0); }
    static void call1()  { java.awt.geom.FlatteningPathIterator value = new java.awt.geom.FlatteningPathIterator((java.awt.geom.PathIterator) null, (double) 0, (int) 0); }
    static void call2(java.awt.geom.FlatteningPathIterator receiver)  { double value = receiver.getFlatness(); }
    static void call3(java.awt.geom.FlatteningPathIterator receiver)  { int value = receiver.getRecursionLimit(); }
    static void call4(java.awt.geom.FlatteningPathIterator receiver)  { int value = receiver.getWindingRule(); }
    static void call5(java.awt.geom.FlatteningPathIterator receiver)  { boolean value = receiver.isDone(); }
    static void call6(java.awt.geom.FlatteningPathIterator receiver)  { receiver.next(); }
    static void call7(java.awt.geom.FlatteningPathIterator receiver)  { int value = receiver.currentSegment((float[]) null); }
    static void call8(java.awt.geom.FlatteningPathIterator receiver)  { int value = receiver.currentSegment((double[]) null); }
}

abstract class Override_java_awt_geom_FlatteningPathIterator extends java.awt.geom.FlatteningPathIterator {
    Override_java_awt_geom_FlatteningPathIterator()  { super((java.awt.geom.PathIterator) null, (double) 0); }
    @Override
    public double getFlatness()  { throw new UnsupportedOperationException(); }
    @Override
    public int getRecursionLimit()  { throw new UnsupportedOperationException(); }
    @Override
    public int getWindingRule()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDone()  { throw new UnsupportedOperationException(); }
    @Override
    public void next()  { throw new UnsupportedOperationException(); }
    @Override
    public int currentSegment(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int currentSegment(double[] arg0)  { throw new UnsupportedOperationException(); }
}
