package org.webawt.internal.peer;

public interface DesktopPeer {
    boolean browse(String uri);
    boolean mail(String uri);
    void requestForeground();
    void openHelpViewer();
    void setEventHandler(PeerDesktopEventHandler handler);
}
