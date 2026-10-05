package org.webawt.internal.peer;

public interface BrowserScheduler {
    void schedule(Runnable task);

    default void cancelPending() {
    }
}
