package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.ViewCSS;
import org.webawt.internal.peer.SystemColorProvider;

public final class Elemental2SystemColorProvider implements SystemColorProvider {
    @Override
    public int getSystemColor(int index, int fallbackRgb) {
        String keyword = keyword(index);
        if (keyword == null || DomGlobal.document.body == null) {
            return fallbackRgb;
        }
        HTMLElement probe = (HTMLElement) DomGlobal.document.createElement("span");
        probe.style.color = keyword;
        probe.style.display = "none";
        DomGlobal.document.body.appendChild(probe);
        String computed = ((ViewCSS) (Object) DomGlobal.window).getComputedStyle(probe).color;
        DomGlobal.document.body.removeChild(probe);
        return parseRgb(computed, fallbackRgb);
    }

    private static String keyword(int index) {
        switch (index) {
            case 1:
            case 14:
                return "Highlight";
            case 2:
            case 15:
                return "HighlightText";
            case 4:
            case 5:
            case 6:
            case 16:
                return "GrayText";
            case 10:
            case 12:
            case 17:
            case 19:
            case 20:
            case 21:
            case 22:
                return "ButtonFace";
            case 11:
            case 18:
                return "ButtonText";
            case 0:
            case 3:
            case 7:
            case 8:
            case 23:
            case 24:
                return "Canvas";
            case 9:
            case 13:
            case 25:
                return "CanvasText";
            default:
                return null;
        }
    }

    private static int parseRgb(String value, int fallbackRgb) {
        if (value == null) {
            return fallbackRgb;
        }
        int open = value.indexOf('(');
        int close = value.indexOf(')');
        if (open < 0 || close <= open) {
            return fallbackRgb;
        }
        String[] components = value.substring(open + 1, close).split(",");
        if (components.length < 3) {
            return fallbackRgb;
        }
        try {
            int red = parseComponent(components[0]);
            int green = parseComponent(components[1]);
            int blue = parseComponent(components[2]);
            return red << 16 | green << 8 | blue;
        } catch (NumberFormatException exception) {
            return fallbackRgb;
        }
    }

    private static int parseComponent(String value) {
        return Math.max(0, Math.min(255, (int) Math.round(Double.parseDouble(value.trim()))));
    }
}
