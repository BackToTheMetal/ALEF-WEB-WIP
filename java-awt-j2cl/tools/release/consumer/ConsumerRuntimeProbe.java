package org.webawt.consumer;
import java.awt.AWTKeyStroke;
import java.lang.reflect.InvocationTargetException;
public final class ConsumerRuntimeProbe {
    private static class Parent {}
    private static final class Child extends Parent {}
    public static String run() {
        if (!AWTKeyStroke.getAWTKeyStroke("shift F2").equals(AWTKeyStroke.getAWTKeyStroke("shift pressed F2"))) {
            throw new AssertionError("implicit key stroke");
        }
        if (!Parent.class.isInstance(new Child()) || !Parent.class.isAssignableFrom(Child.class)) {
            throw new AssertionError("compiler type metadata");
        }
        Throwable cause = new IllegalArgumentException("cause");
        InvocationTargetException wrapped = new InvocationTargetException(cause);
        if (wrapped.getCause() != cause || wrapped.getTargetException() != cause) {
            throw new AssertionError("callback exception cause");
        }
        return "PASS";
    }
}
