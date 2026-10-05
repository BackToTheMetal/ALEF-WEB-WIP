package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAudioElement;
import org.webawt.internal.peer.ToolkitProvider;

public final class Elemental2ToolkitProvider implements ToolkitProvider {
    private static final String BEEP_AUDIO = "data:audio/wav;base64,UklGRjQAAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YRAAAACAq8jY2MjAq4CAgICAgIA=";

    @Override
    public void beep() {
        HTMLAudioElement audio = (HTMLAudioElement) DomGlobal.document.createElement("audio");
        audio.src = BEEP_AUDIO;
        audio.play();
    }

    @Override
    public int getMenuShortcutKeyMaskEx() {
        return DomGlobal.navigator.platform.contains("Mac") ? 256 : 128;
    }
}
