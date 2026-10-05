package java.awt.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;

public class InvocationEvent extends AWTEvent implements ActiveEvent {
    private static final long serialVersionUID = 436056344909459450L;
    public static final int INVOCATION_FIRST = 1200;
    public static final int INVOCATION_DEFAULT = 1200;
    public static final int INVOCATION_LAST = 1200;
    protected Runnable runnable;
    protected volatile Object notifier;
    private final Runnable listener;
    protected boolean catchExceptions;
    private final long when;
    private Throwable throwable;
    private boolean dispatched;

    public InvocationEvent(Object source, Runnable runnable) {
        this(source, INVOCATION_DEFAULT, runnable, null, null, false);
    }

    public InvocationEvent(Object source, Runnable runnable, Object notifier, boolean catchThrowables) {
        this(source, INVOCATION_DEFAULT, runnable, notifier, null, catchThrowables);
    }

    public InvocationEvent(Object source, Runnable runnable, Runnable listener, boolean catchThrowables) {
        this(source, INVOCATION_DEFAULT, runnable, null, listener, catchThrowables);
    }

    protected InvocationEvent(Object source, int id, Runnable runnable, Object notifier, boolean catchThrowables) {
        this(source, id, runnable, notifier, null, catchThrowables);
    }

    private InvocationEvent(Object source, int id, Runnable runnable, Object notifier, Runnable listener, boolean catchThrowables) {
        super(source, id);
        if (runnable == null) {
            throw new NullPointerException("runnable");
        }
        this.runnable = runnable;
        this.notifier = notifier;
        this.listener = listener;
        catchExceptions = catchThrowables;
        when = System.currentTimeMillis();
    }

    @Override
    public void dispatch() {
        try {
            runnable.run();
        } catch (Throwable failure) {
            if (!catchExceptions) {
                rethrow(failure);
            }
            throwable = failure;
        } finally {
            dispatched = true;
            if (listener != null) {
                listener.run();
            }
        }
    }

    public Exception getException() {
        return throwable instanceof Exception ? (Exception) throwable : null;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public long getWhen() {
        return when;
    }

    public boolean isDispatched() {
        return dispatched;
    }

    @Override
    public String paramString() {
        String eventName = id == INVOCATION_DEFAULT ? "INVOCATION_DEFAULT" : "unknown type";
        return eventName + ",runnable=" + runnable + ",notifier=" + notifier + ",catchExceptions=" + catchExceptions + ",when=" + when;
    }

    private static void rethrow(Throwable failure) {
        if (failure instanceof RuntimeException) {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error) {
            throw (Error) failure;
        }
        throw new RuntimeException(failure);
    }
}
