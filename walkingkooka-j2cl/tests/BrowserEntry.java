package walkingkooka.integration.tests;

import jsinterop.annotations.JsMethod;

public final class BrowserEntry {
    @JsMethod
    public static String run() {
        return TimeContracts.run();
    }
}
