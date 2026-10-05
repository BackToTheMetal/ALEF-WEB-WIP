package java.beans;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_beans_VetoableChangeSupport {
    static void call0()  { java.beans.VetoableChangeSupport value = new java.beans.VetoableChangeSupport((java.lang.Object) null); }
    static void call1(java.beans.VetoableChangeSupport receiver)  { receiver.addVetoableChangeListener((java.beans.VetoableChangeListener) null); }
    static void call2(java.beans.VetoableChangeSupport receiver)  { receiver.removeVetoableChangeListener((java.beans.VetoableChangeListener) null); }
    static void call3(java.beans.VetoableChangeSupport receiver)  { java.beans.VetoableChangeListener[] value = receiver.getVetoableChangeListeners(); }
    static void call4(java.beans.VetoableChangeSupport receiver)  { receiver.addVetoableChangeListener((java.lang.String) null, (java.beans.VetoableChangeListener) null); }
    static void call5(java.beans.VetoableChangeSupport receiver)  { receiver.removeVetoableChangeListener((java.lang.String) null, (java.beans.VetoableChangeListener) null); }
    static void call6(java.beans.VetoableChangeSupport receiver)  { java.beans.VetoableChangeListener[] value = receiver.getVetoableChangeListeners((java.lang.String) null); }
    static void call7(java.beans.VetoableChangeSupport receiver) throws java.beans.PropertyVetoException { try { receiver.fireVetoableChange((java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); } catch (java.beans.PropertyVetoException expected) { throw expected; } }
    static void call8(java.beans.VetoableChangeSupport receiver) throws java.beans.PropertyVetoException { try { receiver.fireVetoableChange((java.lang.String) null, (int) 0, (int) 0); } catch (java.beans.PropertyVetoException expected) { throw expected; } }
    static void call9(java.beans.VetoableChangeSupport receiver) throws java.beans.PropertyVetoException { try { receiver.fireVetoableChange((java.lang.String) null, false, false); } catch (java.beans.PropertyVetoException expected) { throw expected; } }
    static void call10(java.beans.VetoableChangeSupport receiver) throws java.beans.PropertyVetoException { try { receiver.fireVetoableChange((java.beans.PropertyChangeEvent) null); } catch (java.beans.PropertyVetoException expected) { throw expected; } }
    static void call11(java.beans.VetoableChangeSupport receiver)  { boolean value = receiver.hasListeners((java.lang.String) null); }
}

abstract class Override_java_beans_VetoableChangeSupport extends java.beans.VetoableChangeSupport {
    Override_java_beans_VetoableChangeSupport()  { super((java.lang.Object) null); }
    @Override
    public void addVetoableChangeListener(java.beans.VetoableChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.VetoableChangeListener[] getVetoableChangeListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addVetoableChangeListener(java.lang.String arg0, java.beans.VetoableChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeVetoableChangeListener(java.lang.String arg0, java.beans.VetoableChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.VetoableChangeListener[] getVetoableChangeListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void fireVetoableChange(java.lang.String arg0, java.lang.Object arg1, java.lang.Object arg2) throws java.beans.PropertyVetoException { throw new UnsupportedOperationException(); }
    @Override
    public void fireVetoableChange(java.lang.String arg0, int arg1, int arg2) throws java.beans.PropertyVetoException { throw new UnsupportedOperationException(); }
    @Override
    public void fireVetoableChange(java.lang.String arg0, boolean arg1, boolean arg2) throws java.beans.PropertyVetoException { throw new UnsupportedOperationException(); }
    @Override
    public void fireVetoableChange(java.beans.PropertyChangeEvent arg0) throws java.beans.PropertyVetoException { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
}
