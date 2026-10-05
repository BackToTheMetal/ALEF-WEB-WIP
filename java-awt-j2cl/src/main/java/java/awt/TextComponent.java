package java.awt;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.InputMethodEvent;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import org.webawt.internal.text.PlainAttributedCharacterIterator;

public class TextComponent extends Component implements Accessible {
    protected transient TextListener textListener;
    private String text;
    private boolean editable = true;
    private int caretPosition;
    private int selectionEnd;
    private int selectionStart;

    TextComponent(String elementName, String text) {
        super(elementName);
        this.text = normalize(text);
    }

    public synchronized void setText(String text) {
        this.text = normalize(text);
        clampSelection();
        updatePeerValue(this.text);
        writeSelectionToPeer();
        fireTextValueChanged();
    }

    public synchronized String getText() {
        return text;
    }

    public synchronized String getSelectedText() {
        readSelectionFromPeer();
        return selectionStart == selectionEnd ? null : text.substring(selectionStart, selectionEnd);
    }

    public boolean isEditable() {
        return editable;
    }

    public synchronized void setEditable(boolean editable) {
        this.editable = editable;
        updatePeerEditable(editable);
    }

    public synchronized int getSelectionStart() {
        readSelectionFromPeer();
        return selectionStart;
    }

    public synchronized void setSelectionStart(int selectionStart) {
        select(selectionStart, Math.max(selectionStart, getSelectionEnd()));
    }

    public synchronized int getSelectionEnd() {
        readSelectionFromPeer();
        return selectionEnd;
    }

    public synchronized void setSelectionEnd(int selectionEnd) {
        select(Math.min(getSelectionStart(), selectionEnd), selectionEnd);
    }

    public synchronized void select(int selectionStart, int selectionEnd) {
        int length = text.length();
        this.selectionStart = Math.max(0, Math.min(selectionStart, length));
        this.selectionEnd = Math.max(this.selectionStart, Math.min(selectionEnd, length));
        caretPosition = this.selectionEnd;
        writeSelectionToPeer();
    }

    public synchronized void selectAll() {
        select(0, text.length());
    }

    public synchronized void setCaretPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position less than zero");
        }
        caretPosition = Math.min(position, text.length());
        selectionStart = caretPosition;
        selectionEnd = caretPosition;
        writeSelectionToPeer();
    }

    public synchronized int getCaretPosition() {
        readSelectionFromPeer();
        return caretPosition;
    }

    @Override
    public InputMethodRequests getInputMethodRequests() {
        return new TextComponentInputMethodRequests();
    }

    public synchronized void addTextListener(TextListener listener) {
        if (listener != null) {
            textListener = AWTEventMulticaster.add(textListener, listener);
        }
    }

    public synchronized void removeTextListener(TextListener listener) {
        textListener = AWTEventMulticaster.remove(textListener, listener);
    }

    public synchronized TextListener[] getTextListeners() {
        return AWTEventMulticaster.getListeners(textListener, TextListener.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerType == TextListener.class ? (T[]) getTextListeners() : super.getListeners(listenerType);
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        if (event.getID() == TextEvent.TEXT_VALUE_CHANGED && textListener != null) {
            return true;
        }
        return super.eventEnabled(event);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof TextEvent) {
            processTextEvent((TextEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    protected void processTextEvent(TextEvent event) {
        for (TextListener listener : getTextListeners()) {
            listener.textValueChanged(event);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePeerValue(text);
        updatePeerEditable(editable);
        updatePeerTextChangeHandler(this::readTextFromPeer);
        updatePeerCompositionEventHandler(this::dispatchCompositionEvent);
        writeSelectionToPeer();
    }

    @Override
    public void removeNotify() {
        readSelectionFromPeer();
        super.removeNotify();
    }

    private void writeSelectionToPeer() {
        if (getPeer() != null) {
            getPeer().setTextSelection(selectionStart, selectionEnd);
        }
    }

    private void readSelectionFromPeer() {
        int[] selection = getPeer() == null ? null : getPeer().getTextSelection();
        if (selection != null) {
            selectionStart = selection[0];
            selectionEnd = selection[1];
            caretPosition = selection[2];
            clampSelection();
        }
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTTextComponent();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTTextComponent extends AccessibleAWTComponent {
        public AccessibleAWTTextComponent() {}
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TEXT;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (isEditable()) {
                states.add(AccessibleState.EDITABLE);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(TextComponent.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return TextComponent.this.getLocale();
        }
    }

    private final class TextComponentInputMethodRequests implements InputMethodRequests {
        @Override
        public Rectangle getTextLocation(TextHitInfo offset) {
            Point location = isShowing() ? getLocationOnScreen() : getLocation();
            Font currentFont = getFont() == null ? new Font(Font.DIALOG, Font.PLAIN, 12) : getFont();
            int insertion = offset == null ? getCaretPosition() : Math.max(0, Math.min(offset.getInsertionIndex(), getText().length()));
            int x = location.x + getFontMetrics(currentFont).stringWidth(getText().substring(0, insertion));
            return new Rectangle(x, location.y, 1, getFontMetrics(currentFont).getHeight());
        }

        @Override
        public TextHitInfo getLocationOffset(int x, int y) {
            Point location = isShowing() ? getLocationOnScreen() : getLocation();
            Font currentFont = getFont() == null ? new Font(Font.DIALOG, Font.PLAIN, 12) : getFont();
            FontMetrics metrics = getFontMetrics(currentFont);
            int relativeX = Math.max(0, x - location.x);
            int insertion = 0;
            while (insertion < getText().length() && metrics.stringWidth(getText().substring(0, insertion + 1)) <= relativeX) {
                insertion++;
            }
            return TextHitInfo.leading(insertion);
        }

        @Override
        public int getInsertPositionOffset() {
            return getCaretPosition();
        }

        @Override
        public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes) {
            String value = getText();
            if (beginIndex < 0 || endIndex < beginIndex || endIndex > value.length()) {
                throw new IllegalArgumentException("Invalid committed text range");
            }
            return new PlainAttributedCharacterIterator(value.substring(beginIndex, endIndex));
        }

        @Override
        public int getCommittedTextLength() {
            return getText().length();
        }

        @Override
        public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
            return null;
        }

        @Override
        public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
            String selected = TextComponent.this.getSelectedText();
            return selected == null ? null : new PlainAttributedCharacterIterator(selected);
        }
    }

    private synchronized void readTextFromPeer() {
        String value = getPeerValue();
        if (value != null && !value.equals(text)) {
            text = value;
            readSelectionFromPeer();
            clampSelection();
            fireTextValueChanged();
        }
    }

    private void dispatchCompositionEvent(String type, String data, long when) {
        String value = data == null ? "" : data;
        int committed = "compositionend".equals(type) ? value.length() : 0;
        TextHitInfo caret = TextHitInfo.afterOffset(value.length());
        dispatchEvent(new InputMethodEvent(this, InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, when, new PlainAttributedCharacterIterator(value), committed, caret, caret));
    }

    private void fireTextValueChanged() {
        TextEvent event = new TextEvent(this, TextEvent.TEXT_VALUE_CHANGED);
        dispatchEvent(event);
    }

    private void clampSelection() {
        selectionStart = Math.min(selectionStart, text.length());
        selectionEnd = Math.min(selectionEnd, text.length());
        caretPosition = Math.min(caretPosition, text.length());
    }

    private static String normalize(String text) {
        return text == null ? "" : text;
    }
}
