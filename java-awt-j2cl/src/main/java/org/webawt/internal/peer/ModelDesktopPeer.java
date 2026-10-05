package org.webawt.internal.peer;

final class ModelDesktopPeer implements DesktopPeer {
    @Override
    public boolean browse(String uri) {
        return false;
    }

    @Override
    public boolean mail(String uri) {
        return false;
    }

    @Override
    public void requestForeground() {
    }

    @Override
    public void openHelpViewer() {
    }

    @Override
    public void setEventHandler(PeerDesktopEventHandler handler) {
    }
}
