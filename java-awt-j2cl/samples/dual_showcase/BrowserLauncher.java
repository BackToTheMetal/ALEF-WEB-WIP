package org.webawt.samples.dualshowcase;

import jsinterop.annotations.JsMethod;
import org.webawt.WebAwt;

public final class BrowserLauncher {
    private static DualShowcase showcase;

    private BrowserLauncher() {
    }

    @JsMethod
    public static void mount(String rootElementId) {
        unmount();
        WebAwt.install(rootElementId);
        showcase = new DualShowcase("WebAWT / J2CL / Elemental2", new BrowserClipboardAccess());
        showcase.setFontLoading((path, loaded, failed) -> WebAwt.loadFont(FontScenarios.FAMILY, path).then(ignored -> {
            FontScenarios.setReference(new java.awt.Font(FontScenarios.FAMILY, java.awt.Font.PLAIN, 24));
            loaded.run();
            return null;
        }, error -> {
            failed.run();
            return null;
        }));
        showcase.show();
    }

    @JsMethod
    public static double[] performanceDiagnostics() {
        return BrowserDiagnostics.performance();
    }

    @JsMethod
    public static int[] resourceDiagnostics() {
        return BrowserDiagnostics.resources();
    }

    @JsMethod
    public static void queueThenUnmount() {
        java.awt.EventQueue.invokeLater(() -> { throw new AssertionError("disposed application's queued callback ran"); });
        unmount();
    }

    @JsMethod
    public static double[] textLayoutDiagnostics() {
        return AttributedScenarios.diagnostics();
    }

    @JsMethod
    public static double[] fontMeasurements() {
        return FontScenarios.measurements();
    }

    @JsMethod
    public static void unmount() {
        if (showcase != null) {
            showcase.dispose();
            showcase = null;
        }
        WebAwt.uninstall();
    }

    @JsMethod
    public static String selectedPage() {
        return showcase == null ? "" : showcase.getSelectedPage();
    }

    @JsMethod
    public static String statusText() {
        String status = showcase == null ? null : showcase.getStatusText();
        return status == null ? "" : status;
    }

    @JsMethod
    public static boolean runSelfCheck() {
        return showcase != null && showcase.runSelfCheck();
    }

    @JsMethod
    public static String keyboardTrace() {
        return showcase == null ? "" : showcase.getKeyboardTrace();
    }

    @JsMethod
    public static String keyboardSelection() {
        return showcase == null ? "" : showcase.getKeyboardSelection();
    }

    @JsMethod
    public static void selectPage(String pageName) {
        if (showcase != null) {
            showcase.selectPage(pageName);
        }
    }

    @JsMethod
    public static void showPopupMenu() {
        if (showcase != null) {
            showcase.showPopupMenu();
        }
    }
}
