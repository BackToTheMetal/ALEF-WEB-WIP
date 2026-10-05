package org.webawt.samples.dualshowcase;

public interface FontLoading {
    void load(String path, Runnable loaded, Runnable failed);
}
