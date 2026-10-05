package org.webawt.samples.dualshowcase;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

/** The gallery's runtime boundary for asynchronous system clipboard actions. */
public class ClipboardAccess {
    public String description() {
        return "Uses the desktop system clipboard.";
    }

    public void writeText(String text, Runnable success, Consumer<String> failure) {
        try {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } catch (RuntimeException exception) {
            failure.accept(exception.getClass().getSimpleName());
            return;
        }
        success.run();
    }

    public void readText(Consumer<String> success, Consumer<String> failure) {
        String text;
        try {
            Object value = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            text = value == null ? "" : value.toString();
        } catch (Exception exception) {
            failure.accept(exception.getClass().getSimpleName());
            return;
        }
        success.accept(text);
    }
}
