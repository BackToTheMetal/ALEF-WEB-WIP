package java.awt.im.spi;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.util.Locale;

public interface InputMethod {
    void setInputMethodContext(InputMethodContext context);
    boolean setLocale(Locale locale);
    Locale getLocale();
    void setCompositionEnabled(boolean enable);
    boolean isCompositionEnabled();
    void reconvert();
    void dispatchEvent(AWTEvent event);
    void notifyClientWindowChange(Rectangle bounds);
    void activate();
    void deactivate(boolean isTemporary);
    void hideWindows();
    void removeNotify();
    void endComposition();
    void dispose();
    Object getControlObject();
}
