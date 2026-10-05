package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Rectangle {
    static void call0(java.awt.Rectangle receiver) { int value = receiver.x; receiver.x = value; }
    static void call1(java.awt.Rectangle receiver) { int value = receiver.y; receiver.y = value; }
    static void call2(java.awt.Rectangle receiver) { int value = receiver.width; receiver.width = value; }
    static void call3(java.awt.Rectangle receiver) { int value = receiver.height; receiver.height = value; }
    static void call4()  { java.awt.Rectangle value = new java.awt.Rectangle(); }
    static void call5()  { java.awt.Rectangle value = new java.awt.Rectangle((java.awt.Rectangle) null); }
    static void call6()  { java.awt.Rectangle value = new java.awt.Rectangle((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call7()  { java.awt.Rectangle value = new java.awt.Rectangle((int) 0, (int) 0); }
    static void call8()  { java.awt.Rectangle value = new java.awt.Rectangle((java.awt.Point) null, (java.awt.Dimension) null); }
    static void call9()  { java.awt.Rectangle value = new java.awt.Rectangle((java.awt.Point) null); }
    static void call10()  { java.awt.Rectangle value = new java.awt.Rectangle((java.awt.Dimension) null); }
    static void call11(java.awt.Rectangle receiver)  { double value = receiver.getX(); }
    static void call12(java.awt.Rectangle receiver)  { double value = receiver.getY(); }
    static void call13(java.awt.Rectangle receiver)  { double value = receiver.getWidth(); }
    static void call14(java.awt.Rectangle receiver)  { double value = receiver.getHeight(); }
    static void call15(java.awt.Rectangle receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call16(java.awt.Rectangle receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
    static void call17(java.awt.Rectangle receiver)  { receiver.setBounds((java.awt.Rectangle) null); }
    static void call18(java.awt.Rectangle receiver)  { receiver.setBounds((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call19(java.awt.Rectangle receiver)  { receiver.setRect((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call20(java.awt.Rectangle receiver)  { receiver.reshape((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call21(java.awt.Rectangle receiver)  { java.awt.Point value = receiver.getLocation(); }
    static void call22(java.awt.Rectangle receiver)  { receiver.setLocation((java.awt.Point) null); }
    static void call23(java.awt.Rectangle receiver)  { receiver.setLocation((int) 0, (int) 0); }
    static void call24(java.awt.Rectangle receiver)  { receiver.move((int) 0, (int) 0); }
    static void call25(java.awt.Rectangle receiver)  { receiver.translate((int) 0, (int) 0); }
    static void call26(java.awt.Rectangle receiver)  { java.awt.Dimension value = receiver.getSize(); }
    static void call27(java.awt.Rectangle receiver)  { receiver.setSize((java.awt.Dimension) null); }
    static void call28(java.awt.Rectangle receiver)  { receiver.setSize((int) 0, (int) 0); }
    static void call29(java.awt.Rectangle receiver)  { receiver.resize((int) 0, (int) 0); }
    static void call30(java.awt.Rectangle receiver)  { boolean value = receiver.contains((java.awt.Point) null); }
    static void call31(java.awt.Rectangle receiver)  { boolean value = receiver.contains((int) 0, (int) 0); }
    static void call32(java.awt.Rectangle receiver)  { boolean value = receiver.contains((java.awt.Rectangle) null); }
    static void call33(java.awt.Rectangle receiver)  { boolean value = receiver.contains((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call34(java.awt.Rectangle receiver)  { boolean value = receiver.inside((int) 0, (int) 0); }
    static void call35(java.awt.Rectangle receiver)  { boolean value = receiver.intersects((java.awt.Rectangle) null); }
    static void call36(java.awt.Rectangle receiver)  { java.awt.Rectangle value = receiver.intersection((java.awt.Rectangle) null); }
    static void call37(java.awt.Rectangle receiver)  { java.awt.Rectangle value = receiver.union((java.awt.Rectangle) null); }
    static void call38(java.awt.Rectangle receiver)  { receiver.add((int) 0, (int) 0); }
    static void call39(java.awt.Rectangle receiver)  { receiver.add((java.awt.Point) null); }
    static void call40(java.awt.Rectangle receiver)  { receiver.add((java.awt.Rectangle) null); }
    static void call41(java.awt.Rectangle receiver)  { receiver.grow((int) 0, (int) 0); }
    static void call42(java.awt.Rectangle receiver)  { boolean value = receiver.isEmpty(); }
    static void call43(java.awt.Rectangle receiver)  { int value = receiver.outcode((double) 0, (double) 0); }
    static void call44(java.awt.Rectangle receiver)  { java.awt.geom.Rectangle2D value = receiver.createIntersection((java.awt.geom.Rectangle2D) null); }
    static void call45(java.awt.Rectangle receiver)  { java.awt.geom.Rectangle2D value = receiver.createUnion((java.awt.geom.Rectangle2D) null); }
    static void call46(java.awt.Rectangle receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call47(java.awt.Rectangle receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_Rectangle extends java.awt.Rectangle {
    Override_java_awt_Rectangle()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void reshape(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocation()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void move(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void translate(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void resize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean inside(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle intersection(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle union(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void grow(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEmpty()  { throw new UnsupportedOperationException(); }
    @Override
    public int outcode(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
