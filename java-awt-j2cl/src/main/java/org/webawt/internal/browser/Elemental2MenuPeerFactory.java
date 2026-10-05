package org.webawt.internal.browser;

import org.webawt.internal.peer.MenuPeer;
import org.webawt.internal.peer.MenuPeerFactory;

public final class Elemental2MenuPeerFactory implements MenuPeerFactory {
    @Override
    public MenuPeer create(boolean menuBar) {
        return new Elemental2MenuPeer(menuBar);
    }
}
