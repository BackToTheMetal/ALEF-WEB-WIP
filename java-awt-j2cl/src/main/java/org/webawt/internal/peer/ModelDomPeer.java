package org.webawt.internal.peer;

class ModelDomPeer implements DomPeer {
    private static int nextId;
    private final String elementId;
    private String text = "";
    private String value = "";
    private Runnable actionHandler;
    private Runnable textChangeHandler;
    private Runnable itemChangeHandler;
    private Runnable scrollHandler;
    private PeerInputEventHandler inputEventHandler;
    private PeerCompositionEventHandler compositionEventHandler;
    private PeerDragEventHandler dragEventHandler;
    private int[] selectedIndexes = new int[0];
    private boolean selected;
    private DomPeer parent;
    private boolean attached;
    private boolean visible = true;
    private boolean enabled = true;
    private boolean focused;
    private int x;
    private int y;
    private int width;
    private int height;
    private int scrollX;
    private int scrollY;
    private int selectionStart;
    private int selectionEnd;

    ModelDomPeer(String elementName) {
        elementId = "webawt-" + elementName + "-" + nextId++;
    }

    @Override
    public String getElementId() {
        return elementId;
    }

    @Override
    public void attachToElement(String parentElementId) {
        if (parentElementId == null) {
            throw new NullPointerException("parentElementId");
        }
        parent = null;
        attached = true;
    }

    @Override
    public void attachTo(DomPeer parent) {
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        this.parent = parent;
        attached = true;
    }

    @Override
    public void detach() {
        attached = false;
        parent = null;
    }

    @Override
    public boolean isAttached() {
        return attached;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setForeground(String cssColor) {
    }

    @Override
    public void setBackground(String cssColor) {
    }

    @Override
    public void setFont(String cssFont) {
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setTextAlignment(int alignment) {
    }

    @Override
    public void setInputGroup(String group) {
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setTextSelection(int start, int end) {
        selectionStart = start;
        selectionEnd = end;
    }

    @Override
    public int[] getTextSelection() {
        return new int[] {selectionStart, selectionEnd, selectionEnd};
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public void setPassword(boolean password) {
    }

    @Override
    public void setInputType(String type) {
    }

    @Override
    public void setRange(int minimum, int maximum, int step, boolean vertical) {
    }

    @Override
    public void setScrollPolicy(int policy) {
    }

    @Override
    public void setScrollPosition(int x, int y) {
        scrollX = Math.max(0, x);
        scrollY = Math.max(0, y);
    }

    @Override
    public int getScrollX() {
        return scrollX;
    }

    @Override
    public int getScrollY() {
        return scrollY;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void setSelectionItems(String[] items, int[] selectedIndexes, boolean multiple, int visibleRows) {
        this.selectedIndexes = copy(selectedIndexes);
    }

    @Override
    public int[] getSelectedIndexes() {
        return copy(selectedIndexes);
    }

    @Override
    public void setActionHandler(Runnable handler) {
        actionHandler = handler;
    }

    @Override
    public void setTextChangeHandler(Runnable handler) {
        textChangeHandler = handler;
    }

    @Override
    public void setCompositionEventHandler(PeerCompositionEventHandler handler) {
        compositionEventHandler = handler;
    }

    @Override
    public void setDragEventHandler(PeerDragEventHandler handler) {
        dragEventHandler = handler;
    }

    @Override
    public void setDragSourceEnabled(boolean enabled) {
    }

    @Override
    public void setItemChangeHandler(Runnable handler) {
        itemChangeHandler = handler;
    }

    @Override
    public void setInputEventHandler(PeerInputEventHandler handler) {
        inputEventHandler = handler;
    }

    @Override
    public void setScrollHandler(Runnable handler) {
        scrollHandler = handler;
    }

    @Override
    public void requestFocus() {
        focused = true;
    }

    void activate() {
        if (actionHandler != null) {
            actionHandler.run();
        }
    }

    void changeValue(String value) {
        this.value = value;
        if (textChangeHandler != null) {
            textChangeHandler.run();
        }
    }

    void changeSelected(boolean selected) {
        this.selected = selected;
        if (itemChangeHandler != null) {
            itemChangeHandler.run();
        }
    }

    boolean dispatchInput(PeerInputEvent event) {
        return inputEventHandler != null && inputEventHandler.handle(event);
    }

    void dispatchComposition(String type, String data, long when) {
        if (compositionEventHandler != null) {
            compositionEventHandler.handle(type, data, when);
        }
    }

    void dispatchDrag(PeerDragEvent event) {
        if (dragEventHandler != null) {
            dragEventHandler.handle(event);
        }
    }

    void scrollTo(int x, int y) {
        setScrollPosition(x, y);
        if (scrollHandler != null) {
            scrollHandler.run();
        }
    }

    private static int[] copy(int[] values) {
        int[] result = new int[values.length];
        System.arraycopy(values, 0, result, 0, values.length);
        return result;
    }
}
