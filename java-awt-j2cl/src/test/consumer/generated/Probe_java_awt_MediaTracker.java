package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MediaTracker {
    static void call0() { int value = java.awt.MediaTracker.LOADING; }
    static void call1() { int value = java.awt.MediaTracker.ABORTED; }
    static void call2() { int value = java.awt.MediaTracker.ERRORED; }
    static void call3() { int value = java.awt.MediaTracker.COMPLETE; }
    static void call4()  { java.awt.MediaTracker value = new java.awt.MediaTracker((java.awt.Component) null); }
    static void call5(java.awt.MediaTracker receiver)  { receiver.addImage((java.awt.Image) null, (int) 0); }
    static void call6(java.awt.MediaTracker receiver)  { receiver.addImage((java.awt.Image) null, (int) 0, (int) 0, (int) 0); }
    static void call7(java.awt.MediaTracker receiver)  { boolean value = receiver.checkAll(); }
    static void call8(java.awt.MediaTracker receiver)  { boolean value = receiver.checkAll(false); }
    static void call9(java.awt.MediaTracker receiver)  { boolean value = receiver.isErrorAny(); }
    static void call10(java.awt.MediaTracker receiver)  { java.lang.Object[] value = receiver.getErrorsAny(); }
    static void call11(java.awt.MediaTracker receiver) throws java.lang.InterruptedException { try { receiver.waitForAll(); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call12(java.awt.MediaTracker receiver) throws java.lang.InterruptedException { try { boolean value = receiver.waitForAll((long) 0); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call13(java.awt.MediaTracker receiver)  { int value = receiver.statusAll(false); }
    static void call14(java.awt.MediaTracker receiver)  { boolean value = receiver.checkID((int) 0); }
    static void call15(java.awt.MediaTracker receiver)  { boolean value = receiver.checkID((int) 0, false); }
    static void call16(java.awt.MediaTracker receiver)  { boolean value = receiver.isErrorID((int) 0); }
    static void call17(java.awt.MediaTracker receiver)  { java.lang.Object[] value = receiver.getErrorsID((int) 0); }
    static void call18(java.awt.MediaTracker receiver) throws java.lang.InterruptedException { try { receiver.waitForID((int) 0); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call19(java.awt.MediaTracker receiver) throws java.lang.InterruptedException { try { boolean value = receiver.waitForID((int) 0, (long) 0); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call20(java.awt.MediaTracker receiver)  { int value = receiver.statusID((int) 0, false); }
    static void call21(java.awt.MediaTracker receiver)  { receiver.removeImage((java.awt.Image) null); }
    static void call22(java.awt.MediaTracker receiver)  { receiver.removeImage((java.awt.Image) null, (int) 0); }
    static void call23(java.awt.MediaTracker receiver)  { receiver.removeImage((java.awt.Image) null, (int) 0, (int) 0, (int) 0); }
}

abstract class Override_java_awt_MediaTracker extends java.awt.MediaTracker {
    Override_java_awt_MediaTracker()  { super((java.awt.Component) null); }
    @Override
    public void addImage(java.awt.Image arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addImage(java.awt.Image arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean checkAll()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean checkAll(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isErrorAny()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] getErrorsAny()  { throw new UnsupportedOperationException(); }
    @Override
    public void waitForAll() throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public boolean waitForAll(long arg0) throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public int statusAll(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean checkID(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean checkID(int arg0, boolean arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isErrorID(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] getErrorsID(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void waitForID(int arg0) throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public boolean waitForID(int arg0, long arg1) throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public int statusID(int arg0, boolean arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeImage(java.awt.Image arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeImage(java.awt.Image arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeImage(java.awt.Image arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
}
