package org.webawt.internal.peer;

public interface PeerDragSourceListener {
    void handle(int type, int userAction, int targetActions, int modifiers, int x, int y, boolean success);
}
