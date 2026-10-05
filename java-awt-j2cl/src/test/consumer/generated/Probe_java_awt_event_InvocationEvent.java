package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_InvocationEvent {
    static void call0() { int value = java.awt.event.InvocationEvent.INVOCATION_FIRST; }
    static void call1() { int value = java.awt.event.InvocationEvent.INVOCATION_DEFAULT; }
    static void call2() { int value = java.awt.event.InvocationEvent.INVOCATION_LAST; }
    static void call3(java.awt.event.InvocationEvent receiver) { java.lang.Runnable value = receiver.runnable; receiver.runnable = value; }
    static void call4(java.awt.event.InvocationEvent receiver) { java.lang.Object value = receiver.notifier; receiver.notifier = value; }
    static void call5(java.awt.event.InvocationEvent receiver) { boolean value = receiver.catchExceptions; receiver.catchExceptions = value; }
    static void call6()  { java.awt.event.InvocationEvent value = new java.awt.event.InvocationEvent((java.lang.Object) null, (java.lang.Runnable) null); }
    static void call7()  { java.awt.event.InvocationEvent value = new java.awt.event.InvocationEvent((java.lang.Object) null, (java.lang.Runnable) null, (java.lang.Object) null, false); }
    static void call8()  { java.awt.event.InvocationEvent value = new java.awt.event.InvocationEvent((java.lang.Object) null, (java.lang.Runnable) null, (java.lang.Runnable) null, false); }
    static void call9()  { java.awt.event.InvocationEvent value = new java.awt.event.InvocationEvent((java.lang.Object) null, (int) 0, (java.lang.Runnable) null, (java.lang.Object) null, false); }
    static void call10(java.awt.event.InvocationEvent receiver)  { receiver.dispatch(); }
    static void call11(java.awt.event.InvocationEvent receiver)  { java.lang.Exception value = receiver.getException(); }
    static void call12(java.awt.event.InvocationEvent receiver)  { java.lang.Throwable value = receiver.getThrowable(); }
    static void call13(java.awt.event.InvocationEvent receiver)  { long value = receiver.getWhen(); }
    static void call14(java.awt.event.InvocationEvent receiver)  { boolean value = receiver.isDispatched(); }
    static void call15(java.awt.event.InvocationEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_InvocationEvent extends java.awt.event.InvocationEvent {
    Override_java_awt_event_InvocationEvent()  { super((java.lang.Object) null, (java.lang.Runnable) null); }
    @Override
    public void dispatch()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Exception getException()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Throwable getThrowable()  { throw new UnsupportedOperationException(); }
    @Override
    public long getWhen()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDispatched()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
