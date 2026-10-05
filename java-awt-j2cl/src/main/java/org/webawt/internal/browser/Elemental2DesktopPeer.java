package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import org.webawt.internal.peer.DesktopPeer;
import org.webawt.internal.peer.PeerDesktopEvent;
import org.webawt.internal.peer.PeerDesktopEventHandler;

public final class Elemental2DesktopPeer implements DesktopPeer {
    private static final Elemental2DesktopPeer INSTANCE = new Elemental2DesktopPeer();
    private PeerDesktopEventHandler eventHandler;

    private boolean attached;
    private final EventListener visibilityListener = event -> dispatchVisibilityChange();
    private final EventListener closeListener = event -> {
            if (eventHandler != null && eventHandler.handle(PeerDesktopEvent.QUIT_REQUEST)) {
                event.preventDefault();
            }
        };

    private Elemental2DesktopPeer() {
    }

    public void attach() {
        if (attached) {
            return;
        }
        attached = true;
        DomGlobal.document.addEventListener("visibilitychange", visibilityListener);
        DomGlobal.window.addEventListener("beforeunload", closeListener);
    }

    public void detach() {
        DomGlobal.document.removeEventListener("visibilitychange", visibilityListener);
        DomGlobal.window.removeEventListener("beforeunload", closeListener);
        eventHandler = null;
        attached = false;
    }

    public static Elemental2DesktopPeer getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean browse(String uri) {
        return DomGlobal.window.open(uri, "_blank", "noopener") != null;
    }

    @Override
    public boolean mail(String uri) {
        return DomGlobal.window.open(uri, "_self") != null;
    }

    @Override
    public void requestForeground() {
        DomGlobal.window.focus();
    }

    @Override
    public void openHelpViewer() {
    }

    @Override
    public void setEventHandler(PeerDesktopEventHandler handler) {
        eventHandler = handler;
    }

    private void dispatchVisibilityChange() {
        if (eventHandler == null) {
            return;
        }
        boolean hidden = "hidden".equals(DomGlobal.document.visibilityState);
        eventHandler.handle(hidden ? PeerDesktopEvent.APP_HIDDEN : PeerDesktopEvent.APP_UNHIDDEN);
        eventHandler.handle(hidden ? PeerDesktopEvent.APP_BACKGROUND : PeerDesktopEvent.APP_FOREGROUND);
    }
}
