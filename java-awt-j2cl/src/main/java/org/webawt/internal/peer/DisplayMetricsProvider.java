package org.webawt.internal.peer;

public interface DisplayMetricsProvider {
    int getViewportWidth();
    int getViewportHeight();
    double getDeviceScaleFactor();
}
