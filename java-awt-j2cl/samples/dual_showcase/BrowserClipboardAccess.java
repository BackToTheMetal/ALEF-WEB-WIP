package org.webawt.samples.dualshowcase;

import java.util.function.Consumer;
import org.webawt.WebAwt;

public final class BrowserClipboardAccess extends ClipboardAccess {
    @Override
    public String description() {
        return "Uses the system clipboard. Allow browser clipboard access when prompted; HTTPS or localhost is required.";
    }

    @Override
    public void writeText(String text, Runnable success, Consumer<String> failure) {
        if (!checkAvailable(failure)) {
            return;
        }
        WebAwt.writeClipboardText(text).then(value -> {
            success.run();
            return null;
        }, error -> {
            failure.accept(String.valueOf(error) + ". Allow clipboard access or use Ctrl+C / Cmd+C.");
            return null;
        });
    }

    @Override
    public void readText(Consumer<String> success, Consumer<String> failure) {
        if (!checkAvailable(failure)) {
            return;
        }
        WebAwt.readClipboardText().then(value -> {
            success.accept(value);
            return null;
        }, error -> {
            failure.accept(String.valueOf(error) + ". Allow clipboard access or use Ctrl+V / Cmd+V.");
            return null;
        });
    }

    private static boolean checkAvailable(Consumer<String> failure) {
        if (WebAwt.isClipboardAvailable()) {
            return true;
        }
        failure.accept("System clipboard unavailable. Open this page over HTTPS or localhost.");
        return false;
    }
}
