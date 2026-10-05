package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import org.webawt.internal.peer.DisplayMetricsProvider;

public final class Elemental2DisplayMetricsProvider implements DisplayMetricsProvider {
    @Override
    public int getViewportWidth() {
        return DomGlobal.window.innerWidth;
    }

    @Override
    public int getViewportHeight() {
        return DomGlobal.window.innerHeight;
    }

    @Override
    public double getDeviceScaleFactor() {
        return DomGlobal.window.devicePixelRatio;
    }
}
