package org.webawt.internal.peer;

public interface DomPeer {
    String getElementId();
    void attachToElement(String parentElementId);
    void attachTo(DomPeer parent);
    void detach();
    boolean isAttached();
    /** Browser screen coordinates are CSS pixels in the viewport, as used by fixed popups.
     * Model-only peers return null so Component can use its hierarchy coordinates. */
    default int[] getLocationOnScreen() { return null; }
    void setBounds(int x, int y, int width, int height);
    void setVisible(boolean visible);
    void setEnabled(boolean enabled);
    default void configureDialog(String title, boolean decorated, int border, int titleHeight, Runnable closeHandler) {
    }
    default void setFocusable(boolean focusable) {
    }
    default void setTextSelection(int start, int end) {
    }
    default int[] getTextSelection() {
        return null;
    }
    void setForeground(String cssColor);
    void setBackground(String cssColor);
    void setFont(String cssFont);
    void setText(String text);
    void setTextAlignment(int alignment);
    void setInputGroup(String group);
    String getValue();
    void setValue(String value);
    void setEditable(boolean editable);
    void setPassword(boolean password);
    void setInputType(String type);
    void setRange(int minimum, int maximum, int step, boolean vertical);
    void setScrollPolicy(int policy);
    void setScrollPosition(int x, int y);
    int getScrollX();
    int getScrollY();
    boolean isSelected();
    void setSelected(boolean selected);
    void setSelectionItems(String[] items, int[] selectedIndexes, boolean multiple, int visibleRows);
    int[] getSelectedIndexes();
    void setActionHandler(Runnable handler);
    void setTextChangeHandler(Runnable handler);
    void setCompositionEventHandler(PeerCompositionEventHandler handler);
    void setDragEventHandler(PeerDragEventHandler handler);
    void setDragSourceEnabled(boolean enabled);
    void setItemChangeHandler(Runnable handler);
    void setInputEventHandler(PeerInputEventHandler handler);
    void setScrollHandler(Runnable handler);
    void requestFocus();
}
