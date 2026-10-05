package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

final class Elemental2LabelPeer extends Elemental2DomPeer {
    private final HTMLElement caption;
    private final String display;

    Elemental2LabelPeer() {
        super("span");
        caption = (HTMLElement) DomGlobal.document.createElement("span");
        element.style.setProperty("display", "-webkit-box");
        element.style.setProperty("display", "flex");
        display = element.style.display;
        element.style.setProperty("-webkit-box-align", "center");
        element.style.setProperty("align-items", "center");
        element.style.setProperty("padding", "2px");
        element.style.setProperty("box-sizing", "border-box");
        element.style.setProperty("overflow", "hidden");
        caption.style.setProperty("white-space", "pre");
        caption.style.setProperty("flex", "0 0 auto");
        element.appendChild(caption);
    }

    @Override
    protected String visibleDisplay() {
        return display;
    }

    @Override
    public void setText(String text) {
        caption.textContent = text == null ? "" : text;
    }

    @Override
    public void setTextAlignment(int alignment) {
        element.style.setProperty("-webkit-box-pack", alignment == 1 ? "center" : alignment == 2 ? "end" : "start");
        element.style.setProperty("justify-content", alignment == 1 ? "center" : alignment == 2 ? "flex-end" : "flex-start");
    }
}
