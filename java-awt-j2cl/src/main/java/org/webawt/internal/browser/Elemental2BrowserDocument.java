package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;

public final class Elemental2BrowserDocument implements BrowserDocument {
    @Override
    public void setTextContent(String elementId, String textContent) {
        Element element = DomGlobal.document.getElementById(elementId);
        if (element == null) {
            throw new IllegalArgumentException("No element exists with id: " + elementId);
        }
        element.textContent = textContent;
    }
}
