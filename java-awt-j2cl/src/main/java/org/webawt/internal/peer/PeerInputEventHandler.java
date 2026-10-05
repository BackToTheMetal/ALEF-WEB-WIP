package org.webawt.internal.peer;

@FunctionalInterface
public interface PeerInputEventHandler {
    boolean handle(PeerInputEvent event);
}
