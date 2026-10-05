package org.webawt.internal.peer;

public interface ToolkitProvider {
    void beep();
    default int getMenuShortcutKeyMaskEx() {
        return 128;
    }
}
