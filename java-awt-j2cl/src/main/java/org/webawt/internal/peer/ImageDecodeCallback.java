package org.webawt.internal.peer;

public interface ImageDecodeCallback {
    void onDecoded(int width, int height, int[] argbPixels);

    void onError();
}
