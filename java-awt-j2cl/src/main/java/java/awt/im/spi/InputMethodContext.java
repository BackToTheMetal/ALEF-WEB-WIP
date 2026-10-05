package java.awt.im.spi;

import java.awt.Window;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;

public interface InputMethodContext extends InputMethodRequests {
    void dispatchInputMethodEvent(int id, AttributedCharacterIterator text, int committedCharacterCount, TextHitInfo caret, TextHitInfo visiblePosition);
    Window createInputMethodWindow(String title, boolean attachToInputContext);
    void enableClientWindowNotification(InputMethod inputMethod, boolean enable);
}
