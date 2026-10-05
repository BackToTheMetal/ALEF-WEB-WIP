package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.CSSProperties;
import elemental2.dom.CompositionEvent;
import elemental2.dom.Element;
import elemental2.dom.DragEvent;
import elemental2.dom.DataTransfer;
import elemental2.dom.HTMLElement;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.MouseEvent;
import elemental2.dom.Node;
import elemental2.dom.WheelEvent;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.webawt.internal.peer.DomPeer;
import org.webawt.internal.peer.PeerInputEvent;
import org.webawt.internal.peer.PeerCompositionEventHandler;
import org.webawt.internal.peer.PeerDragEvent;
import org.webawt.internal.peer.PeerDragEventHandler;
import org.webawt.internal.peer.PeerInputEventHandler;

public class Elemental2DomPeer implements DomPeer {
    private static int nextId;
    protected final HTMLElement element;
    private EventListener actionListener;
    private EventListener textChangeListener;
    private EventListener itemChangeListener;
    private EventListener inputListener;
    private EventListener compositionListener;
    private EventListener dragListener;
    private EventListener scrollListener;
    private PeerInputEventHandler inputEventHandler;
    private PeerCompositionEventHandler compositionEventHandler;
    private PeerDragEventHandler dragEventHandler;
    private Runnable actionHandler;
    private boolean composing;

    public Elemental2DomPeer(String elementName) {
        element = (HTMLElement) DomGlobal.document.createElement(elementName);
        element.id = "webawt-" + elementName + "-" + nextId++;
        element.style.position = "absolute";
    }

    @Override
    public String getElementId() {
        return element.id;
    }

    @Override
    public void attachToElement(String parentElementId) {
        Element parent = DomGlobal.document.getElementById(parentElementId);
        if (parent == null) {
            throw new IllegalArgumentException("No element exists with id: " + parentElementId);
        }
        parent.appendChild(element);
    }

    @Override
    public void attachTo(DomPeer parent) {
        Element parentElement = DomGlobal.document.getElementById(parent.getElementId());
        if (parentElement == null) {
            throw new IllegalStateException("Parent peer is not attached");
        }
        parentElement.appendChild(element);
    }

    @Override
    public void detach() {
        setActionHandler(null);
        setTextChangeHandler(null);
        setCompositionEventHandler(null);
        setDragEventHandler(null);
        setItemChangeHandler(null);
        setInputEventHandler(null);
        setScrollHandler(null);
        Node parent = element.parentNode;
        if (parent != null) {
            parent.removeChild(element);
        }
    }

    @Override
    public boolean isAttached() {
        return element.parentNode != null;
    }

    @Override
    public int[] getLocationOnScreen() {
        var bounds = element.getBoundingClientRect();
        return new int[] {(int) Math.round(bounds.left), (int) Math.round(bounds.top)};
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        element.style.left = x + "px";
        element.style.top = y + "px";
        element.style.width = CSSProperties.WidthUnionType.of(Math.max(0, width) + "px");
        element.style.height = CSSProperties.HeightUnionType.of(Math.max(0, height) + "px");
    }

