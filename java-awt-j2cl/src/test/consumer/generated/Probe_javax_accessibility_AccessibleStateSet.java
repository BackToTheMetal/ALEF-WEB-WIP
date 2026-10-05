package javax.accessibility;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_javax_accessibility_AccessibleStateSet {
    static void call0(javax.accessibility.AccessibleStateSet receiver) { java.util.Vector value = receiver.states; receiver.states = value; }
    static void call1()  { javax.accessibility.AccessibleStateSet value = new javax.accessibility.AccessibleStateSet(); }
    static void call2()  { javax.accessibility.AccessibleStateSet value = new javax.accessibility.AccessibleStateSet((javax.accessibility.AccessibleState[]) null); }
    static void call3(javax.accessibility.AccessibleStateSet receiver)  { boolean value = receiver.add((javax.accessibility.AccessibleState) null); }
    static void call4(javax.accessibility.AccessibleStateSet receiver)  { receiver.addAll((javax.accessibility.AccessibleState[]) null); }
    static void call5(javax.accessibility.AccessibleStateSet receiver)  { boolean value = receiver.remove((javax.accessibility.AccessibleState) null); }
    static void call6(javax.accessibility.AccessibleStateSet receiver)  { receiver.clear(); }
    static void call7(javax.accessibility.AccessibleStateSet receiver)  { boolean value = receiver.contains((javax.accessibility.AccessibleState) null); }
    static void call8(javax.accessibility.AccessibleStateSet receiver)  { javax.accessibility.AccessibleState[] value = receiver.toArray(); }
    static void call9(javax.accessibility.AccessibleStateSet receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_javax_accessibility_AccessibleStateSet extends javax.accessibility.AccessibleStateSet {
    Override_javax_accessibility_AccessibleStateSet()  { super(); }
    @Override
    public boolean add(javax.accessibility.AccessibleState arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addAll(javax.accessibility.AccessibleState[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean remove(javax.accessibility.AccessibleState arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void clear()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(javax.accessibility.AccessibleState arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleState[] toArray()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
