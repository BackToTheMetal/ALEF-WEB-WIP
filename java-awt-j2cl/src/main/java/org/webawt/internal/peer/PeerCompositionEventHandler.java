package org.webawt.internal.peer;

@FunctionalInterface
public interface PeerCompositionEventHandler {
    void handle(String type, String data, long when);
}