    @Override
    public void setVisible(boolean visible) {
        element.style.display = visible ? visibleDisplay() : "none";
        if ("DIALOG".equals(element.tagName)) {
            if (visible) {
                element.setAttribute("open", "");
            } else {
                element.removeAttribute("open");
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        element.setAttribute("aria-disabled", enabled ? "false" : "true");
        element.style.pointerEvents = enabled ? "" : "none";
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).disabled = !enabled;
        } else if (element instanceof HTMLTextAreaElement) {
            ((HTMLTextAreaElement) element).disabled = !enabled;
        } else if (element instanceof HTMLSelectElement) {
            ((HTMLSelectElement) element).disabled = !enabled;
        } else if (element instanceof HTMLButtonElement) {
            ((HTMLButtonElement) element).disabled = !enabled;
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        if (!focusable) {
            focusElement().tabIndex = -1;
        } else if ("CANVAS".equals(element.tagName) || element instanceof HTMLInputElement || element instanceof HTMLTextAreaElement || element instanceof HTMLButtonElement || element instanceof HTMLSelectElement) {
            focusElement().tabIndex = 0;
        }
    }

    @Override
    public void setTextSelection(int start, int end) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).setSelectionRange(start, end);
        } else if (element instanceof HTMLTextAreaElement) {
            ((HTMLTextAreaElement) element).setSelectionRange(start, end);
        }
    }

    @Override
    public int[] getTextSelection() {
        if (element instanceof HTMLInputElement) {
            HTMLInputElement input = (HTMLInputElement) element;
            return selection(input.selectionStart, input.selectionEnd, input.selectionDirection);
        }
        if (element instanceof HTMLTextAreaElement) {
            HTMLTextAreaElement input = (HTMLTextAreaElement) element;
            return selection(input.selectionStart, input.selectionEnd, ((TextSelectionProperties) (Object) input).getSelectionDirection());
        }
        return null;
    }

    private static int[] selection(double start, double end, String direction) {
        return new int[] {(int) start, (int) end, (int) ("backward".equals(direction) ? start : end)};
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private interface TextSelectionProperties {
        @JsProperty
        String getSelectionDirection();
    }

    @Override
    public void setForeground(String cssColor) {
        element.style.color = cssColor == null ? "" : cssColor;
    }

    @Override
    public void setBackground(String cssColor) {
        element.style.backgroundColor = cssColor == null ? "" : cssColor;
    }

    @Override
    public void setFont(String cssFont) {
        element.style.setProperty("font", cssFont == null ? "" : cssFont);
    }

    @Override
    public void setText(String text) {
        if (element instanceof HTMLInputElement) {
            element.setAttribute("aria-label", text == null ? "" : text);
        } else {
            element.textContent = text;
        }
    }

    @Override
    public void setTextAlignment(int alignment) {
        element.style.setProperty("text-align", alignment == 1 ? "center" : alignment == 2 ? "right" : "left");
    }

    @Override
    public void setInputGroup(String group) {
    }

    protected String visibleDisplay() {
        return "";
    }

    protected HTMLElement focusElement() {
        return element;
    }

    protected boolean acceptsInputEvent(elemental2.dom.Event event) {
        return event.target == element;
    }

    private HTMLElement inputEventElement(String type) {
        return "focus".equals(type) || "blur".equals(type) ? focusElement() : element;
    }

    @Override
    public String getValue() {
        if (element instanceof HTMLInputElement) {
            return ((HTMLInputElement) element).value;
        }
        if (element instanceof HTMLTextAreaElement) {
            return ((HTMLTextAreaElement) element).value;
        }
        return element.textContent;
    }

    @Override
    public void setValue(String value) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).value = value;
        } else if (element instanceof HTMLTextAreaElement) {
            ((HTMLTextAreaElement) element).value = value;
        } else {
            element.textContent = value;
        }
    }

    @Override
    public void setEditable(boolean editable) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).readOnly = !editable;
        } else if (element instanceof HTMLTextAreaElement) {
            ((HTMLTextAreaElement) element).readOnly = !editable;
        }
    }

    @Override
    public void setPassword(boolean password) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).type = password ? "password" : "text";
        }
    }

    @Override
    public void setInputType(String type) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).type = type;
        }
    }

    @Override
    public void setRange(int minimum, int maximum, int step, boolean vertical) {
        if (!(element instanceof HTMLInputElement)) {
            return;
        }
        HTMLInputElement input = (HTMLInputElement) element;
        input.min = Integer.toString(minimum);
        input.max = Integer.toString(maximum);
        input.step = Integer.toString(Math.max(1, step));
        input.setAttribute("aria-orientation", vertical ? "vertical" : "horizontal");
        input.setAttribute("orient", vertical ? "vertical" : "horizontal");
        input.style.setProperty("writing-mode", vertical ? "vertical-lr" : "horizontal-tb");
        input.style.setProperty("direction", vertical ? "rtl" : "ltr");
    }

    @Override
    public void setScrollPolicy(int policy) {
        String overflow = policy == 1 ? "scroll" : policy == 2 ? "hidden" : "auto";
        element.style.setProperty("overflow", overflow);
        element.setAttribute("role", "region");
    }

    @Override
    public void setScrollPosition(int x, int y) {
        element.scrollLeft = Math.max(0, x);
        element.scrollTop = Math.max(0, y);
    }

    @Override
    public int getScrollX() {
        return (int) Math.round(element.scrollLeft);
    }

    @Override
    public int getScrollY() {
        return (int) Math.round(element.scrollTop);
    }

    @Override
    public boolean isSelected() {
        return element instanceof HTMLInputElement && ((HTMLInputElement) element).checked;
    }

    @Override
    public void setSelected(boolean selected) {
        if (element instanceof HTMLInputElement) {
            ((HTMLInputElement) element).checked = selected;
        }
    }

    @Override
    public void setSelectionItems(String[] items, int[] selectedIndexes, boolean multiple, int visibleRows) {
        if (!(element instanceof HTMLSelectElement)) {
            return;
        }
        HTMLSelectElement select = (HTMLSelectElement) element;
        while (select.options.length > 0) {
            select.remove(0);
        }
        select.multiple = multiple;
        select.size = Math.max(0, visibleRows);
        for (int index = 0; index < items.length; index++) {
            HTMLOptionElement option = (HTMLOptionElement) DomGlobal.document.createElement("option");
            option.text = items[index];
            option.selected = contains(selectedIndexes, index);
            select.add(option);
        }
    }

    @Override
    public int[] getSelectedIndexes() {
        if (!(element instanceof HTMLSelectElement)) {
            return new int[0];
        }
        HTMLSelectElement select = (HTMLSelectElement) element;
        int count = 0;
        for (int index = 0; index < select.options.length; index++) {
            HTMLOptionElement option = select.options.item(index);
            count += option != null && option.selected ? 1 : 0;
        }
        int[] indexes = new int[count];
        int outputIndex = 0;
        for (int index = 0; index < select.options.length; index++) {
            HTMLOptionElement option = select.options.item(index);
            if (option != null && option.selected) {
                indexes[outputIndex++] = index;
            }
        }
        return indexes;
    }

    @Override
    public void setActionHandler(Runnable handler) {
        if (actionListener != null) {
            element.removeEventListener("click", actionListener);
            actionListener = null;
        }
        actionHandler = handler;
        if (handler != null && !(element instanceof HTMLInputElement)) {
            actionListener = event -> handler.run();
            element.addEventListener("click", actionListener);
        }
    }

    @Override
    public void setTextChangeHandler(Runnable handler) {
        if (textChangeListener != null) {
            element.removeEventListener("input", textChangeListener);
            textChangeListener = null;
        }
        if (handler != null) {
            textChangeListener = event -> handler.run();
            element.addEventListener("input", textChangeListener);
        }
    }

    @Override
    public void setCompositionEventHandler(PeerCompositionEventHandler handler) {
        if (compositionListener != null) {
            for (String type : compositionEventTypes()) {
                element.removeEventListener(type, compositionListener);
            }
            compositionListener = null;
        }
        compositionEventHandler = handler;
        if (handler != null) {
            compositionListener = this::dispatchCompositionEvent;
            for (String type : compositionEventTypes()) {
                element.addEventListener(type, compositionListener);
            }
        }
    }

    @Override
    public void setDragEventHandler(PeerDragEventHandler handler) {
        if (dragListener != null) {
            for (String type : dragEventTypes()) {
                element.removeEventListener(type, dragListener);
            }
            dragListener = null;
        }
        dragEventHandler = handler;
        if (handler != null) {
            dragListener = this::dispatchDragEvent;
            for (String type : dragEventTypes()) {
                element.addEventListener(type, dragListener);
            }
        }
    }

    @Override
    public void setDragSourceEnabled(boolean enabled) {
        element.draggable = enabled;
    }

    @Override
    public void setItemChangeHandler(Runnable handler) {
        if (itemChangeListener != null) {
            element.removeEventListener("change", itemChangeListener);
            itemChangeListener = null;
        }
        if (handler != null) {
            itemChangeListener = event -> handler.run();
            element.addEventListener("change", itemChangeListener);
        }
    }

    @Override
    public void setInputEventHandler(PeerInputEventHandler handler) {
        if (inputListener != null) {
            for (String type : inputEventTypes()) {
                inputEventElement(type).removeEventListener(type, inputListener);
            }
            inputListener = null;
        }
        inputEventHandler = handler;
        if (handler != null) {
            inputListener = this::dispatchInputEvent;
            for (String type : inputEventTypes()) {
                inputEventElement(type).addEventListener(type, inputListener);
            }
        }
    }

    @Override
    public void setScrollHandler(Runnable handler) {
        if (scrollListener != null) {
            element.removeEventListener("scroll", scrollListener);
            scrollListener = null;
        }
        if (handler != null) {
            scrollListener = event -> handler.run();
            element.addEventListener("scroll", scrollListener);
        }
    }

    @Override
    public void requestFocus() {
        focusElement().focus();
    }

    private static boolean contains(int[] values, int expected) {
        for (int value : values) {
            if (value == expected) {
                return true;
            }
        }
        return false;
    }

    private void dispatchInputEvent(elemental2.dom.Event event) {
        if (!acceptsInputEvent(event)) {
            return;
        }
        boolean consumed;
        if (event instanceof KeyboardEvent) {
            consumed = dispatchKeyboardEvent((KeyboardEvent) event);
        } else if (event instanceof WheelEvent) {
            consumed = inputEventHandler.handle(toWheelEvent((WheelEvent) event));
        } else if (event instanceof MouseEvent) {
            consumed = inputEventHandler.handle(toMouseEvent((MouseEvent) event));
        } else {
            int type = "focus".equals(event.type) ? PeerInputEvent.FOCUS_GAINED : PeerInputEvent.FOCUS_LOST;
            consumed = inputEventHandler.handle(new PeerInputEvent(type, (long) event.timeStamp, 0, 0, 0, 0, 0, 0, 0, 0, '\uffff', 0, 0));
        }
        if (consumed) {
            event.preventDefault();
        }
    }

    private void dispatchCompositionEvent(elemental2.dom.Event event) {
        if (event.target != element) {
            return;
        }
        composing = !"compositionend".equals(event.type);
        String data = event instanceof CompositionEvent && ((CompositionEvent) event).data != null ? ((CompositionEvent) event).data : "";
        compositionEventHandler.handle(event.type, data, (long) event.timeStamp);
    }

    private void dispatchDragEvent(elemental2.dom.Event event) {
        if (!(event instanceof DragEvent) || ((DragEvent) event).dataTransfer == null) {
            return;
        }
        DragEvent dragEvent = (DragEvent) event;
        DataTransfer transfer = dragEvent.dataTransfer;
        PeerDragEvent peerEvent = createPeerDragEvent(dragEvent, transfer);
        dragEventHandler.handle(peerEvent);
        if (peerEvent.getType() == PeerDragEvent.DRAG_START) {
            writeTransferData(event, transfer, peerEvent);
        } else if (peerEvent.getAcceptedAction() != 0) {
            event.preventDefault();
            transfer.dropEffect = toDropEffect(peerEvent.getAcceptedAction());
        }
    }

    private static PeerDragEvent createPeerDragEvent(DragEvent event, DataTransfer transfer) {
        int type = peerDragType(event.type);
        int sourceActions = fromEffectAllowed(transfer.effectAllowed);
        int dropAction = fromDropEffect(transfer.dropEffect);
        String[] mimeTypes = new String[transfer.types.length];
        String[] data = new String[transfer.types.length];
        for (int index = 0; index < mimeTypes.length; index++) {
            mimeTypes[index] = transfer.types.at(index);
            data[index] = transfer.getData(mimeTypes[index]);
        }
        return new PeerDragEvent(type, (int) Math.round(event.offsetX), (int) Math.round(event.offsetY), mouseModifiers(event), sourceActions, dropAction, mimeTypes, data);
    }

    private static void writeTransferData(elemental2.dom.Event event, DataTransfer transfer, PeerDragEvent peerEvent) {
        if (peerEvent.getSourceActions() == 0) {
            event.preventDefault();
            return;
        }
        transfer.effectAllowed = toEffectAllowed(peerEvent.getSourceActions());
        String[] mimeTypes = peerEvent.getMimeTypes();
        String[] data = peerEvent.getData();
        for (int index = 0; index < Math.min(mimeTypes.length, data.length); index++) {
            transfer.setData(mimeTypes[index], data[index]);
        }
    }

    private boolean dispatchKeyboardEvent(KeyboardEvent event) {
        int type = "keydown".equals(event.type) ? PeerInputEvent.KEY_PRESSED : PeerInputEvent.KEY_RELEASED;
        boolean inComposition = composing || event.isComposing || "Process".equals(event.key);
        char keyChar = character(event, inComposition);
        int keyCode = inComposition ? 0 : BrowserKeyMapping.keyCode(event.key, event.code, event.location);
        PeerInputEvent pressedOrReleased = new PeerInputEvent(type, (long) event.timeStamp, keyboardModifiers(event), 0, 0, 0, 0, 0, 0, keyCode, keyChar, event.location + 1, 0);
        boolean consumed = inputEventHandler.handle(pressedOrReleased);
        if (!consumed && type == PeerInputEvent.KEY_PRESSED && keyChar != '\uffff') {
            PeerInputEvent typed = new PeerInputEvent(PeerInputEvent.KEY_TYPED, (long) event.timeStamp, keyboardModifiers(event), 0, 0, 0, 0, 0, 0, 0, keyChar, 0, 0);
            consumed = inputEventHandler.handle(typed) || consumed;
        }
        if (!consumed && !inComposition && type == PeerInputEvent.KEY_PRESSED && "Enter".equals(event.key) && actionHandler != null && element instanceof HTMLInputElement) {
            actionHandler.run();
        }
        return consumed;
    }

    private static char character(KeyboardEvent event, boolean inComposition) {
        if (inComposition || event.metaKey || event.key == null || event.altKey && !event.getModifierState("AltGraph")) {
            return '\uffff';
        }
        char character;
        if ("Enter".equals(event.key)) {
            character = '\n';
        } else if ("Tab".equals(event.key)) {
            character = '\t';
        } else if ("Backspace".equals(event.key)) {
            character = '\b';
        } else if (event.key.length() == 1) {
            character = event.key.charAt(0);
        } else {
            return '\uffff';
        }
        if (event.ctrlKey && !event.getModifierState("AltGraph") && character >= 32) {
            char upper = Character.toUpperCase(character);
            return upper >= '@' && upper <= '_' ? (char) (upper & 31) : '\uffff';
        }
        return character;
    }

    private PeerInputEvent toMouseEvent(MouseEvent event) {
        int type = mouseType(event.type, event.buttons);
        int button = type == PeerInputEvent.MOUSE_PRESSED || type == PeerInputEvent.MOUSE_RELEASED || type == PeerInputEvent.MOUSE_CLICKED ? domButton(event.button) : 0;
        return new PeerInputEvent(type, (long) event.timeStamp, mouseModifiers(event), (int) Math.round(event.clientX - element.getBoundingClientRect().left), (int) Math.round(event.clientY - element.getBoundingClientRect().top), (int) Math.round(event.screenX), (int) Math.round(event.screenY), event.detail, button, 0, '\uffff', 0, 0);
    }

    private static PeerInputEvent toWheelEvent(WheelEvent event) {
        double divisor = event.deltaMode == WheelEvent.DOM_DELTA_PIXEL ? 100.0 : 1.0;
        double rotation = event.deltaY / divisor;
        return new PeerInputEvent(PeerInputEvent.MOUSE_WHEEL, (long) event.timeStamp, mouseModifiers(event), (int) Math.round(event.offsetX), (int) Math.round(event.offsetY), (int) Math.round(event.screenX), (int) Math.round(event.screenY), 0, 0, 0, '\uffff', 0, rotation);
    }

    private static int keyboardModifiers(KeyboardEvent event) {
        return modifierMask(event.shiftKey, event.ctrlKey, event.metaKey, event.altKey, 0) | (event.getModifierState("AltGraph") ? 8192 : 0);
    }

    private static int mouseModifiers(MouseEvent event) {
        return modifierMask(event.shiftKey, event.ctrlKey, event.metaKey, event.altKey, event.buttons);
    }

    private static int modifierMask(boolean shift, boolean control, boolean meta, boolean alt, int buttons) {
        int modifiers = shift ? 64 : 0;
        modifiers |= control ? 128 : 0;
        modifiers |= meta ? 256 : 0;
        modifiers |= alt ? 512 : 0;
        modifiers |= (buttons & 1) != 0 ? 1024 : 0;
        modifiers |= (buttons & 4) != 0 ? 2048 : 0;
        return modifiers | ((buttons & 2) != 0 ? 4096 : 0);
    }

    private static int domButton(int button) {
        return button == 1 ? 2 : button == 2 ? 3 : 1;
    }

    private static int mouseType(String type, int buttons) {
        if ("mousedown".equals(type)) {
            return PeerInputEvent.MOUSE_PRESSED;
        }
        if ("mouseup".equals(type)) {
            return PeerInputEvent.MOUSE_RELEASED;
        }
        if ("click".equals(type)) {
            return PeerInputEvent.MOUSE_CLICKED;
        }
        if ("mouseenter".equals(type)) {
            return PeerInputEvent.MOUSE_ENTERED;
        }
        if ("mouseleave".equals(type)) {
            return PeerInputEvent.MOUSE_EXITED;
        }
        return buttons == 0 ? PeerInputEvent.MOUSE_MOVED : PeerInputEvent.MOUSE_DRAGGED;
    }

    private static String[] inputEventTypes() {
        return new String[] {"focus", "blur", "keydown", "keyup", "mousedown", "mouseup", "click", "mousemove", "mouseenter", "mouseleave", "wheel"};
    }

    private static String[] compositionEventTypes() {
        return new String[] {"compositionstart", "compositionupdate", "compositionend"};
    }

    private static String[] dragEventTypes() {
        return new String[] {"dragstart", "dragenter", "dragover", "dragleave", "drop", "dragend"};
    }

    private static int peerDragType(String type) {
        if ("dragstart".equals(type)) {
            return PeerDragEvent.DRAG_START;
        }
        if ("dragenter".equals(type)) {
            return PeerDragEvent.DRAG_ENTER;
        }
        if ("dragover".equals(type)) {
            return PeerDragEvent.DRAG_OVER;
        }
        if ("dragleave".equals(type)) {
            return PeerDragEvent.DRAG_EXIT;
        }
        if ("drop".equals(type)) {
            return PeerDragEvent.DROP;
        }
        return PeerDragEvent.DRAG_END;
    }

    private static int fromEffectAllowed(String effectAllowed) {
        if ("copy".equals(effectAllowed)) {
            return 1;
        }
        if ("move".equals(effectAllowed)) {
            return 2;
        }
        if ("link".equals(effectAllowed)) {
            return 0x40000000;
        }
        if ("copyMove".equals(effectAllowed)) {
            return 3;
        }
        if ("copyLink".equals(effectAllowed)) {
            return 1 | 0x40000000;
        }
        if ("linkMove".equals(effectAllowed)) {
            return 2 | 0x40000000;
        }
        return "none".equals(effectAllowed) ? 0 : 3 | 0x40000000;
    }

    private static int fromDropEffect(String dropEffect) {
        if ("copy".equals(dropEffect)) {
            return 1;
        }
        if ("move".equals(dropEffect)) {
            return 2;
        }
        return "link".equals(dropEffect) ? 0x40000000 : 0;
    }

    private static String toEffectAllowed(int actions) {
        int value = actions & (3 | 0x40000000);
        if (value == 1) {
            return "copy";
        }
        if (value == 2) {
            return "move";
        }
        if (value == 0x40000000) {
            return "link";
        }
        if (value == 3) {
            return "copyMove";
        }
        if (value == (1 | 0x40000000)) {
            return "copyLink";
        }
        if (value == (2 | 0x40000000)) {
            return "linkMove";
        }
        return value == 0 ? "none" : "all";
    }

    private static String toDropEffect(int action) {
        if ((action & 1) != 0) {
            return "copy";
        }
        if ((action & 2) != 0) {
            return "move";
        }
        return (action & 0x40000000) != 0 ? "link" : "none";
    }
}
