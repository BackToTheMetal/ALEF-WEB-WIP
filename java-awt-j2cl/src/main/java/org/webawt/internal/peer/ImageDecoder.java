package org.webawt.internal.peer;

public interface ImageDecoder {
    void decode(String source, ImageDecodeCallback callback);

    default void dispose() {
    }
}
