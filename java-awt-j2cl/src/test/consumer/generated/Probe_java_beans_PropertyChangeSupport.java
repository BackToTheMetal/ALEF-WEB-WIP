package java.beans;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_beans_PropertyChangeSupport {
    static void call0()  { java.beans.PropertyChangeSupport value = new java.beans.PropertyChangeSupport((java.lang.Object) null); }
    static void call1(java.beans.PropertyChangeSupport receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call2(java.beans.PropertyChangeSupport receiver)  { receiver.removePropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call3(java.beans.PropertyChangeSupport receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners(); }
    static void call4(java.beans.PropertyChangeSupport receiver)  { receiver.addPropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call5(java.beans.PropertyChangeSupport receiver)  { receiver.removePropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call6(java.beans.PropertyChangeSupport receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners((java.lang.String) null); }
    static void call7(java.beans.PropertyChangeSupport receiver)  { receiver.firePropertyChange((java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); }
    static void call8(java.beans.PropertyChangeSupport receiver)  { receiver.firePropertyChange((java.lang.String) null, (int) 0, (int) 0); }
    static void call9(java.beans.PropertyChangeSupport receiver)  { receiver.firePropertyChange((java.lang.String) null, false, false); }
    static void call10(java.beans.PropertyChangeSupport receiver)  { receiver.firePropertyChange((java.beans.PropertyChangeEvent) null); }
    static void call11(java.beans.PropertyChangeSupport receiver)  { receiver.fireIndexedPropertyChange((java.lang.String) null, (int) 0, (java.lang.Object) null, (java.lang.Object) null); }
    static void call12(java.beans.PropertyChangeSupport receiver)  { receiver.fireIndexedPropertyChange((java.lang.String) null, (int) 0, (int) 0, (int) 0); }
    static void call13(java.beans.PropertyChangeSupport receiver)  { receiver.fireIndexedPropertyChange((java.lang.String) null, (int) 0, false, false); }
    static void call14(java.beans.PropertyChangeSupport receiver)  { boolean value = receiver.hasListeners((java.lang.String) null); }
}

abstract class Override_java_beans_PropertyChangeSupport extends java.beans.PropertyChangeSupport {
    Override_java_beans_PropertyChangeSupport()  { super((java.lang.Object) null); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.PropertyChangeListener[] getPropertyChangeListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, java.lang.Object arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, boolean arg1, boolean arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.beans.PropertyChangeEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void fireIndexedPropertyChange(java.lang.String arg0, int arg1, java.lang.Object arg2, java.lang.Object arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void fireIndexedPropertyChange(java.lang.String arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void fireIndexedPropertyChange(java.lang.String arg0, int arg1, boolean arg2, boolean arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
}
