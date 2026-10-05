package org.webawt.internal.browser;

import elemental2.dom.DomGlobal;
import java.util.ArrayList;
import java.util.List;
import org.webawt.internal.peer.BrowserScheduler;

public final class Elemental2BrowserScheduler implements BrowserScheduler {
    private final List<Double> timers = new ArrayList<>();

    @Override
    public void schedule(Runnable task) {
        double[] id = new double[1];
        id[0] = DomGlobal.setTimeout(arguments -> {
            timers.remove(Double.valueOf(id[0]));
            task.run();
        }, 0);
        timers.add(id[0]);
    }

    @Override
    public void cancelPending() {
        for (double timer : timers) {
            DomGlobal.clearTimeout(timer);
        }
        timers.clear();
    }
}
