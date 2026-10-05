package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLButtonElement;

public final class Elemental2DialogPeer extends Elemental2DomPeer {
    private HTMLElement titleBar;
    private HTMLElement caption;
    private HTMLButtonElement closeButton;
    private EventListener closeListener;
    private Runnable closeHandler;

    public Elemental2DialogPeer() {
        super("dialog");
        element.style.setProperty("box-sizing", "border-box");
        element.style.setProperty("margin", "0");
        element.style.setProperty("padding", "0");
        element.style.setProperty("border", "0");
        element.style.setProperty("max-width", "none");
        element.style.setProperty("max-height", "none");
        element.style.setProperty("overflow", "hidden");
        element.style.setProperty("z-index", "1000");
    }

    @Override
    public void configureDialog(String title, boolean decorated, int border, int titleHeight, Runnable closeHandler) {
        this.closeHandler = closeHandler;
        element.setAttribute("aria-label", title);
        element.style.setProperty("box-shadow", decorated ? "inset 1px 1px #fff, inset -1px -1px #808080, 0 5px 16px #0006" : "none");
        element.style.setProperty("border-radius", decorated ? "3px" : "0");
        if (decorated && titleBar == null) {
            createTitleBar();
        }
        if (titleBar != null) {
            titleBar.style.setProperty("display", decorated ? "flex" : "none");
            titleBar.style.setProperty("left", border + "px");
            titleBar.style.setProperty("right", border + "px");
            titleBar.style.setProperty("top", border + "px");
            titleBar.style.setProperty("height", titleHeight + "px");
            caption.textContent = title;
            closeButton.setAttribute("aria-label", title.isEmpty() ? "Close dialog" : "Close " + title);
        }
    }

    private void createTitleBar() {
        titleBar = (HTMLElement) DomGlobal.document.createElement("div");
        titleBar.setAttribute("data-webawt-dialog-title", "");
        titleBar.style.setProperty("position", "absolute");
        titleBar.style.setProperty("align-items", "center");
        titleBar.style.setProperty("background", "linear-gradient(#fff 65%, #e4e4e4)");
        titleBar.style.setProperty("color", "#111");
        titleBar.style.setProperty("font-weight", "bold");
        titleBar.style.setProperty("user-select", "none");
        caption = (HTMLElement) DomGlobal.document.createElement("span");
        caption.style.setProperty("flex", "1");
        caption.style.setProperty("min-width", "0");
        caption.style.setProperty("padding-left", "28px");
        caption.style.setProperty("text-align", "center");
        caption.style.setProperty("overflow", "hidden");
        caption.style.setProperty("text-overflow", "ellipsis");
        caption.style.setProperty("white-space", "nowrap");
        closeButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
        closeButton.type = "button";
        closeButton.textContent = "\u00d7";
        closeButton.style.setProperty("box-sizing", "border-box");
        closeButton.style.setProperty("width", "28px");
        closeButton.style.setProperty("height", "22px");
        closeButton.style.setProperty("padding", "0");
        closeButton.style.setProperty("font", "bold 18px/18px sans-serif");
        closeButton.style.setProperty("color", "#111");
        closeListener = event -> {
            event.stopPropagation();
            if (closeHandler != null) {
                closeHandler.run();
            }
        };
        closeButton.addEventListener("click", closeListener);
        titleBar.appendChild(caption);
        titleBar.appendChild(closeButton);
        element.appendChild(titleBar);
    }

    @Override
    public void detach() {
        if (closeButton != null) {
            closeButton.removeEventListener("click", closeListener);
        }
        closeHandler = null;
        super.detach();
    }
}
