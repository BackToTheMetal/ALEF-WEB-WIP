package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;

final class Elemental2CheckboxPeer extends Elemental2DomPeer {
    private final HTMLInputElement input;
    private final HTMLElement caption;
    private final String display;

    Elemental2CheckboxPeer() {
        super("label");
        input = (HTMLInputElement) DomGlobal.document.createElement("input");
        input.type = "checkbox";
        caption = (HTMLElement) DomGlobal.document.createElement("span");
        element.style.setProperty("display", "-webkit-box");
        element.style.setProperty("display", "flex");
        display = element.style.display;
        element.style.setProperty("-webkit-box-align", "center");
        element.style.setProperty("align-items", "center");
        element.style.setProperty("padding", "2px");
        element.style.setProperty("box-sizing", "border-box");
        element.style.setProperty("white-space", "pre");
        input.style.setProperty("font", "inherit");
        input.style.setProperty("margin", "0 4px 0 0");
        input.style.setProperty("flex", "0 0 auto");
        input.style.setProperty("width", "1em");
        input.style.setProperty("height", "1em");
        input.style.setProperty("min-width", "13px");
        input.style.setProperty("min-height", "13px");
        caption.style.setProperty("overflow", "hidden");
        element.appendChild(input);
        element.appendChild(caption);
    }

    @Override
    protected String visibleDisplay() {
        return display;
    }

    @Override
    protected HTMLElement focusElement() {
        return input;
    }

    @Override
    protected boolean acceptsInputEvent(Event event) {
        return !"click".equals(event.type) || event.target == input;
    }

    @Override
    public void setText(String text) {
        caption.textContent = text == null ? "" : text;
        input.setAttribute("aria-label", text == null ? "" : text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.disabled = !enabled;
        caption.style.setProperty("opacity", enabled ? "" : "0.6");
    }

    @Override
    public void setInputType(String type) {
        input.type = type;
    }

    @Override
    public void setInputGroup(String group) {
        input.name = group == null ? "" : group;
    }

    @Override
    public boolean isSelected() {
        return input.checked;
    }

    @Override
    public void setSelected(boolean selected) {
        input.checked = selected;
    }
}
