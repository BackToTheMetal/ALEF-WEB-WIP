package java.awt.im;

import java.awt.AWTEvent;
import java.awt.Component;
import java.util.Locale;

public class InputContext {
    private Locale locale = Locale.getDefault();
    private boolean compositionEnabled = true;
    private boolean disposed;

    protected InputContext() {
    }

    public static InputContext getInstance() {
        return new InputContext();
    }

    public boolean selectInputMethod(Locale locale) {
        ensureUsable();
        if (locale == null) {
            throw new NullPointerException("locale");
        }
        this.locale = locale;
        return true;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setCompositionEnabled(boolean enable) {
        ensureUsable();
        compositionEnabled = enable;
    }

    public boolean isCompositionEnabled() {
        ensureUsable();
        return compositionEnabled;
    }

    public void reconvert() {
        ensureUsable();
        throw new UnsupportedOperationException("Browser input-method reconversion is unavailable");
    }

    public void dispatchEvent(AWTEvent event) {
        ensureUsable();
        if (event == null) {
            throw new NullPointerException("event");
        }
    }

    public void removeNotify(Component client) {
        ensureUsable();
    }

    public void endComposition() {
        ensureUsable();
    }

    public void dispose() {
        disposed = true;
    }

    public Object getInputMethodControlObject() {
        return null;
    }

    private void ensureUsable() {
        if (disposed) {
            throw new IllegalStateException("input context has been disposed");
        }
    }
}
