package org.webawt.internal.peer;

final class ModelMenuPeer implements MenuPeer {
    private boolean attached;

    @Override
    public void attachTo(DomPeer parent) {
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        attached = true;
    }

    @Override
    public void setEntries(MenuPeerEntry[] entries) {
    }

    @Override
    public void setFont(String cssFont) {
    }

    @Override
    public void setHelpMenu(int index) {
    }

    @Override
    public int getPreferredHeight() {
        return 0;
    }

    @Override
    public void show(int x, int y) {
    }

    @Override
    public void showPopup(DomPeer origin, int x, int y) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void detach() {
        attached = false;
    }

    @Override
    public boolean isAttached() {
        return attached;
    }
}
