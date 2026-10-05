package org.webawt.internal.browser;

import org.webawt.internal.peer.DomPeer;
import org.webawt.internal.peer.DomPeerFactory;

public final class Elemental2DomPeerFactory implements DomPeerFactory {
    @Override
    public DomPeer create(String elementName) {
        if ("dialog".equals(elementName)) {
            return new Elemental2DialogPeer();
        }
        if ("checkbox".equals(elementName)) {
            return new Elemental2CheckboxPeer();
        }
        if ("span".equals(elementName)) {
            return new Elemental2LabelPeer();
        }
        return "canvas".equals(elementName) ? new Elemental2CanvasPeer() : new Elemental2DomPeer(elementName);
    }
}
