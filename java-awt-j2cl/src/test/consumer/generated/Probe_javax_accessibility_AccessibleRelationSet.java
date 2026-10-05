package javax.accessibility;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_javax_accessibility_AccessibleRelationSet {
    static void call0(javax.accessibility.AccessibleRelationSet receiver) { java.util.Vector value = receiver.relations; receiver.relations = value; }
    static void call1()  { javax.accessibility.AccessibleRelationSet value = new javax.accessibility.AccessibleRelationSet(); }
    static void call2()  { javax.accessibility.AccessibleRelationSet value = new javax.accessibility.AccessibleRelationSet((javax.accessibility.AccessibleRelation[]) null); }
    static void call3(javax.accessibility.AccessibleRelationSet receiver)  { boolean value = receiver.add((javax.accessibility.AccessibleRelation) null); }
    static void call4(javax.accessibility.AccessibleRelationSet receiver)  { receiver.addAll((javax.accessibility.AccessibleRelation[]) null); }
    static void call5(javax.accessibility.AccessibleRelationSet receiver)  { boolean value = receiver.remove((javax.accessibility.AccessibleRelation) null); }
    static void call6(javax.accessibility.AccessibleRelationSet receiver)  { receiver.clear(); }
    static void call7(javax.accessibility.AccessibleRelationSet receiver)  { int value = receiver.size(); }
    static void call8(javax.accessibility.AccessibleRelationSet receiver)  { boolean value = receiver.contains((java.lang.String) null); }
    static void call9(javax.accessibility.AccessibleRelationSet receiver)  { javax.accessibility.AccessibleRelation value = receiver.get((java.lang.String) null); }
    static void call10(javax.accessibility.AccessibleRelationSet receiver)  { javax.accessibility.AccessibleRelation[] value = receiver.toArray(); }
    static void call11(javax.accessibility.AccessibleRelationSet receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_javax_accessibility_AccessibleRelationSet extends javax.accessibility.AccessibleRelationSet {
    Override_javax_accessibility_AccessibleRelationSet()  { super(); }
    @Override
    public boolean add(javax.accessibility.AccessibleRelation arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addAll(javax.accessibility.AccessibleRelation[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean remove(javax.accessibility.AccessibleRelation arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void clear()  { throw new UnsupportedOperationException(); }
    @Override
    public int size()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRelation get(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRelation[] toArray()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
