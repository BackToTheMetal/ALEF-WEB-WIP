package org.webawt.internal.peer;

public interface FontMeasurementProvider {
    // Width, font ascent and font descent in untransformed logical pixels; null if unavailable.
    double[] measure(String cssFont, String text);
}
