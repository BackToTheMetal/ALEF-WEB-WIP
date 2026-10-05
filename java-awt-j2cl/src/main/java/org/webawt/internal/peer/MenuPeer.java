package org.webawt.internal.peer;

public interface MenuPeer {
    void attachTo(DomPeer parent);
    void setEntries(MenuPeerEntry[] entries);
    void setFont(String cssFont);
    void setHelpMenu(int index);
    int getPreferredHeight();
    void show(int x, int y);
    void showPopup(DomPeer origin, int x, int y);
    void hide();
    void detach();
    boolean isAttached();
}
