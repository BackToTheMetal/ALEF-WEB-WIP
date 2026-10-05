package org.webawt.samples.dualshowcase;

import elemental2.dom.DomGlobal;
import org.webawt.internal.browser.Elemental2CanvasPeer;
import org.webawt.internal.peer.WebAwtRuntime;

final class BrowserDiagnostics {
    private BrowserDiagnostics() {
    }

    static double[] performance() {
        PerformanceScenarios.curveWorkload(2);
        double start = DomGlobal.performance.now();
        PerformanceScenarios.curveWorkload(25);
        double elapsed = DomGlobal.performance.now() - start;
        Elemental2CanvasPeer peer = new Elemental2CanvasPeer();
        int[] before = Elemental2CanvasPeer.imageCacheStatistics();
        int[] pixels = new int[4096];
        java.util.Arrays.fill(pixels, 0xff123456);
        for (int index = 0; index < 20; index++) { peer.drawImage(pixels, 64, 64, 0, 0, 64, 64, 0, 0, 64, 64); }
        int[] repeated = Elemental2CanvasPeer.imageCacheStatistics();
        for (int index = 0; index < 8; index++) {
            pixels[0]++;
            peer.drawImage(pixels, 64, 64, 0, 0, 64, 64, 0, 0, 64, 64);
        }
        int[] changed = Elemental2CanvasPeer.imageCacheStatistics();
        peer.detach();
        peer.detach();
        int[] after = Elemental2CanvasPeer.imageCacheStatistics();
        double largeStart = DomGlobal.performance.now();
        Elemental2CanvasPeer largePeer = new Elemental2CanvasPeer();
        int[] largePixels = new int[512 * 513];
        java.util.Arrays.fill(largePixels, 0xff123456);
        for (int index = 0; index < 2; index++) { largePeer.drawImage(largePixels, 512, 513, 0, 0, 512, 513, 0, 0, 128, 128); }
        int[] largeStats = Elemental2CanvasPeer.imageCacheStatistics();
        largePeer.detach();
        double largeElapsed = DomGlobal.performance.now() - largeStart;
        double scaleStart = DomGlobal.performance.now();
        PerformanceScenarios.scalingWorkload();
        double scaleElapsed = DomGlobal.performance.now() - scaleStart;
        double layoutStart = DomGlobal.performance.now();
        PerformanceScenarios.layoutWorkload();
        double layoutElapsed = DomGlobal.performance.now() - layoutStart;
        return new double[] {elapsed, repeated[0] - before[0], repeated[1] - before[1], changed[0] - repeated[0], changed[2] - before[2], changed[3] - before[3], after[2] - before[2], after[3] - before[3], largeElapsed, scaleElapsed, layoutElapsed, largeStats[0] - after[0], largeStats[2] - after[2], largeStats[3] - after[3]};
    }

    static int[] resources() {
        int[] cache = Elemental2CanvasPeer.imageCacheStatistics();
        return new int[] {cache[2], cache[3], WebAwtRuntime.pendingTaskCount(), java.awt.Window.getWindows().length, java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == null ? 0 : 1};
    }
}
